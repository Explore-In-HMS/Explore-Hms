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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.documentSkewCorrection;

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
import android.widget.TextView;
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
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionCallback;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.image.docrefine.DocRefine;
import com.huawei.hiai.vision.visionkit.image.ImageResult;
import com.huawei.hiai.vision.visionkit.image.detector.DocCoordinates;


public class DocumentSkewCorrectionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnRunService;

    private ImageView ivImage;
    private ImageView ivImageResult;

    private TextView tvResultCode;

    private boolean hasResult = false;

    public DocumentSkewCorrectionActivity() {
        super(ServiceGroupConstants.IMAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_skew_correction_hiai);
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
        btnGallery = findViewById(R.id.btn_document_skew_correction_gallery);
        btnCamera = findViewById(R.id.btn_document_skew_correction_camera);
        btnRunService = findViewById(R.id.btn_document_skew_correction_run);
        ivImage = findViewById(R.id.iv_document_skew_correction_image);
        ivImageResult = findViewById(R.id.iv_image_document_skew_correction_result);
        tvResultCode = findViewById(R.id.tv_document_skew_correction_result_code);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_document_skew_correction_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_document_skew_correction_doc_link_hiai));
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
            if (imageBitmap != null) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivImage);
            }
        });

        ivImageResult.setOnClickListener(v -> {
            if (hasResult) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivImageResult);
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

        if (imageBitmap != null) {
            DocRefine docResolution = new DocRefine(getApplicationContext());

            VisionImage visionImage = VisionImage.fromBitmap(imageBitmap);

            DocCoordinates docCoordinates = new DocCoordinates();

            int result = docResolution.docDetect(visionImage, docCoordinates, null);

            if (result == 101) {
                Toast.makeText(getApplicationContext(), "Make sure that image contain document!", Toast.LENGTH_LONG).show();
            }

            tvResultCode.setText(String.valueOf(result));

            ImageResult imageResult = new ImageResult();

            docResolution.docRefine(visionImage, docCoordinates, imageResult, new VisionCallback<ImageResult>() {
                @Override
                public void onResult(ImageResult imageResult) {

                    if (imageResult != null && imageResult.getBitmap() != null) {

                        runOnUiThread(() -> {
                            Bitmap bitmap = imageResult.getBitmap();
                            ivImageResult.setPadding(0, 0, 0, 0);
                            hasResult = true;
                            Glide.with(DocumentSkewCorrectionActivity.this).load(bitmap).into(ivImageResult);
                        });
                    } else if (imageResult != null) {
                        Toast.makeText(getApplicationContext(), "Refine Failed : ResultCode " + imageResult.getResultCode(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Refine Failed! ", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(int i) {
                    Log.i(TAG, "onError");
                }

                @Override
                public void onProcessing(float v) {
                    Log.i(TAG, "onProcessing");
                }
            });
        } else
            showImageAlert();
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
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
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
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    Log.i(TAG, "Image Load Successful");
                    return false;
                }
            }).into(ivImage);
        }
    }
}
