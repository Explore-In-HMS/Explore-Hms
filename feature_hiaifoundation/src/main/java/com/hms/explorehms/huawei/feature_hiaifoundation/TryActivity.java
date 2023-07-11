/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hms.explorehms.huawei.feature_hiaifoundation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_hiaifoundation.models.ModelInfo;
import com.hms.explorehms.huawei.feature_hiaifoundation.utils.ModelManager;
import com.hms.explorehms.huawei.feature_hiaifoundation.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class TryActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String tryActivityTag = TryActivity.class.getSimpleName();
    protected ArrayList<ModelInfo> demoModelList = new ArrayList<>();
    protected RecyclerView recyclerView;
    protected boolean useNpu = false;
    protected boolean interfaceCompatible = true;
    protected Button btnSync = null;
    protected Button btnAsync = null;
    protected Spinner spinnerModel;
    protected LinearLayoutManager layoutManager = null;
    static String selectedModelName;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initModels();
        copyModels();
        modelCompatibilityProcess();
        initSpinner();
        setupToolbar();
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

    private void initSpinner() {
        spinnerModel = findViewById(R.id.spinner2);
        ArrayList<String> modelNames = new ArrayList<>();
        for(ModelInfo modelInfo : demoModelList) {
            modelNames.add(modelInfo.getOfflineModelName());
        }

        ArrayAdapter<String> adapterModel = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modelNames);

        spinnerModel.setAdapter(adapterModel);
        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                selectedModelName = parent.getItemAtPosition(pos).toString();

                Toast.makeText(TryActivity.this, "Model selected:"+selectedModelName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }

        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        btnSync = findViewById(R.id.btn_sync);
        btnAsync = findViewById(R.id.btn_async);
        btnSync.setOnClickListener(this);
        btnAsync.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sync:
                if(interfaceCompatible) {
                    if (useNpu) {
                        Intent intent = new Intent(TryActivity.this, SyncClassifyActivity.class);
                        intent.putExtra("demoModelList", demoModelList);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Model incompatibility or NO online Compiler interface or Compile model failed, Please run it on CPU", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Interface incompatibility, Please run it on CPU", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_async:
                if(interfaceCompatible) {
                    if (useNpu) {
                        Intent intent = new Intent(TryActivity.this, AsyncClassifyActivity.class);
                        intent.putExtra("demoModelList", demoModelList);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Model incompatibility or NO online Compiler interface or Compile model failed, Please run it on CPU", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Interface incompatibility, Please run it on CPU", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    private void copyModels(){
        AssetManager am = getAssets();
        for (ModelInfo model : demoModelList) {
            if(!Utils.isExistModelsInAppModels(model.getOfflineModel(),model.getModelSaveDir())){
                Utils.copyModelsFromAssetToAppModels(am, model.getOfflineModel(),model.getModelSaveDir());
            }
        }
    }

    private void modelCompatibilityProcess(){
        //load hiaijni.so
        boolean isSoLoadSuccess = ModelManager.loadJNISo();

        if (isSoLoadSuccess) {
            Toast.makeText(this, "load libhiai.so success.", Toast.LENGTH_SHORT).show();

            interfaceCompatible = true;

            Iterator<ModelInfo> iter = demoModelList.iterator();
            while (iter.hasNext()){
                ModelInfo model = iter.next();
                boolean isCompatible = ModelManager.modelCompatibilityProcessFromFile(model.getModelSaveDir() + model.getOfflineModel());
                //incompatible models are removed.
                if(!isCompatible){
                    Toast.makeText(this, "Model "+model.getOfflineModel()+" is not compatible!", Toast.LENGTH_SHORT).show();
                    iter.remove();
                    continue;
                }
                //true if there is any compatible model.
                useNpu |= isCompatible;
            }//while
            Log.d(tryActivityTag, "useNPU : " + useNpu);
        } else {
            interfaceCompatible = false;
            Toast.makeText(this, "load libhiai.so fail.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void initModels(){
        File dir =  getDir("models", Context.MODE_PRIVATE);
        String path = dir.getAbsolutePath() + File.separator;
        ModelInfo model_2 = new ModelInfo();
        model_2.setModelSaveDir(path);
        model_2.setUseAIPP(false);
        model_2.setOfflineModel("hiai_noaipp.om");
        model_2.setOfflineModelName("hiai_noaipp");
        model_2.setOnlineModelLabel("labels_caffe.txt");
        demoModelList.add(model_2);
    }

}
