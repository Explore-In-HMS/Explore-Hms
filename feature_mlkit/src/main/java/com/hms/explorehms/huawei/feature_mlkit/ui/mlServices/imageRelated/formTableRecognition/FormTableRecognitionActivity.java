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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.formTableRecognition;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionAnalyzer;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionAnalyzerFactory;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionAnalyzerSetting;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionConstant;
import com.huawei.hms.mlsdk.fr.MLFormRecognitionTablesAttribute;

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
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class FormTableRecognitionActivity extends AppCompatActivity {


    //region variablesAndObjects
    private static final String TAG = FormTableRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    MLFormRecognitionAnalyzer analyzer;

    private Uri takedImageUri;

    @Nullable
    @BindView(R.id.imageView_formTableRecognition)
    ImageView imageViewFormTableRecognition;

    @Nullable
    @BindView(R.id.btn_formTableRecognitionExport)
    Button btnResultExport;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private static final int PERM_CODE_STORAGE = 1;
    private static final int PERM_CODE_STORAGE_EXPORT = 12;
    String[] permReqStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE = 11;

    private static final int PERM_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permReqCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 22;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_form_table_recognition);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createFormTableAnalyzer();

        // this is important for get image of imageView by Bitmap and use with localAnalyzerWithImage
        imageViewFormTableRecognition.setDrawingCacheEnabled(true);

        imageViewFormTableRecognition.setOnClickListener(view -> {
            Bitmap bitmap = imageViewFormTableRecognition.getDrawingCache();
            DialogUtils.showDialogImagePeekView(
                    this,
                    getApplicationContext(),
                    bitmap
            );
        });

    }


    @OnClick({R.id.btn_formTableRecognitionWithImage, R.id.btn_formTableRecognitionWithStorage,
            R.id.btn_formTableRecognitionWithTakeAPicture, R.id.btn_formTableRecognitionExport})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_formTableRecognitionWithImage:
                showProgress();
                Bitmap bitmap = imageViewFormTableRecognition.getDrawingCache();
                analyseFormTableWithImage(bitmap, false);
                break;
            case R.id.btn_formTableRecognitionWithStorage:
                ActivityCompat.requestPermissions(this, permReqStorage, PERM_CODE_STORAGE);
                break;
            case R.id.btn_formTableRecognitionWithTakeAPicture:
                ActivityCompat.requestPermissions(this, permReqCameraAndStorageForTakePhoto, PERM_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case R.id.btn_formTableRecognitionExport:
                ActivityCompat.requestPermissions(this, permReqStorage, PERM_CODE_STORAGE_EXPORT);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_trs_fr));
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

        if (requestCode == PERM_CODE_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE);
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not use Form Table Recognition Analyzer without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));
            }
        }

        if (requestCode == PERM_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");
                createActionTakeImageIntent();
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.camera_and_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icons_switch_camera_black,
                        "You can not use Hand KeyPoint Detection Analyzer with Take a Picture without Camera And Storage Permission!",
                        getString(R.string.permission_settings_allow), getString(R.string.cancel));
            }
        }


        if (requestCode == PERM_CODE_STORAGE_EXPORT) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> create export excel file ");
                createExcelFile();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        "You can not export Recognized Form Result without Storage Permission!",
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

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    analyseFormTableWithImage(bitmap, true);
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
                    analyseFormTableWithImage(bitmap, true);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
        }
    }


    private void createActionTakeImageIntent() {

        //  check it out for is that working properly?
        // set taken photo name with timestamp and location
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String photoFileName = timeStamp + "_newFormTablePicture";

        String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML FormTable";
        File imageFile = new File(storageDirectory, photoFileName + ".jpg");
        Uri photoUri = Uri.fromFile(imageFile);
        Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
        Log.i(TAG, "onRequestPermissionsResult imageFileAbsPath : " + imageFile.getAbsolutePath());
        Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, photoFileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "ML FormTable Photo From Camera");
            takedImageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Log.i(TAG, "onRequestPermissionsResult takedImageUri --------> : " + takedImageUri);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takedImageUri);
            this.startActivityForResult(takePictureIntent, ACTIVITY_INTENT_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
        } else {
            Log.i(TAG, "takePictureIntent.resolveActivity( this.getPackageManager()) is NULL");
        }
    }


    public void createFormTableAnalyzer() {
        MLFormRecognitionAnalyzerSetting setting = new MLFormRecognitionAnalyzerSetting.Factory().create();
        analyzer = MLFormRecognitionAnalyzerFactory.getInstance().getFormRecognitionAnalyzer(setting);
        // another method
        // analyzer = MLFormRecognitionAnalyzerFactory.getInstance().getFormRecognitionAnalyzer();
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
    private void analyseFormTableWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        if (isFromGallery) {
            imageViewFormTableRecognition.setImageBitmap(bitmap);
        } else {
            imageViewFormTableRecognition.setImageResource(R.drawable.test_image_form_recognition);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewHands size
        Bitmap takedImageBitmap = imageViewFormTableRecognition.getDrawingCache();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(FormTableRecognitionActivity.this, bitmap);

        // Create an MLFrame object using the bitmap.
        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();
        // another method
        // MLFrame frame = MLFrame.fromBitmap(takedImageBitmap);

        hideCreateExcelButton();

        Task<JsonObject> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(recognizeResult -> {
            Log.d(TAG, "MLFormRecognitionAnalyzer onSuccess JsonObjec  : " + recognizeResult.toString());
            // important for peek view from changed imageView resource
            imageViewFormTableRecognition.getDrawingCache();
            displaySuccessAnalyseResults(recognizeResult.toString());
            createTableCellAttributeList(recognizeResult.toString());
        }).addOnFailureListener(e -> {
            Log.e(TAG, "MLFormRecognitionAnalyzer onFailure : " + e.getMessage(), e);
            displayFailureAnalyseResults(e.getMessage());
            hideCreateExcelButton();
        });
    }


    private List<MLFormRecognitionTablesAttribute.TablesContent.TableAttribute.TableCellAttribute> exportTableList;

    private void createTableCellAttributeList(String results) {
        MLFormRecognitionTablesAttribute attribute = new Gson().fromJson(results, MLFormRecognitionTablesAttribute.class);
        if (attribute.getRetCode() == MLFormRecognitionConstant.SUCCESS) {
            exportTableList = new ArrayList<>();
            exportTableList.addAll(attribute.getTablesContent().getTableAttributes().get(0).getTableCellAttributes());
            showCreateExcelButton();
        } else if (attribute.getRetCode() == MLFormRecognitionConstant.FAILED) {
            Log.e(TAG, "MLFormRecognitionAnalyzer MLFormRecognitionTablesAttribute Failed");
            hideCreateExcelButton();
        }

    }

    private void showCreateExcelButton() {
        btnResultExport.setVisibility(View.VISIBLE);
    }

    private void hideCreateExcelButton() {
        btnResultExport.setVisibility(View.GONE);
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
        Log.i(TAG, "FormTable Recognition Success Results : " + text);
        resultLogs.setText("FormTable Recognition Success Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "FormTable Recognition was Failed Results : " + msg);
        resultLogs.setText("FormTable Recognition was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "FormTable Recognition was Failed : \n" + msg);
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


    public void createExcelFile() {

        try {
            WritableWorkbook workbook;
            String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
            String fileName = getExternalCacheDir().getPath() + File.separator + timeStamp + "_formRecognition.xls";
            File file = new File(fileName);
            if (!file.exists()) {
                boolean isFileDeleted = file.delete();
                Log.i(TAG, String.valueOf(isFileDeleted));
                boolean isFileCreated = file.createNewFile();
                Log.i(TAG, String.valueOf(isFileCreated));
            }
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("sheet 1", 0);
            for (int i = 0; i < exportTableList.size(); i++) {
                Label l = new Label(exportTableList.get(i).getStartCol(), exportTableList.get(i).getStartRow(), exportTableList.get(i).getTextInfo());
                sheet.addCell(l);
                sheet.mergeCells(exportTableList.get(i).getStartCol(), exportTableList.get(i).getStartRow(), exportTableList.get(i).getEndCol(), exportTableList.get(i).getEndRow());
            }
            workbook.write();
            workbook.close();

            Log.d(TAG, "createExcelFile : Table successfully Export at : \n " + fileName);
            Toast.makeText(this, "Table successfully Export at : \n " + fileName, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e(TAG, "createExcelFile : IOException : " + e.getMessage(), e);
        } catch (RowsExceededException e) {
            Log.e(TAG, "createExcelFile : RowsExceededException : " + e.getMessage(), e);
        } catch (WriteException e) {
            Log.e(TAG, "createExcelFile : WriteException : " + e.getMessage(), e);
        }
    }

}