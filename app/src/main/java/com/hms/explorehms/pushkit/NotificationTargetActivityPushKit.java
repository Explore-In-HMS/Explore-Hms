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

package com.hms.explorehms.pushkit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.R;

public class NotificationTargetActivityPushKit extends AppCompatActivity {

    /**
     * to back to main activity
     */
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_route_pushkit);

        initUI();
        initListener();
    }

    /**
     * Initialize UI Elements
     */
    private void initUI(){
        btnBack = findViewById(R.id.btn_back_push_route_act);
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener(){
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationTargetActivityPushKit.this, MainActivityPushKit.class);
            startActivity(intent);
        });
    }
}
