package com.hms.explorehms.locationkit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

    //Global values
    int reqCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_location);

        initView();
        createFusedLocationProviderClient();
        createLocationInformationRequest();

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_get_indoor_location,
            R.id.btn_start_indoor_location_update,
            R.id.btn_stop_indoor_location_update})
    public void onItemClick(View v){
        updateLogResults(getString(R.string.results_will_be_here));
        switch (v.getId()){
            case R.id.btn_get_indoor_location:
                    createLocationInformationRequest();
                break;
            case R.id.btn_start_indoor_location_update:
                    requestLocationUpdateEx();
                break;
            case R.id.btn_stop_indoor_location_update:
                    removeLocationUpdateEx();
                break;
            default:
                break;
        }
    }

    private void updateLogResults(String msg){
        tv_resultLogs.setText(msg);
    }

    private void initView(){
        //TextViews
        tv_resultLogs = findViewById(R.id.resultLogsIndoor);
        tv_floor = findViewById(R.id.tv_floor_indoor);
        tv_floor_acc = findViewById(R.id.tv_floor_acc_indoor);
        tv_time = findViewById(R.id.tv_time_indoor);
        tv_requestCounts = findViewById(R.id.tv_request_count_indoor);

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