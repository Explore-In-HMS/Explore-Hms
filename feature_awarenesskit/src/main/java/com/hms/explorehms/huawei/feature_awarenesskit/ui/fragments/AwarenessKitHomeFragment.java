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

package com.hms.explorehms.huawei.feature_awarenesskit.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentAwarenessKitHomeBinding;

public class AwarenessKitHomeFragment extends Fragment {


    private FragmentAwarenessKitHomeBinding binding;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAwarenessKitHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Awareness Kit Services");
        NavController navController = Navigation.findNavController(view);

        binding.btnTimeAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoTimeAwareness));

        binding.btnLocationAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoLocationAwareness));

        binding.btnBehaviorAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoBehaviorAwareness));

        binding.btnBeaconAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoBeaconAwareness));

        binding.btnAudioDeviceAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoAudioDeviceAwareness));

        binding.btnWeatherAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoWeatherAwareness));

        binding.btnAmbientLightAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoLightAwareness));

        binding.btnPhoneStatusAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoPhoneStatusAwareness));

        binding.btnDataDonationAwarenessAwarenesskit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoDataDonationAwareness));
    }
}