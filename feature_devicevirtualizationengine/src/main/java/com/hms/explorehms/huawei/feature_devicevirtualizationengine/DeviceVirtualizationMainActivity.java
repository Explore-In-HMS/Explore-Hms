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

package com.hms.explorehms.huawei.feature_devicevirtualizationengine;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_devicevirtualizationengine.adapter.DeviceVirtualizationPagerAdapter;
import com.hms.explorehms.huawei.feature_devicevirtualizationengine.databinding.ActivityDeviceVirtualizationMainBinding;
import com.hms.explorehms.huawei.feature_devicevirtualizationengine.fragments.DeviceVirtualizationAppIntegrationFragment;
import com.hms.explorehms.huawei.feature_devicevirtualizationengine.fragments.DeviceVirtualizationDeviceIntegrationFragment;
import com.hms.explorehms.huawei.feature_devicevirtualizationengine.fragments.DeviceVirtualizationOverviewFragment;

import java.util.LinkedHashMap;

public class DeviceVirtualizationMainActivity extends AppCompatActivity {


    private ActivityDeviceVirtualizationMainBinding binding;
    private final LinkedHashMap<Fragment, String> fragmentList = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceVirtualizationMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupPagerAdapter();
    }

    private void setupPagerAdapter() {
        fragmentList.put(new DeviceVirtualizationOverviewFragment(), "Overview");
        fragmentList.put(new DeviceVirtualizationAppIntegrationFragment(), "App Integration");
        fragmentList.put(new DeviceVirtualizationDeviceIntegrationFragment(), "Device Integration");

        binding.tablayoutDevicevirtualization.setupWithViewPager(binding.viewpagerDevicevirtualization);

        DeviceVirtualizationPagerAdapter adapter =
                new DeviceVirtualizationPagerAdapter(
                        getSupportFragmentManager(),
                        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                        fragmentList
                );
        binding.viewpagerDevicevirtualization.setAdapter(adapter);
    }


    private void setupToolbar() {
        setSupportActionBar(binding.toolbarDevicevirtualization);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, binding.toolbarDevicevirtualization, getString(R.string.devicevirtualization_url));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}