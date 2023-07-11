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

package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.idCardRecognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;


public class IDCardActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_id_card);
        findViewById(R.id.btn_icr_cn).setOnClickListener(this);
        findViewById(R.id.btn_icr_vn).setOnClickListener(this);
        setupToolbar();
        MLApplication.getInstance().setApiKey(AGConnectServicesConfig.fromContext(this).getString("client/api_key"));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_icr_cn){
            startActivity(new Intent(IDCardActivity.this, IDCardRecognitionActivity.class));
        }else if(view.getId() == R.id.btn_icr_vn){
            startActivity(new Intent(IDCardActivity.this, VNIdCardRecognitionActivity.class));
        }else if(view.getId() == R.id.back){
            finish();
        }
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
