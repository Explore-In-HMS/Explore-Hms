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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.docRecognition;

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
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.document.MLDocument;
import com.huawei.hms.mlsdk.document.MLDocumentAnalyzer;
import com.huawei.hms.mlsdk.document.MLDocumentSetting;
import com.huawei.hms.mlsdk.text.MLRemoteTextSetting;

import java.io.IOException;
import java.util.ArrayList;
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

public class DocumentRecognitionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = DocumentRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.imageView_docRecognition)
    ImageView imageViewDocRecognition;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MLDocumentAnalyzer analyzerRemote;

    private static final int PERMISSION_CODE_STORAGE_FOR_CLOUD = 2;

    String[] permissionRequestStorage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_CLOUD = 22;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_document_recognition);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        Utils.setApiKeyForRemoteMLApplication(this);

        // this is important for get image of imageView by Bitmap and use with localAnalyzerWithImage
        imageViewDocRecognition.setDrawingCacheEnabled(true);

        imageViewDocRecognition.setOnClickListener(view -> {
            Bitmap bitmap = imageViewDocRecognition.getDrawingCache();
            DialogUtils.showDialogImagePeekView(
                    this,
                    getApplicationContext(),
                    bitmap
            );
        });
    }


    @OnClick({R.id.btn_docRecognitionWithImageOnCloud, R.id.btn_docRecognitionWithStorageOnCloud})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_docRecognitionWithImageOnCloud:
                Log.i(TAG, "onclick for btn_textRecognitionWithImageOnCloud");
                if (Utils.haveNetworkConnection(this)) {
                    Bitmap bitmapForCloud = imageViewDocRecognition.getDrawingCache();
                    // another method
                    // Bitmap bitmapForCloud = BitmapFactory.decodeResource(this.getResources(), R.drawable.test_image_doc_recognition_pdf);
                    remoteAnalyzerWithImage(bitmapForCloud, false);
                } else {
                    DialogUtils.showDialogNetworkWarning(
                            this,
                            "NEED NETWORK PERMISSION",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not use Remote Operation without Internet Connection!",
                            "YES GO", "CANCEL");
                }
                break;
            case R.id.btn_docRecognitionWithStorageOnCloud:
                Log.i(TAG, "onclick for btn_docRecognitionWithStorageOnCloud : " + PERMISSION_CODE_STORAGE_FOR_CLOUD);
                ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE_FOR_CLOUD);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_trs_dor));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Nothing is necessary.
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void remoteAnalyzerWithImage(final Bitmap bitmap, final boolean isFromGallery) {
        showProgress();
        // Set parameters.
        List<String> mlDocumentSettingArrayList = new ArrayList<>();
        mlDocumentSettingArrayList.add("zh");
        mlDocumentSettingArrayList.add("en");

        MLDocumentSetting setting = new MLDocumentSetting.Factory()
                // Specify the languages that can be recognized, which should comply with ISO 639-1.
                .setLanguageList(mlDocumentSettingArrayList)
                // Set the format of the returned text border box.
                .setBorderType(MLRemoteTextSetting.ARC)
                // MLRemoteTextSetting.NGON: Return the coordinates of the four corner points of the quadrilateral.
                // MLRemoteTextSetting.ARC: Return the corner points of a polygon border in an arc. The coordinates of up to 72 corner points can be returned.
                .create();

        // Create a document analyzer that uses the customized configuration.
        this.analyzerRemote = MLAnalyzerFactory.getInstance().getRemoteDocumentAnalyzer(setting);

        // Method 2: Use the default parameter settings to automatically detect languages for text recognition.
        // The format of the returned text box is MLRemoteTextSetting.NGON.
        // analyzer = MLAnalyzerFactory.getInstance().getRemoteTextAnalyzer();

        MLFrame frame = MLFrame.fromBitmap(bitmap);

        Task<MLDocument> task = analyzerRemote.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(document -> {
            Utils.showToastMessage(getApplicationContext(), "Analyse Success");
            if (isFromGallery) {
                imageViewDocRecognition.setImageBitmap(bitmap);
            } else {
                imageViewDocRecognition.setImageResource(R.drawable.test_image_doc_recognition_pdf);
            }
            // important for peek view from changed imageView resource
            imageViewDocRecognition.getDrawingCache();
            displaySuccessAnalyseResults(document);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure remoteAnalyzerWithImage : ", e);
                String errorMsg = getMLExceptionErrorCodeAndMessage(e);

                if (errorMsg.equals("")) errorMsg = e.getMessage();

                displayFailureAnalyseResults(errorMsg);
            }
        });

    }


    private void displaySuccessAnalyseResults(MLDocument document) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        StringBuilder mlText = new StringBuilder();
        List<MLDocument.Block> blocks = document.getBlocks();
        Log.i(TAG, "Success AnalyseResults blocks.toString() : " + blocks.toString());
        Log.i(TAG, "\n\n");
        for (MLDocument.Block block : blocks) {
            List<MLDocument.Section> sections = block.getSections();
            for (MLDocument.Section section : sections) {
                List<MLDocument.Line> lines = section.getLineList();
                for (MLDocument.Line line : lines) {
                    List<MLDocument.Word> words = line.getWordList();
                    for (MLDocument.Word word : words) {
                        mlText.append(word.getStringValue()).append(" ");
                    }
                }
            }
            mlText.append("\n");
        }

        Log.i(TAG, "Success AnalyseResults : " + mlText);
        resultLogs.setText("Success AnalyseResults : with " + mlText.length() + " characters :\n" + mlText);
    }


    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure AnalyseResults : " + msg);
        resultLogs.setText("Failure AnalyseResults : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "AnalyseResults was Failed : " + msg);
    }


    // ------------------------------------------------------------------------------------------ //

    public static String getMLExceptionErrorCodeAndMessage(Exception e) {
        String errorMsg = "";
        try {
            MLException mlException = (MLException) e;
            // Obtain the result codes.
            // You can process the result codes and customize respective messages displayed to users.
            // For details about the result codes, please refer to MLException.
            // mlException.getErrCode();
            // Obtain the error information.
            // You can quickly locate the fault based on the result code.
            // mlException.getMessage();
            errorMsg = "ML ErrorCode : " + mlException.getErrCode() + " : ML ErrorMessage : " + mlException.getMessage();
        } catch (Exception error) {
            // Handle the conversion error.
            // This exception is not ML type
        }
        return errorMsg;
    }


    // ------------------------------------------------------------------------------------------ //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_CLOUD) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForCloud -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_CLOUD);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use RemoteDocumentAnalyzer on Cloud without Storage Permission!",
                        "YES GO", "CANCEL");

            }
        }


    }

    // ------------------------------------------------------------------------------------------ //


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (resultCode == 0) {
            Log.w(TAG, "onActivityResult : onActivityResult No any data detected");
            Utils.showToastMessage(getApplicationContext(), "No any data detected");
        } else {

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_CLOUD) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    Log.i(TAG, "onActivityResult : activityIntentCodeStorageForCloud calling with MediaStore.Images.Media.getBitmap bitmap : " + bitmap.getConfig());
                    remoteAnalyzerWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                }

            }
        }
    }

    // ------------------------------------------------------------------------------------------ //


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (this.analyzerRemote != null) {
            try {
                this.analyzerRemote.stop();
                Log.e(TAG, "onDestroy : analyzerRemote stopped");
            } catch (IOException e) {
                Log.e(TAG, "onDestroy : Stop failed: " + e.getMessage());
            }
        }
    }


}