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

package com.hms.explorehms.huawei.feature_devicevirtualizationengine.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_devicevirtualizationengine.R;
import com.hms.explorehms.huawei.feature_devicevirtualizationengine.databinding.FragmentDeviceVirtualizationDeviceIntegrationBinding;


public class DeviceVirtualizationDeviceIntegrationFragment extends Fragment {

    private FragmentDeviceVirtualizationDeviceIntegrationBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentDeviceVirtualizationDeviceIntegrationBinding.inflate(inflater, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvWhatDataCollectInfoAppintegration.setText(
                requireContext().getResources().getString(R.string.text_what_data_collect_info_deviceintegration)
        );

        binding.tvHowDataProtectInfoDeviceintegration.setText(
                requireContext().getResources().getString(R.string.text_how_data_protect_info_deviceintegration)
        );

    }

}