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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.adapter.pager.DocumentTextConverterViewPagerAdapter;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.hms.explorehms.huawei.feature_hiai.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.text.DocConverter;
import com.huawei.hiai.vision.text.SlideCallback;
import com.huawei.hiai.vision.visionkit.image.detector.DocCoordinates;
import com.huawei.hiai.vision.visionkit.text.Text;
import com.huawei.hiai.vision.visionkit.text.TextDetectType;
import com.huawei.hiai.vision.visionkit.text.config.DocConverterConfiguration;
import com.huawei.hiai.vision.visionkit.text.config.TextConfiguration;
import com.huawei.hiai.vision.visionkit.text.config.VisionTextConfiguration;

@SuppressLint("ClickableViewAccessibility")
public class DocumentTextConverterRecognitionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private int textLanguage = TextConfiguration.AUTO;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnTextLanguageAuto;
    private MaterialButton btnTextLanguageManuel;
    private MaterialButton btnGetResult;

    private ImageView ivImage;

    private Spinner spinnerTextLanguage;

    private ScrollView scrollView;

    private ConstraintLayout cLayoutSpinner;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private DocCoordinates coordinatesResult;
    private Bitmap refineBitmapResult;
    private Bitmap superResolutionBitmapResult;
    private Text textResult;

    private View selectedModeView;

    public DocumentTextConverterRecognitionActivity() {
        super(ServiceGroupConstants.TEXT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_text_converter_recognition_hiai);
        baseContext = this;

        initUI();
        setupToolbar();
        initListeners();
        try {
            initService();
        } catch (Exception e) {
            Log.e(TAG, "Initialization Error : " + e.toString());
        }

        setSpinnerAdapter();
    }

    @Override
    public void initUI() {
        tabLayout = findViewById(R.id.tl_document_text_converter_recognition);
        viewPager = findViewById(R.id.vp_document_text_converter_recognition);
        scrollView = findViewById(R.id.sv_document_text_converter);
        btnGallery = findViewById(R.id.btn_document_text_converter_gallery);
        btnCamera = findViewById(R.id.btn_document_text_converter_camera);
        ivImage = findViewById(R.id.iv_document_text_converter_image);
        btnTextLanguageAuto = findViewById(R.id.btn_document_text_converter_lang_auto);
        btnTextLanguageManuel = findViewById(R.id.btn_document_text_converter_lang_manuel);
        spinnerTextLanguage = findViewById(R.id.spinner_document_text_converter_text_languages);
        btnGetResult = findViewById(R.id.btn_document_text_converter_run);
        cLayoutSpinner = findViewById(R.id.cl_document_text_converter_spinner_hiai);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_document_text_converter_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_document_text_converter_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnGallery.setOnClickListener(v -> openGallery());

        btnCamera.setOnClickListener(v -> openCamera());

        btnTextLanguageAuto.setOnClickListener(v -> {
            setQualityButtonsColor(btnTextLanguageAuto);
            textLanguage = TextConfiguration.AUTO;
            cLayoutSpinner.setVisibility(View.GONE);
            setConstraintsOfTextLanguagesButtons(btnTextLanguageAuto);
        });

        btnTextLanguageManuel.setOnClickListener(v -> {
            setQualityButtonsColor(btnTextLanguageManuel);
            cLayoutSpinner.setVisibility(View.VISIBLE);
            setConstraintsOfTextLanguagesButtons(btnTextLanguageManuel);
        });

        spinnerTextLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * TextConfiguration.CHINESE is 1 SPANISH is 2...
                 */
                textLanguage = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnGetResult.setOnClickListener(v -> {
            try {
                getResult();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        scrollView.setOnTouchListener((v, event) -> {
            viewPager.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        viewPager.setOnTouchListener((v, event) -> {
            viewPager.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        btnTextLanguageAuto.performClick();
    }

    @Override
    public void initService() {
        VisionBase.init(DocumentTextConverterRecognitionActivity.this, new ConnectionCallback() {
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

    private void initTabAdapter() {
        viewPager.setAdapter(new DocumentTextConverterViewPagerAdapter(
                DocumentTextConverterRecognitionActivity.this.getSupportFragmentManager(),
                getLifecycle(),
                coordinatesResult,
                refineBitmapResult,
                superResolutionBitmapResult,
                textResult,
                imageBitmap));

        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteHiAi));
        tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#ffffff"));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {

            if (position == 0)
                tab.setText("Doc Coordinates");
            else if (position == 1)
                tab.setText("Doc Refine");
            else if (position == 2)
                tab.setText("Text Recognition");
            else if (position == 3)
                tab.setText("Doc Super Resolution");
        });
        tabLayoutMediator.attach();
    }

    private void setSpinnerAdapter() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_item_general_text_recognition_language_hiai, getResources().getStringArray(R.array.GeneralTextRecognitionLanguageList));

        spinnerTextLanguage.setAdapter(adapter);
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
            if (imageBitmap.getWidth() < 1080)
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 1080, imageBitmap.getHeight(), false);
            else if (imageBitmap.getWidth() > 2560)
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 2560, imageBitmap.getHeight(), false);

            Bitmap reduceBitmap = reduceBitmapResolution(imageBitmap);

            DocConverter docConverter = new DocConverter(getApplicationContext());//Construct Detector.

            VisionImage visionImage = VisionImage.fromBitmap(reduceBitmap);

            VisionTextConfiguration config = new VisionTextConfiguration.Builder()
                    .setAppType(VisionTextConfiguration.APP_NORMAL)
                    .setProcessMode(VisionTextConfiguration.MODE_IN)
                    .setDetectType(TextDetectType.TYPE_TEXT_DETECT_FOCUS_SHOOT)
                    .setLanguage(textLanguage)
                    .build();

            DocConverterConfiguration docConfig = new DocConverterConfiguration.Builder().build();
            docConfig.setTextConfig(config);

            coordinatesResult = null;
            refineBitmapResult = null;
            superResolutionBitmapResult = null;
            textResult = null;

            int resultCode = docConverter.detectSlide(visionImage, new SlideCallback() {
                @Override
                public void onDocDetect(DocCoordinates docCoordinates) {
                    coordinatesResult = docCoordinates;
                }

                @Override
                public void onDocRefine(Bitmap bitmap) {
                    refineBitmapResult = bitmap;
                }

                @Override
                public void onSuperResolution(Bitmap bitmap) {
                    superResolutionBitmapResult = bitmap;
                }

                @Override
                public void onTextRecognition(Text text) {
                    textResult = text;
                }

                @Override
                public void onError(int i) {
                    Log.i(TAG, "onError");
                }
            });


            if (resultCode != 0)
                Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + resultCode, Toast.LENGTH_SHORT).show();
            else
                initTabAdapter(); //send received data to fragments

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

    private void setConstraintsOfTextLanguagesButtons(View view) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btnTextLanguageAuto.getLayoutParams();

        if (view.equals(btnTextLanguageManuel)) {
            params.bottomToBottom = R.id.cl_document_text_converter_spinner_hiai;

        } else if (view.equals(btnTextLanguageAuto)) {
            params.bottomToBottom = R.id.cl_document_text_converter_text_lang_hiai;
        }

        params.bottomMargin = Math.round(16 * getApplicationContext().getResources().getDisplayMetrics().density);
        btnTextLanguageAuto.requestLayout();
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
