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

package com.hms.explorehms.huawei.feature_awarenesskit.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.ActivityAwarenessKitReworkInfoBinding;


public class AwarenessKitReworkInfoActivity extends AppCompatActivity {

    private ActivityAwarenessKitReworkInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAwarenessKitReworkInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUI();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarAwarenessKitInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, (getResources().getString(R.string.awarenesskit_more_information_link_documentation_link)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        binding.tvAdvantagesInfoAwarenesskit.setText(getResources().getString(R.string.advantages_awarenesskit));
        binding.tvCaptureBarrierInfoAwarenesskit.setText(getResources().getString(R.string.capture_barrier_info_awarenesskit));

        binding.btnLetsStartAwarenesskit.setOnClickListener(view ->
                startActivity
                        (new Intent(
                                AwarenessKitReworkInfoActivity.this,
                                AwarenessKitReworkMainActivity.class)
                        )
        );
    }
}