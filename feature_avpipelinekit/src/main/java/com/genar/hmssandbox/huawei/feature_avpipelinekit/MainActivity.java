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

package com.genar.hmssandbox.huawei.feature_avpipelinekit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.genar.hmssandbox.huawei.feature_avpipelinekit.ui.AssetActivity;
import com.genar.hmssandbox.huawei.feature_avpipelinekit.ui.PlayerActivityBase;
import com.genar.hmssandbox.huawei.feature_avpipelinekit.ui.PlayerActivitySRdisabled;
import com.genar.hmssandbox.huawei.feature_avpipelinekit.ui.PlayerActivitySRenabled;
import com.genar.hmssandbox.huawei.feature_avpipelinekit.ui.PlayerActivitySound;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AVP-MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolBar = findViewById(R.id.tb_main_avpipeline);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        handlePermission();
        initAllView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void handlePermission() {
        String[] permissionLists = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        int requestPermissionCode = 1;
        for (String permission : permissionLists) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissionLists, requestPermissionCode);
            }
        }
    }

    void initAllView() {
        Button btn1 = findViewById(R.id.asset);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AssetActivity.class);
                startActivity(intent);
            }
        });
        Button btn2 = findViewById(R.id.playerbase);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivityBase.class);
                startActivity(intent);
            }
        });
        Button btn3 = findViewById(R.id.playerSRdisabled);
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivitySRdisabled.class);
                startActivity(intent);
            }
        });
        Button btn4 = findViewById(R.id.playerSRenabled);
        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivitySRenabled.class);
                startActivity(intent);
            }
        });
        Button btn5 = findViewById(R.id.playerSD);
        btn5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivitySound.class);
                startActivity(intent);
            }
        });
    }
}
