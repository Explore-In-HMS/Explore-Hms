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
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.R;
import com.huawei.hms.location.ActivityConversionInfo;
import com.huawei.hms.location.ActivityConversionRequest;
import com.huawei.hms.location.ActivityIdentification;
import com.huawei.hms.location.ActivityIdentificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ActivityConversionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ActivityConversionActivity.class.getSimpleName();

    private ActivityIdentificationService activityIdentificationService;

    private List<ActivityConversionInfo> transitions;

    private PendingIntent pendingIntent;

    private Unbinder unbinder;

    private CheckBox cbInVehicleIn;
    private CheckBox cbWalkingIn;
    private CheckBox cbWalkingOut;
    private CheckBox cbInVehicleOut;
    private CheckBox cbOnBicycleIn;
    private CheckBox cbOnBicycleOut;
    private CheckBox cbOnFootIn;
    private CheckBox cbOnFootOut;
    private CheckBox cbStillIn;
    private CheckBox cbStillOut;
    private CheckBox cbRunningIn;
    private CheckBox cbRunningOut;

    private TextView tvResultLogs;

    private static final int permissionRequestCode = 1;

    @SuppressLint("StaticFieldLeak")
    private static ActivityConversionActivity instance;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        setupToolbar();
        unbinder = ButterKnife.bind(this);
        instance = this;
        initLayouts();
        createActivityIdentificationService();

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

    public static ActivityConversionActivity getInstance() {
        return instance;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_requestActivityTransitionUpdate, R.id.btn_removeActivityTransitionUpdate})
    public void onItemClick(View v) {
        if(v.getId() == R.id.btn_requestActivityTransitionUpdate){
            if (Utils.checkActivityRecognitionPermission(
                    ActivityConversionActivity.this,
                    permissionRequestCode)
            ) {
                getRequest();
                requestActivityTransitionUpdate();
            }
        } else if(v.getId() == R.id.btn_removeActivityTransitionUpdate){
            removeActivityTransitionUpdates();
        }
    }

    private void initLayouts() {
        tvResultLogs = findViewById(R.id.resultLogs);

        cbInVehicleIn = (CheckBox) findViewById(R.id.cb_IN_VEHICLE_IN);
        cbInVehicleOut = (CheckBox) findViewById(R.id.cb_IN_VEHICLE_OUT);
        cbOnBicycleIn = (CheckBox) findViewById(R.id.cb_ON_BICYCLE_IN);
        cbOnBicycleOut = (CheckBox) findViewById(R.id.cb_ON_BICYCLE_OUT);
        cbOnFootIn = (CheckBox) findViewById(R.id.cb_ON_FOOT_IN);
        cbOnFootOut = (CheckBox) findViewById(R.id.cb_ON_FOOT_OUT);
        cbStillIn = (CheckBox) findViewById(R.id.cb_STILL_IN);
        cbStillOut = (CheckBox) findViewById(R.id.cb_STILL_OUT);
        cbWalkingIn = (CheckBox) findViewById(R.id.cb_WALKING_IN);
        cbWalkingOut = (CheckBox) findViewById(R.id.cb_WALKING_OUT);
        cbRunningIn = (CheckBox) findViewById(R.id.cb_RUNNING_IN);
        cbRunningOut = (CheckBox) findViewById(R.id.cb_RUNNING_OUT);

    }

    /**
     * create a ActivityIdentificationService
     */
    private void createActivityIdentificationService() {
        activityIdentificationService = ActivityIdentification.getService(this);
    }


    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationBroadcastReceiver.class);
        intent.setAction(LocationBroadcastReceiver.ACTION_PROCESS_LOCATION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void requestActivityTransitionUpdate() {
        try {
            if (pendingIntent != null) {
                removeActivityTransitionUpdates();
            }
            updateLogResults(getString(R.string.resultLogs));
            LocationBroadcastReceiver.addConversionListener();
            pendingIntent = getPendingIntent();
            ActivityConversionRequest activityTransitionRequest = new ActivityConversionRequest(transitions);
            activityIdentificationService.createActivityConversionUpdates(activityTransitionRequest, pendingIntent)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, getString(R.string.createActivityConversionUpdatesSuccess));
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.createActivityConversionUpdatesSuccess));
                    }).addOnFailureListener(e -> {
                Log.e(TAG, getString(R.string.createActivityConversionUpdatesOnFailure) + e.getMessage());
                Utils.showToastMessage(getApplicationContext(), getString(R.string.createActivityConversionUpdatesOnFailure_)  + e.getMessage());
                updateLogResults(getString(R.string.createActivityConversionUpdatesOnFailure)  + e.getMessage());
                if (Objects.requireNonNull(e.getMessage()).contains(getString(R.string.permissionDenied))) {
                    showNeedPermissionWarning(getString(R.string.physicalActivityPermission), getString(R.string.physicalActivity));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.createActivityConversionUpdatesException) + e.getMessage());
            Utils.showToastMessage(getApplicationContext(), getString(R.string.createActivityConversionUpdatesException_) + e.getMessage());

        }
    }

    public void removeActivityTransitionUpdates() {
        try {
            LocationBroadcastReceiver.removeConversionListener();
            activityIdentificationService.deleteActivityConversionUpdates(pendingIntent)
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, getString(R.string.removeActivityConversionUpdatesSuccess));
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.removeActivityConversionUpdatesSuccess));
                        updateLogResults(getString(R.string.removeActivityConversionUpdatesSuccess));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, getString(R.string.removeActivityConversionUpdatesFailure_) + e.getMessage(), e);
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.removeActivityConversionUpdatesFailure));
                        updateLogResults(getString(R.string.removeActivityConversionUpdatesFailure__) + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.removeActivityConversionUpdatesException) + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), getString(R.string.removeActivityConversionUpdatesExceptionEmptySpace) + e.getMessage());
        }
    }


    public void updateLogResults(String msg) {
        tvResultLogs.setText(msg);
    }


    public void getRequest() {
        transitions = new ArrayList<>();
        ActivityConversionInfo.Builder activityTransition = new ActivityConversionInfo.Builder();
        RequestValueResult requestValueResult = new RequestValueResult();
        if (cbInVehicleIn.isChecked())
            requestValueResult.addList(100, 0);

        if (cbInVehicleOut.isChecked())
            requestValueResult.addList(100, 1);

        if (cbOnBicycleIn.isChecked())
            requestValueResult.addList(101, 0);

        if (cbOnBicycleOut.isChecked())
            requestValueResult.addList(101, 1);

        if (cbOnFootIn.isChecked())
            requestValueResult.addList(102, 0);

        if (cbOnFootOut.isChecked())
            requestValueResult.addList(102, 1);

        if (cbStillIn.isChecked())
            requestValueResult.addList(103, 0);

        if (cbStillOut.isChecked())
            requestValueResult.addList(103, 1);

        if (cbWalkingIn.isChecked())
            requestValueResult.addList(107, 0);

        if (cbWalkingOut.isChecked())
            requestValueResult.addList(107, 1);

        if (cbRunningIn.isChecked())
            requestValueResult.addList(108, 0);

        if (cbRunningOut.isChecked())
            requestValueResult.addList(108, 1);

        List<RequestValue> result = requestValueResult.result;
        for (int i = 0; i < result.size(); i++) {
            RequestValue temp = result.get(i);
            activityTransition.setActivityType(temp.activityType);
            activityTransition.setConversionType(temp.activityTransition);
            transitions.add(activityTransition.build());
        }
        Log.d(TAG, "getRequest : transitions size is " + transitions.size());
    }


    private static class RequestValue {
        private final int activityType;

        private final int activityTransition;

        RequestValue(int a, int b) {
            this.activityType = a;
            this.activityTransition = b;
        }
    }

    private static class RequestValueResult {
        private final List<RequestValue> result = new ArrayList<>();

        public void addList(int activityType, int activityTransition) {
            RequestValue temp = new RequestValue(activityType, activityTransition);
            result.add(temp);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.i(TAG, "onRequestPermissionsResult: apply ACTIVITY_RECOGNITION PERMISSION successful");

                getRequest();
                requestActivityTransitionUpdate();

            } else {
                Log.e(TAG, "onRequestPermissionsResult: apply ACTIVITY_RECOGNITION PERMISSION  failed");
                showNeedPermissionWarning("NEED ACTIVITY_RECOGNITION PERMISSION", "Activity Recognition");
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
            removeActivityTransitionUpdates();
        }
        unbinder.unbind();
    }


}
