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

package com.hms.explorehms.huawei.feature_awarenesskit.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.huawei.hms.kit.awareness.barrier.BarrierStatus;

public class BarrierReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        BarrierStatus barrierStatus = BarrierStatus.extract(intent);
        String serviceName = barrierStatus.getBarrierLabel().substring(0, barrierStatus.getBarrierLabel().indexOf("+"));
        String label = barrierStatus.getBarrierLabel().substring(barrierStatus.getBarrierLabel().indexOf("+") + 1);
        String channelId = "AwarenessKit";
        NotificationChannel notificationChannel = new NotificationChannel(
                channelId,
                "Awareness Kit",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(serviceName)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(com.hms.explorehms.R.mipmap.ic_launcher_round)
                .setAutoCancel(true);

        String barrierReceiverTag = "TimeBarrierReceiver";
        switch (barrierStatus.getPresentStatus()) {
            case BarrierStatus.TRUE:
                builder.setContentText(label + " Status: TRUE");
                builder.setChannelId(channelId);
                notificationmanager.notify(0, builder.build());
                Log.i(barrierReceiverTag, label + " status:true");
                break;
            case BarrierStatus.FALSE:
                builder.setContentText(label + " Status: FALSE");
                builder.setChannelId(channelId);
                notificationmanager.notify(10, builder.build());
                Log.i(barrierReceiverTag, label + " status:false");
                break;
            case BarrierStatus.UNKNOWN:
                builder.setContentText(label + "Status: UNKNOWN");
                notificationmanager.notify(0, builder.build());
                Log.i(barrierReceiverTag, label + " status:unknown");
                break;
            default:
                break;
        }
    }
}
