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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations.GeneralCardProcessor;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations.GeneralCardResult;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations.HKIdCardProcessor;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations.HomeCardProcessor;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations.PassCardProcessor;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GeneralCardRecognitionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = GeneralCardRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.tv_id_text)
    TextView resultIdText;

    @Nullable
    @BindView(R.id.avatar_sample_img)
    ImageView imageViewCardSample;

    @Nullable
    @BindView(R.id.avatar_img)
    ImageView imageViewCard;

    @Nullable
    @BindView(R.id.avatar_delete)
    ImageView imageViewDelete;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.card_type)
    RadioGroup radGrpCardType;

    int selectedImageId;

    // Same choice was made on the radioGroup on the layout.
    private CardType cardTypeEnum = CardType.HKIDCARD;

    private static final int PERMISSION_CODE_STORAGE = 1;
    private static final int PERMISSION_CODE_CAMERA = 2;
    private static final int PERMISSION_CODE_CAMERA_FOR_STREAM = 3;

    String[] permissionRequestCamera = {Manifest.permission.CAMERA};
    String[] permissionRequestStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int ACTIVITY_INTENT_CODE_STORAGE = 11;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_card_recognition);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        // setup default image id for select gcr type and static recognition operation
        selectedImageId = R.drawable.gcr_identity_card;

        // this is important for get image of imageView by Bitmap and use it
        imageViewCard.setDrawingCacheEnabled(true);

        radGrpCardType.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.passCard:
                    updateCurrentCardType(CardType.PASSCARD);
                    selectedImageId = R.drawable.gcr_pass_card;
                    adjustDefaultImageAndgetBitmap(selectedImageId);
                    break;
                case R.id.HKIdCard:
                    updateCurrentCardType(CardType.HKIDCARD);
                    selectedImageId = R.drawable.gcr_identity_card;
                    adjustDefaultImageAndgetBitmap(selectedImageId);
                    break;
                case R.id.comeHomeCard:
                    updateCurrentCardType(CardType.COMEHOMECARD);
                    selectedImageId = R.drawable.gcr_travel_permit_card;
                    adjustDefaultImageAndgetBitmap(selectedImageId);
                    break;
                default:
                    break;
            }
        });


    }

    @OnClick({R.id.btn_gcrWithPicFromImage, R.id.btn_gcrWithPicFromStorage, R.id.btn_gcrWithTakePhoto, R.id.btn_gcrWithWideoStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gcrWithPicFromImage:
                if (selectedImageId == 0) {
                    Log.i(TAG, "onItemClick : selectedImageId is O not Selected");
                    Utils.showToastMessage(getApplicationContext(), "Please chose card type and upload image before process!");
                } else {
                    Log.i(TAG, "onItemClick : selectedImageId " + selectedImageId);
                    detectWithLocalImage(((BitmapDrawable) imageViewCard.getDrawable()).getBitmap(), null, mlGcrCaptureCallback);
                }
                break;
            case R.id.btn_gcrWithPicFromStorage:
                ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE);
                break;
            case R.id.btn_gcrWithTakePhoto:
                ActivityCompat.requestPermissions(this, permissionRequestCamera, PERMISSION_CODE_CAMERA);
                break;
            case R.id.btn_gcrWithWideoStream:
                ActivityCompat.requestPermissions(this, permissionRequestCamera, PERMISSION_CODE_CAMERA_FOR_STREAM);
                break;
            default:
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_trs_gcr));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ------------------------------------------------------------------------------------------ //

    private Bitmap adjustDefaultImageAndgetBitmap(int drawableId) {
        imageViewCard.setImageResource(drawableId);
        return ((BitmapDrawable) imageViewCard.getDrawable()).getBitmap();
    }

    /**
     * Detect Card information with bitmap.
     *
     * @param bitmap
     * @param object
     * @param callback : MLGcrCapture.Callback
     */
    private void detectWithLocalImage(Bitmap bitmap, Object object, MLGcrCapture.Callback callback) {
        // check and analyse why i use this Object's object params.
        showProgress();
        MLGcrCaptureConfig config = new MLGcrCaptureConfig.Factory().setLanguage("en").create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(config);
        try {
            ocrManager.captureImage(bitmap, object, callback);
        } catch (Exception e) {
            Log.e(TAG, "detectWithLocalImage captureImage Exception : " + e.getMessage(), e);
            displayFailureAnalyseResults(e.getMessage());
        }
    }

    /**
     * Detect Card information with Photo taken by camera.
     *
     * @param object
     * @param callback : MLGcrCapture.Callback
     */
    private void detectWithTakePhoto(Object object, MLGcrCapture.Callback callback) {
        // check and analyse why i use this Object's object params.
        showProgress();
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().setLanguage("en").create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setTipText("Taking photo…\\nKeep the edges aligned")
                .setScanBoxCornerColor(Color.BLUE)
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO).create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        // Create a general card identification processor using the default interface.
        //MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig);
        try {
            ocrManager.capturePhoto(this, object, callback);
        } catch (Exception e) {
            Log.e(TAG, "detectWithTakePhoto capturePhoto Exception : " + e.getMessage(), e);
            displayFailureAnalyseResults(e.getMessage());
        }
    }


    /**
     * Detect Card information with video camera preview .
     * <p>
     * Set the recognition parameters, call the recognizer capture interface for recognition,
     * and the recognition result will be returned through the callback function.
     *
     * @param object
     * @param callback : MLGcrCapture.Callback
     */
    private void detectWithVideoStream(Object object, MLGcrCapture.Callback callback) {
        // check and analyse why i use this Object's object params.
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().setLanguage("en").create();
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setTipText("Recognizing, please align the edges.")
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO).create();
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        try {
            ocrManager.capturePreview(this, object, callback);
        } catch (Exception e) {
            Log.e(TAG, "detectWithVideoStream capturePreview Exception : " + e.getMessage(), e);
            displayFailureAnalyseResults(e.getMessage());
        }
    }

    // ------------------------------------------------------------------------------------------ //


    private MLGcrCapture.Callback mlGcrCaptureCallback = new MLGcrCapture.Callback() {
        @Override
        public int onResult(MLGcrCaptureResult result, Object object) {
            if (result == null) {
                Log.e(TAG, "callback MLGcrCaptureResult is null");
                displayFailureAnalyseResults("MLGcrCaptureResult is NULL !");
                return MLGcrCaptureResult.CAPTURE_CONTINUE;
            }

            GeneralCardProcessor idCard = null;
            GeneralCardResult cardResult = null;

            if (cardTypeEnum == CardType.PASSCARD) {
                idCard = new PassCardProcessor(result.text);
            } else if (cardTypeEnum == CardType.HKIDCARD) {
                idCard = new HKIdCardProcessor(result.text);
            } else if (cardTypeEnum == CardType.COMEHOMECARD) {
                idCard = new HomeCardProcessor(result.text);
            }

            if (idCard != null) {
                cardResult = idCard.getResult();
            }

            imageViewCard.setImageBitmap(result.cardBitmap);

            // If the results don't match
            if (cardResult == null || cardResult.valid.isEmpty() || cardResult.number.isEmpty()) {
                displayFailureAnalyseResults("MLGcrCaptureResult Text and CardNumber is NULL or Empty !");
                resultIdText.setText(null);
                return MLGcrCaptureResult.CAPTURE_CONTINUE;
            }

            resultLogs.setText(null);
            resultIdText.setText("ID : " + cardResult.number);
            displaySuccessAnalyseResults("Valid : " + cardResult.valid + "\n" + "Number : " + cardResult.number);

            return MLGcrCaptureResult.CAPTURE_STOP;
        }

        @Override
        public void onCanceled() {
            Log.e(TAG, "MLGcrCaptureResult callback onCanceled");
            displayFailureAnalyseResults("MLGcrCaptureResult callback onCanceled !");
        }

        @Override
        public void onFailure(int i, Bitmap bitmap) {
            Log.e(TAG, "MLGcrCaptureResult callback onFailure");
            displayFailureAnalyseResults("MLGcrCaptureResult callback onFailure ! " + i);
        }

        @Override
        public void onDenied() {
            Log.e(TAG, "MLGcrCaptureResult callback onDenied");
            displayFailureAnalyseResults("MLGcrCaptureResult callback onCameraDenied !");
        }

        private void displaySuccessAnalyseResults(String analyseResults) {
            hideProgress();
            Utils.createVibration(getApplicationContext(), 200);
            Log.i(TAG, "Success AnalyseResults : " + analyseResults);
            resultLogs.setText("Success AnalyseResults : with " + analyseResults.length() + " characters :\n" + analyseResults);
        }

    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not use MLGcr CaptureImage without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> call detectWithTakePhoto");
                detectWithTakePhoto(new Object(), mlGcrCaptureCallback);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission  was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_camera_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_settings,
                        "You can not use MLGcr CapturePhoto without Camera Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> call detectWithVideoStream ");
                detectWithVideoStream(new Object(), mlGcrCaptureCallback);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_camera_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_settings,
                        "You can not use MLGcr CapturePhoto without Camera Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (resultCode == 0) {
            Log.w(TAG, "onActivityResult : onActivityResult No any data detected");
            Utils.showToastMessage(getApplicationContext(), "No any data detected!");
        } else {

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    Log.i(TAG, "onActivityResult : activityIntentCodeStorage calling with MediaStore.Images.Media.getBitmap bitmap : " + bitmap.getConfig());
                    detectWithLocalImage(bitmap, null, mlGcrCaptureCallback);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                }

            }
        }
    }

    // ------------------------------------------------------------------------------------------ //


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure AnalyseResults : " + msg);
        resultLogs.setText("Failure AnalyseResults : \n" + msg);
        resultIdText.setText(null);
        Utils.showToastMessage(getApplicationContext(), "AnalyseResults was Failed : " + msg);
    }

    // ------------------------------------------------------------------------------------------ //

    private void updateCurrentCardType(CardType type) {
        if (cardTypeEnum != type) {
            resultLogs.setText(null);
            resultIdText.setText(null);
        }
        cardTypeEnum = type;
    }

    // Hong Kong and Macao,
    // Hong Kong identity card,
    // Mainland Travel Permit for Hong Kong and Macao Residents.
    public enum CardType {
        HKIDCARD, PASSCARD, COMEHOMECARD
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}