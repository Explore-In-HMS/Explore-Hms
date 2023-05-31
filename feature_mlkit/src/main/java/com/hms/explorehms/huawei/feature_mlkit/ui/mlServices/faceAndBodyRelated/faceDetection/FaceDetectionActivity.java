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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection;

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
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;

import java.io.IOException;
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

public class FaceDetectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = FaceDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;


    MLFaceAnalyzer faceAnalyzer;

    private static final int PERMISSION_CODE_STORAGE = 1;
    String[] permissionRequestStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE = 11;

    private static final int PERMISSION_CODE_CAMERA = 2;
    String[] permissionRequestCamera = {Manifest.permission.CAMERA};

    @Nullable
    @BindView(R.id.iv_faceDetection)
    ImageView imageViewFaceDetection;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_face_detection);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        setDefaultFaceAnalyzer();

        // this is important for get image of imageView by Bitmap and use with localAnalyzerWithImage
        imageViewFaceDetection.setDrawingCacheEnabled(true);

    }


    @OnClick({R.id.btn_faceDetectionWithImage, R.id.btn_faceDetectionStorage, R.id.btn_faceDetectionWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_faceDetectionWithImage:
                Bitmap bitmap = imageViewFaceDetection.getDrawingCache();
                localFaceAnalyzerWithImage(bitmap, false);
                break;
            case R.id.btn_faceDetectionStorage:
                ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE);
                break;
            case R.id.btn_faceDetectionWithCameraStream:
                ActivityCompat.requestPermissions(this, permissionRequestCamera, PERMISSION_CODE_CAMERA);
                break;
            default:
                Log.i(TAG, "Default");
        }
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_fbrs_fd));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use FaceDetection Analyzer without Storage Permission!",
                        "YES GO", "CANCEL");
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> Start Intent FaceDetectionActivity");
                Utils.startActivity(FaceDetectionActivity.this, FaceDetectionCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icons_switch_camera_black,
                        "You can not use FaceDetection with Stream without Camera Permission!",
                        "YES GO", "CANCEL");
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (resultCode == 0) {
            Log.w(TAG, "onActivityResult : onActivityResult No any data detected");
            Utils.showToastMessage(getApplicationContext(), "onActivityResult No any data detected");
        } else {

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    Log.i(TAG, "onActivityResult : activityIntentCodeStorage calling with MediaStore.Images.Media.getBitmap bitmap : " + bitmap.getConfig());
                    localFaceAnalyzerWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                }

            }
        }
    }





    /* ------------------------------------------------------------------------------------------ */
    //region Face Detections Process

    public void setDefaultFaceAnalyzer() {
        // Method 2: Use the default parameter settings.
        faceAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer();
    }

    /**
     * Create a MLFaceAnalyzer with edited setting parameters
     * calls asyncAnalyseFrame Task and gets success or failure listener results of face detection
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void localFaceAnalyzerWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        showProgress();
        // Method 1: Use customized parameter settings.
        // If the Full SDK mode is used for integration, set parameters based on the integrated model package.
        MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                // Set whether to detect key face points.
                .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                // Set whether to detect facial features.
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                // Enable only facial expression detection and gender detection.
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURE_AGE |
                        MLFaceAnalyzerSetting.TYPE_FEATURE_EMOTION |
                        MLFaceAnalyzerSetting.TYPE_FEATURE_GENDAR)
                // Set whether to detect face contour points.
                .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                // Set whether to enable face tracking and specify the fast tracking mode.
                //.setTracingAllowed(true) // another usage
                .setTracingAllowed(true, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                // Set the speed and precision of the detector.
                .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                // Set whether to enable pose detection (enabled by default).
                .setPoseDisabled(true)
                .allowTracing()
                .create();

        faceAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);

        // Method 2: Use the default parameter settings.
        // This method can be used when the Lite SDK is used for integration.
        // The default parameters are key points, face contour, facial features,
        //   precision mode, and face tracking (disabled by default) for detection.
        // MLFaceAnalyzer analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer();

        // Create an MLFrame by using the bitmap.
        MLFrame frame = MLFrame.fromBitmap(bitmap);

        // The asynchronous call mode is used in preceding sample code.
        // Face detection also supports synchronous call of the analyseFrame function to obtain the detection result.

        Task<List<MLFace>> task = faceAnalyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(faces1 -> {
            // Detection success.
            Log.i(TAG, "asyncAnalyseFrame.onSuccess : faces.size : " + faces1.size());

            if (isFromGallery) {
                imageViewFaceDetection.setImageBitmap(bitmap);
            } else {
                imageViewFaceDetection.setImageResource(R.drawable.icon_face_carl_sagan);
            }
            // important for peek view from changed imageView resource
            imageViewFaceDetection.getDrawingCache();
            String successResultFaceSize = "FaceSize : " + faces1.size();
            String successResultFeatures = faces1.get(0).getFeatures().toString()
                    .replace("com.huawei.hms.mlsdk.face.", "")
                    .replace("},", "},\n");
            String successResultEmotions = faces1.get(0).getEmotions().toString()
                    .replace("com.huawei.hms.mlsdk.face.", "")
                    .replace("},", "},\n");

            displaySuccessAnalyseResults(successResultFaceSize + "\n\n" + successResultFeatures + "\n\n" + successResultEmotions);

        }).addOnFailureListener(e -> {
            // Detection failure.
            String errorMessage = e.getMessage();
            Log.e(TAG, "asyncAnalyseFrame.onFailure exc : " + errorMessage, e);
            try {
                int errorCode = ((MLException) e).getErrCode();
                String errorMsg = e.getMessage();
                errorMessage = "ERROR: " + errorCode + " : " + errorMsg;
            } catch (Exception ex) {
                Log.e(TAG, "asyncAnalyseFrame.onFailure (MLException) e).getErrCode() exc : " + ex.getMessage(), ex);
            }
            Log.e(TAG, "asyncAnalyseFrame.onFailure exc : " + errorMessage);
            displayFailureAnalyseResults(errorMessage);
        });

        try {
            if (faceAnalyzer != null) {
                faceAnalyzer.stop();
            }
        } catch (IOException e) {
            Log.e(TAG, "asyncAnalyseFrame faceAnalyzer.stop() exc : " + e.getMessage(), e);
        }
    }


    public String getGenderGuessFromFaceAnalyseFeature(int featureSexProbability) {
        String gender = "Male";
        if (featureSexProbability > 0.5) gender = "Female";
        return gender;
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations

    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Face Detection Success Results : " + text);
        resultLogs.setText("Face Detection Success Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Face Detection was Failed Results : " + msg);
        resultLogs.setText("Face Detection was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "Face Detection was Failed : \n" + msg);
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

        try {
            if (faceAnalyzer != null) {
                faceAnalyzer.stop();
            }
        } catch (IOException e) {
            Log.e(TAG, "onDestroy faceAnalyzer.stop() exc : " + e.getMessage(), e);
        }

    }

}