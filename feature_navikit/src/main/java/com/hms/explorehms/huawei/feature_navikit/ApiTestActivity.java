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
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_navikit.setting.CommonSetting;
import com.hms.explorehms.huawei.feature_navikit.utils.CommonUtil;
import com.hms.explorehms.huawei.feature_navikit.utils.ConstantNaviUtil;
import com.hms.explorehms.huawei.feature_navikit.utils.DefaultMapNavi;
import com.hms.explorehms.huawei.feature_navikit.utils.ToastUtil;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.MapNaviRoutingTip;
import com.huawei.hms.navi.navibase.enums.VehicleType;
import com.huawei.hms.navi.navibase.model.ClientParas;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;
import com.huawei.hms.navi.navibase.model.MapNaviPath;
import com.huawei.hms.navi.navibase.model.NaviRequestPoint;
import com.huawei.hms.navi.navibase.model.NaviStrategy;
import com.huawei.hms.navi.navibase.model.RoutingRequestParam;
import com.huawei.hms.navi.navibase.model.locationstruct.NaviLatLng;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiTestActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "ApiTestActivity";

    private Button routingStartBtn, naviStartBtn, busBtn;

    private TextView tvFirstDistanceResult, tvFirstPassTimeResult, tvFirstTrafficNumResult, tvSecondDistanceResult, tvSecondPassTimeResult, tvSecondTrafficNumResult, tvThirdDistanceResult, tvThirdPassTimeResult, tvThirdTrafficNumResult;

    private Spinner spStrategy, spMode;

    private VehicleType mVehicleType = VehicleType.DRIVING;

    private MapNavi mapNavi;

    private RoutingRequestParam routingRequestParam;

    private NaviLatLng hwFrom, hwTo;

    private EditText mStartPoint, mEndPoint, mWayPoint1, mWayPoint2;

    private RadioButton dr1, dr2, dr3, dr4;

    private RadioGroup operationEntity;

    private ArrayList<NaviLatLng> mWayPoints = new ArrayList<>();

    private boolean saveTime, avoidFerry, avoidHighway, avoidToll, saveDistance, smartRecommend, priorityRoad, priorityHighway, saveMoney, avoidCongestion, isRouteCalculateSuccess = false;

    private Context context;

    private MapNaviListener mapNaviListener = new DefaultMapNavi() {
        @Override
        public void onCalculateRouteFailure(int errCode) {
            ToastUtil.showToast(ApiTestActivity.this, "drive cal fail, error: " + errCode);
        }

        @Override
        public void onCalculateRouteSuccess(int[] routeIds, MapNaviRoutingTip mapNaviRoutingTip) {
            showResultForCalculate();
        }

        @Override
        public void onCalculateWalkRouteFailure(int errorCode) {
            ToastUtil.showToast(ApiTestActivity.this, "walk cal fail, error: " + errorCode);
        }

        @Override
        public void onCalculateWalkRouteSuccess(int[] routeIds, MapNaviRoutingTip mapNaviRoutingTip) {
            showResultForCalculate();
        }

        @Override
        public void onCalculateCycleRouteFailure(int errorCode) {
            ToastUtil.showToast(ApiTestActivity.this, "cycle cal fail, error: " + errorCode);
        }

        @Override
        public void onCalculateCycleRouteSuccess(int[] routeIds, MapNaviRoutingTip mapNaviRoutingTip) {
            showResultForCalculate();
        }

        @Override
        public void onStartNavi(int code) {
            ToastUtil.showToast(ApiTestActivity.this, "startNavi complete code is :" + code);
            if (code == 0) {
                isRouteCalculateSuccess = false;
                Intent intent = new Intent(ApiTestActivity.this, NaviActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
        context = this;
        initView();
        setupToolbar();
        initListener();

        initMapNavi();
        initApiTestSite();
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
        routingStartBtn = findViewById(R.id.btn_routing);
        naviStartBtn = findViewById(R.id.btn_start_navi);
        busBtn = findViewById(R.id.btn_bus_page);
        mStartPoint = (EditText) findViewById(R.id.m_start_point);
        mEndPoint = (EditText) findViewById(R.id.m_end_point);
        mWayPoint1 = (EditText) findViewById(R.id.way_one_point);
        mWayPoint2 = (EditText) findViewById(R.id.way_two_point);
        spStrategy = findViewById(R.id.sp_navi_strategy);
        spMode = findViewById(R.id.sp_navi_mode);
        operationEntity = (RadioGroup) findViewById(R.id.radioGroup_api);
        dr1 = findViewById(R.id.dr1);
        dr2 = findViewById(R.id.dr2);
        dr3 = findViewById(R.id.dr3);
        dr4 = findViewById(R.id.dr4);
        tvFirstDistanceResult = findViewById(R.id.tvFirstDistanceResult);
        tvFirstPassTimeResult = findViewById(R.id.tvFirstPassTimeResult);
        tvFirstTrafficNumResult = findViewById(R.id.tvFirstTrafficNumResult);
        tvSecondDistanceResult = findViewById(R.id.tvSecondDistanceResult);
        tvSecondPassTimeResult = findViewById(R.id.tvSecondPassTimeResult);
        tvSecondTrafficNumResult = findViewById(R.id.tvSecondTrafficNumResult);
        tvThirdDistanceResult = findViewById(R.id.tvThirdDistanceResult);
        tvThirdPassTimeResult = findViewById(R.id.tvThirdPassTimeResult);
        tvThirdTrafficNumResult = findViewById(R.id.tvThirdTrafficNumResult);
    }

    private void initListener() {
        operationEntity.setOnCheckedChangeListener(this);
        routingStartBtn.setOnClickListener(v -> {
            isRouteCalculateSuccess = false;
            // collecting input parameters
            getRequestParamForRouting();
            // route planning
            routing();
        });

        naviStartBtn.setOnClickListener(v -> {
            if (isRouteCalculateSuccess) {
                if (mVehicleType == VehicleType.DRIVING) {
                    mapNavi.calculateDriveGuide();
                }

                if (mVehicleType == VehicleType.WALKING) {
                    mapNavi.calculateWalkGuide();
                }

                if (mVehicleType == VehicleType.CYCLING) {
                    mapNavi.calculateCyclingGuide();
                }
            } else {
                ToastUtil.showToast(this, "please calculate route first");
            }
        });

        busBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusResultActivity.class);
            startActivity(intent);
        });

        spStrategy.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearNaviStrategy();
                switch (position) {
                    case 0:
                        smartRecommend = true;
                        break;
                    case 1:
                        saveTime = true;
                        break;
                    case 2:
                        avoidHighway = true;
                        break;
                    case 3:
                        avoidFerry = true;
                        break;
                    case 4:
                        avoidToll = true;
                        break;
                    case 5:
                        saveDistance = true;
                        break;
                    case 6:
                        avoidFerry = true;
                        avoidToll = true;
                        break;
                    case 7:
                        priorityRoad = true;
                        break;
                    case 8:
                        priorityHighway = true;
                        break;
                    case 9:
                        saveMoney = true;
                        break;
                    case 10:
                        avoidCongestion = true;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                clearNaviStrategy();
                smartRecommend = true;
            }
        });

        spMode.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mVehicleType = VehicleType.toVehicleType(0);
                        break;
                    case 1:
                        mVehicleType = VehicleType.toVehicleType(1);
                        break;
                    case 2:
                        mVehicleType = VehicleType.toVehicleType(2);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mVehicleType = VehicleType.toVehicleType(0);
            }
        });
    }

    private void initMapNavi() {
        mapNavi = MapNavi.getInstance(this);
        mapNavi.addMapNaviListener(mapNaviListener);
    }

    private void getRequestParamForRouting() {
        routingRequestParam = new RoutingRequestParam();
        String startStr = mStartPoint.getText().toString().trim();
        String endStr = mEndPoint.getText().toString().trim();
        List<String> wayPointStrList = new ArrayList<>();
        String wayPointStr1 = mWayPoint1.getText().toString().trim();
        String wayPointStr2 = mWayPoint2.getText().toString().trim();
        wayPointStrList.add(wayPointStr1);
        wayPointStrList.add(wayPointStr2);
        int i = latlngCheck(startStr, endStr, wayPointStrList);
        if (i != 0) {
            ToastUtil.showToast(this, "input point is invalids");
            return;
        }

        // Start and end points
        List<NaviRequestPoint> fromPoints = new ArrayList<>();
        NaviRequestPoint fromPnt = new NaviRequestPoint();
        fromPnt.setPoint(hwFrom);
        fromPoints.add(fromPnt);
        List<NaviRequestPoint> toPoints = new ArrayList<>();
        NaviRequestPoint toPnt = new NaviRequestPoint();
        toPnt.setPoint(hwTo);
        toPoints.add(toPnt);

        // Pass-through Point
        List<NaviRequestPoint> wayPoints = new ArrayList<>();
        if (mWayPoints != null) {
            for (NaviLatLng naviLatLng : mWayPoints) {
                NaviRequestPoint requestWayPoint = new NaviRequestPoint();
                requestWayPoint.setPoint(naviLatLng);
                wayPoints.add(requestWayPoint);
            }
        }

        // User preference setting
        NaviStrategy naviStrategy = new NaviStrategy();
        if (mVehicleType.equals(VehicleType.DRIVING)) {
            naviStrategy.setSaveTime(saveTime);
            naviStrategy.setAvoidFerry(avoidFerry);
            naviStrategy.setAvoidHighway(avoidHighway);
            naviStrategy.setAvoidToll(avoidToll);
            naviStrategy.setSaveDistance(saveDistance);
            naviStrategy.setSmartRecommend(smartRecommend);
            naviStrategy.setRoadPriority(priorityRoad);
            naviStrategy.setHighwayPriority(priorityHighway);
            naviStrategy.setSaveMoney(saveMoney);
            naviStrategy.setAvoidCrowd(avoidCongestion);
        } else {
            naviStrategy.setAvoidFerry(avoidFerry);
        }

        Log.i(TAG, "vehicleType: " + mVehicleType.name() + " navi strategy: ");

        routingRequestParam.setFromPoints(fromPoints);
        routingRequestParam.setToPoints(toPoints);
        routingRequestParam.setWayPoints(wayPoints);
        routingRequestParam.setStrategy(naviStrategy);
    }

    private void routing() {
        isRouteCalculateSuccess = false;
        if ("".equals(new ConstantNaviUtil(context).API_KEY)) {
            ToastUtil.showToast(this, "please input apiKey");
            return;
        } else {
            setApiKey(new ConstantNaviUtil(context).API_KEY);
        }
        if (mapNavi != null) {
            mapNavi.setVehicleType(mVehicleType);
            switch (mVehicleType) {
                case DRIVING:
                    mapNavi.calculateDriveRoute(routingRequestParam);
                    break;
                case WALKING:
                    mapNavi.calculateWalkRoute(routingRequestParam);
                    break;
                case CYCLING:
                    mapNavi.calculateCycleRoute(routingRequestParam);
                    break;
                default:
                    break;
            }
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



    private int latlngCheck(String startStr, String endStr, List<String> wayPointStrList) {
        if (TextUtils.isEmpty(startStr)) {
            ToastUtil.showToast(this, "Enter the start point.");
            return 1;
        }
        if (TextUtils.isEmpty(endStr)) {
            ToastUtil.showToast(this, "Enter the end point.");
            return 1;
        }
        if (startStr.equals(endStr)) {
            ToastUtil.showToast(this, "The start point and end point cannot be the same.");
            return 2;
        }

        for (String wayPointStr : wayPointStrList) {
            if (wayPointStr.equals(startStr) || wayPointStr.equals(endStr)) {
                ToastUtil.showToast(this, "The way point cannot be the same as the start and end points.");
                return 3;
            }
        }

        try {
            // start point
            if (hwFrom != null) {
                hwFrom = null;
            }
            double startLat = Double.parseDouble(startStr.split(",")[0]);
            double startLng = Double.parseDouble(startStr.split(",")[1]);
            hwFrom = new NaviLatLng(startLat, startLng);

            // end point
            if (hwTo != null) {
                hwTo = null;
            }
            double toLat = Double.parseDouble(endStr.split(",")[0]);
            double toLng = Double.parseDouble(endStr.split(",")[1]);
            hwTo = new NaviLatLng(toLat, toLng);

            // pass-through point
            if (mWayPoints != null && mWayPoints.size() > 0) {
                mWayPoints.clear();
            }

            if (mWayPoints == null) {
                mWayPoints = new ArrayList<>();
            }

            for (String pointStr : wayPointStrList) {
                if (!TextUtils.isEmpty(pointStr)) {
                    double wayLat = Double.parseDouble(pointStr.split(",")[0]);
                    double wayLng = Double.parseDouble(pointStr.split(",")[1]);
                    NaviLatLng wayLatLng = new NaviLatLng(wayLat, wayLng);
                    mWayPoints.add(wayLatLng);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            ToastUtil.showToast(this, "Enter the correct longitude and latitude.");
            return -1;
        }
        return 0;
    }

    private void clearNaviStrategy() {
        smartRecommend = false;
        saveTime = false;
        saveDistance = false;
        avoidFerry = false;
        avoidHighway = false;
        avoidToll = false;
        priorityRoad = false;
        priorityHighway = false;
        saveMoney = false;
        avoidCongestion = false;
    }

    private void showResultForCalculate() {
        isRouteCalculateSuccess = true;
        Map<Integer, MapNaviPath> naviPaths = mapNavi.getNaviPaths();
        String routeInfo = "";
        Iterator<Map.Entry<Integer, MapNaviPath>> iterator = naviPaths.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, MapNaviPath> entry = iterator.next();
            Integer routeId = entry.getKey();
            MapNaviPath naviPath = entry.getValue();
            routeInfo += " routeId: " + routeId + " routeInfo: " + "{ distance: " + naviPath.getAllLength()
                    + "m passTime: " + naviPath.getAllTime() + "s trafficNum: " + naviPath.getTrafficLightNum() + "}";

            switch (routeId) {
                case 0:
                    tvFirstDistanceResult.setText(naviPath.getAllLength() + "m");
                    tvFirstPassTimeResult.setText(naviPath.getAllTime() + "s");
                    tvFirstTrafficNumResult.setText(String.valueOf(naviPath.getTrafficLightNum()));
                    break;
                case 1:
                    tvSecondDistanceResult.setText(naviPath.getAllLength() + "m");
                    tvSecondPassTimeResult.setText(naviPath.getAllTime() + "s");
                    tvSecondTrafficNumResult.setText(String.valueOf(naviPath.getTrafficLightNum()));
                    break;
                case 2:
                    tvThirdDistanceResult.setText(naviPath.getAllLength() + "m");
                    tvThirdPassTimeResult.setText(naviPath.getAllTime() + "s");
                    tvThirdTrafficNumResult.setText(String.valueOf(naviPath.getTrafficLightNum()));
                    break;
                default:
                    //Only 3 routes can be created
            }
        }
        Log.i(TAG, "route size：" + naviPaths.size() + " routeInfo: " + routeInfo);
        //ToastUtil.showToast(this, "route size：" + naviPaths.size() + " " + routeInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initApiTestSite();
        Log.d(TAG, "============onResume");
    }

    private void initApiTestSite() {
        if (mapNavi == null) {
            return;
        }

        String site = CommonSetting.getServerSite();
        switch (site) {
            case DevServerSiteConstant.DR2:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                dr2.setChecked(true);
                break;
            case DevServerSiteConstant.DR3:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                dr3.setChecked(true);
                break;
            case DevServerSiteConstant.DR4:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                dr4.setChecked(true);
                break;
            default:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                dr1.setChecked(true);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "============onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapNavi != null) {
            mapNavi.removeMapNaviListener(mapNaviListener);
            mapNavi.destroy();
        }
        Log.d(TAG, "============onDestroy");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        CommonUtil.changeServerSite(checkedId, mapNavi);
    }
}
