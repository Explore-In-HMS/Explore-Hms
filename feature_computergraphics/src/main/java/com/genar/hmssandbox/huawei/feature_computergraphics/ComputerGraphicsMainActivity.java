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

package com.genar.hmssandbox.huawei.feature_computergraphics;

import android.app.NativeActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_computergraphics.databinding.ActivityComputerGraphicsMainBinding;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class ComputerGraphicsMainActivity extends AppCompatActivity {

    private ActivityComputerGraphicsMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComputerGraphicsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarComputergraphics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, binding.toolbarComputergraphics, getString(R.string.computer_graphics_more_information_link));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        setupToolbar();

        binding.tvUseCasesInfoTextComputergraphics.setText(
                getResources().getString(R.string.computer_graphics_use_cases)
        );

        binding.tvRestrictionsInfoTextComputergraphics.setText(
                getResources().getString(R.string.computer_graphics_restrictions)
        );

        binding.btnLetsTryComputergraphics.setOnClickListener(view -> {
            Intent intent = new Intent(ComputerGraphicsMainActivity.this, NativeActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}