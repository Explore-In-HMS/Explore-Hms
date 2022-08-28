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

package com.hms.explorehms.huawei.feature_shareengine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class ShareEngineMainActivity extends AppCompatActivity {


    private static final String shareEngineAndroidDeviceLink = "https://developer.huawei.com/consumer/en/doc/development/connectivity-Guides/share-preparation-android";
    private static final String shareEngineLinuxDeviceLink = "https://developer.huawei.com/consumer/en/doc/development/connectivity-Guides/share-preparation-Linux";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_engine_main);
        setupToolbar();

        MaterialButton btnBetweenHuaweiPhone = findViewById(R.id.btn_between_huawei);
        MaterialButton btnAndroidDevices = findViewById(R.id.button_android_devices);
        MaterialButton btnLinuxDevices = findViewById(R.id.button_linux_devices);

        btnBetweenHuaweiPhone.setOnClickListener(view -> {
            Intent huaweiPhonesIntent = new Intent(ShareEngineMainActivity.this, ShareEngineHuaweiPhonesActivity.class);
            startActivity(huaweiPhonesIntent);
            finish();
        });

        Intent browserIntent = new Intent(Intent.ACTION_VIEW);

        btnAndroidDevices.setOnClickListener(view -> {
            browserIntent.setData(Uri.parse(shareEngineAndroidDeviceLink));
            startActivity(browserIntent);
        });

        btnLinuxDevices.setOnClickListener(view -> {
            browserIntent.setData(Uri.parse(shareEngineLinuxDeviceLink));
            startActivity(browserIntent);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_share_engine);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_shareengine));
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