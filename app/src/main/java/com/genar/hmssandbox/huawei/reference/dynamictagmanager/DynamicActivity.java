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

package com.genar.hmssandbox.huawei.reference.dynamictagmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class DynamicActivity extends AppCompatActivity {

    HiAnalyticsInstance instance;
    Button button;

    /*
     * Initiate HUAWEI Analytics Kit using the onCreate method of the first activity to obtain a
     * HiAnalyticsInstance instance after the configuration is imported successfully. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic);
        setupToolbar();
        // get HiAnalyticsInstance instance
        instance = HiAnalytics.getInstance(this);
        HiAnalyticsTools.enableLog();

        button = findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_dynamic_tag);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_txt_dtm));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /*Insert visual event code snippets in proper positions of the code.*/
    private void sendEvent() {
        /* developers can report custom event
         event name is "Purchase"*/
        String eventName = "Purchase";

        /* three fields in Bundle*/
        Bundle bundle = new Bundle();
        bundle.putDouble("price", 999);
        bundle.putLong("quantity", 100L);
        bundle.putString("currency", "CNY");

        // report
        if (instance != null) {
            instance.onEvent(eventName, bundle);
            Log.d("DTM-Test", "log event.");
            Toast.makeText(this, "Data sent succesfully!", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}