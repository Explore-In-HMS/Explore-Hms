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

package com.hms.explorehms.locationkit;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hms.explorehms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hms.location.Geofence;
import com.huawei.hms.location.GeofenceRequest;
import com.huawei.hms.location.GeofenceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GeoFenceActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = GeoFenceActivity.class.getSimpleName();

    private GeofenceService geofenceService;

    protected static final List<GeoFenceRequestList> geoFenceRequestLists = new ArrayList<>();

    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_geoFenceData)
    TextView tvGeoFenceData;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tv_geoRequestData)
    TextView tvGeoRequestData;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_trigger)
    TextInputEditText etTrigger;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_removeWithPendingIntentInput)
    TextInputEditText etRemoveWithPendingIntentInput;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.et_removeWithIdInput)
    TextInputEditText etRemoveWithIdInput;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.resultLogs)
    TextView tvResultLogs;

    @SuppressLint("StaticFieldLeak")
    private static GeoFenceActivity instance;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);

        unbinder = ButterKnife.bind(this);

        instance = this;

        geofenceService = new GeofenceService(this);
        setupToolbar();
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


    public static GeoFenceActivity getInstance() {
        return instance;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_createAddGeoFence, R.id.btn_removeGeoFence, R.id.btn_getGeoFenceData,
            R.id.btn_sendRequestPending, R.id.btn_sendRequestNew, R.id.btn_getRequestMessage,
            R.id.btn_removeWithIntent, R.id.btn_removeWithID})
    public void onItemClick(View v) {
        updateLogResults(getString(R.string.resultLogs));
        switch (v.getId()) {
            case R.id.btn_createAddGeoFence:
                List<Integer> intentFlags = new ArrayList<>();
                intentFlags.add(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Utils.startActivity(GeoFenceActivity.this, GeoFenceCreateActivity.class, intentFlags);
                break;
            case R.id.btn_removeGeoFence:
                GeoFenceCreateActivity.GeoFenceData.createNewList();
                break;
            case R.id.btn_getGeoFenceData:
                getGeoFenceData();
                break;
            case R.id.btn_sendRequestPending:
                requestGeoFenceWithIntent();
                break;
            case R.id.btn_sendRequestNew:
                requestGeoFenceWithNewIntent();
                break;
            case R.id.btn_getRequestMessage:
                getRequestMessage();
                break;
            case R.id.btn_removeWithIntent:
                removeWithIntent();
                break;
            case R.id.btn_removeWithID:
                removeWithID();
                break;
            default:
                Log.i(TAG,getString(R.string.defaultText));
        }
    }

    public void getGeoFenceData() {
        List<Geofence> geoFences = GeoFenceCreateActivity.GeoFenceData.returnList();
        StringBuilder buf = new StringBuilder();
        String s;
        if (geoFences.isEmpty()) {
            buf.append(getString(R.string.noGeoFenceData));
        }
        for (int i = 0; i < geoFences.size(); i++) {
            buf.append(getString(R.string.uniqueID)).append(geoFences.get(i).getUniqueId()).append("\n");
        }
        s = buf.toString();
        tvGeoFenceData.setText(s);
        Log.d(TAG, getString(R.string.getGeoFenceData) + s);
        updateLogResults(getString(R.string.getGeoFenceData) + s);
    }


    public void requestGeoFenceWithIntent() {
        String msg;
        if (GeoFenceCreateActivity.GeoFenceData.returnList().isEmpty()) {
            msg = getResources().getString(R.string.no_new_request_to_add);
            tvGeoRequestData.setText(msg);
            Log.d(TAG, getString(R.string.getGeoFenceWithIntent) + msg);
            updateLogResults(getString(R.string.getGeoFenceWithIntentEmptySpace) + msg);
            return;
        }
        if (geoFenceRequestLists.isEmpty()) {
            msg = getString(R.string.noPendingIntentToSend);
            tvGeoRequestData.setText(msg);
            Log.d(TAG, getString(R.string.getGeoFenceWithIntent) + msg);
            updateLogResults(getString(R.string.getGeoFenceWithIntentEmptySpace) + msg);
            return;
        }
        if (checkUniqueID()) {
            msg = getString(R.string.checkUniqueID);
            tvGeoRequestData.setText(msg);
            Log.d(TAG, getString(R.string.getGeoFenceWithIntent) + msg);
            updateLogResults(getString(R.string.getGeoFenceWithIntentEmptySpace) + msg);
            return;
        }
        checkUniqueID();
        GeofenceRequest.Builder geofenceRequest = new GeofenceRequest.Builder();
        geofenceRequest.createGeofenceList(GeoFenceCreateActivity.GeoFenceData.returnList());

        if (!Objects.requireNonNull(etTrigger.getText()).toString().isEmpty()) {
            geofenceRequest.setInitConversions(5);
            msg = getString(R.string.defaultTriggerFive);
        } else {
            int trigGer = Integer.parseInt(etTrigger.getText().toString());
            geofenceRequest.setInitConversions(trigGer);
            msg = getString(R.string.defaultTrigger) + trigGer;
        }

        Log.d(TAG, getString(R.string.getGeoFenceWithIntent) + msg);
        updateLogResults(getString(R.string.getGeoFenceWithIntentEmptySpace) + msg);

        GeoFenceRequestList temp = geoFenceRequestLists.get(geoFenceRequestLists.size() - 1);

        PendingIntent pendingIntent = temp.pendingIntent;

        setGeoFenceRequestList(pendingIntent, temp.requestCode, GeoFenceCreateActivity.GeoFenceData.returnList());

        try {
            geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, getString(R.string.createGeoFenceListOnSuccess));
                            updateLogResults(getString(R.string.createGeoFenceListOnSuccess));
                        } else {
                            // Get the status code for the error and log it using a user-friendly message.
                            Log.e(TAG, getString(R.string.requestGeoFenceWithIntentOnFailureTask) + task.getException().getMessage(), task.getException());
                            updateLogResults(getString(R.string.requestGeoFenceWithIntentGetException) + task.getException().getMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, getString(R.string.requestGeoFenceWithIntentOnFailure) + e.getMessage(), e);
                        updateLogResults(getString(R.string.requestGeoFenceWithIntentOnFailure)+ e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.requestGeoFenceWithIntentException) + e.getMessage(), e);
            updateLogResults(getString(R.string.requestGeoFenceWithIntentException) + e.getMessage());
        }
        GeoFenceCreateActivity.GeoFenceData.createNewList();
    }


    public boolean checkUniqueID() {
        boolean result = false;
        for (GeoFenceRequestList geoFence : geoFenceRequestLists) {
            if (geoFence.checkID()){
                Log.d(TAG, getString(R.string.checkUniqueID_) + geoFence.toString() + getString(R.string.isTrue));
                result = true;
                break;
            }
        }
        if(!result){
            Log.d(TAG, getString(R.string.checkUniqueIDGeoFenceRequestListsFalse));
        }
        return result;
    }


    public void setGeoFenceRequestList(PendingIntent intent, int code, List<Geofence> geoFences) {
        GeoFenceRequestList temp = new GeoFenceRequestList(intent, code, geoFences);
        geoFenceRequestLists.add(temp);
    }


    public void getRequestMessage() {
        StringBuilder buf = new StringBuilder();
        String s = "";
        for (GeoFenceRequestList geoFence : geoFenceRequestLists) {
            buf.append(geoFence.show());
        }
        if (s.isEmpty()) {
            buf.append(getString(R.string.no_request));
        }
        s = buf.toString();

        tvGeoRequestData.setText(s);
        Log.d(TAG, getString(R.string.getRequestMessage) + s);
        updateLogResults(getString(R.string.getRequestMessage_) + s);
    }


    public void removeWithIntent() {
        String etText = Objects.requireNonNull(etRemoveWithPendingIntentInput.getText()).toString();
        if (etText.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), getString(R.string.pleaseEnterPendingIntentId));
        } else {
            PendingIntent intent = findIntentByID(Integer.parseInt(etText));
            if (intent == null) {
                tvGeoRequestData.setText(getResources().getString(R.string.no_such_intent_for_geo_fence));
                Log.d(TAG, "removeWithIntent findIntentByID : No such intent!");
                return;
            }
            try {
                geofenceService.deleteGeofenceList(intent)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "removeWithIntent deleteGeoFence : onSuccess");
                                updateLogResults("removeWithIntent deleteGeoFence : onSuccess");
                            } else {
                                // Get the status code for the error and log it using a user-friendly message.
                                Log.e(TAG, "removeWithIntent deleteGeoFence onFailure : task.getException() : " + task.getException().getMessage(), task.getException());
                                updateLogResults("removeWithIntent deleteGeoFence \ntask.getException() : " + task.getException().getMessage());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "removeWithIntent deleteGeoFence onFailure : " + e.getMessage(), e);
                            updateLogResults("removeWithIntent deleteGeoFence onFailure : " + e.getMessage());
                        });
            } catch (Exception e) {
                Log.e(TAG, "removeWithIntent deleteGeoFence Exception : " + e.getMessage(), e);
                updateLogResults("removeWithIntent deleteGeoFence Exception : " + e.getMessage());
            }
        }
    }

    public PendingIntent findIntentByID(int a) {
        StringBuilder msg = new StringBuilder();
        PendingIntent intent = null;

        for (GeoFenceRequestList geoFence : geoFenceRequestLists) {
            if (geoFence.requestCode == a) {
                intent = geoFence.pendingIntent;
                geoFenceRequestLists.remove(geoFence);
                msg.append(" - ").append(geoFence.geoFences.get(0).getUniqueId());
            }
        }
        if (msg.length() > 0) {
            Log.d(TAG, "findIntentByID : Removed UniqueId : " + msg);
        }
        return intent;
    }

    public void removeWithID() {
        String etText = Objects.requireNonNull(etRemoveWithIdInput.getText()).toString();
        if (etText.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please Enter the GeoFence Id");
        } else {
            String[] str = etText.split(" ");
            List<String> list = new ArrayList<>();
            Collections.addAll(list, str);
            try {
                geofenceService.deleteGeofenceList(list)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, getString(R.string.removeWithIDDeleteGeoFenceOnSuccess) + task.getResult());
                                listRemoveID(str);
                            } else {
                                // Get the status code for the error and log it using a user-friendly message.
                                Log.e(TAG, getString(R.string.removeWithIDDeleteGeoFenceOnFailure) + task.getResult() + " : task.getException() : " + task.getException().getMessage(), task.getException());
                                updateLogResults("removeWithID deleteGeoFence \ntask.getException() : " + task.getException().getMessage());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, getString(R.string.removeWithIDDeleteGeoFenceOnFailure) + e.getMessage(), e);
                            updateLogResults(getString(R.string.removeWithIDDeleteGeoFenceOnFailure) + e.getMessage());
                        });
            } catch (Exception e) {
                Log.e(TAG, "removeWithID deleteGeoFence Exception : " + e.getMessage(), e);
                updateLogResults("removeWithID deleteGeoFence Exception : " + e.getMessage());
            }

        }
    }

    public void listRemoveID(String[] str) {
        StringBuilder msg = new StringBuilder();
        for (GeoFenceRequestList geoFence : geoFenceRequestLists) {
            String removedId = geoFence.removeID(str);
            msg.append(removedId).append(",");
        }
        Log.d(TAG, "listRemoveID : geoFenceRequestLists " + geoFenceRequestLists.toString());
        Log.d(TAG, "listRemoveID : listRemoveID : msg " + msg.toString());
        if (msg.toString().equals(",")) {
            Log.d(TAG, "listRemoveID : removeWithUniqueID : not Removed any uniqueId From List with By " + Arrays.toString(str));
            updateLogResults("removeWithUniqueID : not Removed any uniqueId From List with By " + Arrays.toString(str));
        } else {
            Log.d(TAG, "listRemoveID : removeWithUniqueID : onSuccess : Removed uniqueId : " + msg);
            updateLogResults("removeWithUniqueID : onSuccess : Removed uniqueId  : " + msg);
        }
    }


    public void requestGeoFenceWithNewIntent() {
        String msg = "";
        if (GeoFenceCreateActivity.GeoFenceData.returnList().isEmpty()) {
            msg = getResources().getString(R.string.no_new_request_to_add);
            tvGeoRequestData.setText(msg);
            Log.d(TAG, getString(R.string.requestGeoFenceWithNewIntent)  + msg);
            updateLogResults(getString(R.string.requestGeoFenceWithNewIntentWithEmptySpace) + msg);
            return;
        }
        if (checkUniqueID()) {
            msg = getString(R.string.checkUniqueID);
            tvGeoRequestData.setText(msg);
            Log.d(TAG, getString(R.string.requestGeoFenceWithNewIntent) + msg);
            updateLogResults(getString(R.string.requestGeoFenceWithNewIntentWithEmptySpace) + msg);
            return;
        }
        GeofenceRequest.Builder geofenceRequest = new GeofenceRequest.Builder();
        geofenceRequest.createGeofenceList(GeoFenceCreateActivity.GeoFenceData.returnList());

        if (!Objects.requireNonNull(etTrigger.getText()).toString().isEmpty()) {
            int trigGer = Integer.parseInt(etTrigger.getText().toString());
            geofenceRequest.setInitConversions(trigGer);
            msg = "Trigger is " + trigGer;
        } else {
            geofenceRequest.setInitConversions(5);
            msg = "Default trigger is 5";
        }

        Log.d(TAG, getString(R.string.requestGeoFenceWithNewIntent) + msg);
        updateLogResults(getString(R.string.requestGeoFenceWithNewIntentWithEmptySpace) + msg);

        PendingIntent pendingIntent = getPendingIntent();
        setGeoFenceRequestList(pendingIntent, GeoFenceCreateActivity.GeoFenceData.getRequestCode(), GeoFenceCreateActivity.GeoFenceData.returnList());
        try {
            geofenceService.createGeofenceList(geofenceRequest.build(), pendingIntent)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, getString(R.string.createGeoFenceListOnSuccess));
                            updateLogResults(getString(R.string.createGeoFenceListOnSuccess));
                        } else {
                            // Get the status code for the error and log it using a user-friendly message.
                            Log.e(TAG, "requestGeoFenceWithNewIntent onFailure : task.getException() : " + task.getException().getMessage(), task.getException());
                            updateLogResults("requestGeoFenceWithNewIntent \ntask.getException() : " + task.getException().getMessage());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "requestGeoFenceWithNewIntent onFailure : " + e.getMessage(), e);
                        updateLogResults("requestGeoFenceWithNewIntent onFailure : " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "requestGeoFenceWithNewIntent Exception : " + e.getMessage(), e);
            updateLogResults("requestGeoFenceWithNewIntent Exception : " + e.getMessage());
        }
        GeoFenceCreateActivity.GeoFenceData.createNewList();
    }


    /**
     * getPendingIntent  with use GeoFenceBroadcastReceiver
     * PendingIntent by broadcast from GeoFenceCreateActivity.GeoFenceData.getRequestCode()
     *
     * @return PendingIntent.getBroadcast()
     */
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, GeoFenceBroadcastReceiver.class);
        intent.setAction(GeoFenceBroadcastReceiver.ACTION_PROCESS_LOCATION);
        Log.d(TAG, "new request");
        GeoFenceCreateActivity.GeoFenceData.newRequest();
        return PendingIntent.getBroadcast(this, GeoFenceCreateActivity.GeoFenceData.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    public void updateLogResults(String msg) {
        tvResultLogs.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public static class GeoFenceRequestList {
        private final PendingIntent pendingIntent;
        private final int requestCode;
        private final List<Geofence> geoFences;

        public GeoFenceRequestList(PendingIntent pendingIntent, int requestCode, List<Geofence> geoFences) {
            this.pendingIntent = pendingIntent;
            this.requestCode = requestCode;
            this.geoFences = geoFences;
        }

        public String show() {
            StringBuilder buf = new StringBuilder();
            String s = "";
            for (Geofence gf : geoFences) {
                buf.append("PendingIntent: ").append(requestCode).append(" UniqueID: ").append(gf.getUniqueId()).append("\n");
            }
            s = buf.toString();
            return s;
        }

        public boolean checkID() {
            List<Geofence> returnList = GeoFenceCreateActivity.GeoFenceData.returnList();
            for (Geofence retGfList : returnList) {
                String retId = retGfList.getUniqueId();
                for (Geofence gf : geoFences) {
                    if (retId.equals(gf.getUniqueId())) {
                        return true;
                        //id already exist
                    }
                }
            }
            return false;
        }

        public String removeID(String[] strList) {
            String msg = "";
            for (String strId : strList) {
                for (Geofence gf : geoFences) {
                    if (strId.equals(gf.getUniqueId())) {
                        geoFences.remove(gf);
                        msg = gf.getUniqueId();
                        break;
                    }
                }
            }
            return msg;
        }
    }


}