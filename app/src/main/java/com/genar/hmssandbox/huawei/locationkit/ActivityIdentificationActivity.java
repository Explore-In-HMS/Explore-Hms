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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.huawei.hms.location.ActivityIdentification;
import com.huawei.hms.location.ActivityIdentificationData;
import com.huawei.hms.location.ActivityIdentificationService;

import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivityIdentificationActivity extends AppCompatActivity {


    //region variablesAndObjects
    private static final String TAG = ActivityIdentificationActivity.class.getSimpleName();

    private ActivityIdentificationService activityIdentificationService;

    private PendingIntent pendingIntent;

    private Unbinder unbinder;

    private LinearLayout layoutActivityInVehicle;
    private LinearLayout layoutActivityOnBicycle;
    private LinearLayout layoutActivityOnFoot;
    private LinearLayout layoutActivityStill;
    private LinearLayout layoutActivityUnknown;
    private LinearLayout layoutActivityWalking;
    private LinearLayout layoutActivityRunning;

    private LinearLayout.LayoutParams type0;
    private LinearLayout.LayoutParams type1;
    private LinearLayout.LayoutParams type2;
    private LinearLayout.LayoutParams type3;
    private LinearLayout.LayoutParams type4;
    private LinearLayout.LayoutParams type7;
    private LinearLayout.LayoutParams type8;

    private TextView tvResultLogs;

    private static final int permissionRequestCode = 1;

    @SuppressLint("StaticFieldLeak")
    private static ActivityIdentificationActivity instance;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        setupToolbar();

        unbinder = ButterKnife.bind(this);

        instance = this;

        initLayouts();

        createActivityIdentificationService();

        editAndSetLayoutParams();

        reSetLayoutParams();

        Utils.checkActivityRecognitionPermission(ActivityIdentificationActivity.this, permissionRequestCode);
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

    public static ActivityIdentificationActivity getInstance() {
        return instance;
    }

    private void initLayouts() {
        tvResultLogs = findViewById(R.id.resultLogs);

        layoutActivityInVehicle = findViewById(R.id.layoutActivityIN_VEHICLE);
        layoutActivityOnBicycle = findViewById(R.id.layoutActivityON_BICYCLE);
        layoutActivityOnFoot = findViewById(R.id.layoutActivityON_FOOT);
        layoutActivityStill = findViewById(R.id.layoutActivitySTILL);
        layoutActivityUnknown = findViewById(R.id.layoutActivityUNKNOWN);
        layoutActivityWalking = findViewById(R.id.layoutActivityWALKING);
        layoutActivityRunning = findViewById(R.id.layoutActivityRunning);

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_requestActivityTransitionUpdate, R.id.btn_removeActivityTransitionUpdate})
    public void onItemClick(View v) {
        if(v.getId() == R.id.btn_requestActivityTransitionUpdate){
            if (Utils.checkActivityRecognitionPermission(ActivityIdentificationActivity.this, permissionRequestCode)) {
                requestActivityUpdates(5000);
            }
        }else if(v.getId() == R.id.btn_removeActivityTransitionUpdate){
            removeActivityUpdates();

        }
    }

    /**
     * create a ActivityIdentificationService
     */
    private void createActivityIdentificationService() {
        activityIdentificationService = ActivityIdentification.getService(this);
    }

    public void editAndSetLayoutParams() {
        type0 = (LinearLayout.LayoutParams) layoutActivityInVehicle.getLayoutParams();
        type1 = (LinearLayout.LayoutParams) layoutActivityOnBicycle.getLayoutParams();
        type2 = (LinearLayout.LayoutParams) layoutActivityOnFoot.getLayoutParams();
        type3 = (LinearLayout.LayoutParams) layoutActivityStill.getLayoutParams();
        type4 = (LinearLayout.LayoutParams) layoutActivityUnknown.getLayoutParams();
        type7 = (LinearLayout.LayoutParams) layoutActivityWalking.getLayoutParams();
        type8 = (LinearLayout.LayoutParams) layoutActivityRunning.getLayoutParams();
    }

    public void reSetLayoutParams() {
        int progressbarOriginWidth = 100;
        type0.width = progressbarOriginWidth;
        layoutActivityInVehicle.setLayoutParams(type0);

        type1.width = progressbarOriginWidth;
        layoutActivityOnBicycle.setLayoutParams(type1);

        type2.width = progressbarOriginWidth;
        layoutActivityOnFoot.setLayoutParams(type2);

        type3.width = progressbarOriginWidth;
        layoutActivityStill.setLayoutParams(type3);

        type4.width = progressbarOriginWidth;
        layoutActivityUnknown.setLayoutParams(type4);

        type7.width = progressbarOriginWidth;
        layoutActivityWalking.setLayoutParams(type7);

        type8.width = progressbarOriginWidth;
        layoutActivityRunning.setLayoutParams(type8);
    }


    private PendingIntent createAndGetPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void requestActivityUpdates(long detectionIntervalMillis) {
        try {
            if (pendingIntent != null) {
                removeActivityUpdates();
            }
            pendingIntent = createAndGetPendingIntent();
            LocationBroadcastReceiver.addIdentificationListener();
            activityIdentificationService.createActivityIdentificationUpdates(detectionIntervalMillis, pendingIntent)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, getString(R.string.startActivityIdentificationsSuccess));
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.startActivityIdentificationsSuccess));
                        updateLogResults(getString(R.string.startActivityIdentificationsSuccess));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, getString(R.string.startActivityIdentificationsFailure_) + e.getMessage());
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.startActivityIdentificationsFailure__) + e.getMessage());
                        updateLogResults(getString(R.string.startActivityIdentificationsFailure_) + e.getMessage());
                        if (Objects.requireNonNull(e.getMessage()).contains(getString(R.string.permissionDenied))) {
                            showNeedPermissionWarning(getString(R.string.physicalActivityPermission), getString(R.string.physicalActivity));
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.startActivityIdentificationsException) + e.getMessage());
            Utils.showToastMessage(getApplicationContext(), getString(R.string.startActivityIdentificationsException_) + e.getMessage());
        }
    }

    public void removeActivityUpdates() {
        reSetLayoutParams();
        try {
            LocationBroadcastReceiver.removeIdentificationListener();
            Log.i(TAG, getString(R.string.startToRemoteActivityUpdates));
            activityIdentificationService.deleteActivityIdentificationUpdates(pendingIntent)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, (getString(R.string.deleteActivityIdentificationUpdatesSuccess)));
                        Utils.showToastMessage(getApplicationContext(),  (getString(R.string.deleteActivityIdentificationUpdatesSuccess)));
                        updateLogResults( (getString(R.string.deleteActivityIdentificationUpdatesSuccess)));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, getString(R.string.deleteActivityIdentificationsFailure)+ e.getMessage(), e);
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.deleteActivityIdentificationsFailure_));
                        updateLogResults(getString(R.string.deleteActivityIdentificationsFailure_));
                    });

        } catch (Exception e) {
            Log.e(TAG, getString(R.string.deleteActivityIdentificationsExceptions) + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), getString(R.string.deleteActivityIdentificationsExceptions_) + e.getMessage());
        }
    }


    public void updateLogResults(String msg) {
        tvResultLogs.setText(msg);
    }


    public void setLayoutDataAndParams(List<ActivityIdentificationData> list) {
        reSetLayoutParams();
        for (int i = 0; i < list.size(); i++) {
            int type = list.get(i).getIdentificationActivity();
            int value = list.get(i).getPossibility();
            try {
                int enLarge = 6;
                switch (type) {
                    case ActivityIdentificationData.VEHICLE:
                        type0.width = type0.width + value * enLarge;
                        layoutActivityInVehicle.setLayoutParams(type0);
                        break;
                    case ActivityIdentificationData.BIKE:
                        type1.width = type1.width + value * enLarge;
                        layoutActivityOnBicycle.setLayoutParams(type1);
                        break;
                    case ActivityIdentificationData.FOOT:
                        type2.width = type2.width + value * enLarge;
                        layoutActivityOnFoot.setLayoutParams(type2);
                        break;
                    case ActivityIdentificationData.STILL:
                        type3.width = type3.width + value * enLarge;
                        layoutActivityStill.setLayoutParams(type3);
                        break;
                    case ActivityIdentificationData.OTHERS:
                        type4.width = type4.width + value * enLarge;
                        layoutActivityUnknown.setLayoutParams(type4);
                        break;
                    case ActivityIdentificationData.WALKING:
                        type7.width = type7.width + value * enLarge;
                        layoutActivityWalking.setLayoutParams(type7);
                        break;
                    case ActivityIdentificationData.RUNNING:
                        type8.width = type8.width + value * enLarge;
                        layoutActivityRunning.setLayoutParams(type8);
                        break;
                    default:
                        break;
                }
            } catch (RuntimeException e) {
                Log.e(TAG, getString(R.string.setLayoutDataAndParamsRuntimeException)  + e.getMessage(), e);
            } catch (Exception e) {
                Log.e(TAG, getString(R.string.setLayoutDataAndParamsException_) + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionRequestCode) {
            if (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.i(TAG, getString(R.string.onRequestPermissionsResultPermission));

                requestActivityUpdates(5000);

            } else {
                Log.e(TAG, getString(R.string.onRequestPermissionsResultPermissionFailed));
                showNeedPermissionWarning(getString(R.string.needActivityRecognitionPermission), getString(R.string.activityRecognition));
            }
        }
    }

    public void showNeedPermissionWarning(String msg, String perm) {
        Utils.showDialogPermissionWarning(this,
                msg,
                getString(R.string.permissionSettings),
                R.drawable.icon_settings_loc,
                getString(R.string.activityRecognitionFeatures) + perm + getString(R.string.permission),
                getString(R.string.yesGo), getString(R.string.cancel));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pendingIntent != null) {
            removeActivityUpdates();
        }
        unbinder.unbind();
    }

}
