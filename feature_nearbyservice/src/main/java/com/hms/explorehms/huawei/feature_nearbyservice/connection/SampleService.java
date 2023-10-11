package com.hms.explorehms.huawei.feature_nearbyservice.connection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.huawei.hms.nearby.message.BeaconInfo;
import java.util.List;

public class SampleService extends Service {
    private static final String TAG = "SampleService";
    // 0: A beacon is lost; 1: A beacon is found.
    private static final String KEY_SCAN_ONFOUND_FLAG = "SCAN_ONFOUND_FLAG";
    // Information about the lost or found beacon.
    private static final String KEY_SCAN_BEACON_DATA = "SCAN_BEACON";

    private static final String BEACON_NOTIFY_CHANNEL_ID = "beacon_notification_channel_id";

    private static final String BEACON_NOTIFY_CHANNEL_NAME = "beacon_notification_channel_name";

    private static final int NOTIFICATION_SERVICE_ID = 101;

    public SampleService() {
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        new Handler().postDelayed(this::startForeground, 1000);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        startForeground();
        int onFound = intent.getIntExtra(KEY_SCAN_ONFOUND_FLAG, 0);
        Log.i(TAG, "onFound:" + onFound);

        // Obtain the beacon information for service processing.
        List<BeaconInfo> beaconList = intent.getParcelableArrayListExtra(KEY_SCAN_BEACON_DATA);
        return super.onStartCommand(intent, flags, startId);
    }

    public void startForeground() {
        // The notification bar logic is determined by yourself.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(BEACON_NOTIFY_CHANNEL_ID,
                    BEACON_NOTIFY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, BEACON_NOTIFY_CHANNEL_ID)
                // Set the notification icon.
                // Set the notification title.
                .setContentTitle("Beacon notification")
                // Set the notification content.
                .setContentText("A beacon is found.")
                .setAutoCancel(true)
                .setOngoing(true)
                .build();
        // Android versions later than 8.0 do not want apps to run in the background. Therefore, call startForeground() to avoid ANRs or crashes.
        startForeground(NOTIFICATION_SERVICE_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
