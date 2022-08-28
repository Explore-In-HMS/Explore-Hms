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

package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.face3d;


import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzer;
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzerSetting;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEngine;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEnginePreview;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.LocalFace3DTransactor;
import com.hms.explorehms.huawei.feature_mlkit.R;



import java.io.IOException;

/**
 * Detects face information in camera stream.
 *
 * @since  2020-12-10
 */
public class Live3DFaceAnalyseActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = Live3DFaceAnalyseActivity.class.getSimpleName();

    private static final String OPEN_STATUS = "open_status";

    private ML3DFaceAnalyzer analyzer;

    private LensEngine lensEngine = null;

    private LensEnginePreview mPreview;

    private GraphicOverlay graphicOverlay;

    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_BACK;

    private Camera mCamera;
    private boolean isFront = true;

    private boolean isFacePointsChecked = false;
    private boolean isFaceFeatureChecked = false;

    private ToggleButton facingSwitch;
    private RelativeLayout pointLayout;
    private RelativeLayout pointLayout2;
    private ImageButton faceback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_face_detection_3d);
        this.mPreview = this.findViewById(R.id.face_preview);
        this.graphicOverlay = this.findViewById(R.id.face_overlay);
        this.facingSwitch = this.findViewById(R.id.face_facingSwitch);
        pointLayout = findViewById(R.id.Point_layout);
        pointLayout2 =findViewById(R.id.Point_layout2);
        faceback=findViewById(R.id.face_back);
        this.facingSwitch.setOnCheckedChangeListener(this);
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        pointLayout.setVisibility(View.GONE);
        pointLayout2.setVisibility(View.GONE);
        facingSwitch.setVisibility(View.VISIBLE);
        this.createLensEngine();
        faceback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void createFaceAnalyzer() {
        // Create a face analyzer. You can create an analyzer using the provided customized face detection parameter
        ML3DFaceAnalyzerSetting setting = new ML3DFaceAnalyzerSetting.Factory()
                .setPerformanceType(ML3DFaceAnalyzerSetting.TYPE_SPEED)
                .setTracingAllowed(true)
                .create();

        this.lensEngine.setMachineLearningFrameTransactor(new LocalFace3DTransactor(setting, getApplicationContext()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.face_back) {
            releaseLensEngine();
            this.finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.lensEngine != null) {
            if (isChecked) {
                this.facing = CameraConfiguration.CAMERA_FACING_FRONT;
                this.cameraConfiguration.setCameraFacing(this.facing);
            } else {
                this.facing = CameraConfiguration.CAMERA_FACING_BACK;
                this.cameraConfiguration.setCameraFacing(this.facing);
            }
        }
        this.mPreview.stop();
        this.reStartLensEngine();
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            createFaceAnalyzer();
        } catch (Exception e) {
            Log.e(TAG, "createLensEngine IOException." + e.getMessage());
        }
    }


    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.mPreview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    /**
     * After modifying the face analyzer configuration, you need to create a face analyzer again.
     */
    private void reStartLensEngine() {
        this.startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewTexture(this.mPreview.getSurfaceTexture());
            } catch (IOException e) {
                Log.e(TAG, "initViews IOException." + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
    }

    private void releaseLensEngine() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("facing", this.facing);
        outState.putBoolean(OPEN_STATUS, this.isFacePointsChecked);
        outState.putBoolean(OPEN_STATUS, this.isFaceFeatureChecked);
        super.onSaveInstanceState(outState);
    }
}