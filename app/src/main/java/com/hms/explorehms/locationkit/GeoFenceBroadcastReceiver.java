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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.GeofenceData;

import java.util.List;

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = GeoFenceBroadcastReceiver.class.getSimpleName();

    public static final String ACTION_PROCESS_LOCATION = "com.huawei.hmssample.geofence.GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {

        GeoFenceIntentService.enqueueWork(context, intent);

        if (intent != null) {

            final String action = intent.getAction();

            if (ACTION_PROCESS_LOCATION.equals(action)) {

                // you can Process GeoFenceData
                processingGeoFenceData(intent);

            }
        }

    }
    /**
     * Processing GeoFenceData Data when intent ACTION_PROCESS_LOCATION
     *
     * @param intent : ACTION_PROCESS_LOCATION
     */
    private void processingGeoFenceData(Intent intent) {

        StringBuilder sb = new StringBuilder();
        String next = "\n";
        // Obtain the GeofenceData object from the intent.
        GeofenceData geofenceData = GeofenceData.getDataFromIntent(intent);
        // Obtain a result code.
        int errorCode = geofenceData.getErrorCode();
        // Obtain a result code.
        boolean isFailure = geofenceData.isFailure();
        // Obtain the geofence trigger type.
        int conversion = geofenceData.getConversion();
        // Obtain information about the triggered geofence.
        List<Geofence> list = geofenceData.getConvertingGeofenceList();
        // Obtain information about the location when the geofence is triggered.
        Location mLocation = geofenceData.getConvertingLocation();
        // Check whether the geofence event is triggered normally. If false is returned, an error occurs.
        boolean isSuccess = geofenceData.isSuccess();
        sb.append("GeoFenceBroadcastReceiver : ").append(Utils.getTimeStamp()).append(next);
        for (int i = 0; i < list.size(); i++){
            sb.append("geoFence id :").append(list.get(i).getUniqueId()).append(next);
        }
        sb.append("conversion: ").append(conversion).append(next);
        sb.append("location  : ").append(mLocation.getLongitude()).append(" - ").append(mLocation.getLatitude()).append(next);
        sb.append("isSuccess : ").append(isSuccess).append(next);
        sb.append("isFailure : ").append(isFailure).append(next);
        sb.append("errorCode : ").append(errorCode).append(next);
        Log.i(TAG, "onReceive : " + sb.toString());

        GeoFenceActivity.getInstance().updateLogResults( sb.toString());
    }

}
