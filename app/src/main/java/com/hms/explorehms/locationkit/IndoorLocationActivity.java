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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hms.explorehms.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.HWLocation;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;

import java.util.List;
import java.util.Map;

import butterknife.OnClick;

public class IndoorLocationActivity extends AppCompatActivity {

    //HMS Location Kit Objects
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    //UI Objects
    private TextView tv_resultLogs;
    private TextView tv_requestCounts;
    private TextView tv_floor;
    private TextView tv_floor_acc;
    private TextView tv_time;
    private TextView tv_introduction;

    //Buttons
    private Button btn_get_indoor_location;
    private Button btn_start_update;
    private Button btn_stop_update;

    //Global variables
    int reqCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_location);

        initView();
        setupToolbar();
        initListeners();
        createFusedLocationProviderClient();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Called when the user presses the "back" button in the toolbar.
     * It handles the behavior for navigation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView(){
        //TextViews
        tv_resultLogs = findViewById(R.id.resultLogsIndoor);
        tv_floor = findViewById(R.id.tv_floor_indoor);
        tv_floor_acc = findViewById(R.id.tv_floor_acc_indoor);
        tv_time = findViewById(R.id.tv_time_indoor);
        tv_requestCounts = findViewById(R.id.tv_request_count_indoor);
        tv_introduction = findViewById(R.id.tv_indoor_location_introduction);
        //Buttons
        btn_get_indoor_location = findViewById(R.id.btn_get_indoor_location);
        btn_start_update = findViewById(R.id.btn_start_indoor_location_update);
        btn_stop_update = findViewById(R.id.btn_stop_indoor_location_update);
    }

    private void initListeners(){
        tv_introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/indoor-positioning-develop-steps-0000001188842631";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        btn_get_indoor_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLocationInformationRequest();
            }
        });

        btn_start_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdateEx();
            }
        });

        btn_stop_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLocationUpdateEx();
            }
        });

    }

    private void createFusedLocationProviderClient(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void createLocationInformationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_INDOOR);

        createLocationCallback();
    }

    private void createLocationCallback(){
        try {
            Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.indoor_location_requested));
            mLocationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    List<HWLocation> mHWLocations = locationResult.getHWLocationList();
                    for (HWLocation mHWLocation : mHWLocations){
                        Map<String, Object> maps = mHWLocation.getExtraInfo();
                        parseIndoorLocation(maps);
                    }

                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                }
            };
        }catch (Exception e){
            Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.indoor_location_request_fail) + e.getMessage());
        }


    }

    private void parseIndoorLocation(Map<String, Object> maps){
        if (maps != null && !maps.isEmpty()){
            if (maps.containsKey("isHdNlpLocation")){
                Object object = maps.get("isHdNlpLocation");
                if (object instanceof Boolean){
                    boolean isIndoorLocation = (boolean) object;
                    if(isIndoorLocation){
                        int floor = (int) maps.get("floor");
                        int floorAcc = (int) maps.get("floorAcc");
                        Object obj = maps.get("time");
                        long time = 0;
                        if (obj instanceof Integer){
                            time = ((Integer) obj).longValue();

                        }else if( obj instanceof Long){
                            time = ((Long) obj).longValue();
                        }

                        //Update UI with indoor location information's
                        tv_floor.setText(String.valueOf(floor));
                        tv_floor_acc.setText(String.valueOf(floorAcc));
                        tv_time.setText(String.valueOf(time));
                        tv_resultLogs.setText(Utils.getTimeStamp());
                    }
                }
            }
        }
    }

    private void requestLocationUpdateEx(){
        try {
            Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.indoor_location_requested));
            fusedLocationProviderClient
                    .requestLocationUpdatesEx(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reqCount++;
                            tv_requestCounts.setText(String.valueOf(reqCount));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.request_location_update_ex_failure) + e.getMessage());
                        }
                    });
        }catch (Exception e){
            Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.indoor_location_request_update_fail) + e.getMessage());
        }

    }

    private void removeLocationUpdateEx(){
        fusedLocationProviderClient
                .removeLocationUpdates(mLocationCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.remove_location_update_ex_success));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Utils.showToastMessage(IndoorLocationActivity.this, getString(R.string.remove_location_update_ex_failure) + e.getMessage());

                    }
                });
    }

}