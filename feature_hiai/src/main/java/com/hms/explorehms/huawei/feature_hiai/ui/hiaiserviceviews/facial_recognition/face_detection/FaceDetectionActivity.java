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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.face_detection;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.huawei.hiai.vision.face.FaceDetector;
import com.huawei.hiai.vision.visionkit.common.BoundingBox;
import com.huawei.hiai.vision.visionkit.face.Face;
import com.huawei.hiai.vision.visionkit.face.FaceLandmark;

import java.util.ArrayList;
import java.util.List;


public class FaceDetectionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final String TAG = "FaceDetectionActivity";
    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private ImageView ivImage;

    private MaterialButton btnSelectGallery;
    private MaterialButton btnSelectCamera;
    private MaterialButton btnDetectFace;

    private TextView tvYaw;
    private TextView tvPitch;
    private TextView tvRoll;

    private Bitmap imageBitmap;
    private Uri imageUri;

    public FaceDetectionActivity() {
        super(ServiceGroupConstants.FACIAL);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection_hiai);
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
        ivImage = findViewById(R.id.iv_face_detection_image);
        btnSelectGallery = findViewById(R.id.btn_face_detection_gallery);
        btnSelectCamera = findViewById(R.id.btn_face_detection_camera);
        btnDetectFace = findViewById(R.id.btn_face_detection_run);
        tvYaw = findViewById(R.id.tv_face_detection_yaw);
        tvPitch = findViewById(R.id.tv_face_detection_pitch);
        tvRoll = findViewById(R.id.tv_face_detection_roll);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_face_detection_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_face_detection_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnSelectGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnSelectCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnDetectFace.setOnClickListener(new View.OnClickListener() {
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

    private void getResult() {

        if (imageUri != null) {
            FaceDetector faceDetector = new FaceDetector(FaceDetectionActivity.this);

            VisionImage visionImage = VisionImage.fromBitmap(imageBitmap);

            List<Face> faceResults = new ArrayList<>();

            faceDetector.detect(visionImage, faceResults, new VisionCallback<List<Face>>() {
                @Override
                public void onResult(List<Face> faces) {
                    if (faces != null && faces.size() > 0) {
                        drawOnFaces(faces);
                    } else
                        Toast.makeText(getApplicationContext(), "No Face", Toast.LENGTH_LONG).show();
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
        } else {
            showImageAlert();
        }
    }

    private void drawOnFaces(List<Face> faces) {

        Bitmap tempBmp = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(tempBmp);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

        for (Face face : faces) {

            BoundingBox faceRect = face.getFaceRect();

            paint.setStrokeWidth(20);
            canvas.drawRect(faceRect.getLeft(), faceRect.getTop(), faceRect.getLeft() + faceRect.getWidth(), faceRect.getTop() + faceRect.getHeight(), paint);

            List<FaceLandmark> landmarks = face.getLandmarks();

            paint.setStrokeWidth(60);
            for (FaceLandmark landmark : landmarks) {
                canvas.drawPoint(landmark.getPosition().x, landmark.getPosition().y, paint);
            }

            tvYaw.setText(String.valueOf(face.getYaw()));
            tvPitch.setText(String.valueOf(face.getPitch()));
            tvRoll.setText(String.valueOf(face.getRoll()));
        }

        runOnUiThread(() -> Glide.with(FaceDetectionActivity.this).load(tempBmp).into(ivImage));

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
        tvYaw.setText("");
        tvPitch.setText("");
        tvRoll.setText("");
    }
}
