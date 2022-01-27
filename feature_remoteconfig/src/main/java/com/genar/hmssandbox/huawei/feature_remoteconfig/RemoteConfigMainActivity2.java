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

package com.genar.hmssandbox.huawei.feature_remoteconfig;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.feature_remoteconfig.adaptor.RemoteConfigAdaptor;
import com.genar.hmssandbox.huawei.feature_remoteconfig.model.RemoteResult;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.agconnect.remoteconfig.ConfigValues;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RemoteConfigMainActivity2 extends AppCompatActivity {

    private static final String REMOTE_ERR ="REMOTE_ERR";
    private AGConnectConfig remoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_config_main2);

        setupToolbar();
        setButtonConfiguration();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.scrollBarRemoteConfExample);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setButtonConfiguration(){
        Button btnGetResultFromAGCRemoteConfig = findViewById(R.id.btnGetDataFromRemoteConfiguration);
        btnGetResultFromAGCRemoteConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRemoteConfigurationSettings();

            }
        });
    }

    private void setRemoteConfigurationSettings(){
        remoteConfig = AGConnectConfig.getInstance();
        remoteConfig.applyDefault(R.xml.remote_config);


        //Interval time can be changed.default values is 12 hours
        remoteConfig.fetch(0).addOnSuccessListener(new OnSuccessListener<ConfigValues>() {
            @Override
            public void onSuccess(ConfigValues configValues) {
                remoteConfig.apply(configValues);
                obtainData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(REMOTE_ERR,e.toString());
            }
        });
    }

    private void obtainData(){

        Map<String,Object>allValues = remoteConfig.getMergedAll();
        if(allValues != null && allValues.size() > 0){
            ArrayList<RemoteResult> allResultFromRemote = new ArrayList<>();

            Set set = allValues.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry)iterator.next();
                System.out.println(entry.getKey()+" "+entry.getValue());
                allResultFromRemote.add(new RemoteResult(entry.getKey().toString(),entry.getValue().toString()));
            }

            fillRecyclerView(allResultFromRemote);
        }else{
            Toast.makeText(getApplicationContext(),"There is no value on Cloud",Toast.LENGTH_SHORT).show();
        }
    }

    private void fillRecyclerView(ArrayList<RemoteResult> allValues){
        RecyclerView recyclerViewRemoteResult = findViewById(R.id.recyclerVwRemoteResult);
        recyclerViewRemoteResult.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RemoteConfigAdaptor adaptor = new RemoteConfigAdaptor(allValues);
        recyclerViewRemoteResult.setAdapter(adaptor);
    }


}
