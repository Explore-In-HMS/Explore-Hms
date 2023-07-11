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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageClassification;

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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ImageClassificationGraphicLocal;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ImageClassificationGraphicRemote;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ImageClassificationTransactorLocal;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ImageClassificationTransactorRemote;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.classification.MLImageClassification;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImageClassificationActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ImageClassificationActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;


    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM = 3;
    String[] permissionRequestCameraAndStorageForStream = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private ImageClassificationTransactorLocal classificationTransactorLocal;
    private ImageClassificationTransactorRemote classificationTransactorRemote;

    boolean isRemoteAnalyze = false;


    private Uri takedImageUri;

    private static final int LIVE_OVERLAY = R.id.live_overlay;
    private static final int IV_IMAGE_CLASSIFICATION = R.id.iv_imageClassification;
    private static final int RESULT_LOGS = R.id.resultLogs;
    private static final int PROGRESS_BAR = R.id.progressBar;

    private static final int BTN_IMAGE_CLASSIFICATION_WITH_IMAGE = R.id.btn_imageClassificationWithImage;
    private static final int BTN_IMAGE_CLASSIFICATION_STORAGE = R.id.btn_imageClassificationStorage;
    private static final int BTN_IMAGE_CLASSIFICATION_WITH_TAKE_PICTURE = R.id.btn_imageClassificationWithTakeAPicture;
    private static final int BTN_IMAGE_CLASSIFICATION_WITH_CAMERA_STREAM = R.id.btn_imageClassificationWithCameraStream;
    private static final int IV_INFO = R.id.ivInfo;

    private static final String REQUEST_PERMISSION_MESSAGE = "Would You Like To Go To Permission Settings To Allow?";
    private static final String DIALOG_YES_MESSAGE = "YES GO";
    private static final String DIALOG_CANCEL_MESSAGE = "CANCEL";


    @Nullable
    @BindView(value = LIVE_OVERLAY)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(IV_IMAGE_CLASSIFICATION)
    ImageView imageViewClassification;

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
        setContentView(R.layout.activity_image_classification);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createClassificationTransactors();

        // this is important for get image of imageView by Bitmap and use with AnalyzeWithImage
        imageViewClassification.setDrawingCacheEnabled(true);

    }

    @OnClick({R.id.btn_imageClassificationWithImage, R.id.btn_imageClassificationStorage,
            R.id.btn_imageClassificationWithTakeAPicture, R.id.btn_imageClassificationWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case BTN_IMAGE_CLASSIFICATION_WITH_IMAGE:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewClassification.getDrawingCache();
                analyseClassificationWithImage(bitmap, false, isRemoteAnalyze);
                break;
            case BTN_IMAGE_CLASSIFICATION_STORAGE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case BTN_IMAGE_CLASSIFICATION_WITH_TAKE_PICTURE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case BTN_IMAGE_CLASSIFICATION_WITH_CAMERA_STREAM:
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_ic));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createClassificationTransactors() {
        classificationTransactorLocal = new ImageClassificationTransactorLocal(getApplicationContext());
        classificationTransactorRemote = new ImageClassificationTransactorRemote(getApplicationContext());
    }


    /**
     * Classification with Asynchronous method
     * <p>
     * The image classification service is suitable for highly-functional apps
     * that are capable of classifying and managing images with remarkable ease and precision.
     * If you have a photo gallery app, integrating it with the image classification service provides for a new layer of intelligence,
     * as images can be automatically sorted by category.
     *
     * @param bitmap
     * @param isFromGallery   : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     * @param isRemoteAnalyze : true = classification will make with remote analyzer, false = classification will make local analyzer
     */
    private void analyseClassificationWithImage(Bitmap bitmap, boolean isFromGallery, boolean isRemoteAnalyze) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewClassification.setImageBitmap(bitmap);
        } else {
            imageViewClassification.setImageResource(R.drawable.test_image_classification);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewObject size
        Bitmap takedImageBitmap = imageViewClassification.getDrawingCache();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(ImageClassificationActivity.this, bitmap);

        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();

        if (isRemoteAnalyze) {

            classificationTransactorRemote.detectInImage(frame)
                    .addOnSuccessListener(classificationResults -> {
                        if (classificationResults == null) {
                            Log.e(TAG, "analyseClassificationWithImage : remote : detectInImage results is NULL !");
                            return;
                        }
                        Log.e(TAG, "analyseClassificationWithImage : remote : classificationResults addOnSuccessListener : " + classificationResults.size());

                        if (classificationResults.size() > 0) {
                            graphicOverlay.clear();
                            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                            graphicOverlay.addGraphic(imageGraphic);

                            List<String> classificationList = new ArrayList<>();
                            for (int i = 0; i < classificationResults.size(); ++i) {
                                MLImageClassification classification = classificationResults.get(i);
                                if (classification.getName() != null) {
                                    classificationList.add(classification.getName());
                                }
                            }

                            ImageClassificationGraphicRemote objectGraphic = new ImageClassificationGraphicRemote(graphicOverlay, getApplicationContext(), classificationList);
                            graphicOverlay.addGraphic(objectGraphic);
                            graphicOverlay.postInvalidate();

                            imageViewClassification.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, graphicOverlay.getWidth(), graphicOverlay.getHeight()));

                            displaySuccessAnalyseResults(classificationList);

                        } else {
                            Utils.showToastMessage(getApplicationContext(), "No classification data, Detected Remote ClassificationResults size is zero!");
                            displayFailureAnalyseResults("No classification data, Detected Remote ClassificationResults size is zero!");
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
                            Log.e(TAG, "analyseClassificationWithImage.remoteTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
                        }
                        Log.e(TAG, "analyseClassificationWithImage.remoteTransactor.detectInImage.onFailure exc : " + errorMessage, e);

                        displayFailureAnalyseResults(errorMessage);

                        hideProgress();
                    });

            stopClassificationTransactorRemote();

        } else {

            classificationTransactorLocal.detectInImage(frame)
                    .addOnSuccessListener(classificationResults -> {
                        if (classificationResults == null) {
                            Log.e(TAG, "analyseClassificationWithImage : remote : detectInImage results is NULL !");
                            return;
                        }
                        Log.e(TAG, "analyseClassificationWithImage : remote : classificationResults addOnSuccessListener : " + classificationResults.size());

                        if (classificationResults.size() > 0) {
                            graphicOverlay.clear();
                            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                            graphicOverlay.addGraphic(imageGraphic);

                            List<String> classificationList = new ArrayList<>();
                            for (int i = 0; i < classificationResults.size(); ++i) {
                                MLImageClassification classification = classificationResults.get(i);
                                if (classification.getName() != null) {
                                    classificationList.add(classification.getName());
                                }
                            }

                            ImageClassificationGraphicLocal objectGraphic = new ImageClassificationGraphicLocal(graphicOverlay, getApplicationContext(), classificationResults);
                            graphicOverlay.addGraphic(objectGraphic);
                            graphicOverlay.postInvalidate();

                            imageViewClassification.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                            displaySuccessAnalyseResults(classificationList);

                        } else {
                            Utils.showToastMessage(getApplicationContext(), "No classification data, Detected Local ClassificationResults size is zero!");
                            displayFailureAnalyseResults("No classification data, Detected Local ClassificationResults size is zero!");
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
                            Log.e(TAG, "analyseClassificationWithImage.localTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
                        }
                        Log.e(TAG, "analyseClassificationWithImage.localTransactor.detectInImage.onFailure exc : " + errorMessage, e);

                        displayFailureAnalyseResults(errorMessage);

                        hideProgress();
                    });
            stopClassificationTransactorLocal();
        }
    }

    private void displaySuccessAnalyseResults(List<String> resultList) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);

        String resultMessage = "Image Classification Success Results : with " + resultList.size() + " classifications. :\n" + resultList.toString();

        Log.i(TAG, resultMessage);
        resultLogs.setText(resultMessage);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        String resultMessage = "Image Classification was Failed Results : \n" + msg;
        Log.e(TAG, resultMessage);
        resultLogs.setText(resultMessage);
        Utils.showToastMessage(getApplicationContext(), resultMessage);
    }


    private void clearLogs() {
        resultLogs.setText(R.string.classification_result_descriptions_will_be_here);
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
                        getString(R.string.need_storage_permission),
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Image Classification without Storage Permission!",
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
                String photoFileName = timeStamp + "_newClassificationPicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML Image Classification";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Image Classification Photo From Camera");

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
                        "You can not use Image Classification with Take a Picture without Camera And Storage Permission!",
                        DIALOG_YES_MESSAGE, DIALOG_CANCEL_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission ForDevice -> Start Custom Camera Capture Activity");
                Utils.startActivity(ImageClassificationActivity.this, ImageClassificationCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraAndStoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        REQUEST_PERMISSION_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Image Classification with Camera Stream without Camera And Storage Permission!",
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
                    analyseClassificationWithImage(bitmap, true, isRemoteAnalyze);
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
                    analyseClassificationWithImage(bitmap, true, isRemoteAnalyze);
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

        stopClassificationTransactorLocal();
        stopClassificationTransactorRemote();

    }

    public void stopClassificationTransactorLocal() {
        if (classificationTransactorLocal != null) {
            classificationTransactorLocal.stop();
        }
    }

    public void stopClassificationTransactorRemote() {
        if (classificationTransactorRemote != null) {
            classificationTransactorRemote.stop();
        }
    }

}