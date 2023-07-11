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

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.agconnect.appmessaging.AGConnectAppMessaging;
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingCallback;
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingOnClickListener;
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingOnDismissListener;
import com.huawei.agconnect.appmessaging.AGConnectAppMessagingOnDisplayListener;
import com.huawei.agconnect.appmessaging.model.Action;
import com.huawei.agconnect.appmessaging.model.AppMessage;

/**
 * This activity allows the user to test App Messaging features, such as Pop-up, Banner and Image messages.
 */
public class AppMessagingTestActivity extends AppCompatActivity {

    private AGConnectAppMessaging agConnectAppMessaging;

    /**
     * The method initializes the sets up necessary for UI and toolbar.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_messaging_test);


        setupToolbar();
        setPopUpMessage();
        setButtonClicks();
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.appMessagingTestToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
     * It initializes agConnectAppMessaging by AGConnectAppMessaging for using apis of App Messaging
     */
    private void setPopUpMessage() {
        agConnectAppMessaging = AGConnectAppMessaging.getInstance();
    }

    /**
     * It handle button clicks to get events
     */
    private void setButtonClicks() {
        Button btnPopUpMessage = findViewById(R.id.btnShowPopupMessage);
        btnPopUpMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agConnectAppMessaging.setForceFetch("PopupMessageCustomeEvent");
            }
        });

        Button btnBannerMessage = findViewById(R.id.btnShowBannerMessage);
        btnBannerMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agConnectAppMessaging.setForceFetch("BannerMessageCustomeEvent");
            }
        });

        Button btnImageMessage = findViewById(R.id.btnShowImageMessage);
        btnImageMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agConnectAppMessaging.setForceFetch("ImageMessageExample");
            }
        });
        popUpListener();
    }

    /**
     * It shows toast message according to message events such as showed, clicked or dismissed.
     */
    public void popUpListener() {
        agConnectAppMessaging.addOnDisplayListener(new AGConnectAppMessagingOnDisplayListener() {
            @Override
            public void onMessageDisplay(AppMessage appMessage) {
                Toast.makeText(AppMessagingTestActivity.this, "Message showed", Toast.LENGTH_LONG).show();
            }
        });
        agConnectAppMessaging.addOnClickListener(new AGConnectAppMessagingOnClickListener() {
            @Override
            public void onMessageClick(@NonNull AppMessage appMessage, @NonNull Action action) {
                Toast.makeText(AppMessagingTestActivity.this, "Message Clicked", Toast.LENGTH_LONG).show();

            }
        });
        agConnectAppMessaging.addOnDismissListener(new AGConnectAppMessagingOnDismissListener() {
            @Override
            public void onMessageDismiss(@NonNull AppMessage appMessage, @NonNull AGConnectAppMessagingCallback.DismissType dismissType) {
                Toast.makeText(AppMessagingTestActivity.this, "Message Dismiss, dismiss type: " + dismissType, Toast.LENGTH_LONG).show();

            }

        });
    }
}