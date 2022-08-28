/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.hms.explorehms.pushkit.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hms.explorehms.R;
import com.hms.explorehms.pushkit.NotificationTargetActivityPushKit;
import com.google.android.material.textfield.TextInputEditText;

import java.security.SecureRandom;
import java.util.Objects;

public class GetNotificationDialog extends Dialog {

    /**
     * UI Elements
     */
    private final Context context;
    private View dialogView;

    private TextInputEditText etTitle;
    private TextInputEditText etDescription;
    private TextInputEditText etSubtext;

    private Button btnBack;
    private Button btnGetNotification;

    private SwitchCompat cbOpenActivity;
    private SwitchCompat cbVibrate;

    private static final String push_kit_demo_channel = "push_kit_demo_channel";
    private static final String push_kit_demo_channel_vibrate = "push_kit_demo_channel_vibrate";


    /**
     * @param context context
     */
    public GetNotificationDialog(Context context){
        super(context,R.style.custom_pushkit_theme_black);

        this.context = context;

        initUI();
        initListener();

        setCanceledOnTouchOutside(false);

        setContentView(dialogView);
    }

    /**
     * Initialize UI Elements
     */
    @SuppressLint("InflateParams")
    private void initUI(){

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        dialogView = layoutInflater.inflate(R.layout.dialog_get_notification_pushkit,null,false);

        etTitle = dialogView.findViewById(R.id.et_pushkit_notification_title);
        etDescription = dialogView.findViewById(R.id.et_pushkit_notification_description);
        etSubtext = dialogView.findViewById(R.id.et_pushkit_notification_subtext);
        cbOpenActivity = dialogView.findViewById(R.id.cb_pushkit_notification_openactivity);
        cbVibrate = dialogView.findViewById(R.id.cb_pushkit_notification_vibration);
        btnBack = dialogView.findViewById(R.id.btn_pushkit_notification_back);
        btnGetNotification = dialogView.findViewById(R.id.btn_pushkit_notification_getnotification);
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener(){
        btnBack.setOnClickListener(v -> dismiss());

        btnGetNotification.setOnClickListener(v -> createNotification());
    }

    /**
     * Create Notification Method
     */
    private void createNotification(){

        /*
         * Create notification channel if it doesn't exist.
         */
        NotificationManager manager = this.context.getSystemService(NotificationManager.class);

        /*
         * Vibration enabled notification channel
         */
        if(cbVibrate.isChecked()){
            if(manager.getNotificationChannel(push_kit_demo_channel_vibrate) == null){
                NotificationChannel channelVibration = new NotificationChannel(
                        push_kit_demo_channel_vibrate,
                        "Push Kit Demo Channel Vibration",
                        NotificationManager.IMPORTANCE_HIGH
                );

                /*
                 * Vibration type
                 * @param long[]{delay, vibrate, sleep, vibrate, sleep}
                 */
                channelVibration.setVibrationPattern(new long[]{0,1000});

                manager.createNotificationChannel(channelVibration);
            }
        }else{
            /*
             * Default notification channel
             */
            if(manager.getNotificationChannel(push_kit_demo_channel) == null){
                NotificationChannel channel1 = new NotificationChannel(
                        push_kit_demo_channel,
                        "Push Kit Demo Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );

                manager.createNotificationChannel(channel1);
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);

        Intent intent = new Intent(this.context, NotificationTargetActivityPushKit.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);


        /*
         * Notification object by selected options
         */
        Notification notification = new NotificationCompat
                .Builder(this.context, cbVibrate.isChecked() ? push_kit_demo_channel_vibrate : push_kit_demo_channel)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(Objects.requireNonNull(etTitle.getText()).toString().equals("") ? "Test Title" : etTitle.getText().toString())
                .setContentText(Objects.requireNonNull(etDescription.getText()).toString().equals("") ? "Test Description" : etDescription.getText().toString())
                .setSubText(Objects.requireNonNull(etSubtext.getText()).toString().equals("") ? "Test Subtext" : etSubtext.getText().toString())
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setColor(Color.RED)
                .setContentIntent(cbOpenActivity.isChecked() ? pendingIntent : null)
                .build();

        /*
         * Create notification by random notification ID.
         */
        int randomNotificationID = new SecureRandom().nextInt(333);
        notificationManager.notify(randomNotificationID,notification);
    }


}

