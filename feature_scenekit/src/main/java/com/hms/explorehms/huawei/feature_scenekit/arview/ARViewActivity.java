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

package com.hms.explorehms.huawei.feature_scenekit.arview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_scenekit.R;
import com.huawei.hms.scene.sdk.ARView;


public class ARViewActivity extends AppCompatActivity {
    private ARView viewARView;
    private Button buttonARView;
    private SwitchCompat switchARView;
    private boolean isLoadResource = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_arview);

        setupToolbar();
        initUI();
        initListener();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_ar_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {
        viewARView = findViewById(R.id.view_arview);
        buttonARView = findViewById(R.id.btn_arview);

        switchARView = findViewById(R.id.swt_arview);
        switchARView.setChecked(true);
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener() {

        switchARView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewARView.enablePlaneDisplay(isChecked);
            }
        });

        buttonARView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!isLoadResource) {
                        viewARView.loadAsset("ARView/scene.gltf");
                        isLoadResource = true;
                        buttonARView.setText(getResources().getString(R.string.txt_clear));
                    } else {
                        viewARView.clearResource();
                        viewARView.loadAsset("");
                        isLoadResource = false;
                        buttonARView.setText(getResources().getString(R.string.txt_load));
                    }
                } catch (Exception e) {
                    Log.e("SCENEKIT", e.toString());
                }
            }
        });
    }

    /**
     * Pause ARView Service
     */
    @Override
    protected void onPause() {
        super.onPause();
        viewARView.onPause();
    }

    /**
     * Resume ARView Service
     */
    @Override
    protected void onResume() {
        super.onResume();
        viewARView.onResume();
    }

    /**
     * Destroy ARView Service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewARView.destroy();
    }
}
