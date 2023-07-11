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
package com.hms.explorehms.huawei.feature_appmessaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

/**
 * This activity allows the user to see how to App Messaging with Huawei Messaging.
 * If user click button "Open Test Page" on this Activity, user can go directly to testing App Messaging features.
 */
public class AppMessagingMainActivity extends AppCompatActivity {

    /**
     * The method initializes the sets up necessary for UI and toolbar.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_messaging_main);

        setupToolbar();
        setButtonSetEvent();
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.appMessagingToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_for_app_messaging_hyperlink_link));
    }

    /**
     * Called when the user presses the "back" button in the toolbar.
     * It handles the behavior for navigation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * It handles btnChangeActivity click event to navigate
     */
    private void setButtonSetEvent() {
        Button btnChangeActivity = findViewById(R.id.btnChangeActivityToAppMessageTypes);
        btnChangeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActivityChangeAction();
            }
        });
    }

    /**
     * Starts the AppMessagingTestActivity so that the user can test the app messaging features.
     */
    private void setActivityChangeAction() {
        Intent testActivityIntent = new Intent(getApplicationContext(), AppMessagingTestActivity.class);
        startActivity(testActivityIntent);
    }

    /**
     * Called when the activity is attaching to its context.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}