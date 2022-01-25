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
import com.genar.hmssandbox.huawei.databinding.FragmentMapKitMapStyleBinding;
import com.genar.hmssandbox.huawei.mapkit.base.BaseFragment;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.MapStyleOptions;


public class MapKitMapStyleFragment extends BaseFragment implements OnMapReadyCallback {

    private FragmentMapKitMapStyleBinding binding;
    private boolean darkMode = false;

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
    }

    private void changeStyle() {
        /*        hMap.previewId("661426468651539584");
        hMap.setStyleId("661432858472356992");*/
        hMap.setMapStyle(darkMode ? MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_night_hms) :
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle_grayscale_hms));
    }
}