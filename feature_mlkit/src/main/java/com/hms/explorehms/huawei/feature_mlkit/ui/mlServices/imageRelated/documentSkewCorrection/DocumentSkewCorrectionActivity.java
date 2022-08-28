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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.documentSkewCorrection;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.landmarkRecognition.LandmarkRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionAnalyzer;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionAnalyzerFactory;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionAnalyzerSetting;
import com.huawei.hms.mlsdk.dsc.MLDocumentSkewCorrectionCoordinateInput;

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

public class DocumentSkewCorrectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = LandmarkRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private MLDocumentSkewCorrectionAnalyzer analyzer;

    private Uri takedImageUri;
    private Bitmap correctedImageBitmap;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;

    @Nullable
    @BindView(R.id.iv_documentCorrection)
    ImageView imageViewDocumentCorrection;

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
        setContentView(R.layout.activity_document_skew_correction);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        // this is important for get image of imageView by Bitmap and use with localAnalyzerWithImage
        imageViewDocumentCorrection.setDrawingCacheEnabled(true);
        imageViewDocumentCorrection.setOnClickListener(view -> {
            Bitmap bitmap = imageViewDocumentCorrection.getDrawingCache();
            DialogUtils.showDialogImagePeekView(
                    this,
                    getApplicationContext(),
                    bitmap
            );
        });

        createDocumentSkewCorrectionAnalyzer();

    }


    @OnClick({R.id.btn_documentCorrectionWithImage, R.id.btn_documentCorrectionWithStorage,
            R.id.btn_documentCorrectionWithTakeAPicture})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_documentCorrectionWithImage:
                showProgress();
                Bitmap bitmap = imageViewDocumentCorrection.getDrawingCache();
                analyseDocumentSkewDetectWithImage(bitmap, false);
                break;
            case R.id.btn_documentCorrectionWithStorage:
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.btn_documentCorrectionWithTakeAPicture:
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_dsc));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createDocumentSkewCorrectionAnalyzer() {
        MLDocumentSkewCorrectionAnalyzerSetting setting = new MLDocumentSkewCorrectionAnalyzerSetting.Factory().create();
        analyzer = MLDocumentSkewCorrectionAnalyzerFactory.getInstance().getDocumentSkewCorrectionAnalyzer(setting);
    }

    /**
     * DocumentSkewDetection with Asynchronous method
     * <p>
     * asyncDocumentSkewDetect asynchronous method or analyseFrame synchronous method to detect the text box.
     * When the return code is MLDocumentSkewCorrectionConstant.SUCCESS,
     * the coordinates of the four vertices of the text box are returned.
     * The coordinates are relative to the coordinates of the input image.
     * If the coordinates are inconsistent with those of the device, you need to convert the coordinates.
     * Otherwise, the returned data is meaningless.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseDocumentSkewDetectWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        if (isFromGallery) {
            imageViewDocumentCorrection.setImageBitmap(bitmap);
        } else {
            imageViewDocumentCorrection.setImageResource(R.drawable.test_image_document_skew_correction);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewHands size
        Bitmap takedImageBitmap = imageViewDocumentCorrection.getDrawingCache();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(DocumentSkewCorrectionActivity.this, bitmap);

        // Create an MLFrame object using the bitmap.
        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();
        // another method
        // MLFrame frame = MLFrame.fromBitmap(takedImageBitmap);

        analyzer.asyncDocumentSkewDetect(frame)
                .addOnSuccessListener(correctionResult -> {
                    Log.d(TAG, "DocumentSkewDetect onSuccess correctionResult  : " + correctionResult.toString());
                    if (correctionResult.getResultCode() != 0) {
                        correctedImageBitmap = null;
                        Log.d(TAG, "DocumentSkewDetect onSuccess but Result Failed : The picture does not meet the requirements!");
                        Utils.showToastMessage(this, "DocumentSkewDetect Failed :\nThe picture does not meet the requirements!");
                        displayFailureAnalyseResults("DocumentSkewDetect Failed :\nThe picture does not meet the requirements!");
                    } else {
                        Point leftTop = correctionResult.getLeftTopPosition();
                        Point rightTop = correctionResult.getRightTopPosition();
                        Point leftBottom = correctionResult.getLeftBottomPosition();
                        Point rightBottom = correctionResult.getRightBottomPosition();

                        List<Point> coordinates = new ArrayList<>();
                        coordinates.add(leftTop);
                        coordinates.add(rightTop);
                        coordinates.add(rightBottom);
                        coordinates.add(leftBottom);

                        MLDocumentSkewCorrectionCoordinateInput coordinateData = new MLDocumentSkewCorrectionCoordinateInput(coordinates);

                        displaySuccessAnalyseResults("Document Skew Correction Success Results : \n" +
                                "DocumentSkewDetect onSuccess : coordinates with :\n" +
                                "top : " + leftTop.toString() + "-" + leftTop.toString() + " /\n" +
                                "bottom : " + leftBottom.toString() + "-" + rightBottom.toString());

                        analyseDocumentSkewCorrectWithImage(frame, coordinateData);

                    }
                })
                .addOnFailureListener(e -> {
                    correctedImageBitmap = null;
                    Log.e(TAG, "DocumentSkewDetect onFailure : " + e.getMessage(), e);
                    Utils.showToastMessage(this, "DocumentSkewDetect Exception : " + e.getMessage());
                    displayFailureAnalyseResults("DocumentSkewDetect onFailure : " + e.getMessage());
                });
    }


    /**
     * DocumentSkewCorrection with Asynchronous method
     * <p>
     * After the detection is successful,
     * obtain the coordinate data of the four vertices in the text box,
     * use the upper left vertex as the start point, and add the upper left vertex,
     * upper right vertex, lower right vertex, and lower left vertex to the list (List<Point>) clockwise.
     * Finally, create an MLDocumentSkewCorrectionCoordinateInput object.
     *
     * @param frame       : DocSkewDetected result frame
     * @param coordinates : DocSkewDetected result MLDocumentSkewCorrectionCoordinateInput
     */
    private void analyseDocumentSkewCorrectWithImage(MLFrame frame, MLDocumentSkewCorrectionCoordinateInput coordinates) {

        try {
            analyzer.asyncDocumentSkewCorrect(frame, coordinates)
                    .addOnSuccessListener(refineResult -> {
                        if (refineResult != null && refineResult.getResultCode() == 0) {

                            correctedImageBitmap = refineResult.getCorrected();

                            imageViewDocumentCorrection.setImageBitmap(correctedImageBitmap);
                            // important for peek view from changed imageView resource
                            imageViewDocumentCorrection.getDrawingCache();

                            displaySuccessAnalyseResults(resultLogs.getText() + "\nDocumentSkewCorrect onSuccess :\nresultCode : " + refineResult.getResultCode());
                        } else {
                            Log.d(TAG, "DocumentSkewCorrect Failed : Result is NULL!");
                            Utils.showToastMessage(this, "DocumentSkewCorrect Failed : Result is NULL!");
                            displayFailureAnalyseResults(resultLogs.getText() + "\nDocumentSkewCorrect Failed : Result is NULL!");
                        }

                    })
                    .addOnFailureListener(e -> {
                        correctedImageBitmap = null;
                        Log.e(TAG, "DocumentSkewCorrect onFailure : Exception : " + e.getMessage(), e);
                        Utils.showToastMessage(this, "DocumentSkewCorrect onFailure : Exception : " + e.getMessage());
                        displayFailureAnalyseResults(resultLogs.getText() + "\nDocumentSkewCorrect onFailure :\n" + e.getMessage());
                    });

        } catch (Exception e) {
            Log.e(TAG, "DocumentSkewCorrect Exception : " + e.getMessage(), e);
            Utils.showToastMessage(this, "DocumentSkewCorrect Exception : " + e.getMessage());
            displayFailureAnalyseResults(resultLogs.getText() + "\nDocumentSkewCorrect Exception :\n" + e.getMessage());
        }
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
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use Document Skew Correction without Storage Permission!",
                        "YES GO", "CANCEL");
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");
                createActionTakeImageIntent();
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icons_switch_camera_black,
                        "You can not use Hand KeyPoint Detection Analyzer with Take a Picture without Camera And Storage Permission!",
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
                    analyseDocumentSkewDetectWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult activityIntentCodeStorage IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }
            }

            if (requestCode == ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
                Bitmap bitmap;
                try {
                    Log.i(TAG, "onActivityResult takedImageUri --------> " + takedImageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), takedImageUri);
                    analyseDocumentSkewDetectWithImage(bitmap, true);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
        }
    }


    private void createActionTakeImageIntent() {

        // check it out for is that working properly?
        // set taken photo name with timestamp and location
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoFileName = timeStamp + "_newDocSkewCorrectionPhoto";

        String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML DocSkewCorrection";
        File imageFile = new File(storageDirectory, photoFileName + ".jpg");
        Uri photoUri = Uri.fromFile(imageFile);
        Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
        Log.i(TAG, "onRequestPermissionsResult imageFileAbsPath : " + imageFile.getAbsolutePath());
        Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, photoFileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "ML DocSkewCorrection Photo From Camera");
            takedImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.i(TAG, "onRequestPermissionsResult takedImageUri --------> : " + takedImageUri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takedImageUri);
            this.startActivityForResult(takePictureIntent, ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
        } else {
            Log.i(TAG, "takePictureIntent.resolveActivity( this.getPackageManager()) is NULL");
        }
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Document Skew Correction Success Results : " + text.replace("\n", " "));
        resultLogs.setText(text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Document Skew Correction was Failed Results : " + msg.replace("\n", " "));
        resultLogs.setText(msg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (analyzer != null) {
            try {
                analyzer.stop();
            } catch (IOException e) {
                Log.e(TAG, "onDestroy analyzer.stop exception : " + e.getMessage(), e);
            }
        }
    }


}