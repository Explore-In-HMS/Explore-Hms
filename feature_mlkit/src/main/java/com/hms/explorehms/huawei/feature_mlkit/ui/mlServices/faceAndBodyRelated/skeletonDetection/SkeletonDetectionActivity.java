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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.skeletonDetection;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.SkeletonGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.SkeletonTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SkeletonDetectionActivity extends AppCompatActivity {

    private static final String PERMISSION_SETTINGS_TO_ALLOW = "Would You Like To Go To Permission Settings To Allow?";

    private static final String YES_GO_MESSAGE = "YES GO";
    private static final String CANCEL_MESSAGE = "CANCEL";

    //region variablesAndObjects
    private static final String TAG = SkeletonDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;

    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM = 3;

    String[] permissionRequestCameraAndStorageForStream = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private MLSkeletonAnalyzer skeletonAnalyzer;

    private MLSkeletonAnalyzerSetting analyzerSetting;

    private SkeletonTransactor skeletonTransactor;

    private boolean isYogaChecked;

    private Uri takedImageUri;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(R.id.iv_skeletonDetection)
    ImageView imageViewSkeleton;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.switch_button_yoga)
    Switch switchyoga;
    //endregion variablesAndObjects


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_skeleton_detection);
        isYogaChecked=false;
        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createSkeletonAnalyzerSetting();

        createSkeletonTransactor();

        // this is important for get image of imageView by Bitmap and use with AnalyzerWithImage
        imageViewSkeleton.setDrawingCacheEnabled(true);
        switchyoga.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              isYogaChecked=true;
                createSkeletonAnalyzerSetting();

            }
        });

    }


    @OnClick({R.id.btn_skeletonDetectionWithImage, R.id.btn_skeletonDetectionStorage,
            R.id.btn_skeletonDetectionWithTakeAPicture, R.id.btn_skeletonDetectionWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_skeletonDetectionWithImage:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewSkeleton.getDrawingCache();
                analyseSkeletonWithImage(bitmap, false);
                break;
            case R.id.btn_skeletonDetectionStorage:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.btn_skeletonDetectionWithTakeAPicture:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case R.id.btn_skeletonDetectionWithCameraStream:
                clearLogs();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForStream, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_fbrs_skld));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE);
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        PERMISSION_SETTINGS_TO_ALLOW,
                        R.drawable.icon_folder,
                        "You can not use Skeleton Detection Analyzer without Storage Permission!",
                        YES_GO_MESSAGE, CANCEL_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");

                // set taken photo name with timestamp and location
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String photoFileName = timeStamp + "_newSkeletonPicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML Skeleton";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Skeleton Detection Photo From Camera");

                    takedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Log.i(TAG, "onRequestPermissionsResult takedImageUri --------> : " + takedImageUri);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takedImageUri);
                    this.startActivityForResult(takePictureIntent, ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                } else {
                    Log.i(TAG, "takePictureIntent.resolveActivity( this.getPackageManager()) is NULL");
                }
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        PERMISSION_SETTINGS_TO_ALLOW,
                        R.drawable.icons_switch_camera_black,
                        "You can not use Skeleton Detection Analyzer with Take a Picture without Camera And Storage Permission!",
                        YES_GO_MESSAGE, CANCEL_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission ForDevice -> Start Custom Camera Capture Activity");
                Utils.startActivity(SkeletonDetectionActivity.this, SkeletonDetectionCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraAndStoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        PERMISSION_SETTINGS_TO_ALLOW,
                        R.drawable.icon_folder,
                        "You can not use Skeleton Detection Analyzer with Camera Stream without Camera And Storage Permission!",
                        YES_GO_MESSAGE, CANCEL_MESSAGE);
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
            hideProgress();
        } else {

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    analyseSkeletonWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult activityIntentCodeStorageForSelectImage IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
            if (requestCode == ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
                Bitmap bitmap;
                try {
                    Log.i(TAG, "onActivityResult takedImageUri --------> " + takedImageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), takedImageUri);
                    analyseSkeletonWithImage(bitmap, true);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
        }
    }

    private void createSkeletonTransactor() {

        skeletonTransactor = new SkeletonTransactor(analyzerSetting, this);
    }

    private void createSkeletonAnalyzerSetting() {
        // Method 1: Use customized parameter settings.
        if(isYogaChecked==true) {
            analyzerSetting = new MLSkeletonAnalyzerSetting.Factory()
                    // Set the detection mode.
                    // MLSkeletonAnalyzerSetting.TYPE_NORMAL: Detect skeleton points for normal postures.
                    // MLSkeletonAnalyzerSetting.TYPE_YOGA: Detect skeleton points for yoga postures.
                    .setAnalyzerType(MLSkeletonAnalyzerSetting.TYPE_YOGA)
                    .create();
        }
        else{
            analyzerSetting = new MLSkeletonAnalyzerSetting.Factory()
                    // Set the detection mode.
                    // MLSkeletonAnalyzerSetting.TYPE_NORMAL: Detect skeleton points for normal postures.
                    // MLSkeletonAnalyzerSetting.TYPE_YOGA: Detect skeleton points for yoga postures.
                    .setAnalyzerType(MLSkeletonAnalyzerSetting.TYPE_NORMAL)
                    .create();
        }
    }

    /**
     * Detection with Asynchronous method
     * <p>
     * In addition to the coordinate information of each skeleton point,
     * the detection result includes a confidence value of each point.
     * Skeleton points that are incorrectly recognized can be filtered out based on the confidence values.
     * In actual scenarios, a threshold can be flexibly set based on tolerance of misRecognition.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseSkeletonWithImage(Bitmap bitmap, boolean isFromGallery) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewSkeleton.setImageBitmap(bitmap);
        } else {
            imageViewSkeleton.setImageResource(R.drawable.icon_human_jumped);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewSkeleton size
        Bitmap takedImageBitmap = imageViewSkeleton.getDrawingCache();

        takedImageUri = BitmapUtils.getImageUriFromBitmap(SkeletonDetectionActivity.this, bitmap);

        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();

        skeletonTransactor.detectInImage(frame).addOnSuccessListener(skeletonResults -> {
            if (skeletonResults == null) {
                Log.e(TAG, "analyseSkeletonWithImage : detectInImage results is NULL !");
                return;
            }
            if (skeletonResults.size() > 0) {
                graphicOverlay.clear();
                CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                graphicOverlay.addGraphic(imageGraphic);

                SkeletonGraphic skeletonGraphic = new SkeletonGraphic(graphicOverlay, skeletonResults);
                graphicOverlay.addGraphic(skeletonGraphic);
                graphicOverlay.postInvalidate();

                imageViewSkeleton.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                displaySuccessAnalyseResults(skeletonResults.toString());

            } else {
                Utils.showToastMessage(getApplicationContext(), "No skeleton data, Template creation Failed!");
                displayFailureAnalyseResults("No skeleton data, Template creation Failed!");
            }

            hideProgress();

        }).addOnFailureListener(e -> {
            // Detection failure.
            String errorMessage = e.getMessage();
            try {
                int errorCode = ((MLException) e).getErrCode();
                String errorMsg = e.getMessage();
                errorMessage = "ERROR: " + errorCode + " : " + errorMsg;
            } catch (Exception ex) {
                Log.e(TAG, "analyseSkeletonWithImage.skeletonTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
            }
            Log.e(TAG, "analyseSkeletonWithImage.skeletonTransactor.detectInImage.onFailure exc : " + errorMessage, e);

            displayFailureAnalyseResults(errorMessage);

            hideProgress();
        });
        stopSkeletonTransactor();
    }

    private void createSkeletonAnalyzer() {
        // Method 1: Use customized parameter settings.
        // Method 2: Use default parameter settings. The default mode is to detect skeleton points for normal postures.
        skeletonAnalyzer = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer();
    }


    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Skeleton Detection Success Results : " + text);
        resultLogs.setText("Skeleton Detection Success Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Skeleton Detection was Failed Results : " + msg);
        resultLogs.setText("Skeleton Detection was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "Skeleton Detection was Failed : \n" + msg);
    }


    private void clearLogs() {
        resultLogs.setText("Detection Result Descriptions Will Be Here!");
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

        stopSkeletonTransactor();

        stopSkeletonAnalyzer();
    }

    public void stopSkeletonTransactor() {

        if (skeletonTransactor != null) {
            skeletonTransactor.stop();
        }
    }

    public void stopSkeletonAnalyzer() {

        try {
            if (skeletonAnalyzer != null) {
                skeletonAnalyzer.stop();
            }
        } catch (IOException e) {
            Log.i(TAG, "stopSkeletonAnalyzer : Exception : " + e.getMessage(), e);
        }
    }


}