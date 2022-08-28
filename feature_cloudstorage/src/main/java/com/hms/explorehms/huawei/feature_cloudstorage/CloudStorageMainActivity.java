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

package com.hms.explorehms.huawei.feature_cloudstorage;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_cloudstorage.databinding.ActivityCloudStorageMainBinding;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class CloudStorageMainActivity extends AppCompatActivity {

    private ActivityCloudStorageMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCloudStorageMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarCloudstorage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, binding.toolbarCloudstorage, getResources().getString(R.string.more_information__link_cloudstorage));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}