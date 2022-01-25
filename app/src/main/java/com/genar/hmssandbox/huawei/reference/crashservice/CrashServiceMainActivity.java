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
package com.genar.hmssandbox.huawei.reference.crashservice;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class CrashServiceMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_service_main);

        setupToolbar();

        //Crash service activated
        setCrashService();

        sendExceptionToUser();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.crash_service_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_for_crash_hyperlink_link));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void sendExceptionToUser() {
        Button btnSendException = findViewById(R.id.btnCrashServiceException);
        btnSendException.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        CustomeAlertAdaptor customeAlertAdaptor = new CustomeAlertAdaptor();
        customeAlertAdaptor.show(getSupportFragmentManager(), "Test");
    }


    private void setCrashService() {
        AGConnectCrash.getInstance().enableCrashCollection(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}