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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.bankCardRecognition;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.textRecognition.TextRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.StringUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCapture;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureConfig;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureFactory;
import com.huawei.hms.mlplugin.card.bcr.MLBcrCaptureResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class BankCardRecognitionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = TextRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.tv_cardNumber)
    TextView cardNumberText;

    @Nullable
    @BindView(R.id.cardNumber_img)
    ImageView imageViewCardNumber;

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

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE = 2;
    String[] permissionRequestCameraAndStorage = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card_recognition);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        // this is important for get image of imageView by Bitmap and use it
        imageViewCard.setDrawingCacheEnabled(true);
        imageViewCardNumber.setDrawingCacheEnabled(true);

        showSampleCardImageHideIdCardImage();

    }

    @OnClick({R.id.btn_bcrWithTakePhoto, R.id.cardNumber_img, R.id.avatar_delete})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bcrWithTakePhoto:
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorage, PERMISSION_CODE_CAMERA_AND_STORAGE);
                break;
            case R.id.cardNumber_img:
                String cardText = cardNumberText.getText().toString();
                if (!cardText.isEmpty()) {
                    StringUtils.copyTextToClipboard(getApplicationContext(), "copiedCardNumber", cardText);
                    Utils.showToastMessage(getApplicationContext(), "Card Number Copied to Clipboard!");
                } else {

                    Utils.showToastMessage(getApplicationContext(), "Card Number Empty");
                }
                break;
            case R.id.avatar_delete:
                showSampleCardImageHideIdCardImage();
                resultLogs.setText("");
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_trs_bcr));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    // ------------------------------------------------------------------------------------------ //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> call startCameraStreamCapture");
                startCaptureCameraStream();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission  was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA and STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use MLGcr CapturePhoto without Camera And Storage Permission!",
                        "YES GO", "CANCEL");

            }
        }
    }

    // ------------------------------------------------------------------------------------------ //

    private void startCaptureCameraStream() {
        showProgress();
        MLBcrCaptureConfig config = new MLBcrCaptureConfig.Factory()
                .setIsShowPortraitStatusBar(true)
                .setOrientation(MLBcrCaptureConfig.ORIENTATION_AUTO)
                .setResultType(MLBcrCaptureConfig.RESULT_ALL)
                .create();
        MLBcrCapture bcrCapture = MLBcrCaptureFactory.getInstance().getBcrCapture(config);

        try {
            bcrCapture.captureFrame(this, this.callback);
        } catch (Exception e) {
            Log.e(TAG, "MLBcrCapture captureFrame Exception : " + e.getMessage(), e);
            displayFailureAnalyseResults(e.getMessage());
        }
    }


    private final MLBcrCapture.Callback callback = new MLBcrCapture.Callback() {

        @Override
        public void onSuccess(MLBcrCaptureResult result) {
            if (result == null) {
                Log.e(TAG, "callback MLBcrCaptureResult is null");
                displayFailureAnalyseResults("MLBcrCaptureResult is NULL !");
                return;
            }
            showBankCardImageHideSampleCardImage(result.getOriginalBitmap(), result.getNumberBitmap());

            displaySuccessAnalyseResults(result);

        }

        @Override
        public void onCanceled() {
            displayFailureAnalyseResults("MLBcrCaptureResult callback onCanceled !");

        }

        @Override
        public void onFailure(int retCode, Bitmap bitmap) {
            displayFailureAnalyseResults("MLBcrCaptureResult callback onFailure ! " + retCode);
        }

        @Override
        public void onDenied() {
            displayFailureAnalyseResults("MLBcrCaptureResult callback onCameraDenied !");
        }

        private void showBankCardImageHideSampleCardImage(Bitmap bitmapBankCard, Bitmap bitmapCardNumber) {
            imageViewCardSample.setVisibility(View.GONE);
            imageViewCard.setVisibility(View.VISIBLE);
            imageViewCard.setImageBitmap(bitmapBankCard);
            // important for peek view from changed imageView resource
            imageViewCard.getDrawingCache();
            imageViewCardNumber.setVisibility(View.VISIBLE);
            imageViewCardNumber.setImageBitmap(bitmapCardNumber);
            // important for peek view from changed imageView resource
            imageViewCardNumber.getDrawingCache();
            imageViewDelete.setVisibility(View.VISIBLE);
        }


        private void displaySuccessAnalyseResults(MLBcrCaptureResult result) {
            hideProgress();
            String analyseResults = editFormatCardAnalyseResult(result);
            Utils.createVibration(getApplicationContext(), 200);
            Log.i(TAG, "Success AnalyseResults : " + analyseResults);
            cardNumberText.setText(result.getNumber());
            resultLogs.setText("Success AnalyseResults : with " + analyseResults.length() + " characters :\n" + analyseResults);
        }

        private String editFormatCardAnalyseResult(MLBcrCaptureResult result) {
            StringBuilder resultBuilder = new StringBuilder();

            resultBuilder.append("Number：");
            resultBuilder.append(result.getNumber());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Issuer：");
            resultBuilder.append(result.getIssuer());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Expire: ");
            resultBuilder.append(result.getExpire());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Type: ");
            resultBuilder.append(result.getType());
            resultBuilder.append(System.lineSeparator());

            resultBuilder.append("Organization: ");
            resultBuilder.append(result.getOrganization());
            resultBuilder.append(System.lineSeparator());

            return resultBuilder.toString();
        }
    };


    // ------------------------------------------------------------------------------------------ //


    // ------------------------------------------------------------------------------------------ //

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showSampleCardImageHideIdCardImage() {
        imageViewCardSample.setVisibility(View.VISIBLE);
        imageViewCardNumber.setVisibility(View.GONE);
        imageViewCard.setVisibility(View.GONE);
        imageViewDelete.setVisibility(View.GONE);
        cardNumberText.setText("");
    }


    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure AnalyseResults : " + msg);
        resultLogs.setText("Failure AnalyseResults : \n" + msg);
        cardNumberText.setText("");
        Utils.showToastMessage(getApplicationContext(), "AnalyseResults was Failed : " + msg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}