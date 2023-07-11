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

package com.hms.explorehms.huawei.feature_videoeditorkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class ServiceIntroductionActivity extends AppCompatActivity {

    MaterialButton btn_try;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_introduction);
        btn_try=findViewById(R.id.btn_try_video_editor);
        Intent intent=new Intent(this,MainActivity.class);
        Toolbar toolBar = findViewById(R.id.tb_main_video_editor);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btn_try.setOnClickListener(view -> {
            if(getNetworkType(getApplicationContext()).equals("Mobile")||getNetworkType(getApplicationContext()).equals("")){
                Toast.makeText(getApplicationContext(),"Please open Wi-fi for using Video Editor Kit",Toast.LENGTH_SHORT).show();
                Intent intentSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intentSettings);
            }
            else if (getNetworkType(getApplicationContext()).equals("WiFi")){
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String getNetworkType(Context context){
        String networkType = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "WiFi";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkType = "Mobile";
            }
        } else {
            networkType="";
            Toast.makeText(this,"Please Open Wifi for using video editor kit",Toast.LENGTH_SHORT).show();
        }
        return networkType;
    }

}