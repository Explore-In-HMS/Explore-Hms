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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.aestheticScore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.huawei.hiai.vision.image.detector.AestheticsScoreDetector;
import com.huawei.hiai.vision.visionkit.image.detector.AEModelConfiguration;
import com.huawei.hiai.vision.visionkit.image.detector.AestheticsScore;

@SuppressLint("ClickableViewAccessibility")
public class AestheticsScoreActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private ImageView ivImage;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnOSPMode;
    private MaterialButton btnHFMode;
    private MaterialButton btnAllMode;
    private MaterialButton btnRunAestheticScore;

    private MaterialTextView tvScore;
    private MaterialTextView tvOspScore;
    private MaterialTextView tvHFScore;
    private MaterialTextView tvFrameTimeStamp;

    private ScrollView scrollView;

    private View selectedModeView;
    private int detectImageMode = 3; //All mode default;

    public AestheticsScoreActivity() {
        super(ServiceGroupConstants.IMAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aesthetics_score_hiai);
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
        btnOSPMode = findViewById(R.id.btn_aesthetics_score_mode_1);
        btnHFMode = findViewById(R.id.btn_aesthetics_score_mode_2);
        btnAllMode = findViewById(R.id.btn_aesthetics_score_mode_3);
        btnRunAestheticScore = findViewById(R.id.btn_image_aesthetics_score_run);
        tvScore = findViewById(R.id.tv_aesthetics_score_score);
        tvOspScore = findViewById(R.id.tv_aesthetics_score_osp_score);
        tvHFScore = findViewById(R.id.tv_aesthetics_score_hfs_score);
        tvFrameTimeStamp = findViewById(R.id.tv_aesthetics_score_frame_timestamp);
        ivImage = findViewById(R.id.iv_aesthetics_score);
        btnGallery = findViewById(R.id.btn_aesthetics_score_gallery);
        btnCamera = findViewById(R.id.btn_aesthetics_score_camera);
        scrollView = findViewById(R.id.sv_aesthetics_score);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_aesthetics_score_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_aesthetic_score_doc_link_hiai));
    }


    @Override
    public void initListeners() {

        btnOSPMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImageMode = 1;
                setQualityButtonsColor(btnOSPMode);
            }
        });

        btnHFMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImageMode = 2;
                setQualityButtonsColor(btnHFMode);
            }
        });

        btnAllMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectImageMode = 3;
                setQualityButtonsColor(btnAllMode);
            }
        });

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

        btnRunAestheticScore.setOnClickListener(new View.OnClickListener() {
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

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvOspScore.getParent().requestDisallowInterceptTouchEvent(false);
                tvHFScore.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        tvOspScore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvOspScore.getParent().requestDisallowInterceptTouchEvent(true);
                tvHFScore.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ivImage.setOnClickListener(v -> {
            if (imageBitmap != null) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivImage);
            }
        });

        btnAllMode.performClick();
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

    private void getResult() {

        if (imageBitmap != null) {
            //Define the aestheticsScoreDetector instance, and use the context of this app as the input parameter
            AestheticsScoreDetector aestheticsScoreDetector = new AestheticsScoreDetector(getApplicationContext());

            //Configure engine parameters. If you do not set this parameter, all models are enabled. 1: Enable OSP_MODE; 2: Enable HF_MODE; 3: Enable all.
            AEModelConfiguration aeModelConfiguration = new AEModelConfiguration();
            aeModelConfiguration.getDetectImageConf().setDetectImageMode(detectImageMode);
            aestheticsScoreDetector.setAeModelConfiguration(aeModelConfiguration);

            //Define VisionImage
            VisionImage image = VisionImage.fromBitmap(imageBitmap); // you can set uri, byte etc...

            AestheticsScore outputData = new AestheticsScore();

            aestheticsScoreDetector.detectImage(image, outputData, new VisionCallback<AestheticsScore>() {
                @Override
                public void onResult(AestheticsScore aestheticsScore) {

                    float score = aestheticsScore.getScore();
                    long timeStamp = aestheticsScore.getFrameTimeStamp();
                    float[] ospScores = aestheticsScore.getOSPScores();
                    float[] hfScores = aestheticsScore.getHFSCore();

                    StringBuilder ospScore = new StringBuilder("[ ");
                    StringBuilder hfScore = new StringBuilder("[ ");

                    if (ospScores != null) {
                        for (float scr : ospScores) {
                            ospScore.append(scr);
                            ospScore.append(" ");
                            ospScore.append(",");
                        }
                        ospScore.deleteCharAt(ospScore.length() - 1); // delete last comma
                        ospScore.append(" ]");
                    }

                    if (hfScores != null) {
                        for (float scr : hfScores) {
                            hfScore.append(scr);
                            hfScore.append(" ");
                            hfScore.append(",");
                        }
                        hfScore.deleteCharAt(hfScore.length() - 1); // delete last comma
                        hfScore.append(" ]");
                    }

                    runOnUiThread(() -> {
                        tvScore.setText(String.valueOf(score));
                        tvFrameTimeStamp.setText(String.valueOf(timeStamp));
                        tvOspScore.setText(ospScore.toString());
                        tvHFScore.setText(hfScore.toString());
                    });
                }

                @Override
                public void onError(int i) {
                    Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + i, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProcessing(float v) {
                    Log.i(TAG, "onProcessing");
                }

            });

        } else {
            showImageAlert();
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

    private void clear() {
        tvScore.setText("");
        tvOspScore.setText("");
        tvHFScore.setText("");
        tvFrameTimeStamp.setText("");

        ivImage.setPadding(0, 0, 0, 0);
    }

    private void setQualityButtonsColor(View view) {

        if (selectedModeView != null) {
            selectedModeView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            ((MaterialButton) selectedModeView).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        }


        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        ((MaterialButton) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        selectedModeView = view;
    }
}
