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
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hms.explorehms.R;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.HWLocation;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HdLocationActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = HdLocationActivity.class.getSimpleName();

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback mLocationHDCallback;

    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.callbackTableLayoutShow)
    TableLayout tableLayoutCallback;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.resultLogs)
    TextView tvResultLogs;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_hd);
        setupToolbar();
        unbinder = ButterKnife.bind(this);

        createFusedLocationProviderClient();

        String locationRequestJson = Utils.getJsonFromFile(this, "LocationRequest.json", true);
        initDataDisplayView(tableLayoutCallback, locationRequestJson);

        isGrantPermissions();

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
    @OnClick({R.id.btn_get_loc_hd, R.id.btn_remove_location})
    public void onItemClick(View v) {
        updateLogResults("Result Logs Will Be Here");
        switch (v.getId()) {
            case R.id.btn_get_loc_hd:
                if (isGrantPermissions()) {
                    getLocationWithHd();
                }
                break;
            case R.id.btn_remove_location:
                removeLocationHd();
                break;
            default:
                Log.i(TAG, getString(R.string.defaultText));
        }
    }


    private void updateLogResults(String msg) {
        tvResultLogs.setText(msg);
    }

    /**
     * create a fusedLocationProviderClient
     */
    private void createFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    protected void initDataDisplayView(TableLayout tableLayout, String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonObject.getString(key);

                TableRow tableRow = new TableRow(getBaseContext());
                TextView textView = new TextView(getBaseContext());
                textView.setText(key);
                textView.setTextSize(14);
                textView.setTextColor(Color.BLACK);
                textView.setId(getBaseContext().getResources()
                        .getIdentifier(key + "_key", "id", getBaseContext().getPackageName()));
                tableRow.addView(textView);

                EditText editText = new EditText(getBaseContext());
                editText.setText(value);
                editText.setTextSize(12);
                editText.setEnabled(false);
                editText.setCursorVisible(false);
                editText.setFocusable(false);
                editText.setKeyListener(null);

                editText.setId(getBaseContext().getResources()
                        .getIdentifier(key + "_value", "id", getBaseContext().getPackageName()));
                editText.setTextColor(Color.DKGRAY);
                tableRow.addView(editText);
                tableLayout.addView(tableRow);
            }
        } catch (JSONException e) {
            Log.e(TAG, "initDataDisplayView JSONException:" + e.getMessage(), e);
        }
    }

    private void getLocationWithHd() {
        new Thread(() -> {
            try {
                LocationRequest locationRequest = new LocationRequest();
                if (null == mLocationHDCallback) {
                    mLocationHDCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationRequest) {
                            Log.d(TAG, "getLocationWithHd onSuccess and onLocationResult will print");
                            updateLogResults("getLocationWithHd onSuccess onLocationResult will print");
                            logResult(locationRequest);
                        }

                        @Override
                        public void onLocationAvailability(LocationAvailability locationAvailability) {
                            Log.i(TAG, "getLocationWithHd callback onLocationAvailability print");
                            if (locationAvailability != null) {
                                boolean flag = locationAvailability.isLocationAvailable();
                                Log.d(TAG, "onLocationAvailability isLocationAvailable : " + flag);
                                updateLogResults("onLocationAvailability isLocationAvailable : " + flag);
                            }
                        }
                    };
                }
                fusedLocationProviderClient
                        .requestLocationUpdatesEx(locationRequest, mLocationHDCallback, Looper.getMainLooper())
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "getLocationWithHd onSuccess");
                            updateLogResults("getLocationWithHd onSuccess.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "getLocationWithHd onFailure : " + e.getMessage(), e);
                            updateLogResults("getLocationWithHd onFailure : " + e.getMessage());
                        });
            } catch (Exception e) {
                Log.e(TAG, "getLocationWithHd Exception:" + e.getMessage(), e);
            }
        }).start();
    }


    private void removeLocationHd() {
        new Thread(() -> {
            try {
                /*
                  comment for developer
                  removeLocationHd onFailure : 10801: PARAM_ERROR_EMPTY
                  remove location updates with callback api exception:10801: PARAM_ERROR_EMPTY
                 */
                fusedLocationProviderClient.removeLocationUpdates(mLocationHDCallback)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "removeLocationHd onSuccess");
                            updateLogResults("removeLocationHd onSuccess.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "removeLocationHd onFailure : " + e.getMessage(), e);
                            updateLogResults("removeLocationHd onFailure : " + e.getMessage());
                        });
            } catch (Exception e) {
                Log.e(TAG, "removeLocationHd Exception:" + e.getMessage(), e);
            }
        }).start();

    }


    private void logResult(LocationResult locationRequest) {
        if (locationRequest != null) {
            logLocation(locationRequest.getLocations());
            logHwLocation(locationRequest.getHWLocationList());

            updateLogResults( "LocationRequestWithHd : \n" + oldLocationLogResult + " \n...\n" + newLocationLogResult);

        } else {
            Log.i(TAG, "getLocationWithHd callback  onLocationResult locationResult is Null");
        }
    }

    String oldLocationLogResult;
    String newLocationLogResult;

    private void logHwLocation(List<HWLocation> hwLocations) {
        if (hwLocations == null || hwLocations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback hwLocations is empty");
            return;
        }
        for (HWLocation hwLocation : hwLocations) {
            if (hwLocation == null) {
                Log.i(TAG, "getLocationWithHd callback hwLocation is empty");
                return;
            }
            boolean hdbBinary = false;
            Map<String, Object> extraInfo = hwLocation.getExtraInfo();
            int sourceType = 0;
            if (extraInfo != null && !extraInfo.isEmpty() && extraInfo.containsKey(getString(R.string.sourceType))) {
                Object object = extraInfo.get(getString(R.string.sourceType));
                if (object instanceof Integer) {
                    sourceType = (int) object;
                    hdbBinary = Utils.getBinaryFlag(sourceType);
                }
            }
            String hdFlag = "";
            if (hdbBinary) {
                hdFlag = "result is HD";
            }
            newLocationLogResult = "[new] location result : " + "\n" +
                    "Longitude = " + hwLocation.getLongitude() + "\n" +
                    "Latitude = " + hwLocation.getLatitude() + "\n" +
                    "SourceType = " + sourceType + "\n" +
                    "Accuracy = " + hwLocation.getAccuracy() + "\n" +
                    hwLocation.getCountryName() + "," + hwLocation.getState() + "," +
                    hwLocation.getCity() + "," + hwLocation.getCounty() + "," +
                    hwLocation.getFeatureName() + "\n" + hdFlag;

            Log.d(TAG, "logHwLocation : " + newLocationLogResult);
        }
    }

    private void logLocation(List<Location> locations) {
        String hdFlag = "";
        if (locations == null || locations.isEmpty()) {
            Log.i(TAG, "getLocationWithHd callback locations is empty");
            return;
        }
        for (Location location : locations) {
            if (location == null) {
                Log.i(TAG, "getLocationWithHd callback location is empty");
                return;
            }
            boolean hdbBinary = false;
            Bundle extraInfo = location.getExtras();
            int sourceType = 0;
            if (extraInfo != null && !extraInfo.isEmpty() && extraInfo.containsKey(getString(R.string.sourceType))) {
                sourceType = extraInfo.getInt(getString(R.string.sourceType), -1);
                hdbBinary = Utils.getBinaryFlag(sourceType);
            }
            if (hdbBinary) {
                hdFlag = "result is HD";
            }
            oldLocationLogResult = "[old] location result : " + "\n" +
                    "Longitude = " + location.getLongitude() + "\n" +
                    "Latitude = " + location.getLatitude() + "\n" +
                    "SourceType = " + sourceType + "\n" +
                    "Accuracy = " + location.getAccuracy() + "\n" +
                    hdFlag;

            Log.d(TAG, "getLocationWithHd : " + oldLocationLogResult);
        }
    }


    private boolean isGrantPermissions() {
        boolean isPermitGrant;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            isPermitGrant = Utils.isGrantLocationPermissions(HdLocationActivity.this, Utils.permissionRequest1, Utils.permissionRequestCode1);
        } else {
            isPermitGrant = Utils.isGrantLocationPermissions(HdLocationActivity.this, Utils.permissionRequest2, Utils.permissionRequestCode2);
        }
        return isPermitGrant;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.permissionRequestCode1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            } else {
                Log.e(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
                showNeedPermissionWarning("NEED LOCATION PERMISSION", "Location");
            }
        }

        if (requestCode == Utils.permissionRequestCode2) {
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful");
            } else {
                Log.e(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed");
                showNeedPermissionWarning("NEED LOCATION PERMISSION", "Location");
            }
        }
    }

    public void showNeedPermissionWarning(String msg, String perm) {
        Utils.showDialogPermissionWarning(this,
                msg,
                "Would You Like To Go To Permission Settings To Allow?",
                R.drawable.icon_settings_loc,
                "You can not use Location Features without " + perm + " Permission!",
                "YES GO", "CANCEL");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}