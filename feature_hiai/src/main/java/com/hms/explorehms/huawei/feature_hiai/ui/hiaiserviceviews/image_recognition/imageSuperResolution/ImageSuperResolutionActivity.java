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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.imageSuperResolution;

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
import androidx.core.content.ContextCompat;

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
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionCallback;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.image.sr.ImageSuperResolution;
import com.huawei.hiai.vision.visionkit.common.VisionConfiguration;
import com.huawei.hiai.vision.visionkit.image.ImageResult;
import com.huawei.hiai.vision.visionkit.image.sr.SISRConfiguration;

public class ImageSuperResolutionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private ImageView ivImage;
    private ImageView ivImageResult;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnProcessModeOut;
    private MaterialButton btnProcessModeIn;
    private MaterialButton btnQualityLow;
    private MaterialButton btnQualityMedium;
    private MaterialButton btnQualityHigh;
    private MaterialButton btnDoSuperResolution;

    private MaterialTextView tvResultCode;

    private View viewMode;
    private View viewQuality;

    private int processMode = VisionConfiguration.MODE_OUT;
    private int quality = SISRConfiguration.SISR_QUALITY_MEDIUM;

    private boolean hasResult = false;

    public ImageSuperResolutionActivity() {
        super(ServiceGroupConstants.IMAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_super_resolution_hiai);
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
        ivImage = findViewById(R.id.iv_image_image_super_resolution);
        btnGallery = findViewById(R.id.btn_image_super_res_gallery);
        btnCamera = findViewById(R.id.btn_image_super_res_camera);
        btnProcessModeOut = findViewById(R.id.btn_image_super_res_process_mode_out);
        btnProcessModeIn = findViewById(R.id.btn_image_super_res_process_mode_in);
        btnQualityLow = findViewById(R.id.btn_image_super_res_quality_low);
        btnQualityMedium = findViewById(R.id.btn_image_super_res_quality_medium);
        btnQualityHigh = findViewById(R.id.btn_image_super_res_quality_high);
        btnDoSuperResolution = findViewById(R.id.btn_image_super_res_do_resolution);
        tvResultCode = findViewById(R.id.tv_image_super_resolution_result_code);
        ivImageResult = findViewById(R.id.iv_image_image_super_resolution_result);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_image_super_res_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_image_super_resolution_doc_link_hiai));
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

        btnProcessModeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processMode = VisionConfiguration.MODE_OUT;
                setModeButtonsColor(btnProcessModeOut);
            }
        });

        btnProcessModeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processMode = VisionConfiguration.MODE_IN;
                setModeButtonsColor(btnProcessModeIn);
            }
        });

        btnQualityLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quality = SISRConfiguration.SISR_QUALITY_LOW;
                setQualityButtonsColor(btnQualityLow);
            }
        });

        btnQualityMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quality = SISRConfiguration.SISR_QUALITY_MEDIUM;
                setQualityButtonsColor(btnQualityMedium);
            }
        });

        btnQualityHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quality = SISRConfiguration.SISR_QUALITY_HIGH;
                setQualityButtonsColor(btnQualityHigh);
            }
        });

        btnDoSuperResolution.setOnClickListener(new View.OnClickListener() {
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

        btnProcessModeOut.performClick();
        btnQualityMedium.performClick();
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

        if (imageBitmap == null) {
            showImageAlert();
        } else {

            int height = imageBitmap.getHeight();
            int width = imageBitmap.getWidth();

            if (height > width) {
                if (height > 800)
                    height = 800;
                if (width > 600)
                    width = 600;
            } else {
                if (height > 600)
                    height = 600;
                if (width > 800)
                    width = 800;
            }

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);

            VisionImage image = VisionImage.fromBitmap(scaledBitmap);
            ImageSuperResolution superResolution = new ImageSuperResolution(getApplicationContext());

            SISRConfiguration paras = new SISRConfiguration
                    .Builder()
                    .setProcessMode(processMode)
                    .build();

            paras.setQuality(quality);

            superResolution.setSuperResolutionConfiguration(paras);

            ImageResult imageResult = new ImageResult();
            ivImageResult.setPadding(0, 0, 0, 0);

            int resultCode = superResolution.doSuperResolution(image, imageResult, new VisionCallback<ImageResult>() {
                @Override
                public void onResult(ImageResult imageResult) {

                    if (imageResult != null) {
                        int resultCode = imageResult.getResultCode();

                        if (resultCode != 0) {
                            Toast.makeText(getApplicationContext(), "Failed to run super-resolution, return :" + imageResult, Toast.LENGTH_LONG).show();
                        } else {
                            Bitmap bitmapResult = imageResult.getBitmap();

                            if (bitmapResult != null) {
                                runOnUiThread(() -> {
                                    hasResult = true;
                                    Glide.with(ImageSuperResolutionActivity.this).load(bitmapResult).into(ivImageResult);
                                    tvResultCode.setText(String.valueOf(resultCode));
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Result bitmap is null!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Result is null!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(int i) {
                    Log.e(TAG, "Failed to run super-resolution, Error Code : " + i);
                }

                @Override
                public void onProcessing(float v) {
                }
            });

            if (resultCode == 700) {
                Log.d(TAG, "Wait for result.");
                tvResultCode.setText(R.string.txt_image_super_resolution_result_code);
            } else if (resultCode != 0) {
                Log.e(TAG, "Failed to run super-resolution, return : " + resultCode);
                tvResultCode.setText(String.valueOf(resultCode));
            }
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
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    clear();
                    Log.i(TAG, "Image Load Successful");
                    return false;
                }
            }).into(ivImage);
        }
    }

    public void clear() {
        if (imageBitmap != null)
            ivImageResult.setImageBitmap(null);
        tvResultCode.setText("");
    }

    private void setQualityButtonsColor(View view) {

        if (viewQuality != null) {
            viewQuality.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            ((MaterialButton) viewQuality).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        }


        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        ((MaterialButton) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        viewQuality = view;
    }

    private void setModeButtonsColor(View view) {

        if (viewMode != null) {
            viewMode.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            ((MaterialButton) viewMode).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        }

        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        ((MaterialButton) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        viewMode = view;
    }

}
