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
package com.genar.hmssandbox.huawei.feature_arengine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_arengine.constant.ServicePermissionsConstant;
import com.genar.hmssandbox.huawei.feature_arengine.ui.body.BodyActivity;
import com.genar.hmssandbox.huawei.feature_arengine.ui.hand.HandActivity;
import com.genar.hmssandbox.huawei.feature_arengine.ui.health.HealthActivity;
import com.genar.hmssandbox.huawei.feature_arengine.ui.world.WorldActivity;
import com.genar.hmssandbox.huawei.feature_arengine.utils.AppUtils;
import com.genar.hmssandbox.huawei.feature_arengine.utils.DialogUtils;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class MainActivityAREngine extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 123;

    private MaterialButton btnWorldTracking;
    private MaterialButton btnFaceTracking;
    private MaterialButton btnHandTracking;
    private MaterialButton btnBodyTracking;
    private MaterialButton btnHealthTracking;

    private Intent currentIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_arengine);

        initUI();
        setupToolbar();
        initListeners();
    }

    private void initUI(){
        btnWorldTracking = findViewById(R.id.btn_ar_world_tracking_arengine);
        btnFaceTracking = findViewById(R.id.btn_ar_face_tracking_arengine);
        btnHandTracking = findViewById(R.id.btn_ar_hand_tracking_arengine);
        btnBodyTracking = findViewById(R.id.btn_ar_body_tracking_arengine);
        btnHealthTracking = findViewById(R.id.btn_ar_image_tracking_arengine);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_main_arengine);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar , getResources().getString(R.string.txt_doc_link_arengine));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initListeners(){

        btnWorldTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntent = new Intent(MainActivityAREngine.this, WorldActivity.class);
                checkPermissions();
            }
        });

        btnFaceTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogUtils.warningDialog(MainActivityAREngine.this);

                /**
                 * Feature Maintanence
                 */

            }
        });

        btnHandTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntent = new Intent(MainActivityAREngine.this, HandActivity.class);
                checkPermissions();
            }
        });

        btnBodyTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntent = new Intent(MainActivityAREngine.this, BodyActivity.class);
                checkPermissions();
            }
        });

        btnHealthTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIntent = new Intent(MainActivityAREngine.this, HealthActivity.class);
                checkPermissions();
            }
        });
    }

    private void checkPermissions(){
        boolean permissionsGranted = true;

        for(String permission : ServicePermissionsConstant.PERMISSIONS){
            if(getApplicationContext().checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                permissionsGranted = false;
                break;
            }
        }

        if(permissionsGranted)
            startActivity(currentIntent);
        else
            requestPermissions(ServicePermissionsConstant.PERMISSIONS, PERMISSION_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PERMISSION_REQUEST && grantResults.length > 0){
            boolean grantResult = true;
            for(int permission : grantResults){
                if(permission == PackageManager.PERMISSION_DENIED){
                    DialogUtils.permissionDeniedDialog(MainActivityAREngine.this,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkPermissions();
                        }
                    });
                    grantResult = false;
                }
            }
            if(grantResult){
               startActivity(currentIntent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}
