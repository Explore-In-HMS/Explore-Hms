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

package com.hms.explorehms.mapkit.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentMapKitMapStyleBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.MapStyleOptions;
import com.huawei.hms.maps.model.MyLocationStyle;

/**
 * It handles style of map such as Dark mode and Light mode.
 */
public class MapKitMapStyleFragment extends BaseFragment implements OnMapReadyCallback {

    private FragmentMapKitMapStyleBinding binding;
    private boolean darkMode = false;
    private int isChange = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapKitMapStyleBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initializeUI() {
        binding.btnChangeStyleMapkit.setOnClickListener(view1 -> {
            this.darkMode = !this.darkMode;
            changeStyle();
        });

        binding.btnChangeLocationStyleMapkit.setOnClickListener(view -> {

                    switch (isChange) {
                        case 0:{
                            int resourceId = R.mipmap.fruit_marker_icon;
                            MyLocationStyle style = new MyLocationStyle().anchor(0.5f,0.5f)
                                    .radiusFillColor(Color.RED)
                                            .myLocationIcon(BitmapDescriptorFactory.fromResource(resourceId));
                            setMyLocationStyle(style);
                            isChange = 1;
                            break;
                        }
                        case 1: {
                            MyLocationStyle style = new MyLocationStyle().anchor(0.5f,0.5f)
                                    .radiusFillColor(Color.BLUE);
                            setMyLocationStyle(style);
                            isChange = 0;
                            break;
                        }
                    }
                }
                );

    }
    public void setMyLocationStyle(MyLocationStyle style){
        hMap.setMyLocationStyle(style);
    }

    public MyLocationStyle getMyLocationStyle(){
        return hMap.getMyLocationStyle();
    }
    /**
     * It changes style
     */
    private void changeStyle() {
        /*        hMap.previewId("661426468651539584");
        hMap.setStyleId("661432858472356992");*/
        hMap.setMapStyle(darkMode ? MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_night_hms) :
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_grayscale_hms));
    }
}