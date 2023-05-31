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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.cameraOperations.CameraSurfaceView;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.cameraOperations.FaceAnalyzerTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.cameraOperations.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FaceDetectionCameraActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = FaceDetectionCameraActivity.class.getSimpleName();

    private Unbinder unbinder;

    MLFaceAnalyzer faceAnalyzer;
    MLFaceAnalyzerSetting faceAnalyzerSetting;

    private LensEngine mLensEngine;
    private int cameraLens = LensEngine.FRONT_LENS;


    @Nullable
    @BindView(R.id.live_preview)
    CameraSurfaceView cameraSurfaceView;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(R.id.rl_switch_face_data)
    RelativeLayout layoutSwitchFaceData;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Nullable
    @BindView(R.id.switch_face_data)
    Switch switchFaceData;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.back)
    ImageButton back;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_face_detection_camera);

        Utils.hideNavigationBarActivity(this);

        unbinder = ButterKnife.bind(this);

        faceAnalyzer = createFaceAnalyzer();

        createLensEngine();

        //  check it out and edit graphicOverlay class for switchFaceData
        switchFaceData.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.e(TAG, "switchFaceData onCheckedChanged b : " + b);
            setVisibilityFaceData(switchFaceData.isChecked());
        });
    }


    @OnClick({R.id.btnCameraSwitch, R.id.rl_switch_face_data, R.id.switch_face_data, R.id.ivInfo, R.id.back,})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btnCameraSwitch:
                if (mLensEngine != null) {
                    if (cameraLens == LensEngine.FRONT_LENS) {
                        cameraLens = LensEngine.BACK_LENS;
                    } else {
                        cameraLens = LensEngine.FRONT_LENS;
                    }

                    mLensEngine.close();
                    createLensEngine();
                    startLensEngine();
                }
                break;
            case R.id.rl_switch_face_data:
                switchFaceData.setChecked(!switchFaceData.isChecked());
                break;
            case R.id.ivInfo:
                Utils.openWebPage(this, getResources().getString(R.string.link_fbrs_fd));
                break;
            case R.id.back:
                onBackPressed();
                break;
            default:
                Log.i(TAG,"Default");
        }
    }


    private MLFaceAnalyzer createFaceAnalyzer() {
        faceAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(createDefaultFaceAnalyzerSetting());
        faceAnalyzer.setTransactor(new FaceAnalyzerTransactor(graphicOverlay));
        return faceAnalyzer;
    }

    private void createLensEngine() {
        Context context = getApplicationContext();
        // step 3: add on-device lens engine
        mLensEngine = new LensEngine.Creator(context, faceAnalyzer)
                .setLensType(cameraLens)
                .applyDisplayDimension(1600, 1024)
                .applyFps(30.0f)  // 25 is slow
                .enableAutomaticFocus(true)
                .create();
        // finish
    }

    private void startLensEngine() {
        Log.e(TAG, "startLensEngine");
        if (mLensEngine != null) {
            try {
                cameraSurfaceView.start(mLensEngine, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "StartLensEngine Failed : " + e.getMessage(), e);
                mLensEngine.release();
                mLensEngine = null;
            }
        }
    }

    public MLFaceAnalyzerSetting createDefaultFaceAnalyzerSetting() {
        // Method 1: Use customized parameter settings.
        // If the Full SDK mode is used for integration, set parameters based on the integrated model package.
        faceAnalyzerSetting = new MLFaceAnalyzerSetting.Factory()
                // Set whether to detect facial features.
                .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                // Set whether to detect key face points.
                .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                // Set whether to detect face contour points.
                .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                // Set whether to enable face tracking and specify the fast tracking mode.
                //.setTracingAllowed(true) // another usage
                .setTracingAllowed(true, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                .allowTracing()
                // Set the speed and precision of the detector.
                .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                // Set whether to enable pose detection (enabled by default).
                .setPoseDisabled(true)
                //.setMinFaceProportion(0.1f)
                .create();

        // Method 2: Use the default parameter settings.
        // MLFaceAnalyzer analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer();
        return faceAnalyzerSetting;
    }


    private void setVisibilityFaceData(boolean checked) {
        Log.e(TAG, "setVisibilityFaceData : " + checked);
        MLFaceAnalyzerSetting detectorOptions;
        detectorOptions = editFaceAnalyzerSettingWithVisibilityFaceData(checked);
        faceAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(detectorOptions);
        faceAnalyzer.setTransactor(new FaceAnalyzerTransactor(graphicOverlay));
    }

    // check it out and edit graphicOverlay class for switchFaceData
    public MLFaceAnalyzerSetting editFaceAnalyzerSettingWithVisibilityFaceData(boolean isVisible) {
        if (isVisible) {
            faceAnalyzerSetting = new MLFaceAnalyzerSetting.Factory()
                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_PRECISION)
                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                    .setTracingAllowed(true, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                    .allowTracing()
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    .create();

        } else {
            faceAnalyzerSetting = new MLFaceAnalyzerSetting.Factory()
                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES)
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                    .setTracingAllowed(true, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                    .allowTracing()
                    .create();
        }
        return faceAnalyzerSetting;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSurfaceView.stop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

        if (this.mLensEngine != null) {
            this.mLensEngine.release();
        }
        try {
            if (faceAnalyzer != null) {
                faceAnalyzer.stop();
                faceAnalyzer.destroy();
            }
        } catch (IOException e) {
            Log.e(TAG, "onDestroy faceAnalyzer.stop() exc : " + e.getMessage(), e);
        }

    }


}