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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.handKeyPointDetection;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.HandKeyPointGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.HandKeypointTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HandKeyPointDetectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = HandKeyPointDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private HandKeypointTransactor handKeypointTransactor;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;


    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM = 3;
    String[] permissionRequestCameraAndStorageForStream = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    private Uri takedImageUri;
    boolean isLandScape;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;

    @Nullable
    @BindView(R.id.iv_handKeyPointDetection)
    ImageView imageViewHand;

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
        setContentView(R.layout.activity_hand_key_point_detection);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        // this is important for get image of imageView by Bitmap and use with AnalyzerWithImage
        imageViewHand.setDrawingCacheEnabled(true);

        createHandKeyPointTransactor();

        isLandScape = (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

    }


    @OnClick({R.id.btn_handKeyPointDetectionWithImage, R.id.btn_handKeyPointDetectionStorage,
            R.id.btn_handKeyPointDetectionWithTakeAPicture, R.id.btn_handKeyPointDetectionWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_handKeyPointDetectionWithImage:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewHand.getDrawingCache();
                analyseHandKeyPointByAsyncWithImage(bitmap, false);
                break;
            case R.id.btn_handKeyPointDetectionStorage:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.btn_handKeyPointDetectionWithTakeAPicture:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case R.id.btn_handKeyPointDetectionWithCameraStream:
                clearLogs();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForStream, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_fbrs_hd));
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
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not use Hand KeyPoint Detection Analyzer without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");


                //  check it out for is that working properly?
                // set taken photo name with timestamp and location
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String photoFileName = timeStamp + "_newHandPicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML HandKeyPoint";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Hand Detection Photo From Camera");
                    takedImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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
                        getString(R.string.camera_and_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icons_switch_camera_black,
                        "You can not use Hand KeyPoint Detection Analyzer with Take a Picture without Camera And Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission ForDevice -> Start Custom Camera Capture Activity");
                Utils.startActivity(HandKeyPointDetectionActivity.this, HandKeyPointDetectionCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraAndStoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.camera_and_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not use Hand KeyPoint Detection Analyzer with Camera Stream without Camera And Storage Permission!",
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
            Utils.showToastMessage(getApplicationContext(), "onActivityResult No any data detected");
            hideProgress();
        } else {

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                    analyseHandKeyPointByAsyncWithImage(bitmap, true);

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

                    analyseHandKeyPointByAsyncWithImage(bitmap, true);

                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }

            }
        }
    }


    /* ------------------------------------------------------------------------------------------ */
    //region HandKeyPoint Detections Process


    private void createHandKeyPointTransactor() {
        handKeypointTransactor = new HandKeypointTransactor();
    }

    /**
     * Detection with Asynchronous method
     * <p>
     * he coordinate information of each hand keypoint, the detection result includes a confidence value of each keypoint.
     * Hand keypoints that are incorrectly recognized can be filtered out based on the confidence values.
     * In actual scenarios, a threshold can be flexibly set based on tolerance of misrecognition.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseHandKeyPointByAsyncWithImage(final Bitmap bitmap, final boolean isFromGallery) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewHand.setImageBitmap(bitmap);
        } else {
            imageViewHand.setImageResource(R.drawable.icon_hand_tatooed);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewHands size
        Bitmap takedImageBitmap = imageViewHand.getDrawingCache();

        takedImageUri = BitmapUtils.getImageUriFromBitmap(HandKeyPointDetectionActivity.this, bitmap);

        // Create an MLFrame object using the bitmap.
        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();

        handKeypointTransactor.detectInImage(frame).addOnSuccessListener(handResults -> {
            if (handResults == null) {
                Log.e(TAG, "analyseHandKeyPointWithImage : detectInImage : results is NULL !");
                return;
            }
            if (handResults.size() > 0) {
                graphicOverlay.clear();
                CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                graphicOverlay.addGraphic(imageGraphic);

                HandKeyPointGraphic handKeyPointGraphic = new HandKeyPointGraphic(graphicOverlay, handResults);
                graphicOverlay.addGraphic(handKeyPointGraphic);
                graphicOverlay.postInvalidate();

                imageViewHand.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                displaySuccessAnalyseResults(handResults.toString());

            } else {
                Utils.showToastMessage(getApplicationContext(), "No HandKeyPoint data, Template creation Failed!");
                displayFailureAnalyseResults("No HandKeyPoint data, Template creation Failed!");
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
                Log.e(TAG, "analyseHandKeyPointWithImage : detectInImage : onFailure (MLException) errCode : " + ex.getMessage(), ex);
            }
            Log.e(TAG, "analyseHandKeyPointWithImage : detectInImage : onFailure exc : " + errorMessage);
            displayFailureAnalyseResults(errorMessage);

            hideProgress();

        });

        stopHandKeyPointTransactor();

    }


    /**
     * Detection with Synchronous method
     * <p>
     * The coordinate information of each hand keypoint, the detection result includes a confidence value of each keypoint.
     * Hand keypoints that are incorrectly recognized can be filtered out based on the confidence values.
     * In actual scenarios, a threshold can be flexibly set based on tolerance of misrecognition.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseHandKeyPointBySyncWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        // Create an MLFrame object using the bitmap.
        MLFrame frame = MLFrame.fromBitmap(bitmap);
        SparseArray<MLHandKeypoints> mlHandKeypointsSparseArray = createHandKeyPointAnalyzer().analyseFrame(frame);
        for (int i = 0; i < mlHandKeypointsSparseArray.size(); i++) {
            // Process the detection result.
            Log.i(TAG, "HandKeyPoint mlHandKeypointsSparseArray : " + mlHandKeypointsSparseArray.toString());

            //write and add graphic overlay
            displaySuccessAnalyseResults(mlHandKeypointsSparseArray.toString());

        }
    }

    private MLHandKeypointAnalyzer createHandKeyPointAnalyzer() {
        MLHandKeypointAnalyzerSetting setting = new MLHandKeypointAnalyzerSetting.Factory()
                // MLHandKeypointAnalyzerSetting.TYPE_ALL indicates that all results are returned.
                // MLHandKeypointAnalyzerSetting.TYPE_KEYPOINT_ONLY indicates that only hand keypoint information is returned.
                // MLHandKeypointAnalyzerSetting.TYPE_RECT_ONLY indicates that only palm information is returned.
                .setSceneType(MLHandKeypointAnalyzerSetting.TYPE_ALL)
                // Set the maximum number of hand regions that can be detected in an image.
                // By default, a maximum of 10 hand regions can be detected.
                .setMaxHandResults(2)
                .create();
        return MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer(setting);

    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "HandKeyPoint Detection Success Results : " + text);
        resultLogs.setText("HandKeyPoint Detection Success Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "HandKeyPoint Detection was Failed Results : " + msg);
        resultLogs.setText("HandKeyPoint Detection was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "HandKeyPoint Detection was Failed : \n" + msg);
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
        stopHandKeyPointTransactor();

    }

    public void stopHandKeyPointTransactor() {
        if (handKeypointTransactor != null) {
            handKeypointTransactor.stop();
        }
    }

}