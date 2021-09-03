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
package com.genar.hmssandbox.huawei.feature_appmessaging;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.agconnect.appmessaging.AGConnectAppMessaging;
import com.huawei.hms.ads.HwAds;

public class AppMessagingTestActivity extends AppCompatActivity {

    private AGConnectAppMessaging agConnectAppMessaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_messaging_test);

        //Set Ads kit initialization
        HwAds.init(getApplicationContext());

        setupToolbar();
        setPopUpMessage();
        setButtonClicks();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.appMessagingTestToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setPopUpMessage(){
        agConnectAppMessaging = AGConnectAppMessaging.getInstance();
    }


    private void setButtonClicks(){
        Button btnPopUpMessage = findViewById(R.id.btnShowPopupMessage);
        btnPopUpMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agConnectAppMessaging.trigger("PopupMessageCustomeEvent");
            }
        });

        Button btnBannerMessage = findViewById(R.id.btnShowBannerMessage);
        btnBannerMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agConnectAppMessaging.trigger("BannerMessageCustomeEvent");
            }
        });

        Button btnImageMessage = findViewById(R.id.btnShowImageMessage);
        btnImageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agConnectAppMessaging.trigger("ImageMessageExample");
            }
        });
    }
}