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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.textImageSuperResolution;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.huawei.hiai.vision.image.sr.TxtImageSuperResolution;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.ImageResult;

public class TextImageSuperResolutionActivity extends BaseServiceActivity implements BaseServiceInterface {

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

    public TextImageSuperResolutionActivity() {
        super(ServiceGroupConstants.IMAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_image_super_resolution_hiai);
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
        btnGallery = findViewById(R.id.btn_text_image_super_res_gallery);
        btnCamera = findViewById(R.id.btn_text_image_super_res_camera);
        btnRunService = findViewById(R.id.btn_text_image_super_resolution_run);
        ivImage = findViewById(R.id.iv_text_image_super_resolution_image);
        ivImageResult = findViewById(R.id.iv_image_text_image_super_resolution_result);
        tvResultCode = findViewById(R.id.tv_text_image_super_resolution_result_code);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_text_image_super_res_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.msg_text_image_super_resolution_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnGallery.setOnClickListener(v -> openGallery());

        btnCamera.setOnClickListener(v -> openCamera());

        btnRunService.setOnClickListener(v -> {
            try {
                getResult();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
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
        VisionBase.init(TextImageSuperResolutionActivity.this, new ConnectionCallback() {
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
            if (imageBitmap.getConfig() == Bitmap.Config.ARGB_8888) {

                /**
                 * max bitmap height * width = 1340000 so
                 * we should reduce resolution,
                 * it cause error code 200
                 */
                Bitmap bitmapReduced = reduceBitmapResolution(imageBitmap);

                Frame frame = new Frame();

                frame.setBitmap(bitmapReduced);

                TxtImageSuperResolution txtImageSuperResolution = new TxtImageSuperResolution(getApplicationContext());

                ImageResult imageResult = txtImageSuperResolution.doSuperResolution(frame, null);

                if (imageResult != null) {
                    tvResultCode.setText(String.valueOf(imageResult.getResultCode()));

                    Bitmap bitmapResult = imageResult.getBitmap();

                    if (bitmapResult != null) {
                        ivImageResult.setPadding(0, 0, 0, 0);
                        hasResult = true;
                        Glide.with(this).load(bitmapResult).into(ivImageResult);
                    } else {
                        Toast.makeText(TextImageSuperResolutionActivity.this, "Return bitmap is null", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(TextImageSuperResolutionActivity.this, "Resolution failed, result null", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(TextImageSuperResolutionActivity.this, "Bitmap type must be ARG8888", Toast.LENGTH_LONG).show();
            }
        } else
            showImageAlert();
    }

    private Bitmap reduceBitmapResolution(Bitmap bitmap) {

        int maxPixelSize = 1340000;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        if (height * width > maxPixelSize) {
            height = (height * 9) / 10;
            width = (width * 9) / 10;

            //keep ratio

            return reduceBitmapResolution(Bitmap.createScaledBitmap(bitmap, width, height, false));
        } else {
            return bitmap;
        }
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
