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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.sceneDetection;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.SceneDetectionGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.SceneDetectionTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.scd.MLSceneDetection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SceneDetectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = SceneDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private SceneDetectionTransactor sceneDetectionTransactor;

    private Uri takedImageUri;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;

    private static final int PERMISSION_CODE_CAMERA_FOR_STREAM = 3;
    String[] permissionRequestCameraForStream = {Manifest.permission.CAMERA};

    private static final int LIVE_OVERLAY = R.id.live_overlay;
    private static final int IMAGE_VIEW_SCENE_DETECTION = R.id.imageView_sceneDetection;
    private static final int RESULT_LOGS = R.id.resultLogs;
    private static final int PROGRESS_BAR = R.id.progressBar;

    private static final int BTN_SCENE_DETECTION_WITH_IMAGE = R.id.btn_sceneDetectionWithImage;
    private static final int BTN_SCENE_DETECTION_WITH_STORAGE = R.id.btn_sceneDetectionWithStorage;
    private static final int BTN_SCENE_DETECTION_WITH_TAKE_PICTURE = R.id.btn_sceneDetectionWithTakeAPicture;
    private static final int BTN_SCENE_DETECTION_WITH_CAMERA_STREAM = R.id.btn_sceneDetectionWithCameraStream;
    private static final int IV_INFO = R.id.ivInfo;

    private static final String REQUEST_PERMISSION_MESSAGE = "Would You Like To Go To Permission Settings To Allow?";
    private static final String DIALOG_YES_MESSAGE = "YES GO";
    private static final String DIALOG_CANCEL_MESSAGE = "CANCEL";

    @Nullable
    @BindView(LIVE_OVERLAY)
    GraphicOverlay graphicOverlay;

    @Nullable
    @BindView(IMAGE_VIEW_SCENE_DETECTION)
    ImageView imageViewSceneDetection;

    @Nullable
    @BindView(RESULT_LOGS)
    TextView resultLogs;

    @Nullable
    @BindView(PROGRESS_BAR)
    ProgressBar progressBar;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_scene_detection);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createSceneDetectionTransactor();

        // this is important for get image of imageView by Bitmap and use with AnalyzeWithImage
        imageViewSceneDetection.setDrawingCacheEnabled(true);

    }

    @OnClick({R.id.btn_sceneDetectionWithImage, R.id.btn_sceneDetectionWithStorage,
            R.id.btn_sceneDetectionWithTakeAPicture, R.id.btn_sceneDetectionWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case BTN_SCENE_DETECTION_WITH_IMAGE:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewSceneDetection.getDrawingCache();
                analyseSceneDetectionWithImage(bitmap, false);
                break;
            case BTN_SCENE_DETECTION_WITH_STORAGE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case BTN_SCENE_DETECTION_WITH_TAKE_PICTURE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case BTN_SCENE_DETECTION_WITH_CAMERA_STREAM:
                clearLogs();
                ActivityCompat.requestPermissions(this, permissionRequestCameraForStream, PERMISSION_CODE_CAMERA_FOR_STREAM);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_sd));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createSceneDetectionTransactor() {
        float confidenceMaxResult = 50.0f;
        Log.d(TAG, "createSceneDetectionTransactor : confidenceMaxResult : " + confidenceMaxResult);
        // You can change confidence parameter
        sceneDetectionTransactor = new SceneDetectionTransactor(getApplicationContext(), "");
    }


    /**
     * Detection with Asynchronous method
     * <p>
     * The scene detection service can classify the scenario content of images and add labels,
     * such as outdoor scenery, indoor places, and buildings, to help understand the image content.
     * Based on the detected information, you can create more personalized app experience for users.
     * Currently, on-device detection on 102 scenarios is supported.
     * <p>
     * For details about the scenarios, please refer to this url
     * https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/ml-resource-0000001050038188-V5
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseSceneDetectionWithImage(Bitmap bitmap, boolean isFromGallery) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewSceneDetection.setImageBitmap(bitmap);
        } else {
            imageViewSceneDetection.setImageResource(R.drawable.test_image_scene_detection);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewObject size
        Bitmap takedImageBitmap = imageViewSceneDetection.getDrawingCache();

        takedImageUri = BitmapUtils.getImageUriFromBitmap(SceneDetectionActivity.this, bitmap);

        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();

        Log.d(TAG, "analyseSceneDetectionWithImage : TODO edit here : " + bitmap.getWidth() + "-" + bitmap.getHeight());

        sceneDetectionTransactor.detectInImage(frame)
                .addOnSuccessListener(sceneDetectionsResults -> {
                    if (sceneDetectionsResults == null) {
                        Log.e(TAG, "analyseSceneDetectionWithImage : remote : detectInImage results is NULL !");
                        return;
                    }
                    int sceneDetectionsSize = sceneDetectionsResults.size();
                    Log.e(TAG, "analyseSceneDetectionWithImage : remote : addOnSuccessListener sceneDetectionsSize : " + sceneDetectionsSize);

                    if (sceneDetectionsSize > 0) {
                        graphicOverlay.clear();
                        CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                        graphicOverlay.addGraphic(imageGraphic);

                        StringBuilder sceneDetectionResultMessage = new StringBuilder();

                        for (int i = 0; i < sceneDetectionsSize; ++i) {
                            MLSceneDetection scene = sceneDetectionsResults.get(i);

                            sceneDetectionResultMessage.append(i).append(". Scene : ").append(scene.getResult()).append(" - Confidence : ").append(scene.getConfidence()).append("\n");
                        }

                        SceneDetectionGraphic sceneGraphic = new SceneDetectionGraphic(graphicOverlay, getApplicationContext(), sceneDetectionsResults);
                        graphicOverlay.addGraphic(sceneGraphic);
                        graphicOverlay.postInvalidate();

                        imageViewSceneDetection.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                        String msg = "Scene Detection Success Results : " + sceneDetectionsSize + " scenes. :\n" + sceneDetectionResultMessage.toString();
                        displaySuccessAnalyseResults(msg);

                    } else {
                        Utils.showToastMessage(getApplicationContext(), "No Scene data, Detected Local sceneDetectionsResults size is zero!");
                        displayFailureAnalyseResults("No Scene data, Detected Local sceneDetectionsResults size is zero!");
                    }

                    hideProgress();

                })
                .addOnFailureListener(e -> {
                    String errorMessage = e.getMessage();
                    try {
                        int errorCode = ((MLException) e).getErrCode();
                        String errorMsg = e.getMessage();
                        errorMessage = "ERROR: " + errorCode + " : " + errorMsg;
                    } catch (Exception ex) {
                        Log.e(TAG, "analyseSceneDetectionWithImage.sceneDetectionTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
                    }
                    Log.e(TAG, "analyseSceneDetectionWithImage.sceneDetectionTransactor.detectInImage.onFailure exc : " + errorMessage, e);

                    displayFailureAnalyseResults(errorMessage);
                    hideProgress();
                });

        stopSceneDetectionTransactor();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

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
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Scene Detection without Storage Permission!",
                        DIALOG_YES_MESSAGE, DIALOG_CANCEL_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");

                // set taken photo name with timestamp and location
                @SuppressLint("SimpleDateFormat")
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String photoFileName = timeStamp + "_newScenePicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML Scene";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Scene Detection Photo From Camera");

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
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Scene Detection with Take a Picture without Camera And Storage Permission!",
                        DIALOG_YES_MESSAGE, DIALOG_CANCEL_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission ForDevice -> Start Custom Camera Capture Activity");
                Utils.startActivity(SceneDetectionActivity.this, SceneDetectionCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : Camera Permission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA PERMISSION",
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icons_switch_camera_black,
                        "You can not use Scene Detection with Camera Stream without Camera Permission!",
                        DIALOG_YES_MESSAGE, DIALOG_CANCEL_MESSAGE);
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
                    analyseSceneDetectionWithImage(bitmap, true);
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
                    analyseSceneDetectionWithImage(bitmap, true);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }

            }
        }
    }


    private void displaySuccessAnalyseResults(String result) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, result);
        resultLogs.setText(result);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        String displayMessage = "Scene Detection was Failed Results : \n" + msg;
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, displayMessage);
        resultLogs.setText(displayMessage);
        Utils.showToastMessage(getApplicationContext(), displayMessage);
    }


    private void clearLogs() {
        resultLogs.setText(R.string.detection_result_descriptions_will_be_here);
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

        stopSceneDetectionTransactor();

    }

    private void stopSceneDetectionTransactor() {
        if (sceneDetectionTransactor != null) {
            sceneDetectionTransactor.stop();
        }
    }

}