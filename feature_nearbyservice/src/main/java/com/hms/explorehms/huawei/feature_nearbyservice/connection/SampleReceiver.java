package com.hms.explorehms.huawei.feature_nearbyservice.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huawei.hms.nearby.message.BeaconInfo;
import java.util.List;

public class SampleReceiver extends BroadcastReceiver {
    private static final String TAG = "SampleReceiver";
    private static final String ACTION_SCAN_ONFOUND_RESULT = "com.huawei.hms.nearby.action.ONFOUND_BEACON";
    // 0: A beacon is lost; 1: A beacon is found.
    private static final String KEY_SCAN_ONFOUND_FLAG = "SCAN_ONFOUND_FLAG";
    // Information about the lost or found beacon.
    private static final String KEY_SCAN_BEACON_DATA = "SCAN_BEACON";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_SCAN_ONFOUND_RESULT.equals(action)) {
            // Obtain the beacon information for service processing.
            int onFound = intent.getIntExtra(KEY_SCAN_ONFOUND_FLAG, 0);
            List<BeaconInfo> beaconList = intent.getParcelableArrayListExtra(KEY_SCAN_BEACON_DATA);

        }
    }
}