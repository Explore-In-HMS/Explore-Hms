/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hms.explorehms.pushkit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hms.explorehms.R;
import com.hms.explorehms.pushkit.NotificationTargetActivityPushKit;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

import java.security.SecureRandom;

public class HuaweiPushServicePushKit extends HmsMessageService {

    private static final String TAG = "PUSH_KIT";
    private static final String EXPLOREHMS_ACTION = "com.hms.explorehms.huawei.feature_pushkit.action";

    /**
     * When an app calls the getToken method to apply for a token from the server,
     * if the server does not return the token during current method calling, the server can return the token through this method later.
     * @param token token
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.i(TAG, "received refresh token:" + token);
        // send the token to your app server.
        if (!TextUtils.isEmpty(token)) {
            refreshedTokenToServer(token);
        }
    }

    private void refreshedTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

    /**
     * This method is used to receive downstream data messages.
     * This method callback must be completed in 10 seconds. Otherwise, you need to start a new Job for callback processing.
     *
     * @param message RemoteMessage object
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);


        Log.i(TAG, "onMessageReceived is called");
        if (message == null) {
            Log.e(TAG, "Received message entity is null!");
            return;
        }

        Log.i(TAG,message.toString());

        Intent intent = new Intent();
        intent.setAction(EXPLOREHMS_ACTION);
        intent.putExtra("method", "onMessageReceived");
        intent.putExtra("data", message);

        sendBroadcast(intent);
        createNotification();
    }

    /**
     * Create notification when data message received to HMS Message Service.
     * Notification message could configure depends on your needs.
     */
    private void createNotification(){

        /*
         * Create notification channel if it doesn't exist.
         */
        NotificationManager manager = getSystemService(NotificationManager.class);

        if(manager.getNotificationChannel(getResources().getString(R.string.push_kit_demo_channel_pushkit)) == null){
            NotificationChannel channel1 = new NotificationChannel(
                    getResources().getString(R.string.push_kit_demo_channel_pushkit),
                    getResources().getString(R.string.push_kit_demo_channel_pushkit_name),
                    NotificationManager.IMPORTANCE_HIGH
            );

            manager.createNotificationChannel(channel1);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent intent = new Intent(this, NotificationTargetActivityPushKit.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        /*
         * Optional : Set Content Intent to start when tap the notification.
         * @see setContentIntent(pendinIntent)
         */
        Notification notification = new NotificationCompat
                .Builder(this, getResources().getString(R.string.push_kit_demo_channel_pushkit))
                .setSmallIcon(R.mipmap.ic_launcher_health)
                .setContentTitle("Data Message")
                .setContentText("Data Message Received!")
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setColor(Color.RED)
                .setContentIntent(pendingIntent)
                .build();

        /*
         * Create notification by random notification ID.
         */
        int randomNotificationID = new SecureRandom().nextInt(333);
        notificationManager.notify(randomNotificationID,notification);
    }
}