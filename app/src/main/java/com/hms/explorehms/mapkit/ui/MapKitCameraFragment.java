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

package com.hms.explorehms.mapkit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.databinding.FragmentMapKitCameraBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.model.LatLng;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class MapKitCameraFragment extends BaseFragment {

    private FragmentMapKitCameraBinding binding;
    private List<LatLng> dummyLocationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapKitCameraBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initializeUI() {
        initDummyLocationList();
        binding.btnMoveCameraMapkit.setOnClickListener(view1 ->
                moveCamera(generateRandomLocation()));
        binding.btnAnimateCameraMapkit.setOnClickListener(view1 ->
                animateCamera(generateRandomLocation()));
    }

    private void moveCamera(LatLng latLng) {
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, hMap.getCameraPosition().zoom));
    }

    private void animateCamera(LatLng latLng) {
        hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, hMap.getCameraPosition().zoom), 1500, null);
    }

    private LatLng generateRandomLocation() {
        int random = new SecureRandom().nextInt(dummyLocationList.size() - 1);
        return dummyLocationList.get(random);
    }

    private void initDummyLocationList() {
        dummyLocationList = new ArrayList<>();
        dummyLocationList.add(new LatLng(40.997688, 28.976738));
        dummyLocationList.add(new LatLng(42.682224, 23.319820));
        dummyLocationList.add(new LatLng(37.974309, 23.729754));
        dummyLocationList.add(new LatLng(41.332250, 19.818851));
        dummyLocationList.add(new LatLng(41.881108, 12.504384));
        dummyLocationList.add(new LatLng(45.453016, 9.180472));
        dummyLocationList.add(new LatLng(47.368767, 8.543918));
        dummyLocationList.add(new LatLng(48.854917, 2.342390));
        dummyLocationList.add(new LatLng(43.3687384026398, -8.401787863310403));
        dummyLocationList.add(new LatLng(50.93837, 4.03853760690213));
        dummyLocationList.add(new LatLng(56.1523296, 10.202848880931285));
        dummyLocationList.add(new LatLng(31.162891014106208, 52.6448316));
        dummyLocationList.add(new LatLng(30.55, 72.116666666667));
        dummyLocationList.add(new LatLng(22.2478, 114.152));
        dummyLocationList.add(new LatLng(18.223043, 42.51591246120995));
        dummyLocationList.add(new LatLng(40.2726479, 44.63145265248378));
        dummyLocationList.add(new LatLng(38.321492073726546, 26.307366263230595));
        dummyLocationList.add(new LatLng(40.5039182, 20.2250341));
        dummyLocationList.add(new LatLng(38.5712604, -7.9084285));
        dummyLocationList.add(new LatLng(39.3612054, -9.1572753));
        dummyLocationList.add(new LatLng(46.4882049, 20.093985));
        dummyLocationList.add(new LatLng(55.70401, 12.56197));
        dummyLocationList.add(new LatLng(43.4816058, -1.5607595554791367));
        dummyLocationList.add(new LatLng(10.9466618, 106.8307976));
        dummyLocationList.add(new LatLng(47.135935, 7.2448435));
        dummyLocationList.add(new LatLng(64.7665836, -21.5));
        dummyLocationList.add(new LatLng(44.8148022, 15.869069460752456));
        dummyLocationList.add(new LatLng(43.9372495, 15.4424644));
        dummyLocationList.add(new LatLng(54.6068851, 24.0317311));
        dummyLocationList.add(new LatLng(35.8256162, 14.5287408));
    }

}