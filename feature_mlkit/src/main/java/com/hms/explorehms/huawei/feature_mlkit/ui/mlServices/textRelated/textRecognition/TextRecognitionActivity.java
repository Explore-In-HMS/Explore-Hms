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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.textRecognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TextRecognitionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = TextRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.imageView_textRecognition)
    ImageView imageViewTextRecognition;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MLTextAnalyzer analyzerLocal;
    private MLTextAnalyzer analyzerRemote;

    private static final int PERMISSION_CODE_CAMERA = 1;
    private static final int PERMISSION_CODE_STORAGE = 2;

    private static final int PERMISSION_CODE_STORAGE_FOR_CLOUD = 3;

    String[] permissionRequestCamera = {Manifest.permission.CAMERA};
    String[] permissionRequestStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int ACTIVITY_INTENT_CODE_CAMERA = 11;
    private static final int ACTIVITY_INTENT_CODE_STORAGE = 22;

    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_CLOUD = 33;

    // endregion

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_text_recognition);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        // this is important for get image of imageView by Bitmap and use with localAnalyzerWithImage
        imageViewTextRecognition.setDrawingCacheEnabled(true);

    }

    @OnClick({R.id.btn_textRecognitionWithImage, R.id.btn_textRecognitionWithStorage, R.id.btn_textRecognitionWithCamera,
            R.id.btn_textRecognitionWithImageOnCloud, R.id.btn_textRecognitionWithStorageOnCloud})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_textRecognitionWithImage:
                Log.i(TAG, "onclick for btn_textRecognitionWithImage");
                Bitmap bitmap = imageViewTextRecognition.getDrawingCache();
                // another method
                // Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.image_text_recognition);
                localAnalyzerWithImage(bitmap, false);
                break;
            case R.id.btn_textRecognitionWithStorage:
                Log.i(TAG, "onclick for btn_textRecognitionWithStorage : " + PERMISSION_CODE_STORAGE);
                ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE);
                break;
            case R.id.btn_textRecognitionWithCamera:
                Log.i(TAG, "onclick for btn_textRecognitionWithCamera : " + PERMISSION_CODE_CAMERA);
                ActivityCompat.requestPermissions(this, permissionRequestCamera, PERMISSION_CODE_CAMERA);
                break;
            case R.id.btn_textRecognitionWithImageOnCloud:
                Log.i(TAG, "onclick for btn_textRecognitionWithImageOnCloud");
                if (Utils.haveNetworkConnection(this)) {
                    Bitmap bitmapForCloud = imageViewTextRecognition.getDrawingCache();
                    // another method
                    // Bitmap bitmapForCloud = BitmapFactory.decodeResource(this.getResources(), R.drawable.image_text_recognition);
                    remoteAnalyzerWithImage(bitmapForCloud, false);
                } else {
                    DialogUtils.showDialogNetworkWarning(
                            this,
                            getString(R.string.need_network_permission),
                            getString(R.string.permission_settings_allow_open_network),
                            R.drawable.icon_settings,
                            "You can not use Remote Operation without Internet Connection!",
                            getString(R.string.yes_go), getString(R.string.cancel));
                }
                break;
            case R.id.btn_textRecognitionWithStorageOnCloud:
                Log.i(TAG, "onclick for btn_textRecognitionWithStorageOnCloud : " + PERMISSION_CODE_STORAGE_FOR_CLOUD);
                ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE_FOR_CLOUD);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_trs_ter));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Text recognition on the device
     * <p>
     * Create the text analyzer MLTextAnalyzer to recognize characters in images.
     * You can set MLLocalTextSetting to specify languages that can be recognized.
     * If you do not set the languages, only Romance languages can be recognized by default.
     * Use default parameter settings to configure the on-device text analyzer.
     * Only Romance languages can be recognized.
     * analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer();
     * Use the customized parameter MLLocalTextSetting to configure the text analyzer on the device.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void localAnalyzerWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        showProgress();
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage("en")
                .create();
        this.analyzerLocal = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);

        Log.i(TAG, "localAnalyzerWithImage() : analyzer isAvailable : " + this.analyzerLocal.isAvailable() + " - analyzer getAnalyseType :  " + this.analyzerLocal.getAnalyseType());
        Log.i(TAG, "localAnalyzerWithImage() : bitmap : " + bitmap.getConfig());

        // Create an MLFrame by using android.graphics.Bitmap.
        // Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.test_image_text_recognition);

        // Use default parameter settings.
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = this.analyzerLocal.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                if (isFromGallery) {
                    imageViewTextRecognition.setImageBitmap(bitmap);
                } else {
                    imageViewTextRecognition.setImageResource(R.drawable.test_image_text_recognition);
                }
                // important for peek view from changed imageView resource
                imageViewTextRecognition.getDrawingCache();
                displaySuccessAnalyseResults(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure localAnalyzerWithImage : ", e);
                displayFailureAnalyseResults(e.getMessage());
            }
        });
    }

    private void displaySuccessAnalyseResults(MLText mlText) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        String result = "";
        List<MLText.Block> blocks = mlText.getBlocks();
        for (MLText.Block block : blocks) {
            for (MLText.TextLine line : block.getContents()) {
                result += line.getStringValue() + "\n";
            }
        }
        Log.i(TAG, "Success AnalyseResults : " + mlText.getStringValue());
        resultLogs.setText("Success AnalyseResults : with " + result.length() + " characters :\n" + result);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure AnalyseResults : " + msg);
        resultLogs.setText("Failure AnalyseResults : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "AnalyseResults was Failed : " + msg);
    }

    // ------------------------------------------------------------------------------------------ //

    /**
     * Text recognition on the cloud.
     * <p>
     * If you want to use cloud text analyzer,
     * you need to apply for an agconnect-services.json file in the developer
     * alliance(https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/ml-add-agc),
     * replacing the sample-agconnect-services.json in the project.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void remoteAnalyzerWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        showProgress();
        Log.i(TAG, "remoteAnalyzerWithImage");

        List<String> remoteAnalyzerWithImageList = new ArrayList<>();
        remoteAnalyzerWithImageList.add("zh");
        remoteAnalyzerWithImageList.add("en");
        // Create an analyzer. You can customize the analyzer by creating MLRemoteTextSetting
        MLRemoteTextSetting setting =
                new MLRemoteTextSetting.Factory()
                        // Set the on-cloud text detection mode.
                        // MLRemoteTextSetting.OCR_COMPACT_SCENE: dense text recognition
                        // MLRemoteTextSetting.OCR_LOOSE_SCENE: sparse text recognition
                        //.setTextDensityScene(MLRemoteTextSetting.OCR_LOOSE_SCENE)
                        .setTextDensityScene(MLRemoteTextSetting.OCR_COMPACT_SCENE)
                        // Specify the languages that can be recognized, which should comply with ISO 639-1.
                        .setLanguageList(remoteAnalyzerWithImageList)
                        // Set the format of the returned text border box.
                        .setBorderType(MLRemoteTextSetting.ARC)
                        // MLRemoteTextSetting.NGON: Return the coordinates of the four corner points of the quadrilateral.
                        // MLRemoteTextSetting.ARC: Return the corner points of a polygon border in an arc. The coordinates of up to 72 corner points
                        .create();

        Utils.setApiKeyForRemoteMLApplication(this);

        this.analyzerRemote = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer(setting);

        Log.i(TAG, "remoteAnalyzerWithImage() : analyzer isAvailable : " + this.analyzerRemote.isAvailable() + " - analyzer getAnalyseType :  " + this.analyzerRemote.getAnalyseType());
        Log.i(TAG, "remoteAnalyzerWithImage() : bitmap : " + bitmap.getConfig());

        // Use default parameter settings.
        // analyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer();
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        Task<MLText> task = this.analyzerRemote.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(new OnSuccessListener<MLText>() {
            @Override
            public void onSuccess(MLText text) {
                if (isFromGallery) {
                    imageViewTextRecognition.setImageBitmap(bitmap);
                } else {
                    imageViewTextRecognition.setImageResource(R.drawable.test_image_text_recognition);
                }
                // important for peek view from changed imageView resource
                imageViewTextRecognition.getDrawingCache();
                displaySuccessAnalyseResults(text);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure remoteAnalyzerWithImage : ", e);
                displayFailureAnalyseResults(e.getMessage());
            }
        });
    }// remoteAnalyzerWithImage


    // ------------------------------------------------------------------------------------------ //

    /**
     * Text recognition with camera on the device
     */
    private void localAnalyzerWithCamera() {
        Log.i(TAG, "localAnalyzerWithCamera()");
        startActivity(new Intent(this, TextRecognitionCameraActivity.class));
    }// localAnalyzerWithCamera

    // ------------------------------------------------------------------------------------------ //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> Start Intent localAnalyzerWithCamera");
                localAnalyzerWithCamera();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_camera_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_settings,
                        "You can not use CameraAnalyzer without Camera Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }

        if (requestCode == PERMISSION_CODE_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not use ImageAnalyzer without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_CLOUD) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForCloud -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_CLOUD);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not use ImageAnalyzer on Cloud without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }


    }

    // ------------------------------------------------------------------------------------------ //


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (resultCode == 0) {
            Log.w(TAG, "onActivityResult : onActivityResult No any data detected");
        } else {


            if (requestCode == ACTIVITY_INTENT_CODE_CAMERA) {
                // maybe feature necessary
                Log.i(TAG, "onActivityResult : activityIntentCodeCamera : " + ACTIVITY_INTENT_CODE_CAMERA);

            } else if (requestCode == ACTIVITY_INTENT_CODE_STORAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    Log.i(TAG, "onActivityResult : activityIntentCodeStorage calling with MediaStore.Images.Media.getBitmap bitmap : " + bitmap.getConfig());
                    localAnalyzerWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                }

            } else if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_CLOUD) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    Log.i(TAG, "onActivityResult : activityIntentCodeStorageForCloud calling with MediaStore.Images.Media.getBitmap bitmap : " + bitmap.getConfig());
                    remoteAnalyzerWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                }

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

        if (this.analyzerLocal != null) {
            try {
                this.analyzerLocal.stop();
                Log.e(TAG, "onDestroy : analyzerLocal stopped");
            } catch (IOException e) {
                Log.e(TAG, "onDestroy : Stop failed: " + e.getMessage());
            }
        }
        if (this.analyzerRemote != null) {
            try {
                this.analyzerRemote.stop();
                Log.e(TAG, "onDestroy : analyzerRemote stopped");
            } catch (IOException e) {
                Log.e(TAG, "onDestroy : Stop failed: " + e.getMessage());
            }
        }
    }


}