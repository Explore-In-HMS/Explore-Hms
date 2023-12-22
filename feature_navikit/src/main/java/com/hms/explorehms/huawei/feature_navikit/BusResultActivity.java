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

package com.hms.explorehms.huawei.feature_navikit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_navikit.setting.CommonSetting;
import com.hms.explorehms.huawei.feature_navikit.utils.CommonUtil;
import com.hms.explorehms.huawei.feature_navikit.utils.ConstantNaviUtil;
import com.hms.explorehms.huawei.feature_navikit.utils.DefaultMapNavi;
import com.hms.explorehms.huawei.feature_navikit.utils.ToastUtil;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.VehicleType;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;
import com.huawei.hms.navi.navibase.model.bus.BusNaviPathBean;
import com.huawei.hms.navi.navibase.model.busnavirequest.BusCqlRequest;
import com.huawei.hms.navi.navibase.model.busnavirequest.Destination;
import com.huawei.hms.navi.navibase.model.busnavirequest.Origin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BusResultActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "BusResultActivity";

    private Button busPlan;

    private EditText busEnd, busStart, alternatives, returnArray, pedestrianMaxDistance, changes, pedestrianSpeed;;

    private RadioButton dr1, dr2, dr3, dr4;

    private RadioGroup operationEntity;

    private MapNavi mapNavi;

    private Context context;

    private MapNaviListener mapNaviListener = new DefaultMapNavi() {
        @Override
        public void onCalcuBusDriveRouteSuccess(BusNaviPathBean busNaviPathBean) {
            ToastUtil.showToast(BusResultActivity.this, "bus routing success");

        }

        @Override
        public void onCalcuBusDriveRouteFailed(int i) {
            ToastUtil.showToast(BusResultActivity.this, "bus routing fail: " + i);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_result);
        context = this;
        initNavi();
        initView();
        setupToolbar();
        initBusSite();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarNaviKitInfo);
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

    private void initView() {
        busPlan = findViewById(R.id.btn_bus_routing);
        busPlan.setOnClickListener(this);
        busEnd = findViewById(R.id.bus_end_point);
        busStart = findViewById(R.id.bus_start_point);
        alternatives = findViewById(R.id.bus_alternatives);
        returnArray = findViewById(R.id.bus_return_array);
        pedestrianMaxDistance = findViewById(R.id.bus_pedestrianMaxDistance);
        changes = findViewById(R.id.bus_changes);
        pedestrianSpeed = findViewById(R.id.bus_pedestrianSpeed);
        dr1 = findViewById(R.id.bus_dr1);
        dr2 = findViewById(R.id.bus_dr2);
        dr3 = findViewById(R.id.bus_dr3);
        dr4 = findViewById(R.id.bus_dr4);
        operationEntity = (RadioGroup) findViewById(R.id.radioGroup_bus);
        if (operationEntity != null) {
            operationEntity.setOnCheckedChangeListener(this);
        }
    }

    private void initNavi() {
        mapNavi = MapNavi.getInstance(this);
        mapNavi.addMapNaviListener(mapNaviListener);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bus_routing) {
            if ("".equals(new ConstantNaviUtil(context).API_KEY)) {
                ToastUtil.showToast(this, "please input apiKey");
                return;
            } else {
                setApiKey(new ConstantNaviUtil(context).API_KEY);
            }
            // begin point
            String latLngStr = busStart.getText().toString();
            String[] latLngArrayStart = latLngStr.split(",");

            // end point
            String latLngStr2 = busEnd.getText().toString();
            String[] latLngArrayEnd = latLngStr2.split(",");

            // Number of Alternative Routes
            int alternativesValue = Integer.parseInt(alternatives.getText().toString());

            // Maximum number of transfers
            int changeValue = Integer.parseInt(changes.getText().toString());

            // Returned information
            String returnStr = returnArray.getText().toString();
            String[] returnArrayValue = returnStr.split(",");

            // Walking distance
            int pedestrianMaxDistanceValue = Integer.parseInt(pedestrianMaxDistance.getText().toString());

            // Walking speed
            int pedestrianSpeedValue = Integer.parseInt(pedestrianSpeed.getText().toString());

            BusCqlRequest routePlan = new BusCqlRequest();
            Origin or = new Origin();
            if (1 < latLngArrayStart.length) {
                or.setLat(Double.parseDouble(latLngArrayStart[0]));
                or.setLng(Double.parseDouble(latLngArrayStart[1]));
            }

            Destination des = new Destination();
            if (1 < latLngArrayEnd.length) {
                des.setLat(Double.parseDouble(latLngArrayEnd[0]));
                des.setLng(Double.parseDouble(latLngArrayEnd[1]));
            }

            routePlan.setOrigin(or);
            routePlan.setDestination(des);
            routePlan.setReturnMode(returnArrayValue);
            routePlan.setAlternatives(alternativesValue);
            routePlan.setLanguage("en");
            routePlan.setUnits(0);
            routePlan.setChanges(changeValue);
            routePlan.setPedestrianSpeed(pedestrianSpeedValue);
            routePlan.setPedestrianMaxDistance(pedestrianMaxDistanceValue);
            if (mapNavi != null) {
                mapNavi.setVehicleType(VehicleType.BUS);
                mapNavi.calculateBusDriveRoute(routePlan);
            }
        }
    }

    private void initBusSite() {
        if (mapNavi == null) {
            return;
        }

        String busSite = CommonSetting.getServerSite();
        switch (busSite) {
            case DevServerSiteConstant.DR4:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                dr4.setChecked(true);
                break;
            case DevServerSiteConstant.DR3:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                dr3.setChecked(true);
                break;
            case DevServerSiteConstant.DR2:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                dr2.setChecked(true);
                break;
            default:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                dr1.setChecked(true);
                break;
        }
    }

    private void setApiKey(String apiKey) {
        if(apiKey == null || mapNavi == null) {
            return;
        }
        try {
            mapNavi.setApiKey(URLEncoder.encode(apiKey, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        CommonUtil.changeServerSite(checkedId, mapNavi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapNavi != null) {
            mapNavi.removeMapNaviListener(mapNaviListener);
        }
    }
}
