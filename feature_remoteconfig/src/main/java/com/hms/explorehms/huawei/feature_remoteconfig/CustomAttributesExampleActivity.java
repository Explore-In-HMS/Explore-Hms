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

package com.hms.explorehms.huawei.feature_remoteconfig;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_remoteconfig.model.RemoteResult;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.agconnect.remoteconfig.ConfigValues;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CustomAttributesExampleActivity extends AppCompatActivity {

    private static final String REMOTE_ERR = "REMOTE_ERR";
    private AGConnectConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_attributes_example);

        setupToolbar();
        setButtonConfiguration();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.scrollBarRemoteConfExampleCustom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setButtonConfiguration() {
        Button btnGetResultFromAGCRemoteConfig = findViewById(R.id.btnStartCustomAttributes);
        btnGetResultFromAGCRemoteConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRemoteConfigurationSettings();

            }
        });
    }

    private void setRemoteConfigurationSettings() {

        EditText etKeyText = findViewById(R.id.et_key_text);
        EditText etValueText = findViewById(R.id.et_value_text);

        Map<String, String> map = new HashMap<String, String>();
        map.put(etKeyText.getText().toString(), etValueText.getText().toString());

        remoteConfig = AGConnectConfig.getInstance();
        remoteConfig.getCustomAttributes();
        remoteConfig.setCustomAttributes(map);


        //Interval time can be changed.default values is 12 hours
        remoteConfig.fetch(0).addOnSuccessListener(new OnSuccessListener<ConfigValues>() {
            @Override
            public void onSuccess(ConfigValues configValues) {
                remoteConfig.apply(configValues);
                obtainData();
                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(REMOTE_ERR, e.toString());
            }
        });
    }

    private void obtainData() {

        Map<String, Object> allValues = remoteConfig.getMergedAll();
        if (allValues != null && allValues.size() > 0) {
            ArrayList<RemoteResult> allResultFromRemote = new ArrayList<>();

            Set set = allValues.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                System.out.println(entry.getKey() + " " + entry.getValue());
                allResultFromRemote.add(new RemoteResult(entry.getKey().toString(), entry.getValue().toString()));
            }
        } else {
            Toast.makeText(getApplicationContext(), "There is no value on Cloud", Toast.LENGTH_SHORT).show();
        }
    }

}
