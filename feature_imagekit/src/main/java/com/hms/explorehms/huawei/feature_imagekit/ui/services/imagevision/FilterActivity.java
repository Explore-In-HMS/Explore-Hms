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

package com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.utils.ApplicationUtils;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageVisionUtils;
import com.github.clemp6r.futuroid.Async;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.image.vision.ImageVision;
import com.huawei.hms.image.vision.ImageVisionImpl;
import com.huawei.hms.image.vision.bean.ImageVisionResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

@SuppressLint("DefaultLocale")
public class FilterActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "IMAGEKIT";

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    /**
     * ImageVisionImp object. All Image Vision Service functions are called from this object
     */
    private ImageVisionImpl imageVisionAPI = null;

    /**
     * UI Elements
     */
    private SeekBar seekBarFilterType;
    private SeekBar seekBarIntensity;
    private SeekBar seekBarCompressLevel;

    private TextView tvFilterType;
    private TextView tvIntensity;
    private TextView tvCompress;

    private MaterialButton ivOpenGallery;
    private MaterialButton ivOpenCamera;


    private ImageView ivImageFilter;


    private Bitmap bmNonFiltered;

    /**
     * Filter service parameters
     */
    private int filterType = 0;
    private Float intensityLevel = 1.0f;
    private Float compressRateLevel = 1.0f;

    /**
     * Target image URI
     */
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_vision_filter);

        setupToolbar();
        initUI();
        initListener();
        initService();
    }

    /**
     * Initialize Toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_filter_service_image_kit);
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
     * Initialize UI Elements
     */
    private void initUI() {
        seekBarFilterType = findViewById(R.id.sb_filter_type);
        seekBarIntensity = findViewById(R.id.sb_intensity_level);
        seekBarCompressLevel = findViewById(R.id.sb_compress_level);

        tvFilterType = findViewById(R.id.tv_filter_type_image_kit);
        tvIntensity = findViewById(R.id.tv_intensity_level_image_kit);
        tvCompress = findViewById(R.id.tv_compress_level_image_kit);

        ivImageFilter = findViewById(R.id.iv_image_filter_service_image_kit);
        ivOpenGallery = findViewById(R.id.btn_filter_service_image_kit_gallery);
        ivOpenCamera = findViewById(R.id.btn_filter_service_image_kit_camera);
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener() {

        seekBarFilterType.setOnSeekBarChangeListener(this);
        seekBarIntensity.setOnSeekBarChangeListener(this);
        seekBarCompressLevel.setOnSeekBarChangeListener(this);

        ivOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        ivOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

    }

    /**
     * Initialize Service
     *
     * @see "https://bit.ly/3nlEvIW"
     */
    private void initService() {

        // Obtain an ImageVisionImpl instance.
        imageVisionAPI = ImageVision.getInstance(this);

        final JSONObject authJson = ApplicationUtils.createAuthJson(this);


        imageVisionAPI.setVisionCallBack(new ImageVision.VisionCallBack() {
            @Override
            public void onSuccess(int successCode) {
                int initCode = imageVisionAPI.init(getApplicationContext(), authJson);

                if (initCode == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_initialization_success_common_image_kit), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_callback_failed_common_image_kit) + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
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
     * Filter Service start filter, necessary parameters should take from user
     * To filter the image call getColorFilter(JSONObject,Bitmap)
     */
    public void startFilter() {
        final JSONObject requestJson = new JSONObject();
        JSONObject taskJson = new JSONObject();

        try {
            if (imageVisionAPI != null) {

                if (bmNonFiltered == null)
                    bmNonFiltered = ((BitmapDrawable) ivImageFilter.getDrawable()).getBitmap();

                taskJson.put("filterType", filterType); // 0 -24
                taskJson.put("intensity", intensityLevel); // 0 - 0.1
                taskJson.put("compressRate", compressRateLevel); // 0.1 - 1
                requestJson.put("taskJson", taskJson);
                requestJson.put("authJson", ApplicationUtils.createAuthJson(this));
                requestJson.put("requestId", 1);

                Async.submit(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        ImageVisionResult visionResult = imageVisionAPI.getColorFilter(requestJson, bmNonFiltered);

                        Log.i(TAG, "ResultCode -> " + visionResult.getResultCode());

                        if (visionResult.getImage() != null)
                            ivImageFilter.setImageBitmap(visionResult.getImage());

                        //stopService();

                        return visionResult.getImage();
                    }
                });


            }
        } catch (JSONException ex) {
            Log.e(TAG, ex.toString());
        }
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }

    /**
     * Stop Filter Service, if you do not need to use filters any longer use this function
     */
    private void stopService() { //If you do not need to use filters any longer, call the imageVisionAPI.stop() API to stop the Image Vision service
        imageVisionAPI.stop();
    }


    /**
     * Obtain data from Camera or Gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Picasso.get()
                    .load(data.getData())
                    .fit()
                    .centerCrop()
                    .into(ivImageFilter, new Callback() {
                        @Override
                        public void onSuccess() {
                            bmNonFiltered = null;
                            Log.i(TAG, "Image loaded");

                            if (filterType != 0 || compressRateLevel != 1) {
                                startFilter();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.i(TAG, "Image load failed");
                        }
                    });
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
            Picasso.get().
                    load(imageUri).
                    fit().
                    centerCrop().
                    into(ivImageFilter, new Callback() {
                        @Override
                        public void onSuccess() {
                            bmNonFiltered = null;
                            Log.i(TAG, "Image loaded");

                            if (filterType != 0 || compressRateLevel != 1) {
                                startFilter();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.i(TAG, "Image load failed");
                        }
                    });
        }
    }

    /**
     * SeekBar Listener
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.equals(seekBarFilterType)) {
            tvFilterType.setText(String.format("%s : %d- %s", getString(R.string.txt_filter_type_filter_image_kit), progress, ImageVisionUtils.getFilters().get(String.valueOf(progress))));
        } else if (seekBar.equals(seekBarIntensity)) {
            float intensityValue = ((float) progress);
            intensityValue /= 100;
            tvIntensity.setText(getString(R.string.txt_intensity_image_kit) + intensityValue);
        } else if (seekBar.equals(seekBarCompressLevel)) {
            float compressRateValue = (float) progress;
            compressRateValue /= 100;
            tvCompress.setText(getString(R.string.txt_compress_image_kit) + compressRateValue);
        }
    }

    /**
     * SeekBar Listener
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int progress = seekBar.getProgress();

        if (seekBar.equals(seekBarFilterType)) {

            tvFilterType.setText(String.format("%s : %d- %s", getString(R.string.txt_filter_type_filter_image_kit), progress, ImageVisionUtils.getFilters().get(String.valueOf(progress))));
            filterType = progress;
            startFilter();

        } else if (seekBar.equals(seekBarIntensity)) {

            float intensityValue = ((float) progress);
            intensityValue /= 100;
            tvIntensity.setText(getString(R.string.txt_intensity_image_kit) + intensityValue);

            intensityLevel = intensityValue;

            startFilter();

        } else if (seekBar.equals(seekBarCompressLevel)) {

            float compressRateValue = (float) progress;
            compressRateValue /= 100;
            tvCompress.setText(getString(R.string.txt_compress_image_kit) + compressRateValue);

            compressRateLevel = compressRateValue;

            startFilter();
        }
    }

    /**
     * SeekBar Listener
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "onStartTrackingTouch");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
