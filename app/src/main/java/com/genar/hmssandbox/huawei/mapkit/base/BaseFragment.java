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

package com.genar.hmssandbox.huawei.mapkit.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.mapkit.data.Constants;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.HuaweiMapOptions;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;


public abstract class BaseFragment extends Fragment implements OnMapReadyCallback {

    protected HuaweiMap hMap;
    private MapView mapView;

    private ProgressDialogScreenMapKit dialogScreenMapKit;


    public abstract void initializeUI();


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = getView().findViewById(R.id.mapView_mapkit);
        dialogScreenMapKit = new ProgressDialogScreenMapKit(getContext());
        initHuaweiMap(savedInstanceState);
    }

    private void initHuaweiMap(Bundle savedInstanceState) {
        dialogScreenMapKit.showProgressDialog();
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(Constants.MAP_BUNDLE);
        }
        MapsInitializer.setApiKey(Constants.MAP_KEY);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        try {
            this.hMap = huaweiMap;
            hMap.setMyLocationEnabled(true);
            hMap.getUiSettings().setMyLocationButtonEnabled(true);

            LatLng latLng = new LatLng(41.01900781145951, 28.9559978825787);
            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

            initializeUI();
        } catch (Exception e) {
            Log.d(Constants.TAG, "onMapReady: " + e.getMessage());
        }
        dialogScreenMapKit.dismissProgressDialog();
    }

    public void toast(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    public void showProgressDialog() {
        dialogScreenMapKit.showProgressDialog();
    }

    public void dismissProgressDialog() {
        dialogScreenMapKit.dismissProgressDialog();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
}
