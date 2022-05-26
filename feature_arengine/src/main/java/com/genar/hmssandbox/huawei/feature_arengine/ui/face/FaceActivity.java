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
package com.genar.hmssandbox.huawei.feature_arengine.ui.face;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_arengine.R;
import com.genar.hmssandbox.huawei.feature_arengine.common.DisplayRotationManager;
import com.genar.hmssandbox.huawei.feature_arengine.ui.ConnectAppMarketActivity;
import com.genar.hmssandbox.huawei.feature_arengine.ui.face.rendering.FaceRenderManager;
import com.genar.hmssandbox.huawei.feature_arengine.utils.AppUtils;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hiar.ARConfigBase;
import com.huawei.hiar.AREnginesApk;
import com.huawei.hiar.ARFaceTrackingConfig;
import com.huawei.hiar.ARSession;
import com.huawei.hiar.exceptions.ARCameraNotAvailableException;
import com.huawei.hiar.exceptions.ARUnSupportedConfigurationException;
import com.huawei.hiar.exceptions.ARUnavailableClientSdkTooOldException;
import com.huawei.hiar.exceptions.ARUnavailableServiceApkTooOldException;
import com.huawei.hiar.exceptions.ARUnavailableServiceNotInstalledException;

import java.util.List;

public class FaceActivity extends AppCompatActivity {

    private static final String TAG = FaceActivity.class.getSimpleName();

    private ARSession mArSession = null;

    private GLSurfaceView glSurfaceView;

    private FaceRenderManager mFaceRenderManager;

    private DisplayRotationManager mDisplayRotationManager;

    private static final boolean IS_OPEN_CAMERA_OUTSIDE = false;

    private CameraHelper mCamera;

    private Surface mPreViewSurface;

    private Surface mVgaSurface;

    private Surface mDepthSurface;

    private ARConfigBase mArConfig;

    private MaterialTextView mTextView;

    private String message = null;

    private boolean isRemindInstall = false;

    // The initial texture ID is -1.
    private int textureId = -1;

    //Lightning Modes
    private int lightModeNone = ARConfigBase.LIGHT_MODE_NONE;
    private int lightModeAmbientIntensity = ARConfigBase.LIGHT_MODE_AMBIENT_INTENSITY;
    private int lightModeEnvironmentLighting = ARConfigBase.LIGHT_MODE_ENVIRONMENT_LIGHTING;
    private int lightModeEnvironmentTexture = ARConfigBase.LIGHT_MODE_ENVIRONMENT_TEXTURE;
    private int lightModeAll = ARConfigBase.LIGHT_MODE_ALL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_tracking_arengine);

        initUI();
        setupToolbar();


        initAR();
    }

    private void initUI() {
        mTextView = findViewById(R.id.tv_face_track_arengine);
        glSurfaceView = findViewById(R.id.gl_face_tracking_arengine);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_face_track_arengine);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.txt_doc_link_face_hand_body_arengine));
    }

    private void initAR() {
        mDisplayRotationManager = new DisplayRotationManager(this);

        glSurfaceView.setPreserveEGLContextOnPause(true);

        // Set the OpenGLES version.
        glSurfaceView.setEGLContextClientVersion(2);

        // Set the EGL configuration chooser, including for the
        // number of bits of the color buffer and the number of depth bits.
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        mFaceRenderManager = new FaceRenderManager(this, this);
        mFaceRenderManager.setDisplayRotationManage(mDisplayRotationManager);
        mFaceRenderManager.setTextView(mTextView);

        glSurfaceView.setRenderer(mFaceRenderManager);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mDisplayRotationManager.registerDisplayListener();
        Exception exception = null;
        message = null;
        if (mArSession == null) {
            try {
                if (!arEngineAbilityCheck()) {
                    finish();
                    return;
                }
                mArSession = new ARSession(this);
                ARFaceTrackingConfig mArConfig = new ARFaceTrackingConfig(mArSession);
                mArConfig.setCameraLensFacing(ARConfigBase.CameraLensFacing.FRONT);
                mArConfig.setLightingMode(lightModeEnvironmentLighting);
                mArConfig.setPowerMode(ARConfigBase.PowerMode.POWER_SAVING);

                if (IS_OPEN_CAMERA_OUTSIDE) {
                    mArConfig.setImageInputMode(ARConfigBase.ImageInputMode.EXTERNAL_INPUT_ALL);

                }
                mArSession.configure(mArConfig);
            } catch (Exception capturedException) {
                exception = capturedException;
                setMessageWhenError(capturedException);
            }
            if (mArConfig.getLightingMode() != lightModeEnvironmentLighting) {
                String toastMsg = "Please update HUAWEI AR Engine app in the AppGallery.";
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
            if (message != null) {
                stopArSession(exception);
                return;
            }
        }
        try {
            mArSession.resume();
        } catch (ARCameraNotAvailableException e) {
            Toast.makeText(this, getString(R.string.txt_camera_open_failed_message), Toast.LENGTH_LONG).show();
            mArSession = null;
            return;
        }
        mDisplayRotationManager.registerDisplayListener();
        setCamera();
        mFaceRenderManager.setArSession(mArSession);
        mFaceRenderManager.setOpenCameraOutsideFlag(IS_OPEN_CAMERA_OUTSIDE);
        mFaceRenderManager.setTextureId(textureId);
        glSurfaceView.onResume();
    }

    /**
     * Check whether HUAWEI AR Engine server (com.huawei.arengine.service) is installed on the current device.
     * If not, redirect the user to HUAWEI AppGallery for installation.
     */
    private boolean arEngineAbilityCheck() {
        boolean isInstallArEngineApk = AREnginesApk.isAREngineApkReady(this);
        if (!isInstallArEngineApk && isRemindInstall) {
            Toast.makeText(this, getString(R.string.txt_agree_message), Toast.LENGTH_LONG).show();
            finish();
        }
        Log.d(TAG, "Is Install AR Engine Apk: " + isInstallArEngineApk);
        if (!isInstallArEngineApk) {
            startActivity(new Intent(this, ConnectAppMarketActivity.class));
            isRemindInstall = true;
        }
        return AREnginesApk.isAREngineApkReady(this);
    }

    private void setMessageWhenError(Exception catchException) {
        if (catchException instanceof ARUnavailableServiceNotInstalledException) {
            startActivity(new Intent(this, ConnectAppMarketActivity.class));
        } else if (catchException instanceof ARUnavailableServiceApkTooOldException) {
            message = "Please update HuaweiARService.apk";
        } else if (catchException instanceof ARUnavailableClientSdkTooOldException) {
            message = "Please update this app";
        } else if (catchException instanceof ARUnSupportedConfigurationException) {
            message = "The configuration is not supported by the device!";
        } else {
            message = "exception throw";
        }
    }

    private void stopArSession(Exception exception) {
        Log.i(TAG, "Stop session start.");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Creating session error ", exception);
        if (mArSession != null) {
            mArSession.stop();
            mArSession = null;
        }
        Log.i(TAG, "Stop session end.");
    }

    private void setCamera() {
        if (IS_OPEN_CAMERA_OUTSIDE && mCamera == null) {
            Log.i(TAG, "new Camera");
            DisplayMetrics dm = new DisplayMetrics();
            mCamera = new CameraHelper(this);
            mCamera.setupCamera(dm.widthPixels, dm.heightPixels);
        }

        // Check whether setCamera is called for the first time.
        if (IS_OPEN_CAMERA_OUTSIDE) {
            if (textureId != -1) {
                mArSession.setCameraTextureName(textureId);
                initSurface();
            } else {
                int[] textureIds = new int[1];
                GLES20.glGenTextures(1, textureIds, 0);
                textureId = textureIds[0];
                mArSession.setCameraTextureName(textureId);
                initSurface();
            }

            SurfaceTexture surfaceTexture = new SurfaceTexture(textureId);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.setPreViewSurface(mPreViewSurface);
            mCamera.setVgaSurface(mVgaSurface);
            mCamera.setDepthSurface(mDepthSurface);
            if (!mCamera.openCamera()) {
                String showMessage = "Open camera filed!";
                Log.e(TAG, showMessage);
                Toast.makeText(this, showMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initSurface() {
        List<ARConfigBase.SurfaceType> surfaceTypeList = mArConfig.getImageInputSurfaceTypes();
        List<Surface> surfaceList = mArConfig.getImageInputSurfaces();

        Log.i(TAG, "surfaceList size : " + surfaceList.size());
        int size = surfaceTypeList.size();
        for (int i = 0; i < size; i++) {
            ARConfigBase.SurfaceType type = surfaceTypeList.get(i);
            Surface surface = surfaceList.get(i);
            if (ARConfigBase.SurfaceType.PREVIEW.equals(type)) {
                mPreViewSurface = surface;
            } else if (ARConfigBase.SurfaceType.VGA.equals(type)) {
                mVgaSurface = surface;
            } else if (ARConfigBase.SurfaceType.METADATA.equals(type)) {
                Log.i(TAG, "Surface metadata");
            } else if (ARConfigBase.SurfaceType.DEPTH.equals(type)) {
                mDepthSurface = surface;
            } else {
                Log.i(TAG, "Unknown type.");
            }
            Log.i(TAG, "list[" + i + "] get surface : " + surface + ", type : " + type);
        }
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause start.");
        super.onPause();
        if (IS_OPEN_CAMERA_OUTSIDE && mCamera != null) {
            mCamera.closeCamera();
            mCamera.stopCameraThread();
            mCamera = null;
        }

        if (mArSession != null) {
            mDisplayRotationManager.unregisterDisplayListener();
            glSurfaceView.onPause();
            mArSession.pause();
            Log.i(TAG, "Session paused!");
        }
        Log.i(TAG, "onPause end.");
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy start.");
        super.onDestroy();
        if (mArSession != null) {
            Log.i(TAG, "Session onDestroy!");
            mArSession.stop();
            mArSession = null;
            Log.i(TAG, "Session stop!");
        }
        Log.i(TAG, "onDestroy end.");
    }

    @Override
    public void onWindowFocusChanged(boolean isHasFocus) {
        Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(isHasFocus);
        if (isHasFocus) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
