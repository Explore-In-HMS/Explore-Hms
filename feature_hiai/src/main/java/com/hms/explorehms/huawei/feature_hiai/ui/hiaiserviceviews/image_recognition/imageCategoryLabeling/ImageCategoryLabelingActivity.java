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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.imageCategoryLabeling;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.hms.explorehms.huawei.feature_hiai.adapter.list.ImageCategoryLabelingLabelContentsAdapter;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.hms.explorehms.huawei.feature_hiai.utils.ImageUtils;
import com.hms.explorehms.huawei.feature_hiai.utils.hiai_service_utils.ImageRecognitionUtils;
import com.google.android.material.button.MaterialButton;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.image.detector.LabelDetector;
import com.huawei.hiai.vision.visionkit.image.detector.Label;
import com.huawei.hiai.vision.visionkit.image.detector.LabelContent;

import java.util.List;

public class ImageCategoryLabelingActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Bitmap imageBitmap;
    private Uri imageUri;

    private ImageView ivImage;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnRunService;

    private TextView tvCategory;
    private TextView tvCategoryProbability;

    private ListView lvLabelContents;

    public ImageCategoryLabelingActivity() {
        super(ServiceGroupConstants.IMAGE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_category_labeling_hiai);
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
        ivImage = findViewById(R.id.iv_image_category_labeling_image);
        btnGallery = findViewById(R.id.btn_image_category_labeling_gallery);
        btnCamera = findViewById(R.id.btn_image_category_labeling_camera);
        btnRunService = findViewById(R.id.btn_image_category_labeling_run);
        tvCategory = findViewById(R.id.tv_image_category_label_category);
        tvCategoryProbability = findViewById(R.id.tv_image_category_label_category_probability);
        lvLabelContents = findViewById(R.id.lv_image_category_result_label_contents);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_image_category_labeling_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_image_category_labeling_doc_link_hiai));
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
            LabelDetector labelDetector = new LabelDetector(getApplicationContext());

            VisionImage visionImage = VisionImage.fromBitmap(imageBitmap);

            Label label = new Label();

            int resultCode = labelDetector.detect(visionImage, label, null);

            int category = label.getCategory();
            float categoryProbability = label.getCategoryProbability();
            List<LabelContent> labelContents = label.getLabelContent();
            Rect[] rectangles = label.getObjectRects();

            String categoryName = "Others";
            if (category > 0 && ImageRecognitionUtils.IMAGE_CATEGORY_LABELING_CATEGORY.length > category) {
                categoryName = ImageRecognitionUtils.IMAGE_CATEGORY_LABELING_CATEGORY[category];
            }

            tvCategory.setText(categoryName);
            tvCategoryProbability.setText(String.valueOf(categoryProbability));

            ImageCategoryLabelingLabelContentsAdapter adapter =
                    new ImageCategoryLabelingLabelContentsAdapter(
                            ImageCategoryLabelingActivity.this,
                            labelContents
                    );
            lvLabelContents.setVisibility(View.VISIBLE);
            lvLabelContents.setAdapter(adapter);
            lvLabelContents.invalidate();

            drawRectanglesOnImage(rectangles);

            if (resultCode == 0)
                Toast.makeText(getApplicationContext(), "Detection Successful", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        } else
            showImageAlert();

    }

    private void drawRectanglesOnImage(Rect[] rectangles) {
        if (rectangles != null && rectangles.length != 0) {
            Bitmap bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();

            final Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas c = new Canvas(mutableBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            for (Rect rect : rectangles) {
                c.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
            }

            Glide.with(this).load(mutableBitmap).into(ivImage);
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
