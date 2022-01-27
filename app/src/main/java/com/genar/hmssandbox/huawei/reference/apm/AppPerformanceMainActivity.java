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

package com.genar.hmssandbox.huawei.reference.apm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class AppPerformanceMainActivity extends AppCompatActivity {

    private Intent activityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_performance_main);
        setupToolbar();

        activityIntent = new Intent(Intent.ACTION_VIEW);

        setCodeLabLink();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.apmToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.text_for_apm_link_for_web_site));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setCodeLabLink() {
        Button btnApmCodeLab = findViewById(R.id.btnApmCodelab);
        btnApmCodeLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIntentContent(getResources().getString(R.string.text_for_apm_link_for_codelab));
            }
        });
    }


    private void setIntentContent(String link) {
        if (!link.isEmpty()) {
            activityIntent.setData(Uri.parse(link));
            startActivity(activityIntent);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}