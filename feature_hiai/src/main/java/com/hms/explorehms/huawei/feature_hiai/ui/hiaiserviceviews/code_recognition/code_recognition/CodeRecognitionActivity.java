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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.code_recognition.code_recognition;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.hms.explorehms.huawei.feature_hiai.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hiai.vision.barcode.BarcodeDetector;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionCallback;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.visionkit.barcode.Barcode;
import com.huawei.hiai.vision.visionkit.barcode.ZxingBarcodeConfiguration;
import com.huawei.hiai.vision.visionkit.text.config.VisionTextConfiguration;

import java.util.List;

public class CodeRecognitionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final String TAG = "CodeRecognitionActivity";
    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private ImageView ivImage;

    private Uri imageUri;
    private Bitmap bitmapOfImage;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnRunService;

    private MaterialTextView tvBarcodeResult;

    private StringBuilder barcodeStringBuilder = new StringBuilder();
    private int codeCount = 1;

    public CodeRecognitionActivity() {
        super(ServiceGroupConstants.CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_recognition_hiai);
        baseContext = this;

        initUI();
        setupToolbar();
        initListeners();
        try {
            initService();
        } catch (Exception e) {
            Log.e(TAG, "Initialization Error : " + e.toString());
        }
    }

    @Override
    public void initUI() {
        btnGallery = findViewById(R.id.btn_code_recognition_gallery);
        btnCamera = findViewById(R.id.btn_code_recognition_camera);
        ivImage = findViewById(R.id.iv_code_recognition_image);
        tvBarcodeResult = findViewById(R.id.tv_barcode_result_json);
        btnRunService = findViewById(R.id.btn_code_recognition_run);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_code_recognition_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_code_recognation_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnRunService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getResult();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivImage.setOnClickListener(v -> {
            if (imageUri != null) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivImage);
            }
        });
    }

    @Override
    public void initService() {
        VisionBase.init(getApplicationContext(), new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i(TAG, "onServiceConnect");
                Toast.makeText(getApplicationContext(), "Service Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnect() {
                Log.i(TAG, "onServiceDisconnect");
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    public void getResult() {

        if (bitmapOfImage != null) {
            BarcodeDetector mBarcodeDetector = new BarcodeDetector(getApplicationContext());//Construct Detector.

            VisionImage image = VisionImage.fromBitmap(bitmapOfImage);

            ZxingBarcodeConfiguration config = new ZxingBarcodeConfiguration.Builder()
                    .setProcessMode(VisionTextConfiguration.MODE_IN)
                    .build();
            mBarcodeDetector.setConfiguration(config);

            mBarcodeDetector.detect(image, null, new VisionCallback<List<Barcode>>() {
                @Override
                public void onResult(List<Barcode> barcodeList) {
                    if (barcodeList != null) {
                        for (Barcode barcode : barcodeList) {
                            getBarcodeInfo(barcode);
                        }

                        String barcodeString = barcodeStringBuilder.toString();
                        runOnUiThread(() -> {
                            tvBarcodeResult.setVisibility(View.VISIBLE);
                            tvBarcodeResult.setText(barcodeString);
                        });
                    }
                }

                @Override
                public void onError(int i) {
                    Log.e(TAG, "Detection Failed, ResultCode " + i);
                    Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + i, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProcessing(float v) {
                    Log.i(TAG, "onProcessing");
                }
            });
        } else
            showImageAlert();

    }

    public void getBarcodeInfo(Barcode barcode) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String barcodeJson = gson.toJson(barcode);

        barcodeStringBuilder.append("\nRESULT : "); //Start Info
        barcodeStringBuilder.append(codeCount);
        barcodeStringBuilder.append("\n\n");

        barcodeStringBuilder.append(barcodeJson);
        barcodeStringBuilder.append("\n");

        codeCount++;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
            Glide.with(this).load(imageUri).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    bitmapOfImage = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    clear();
                    Log.i(TAG, "Image Load Successful");
                    return false;
                }
            }).into(ivImage);

        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            Glide.with(this).load(imageUri).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    bitmapOfImage = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    clear();
                    Log.i(TAG, "Image Load Successful");
                    return false;
                }
            }).into(ivImage);

        }
    }

    public void clear() {
        codeCount = 1;
        barcodeStringBuilder = new StringBuilder();
        tvBarcodeResult.setText("");

        ivImage.setPadding(0, 0, 0, 0);
    }
}
