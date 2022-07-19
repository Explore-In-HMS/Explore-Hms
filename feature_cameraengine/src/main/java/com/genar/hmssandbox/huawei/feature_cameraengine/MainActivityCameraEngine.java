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

package com.genar.hmssandbox.huawei.feature_cameraengine;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_cameraengine.adapter.MainViewPagerAdapterCameraEngine;
import com.genar.hmssandbox.huawei.feature_cameraengine.util.DialogUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.camera.camerakit.Mode;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class MainActivityCameraEngine extends AppCompatActivity {


    private final int PERMISSION_REQUEST = 99;
    private final String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_screen_camera_engine);

        initUI();
        setupToolbar();
        checkEnginePermissions();
    }

    private void initUI() {
        tabLayout = findViewById(R.id.tl_main_camera_engine);
        viewPager = findViewById(R.id.vp_main_camera_engine);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main_camera_engine);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.txt_doc_link_camera_engine));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void checkEnginePermissions() {
        boolean allOK = true;
        for (String permission : permissions) {
            if (getApplicationContext().checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                allOK = false;
                requestPermissions(permissions, PERMISSION_REQUEST);
                break;
            }
        }

        if (allOK)
            initAdapter();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
            boolean allOK = true;
            for (int permission : grantResults) {
                if (permission == PackageManager.PERMISSION_DENIED) {
                    allOK = false;
                    break;
                }
            }
            if (allOK)
                initAdapter();
            else {
                DialogUtils.permissionDeniedDialog(MainActivityCameraEngine.this, (dialog, which) -> {
                    dialog.dismiss();
                    checkEnginePermissions();
                }, (dialog, which) -> finish());
            }
        }
    }

    private void initAdapter() {
        viewPager.setAdapter(new MainViewPagerAdapterCameraEngine(MainActivityCameraEngine.this.getSupportFragmentManager(), getLifecycle()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {
            if (position == 0) {
                tab.setText("Photo Mode");
            } else if (position == 1) {
                tab.setText("HDR Mode");
            } else if (position == 2) {
                tab.setText("Night Mode");
            } else if (position == 3) {
                tab.setText("Ultra-wide Angle Mode");
            } else if (position == 4) {
                tab.setText("Portrait Mode");
            } else if (position == 5) {
                tab.setText("Super Slow-mo Recording");
            } else if (position == 6) {
                tab.setText("Pro Mode (Photo)");
            } else if (position == 7) {
                tab.setText("Pro Mode (Video)");
            } else if (position == 8) {
                tab.setText("Recording Mode");
            }
            else if(position==9){
                tab.setText("Dual-View Video");
            }
            /*else if(position == -1){ //maintenance
                tab.setText("Slow-mo Recording");
            }else if(position == -1){ //maintenance
                tab.setText("Dual-View Video");
            }*/
        });

        tabLayoutMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
