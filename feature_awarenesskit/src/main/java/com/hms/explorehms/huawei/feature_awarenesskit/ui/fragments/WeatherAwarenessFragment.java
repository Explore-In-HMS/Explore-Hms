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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentWeatherAwarenessBinding;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.status.WeatherStatus;
import com.huawei.hms.kit.awareness.status.weather.Situation;
import com.huawei.hms.kit.awareness.status.weather.WeatherSituation;


public class WeatherAwarenessFragment extends Fragment {

    private final static String weatherAwarenessTag = "WeatherAwareness";

    private FragmentWeatherAwarenessBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWeatherAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Weather Awareness");

        binding.tvWeatherAwarenessInfo.setText(getActivity().getResources().getString(R.string.weather_awareness_info));

        checkPermission();

        initUI();
    }

    private void initUI() {
        binding.progressBarWeatherawareness.setVisibility(View.INVISIBLE);
        binding.btnGetWeatherWeatherawareness.setOnClickListener(view -> {
            clearUI();
            binding.progressBarWeatherawareness.setVisibility(View.VISIBLE);
            getWeather();
        });
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    private void getWeather() {

        Awareness.getCaptureClient(requireActivity()).getWeatherByDevice()
                .addOnSuccessListener(weatherStatusResponse -> {
                    binding.progressBarWeatherawareness.setVisibility(View.INVISIBLE);
                    WeatherStatus weatherStatus = weatherStatusResponse.getWeatherStatus();
                    WeatherSituation weatherSituation = weatherStatus.getWeatherSituation();
                    Situation situation = weatherSituation.getSituation();
                    if (situation != null) {
                        binding.tvCityTextWeatherawareness.setText(weatherSituation.getCity().getName());
                        binding.tvTemperatureTextWeatherawareness.setText(situation.getTemperatureC() + "℃" +
                                ", " + situation.getTemperatureF() + "℉");

                        binding.tvWindSpeedWeatherawareness.setText(situation.getWindSpeed() + "km/h");
                        binding.tvWindDirectionWeatherawarareness.setText(situation.getWindDir());
                        binding.tvHumidityWeatherawareness.setText(situation.getHumidity());


                    } else {
                        Log.d(weatherAwarenessTag, "failed");
                        clearUI();
                        Toast.makeText(requireContext(), "Weather info returned empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBarWeatherawareness.setVisibility(View.INVISIBLE);
                    clearUI();
                    Toast.makeText(requireContext(), "Please allow location permission from Hms Core", Toast.LENGTH_SHORT).show();
                    Log.e(weatherAwarenessTag, "get weather failed" + e.getMessage() + ", ");
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + "com.huawei.hwid")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    requireActivity().finish();
                });

    }

    private void clearUI() {
        binding.tvCityTextWeatherawareness.setText("");
        binding.tvTemperatureTextWeatherawareness.setText("");
        binding.tvWindSpeedWeatherawareness.setText("");
        binding.tvWindDirectionWeatherawarareness.setText("");
        binding.tvHumidityWeatherawareness.setText("");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permission, 101);
        }
    }
}