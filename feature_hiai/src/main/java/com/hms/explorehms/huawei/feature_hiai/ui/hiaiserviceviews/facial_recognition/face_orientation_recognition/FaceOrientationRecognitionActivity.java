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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.face_orientation_recognition;

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
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.face.HeadposeDetector;
import com.huawei.hiai.vision.visionkit.face.HeadPoseConfiguration;
import com.huawei.hiai.vision.visionkit.face.HeadPoseResult;
import com.huawei.hiai.vision.visionkit.text.config.VisionTextConfiguration;

public class FaceOrientationRecognitionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final String TAG = "FaceOrientationRecognitionActivity";
    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private MaterialButton ivFromGallery;
    private MaterialButton ivFromCamera;
    private MaterialButton btnGetOrientation;

    private TextView tvOrientationResult;
    private TextView tvOrientationConfidence;

    private ImageView ivImage;

    private Uri imageUri;
    private Bitmap imageBitmap;

    public FaceOrientationRecognitionActivity() {
        super(ServiceGroupConstants.FACIAL);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_orientation_recognition_hiai);
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
        ivImage = findViewById(R.id.iv_face_orientation_recognition_image);
        ivFromGallery = findViewById(R.id.btn_face_orientation_gallery);
        ivFromCamera = findViewById(R.id.btn_face_orientation_camera);
        tvOrientationResult = findViewById(R.id.tv_face_orientation_result);
        tvOrientationConfidence = findViewById(R.id.tv_face_orientation_confidence);
        btnGetOrientation = findViewById(R.id.btn_face_orientation_run);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_face_orientation_recognition_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_face_orientation_recog_doc_link_hiai));
    }

    @Override
    public void initListeners() {


        ivFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        ivFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnGetOrientation.setOnClickListener(new View.OnClickListener() {
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

    /**
     * Requirement on the image size: height/width = 3/4. Otherwise, error code 200 is returned
     */
    private void getResult() {

        if (imageBitmap != null) {

            int width = imageBitmap.getWidth();

            int height = (width * 3) / 4;

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);

            VisionImage visionImage = VisionImage.fromBitmap(scaledBitmap);

            HeadPoseConfiguration configuration = new HeadPoseConfiguration.Builder()
                    .setProcessMode(VisionTextConfiguration.MODE_IN)
                    .build();

            HeadposeDetector headposeDetector = new HeadposeDetector(getApplicationContext());

            headposeDetector.setConfiguration(configuration);

            HeadPoseResult headPoseResult = new HeadPoseResult();

            int resultCode = headposeDetector.detect(visionImage, headPoseResult, null); //

            if (resultCode == 0) {
                int orientationResult = headPoseResult.getHeadpose();

                if (orientationResult == 0) {
                    tvOrientationResult.setText("NO FACE");  // 0 indicates that there is no one,
                } else if (orientationResult == 1) {
                    tvOrientationResult.setText("UPWARD");   // 1 indicates that the face faces upward,
                } else if (orientationResult == 2) {
                    tvOrientationResult.setText("RIGHT");    // 2 indicates that the face faces the right,
                } else if (orientationResult == 3) {
                    tvOrientationResult.setText("DOWNWARD"); // 3 indicates that the face faces downward
                } else if (orientationResult == 4) {
                    tvOrientationResult.setText("LEFT");     // 4 indicates that the face faces the left.
                }

                float confidence = headPoseResult.getConfidence();
                tvOrientationConfidence.setText(String.valueOf(confidence));
            } else {
                Toast.makeText(FaceOrientationRecognitionActivity.this, "Detection failed. Result code : " + resultCode, Toast.LENGTH_LONG).show();
            }
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

    public void clear() {
        tvOrientationResult.setText("");
        tvOrientationConfidence.setText("");
    }

}
