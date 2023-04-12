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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.landmarkRecognition;

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

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ImageClassificationGraphicRemote;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.LandmarkTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class LandmarkRecognitionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = LandmarkRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;


    private LandmarkTransactor landmarkTransactor;

    private Uri takedImageUri;

    String resultLandMark = "";


    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;

    @Nullable
    @BindView(R.id.iv_landmarkRecognition)
    ImageView imageViewLandmarkRecognition;

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
        setContentView(R.layout.activity_landmark_recognition);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createLandmarkRecognitionTransactors();

        // this is important for get image of imageView by Bitmap and use with AnalyzeWithImage
        imageViewLandmarkRecognition.setDrawingCacheEnabled(true);

        imageViewLandmarkRecognition.setOnClickListener(view -> {
            if (!resultLandMark.isEmpty())
                Utils.openWebPage(LandmarkRecognitionActivity.this, getResources().getString(R.string.link_google_query) + resultLandMark);
        });

    }


    @OnClick({R.id.btn_landmarkRecognitionWithImage, R.id.btn_landmarkRecognitionStorage,
            R.id.btn_landmarkRecognitionWithTakeAPicture})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_landmarkRecognitionWithImage:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewLandmarkRecognition.getDrawingCache();
                analyseRecognitionWithImage(bitmap, false);
                break;
            case R.id.btn_landmarkRecognitionStorage:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.btn_landmarkRecognitionWithTakeAPicture:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_lr));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void createLandmarkRecognitionTransactors() {
        landmarkTransactor = new LandmarkTransactor(this);
    }


    /**
     * Landmark recognition with Asynchronous method
     * <p>
     * Landmark recognition can be used in tourism scenarios.
     * In landmark recognition, the device calls the on-cloud API for detection
     * and the detection algorithm model runs on the cloud.
     * During commissioning and usage, ensure that the device can access the Internet.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseRecognitionWithImage(Bitmap bitmap, boolean isFromGallery) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewLandmarkRecognition.setImageBitmap(bitmap);
        } else {
            imageViewLandmarkRecognition.setImageResource(R.drawable.test_image_landmark_recognition);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewObject size
        Bitmap takedImageBitmap = imageViewLandmarkRecognition.getDrawingCache();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(LandmarkRecognitionActivity.this, bitmap);

        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();
        /*
         *
         * Create an MLFrame object using android.graphics.Bitmap.
         * JPG, JPEG, PNG, and BMP images are supported.
         * It is recommended that the image size be greater than or equal to 640 x 640 px.
         *
         */

        landmarkTransactor.detectInImage(frame)
                .addOnSuccessListener(recognitionResults -> {
                    if (recognitionResults == null) {
                        Log.e(TAG, "analyseRecognitionWithImage : landmarkTransactor results is NULL !");
                        return;
                    }
                    Log.e(TAG, "analyseRecognitionWithImage : recognitionResults addOnSuccessListener : " + recognitionResults.size());

                    if (recognitionResults.size() > 0) {
                        graphicOverlay.clear();
                        CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                        graphicOverlay.addGraphic(imageGraphic);

                        List<String> landmarkList = new ArrayList<>();
                        for (int i = 0; i < recognitionResults.size(); ++i) {
                            MLRemoteLandmark recognition = recognitionResults.get(i);
                            Log.e(TAG, "recognition : " + i + " : " + recognition.getLandmarkIdentity() + " " + recognition.getLandmark());
                            if (recognition.getLandmark() != null) {
                                landmarkList.add(recognition.getLandmark());
                                resultLandMark = recognition.getLandmark();
                            }
                        }

                        ImageClassificationGraphicRemote objectGraphic = new ImageClassificationGraphicRemote(graphicOverlay, getApplicationContext(), landmarkList);
                        graphicOverlay.addGraphic(objectGraphic);
                        graphicOverlay.postInvalidate();

                        imageViewLandmarkRecognition.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                        displaySuccessAnalyseResults(landmarkList);

                    } else {
                        Utils.showToastMessage(getApplicationContext(), "No Landmark data, Recognized Remote LandmarkResults size is zero!");
                        displayFailureAnalyseResults("No Landmark data, Recognized Remote LandmarkResults size is zero!");
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
                        Log.e(TAG, "analyseRecognitionWithImage.landmarkTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
                    }
                    Log.e(TAG, "analyseRecognitionWithImage.landmarkTransactor.detectInImage.onFailure exc : " + errorMessage, e);

                    displayFailureAnalyseResults(errorMessage);

                    hideProgress();
                });

        stopRecognitionTransactor();

    }


    private void displaySuccessAnalyseResults(List<String> resultList) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Landmark Recognition Success Results : " + resultList.size() + " landmarks. :\n" + resultList.toString());
        resultLogs.setText("Landmark Recognition Success Results : with " + resultList.size() + " landmarks. :\n" + resultList.toString());
    }

    private void displayFailureAnalyseResults(String msg) {
        resultLandMark = "";
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Landmark Recognition was Failed Results : " + msg);
        resultLogs.setText("Landmark Recognition was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "Landmark Recognition was Failed : \n" + msg);
    }


    private void clearLogs() {
        resultLandMark = "";
        resultLogs.setText(R.string.recognition_result_descriptions_will_be_here);
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
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use Landmark Recognition without Storage Permission!",
                        "YES GO", "CANCEL");
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");

                // check it out for is that working properly?
                // set taken photo name with timestamp and location
                @SuppressLint("SimpleDateFormat")
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String photoFileName = timeStamp + "_newRecognitipnPicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML Landmark Recognition";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Landmark Recognition Photo From Camera");

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
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icons_switch_camera_black,
                        "You can not use Landmark Recognition with Take a Picture without Camera And Storage Permission!",
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
            hideProgress();
        } else {

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    analyseRecognitionWithImage(bitmap, true);
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
                    analyseRecognitionWithImage(bitmap, true);
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
        unbinder.unbind();
        stopRecognitionTransactor();
    }

    public void stopRecognitionTransactor() {
        if (landmarkTransactor != null) {
            landmarkTransactor.stop();
        }
    }


}