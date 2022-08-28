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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.objectDetection;

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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ObjectGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ObjectTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.objects.MLObject;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzer;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzerSetting;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ObjectDetectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ObjectDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;


    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM = 3;
    String[] permissionRequestCameraAndStorageForStream = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private MLObjectAnalyzer objectAnalyzer;

    private MLObjectAnalyzerSetting analyzerSetting;

    private ObjectTransactor objectTransactor;

    private Uri takedImageUri;

    private static final int LIVE_OVERLAY = R.id.live_overlay;
    private static final int IV_OBJECT_DETECTION = R.id.iv_objectDetection;
    private static final int RESULT_LOGS = R.id.resultLogs;
    private static final int PROGRESS_BAR = R.id.progressBar;

    private static final int BTN_OBJECT_DETECTION_WITH_IMAGE = R.id.btn_objectDetectionWithImage;
    private static final int BTN_OBJECT_DETECTION_STORAGE = R.id.btn_objectDetectionStorage;
    private static final int BTN_OBJECT_DETECTION_WITH_TAKE_PICTURE = R.id.btn_objectDetectionWithTakeAPicture;
    private static final int BTN_OBJECT_DETECTION_WITH_CAMERA_STREAM = R.id.btn_objectDetectionWithCameraStream;
    private static final int IV_INFO = R.id.ivInfo;

    private static final String REQUEST_PERMISSION_MESSAGE = "Would You Like To Go To Permission Settings To Allow?";
    private static final String DIALOG_YES_MESSAGE = "YES GO";
    private static final String DIALOG_CANCEL_MESSAGE = "CANCEL";

    @Nullable
    @BindView(LIVE_OVERLAY)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(IV_OBJECT_DETECTION)
    ImageView imageViewObject;

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
        setContentView(R.layout.activity_object_detection);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createObjectAnalyzerSetting();

        createObjectTransactor();

        // this is important for get image of imageView by Bitmap and use with AnalyzerWithImage
        imageViewObject.setDrawingCacheEnabled(true);

    }

    @OnClick({R.id.btn_objectDetectionWithImage, R.id.btn_objectDetectionStorage,
            R.id.btn_objectDetectionWithTakeAPicture, R.id.btn_objectDetectionWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case BTN_OBJECT_DETECTION_WITH_IMAGE:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewObject.getDrawingCache();
                analyseObjectWithImage(bitmap, false);
                break;
            case BTN_OBJECT_DETECTION_STORAGE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case BTN_OBJECT_DETECTION_WITH_TAKE_PICTURE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case BTN_OBJECT_DETECTION_WITH_CAMERA_STREAM:
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_od));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createObjectTransactor() {
        objectTransactor = new ObjectTransactor(analyzerSetting);
    }

    private void createObjectAnalyzerSetting() {
        analyzerSetting = new MLObjectAnalyzerSetting.Factory()
                .setAnalyzerType(MLObjectAnalyzerSetting.TYPE_VIDEO)
                .allowMultiResults()
                .allowClassification()
                .create();
    }


    /**
     * Detection with Asynchronous method
     * <p>
     * In addition to the object boundaries. information of each object border,
     * Objects that are incorrectly recognized can be filtered out based on the confidence values.
     * In actual scenarios, a threshold can be flexibly set based on tolerance of misRecognition.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseObjectWithImage(Bitmap bitmap, boolean isFromGallery) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewObject.setImageBitmap(bitmap);
        } else {
            imageViewObject.setImageResource(R.drawable.icon_human_jumped);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewObject size
        Bitmap takedImageBitmap = imageViewObject.getDrawingCache();

        takedImageUri = BitmapUtils.getImageUriFromBitmap(ObjectDetectionActivity.this, bitmap);

        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();

        objectTransactor.detectInImage(frame)
                .addOnSuccessListener(objectResults -> {
                    if (objectResults == null) {
                        Log.e(TAG, "analyseObjectWithImage : detectInImage results is NULL !");
                        return;
                    }
                    Log.e(TAG, "analyseObjectWithImage : TODO : check it out for empty MLObjectResults from objectTransactor addOnSuccessListener : " + objectResults.size());

                    if (objectResults.size() > 0) {
                        graphicOverlay.clear();
                        CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                        graphicOverlay.addGraphic(imageGraphic);

                        ObjectGraphic objectGraphic = new ObjectGraphic(graphicOverlay, (MLObject) objectResults);
                        graphicOverlay.addGraphic(objectGraphic);
                        graphicOverlay.postInvalidate();

                        imageViewObject.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                        displaySuccessAnalyseResults(objectResults.toString());

                    } else {
                        Utils.showToastMessage(getApplicationContext(), "No object data, Detected ObjectResult size is zero!");
                        displayFailureAnalyseResults("No object data, Detected ObjectResult size is zero!");
                    }

                    hideProgress();

                })
                .addOnFailureListener(e -> {
                    // Detection failure.
                    String errorMessage = e.getMessage();
                    try {
                        int errorCode = ((MLException) e).getErrCode();
                        String errorMsg = e.getMessage();
                        errorMessage = "ERROR: " + errorCode + " : " + errorMsg;
                    } catch (Exception ex) {
                        Log.e(TAG, "analyseObjectWithImage.objectTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
                    }
                    Log.e(TAG, "analyseObjectWithImage.objectTransactor.detectInImage.onFailure exc : " + errorMessage, e);

                    displayFailureAnalyseResults(errorMessage);

                    hideProgress();

                });

        stopObjectTransactor();

    }

    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);

        String textResult = "Object Detection Success Results : with " + text.length() + " characters :\n" + text;

        Log.i(TAG, textResult);
        resultLogs.setText(textResult);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);

        String textResult = "Object Detection was Failed Results : \n" + msg;
        Log.e(TAG, textResult);
        resultLogs.setText(textResult);
        Utils.showToastMessage(getApplicationContext(), textResult);
    }


    private void clearLogs() {
        resultLogs.setText(getString(R.string.txt_for_object_detection_clear_logs_message));
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
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
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Object Detection Analyzer without Storage Permission!",
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
                String photoFileName = timeStamp + "_newObjectPicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML Object";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Object Detection Photo From Camera");

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
                        R.drawable.icons_switch_camera_black,
                        "You can not use Object Detection Analyzer with Take a Picture without Camera And Storage Permission!",
                        DIALOG_YES_MESSAGE, DIALOG_CANCEL_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission ForDevice -> Start Custom Camera Capture Activity");
                Utils.startActivity(ObjectDetectionActivity.this, ObjectDetectionCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraAndStoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Object Detection Analyzer with Camera Stream without Camera And Storage Permission!",
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
                    analyseObjectWithImage(bitmap, true);
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
                    analyseObjectWithImage(bitmap, true);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopObjectTransactor();
        stopObjectAnalyzer();
        unbinder.unbind();
    }

    public void stopObjectTransactor() {

        if (objectTransactor != null) {
            objectTransactor.stop();
        }
    }

    public void stopObjectAnalyzer() {

        try {
            if (objectAnalyzer != null) {
                objectAnalyzer.stop();
            }
        } catch (IOException e) {
            Log.i(TAG, "stop objectAnalyzer : Exception : " + e.getMessage(), e);
        }
    }

}