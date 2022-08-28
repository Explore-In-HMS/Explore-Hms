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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.facial_comparison;

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
import com.huawei.hiai.vision.face.FaceComparator;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.FaceCompareResult;

import org.json.JSONObject;

public class FacialComparisionActivity extends BaseServiceActivity implements BaseServiceInterface {


    private static final String TAG = "FacialComparisionActivity";
    private final static int GALLERY_REQUEST = 99;
    private final static int CAMERA_REQUEST = 100;

    private Uri imageUri;

    private MaterialButton ivOpenGallery;
    private MaterialButton ivOpenCamera;
    private MaterialButton btnCompare;

    private ImageView imageFirst;
    private ImageView imageSecond;

    private MaterialTextView tvCompareScore;
    private MaterialTextView tvIsSamePerson;

    private Bitmap bitmap1;
    private Bitmap bitmap2;

    private int whichPhoto = 1;

    public FacialComparisionActivity() {
        super(ServiceGroupConstants.FACIAL);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facial_comparison_hiai);
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
        ivOpenGallery = findViewById(R.id.btn_facial_comparison_gallery);
        ivOpenCamera = findViewById(R.id.btn_facial_comparison_camera);
        imageFirst = findViewById(R.id.iv_facial_comp_image_1);
        imageSecond = findViewById(R.id.iv_facial_comp_image_2);
        btnCompare = findViewById(R.id.btn_facial_comparison_run);
        tvCompareScore = findViewById(R.id.tv_facial_comparison_score);
        tvIsSamePerson = findViewById(R.id.tv_facial_comparison_is_same_person);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_facial_comparison_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_facial_comp_doc_link_hiai));
    }

    @Override
    public void initListeners() {


        ivOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoFromGallery();
            }
        });

        ivOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        btnCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap1 != null && bitmap2 != null) {
                    try {
                        getResult();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Select image from gallery or take picture", Toast.LENGTH_LONG).show();
            }
        });

        imageFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichPhoto = 1;
                switchImageFocus();
            }
        });

        imageSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichPhoto = 2;
                switchImageFocus();
            }
        });

        imageFirst.setOnLongClickListener(v -> {
            if (bitmap1 != null)
                Util.showDialogImagePeekView(this, getApplicationContext(), imageFirst);
            return false;
        });

        imageSecond.setOnLongClickListener(v -> {
            if (bitmap2 != null)
                Util.showDialogImagePeekView(this, getApplicationContext(), imageSecond);
            return false;
        });
    }

    private void switchImageFocus() {
        if (whichPhoto == 1) {
            imageFirst.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_border_primary_hiai));
            imageSecond.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_border_black_white_back_hiai));
            imageFirst.setPadding(10, 0, 10, 0);
        } else if (whichPhoto == 2) {
            imageFirst.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_border_black_white_back_hiai));
            imageSecond.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.view_border_primary_hiai));
            imageSecond.setPadding(10, 0, 10, 0);
        }
    }

    private void selectPhotoFromGallery() {
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

    @Override
    public void initService() {

        /**
         * Perform initialization by using the VisionBase static class to asynchronously obtain a connection to the service.
         */
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


    private void getResult() {

        /**
         * Define the facial comparison instance, and use the context of this project as the input parameters.
         */
        FaceComparator faceComparator = new FaceComparator(getApplicationContext());

        /**
         * Define the frames.
         */
        Frame frame1 = new Frame();
        Frame frame2 = new Frame();

        /**
         * Place the bitmaps of to-be-compared images into the frame.
         */
        frame1.setBitmap(bitmap1);
        frame2.setBitmap(bitmap2);

        JSONObject jsonObject = faceComparator.faceCompare(frame1, frame2, null);
        FaceCompareResult result = faceComparator.convertResult(jsonObject);

        tvCompareScore.setText(String.valueOf(result != null ? result.getSocre() : "Result Is Empty. Failed"));
        tvIsSamePerson.setText(String.valueOf(result != null ? result.isSamePerson() : "Result Is Empty. Failed"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView ivToLoad = null;

        if (whichPhoto == 1) {
            ivToLoad = imageFirst;
        } else if (whichPhoto == 2) {
            ivToLoad = imageSecond;
        }

        if (ivToLoad != null) {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
                if (whichPhoto == 1) {
                    bitmap1 = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    whichPhoto = 2;
                } else {
                    bitmap2 = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    whichPhoto = 1;
                }

                Glide.with(this).load(imageUri).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.i(TAG, "Image Load Successful");
                        return false;
                    }
                }).into(ivToLoad);

            } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

                imageUri = data.getData();

                if (imageUri != null) {

                    if (whichPhoto == 1) {
                        bitmap1 = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    } else {
                        bitmap2 = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    }

                    Glide.with(this).load(imageUri).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.i(TAG, "Image Load Successful");
                            return false;
                        }
                    }).into(ivToLoad);
                }

            }
        }
    }


}
