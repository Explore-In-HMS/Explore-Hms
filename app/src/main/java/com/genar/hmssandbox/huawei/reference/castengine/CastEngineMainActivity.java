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

package com.genar.hmssandbox.huawei.reference.castengine;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.databinding.ActivityCastEngineMainBinding;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class CastEngineMainActivity extends AppCompatActivity {

    private ActivityCastEngineMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCastEngineMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        initUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_castengine);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.castengine_link_documentation_link));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        String scenarios = getResources().getString(R.string.scenarios_info_castengine);
        binding.tvScenariosInfoTextCastengine.setText(scenarios);

        String advantages = getResources().getString(R.string.advantages_info_text);
        binding.tvAdvantagesInfoTextCastengine.setText(advantages);

        String precautions = getResources().getString(R.string.precautions_info_text);
        binding.tvPrecautionsTextCastengine.setText(precautions);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}