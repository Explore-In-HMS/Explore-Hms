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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.livenessDetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.FaceDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureError;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult;

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

public class LivenessDetectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = FaceDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;


    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE = 1;
    String[] permissionRequestCameraAndStorage = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE = 11;

    @Nullable
    @BindView(R.id.iv_livenessDetection)
    ImageView ivDetectedPhoto;

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
        setContentView(R.layout.activity_liveness_detection);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

    }


    @OnClick({R.id.btn_livenessDetectionWithCameraStream, R.id.iv_livenessDetection,R.id.btn_livenessDetectionWithMask})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_livenessDetectionWithCameraStream:
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorage, PERMISSION_CODE_CAMERA_AND_STORAGE);
                break;
            case R.id.iv_livenessDetection:
                //  image peek view
                break;
            case R.id.btn_livenessDetectionWithMask:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forums.developer.huawei.com/forumPortal/en/topic/0202468831327600100"));
                startActivity(browserIntent);
            default:
                Log.i(TAG, "Default");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_fbrs_ld));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    /* ------------------------------------------------------------------------------------------ */
    //region Liveness Detections Process

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
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> call startLivenessDetection");
                startLivenessDetection();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission  was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA and STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use LivenessDetection without Camera And Storage Permission!",
                        "YES GO", "CANCEL");

            }
        }
    }


    private void startLivenessDetection() {
        MLLivenessCapture capture = MLLivenessCapture.getInstance();
        Log.i(TAG, "MLLivenessCapture startDetect...");
        try {
            capture.startDetect(this, livenessDetectionCallback);
        } catch (Exception e) {
            Log.e(TAG, "MLLivenessCapture startDetect Exception : " + e.getMessage(), e);
            displayFailureAnalyseResults(e.getMessage());
        }
    }

    private MLLivenessCapture.Callback livenessDetectionCallback = new MLLivenessCapture.Callback() {
        @Override
        public void onSuccess(MLLivenessCaptureResult result) {
            // Processing logic when the detection is successful. The detection result indicates whether the face is of a real user.
            Log.i(TAG, "MLLivenessCapture onSuccess ..." + result.toString());
            hideProgress();
            Utils.createVibration(getApplicationContext(), 200);
            Log.i(TAG, "Liveness Detection Success Results : " + result.toString());
            resultLogs.setText(getString(R.string.liveness_detection_success_results) + result.toString());
            if (result.getBitmap() == null) {
                ivDetectedPhoto.setImageResource(R.drawable.icon_liveness);
                resultLogs.setText("Liveness Detection Success Results but this not have bitmap image ! : \n" + result.toString());
            } else {
                ivDetectedPhoto.setImageBitmap(result.getBitmap());
                resultLogs.setText(getString(R.string.liveness_detection_success_results) + result.toString());
            }
        }

        @Override
        public void onFailure(int errorCode) {
            // Processing logic when the detection fails. For example, the camera is abnormal (CAMERA_ERROR).
            String errorDetail = getDetailErrorPrompt(errorCode);
            if (errorDetail.isEmpty()) {
                errorDetail = "MLLivenessCapture onError ";
            }
            Log.i(TAG, "MLLivenessCapture onError : errorCode : " + errorCode + " errorDetail : " + errorDetail);
            displayFailureAnalyseResults(errorDetail);
        }

        private String getDetailErrorPrompt(int errorCode) {
            switch (errorCode) {
                case MLLivenessCaptureError.CAMERA_NO_PERMISSION:
                    return "ERROR: 11401 Camera Permision";
                case MLLivenessCaptureError.CAMERA_START_FAILED:
                    return "ERROR: 11402 Camera Start Failed";
                case MLLivenessCaptureError.USER_CANCEL:
                    return "ERROR: 11403 User Cancel";
                default:
                    return "";
            }
        }

    };

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Liveness Detection Failed Results : " + msg);
        resultLogs.setText("Liveness Detection was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "Liveness Detection was Failed : \n" + msg);
    }


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
    }

    // check it out for not invoked onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK || intent == null) {
            return;
        }
        Log.i(TAG, "onActivityResult requestCode " + requestCode + ", resultCode " + resultCode + " : " + intent.getData().toString());

        if (requestCode == ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE) {
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), intent.getData());
                Log.i(TAG, "onActivityResult : activityIntentCodeCameraAndStorage getBitmap intent data : " + bitmap.getConfig());
                ivDetectedPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.e(TAG, "onActivityResult IOException for getBitmap with data.getData : " + e.getMessage(), e);
                Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
            }
        }
    }


}