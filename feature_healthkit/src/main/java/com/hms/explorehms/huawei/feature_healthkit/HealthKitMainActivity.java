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

package com.hms.explorehms.huawei.feature_healthkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_healthkit.auth.HealthKitAuthClientActivity;
import com.hms.explorehms.huawei.feature_healthkit.auth.HealthKitAuthCloudActivity;


/**
 * functional description
 *
 * @since 2020-03-19
 */
public class HealthKitMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_kit_main);
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_health_kit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this , toolbar , getString(R.string.url_txt_healthkit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Data Controller
     *
     * @param view UI object
     */
    public void hihealthDataControllerOnclick(View view) {
        Intent intent = new Intent(this, com.hms.explorehms.huawei.feature_healthkit.HealthKitDataControllerActivity.class);
        startActivity(intent);
    }

    /**
     * Setting Controller
     *
     * @param view UI object
     */
    public void hihealthSettingControllerOnclick(View view) {
        Intent intent = new Intent(this, HealthKitSettingControllerActivity.class);
        startActivity(intent);
    }

    /**
     * Auto Recorder
     *
     * @param view UI object
     */
    public void hihealthAutoRecorderOnClick(View view) {
        Intent intent = new Intent(this, com.hms.explorehms.huawei.feature_healthkit.HealthKitAutoRecorderControllerActivity.class);
        startActivity(intent);
    }

    /**
     * Activity Records Controller
     *
     * @param view UI object
     */
    public void hihealthActivityRecordOnClick(View view) {
        Intent intent = new Intent(this, HealthKitActivityRecordControllerActivity.class);
        startActivity(intent);
    }

    /**
     * signing In and applying for Scopes
     *
     * @param view UI object
     */
    public void onLoginClick(View view) {
        Intent intent = new Intent(this, HealthKitAuthClientActivity.class);
        startActivity(intent);
    }

    /**
     * Huawei ID signing In and authorization through cloud interfaces.
     *
     * @param view UI object
     */
    public void onCloudLoginClick(View view) {
        Intent intent = new Intent(com.hms.explorehms.huawei.feature_healthkit.HealthKitMainActivity.this, HealthKitAuthCloudActivity.class);
        startActivity(intent);
    }
}
