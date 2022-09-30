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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.general_text_recognition;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.hms.explorehms.huawei.feature_hiai.utils.ImageUtils;
import com.hms.explorehms.huawei.feature_hiai.utils.hiai_service_utils.TextRecognitionUtils;
import com.google.android.material.button.MaterialButton;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.text.TextDetector;
import com.huawei.hiai.vision.visionkit.text.Text;
import com.huawei.hiai.vision.visionkit.text.TextDetectType;
import com.huawei.hiai.vision.visionkit.text.config.TextConfiguration;
import com.huawei.hiai.vision.visionkit.text.config.VisionTextConfiguration;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GeneralTextRecognitionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private int textLanguage = TextConfiguration.AUTO;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnTextLangAuto;
    private MaterialButton btnTextLangManuel;
    private MaterialButton btnGetResult;

    private ImageView ivImage;
    private ImageView ivResultImage;

    private Spinner spinnerTextLanguage;

    private ConstraintLayout cLayoutSpinner;

    private TextView tvResultText;
    private TextView tvResultTextLang;
    private TextView tvResultProbability;

    private View selectedModeView;

    private boolean hasResult = false;

    public GeneralTextRecognitionActivity() {
        super(ServiceGroupConstants.TEXT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_text_recognition_hiai);
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
        btnGallery = findViewById(R.id.btn_general_text_recognition_gallery);
        btnCamera = findViewById(R.id.btn_general_text_recognition_camera);
        ivImage = findViewById(R.id.iv_general_text_recognition_image);
        ivResultImage = findViewById(R.id.iv_general_text_recognition_result_image);
        btnTextLangAuto = findViewById(R.id.btn_general_text_recognition_lang_auto);
        btnTextLangManuel = findViewById(R.id.btn_general_text_recognition_lang_manuel);
        btnGetResult = findViewById(R.id.btn_general_text_recognition_run);
        spinnerTextLanguage = findViewById(R.id.spinner_general_text_recognition_text_languages);
        cLayoutSpinner = findViewById(R.id.cl_general_text_recognition_spinner_hiai);
        tvResultText = findViewById(R.id.tv_general_text_recognition_result_text);
        tvResultTextLang = findViewById(R.id.tv_general_text_recognition_result_language);
        tvResultProbability = findViewById(R.id.tv_general_text_recognition_result_probability);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_general_text_recognition_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_general_text_recognition_link_hiai));
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

        btnTextLangAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQualityButtonsColor(btnTextLangAuto);
                textLanguage = TextConfiguration.AUTO;
                cLayoutSpinner.setVisibility(View.GONE);
                setConstraintsOfTextLanguagesButtons(btnTextLangAuto);


            }
        });

        btnTextLangManuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQualityButtonsColor(btnTextLangManuel);
                cLayoutSpinner.setVisibility(View.VISIBLE);
                setConstraintsOfTextLanguagesButtons(btnTextLangManuel);
            }
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
                Log.i(TAG, "onNothingSelected");
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

        ivImage.setOnClickListener(v -> {
            if (imageBitmap != null) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivImage);
            }
        });

        ivResultImage.setOnClickListener(v -> {
            if (hasResult) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivResultImage);
            }
        });

        btnTextLangAuto.performClick();
    }

    @Override
    public void initService() {
        VisionBase.init(GeneralTextRecognitionActivity.this, new ConnectionCallback() {
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

        tvResultText.setText("");
        tvResultTextLang.setText("");
        tvResultProbability.setText("");

        if (imageBitmap != null) {

            resizeBitmap();

            TextDetector textDetector = new TextDetector(getApplicationContext());

            VisionImage visionImage = VisionImage.fromBitmap(imageBitmap);

            VisionTextConfiguration configuration = new VisionTextConfiguration.Builder()
                    .setAppType(VisionTextConfiguration.APP_NORMAL)
                    .setProcessMode(VisionTextConfiguration.MODE_IN)
                    .setDetectMode(TextDetectType.TYPE_TEXT_DETECT_FOCUS_SHOOT)
                    .setLanguage(textLanguage)
                    .build();

            textDetector.setVisionConfiguration(configuration);

            TextConfiguration textConfiguration = new TextConfiguration();
            textConfiguration.setLanguage(textLanguage);
            textConfiguration.setEngineType(TextDetectType.TYPE_TEXT_DETECT_FOCUS_SHOOT);


            textDetector.setTextConfiguration(textConfiguration);

            Text result = new Text();

            int resultCode = textDetector.detect(visionImage, result, null);

            if (resultCode == 0) {
                Toast.makeText(getApplicationContext(), "Detection Successful", Toast.LENGTH_SHORT).show();

                /**
                 * TEXT
                 */
                if (result.getValue() != null)
                    tvResultText.setText(result.getValue());

                /**
                 * LANG
                 */
                int langID = result.getPageLanguage();
                String languageName = TextRecognitionUtils.getTextLanguages().get(langID);
                tvResultTextLang.setText(languageName);

                /**
                 * PROBABILITY
                 */
                tvResultProbability.setText(String.valueOf(result.getProbability()));

                drawTextPointsOnImage(result);
            } else
                Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        } else {
            showImageAlert();
        }
    }

    private void drawTextPointsOnImage(Text result) {

        boolean edited = false;

        Rect rect = result.getTextRect();
        Point[] points = result.getCornerPoints();

        Bitmap bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(mutableBitmap);

        if (points != null) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(10);

            for (Point p : points) {
                c.drawPoint(p.x, p.y, paint);
            }

            edited = true;
        }

        if (rect != null) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            c.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);

            edited = true;
        } else if (points != null) {

            try {
                int x1 = points[0].x;
                int y1 = points[1].y;
                int x2 = points[2].x;
                int y2 = points[3].y;

                Rect rectArea = new Rect(x1, y1, x2, y2);

                Paint paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);

                c.drawRect(rectArea.left, rectArea.top, rectArea.right, rectArea.bottom, paint);

                edited = true;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                edited = false;
            }
        }

        if (edited) {
            ivResultImage.setVisibility(View.VISIBLE);
            ivResultImage.setPadding(3, 3, 3, 3);
            Glide.with(this).load(mutableBitmap).into(ivResultImage);
            hasResult = true;
        } else {
            ivResultImage.setVisibility(View.GONE);
        }
    }

    public void resizeBitmap() {
        int height = imageBitmap.getHeight();
        int width = imageBitmap.getWidth();

        boolean scale = false;

        if (width > 1440) {
            width = 1439;
            scale = true;
        }
        if (height > 15210) {
            height = 15209;
            scale = true;
        }

        if (scale) imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
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

    private void setConstraintsOfTextLanguagesButtons(View view) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btnTextLangAuto.getLayoutParams();

        if (view.equals(btnTextLangManuel)) {
            params.bottomToBottom = R.id.btn_general_text_recognition_lang_auto;

        } else if (view.equals(btnTextLangAuto)) {
            params.bottomToBottom = R.id.btn_general_text_recognition_lang_auto;
        }

        params.bottomMargin = Math.round(16 * getApplicationContext().getResources().getDisplayMetrics().density);
        btnTextLangAuto.requestLayout();
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
