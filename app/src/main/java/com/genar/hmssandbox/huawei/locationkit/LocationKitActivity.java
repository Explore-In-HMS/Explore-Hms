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

package com.genar.hmssandbox.huawei.locationkit;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationEnhanceService;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStates;
import com.huawei.hms.location.NavigationRequest;
import com.huawei.hms.location.NavigationResult;
import com.huawei.hms.location.SettingsClient;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.genar.hmssandbox.huawei.locationkit.GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION;
import static com.huawei.hms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED;

public class LocationKitActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = LocationKitActivity.class.getSimpleName();

    int reqCount = 0;

    private LocationCallback mLocationCallback;

    private LocationSettingsStates mLocationGnss;

    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private SettingsClient settingsClient;

    private PendingIntent pendingIntent;

    //the mockMode flag
    private boolean mMockModeFlag;


    //the Navigation context type
    private int mContextType = 2;

    public static Double latitudeValue = 40.85772552625762;
    public static Double longitudeValue = 29.299748722821263;
    private static final Double mockLatitudeValue = 118.76;
    private static final Double mockLongitudeValue = 31.98;

    private static final int activityResultCodeGpsForCallback = 5;
    private static final int activityResultCodeGpsForIntent = 6;

    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_request_count)
    TextView tvRequestCount;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_accuracy)
    TextView tvAccuracy;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_latitude)
    TextView tvLatitude;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_longitude)
    TextView tvLongitude;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.resultLogs)
    TextView tvResultLogs;

    @SuppressLint("StaticFieldLeak")
    private static LocationKitActivity instance;

    // endregion

    public static LocationKitActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_kit);
        setupToolbar();
        unbinder = ButterKnife.bind(this);

        instance = this;
        mLocationGnss = new LocationSettingsStates();
        createFusedLocationProviderClient();
        createLocationSettingClient();

        createLocationInformationRequest();

        if (isGrantPermissions()) {
            checkLastKnownLocation();
            // optional getCompleteAddress(latitudeValue, longitudeValue) can be executed;
        }

        RadioGroup mRadioGroupSetMockMode = findViewById(R.id.radioGroup_mockMode);
        mRadioGroupSetMockMode.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            //If you do not need to simulate a location, set mode to false. Otherwise, other applications cannot use the positioning function of Huawei location service.
            RadioButton radioButton = radioGroup.findViewById(checkedId);
            mMockModeFlag = Boolean.parseBoolean(radioButton.getText().toString());
            Log.d(TAG, "onCheckedChanged mMockModeFlag : " + mMockModeFlag);
            updateLogResults("You Can set MockLocation with Selected MockMode : " + mMockModeFlag);
        });

        RadioGroup mRadioGroupGetContextState = findViewById(R.id.radioGroup_contextState);
        mRadioGroupGetContextState.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton radioButton = radioGroup.findViewById(checkedId);
            if (checkedId == R.id.radioGroup_state_overpass) {
                mContextType = 1;
            } else if (checkedId == R.id.radioGroup_state_supportEx) {
                mContextType = 2;
            }
            Log.d(TAG, "onCheckedChanged mContextType : " + mContextType + " : " + radioButton.getText().toString());
            updateLogResults("You Can Get Navigation Context State with Selected NavContextState : " + mContextType);
        });

    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_location));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_start_loc_feed, R.id.btn_stop_loc_feed, R.id.btn_last_known_loc,
            R.id.btn_get_location_availability, R.id.btn_start_loc_intent, R.id.btn_stop_loc_intent,
            R.id.btn_start_activity_identification, R.id.btn_start_activity_conversion,
            R.id.btn_setMock_location, R.id.btn_getContext_State,
            R.id.btn_getLocationHD, R.id.btn_start_activity_geoFence})
    public void onItemClick(View v) {
        updateLogResults("Result Logs Will Be Here");
        switch (v.getId()) {
            case R.id.btn_start_loc_feed:
                if (isGrantPermissions()) {
                    requestLocationUpdatesWithCallback();
                }
                break;
            case R.id.btn_stop_loc_feed:
                removeLocationUpdatesWithCallback();
                break;
            case R.id.btn_last_known_loc:
                checkLastKnownLocation();
                break;
            case R.id.btn_get_location_availability:
                getLocationAvailability();
                break;
            case R.id.btn_start_activity_identification:
                if (isGrantPermissions()) {
                    Utils.startActivity(LocationKitActivity.this, ActivityIdentificationActivity.class);
                }
                break;
            case R.id.btn_start_activity_conversion:
                if (isGrantPermissions()) {
                    Utils.startActivity(LocationKitActivity.this, ActivityConversionActivity.class);
                }
                break;
            case R.id.btn_setMock_location:
                setMockMode();
                break;
            case R.id.btn_getContext_State:
                getNavigationContextState();
                break;
            case R.id.btn_getLocationHD:
                Utils.startActivity(LocationKitActivity.this, HdLocationActivity.class);
                break;
            case R.id.btn_start_loc_intent:
                if (isGrantPermissions()) {
                    requestLocationUpdatesWithIntent();
                }
                break;
            case R.id.btn_stop_loc_intent:
                removeLocationUpdatesWithIntent();
                break;
            case R.id.btn_start_activity_geoFence:
                Utils.startActivity(LocationKitActivity.this, GeoFenceActivity.class);
                break;
            default:
                Log.i(TAG, getString(R.string.defaultText));
        }
    }

    /**
     * create a fusedLocationProviderClient
     */
    private void createFusedLocationProviderClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * create a locationSettingClient
     */
    private void createLocationSettingClient() {
        settingsClient = LocationServices.getSettingsClient(this);
    }


    /**
     * create LocationRequest and Set parameters for continuously requesting device locations
     * <p>
     * Call the onLocationResult() method in the defined LocationCallback class
     * to obtain a LocationResult object that contains the location information.
     * Return the location information in the extension information of the PendingIntent object.
     */
    private void createLocationInformationRequest() {
        mLocationRequest = new LocationRequest();
        // Set the interval for location updates, in milliseconds.
        mLocationRequest.setInterval(2000);
        // set the priority of the request
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        createLocationUpdatesWithCallback();
    }

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
                                String locationUpdateString = Utils.getTimeStamp() + " :\n" +
                                        "[ " + location.getLongitude() + " - " + location.getLatitude() + " ] \n" +
                                        "( accuracy  " + location.getAccuracy() + " ) - speed : " + location.getSpeed();

                                Log.i(TAG, "createLocationUpdatesWithCallback.onLocationResult location[Longitude,Latitude,Accuracy]:" + locationUpdateString);

                                updateLocationInfo(location);
                                updateLogResults("LocationUpdatesWithCallback : \n" + locationUpdateString + "\n");
                            }
                        }
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    if (locationAvailability != null) {
                        boolean flag = locationAvailability.isLocationAvailable();
                        Log.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                    }
                }
            };
        }
    }


    private PendingIntent getPendingIntent() {
        // The LocationBroadcastReceiver class is a customized static broadcast class. For details about the implementation, please refer to the sample code.
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(ACTION_PROCESS_LOCATION);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    /**
     * requestLocationUpdatesWithCallback
     * <p>
     * function:Requests location updates with a callback on the specified Looper thread.
     * first:use SettingsClient object to call checkLocationSettings(LocationSettingsRequest locationSettingsRequest) method to check device settings.
     * second: use  FusedLocationProviderClient object to call requestLocationUpdates (LocationRequest request, LocationCallback callback, Looper looper) method.
     */
    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // check devices settings before request location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        Log.i(TAG, "requestLocationUpdatesWithCallback check location settings success");
                        // request location updates
                        fusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                .addOnSuccessListener(aVoid -> {
                                    Utils.showToastMessage(getApplicationContext(), "Location Feed has been started.");
                                    Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess");
                                    updateLogResults("requestLocationUpdatesWithCallback onSuccess");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "requestLocationUpdatesWithCallback onFailure : " + e.getMessage(), e);
                                    Utils.showToastMessage(getApplicationContext(), "Location Feed info failure");
                                    updateLogResults("requestLocationUpdatesWithCallback onFailure : " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "checkLocationSetting onFailure : " + e.getMessage(), e);
                        updateLogResults("checkLocationSetting onFailure : " + e.getMessage());
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(LocationKitActivity.this, activityResultCodeGpsForCallback);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e(TAG, "PendingIntent onFailure unable to execute request.getMessage " + sie.getMessage());
                                updateLogResults("requestLocationUpdatesWithCallback PendingIntent onFailure : " + sie.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback Exception : " + e.getMessage(), e);
            updateLogResults("requestLocationUpdatesWithCallback Exception : " + e.getMessage());
        }
    }

    /**
     * to stop requesting location updates
     * Note: When requesting location updates is stopped,
     * the mLocationCallback object must be the same as LocationCallback in the requestLocationUpdates method.
     */
    private void removeLocationUpdatesWithCallback() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess");
                        updateLogResults("removeLocationUpdatesWithCallback onSuccess");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "removeLocationUpdatesWithCallback onFailure : " + e.getMessage(), e);
                        updateLogResults("removeLocationUpdatesWithCallback onFailure : " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback Exception : " + e.getMessage(), e);
            updateLogResults("removeLocationUpdatesWithCallback Exception : " + e.getMessage());
        }
    }


    /**
     * getLocationAvailability
     */
    private void getLocationAvailability() {
        try {
            Task<LocationAvailability> locationAvailability = fusedLocationProviderClient.getLocationAvailability();
            locationAvailability
                    .addOnSuccessListener(locationAvailabilityResult -> {
                        if (locationAvailabilityResult != null) {
                            Log.e(TAG, "getLocationAvailability : " + locationAvailabilityResult.toString());
                            updateLogResults("getLocationAvailability : " + locationAvailabilityResult.toString());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "getLocationAvailability onFailure : " + e.getMessage(), e);
                        updateLogResults("getLocationAvailability onFailure : " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "getLocationAvailability Exception : " + e.getMessage(), e);
            updateLogResults("getLocationAvailability Exception : " + e.getMessage());
        }
    }


    /**
     * Setting the mock Mode
     */
    private void setMockMode() {
        try {
            Log.i(TAG, "setMockMode mock mode is " + mMockModeFlag);
            // Note: To enable the mock function, enable the android.permission.ACCESS_MOCK_LOCATION permission in the AndroidManifest.xml file,
            // and set the application to the mock location app in the device setting.
            Task<Void> voidTask = fusedLocationProviderClient.setMockMode(mMockModeFlag);
            voidTask.addOnSuccessListener(aVoid -> {
                Log.e(TAG, "setMockMode onSuccess ");
                updateLogResults("setMockMode onSuccess");

                if (mMockModeFlag) setMockLocation();

            }).addOnFailureListener(e -> {
                Log.e(TAG, "setMockMode onFailure : " + e.getMessage(), e);
                updateLogResults("setMockMode onFailure : " + e.getMessage());
                if (e.getMessage().contains("PERMISSION_DENIED") || e.getMessage().contains("10803")) {
                    Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.mock_mode_use_warning));
                    updateLogResults(getResources().getString(R.string.mock_mode_use_method_introduction) + "\n" + tvResultLogs.getText());
                    if (Utils.isDeveloperOptionOpen(getApplicationContext())) {
                        showDeveloperOptionsWarning("Developer Options And MOCK LOCATION Permission", "Mock Location");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "setMockMode Exception : " + e.getMessage(), e);
            updateLogResults("setMockMode Exception : " + e.getMessage());
        }
    }

    private void setMockLocation() {
        final Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLongitude(mockLongitudeValue);
        mockLocation.setLatitude(mockLatitudeValue);
        fusedLocationProviderClient.setMockLocation(mockLocation)
                // Define callback for success in setting the mock location information.
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, getString(R.string.setMockLocationOnSuccessWith) + mockLatitudeValue + " - " + mockLongitudeValue);
                    Utils.showToastMessage(getApplicationContext(), getString(R.string.setMockLocationOnSuccessWith) + mockLatitudeValue + " - " + mockLongitudeValue);
                    updateLogResults(getString(R.string.setMockLocationOnSuccessWith) + mockLatitudeValue + " - " + mockLongitudeValue);
                    checkLastKnownLocation();
                })
                // Define callback for failure in setting the mock location information.
                .addOnFailureListener(e -> {
                    Log.e(TAG, getString(R.string.mockLocationOnFailure) + e.getMessage(), e);
                    Utils.showToastMessage(getApplicationContext(), getString(R.string.mockLocationOnFailure) + e.getMessage());
                    updateLogResults(getString(R.string.mockLocationOnFailure) + e.getMessage());
                });
    }

    /**
     * getNavigationContextState
     */
    private void getNavigationContextState() {
        LocationEnhanceService locationEnhanceService = LocationServices.getLocationEnhanceService(this);
        try {
            Log.d(TAG, "getNavigationContextState with mContextType : " + mContextType);
            NavigationRequest request = new NavigationRequest(mContextType);
            Task<NavigationResult> task = locationEnhanceService.getNavigationState(request)
                    .addOnSuccessListener(result -> {
                        Log.e(TAG, "getNavigationContextState onSuccess State is : \n" + result.getState() + " Possibility is : " + result.getPossibility());
                        updateLogResults("NavigationContextState onSuccess State is \n:" + result.getState() + " Possibility is : " + result.getPossibility());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "getNavigationContextState onFailure : " + e.getMessage(), e);
                        updateLogResults("NavigationContextState onFailure : " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "getNavigationContextState Exception : " + e.getMessage(), e);
            updateLogResults("NavigationContextState Exception : " + e.getMessage());
        }

    }


    @SuppressLint("SetTextI18n")
    private void updateLocationInfo(Location location) {
        tvRequestCount.setText("Request Count : " + reqCount);
        tvAccuracy.setText("Accuracy : " + location.getAccuracy());
        if (mMockModeFlag) {
            tvLatitude.setText("Latitude : " + location.getLatitude() + " ( MockLoc )");
            tvLongitude.setText("Longitude : " + location.getLongitude() + " ( MockLoc )");
        } else {
            tvLatitude.setText("Latitude : " + location.getLatitude());
            tvLongitude.setText("Longitude : " + location.getLongitude());
        }
    }

    public void updateLogResults(String msg) {
        tvResultLogs.setText(msg);
    }

    /**
     * Obtain the last known location.
     */
    private void checkLastKnownLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location == null) {
                            if (mMockModeFlag) {
                                Log.e(TAG, "CheckLastKnownLocation is NULL!\nMockMode is TRUE\nmock mode api exception:10803: PERMISSION_DENIED");
                                Utils.showToastMessage(getApplicationContext(), "Current Location " + getResources().getString(R.string.mock_mode_use_warning));
                                updateLogResults("For use Mock Current Location : You you should follow these steps :\n" +
                                        getResources().getString(R.string.mock_mode_use_method_introduction));
                                if (Utils.isDeveloperOptionOpen(getApplicationContext())) {
                                    showDeveloperOptionsWarning("Developer Options And MOCK LOCATION Permission", "Mock Location");
                                }
                            } else {
                                Utils.showDialogGpsWarning(this,
                                        "Location is NULL!\nNEED GPS Settings Check",
                                        getString(R.string.permissionSettings),
                                        R.drawable.icon_settings_loc,
                                        "You can not use Location Features without GPS!",
                                        getString(R.string.yesGo), getString(R.string.cancel));
                            }

                            return;
                        }

                        String lastLocationString = "Location[ " + location.getLatitude() + " " + location.getLongitude() + " ] ";

                        if (mMockModeFlag)
                            lastLocationString = lastLocationString + " MockLocation!";

                        Log.i(TAG, "CheckLastKnownLocation onSuccess " + lastLocationString);
                        updateLogResults("CheckLastKnownLocation onSuccess\n" + lastLocationString);

                        latitudeValue = location.getLatitude();
                        longitudeValue = location.getLongitude();

                        updateLocationInfo(location);
                        //Processing logic of the Location object.
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "CheckLastKnownLocation onFailure : " + e.getMessage(), e);
                        Utils.showToastMessage(getApplicationContext(), "Error: Last known location could not found!");
                        updateLogResults("CheckLastKnownLocation onFailure:\n" + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "CheckLastKnownLocation exception : " + e.getMessage(), e);
            updateLogResults("CheckLastKnownLocation : Exception : " + e.getMessage());
        }
    }


    /**
     * Request for location update
     */
    private void requestLocationUpdatesWithIntent() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            //Before requesting location update, invoke checkLocationSettings to check device settings.
            Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest);
            locationSettingsResponseTask.
                    addOnSuccessListener(locationSettingsResponse -> {
                        Log.i(TAG, "check location settings success");
                        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getPendingIntent())
                                .addOnSuccessListener(aVoid -> {
                                    Log.i(TAG, "LocationUpdates WithIntent onSuccess");
                                    updateLogResults("LocationUpdates WithIntent onSuccess");
                                })
                                .addOnFailureListener(e -> {
                                    Log.i(TAG, "LocationUpdates WithIntent onFailure!");
                                    updateLogResults("LocationUpdates WithIntent onFailure!");
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "requestLocationUpdates onFailure : " + e.getMessage(), e);
                        updateLogResults("requestLocationUpdates onFailure : " + e.getMessage());
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == RESOLUTION_REQUIRED) {
                            try {
                                //When the startResolutionForResult is invoked, a dialog box is displayed, asking you to open the corresponding permission.
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(LocationKitActivity.this, activityResultCodeGpsForIntent);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e(TAG, "requestLocationUpdates onFailure unable to execute request.getLocalizedMessage " + sie.getLocalizedMessage());
                                Log.e(TAG, "requestLocationUpdates onFailure unable to execute request.getMessage " + sie.getMessage());
                                updateLogResults("RequestLocationUpdates WithIntent onFailure : " + sie.getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "LocationUpdates WithIntent  Exception : " + e.getMessage(), e);
            updateLogResults("LocationUpdates WithIntent  Exception : " + e.getMessage());
        }
    }

    /**
     * Remove Location Update
     */
    private void removeLocationUpdatesWithIntent() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(pendingIntent)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "RemoveLocationUpdates WithIntent onSuccess");
                        updateLogResults("RemoveLocationUpdates WithIntent onSuccess");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "RemoveLocationUpdates WithIntent onFailure : " + e.getMessage(), e);
                        updateLogResults("RemoveLocationUpdates WithIntent onFailure : " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "RemoveLocationUpdates WithIntent Exception : " + e.getMessage(), e);
            updateLogResults("RemoveLocationUpdates WithIntent Exception : " + e.getMessage());
        }
    }


    private boolean isGrantPermissions() {
        boolean isPermitGrant;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            isPermitGrant = Utils.isGrantLocationPermissions(LocationKitActivity.this, Utils.permissionRequest1, Utils.permissionRequestCode1);
        } else {
            isPermitGrant = Utils.isGrantLocationPermissions(LocationKitActivity.this, Utils.permissionRequest2, Utils.permissionRequestCode2);
        }
        return isPermitGrant;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.permissionRequestCode1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, getString(R.string.onRequestPermissionsResultLocationPermissionSuccessful));
            } else {
                Log.e(TAG, getString(R.string.onRequestPermissionsResultLocationPermissionFailed));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (requestCode == activityResultCodeGpsForCallback) {
            if (resultCode == -1) {
                Log.i(TAG, getString(R.string.applyGpsResolvableApiExceptionSuccessful));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.gpsIsActivated));
                mLocationGnss.setGnssUsable(true);
                mLocationGnss.setGnssPresent(true);
                requestLocationUpdatesWithCallback();
            } else {
                Log.i(TAG, getString(R.string.applyGpsResolvableApiExceptionSuccessful));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.gpsIsNotActivated));
                mLocationGnss.setGnssUsable(false);
            }
        }
        if (requestCode == activityResultCodeGpsForIntent) {
            if (resultCode == -1) {
                Log.i(TAG, getString(R.string.applyGpsResolvableApiExceptionSuccessful));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.gpsIsActivated));
                mLocationGnss.setGnssUsable(true);
                mLocationGnss.setGnssPresent(true);
                checkLastKnownLocation();
                requestLocationUpdatesWithIntent();
            } else {
                Log.i(TAG, getString(R.string.applyGpsResolvableApiExceptionSuccessful));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.gpsIsNotActivated));
                mLocationGnss.setGnssUsable(false);
            }
        }
    }

    public void showNeedPermissionWarning(String msg, String perm) {
        Utils.showDialogPermissionWarning(this,
                msg,
                getString(R.string.permissionSettings),
                R.drawable.icon_settings_loc,
                "You can not use Location Features without " + perm + " Permission!",
                getString(R.string.yesGo), getString(R.string.cancel));
    }

    public void showDeveloperOptionsWarning(String msg, String perm) {
        Utils.showDialogDeveloperOptionsWarning(this,
                msg,
                "Would You Like To Go To Developer Options and Allow to Mock Location?",
                R.drawable.icon_settings_loc,
                "You can not use Mock Location Feature without " + perm + " Permission!",
                getString(R.string.yesGo), getString(R.string.cancel));
    }


    public void getCompleteAddress(Double latitude, Double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String featureName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            String adminArea = addresses.get(0).getAdminArea();
            String subAdminArea = addresses.get(0).getSubAdminArea();

            String addressDetailInformation = "getCompleteAddress : \n" +
                    "address      : " + address + "\n" +
                    "country      : " + country + "\n" +
                    "city         : " + city + "\n" +
                    "adminArea    : " + adminArea + "\n" +
                    "subAdminArea : " + subAdminArea + "\n" +
                    "state        : " + state + "\n" +
                    "postalCode   : " + postalCode + "\n" +
                    "featureName  : " + featureName + "\n";

            Log.i(TAG, addressDetailInformation);
            updateLogResults(addressDetailInformation);

        } catch (Exception ex) {
            Log.e(TAG, "getCompleteAddress : Exception : " + ex.getMessage(), ex);
            updateLogResults("getCompleteAddress : Exception : " + ex.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pendingIntent != null) {
            removeLocationUpdatesWithIntent();
        }
        //if (mLocationCallback != null) {
        //    removeLocationUpdatesWithCallback();
        //}
        unbinder.unbind();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}