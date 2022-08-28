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

package com.hms.explorehms.huawei.feature_hiaifoundation;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.hms.explorehms.huawei.feature_hiaifoundation.adapter.ClassifyAdapter;
import com.hms.explorehms.huawei.feature_hiaifoundation.models.ClassifyItemModel;
import com.hms.explorehms.huawei.feature_hiaifoundation.models.ModelInfo;
import com.hms.explorehms.huawei.feature_hiaifoundation.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import static com.hms.explorehms.huawei.feature_hiaifoundation.utils.Constant.GALLERY_REQUEST_CODE;
import static com.hms.explorehms.huawei.feature_hiaifoundation.utils.Constant.IMAGE_CAPTURE_REQUEST_CODE;


public abstract class NpuClassifyActivity extends AppCompatActivity {
    private static final String NpuClassifyActivityTag = NpuClassifyActivity.class.getSimpleName();
    protected List<ClassifyItemModel> classifyItemModels;

    protected RecyclerView recyclerView;

    protected AssetManager assetManager;

    protected String[] predictedClass =  new String[3];

    protected Bitmap initClassifiedImg;

    protected ClassifyAdapter classifyAdapter;

    protected Button btnGallery;
    protected Button btnCamera;

    protected ModelInfo selectedModel;

    protected ArrayList<ModelInfo> modelList;

    protected Vector<String> wordLabel =  new Vector<>();

    protected float inferenceTime;

    protected float[] outputData;

    protected ArrayList<float[]> outputDataList;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_npu_classify);

        classifyItemModels = new ArrayList<>();

        assetManager = getResources().getAssets();

        initView();

        modelList = (ArrayList<ModelInfo>)getIntent().getSerializableExtra("demoModelList");

        modelList = loadModel(modelList);

        ArrayList<String> modelNames = new ArrayList<>();
        for(ModelInfo modelInfo : modelList) {
            modelNames.add(modelInfo.getOfflineModelName());
        }

        //use default model
        selectedModel = modelList.get(0);
        //overwritten by specified model
        for(ModelInfo model : modelList) {
            if(model.getOfflineModelName().equals(TryActivity.selectedModelName)) {
                selectedModel = model;
                break;
            }
        }

        preProcess();
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

    private void setHeaderView(RecyclerView view) {
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header, view, false);

        btnGallery = header.findViewById(R.id.btn_gallery);
        btnCamera = header.findViewById(R.id.btn_camera);
        classifyAdapter.setHeaderView(header);
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        classifyAdapter = new ClassifyAdapter(classifyItemModels);
        recyclerView.setAdapter(classifyAdapter);

        setHeaderView(recyclerView);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStoragePermission();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    GALLERY_REQUEST_CODE);
        } else {
            //selectedModel may be changed,so reassign.
            for(ModelInfo model : modelList) {
                if(model.getOfflineModelName().equals(TryActivity.selectedModelName)) {
                    selectedModel = model;
                    Toast.makeText(NpuClassifyActivity.this, "Run Model:"+TryActivity.selectedModelName, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            chooseImageAndClassify();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    IMAGE_CAPTURE_REQUEST_CODE);
        } else {
            //selectedModel may be changed,so reassign.
            for(ModelInfo model : modelList) {
                if(model.getOfflineModelName().equals(TryActivity.selectedModelName)) {
                    selectedModel = model;
                    Toast.makeText(NpuClassifyActivity.this, "Run Model:"+TryActivity.selectedModelName, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            takePictureAndClassify();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_REQUEST_CODE &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageAndClassify();
            } else {
                Toast.makeText(NpuClassifyActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureAndClassify();
            } else {
                Toast.makeText(NpuClassifyActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePictureAndClassify() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
        }
    }

    private void chooseImageAndClassify() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                try {
                    Bitmap bitmap;
                    ContentResolver resolver = getContentResolver();
                    Uri originalUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                    cursor.moveToFirst();
                    Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    initClassifiedImg = Bitmap.createScaledBitmap(rgba, selectedModel.getInput_W(), selectedModel.getInput_H(), true);
                    byte[] inputData;
                    if(selectedModel.getUseAIPP()){
                        inputData = Utils.getPixelsAIPP(selectedModel.getFramework(),initClassifiedImg,selectedModel.getInput_W(),selectedModel.getInput_H());
                    }else {
                        inputData = Utils.getPixels(selectedModel.getFramework(),initClassifiedImg,selectedModel.getInput_W(),selectedModel.getInput_H());
                    }
                    ArrayList<byte[]> inputDataList = new ArrayList<>();
                    inputDataList.add(inputData);
                    Log.d(NpuClassifyActivityTag,"inputData.length is :"+inputData.length+"");
                    runModel(selectedModel,inputDataList);
                } catch (IOException e) {
                    Log.e(NpuClassifyActivityTag, e.toString());
                }

                break;
              case IMAGE_CAPTURE_REQUEST_CODE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Bitmap rgba = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    initClassifiedImg = Bitmap.createScaledBitmap(rgba, selectedModel.getInput_W(), selectedModel.getInput_H(), true);
                    byte[] inputData;
                    if(selectedModel.getUseAIPP()){
                        inputData = Utils.getPixelsAIPP(selectedModel.getFramework(),initClassifiedImg,selectedModel.getInput_W(),selectedModel.getInput_H());
                    }else {
                        inputData = Utils.getPixels(selectedModel.getFramework(),initClassifiedImg,selectedModel.getInput_W(),selectedModel.getInput_H());
                    }
                    ArrayList<byte[]> inputDataList = new ArrayList<>();
                    inputDataList.add(inputData);
                    Log.i(NpuClassifyActivityTag,"inputData.length is :"+inputData.length+"");
                    runModel(selectedModel,inputDataList);
             break;

            default:
                break;
        }
        else {
            Toast.makeText(NpuClassifyActivity.this,
                    "Return without selecting pictures|Gallery has no pictures|Return without taking pictures", Toast.LENGTH_SHORT).show();
        }

    }

    protected void preProcess() {
        byte[] labels;
        try {
            Log.i(NpuClassifyActivityTag, "modelList size: " + modelList.size());
            InputStream assetsInputStream = getAssets().open(modelList.get(0).getOnlineModelLabel());
            int available = assetsInputStream.available();
            labels = new byte[available];
            assetsInputStream.read(labels);
            assetsInputStream.close();
            String words = new String(labels);
            String[] contens = words.split("\n");
            Collections.addAll(wordLabel, contens);
            Log.i(NpuClassifyActivityTag, "initLabel size: " + wordLabel.size());
        }catch (Exception e){
            Log.e(NpuClassifyActivityTag,e.getMessage());
        }

    }

    protected void postProcess(float[] outputData){
        if(outputData != null){
            int[] max_index = new int[3];
            double[] max_num = new double[3];
            int maxLength = outputData.length;
            if(maxLength > wordLabel.size()){
                maxLength = wordLabel.size();
            }
            for (int i = 0; i < maxLength; i++) {
                double tmp = outputData[i];
                for (int j = 0; j < 3; j++) {
                    if (tmp > max_num[j]) {
                        max_index[j] = i;
                        tmp += max_num[j];
                        max_num[j] = tmp - max_num[j];
                        tmp -= max_num[j];
                    }
                }
            }

            predictedClass[0] = wordLabel.get(max_index[0]) + " - " + max_num[0] * 100 +"%\n";
            predictedClass[1] = wordLabel.get(max_index[1]) + " - " + max_num[1] * 100 +"%\n"+
                    wordLabel.get(max_index[2]) + " - " + max_num[2] * 100 +"%\n";
            predictedClass[2] ="inference time:" +inferenceTime+ "ms\n";
            for(String res : predictedClass) {
                Log.i(NpuClassifyActivityTag, res);
            }

            classifyItemModels.add(new ClassifyItemModel(predictedClass[0], predictedClass[1], predictedClass[2], initClassifiedImg));
            classifyAdapter.notifyDataSetChanged();

        }else {
            Toast.makeText(NpuClassifyActivity.this,
                    "run model fail.", Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract void runModel(ModelInfo modelInfo, ArrayList<byte[]> inputDataList);

    protected abstract ArrayList<ModelInfo> loadModel(ArrayList<ModelInfo> modelInfo);

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
