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

package com.hms.explorehms.huawei.feature_nearbyservice;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_nearbyservice.connection.ChatActivity;
import com.hms.explorehms.huawei.feature_nearbyservice.nearby_message.NearbyMessageActivity;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class NearbyServiceActivity extends AppCompatActivity {

    CardView btnConnection;
    CardView btnMessage;
    CardView btnNearby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_service);
        setupToolbar();
        btnMessage = findViewById(R.id.cv_nearbymessage);
        btnMessage.setOnClickListener(v -> Util.startActivity(NearbyServiceActivity.this, NearbyMessageActivity.class));
        btnConnection = findViewById(R.id.cv_nearbyconnection);
        btnConnection.setOnClickListener(v -> Util.startActivity(NearbyServiceActivity.this, ChatActivity.class));
        btnConnection = findViewById(R.id.cv_nearwifi);
        btnConnection.setOnClickListener(v -> Util.startActivity(NearbyServiceActivity.this, WifiShareActivity.class));

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_nearbyservice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_nearbyservice));
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