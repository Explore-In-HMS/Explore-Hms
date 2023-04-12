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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.textRecognition;

import android.app.Dialog;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEngine;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEnginePreview;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.TextTransactor;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class TextRecognitionCameraActivity extends AppCompatActivity implements View.OnClickListener  {

    //region variables

    private static final String TAG = TextRecognitionCameraActivity.class.getSimpleName();

    public static final String CAMERA_FACING = "facing";


    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;

    CameraConfiguration cameraConfiguration = null;

    private int facing = CameraConfiguration.CAMERA_FACING_BACK;


    private Dialog languageDialog;
    private TextView textCN;
    private TextView textEN;
    private TextView textJN;
    private TextView textKN;
    private TextView textLN;
    private TextView languageText;

    //endregion


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CAMERA_FACING, facing);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_text_recognition_camera);

        Utils.hideNavigationBarActivity(this);

        if (savedInstanceState != null) facing = savedInstanceState.getInt(CAMERA_FACING);

        preview = findViewById(R.id.live_preview);

        graphicOverlay = findViewById(R.id.live_overlay);

        languageText= findViewById(R.id.language_setting);

        cameraConfiguration = new CameraConfiguration();
        cameraConfiguration.setCameraFacing(facing);

        initViews();

        createLensEngine();

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

    }


    private void initViews() {
        findViewById(R.id.back).setOnClickListener(this);
        languageText.setOnClickListener(this);
        findViewById(R.id.ivInfo).setOnClickListener(this);
        createLanguageDialog();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.language_setting:
                showLanguageDialog();
                break;
            case R.id.tv_select_english:
                textEN.setSelected(true);
                textLN.setSelected(false);
                textCN.setSelected(false);
                textJN.setSelected(false);
                textKN.setSelected(false);
                languageText.setText("English");
                languageDialog.dismiss();
                preview.release();
                restartLensEngine();
                break;
            case R.id.tv_select_latin:
                textLN.setSelected(true);
                textEN.setSelected(false);
                textCN.setSelected(false);
                textJN.setSelected(false);
                textKN.setSelected(false);
                languageText.setText("Latin");
                languageDialog.dismiss();
                preview.release();
                restartLensEngine();
                break;
            case R.id.tv_select_simple_cn:
                textCN.setSelected(true);
                textEN.setSelected(false);
                textLN.setSelected(false);
                textJN.setSelected(false);
                textKN.setSelected(false);
                languageText.setText("Simplified Chinese");
                languageDialog.dismiss();
                preview.release();
                restartLensEngine();
                break;
            case R.id.tv_select_japanese:
                textJN.setSelected(true);
                textCN.setSelected(false);
                textEN.setSelected(false);
                textLN.setSelected(false);
                textKN.setSelected(false);
                languageText.setText("Japanese");
                languageDialog.dismiss();
                preview.release();
                restartLensEngine();
                break;
            case R.id.tv_select_korean:
                textKN.setSelected(true);
                textJN.setSelected(false);
                textCN.setSelected(false);
                textEN.setSelected(false);
                textLN.setSelected(false);
                languageText.setText("Korean");
                languageDialog.dismiss();
                preview.release();
                restartLensEngine();
                break;
            case R.id.ivInfo:
                Utils.openWebPage(this, getResources().getString(R.string.link_trs_ter));
                break;
            case R.id.back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void createLanguageDialog() {
        languageDialog = new Dialog(this, R.style.BottomDialogStyle);
        View view = View.inflate(this, R.layout.dialog_bottom_up_lang_select, null);
        // Set up a custom layout
        languageDialog.setContentView(view);

        textEN = view.findViewById(R.id.tv_select_english);
        textEN.setOnClickListener(this);

        textLN = view.findViewById(R.id.tv_select_latin);
        textLN.setOnClickListener(this);

        textCN = view.findViewById(R.id.tv_select_simple_cn);
        textCN.setOnClickListener(this);

        textJN = view.findViewById(R.id.tv_select_japanese);
        textJN.setOnClickListener(this);

        textKN = view.findViewById(R.id.tv_select_korean);
        textKN.setOnClickListener(this);

        languageDialog.setCanceledOnTouchOutside(true);

        // Set the size of the dialog
        Window dialogWindow = languageDialog.getWindow();
        WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(layoutParams);
    }


    private void showLanguageDialog() {
        languageDialog.show();
    }


    private void createLensEngine() {
        if (lensEngine == null) {
            lensEngine = new LensEngine(this, cameraConfiguration, graphicOverlay);
        }
        try {
            TextTransactor textTransactor = new TextTransactor();
            lensEngine.setMachineLearningFrameTransactor(textTransactor);
        } catch (Exception e) {
            Log.e(TAG, "createLensEngine : Can not create image transactor : " + e.getMessage());
            Utils.showToastMessage(getApplicationContext(), "Can not create image transactor: " + e.getMessage());
        }
    }

    private void startLensEngine() {
        if (lensEngine != null) {
            try {
                preview.start(lensEngine, false);
            } catch (IOException e) {
                Log.e(TAG, "startLensEngine : Unable to start lensEngine : " + e.getMessage());
                lensEngine.release();
                lensEngine = null;
            }
        }
    }


    private void restartLensEngine() {
        lensEngine.release();
        lensEngine = null;
        createLensEngine();
        startLensEngine();
        if (lensEngine == null || lensEngine.getCamera() == null) {
            return;
        }
        Camera mCamera = lensEngine.getCamera();
        try {
            mCamera.setPreviewDisplay(preview.getSurfaceHolder());
        } catch (IOException e) {
            Log.e(TAG, "restartLensEngine : initViews IOException : " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseLensEngine();
    }


    @Override
    public void onResume() {
        super.onResume();
        startLensEngine();
    }


    @Override
    protected void onStop() {
        super.onStop();
        preview.stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
    }


    private void releaseLensEngine() {
        if (lensEngine != null) {
            lensEngine.release();
            lensEngine = null;
        }
    }


}