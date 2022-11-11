package com.hms.explorehms.huawei.feature_navikit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_navikit.utils.CommonUtil;
import com.hms.explorehms.huawei.feature_navikit.utils.DefaultMapNavi;
import com.hms.explorehms.huawei.feature_navikit.utils.ToastUtil;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.MapNaviListener;
import com.huawei.hms.navi.navibase.enums.VehicleType;
import com.huawei.hms.navi.navibase.model.ClientParas;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;
import com.huawei.hms.navi.navibase.model.bus.BusNaviPathBean;
import com.huawei.hms.navi.navibase.model.busnavirequest.BusCqlRequest;
import com.huawei.hms.navi.navibase.model.busnavirequest.Destination;
import com.huawei.hms.navi.navibase.model.busnavirequest.Origin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BusResultActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "BusResultActivity";

    private Button busPlan;

    private EditText busEnd, busStart, alternatives, returnArray, pedestrianMaxDistance, changes, pedestrianSpeed, keyValue, conversationId;

    private RadioButton dr1, dr2, dr3, dr4;

    private RadioGroup operationEntity;

    private MapNavi mapNavi;

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
        initNavi();
        initView();
        initServerSite();
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
        keyValue = findViewById(R.id.user_apikey_var2);
        conversationId = findViewById(R.id.conversation_id_var2);

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

    private void initServerSite() {
        if (dr1.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
        }
        if (dr2.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR2);
        }
        if (dr3.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR3);
        }
        if (dr4.isChecked()) {
            MapNavi.setDevServerSite(DevServerSiteConstant.DR4);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bus_routing) {
            setImportantParam();
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

    private void setImportantParam() {
        if ("".equals(keyValue.getText().toString())) {
            ToastUtil.showToast(this, "please input apiKey");
            return;
        } else {
            setApiKey(keyValue.getText().toString().trim());
        }

        String clientId = conversationId.getText().toString();
        if (clientId.length() != 32) {
            Log.e(TAG, "conversationId must be 32 bit");
            ToastUtil.showToast(this, "conversationId must be 32 bit.");
            return;
        } else {
            ClientParas clientParas = new ClientParas();
            clientParas.setConversationId(clientId);
            MapNavi.initSettings(clientParas);
        }
    }

    private void setApiKey(String apiKey) {
        if (apiKey == null) {
            return;
        }
        try {
            MapNavi.setApiKey(URLEncoder.encode(apiKey, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to set api Key: " + e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        CommonUtil.changeBusServerSite(checkedId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapNavi != null) {
            mapNavi.removeMapNaviListener(mapNaviListener);
        }
    }
}
