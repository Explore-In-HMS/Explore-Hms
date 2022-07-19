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
package com.genar.hmssandbox.huawei.feature_arengine.ui.health;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_arengine.R;
import com.genar.hmssandbox.huawei.feature_arengine.common.DisplayRotationManager;
import com.genar.hmssandbox.huawei.feature_arengine.ui.ConnectAppMarketActivity;
import com.genar.hmssandbox.huawei.feature_arengine.ui.health.render.HealthRenderManager;
import com.genar.hmssandbox.huawei.feature_arengine.utils.AppUtils;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hiar.ARConfigBase;
import com.huawei.hiar.AREnginesApk;
import com.huawei.hiar.ARFaceTrackingConfig;
import com.huawei.hiar.ARSession;
import com.huawei.hiar.common.FaceHealthCheckState;
import com.huawei.hiar.exceptions.ARCameraNotAvailableException;
import com.huawei.hiar.exceptions.ARUnSupportedConfigurationException;
import com.huawei.hiar.exceptions.ARUnavailableClientSdkTooOldException;
import com.huawei.hiar.exceptions.ARUnavailableServiceApkTooOldException;
import com.huawei.hiar.exceptions.ARUnavailableServiceNotInstalledException;
import com.huawei.hiar.listener.FaceHealthCheckStateEvent;
import com.huawei.hiar.listener.FaceHealthServiceListener;

import java.util.EventObject;

public class HealthActivity extends AppCompatActivity {

    private static final String TAG = "AREngine";

    private static final int MAX_PROGRESS = 100;

    private GLSurfaceView mGlSurfaceView;

    private ARSession mArSession;

    private boolean isRemindInstall = false;

    private String mMessage;

    private HealthRenderManager mHealthRenderManager;

    private DisplayRotationManager mDisplayRotationManager;

    private ProgressBar mHealthProgressBar;

    private MaterialTextView mProgressTips;

    private MaterialTextView mHealthCheckStatusTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_health_tracking_arengine);

        initIU();
        setupToolbar();
        mDisplayRotationManager = new DisplayRotationManager(this);

        createSurfaceElements();
    }

    private void initIU() {
        mHealthProgressBar = findViewById(R.id.pb_health_status_arengine);
        mGlSurfaceView = findViewById(R.id.gl_health_tracking_arengine);
        mProgressTips = findViewById(R.id.tv_health_process_tip_arengine);
        mHealthCheckStatusTextView = findViewById(R.id.tv_health_status_arengine);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_health_track_arengine);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.txt_doc_link_arengine));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createSurfaceElements() {
        mGlSurfaceView.setPreserveEGLContextOnPause(true);

        // Set the OpenGLES version.
        mGlSurfaceView.setEGLContextClientVersion(2);

        // Set the EGL configuration chooser, including for the
        // number of bits of the color buffer and the number of depth bits.
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mHealthRenderManager = new HealthRenderManager(this, this);
        mHealthRenderManager.setDisplayRotationManage(mDisplayRotationManager);
        TableLayout mHealthParamTable = findViewById(R.id.tl_health_param_arengine);
        mHealthRenderManager.setHealthParamTable(mHealthParamTable);
        mGlSurfaceView.setRenderer(mHealthRenderManager);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMessage = null;
        if (mArSession == null) {
            try {
                if (!arEngineAbilityCheck()) {
                    finish();
                    return;
                }
                mArSession = new ARSession(this);
                ARFaceTrackingConfig mArFaceTrackingConfig = new ARFaceTrackingConfig(mArSession);
                mArFaceTrackingConfig.setEnableItem(ARConfigBase.ENABLE_HEALTH_DEVICE);
                mArFaceTrackingConfig
                        .setFaceDetectMode(ARConfigBase.FaceDetectMode.HEALTH_ENABLE_DEFAULT.getEnumValue());
                mArSession.configure(mArFaceTrackingConfig);
                setHealthServiceListener();
            } catch (ARUnavailableServiceNotInstalledException capturedException) {
                startActivity(new Intent(this, ConnectAppMarketActivity.class));
            } catch (ARUnavailableServiceApkTooOldException capturedException) {
                mMessage = "Please update HuaweiARService.apk";
            } catch (ARUnavailableClientSdkTooOldException capturedException) {
                mMessage = "Please update this app";
            } catch (ARUnSupportedConfigurationException capturedException) {
                mMessage = "The configuration is not supported by the device!";
            } catch (Exception capturedException) {
                mMessage = "unknown exception throws!";
            }
            if (mMessage != null) {
                stopArSession();
                return;
            }
        }
        try {
            mArSession.resume();
        } catch (ARCameraNotAvailableException e) {
            Toast.makeText(this, "Camera open failed, please restart the app", Toast.LENGTH_LONG).show();
            mArSession = null;
            return;
        }
        mDisplayRotationManager.registerDisplayListener();
        mHealthRenderManager.setArSession(mArSession);
        mGlSurfaceView.onResume();
    }

    private void stopArSession() {
        Log.i(TAG, "Stop session start.");
        Toast.makeText(this, mMessage, Toast.LENGTH_LONG).show();
        if (mArSession != null) {
            mArSession.stop();
            mArSession = null;
        }
        Log.i(TAG, "Stop session end.");
    }

    /**
     * Check whether HUAWEI AR Engine server (com.huawei.arengine.service) is installed on the current device.
     * If not, redirect the user to HUAWEI AppGallery for installation.
     *
     * @return true:AR Engine ready
     */
    private boolean arEngineAbilityCheck() {
        boolean isInstallArEngineApk = AREnginesApk.isAREngineApkReady(this);
        if (!isInstallArEngineApk && isRemindInstall) {
            Toast.makeText(this, "Please agree to install.", Toast.LENGTH_LONG).show();
            finish();
        }
        Log.d(TAG, "Is Install AR Engine Apk: " + isInstallArEngineApk);
        if (!isInstallArEngineApk) {
            startActivity(new Intent(this, ConnectAppMarketActivity.class));
            isRemindInstall = true;
        }
        return AREnginesApk.isAREngineApkReady(this);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause start.");
        super.onPause();
        if (mArSession != null) {
            mDisplayRotationManager.unregisterDisplayListener();
            mGlSurfaceView.onPause();
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
            mArSession.stop();
            mArSession = null;
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

    private void setHealthServiceListener() {
        mArSession.addServiceListener(new FaceHealthServiceListener() {
            @Override
            public void handleEvent(EventObject eventObject) {
                if (!(eventObject instanceof FaceHealthCheckStateEvent)) {
                    return;
                }
                final FaceHealthCheckState faceHealthCheckState =
                        ((FaceHealthCheckStateEvent) eventObject).getFaceHealthCheckState();
                runOnUiThread(() -> mHealthCheckStatusTextView.setText(faceHealthCheckState.toString()));
            }

            @Override
            public void handleProcessProgressEvent(final int progress) {
                mHealthRenderManager.setHealthCheckProgress(progress);
                runOnUiThread(() -> setProgressTips(progress));
            }
        });
    }

    private void setProgressTips(int progress) {
        String progressTips = "Processing...";
        if (progress >= MAX_PROGRESS) {
            progressTips = "Finished";
        } else if (progress == 0) {
            progressTips = "Not Processing";
        }
        mProgressTips.setText(progressTips);
        mHealthProgressBar.setProgress(progress);
    }


}
