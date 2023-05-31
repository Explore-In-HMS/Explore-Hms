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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.body_recognition.pose_estimation;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.image.detector.PoseEstimationDetector;
import com.huawei.hiai.vision.visionkit.image.detector.BodySkeletons;
import com.huawei.hiai.vision.visionkit.image.detector.PeConfiguration;
import com.huawei.hiai.vision.visionkit.text.config.VisionTableConfiguration;
import com.huawei.hiai.vision.visionkit.text.config.VisionTextConfiguration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PoseEstimationActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final String TAG = "PoseEstimationActivity";

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private ImageView ivImage;
    private ImageView ivImageResult;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnGetResult;

    private MaterialTextView tvValue;

    private boolean hasResult = false;

    public PoseEstimationActivity() {
        super(ServiceGroupConstants.BODY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_estimation_hiai);
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
        btnGallery = findViewById(R.id.btn_pose_estimation_gallery);
        btnCamera = findViewById(R.id.btn_pose_estimation_camera);
        ivImage = findViewById(R.id.iv_pose_estimation_image);
        ivImageResult = findViewById(R.id.iv_pose_estimation_image_result);
        btnGetResult = findViewById(R.id.btn_pose_estimation_run);
        tvValue = findViewById(R.id.tv_pose_estimation_result_text);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_pose_estimation_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_pose_estimation_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnGallery.setOnClickListener(v -> openGallery());

        btnCamera.setOnClickListener(v -> openCamera());

        btnGetResult.setOnClickListener(v -> {
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
        VisionBase.init(PoseEstimationActivity.this, new ConnectionCallback() {
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


        if (imageBitmap != null && imageUri != null) {
            imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
            PoseEstimationDetector poseEstimationDetector = new PoseEstimationDetector(getApplicationContext());

            PeConfiguration config = new PeConfiguration.Builder()
                    .setProcessMode(VisionTextConfiguration.MODE_IN)
                    .setAppType(VisionTableConfiguration.APP_NORMAL)
                    .build();

            poseEstimationDetector.setConfiguration(config);

            VisionImage visionImage = VisionImage.fromBitmap(imageBitmap);

            List<BodySkeletons> result = new ArrayList<>();

            int resultCode = poseEstimationDetector.detect(visionImage, result, null);

            boolean reImage = false;

            if (resultCode == 0) {

                StringBuilder builder = new StringBuilder();

                Bitmap bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(30);

                if (result.size() > 0)
                    reImage = true;

                for (BodySkeletons body : result) {

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String bodyJson = gson.toJson(body);

                    bodyJson = bodyJson.replace(",\n", ",");

                    builder.append(bodyJson);

                    for (Point point : body.getPosition()) {
                        if (point.x != 0 && point.y != 0) {
                            canvas.drawPoint(point.x, point.y, paint);
                        }
                    }
                }

                if (reImage) {
                    tvValue.setText(builder.toString());
                    tvValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackHiAi));
                    ivImageResult.setPadding(0, 0, 0, 0);
                    hasResult = true;
                    Glide.with(this).load(mutableBitmap.copy(Bitmap.Config.ARGB_8888, false)).into(ivImageResult);
                } else {
                    tvValue.setText(getString(R.string.txt_pose_estimation_body_not_found_hiai));
                    tvValue.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                }

            } else {
                Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + resultCode, Toast.LENGTH_SHORT).show();
            }

        } else
            showImageAlert();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
            Picasso.get().load(imageUri).into(ivImage, new Callback() {
                @Override
                public void onSuccess() {
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    Log.i(TAG, "Image Load Successful");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                }
            });

        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(ivImage, new Callback() {
                @Override
                public void onSuccess() {
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    Log.i(TAG, "Image Load Successful");
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                }
            });

        }
    }
}
