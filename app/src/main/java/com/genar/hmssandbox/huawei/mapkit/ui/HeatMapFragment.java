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

package com.genar.hmssandbox.huawei.mapkit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.databinding.FragmentHeatMapBinding;
import com.genar.hmssandbox.huawei.mapkit.base.BaseFragment;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.GroundOverlayOptions;
import com.huawei.hms.maps.model.HeatMap;
import com.huawei.hms.maps.model.HeatMapOptions;
import com.huawei.hms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class HeatMapFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return FragmentHeatMapBinding.inflate(getLayoutInflater()).getRoot();
    }


    @Override
    public void initializeUI() {
        HeatMapOptions heatMapOptions = new HeatMapOptions();
        heatMapOptions.intensity(2f);
// Set the heatmap dataset.
        heatMapOptions.dataSet(R.raw.population_density);
// Add the heatmap to a map.
        HeatMap heatMap = hMap.addHeatMap("id", heatMapOptions);
        Map<Float, Integer> map = new HashMap<>();
        map.put(1.0f, 158777);
        heatMap.setColor(map);
// Modify the heatmap intensity.
        heatMap.setIntensity(10f);
// Modify the heatmap transparency.
        heatMap.setOpacity(0.5f);
// Modify the heatmap radius.
        heatMap.setRadius(10f);

    }



}