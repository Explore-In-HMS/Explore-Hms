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

import com.huawei.hms.location.ActivityConversionData;
import com.huawei.hms.location.ActivityConversionResponse;
import com.huawei.hms.location.ActivityIdentificationData;
import com.huawei.hms.location.ActivityIdentificationResponse;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationResult;

import java.util.ArrayList;
import java.util.List;

public class LocationBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = LocationBroadcastReceiver.class.getSimpleName();

    public static final String ACTION_PROCESS_LOCATION = "com.hms.explorehms.huawei.locationkit.ACTION_PROCESS_LOCATION";

    private static  boolean isListenActivityIdentification = false;

    private static  boolean isListenActivityConversion = false;

    List<String> activityConversionList;
    List<String> activityIdentificationList;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {

            final String action = intent.getAction();

            if (ACTION_PROCESS_LOCATION.equals(action)) {

                // you can Process Activity Conversion Data
                processingActivityConversionData(intent);

                // you can Process Activity Identification Data
                processingActivityIdentificationData(intent);

                // you can Process LocationResult Data when LocationUpdates with intent
                processingLocationUpdatesResultData(intent);

                // you can Processing LocationAvailability Data
                processingAvailabilityData(intent);
            }
        }
    }

    /**
     * Processing Activity Conversion Data when intent ACTION_PROCESS_LOCATION
     *
     * @param intent : ACTION_PROCESS_LOCATION
     */
    private void processingActivityConversionData(Intent intent) {
        ActivityConversionResponse activityTransitionResult = ActivityConversionResponse.getDataFromIntent(intent);
        if (activityTransitionResult != null && isListenActivityConversion) {

            List<ActivityConversionData> list = activityTransitionResult.getActivityConversionDatas();

            String msg = "ActivityConversionData  : " + Utils.getTimeStamp() + " :\n" + list.toString();
            Log.i(TAG, msg);

            ActivityConversionActivity.getInstance().updateLogResults(msg);

            activityConversionList = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                Log.i(TAG, "activityTransitionEvent [" + i + "]" + list.get(i));
                activityConversionList.add(String.valueOf(list.get(i).getConversionType()));
            }
        }
    }

    /**
     * Processing Activity Identification Data when intent ACTION_PROCESS_LOCATION
     *
     * @param intent : ACTION_PROCESS_LOCATION
     */
    private void processingActivityIdentificationData(Intent intent) {
        ActivityIdentificationResponse activityRecognitionResult = ActivityIdentificationResponse.getDataFromIntent(intent);
        if (activityRecognitionResult != null && isListenActivityIdentification) {

            List<ActivityIdentificationData> list = activityRecognitionResult.getActivityIdentificationDatas();
            ActivityIdentificationActivity.getInstance().setLayoutDataAndParams(list);

            String msg = "ActivityIdentificationData  : " + Utils.getTimeStamp() + " :\n" + list.toString();
            Log.i(TAG, msg);

            ActivityIdentificationActivity.getInstance().updateLogResults(TAG + " -> " + msg);

            activityIdentificationList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Log.i(TAG, "activityTransitionEvent [" + i + "]" + list.get(i));
                activityIdentificationList.add(String.valueOf(list.get(i).getIdentificationActivity()));
            }
        }
    }

    /**
     * Processing LocationResult Data when LocationUpdates with intent
     *
     * @param intent : ACTION_PROCESS_LOCATION
     */
    private void processingLocationUpdatesResultData(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null) {
                List<Location> locations = result.getLocations();
                if (!locations.isEmpty()) {
                    String msg;
                    for (Location location : locations) {
                        msg = "LocationUpdateWithIntent : " +
                                Utils.getTimeStamp() + " :\n" +
                                "[Longitude,Latitude,Accuracy] : " +
                                "[ " + location.getLongitude() +
                                " ," + location.getLatitude() +
                                " ," + location.getAccuracy() +
                                " ]";
                        Log.i(TAG, msg);
                    }
                }
            }
        }
    }

    /**
     * Processing Availability Data
     *
     * @param intent : ACTION_PROCESS_LOCATION
     */
    private void processingAvailabilityData(Intent intent) {
        if (LocationAvailability.hasLocationAvailability(intent)) {
            LocationAvailability locationAvailability =
                    LocationAvailability.extractLocationAvailability(intent);
            if (locationAvailability != null) {
                Log.i(TAG, "LocationAvailability  : " + locationAvailability.isLocationAvailable());
            }
        }
    }


    public static void addConversionListener() {
        isListenActivityConversion = true;
    }

    public static void removeConversionListener() {
        isListenActivityConversion = false;
    }

    public static void addIdentificationListener() {
        isListenActivityIdentification = true;
    }

    public static void removeIdentificationListener() {
        isListenActivityIdentification = false;
    }

}
