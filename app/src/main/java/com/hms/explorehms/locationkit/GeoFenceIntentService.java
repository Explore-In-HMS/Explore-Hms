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

package com.hms.explorehms.locationkit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.hms.explorehms.R;
import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.GeofenceData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

public class GeoFenceIntentService extends JobIntentService {

    private static final String TAG = GeoFenceIntentService.class.getSimpleName();

    private static final int JOB_ID = 573;
    private static final String CHANNEL_ID = "channel_01";

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeoFenceIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofenceData geofenceData = GeofenceData.getDataFromIntent(intent);
        int conversion = geofenceData.getConversion();
        ArrayList<Geofence> geofenceTransition = (ArrayList<Geofence>) geofenceData.getConvertingGeofenceList();
        String geofenceTransitionDetails = getGeofenceTransitionDetails(conversion, geofenceTransition);
        sendNotification(geofenceTransitionDetails);
        Log.i(TAG, "onHandleWork : geofenceTransitionDetails : " + geofenceTransitionDetails);
    }

    private String getGeofenceTransitionDetails(int conversion, ArrayList<Geofence> triggeringGeofences) {
        String geofenceConversion = getConversionString(conversion);
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getUniqueId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
        String result = geofenceConversion + ": " + triggeringGeofencesIdsString;
        Log.i(TAG, "getGeofenceTransitionDetails : triggeringGeofenceConversion : " + result);
        return result;
    }


    private void sendNotification(String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), LocationKitActivity.class);
        // TODO : check it with main act

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(LocationKitActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.icon_locationkit)
                // In a real app, you may want to use a library like Volley to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.icon_locationkit))
                .setColor(Color.RED)
                .setContentTitle("GeoFence Transition Info")
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        assert mNotificationManager != null;
        mNotificationManager.notify(0, builder.build());

        Log.i(TAG, "sendNotification : notificationDetails : " + notificationDetails);
    }

    private String getConversionString(int conversionType) {
        switch (conversionType) {
            case Geofence.ENTER_GEOFENCE_CONVERSION:
                return "Entered to GeoFence";
            case Geofence.EXIT_GEOFENCE_CONVERSION:
                return "Exited GeoFence";
            case Geofence.DWELL_GEOFENCE_CONVERSION:
                return "Dwell GeoFence";
            default:
                return "Unknown Transition";
        }
    }

}