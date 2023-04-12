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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hms.explorehms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.SettingsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.huawei.hms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED;

public class GeoFenceCreateActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = GeoFenceCreateActivity.class.getSimpleName();

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SettingsClient mSettingsClient;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    int activityResultCodeGpsForCallback = 5;

    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetLatitude)
    TextInputEditText etSetLatitude;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetLongitude)
    TextInputEditText etSetLongitude;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetRadius)
    TextInputEditText etSetRadius;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetUniqueId)
    TextInputEditText etSetUniqueId;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetConversions)
    TextInputEditText etSetConversions;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetValidContinueTime)
    TextInputEditText etSetValidContinueTime;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetDwellDelayTime)
    TextInputEditText etSetDwellDelayTime;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.etSetNotificationInterval)
    TextInputEditText etSetNotificationInterval;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.resultLogs)
    TextView tvResultLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence_create);
        setupToolbar();
        unbinder = ButterKnife.bind(this);

        createFusedLocationProviderClient();
        createLocationSettingClient();

        createLocationRequest();
        createLocationUpdatesWithCallback();

    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_getCurrentLocation, R.id.btn_createGeoFence, R.id.btn_showGeoList})
    public void onItemClick(View v) {
        updateLogResults("Result Logs Will Be Here");
        switch (v.getId()) {
            case R.id.btn_getCurrentLocation:
                getCurrentLocationGeoFence();
                break;
            case R.id.btn_createGeoFence:
                createFromTextGeoData();
                break;
            case R.id.btn_showGeoList:
                String result = GeoFenceCreateActivity.GeoFenceData.showGeoFenceData();
                updateLogResults(result);
                break;
            default:
                Log.i(TAG,getString(R.string.defaultText));
        }
    }


    /**
     * create a fusedLocationProviderClient
     */
    private void createFusedLocationProviderClient() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * create a locationSettingClient
     */
    private void createLocationSettingClient() {
        mSettingsClient = LocationServices.getSettingsClient(this);
    }


    /**
     * create LocationRequest and Set parameters for continuously requesting device locations
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Set the interval for location updates, in milliseconds.
        mLocationRequest.setInterval(2000);
        // set the priority of the request
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    int reqCount = 0;

    /**
     * create mLocationCallback
     * <p>
     * Call the onLocationResult() method in the defined LocationCallback class
     * to obtain a LocationResult object that contains the location information.
     * Return the location information in the extension information of the PendingIntent object.
     */
    private void createLocationUpdatesWithCallback() {
        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            for (Location location : locations) {
                                reqCount++;
                                String locationUpdateString = "[ " + location.getLongitude() + " - " + location.getLatitude() + " ] \n" +
                                        "( accuracy  " + location.getAccuracy() + " ) - speed : " + location.getSpeed();

                                Log.i(TAG, "createLocationUpdatesWithCallback.onLocationResult location[Longitude,Latitude,Accuracy]:" + locationUpdateString);

                                etSetLatitude.setText(String.valueOf(location.getLatitude()));
                                etSetLongitude.setText(String.valueOf(location.getLongitude()));

                                updateLogResults("LocationUpdatesWithCallback : \n" + locationUpdateString + "\n");

                                removeLocationUpdatesWithCallback();

                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Log.i(TAG, "onLocationAvailability isLocationAvailable : " + flag);
                        if (!flag) {
                            Utils.showToastMessage(getApplicationContext(), "LocationAvailable is False!");
                        }
                    }
                }
            };
        }
    }


    private void getCurrentLocationGeoFence() {
        try {
            LocationSettingsRequest.Builder builders = new LocationSettingsRequest.Builder();
            builders.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builders.build();
            // Before requesting location update, invoke checkLocationSettings to check device settings.
            mSettingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        Log.i(TAG, "getCurrentLocationGeoFence check location settings success");
                        mFusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                .addOnSuccessListener(aVoid -> {
                                    Log.i(TAG, getString(R.string.currentLocationSuccess));
                                    updateLogResults(getString(R.string.currentLocationSuccess));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, getString(R.string.currentLocationGeoFenceOnFailure)+ e.getMessage(), e);
                                    Utils.showToastMessage(getApplicationContext(), getString(R.string.currentLocationGeoFenceOnFailure) + e.getMessage());
                                    updateLogResults(getString(R.string.currentLocationGeoFenceOnFailure) + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "checkLocationSetting onFailure : " + e.getMessage(), e);
                        updateLogResults("checkLocationSetting onFailure : " + e.getMessage());
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(GeoFenceCreateActivity.this, activityResultCodeGpsForCallback);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e(TAG, getString(R.string.resolvableCurrentLocationGeoFenceOnFailure) + sie.getMessage());
                                updateLogResults(getString(R.string.resolvableCurrentLocationGeoFenceOnFailure) + sie.getMessage());
                            }
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "CurrentLocationGeoFence Exception : " + e.getMessage(), e);
            updateLogResults("CurrentLocationGeoFence Exception : " + e.getMessage());
        }
    }


    /**
     * to stop requesting location updates
     * Note: When requesting location updates is stopped,
     * the mLocationCallback object must be the same as LocationCallback in the requestLocationUpdates method.
     */
    private void removeLocationUpdatesWithCallback() {
        try {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess"))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "removeLocationUpdatesWithCallback onFailure : " + e.getMessage(), e);
                        updateLogResults("removeLocationUpdatesWithCallback onFailure : " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback Exception : " + e.getMessage(), e);
            updateLogResults("removeLocationUpdatesWithCallback Exception : " + e.getMessage());
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (requestCode == activityResultCodeGpsForCallback) {
            if (resultCode == -1) {
                Log.i(TAG, "onRequestPermissionsResult: apply GPS ResolvableApiException successful");
                Utils.showToastMessage(getApplicationContext(), "GPS is activated now! You can again your transaction");
                getCurrentLocationGeoFence();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply GPS ResolvableApiException Failed");
                Utils.showToastMessage(getApplicationContext(), "GPS is not activated! You can not use your transaction");
            }
        }
    }


    public void createFromTextGeoData() {
        if( Objects.requireNonNull(etSetUniqueId.getText()).toString().isEmpty() ){
            Utils.showToastMessage(getApplicationContext(), "Please set UniqueId before.");
            etSetUniqueId.setError("Please set UniqueId!");
        }else if(Objects.requireNonNull(etSetLatitude.getText()).toString().isEmpty()){
            Utils.showToastMessage(getApplicationContext(), "Please set Latitude before.");
            etSetLatitude.setError("Please set latitude!");
        }else if(Objects.requireNonNull(etSetLongitude.getText()).toString().isEmpty()){
            Utils.showToastMessage(getApplicationContext(), "Please set Longitude before.");
            etSetLongitude.setError("Please set longitude!");
        }else if(Objects.requireNonNull(etSetRadius.getText()).toString().isEmpty()){
            etSetRadius.setError("Please set radius!");
            Utils.showToastMessage(getApplicationContext(), "Please set Radius before.");
        }else if(Objects.requireNonNull(etSetConversions.getText()).toString().isEmpty()){
            etSetConversions.setError("Please set Conversions Type!");
            Utils.showToastMessage(getApplicationContext(), "Please set Conversions Type before.");
        }else if(Objects.requireNonNull(etSetValidContinueTime.getText()).toString().isEmpty()){
            etSetValidContinueTime.setError("Please set ValidContinueTime!");
            Utils.showToastMessage(getApplicationContext(), "Please set ValidContinueTime before.");
        }else if(Objects.requireNonNull(etSetDwellDelayTime.getText()).toString().isEmpty()){
            etSetDwellDelayTime.setError("Please set DwellDelayTime!");
            Utils.showToastMessage(getApplicationContext(), "Please set DwellDelayTime before.");
        }else if(Objects.requireNonNull(etSetNotificationInterval.getText()).toString().isEmpty()){
            etSetNotificationInterval.setError("Please set DwellDelayTime!");
            Utils.showToastMessage(getApplicationContext(), "Please set NotificationInterval before.");
        }else{
            GeoData geoData = new GeoData();
            geoData.uniqueId = etSetUniqueId.getText().toString();
            geoData.longitude = Double.parseDouble(etSetLongitude.getText().toString());
            geoData.latitude = Double.parseDouble(etSetLatitude.getText().toString());
            geoData.radius = Float.parseFloat(etSetRadius.getText().toString());
            geoData.conversions = Integer.parseInt(etSetConversions.getText().toString());
            geoData.validContinueTime = Long.parseLong(etSetValidContinueTime.getText().toString());
            geoData.dwellDelayTime = Integer.parseInt(etSetDwellDelayTime.getText().toString());
            geoData.notificationInterval = Integer.parseInt(etSetNotificationInterval.getText().toString());
            Log.i(TAG, "createFromTextGeoData geoData : " + geoData.toString());
            GeoFenceData.addGeoFence(geoData);
            Utils.showToastMessage(getApplicationContext(), "Create and add GeoFence onSuccess : " + geoData.uniqueId );
            updateLogResults("Create and add GeoFence onSuccess : \n" + geoData.toString() );
        }
    }


    private void updateLogResults(String msg) {
        tvResultLogs.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public static class GeoFenceData {

        private static int requestCode = 0;

        static ArrayList<Geofence> geoFences = new ArrayList<>();

        static Geofence.Builder geoBuild = new Geofence.Builder();

        public static void addGeoFence(GeoData data) {

            if (!checkStyle(geoFences, data.uniqueId)) {
                Log.e(TAG, "GeoFenceData.class addGeofence Fialed : not unique ID! ");
                return;
            }
            geoBuild.setRoundArea(data.latitude, data.longitude, data.radius);
            geoBuild.setUniqueId(data.uniqueId);
            geoBuild.setConversions(data.conversions);
            geoBuild.setValidContinueTime(data.validContinueTime);
            geoBuild.setDwellDelayTime(data.dwellDelayTime);
            geoBuild.setNotificationInterval(data.notificationInterval);
            geoFences.add(geoBuild.build());
            Log.d(TAG, "GeoFenceData.class addGeoFence Success : " + geoFences.get(geoFences.size()-1).getUniqueId());
        }

        public static void createNewList() {
            geoFences = new ArrayList<>();
        }

        public static boolean checkStyle(List<Geofence> geofences, String Id) {
            for (int i = 0; i < geofences.size(); i++) {
                if (geofences.get(i).getUniqueId().equals(Id))
                    return false;
            }
            return true;
        }

        public static List<Geofence> returnList() {
            return geoFences;
        }

        public static String showGeoFenceData() {
            StringBuilder msg;
            if (geoFences.isEmpty()) {
                msg = new StringBuilder("GeoFenceData is Empty! : no GeoFence Data!");
                Log.e(TAG, "GeoFenceData.class show() : " + msg);
            }
            msg = new StringBuilder("GeoFenceData Unique ID : \n");
            for (int i = 0; i < geoFences.size(); i++) {
                Log.d(TAG, "GeoFenceData.class show() : geofences Unique ID : " + (geoFences.get(i)).getUniqueId());
                msg.append(" - ").append((geoFences.get(i)).getUniqueId());
            }
            Log.d(TAG, "GeoFenceData.class show() : " + msg.toString().replace("\\n",""));
            return msg.toString();
        }

        public static void newRequest() {
            requestCode++;
        }

        public static int getRequestCode() {
            return requestCode;
        }
    }

    public static class GeoData {
        private double latitude;
        private double longitude;
        private float radius;
        private String uniqueId;
        private int conversions;
        private long validContinueTime;
        private int dwellDelayTime;
        private int notificationInterval;

        @Override
        public @NotNull String toString() {
            return "GeoData{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", radius=" + radius +
                    ", uniqueId='" + uniqueId + '\'' +
                    ", conversions=" + conversions +
                    ", validContinueTime=" + validContinueTime +
                    ", dwellDelayTime=" + dwellDelayTime +
                    ", notificationInterval=" + notificationInterval +
                    '}';
        }
    }

}

