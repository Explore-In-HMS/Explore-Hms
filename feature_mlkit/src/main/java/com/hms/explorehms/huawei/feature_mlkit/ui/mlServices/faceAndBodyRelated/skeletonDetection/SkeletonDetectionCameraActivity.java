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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.skeletonDetection;

import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEngine;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEnginePreview;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.SkeletonTransactor;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SkeletonDetectionCameraActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = SkeletonDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;
    //  Whether the camera preview interface drawing is asynchronous,
    //  if synchronized, frame by frame will be very stuttered
    private static boolean isAsynchronous = true;

    private CameraConfiguration cameraConfiguration = null;
    private int facingCamera = CameraConfiguration.CAMERA_FACING_BACK;

    private OrientationEventListener mOrientationListener;

    private LensEngine mLensEngine;

    @Nullable
    @BindView(R.id.live_preview)
    LensEnginePreview lensEnginePreview;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(R.id.btnCameraSwitch)
    ImageButton btnCameraSwitch;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_skeleton_detection_camera);

        Utils.hideNavigationBarActivity(this);

        if (savedInstanceState != null) {
            facingCamera = savedInstanceState.getInt("facingCamera");
        }

        unbinder = ButterKnife.bind(this);

        cameraConfiguration = new CameraConfiguration();
        cameraConfiguration.setCameraFacing(facingCamera);
        if (Camera.getNumberOfCameras() == 1) {
            btnCameraSwitch.setVisibility(View.INVISIBLE);
        }

        createLensEngine();
        initOrientationListener();

        SkeletonDetectionCameraActivity.setIsAsynchronous(false);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();


    }


    @OnClick({R.id.btnCameraSwitch, R.id.ivInfo, R.id.back,})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btnCameraSwitch:
                if (mLensEngine != null) {
                    if (facingCamera == CameraConfiguration.CAMERA_FACING_FRONT) {
                        facingCamera = CameraConfiguration.CAMERA_FACING_BACK;
                        cameraConfiguration.setCameraFacing(facingCamera);
                    } else {
                        facingCamera = CameraConfiguration.CAMERA_FACING_FRONT;
                        cameraConfiguration.setCameraFacing(facingCamera);
                    }

                    lensEnginePreview.stop();
                    restartLensEngine();
                }
                break;
            case R.id.ivInfo:
                Utils.openWebPage(this, getResources().getString(R.string.link_fbrs_skld));
                break;
            case R.id.back:
                onBackPressed();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("facingCamera", facingCamera);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        lensEnginePreview.stop();
        createLensEngine();
        startLensEngine();
    }

    /* ------------------------------------------------------------------------------------------ */
    //region lensEngine Operations


    private void createLensEngine() {
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, cameraConfiguration, graphicOverlay);
        }

        try {
            MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory().create();
            SkeletonTransactor skeletonTransactor = new SkeletonTransactor(setting, this);
            mLensEngine.setMachineLearningFrameTransactor(skeletonTransactor);
        } catch (Exception e) {
            Log.e(TAG, "createLensEngine Exception : Unable to create lensEngine and transactor : " + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), "Unable to create lensEngine and transactor : " + e.getMessage());
        }
    }


    private void startLensEngine() {
        if (mLensEngine != null) {
            try {
                // You can sync or async
                lensEnginePreview.start(mLensEngine, false);
            } catch (IOException e) {
                Log.e(TAG, "startLensEngine Exception : Unable to start lensEngine : " + e.getMessage(), e);
                mLensEngine.release();
                mLensEngine = null;
            }
        }
    }

    public void restartLensEngine() {
        startLensEngine();
        if (null != mLensEngine) {
            Camera mCamera = mLensEngine.getCamera();
            try {
                mCamera.setPreviewDisplay(lensEnginePreview.getSurfaceHolder());
            } catch (IOException e) {
                Log.e(TAG, "restartLensEngine initViews Exception: " + e.getMessage(), e);
            }
        }
    }


    private void releaseLensEngine() {
        if (mLensEngine != null) {
            mLensEngine.release();
            mLensEngine = null;
        }
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    private void initOrientationListener() {
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                Log.d(TAG, "OrientationEventListener onOrientationChanged orientation : " + orientation + " - " + ((orientation + 45) / 90));
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startLensEngine();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lensEnginePreview.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lensEnginePreview.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseLensEngine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

        releaseLensEngine();

        setIsAsynchronous(false);

        mOrientationListener.disable();
    }


    public static boolean isIsAsynchronous() {
        return isAsynchronous;
    }

    public static void setIsAsynchronous(boolean isAsynchronous) {
        SkeletonDetectionCameraActivity.isAsynchronous = isAsynchronous;
    }


}