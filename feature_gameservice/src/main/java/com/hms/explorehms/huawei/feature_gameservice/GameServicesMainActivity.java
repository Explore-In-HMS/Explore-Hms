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

package com.hms.explorehms.huawei.feature_gameservice;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ActivityGameServicesMainBinding;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

/**
 * This shows how we use Huawei Game Service.
 * Huawei Game Service features are displayed through a game played to the user.
 */
public class GameServicesMainActivity extends AppCompatActivity {

    private ActivityGameServicesMainBinding binding;

    /**
     * The method initializes the sets up necessary for ViewBinding and call setupToolbar.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameServicesMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbarGameservices);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, binding.toolbarGameservices, getString(R.string.url_txt_gameservice));
    }

    /**
     * Called when the user presses the "back" button in the toolbar.
     * It handles the behavior for navigation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        GameConstants.isSecondTime = true;
        return true;
    }

    /**
     * Called when the activity is attaching to its context.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}