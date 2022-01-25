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
package com.genar.hmssandbox.huawei.feature_mlkit.ui.mlServices.imageRelated.sceneDetection;

import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.feature_mlkit.R;
import com.genar.hmssandbox.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.genar.hmssandbox.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEngine;
import com.genar.hmssandbox.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEnginePreview;
import com.genar.hmssandbox.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.genar.hmssandbox.huawei.feature_mlkit.ui.cameraOperations.transactor.SceneDetectionTransactor;
import com.genar.hmssandbox.huawei.feature_mlkit.utils.Utils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SceneDetectionCameraActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = SceneDetectionCameraActivity.class.getSimpleName();

    private Unbinder unbinder;

    private CameraConfiguration cameraConfiguration = null;
    private int facingCamera = CameraConfiguration.CAMERA_FACING_BACK;

    private OrientationEventListener mOrientationListener;

    private LensEngine mLensEngine;

    private boolean isInitialization = false;

    private static final int LIVE_PREVIEW = R.id.live_preview;
    private static final int LIVE_OVERLAY= R.id.live_overlay ;
    private static final int BTN_CAMERA_SWITCH = R.id.btnCameraSwitch;
    private static final int PROGRESS_BAR = R.id.progressBar;

    private static final int IV_INFO = R.id.ivInfo;
    private static final int BACK = R.id.back;

    @Nullable
    @BindView(LIVE_PREVIEW)
    LensEnginePreview lensEnginePreview;

    @Nullable
    @BindView(LIVE_OVERLAY)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(BTN_CAMERA_SWITCH)
    ImageButton btnCameraSwitch;

    @Nullable
    @BindView(PROGRESS_BAR)
    ProgressBar progressBar;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_detection_camera);

        unbinder = ButterKnife.bind(this);

        Utils.hideNavigationBarActivity(this);

        if (savedInstanceState != null) {
            facingCamera = savedInstanceState.getInt("facingCamera");
        }

        cameraConfiguration = new CameraConfiguration();
        cameraConfiguration.setCameraFacing(facingCamera);
        if (Camera.getNumberOfCameras() == 1) {
            btnCameraSwitch.setVisibility(View.INVISIBLE);
        }
        createLensEngine();
        initOrientationListener();
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

    @OnClick({R.id.btnCameraSwitch, R.id.ivInfo, R.id.back,})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case BTN_CAMERA_SWITCH:
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
            case IV_INFO:
                Utils.openWebPage(this, getResources().getString(R.string.link_irs_sd));
                break;
            case BACK:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    /* ------------------------------------------------------------------------------------------ */
    //region lensEngine Operations

    private void createLensEngine() {
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, cameraConfiguration, graphicOverlay);
        }
        try {
            mLensEngine.setMachineLearningFrameTransactor(new SceneDetectionTransactor(getApplicationContext(), ""));
            isInitialization = true;
        } catch (Exception e) {
            Log.e(TAG, "createLensEngine Exception : Unable to create lensEngine and transactor : " + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), "Unable to create lensEngine and transactor : " + e.getMessage());
        }
    }

    private void startLensEngine() {
        if (mLensEngine != null) {
            try {
                lensEnginePreview.start(mLensEngine, false);
                // You can sync or async

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
        if (!isInitialization) {
            createLensEngine();
        }
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

        mOrientationListener.disable();
    }


}