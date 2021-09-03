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

package com.genar.hmssandbox.huawei.feature_localauthenticationengine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_localauthenticationengine.databinding.ActivityLocalAuthenticationMainBinding;

import java.util.Objects;

public class LocalAuthenticationMainActivity extends AppCompatActivity {

    private ActivityLocalAuthenticationMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocalAuthenticationMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setTitle("Local Authentication Engine");

        initUI();
    }

    private void initUI() {
        binding.tvKeyFeaturesInfoLocalauthentication.setText(
                getResources().getString(R.string.text_key_features_info_localauthentication)
        );
        binding.tvRequirementsInfoLocalauthentication.setText(
                getResources().getString(R.string.text_development_environment_info_localauthentication)
        );
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarLocalauthentication);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, binding.toolbarLocalauthentication, getResources().getString(R.string.localauthentication_url));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}