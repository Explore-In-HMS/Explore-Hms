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

package com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.image.vision.crop.CropLayoutView;

public class ImageCroppingActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    /**
     * Target image URI
     */
    private Uri imageUri;

    /**
     * UI Elements
     */
    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnCropRect;
    private MaterialButton btnCropOval;
    private MaterialButton btnRotate;
    private MaterialButton btnMirrorHor;
    private MaterialButton btnMirrorVert;
    private MaterialButton btnCropRatio;
    private MaterialButton btnCrope;
    private MaterialButton cbCropRandom;

    private ImageView ivCroppedImage;

    private EditText etRatioX;
    private EditText etRatioY;

    private View viewCropStyle;

    /**
     * CropLayoutView Element That Process Crop Functions
     */
    private CropLayoutView cropLayoutView;
    private int cropStyle = 0;
    private boolean randomRatio = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_vision_image_cropping);

        initUI();
        initListener();
        initSettings();
        setupToolbar();
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {

        btnGallery = findViewById(R.id.btn_crop_service_image_kit_gallery);
        btnCamera = findViewById(R.id.btn_crop_service_image_kit_camera);
        cropLayoutView = findViewById(R.id.clv_imagekit_crop_layout_crop);
        btnCrope = findViewById(R.id.btn_imagekit_crop_cropimage);
        btnCropRect = findViewById(R.id.btn_image_crop_rect_image_kit);
        btnCropOval = findViewById(R.id.btn_image_crop_oval_image_kit);
        btnRotate = findViewById(R.id.btn_imagekit_crop_rotate);
        btnMirrorHor = findViewById(R.id.btn_imagekit_crop_mirror_hor);
        btnMirrorVert = findViewById(R.id.btn_imagekit_crop_mirror_ver);
        etRatioX = findViewById(R.id.et_imagekit_crop_ratiox);
        etRatioY = findViewById(R.id.et_imagekit_crop_ratioy);
        btnCropRatio = findViewById(R.id.btn_imagekit_crop_crop);
        cbCropRandom = findViewById(R.id.btn_imagekit_crop_crop_random);
        ivCroppedImage = findViewById(R.id.iv_imagekit_crop_croppedimage);
    }

    /**
     * Initialize Toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_crop_service_image_kit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.txt_image_doc_link_image_kit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener() {

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


        btnCropRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCropStyle(btnCropRect);
            }
        });

        btnCropOval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCropStyle(btnCropOval);
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate90Degrees();
            }
        });

        btnMirrorHor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mirrorHorizontaly();
            }
        });

        btnMirrorVert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mirrorVertically();
            }
        });

        btnCropRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAspectRatio();
            }
        });

        cbCropRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomRatio = !randomRatio;
                randomRatio(randomRatio);
            }
        });

        btnCrope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        btnCropRect.performClick();
    }

    /**
     * Initialize CropLayoutView object's Settings
     */
    private void initSettings() {
        Bitmap bitmapHuawei = BitmapFactory.decodeResource(getResources(), R.drawable.huawei_logo_imagekit);
        cropLayoutView.setImageBitmap(bitmapHuawei);
    }

    /**
     * To crop a rectangle-shaped image or an oval-shaped image
     */
    private void changeCropStyle(View view) {

        if (cropStyle == 0) {
            cropLayoutView.setCropShape(CropLayoutView.CropShape.OVAL);
            cropStyle = 1;
        } else {
            cropLayoutView.setCropShape(CropLayoutView.CropShape.RECTANGLE);
            cropStyle = 0;
        }

        if (viewCropStyle != null) {
            ((MaterialButton) viewCropStyle).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            ((MaterialButton) viewCropStyle).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteImageKit));
        }

        ((MaterialButton) view).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteImageKit));
        ((MaterialButton) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        viewCropStyle = view;
    }

    /**
     * To rotate the image by 90 degrees
     */
    private void rotate90Degrees() {
        cropLayoutView.rotateClockwise();
    }

    /**
     * To flip the image for horizontal mirroring
     */
    private void mirrorHorizontaly() {
        cropLayoutView.flipImageHorizontally();
    }

    /**
     * To flip the image for vertical mirroring
     */
    private void mirrorVertically() {
        cropLayoutView.flipImageVertically();
    }

    /**
     * To crop an image with a fixed ratio
     */
    private void changeAspectRatio() {

        try {
            if (etRatioX.getText().toString().equals("") || etRatioY.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_ratio_xy_warning_cropping_image_kit), Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(etRatioX.getText().toString()) <= 0 || Integer.parseInt(etRatioY.getText().toString()) <= 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_ratio_xy_lower_zero_warning_cropping_image_kit), Toast.LENGTH_LONG).show();
            } else {
                cropLayoutView.setAspectRatio(
                        Integer.parseInt(etRatioX.getText().toString()),
                        Integer.parseInt(etRatioY.getText().toString()));
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_ratio_xy_not_valid_warning_cropping_image_kit), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * To crop an image with a random ratio
     */
    private void randomRatio(boolean random) {
        cropLayoutView.setFixedAspectRatio(!random);

        if (random) {
            ((MaterialButton) cbCropRandom).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteImageKit));
            ((MaterialButton) cbCropRandom).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        } else {
            ((MaterialButton) cbCropRandom).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            ((MaterialButton) cbCropRandom).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteImageKit));
        }
    }

    /**
     * Adjust the size of the cropped image, and obtain the bitmap of the cropped image
     */
    private void cropImage() {
        Bitmap croppedImage = cropLayoutView.getCroppedImage();

        ivCroppedImage.setVisibility(View.VISIBLE);
        ivCroppedImage.setImageBitmap(croppedImage);
        ivCroppedImage.requestFocus();
    }

    /**
     * Open Gallery Function, necessary Android permissions should have taken before this process
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    /**
     * Open Camera Function, necessary Android permissions should have taken before this process
     */
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
     * Obtain data from Camera or Gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {
                cropLayoutView.setImageBitmap(ImageUtils.uriToBitmap(getApplicationContext(), imageUri));
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                cropLayoutView.setImageBitmap(ImageUtils.uriToBitmap(getApplicationContext(), imageUri));
            }
        }
    }
}
