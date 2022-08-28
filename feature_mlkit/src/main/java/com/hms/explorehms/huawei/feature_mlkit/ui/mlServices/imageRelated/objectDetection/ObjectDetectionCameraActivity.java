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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.objectDetection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEngine;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEnginePreview;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ObjectTransactor;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzerSetting;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ObjectDetectionCameraActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ObjectDetectionCameraActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_CAMERA = 1;
    String[] permissionRequestCamera = {Manifest.permission.CAMERA};

    private CameraConfiguration cameraConfiguration = null;
    private int facingCamera = CameraConfiguration.CAMERA_FACING_BACK;

    private OrientationEventListener mOrientationListener;

    private LensEngine mLensEngine;

    private static final int BTN_CAMERA_SWITCH = R.id.btnCameraSwitch;
    private static final int IV_INFO = R.id.ivInfo;
    private static final int BACK = R.id.back;

    private static final int LIVE_PREVIEW = R.id.live_preview;
    private static final int LIVE_OVERLAY = R.id.live_overlay;
    private static final int PROGRESS_BAR = R.id.progressBar;


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
        setContentView(R.layout.activity_object_detection_camera);

        Utils.hideNavigationBarActivity(this);

        unbinder = ButterKnife.bind(this);

        if (savedInstanceState != null) {
            facingCamera = savedInstanceState.getInt("facingCamera");
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        if (isGrantPermissions()) {
            createCameraOperations();
        } else {
            Utils.showToastMessage(getApplicationContext(), "Camera Permission Was Not Granted!\nPlease Permit to Use Feature.");
        }

    }

    private boolean isGrantPermissions() {
        boolean isPermitGrant = false;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ObjectDetectionCameraActivity.this, permissionRequestCamera, PERMISSION_CODE_CAMERA);
        } else {
            Log.i(TAG, "isGrantPermissions CAMERA has granted.");
            isPermitGrant = true;
        }
        return isPermitGrant;
    }

    private void createCameraOperations() {
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
                Utils.openWebPage(this, getResources().getString(R.string.link_irs_od));
                break;
            case BACK:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERMISSION_CODE_CAMERA) {
            Log.d(TAG, "onClick cv_img_related_obj_dtc");
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission");
                finish();
                Utils.startActivity(ObjectDetectionCameraActivity.this, ObjectDetectionCameraActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : Camera was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icons_switch_camera,
                        "You can not use Object Detection without Camera And Storage Permission!",
                        "YES GO", "CANCEL");
            }
        }

    }

    /* ------------------------------------------------------------------------------------------ */
    //region lensEngine Operations


    private void createLensEngine() {
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, cameraConfiguration, graphicOverlay);
        }
        try {
            MLObjectAnalyzerSetting setting = new MLObjectAnalyzerSetting.Factory()
                    .setAnalyzerType(MLObjectAnalyzerSetting.TYPE_VIDEO)
                    .allowMultiResults()
                    .allowClassification()
                    .create();
            ObjectTransactor objectTransactor = new ObjectTransactor(setting);
            mLensEngine.setMachineLearningFrameTransactor(objectTransactor);
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

        releaseLensEngine();

        if (mOrientationListener != null)
            mOrientationListener.disable();

        unbinder.unbind();
    }


}