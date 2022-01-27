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

package com.genar.hmssandbox.huawei.feature_scenekit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_scenekit.arview.ARViewActivity;
import com.genar.hmssandbox.huawei.feature_scenekit.faceview.FaceViewActivity;
import com.genar.hmssandbox.huawei.feature_scenekit.sceneview.SceneViewActivity;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.scene.common.base.error.exception.ModuleException;
import com.huawei.hms.scene.common.base.error.exception.StateException;
import com.huawei.hms.scene.common.base.error.exception.UpdateNeededException;
import com.huawei.hms.scene.sdk.fluid.SceneKitFluid;
import com.huawei.hms.scene.sdk.render.SceneKit;
import com.huawei.hms.videokit.player.common.Constants;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_UPDATE = 100001;
    private boolean initialized2D = false;
    private static final int PERMISSION_REQUEST = 99;
    private static final int REQ_CODE_UPDATE_SCENE_KIT = 10001;
    private boolean initialized = false;
    private static final int RES_CODE_UPDATE_SUCCESS = -1;
    private static final int RES_CODE_UPDATE_SUCCESS_2D = -1;

    /**
     * Service Related UI Elements
     */
    private MaterialButton btnSceneView;
    private MaterialButton btnArView;
    private MaterialButton btnFaceView;
    private MaterialButton btnRenderView;
    private MaterialButton btn2dSimulationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scenekit);

        setupToolbar();
        initUI();
        initListener();
    }

    private void initializeSceneKit() {
        // If the initialization is already complete, do not perform it again.
        if (initialized) {
            return;
        }
        // Create a SceneKit.Property class and set the app ID and the graphics backend API.
        SceneKit.Property property = SceneKit.Property.builder()
                .setAppId("102418005")
                .setGraphicsBackend(SceneKit.Property.GraphicsBackend.GLES)
                .build();
        try {
            // Use the synchronous initialization API for initialization.
            SceneKit.getInstance()
                    .setProperty(property)
                    .initializeSync(getApplicationContext());
            initialized = true;
            Toast.makeText(this, "Render View starting you can use finger to gesture events", Toast.LENGTH_LONG).show();
            btnRenderView.performClick();
        } catch (UpdateNeededException e) {
            // Capture the exception that an upgrade is needed and start the upgrade activity.
            startActivityForResult(e.getIntent(), REQ_CODE_UPDATE_SCENE_KIT);
        } catch (Exception e) {
            // Handle the initialization exception.
            Toast.makeText(this, "failed to initialize SceneKit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSceneKitFluid() {
        // Use the asynchronous API for initialization.
        SceneKitFluid.getInstance().initialize(this, new SceneKitFluid.OnInitEventListener() {
            @Override
            public void onUpdateNeeded(Intent intent) {
                startActivityForResult(intent, REQ_CODE_UPDATE);
            }

            @Override
            public void onInitialized() {
                initialized2D = true;
            }

            @Override
            public void onException(Exception e) {
                Toast.makeText(MainActivity.this, "failed to initialize SceneKit fluid: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the upgrade is successful, re-initialize the SceneKit class.
        if (requestCode == REQ_CODE_UPDATE_SCENE_KIT
                && resultCode == RES_CODE_UPDATE_SUCCESS) {
            try {
                SceneKit.getInstance()
                        .initializeSync(getApplicationContext());
                initialized = true;
                Toast.makeText(this, "SceneKit initialized", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // No upgrade need is detected during re-initialization.
                Toast.makeText(this, "failed to initialize SceneKit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQ_CODE_UPDATE
                && resultCode == RES_CODE_UPDATE_SUCCESS_2D) {
            try {
                SceneKitFluid.getInstance().initializeSync(getApplicationContext());
                initialized = true;
                Toast.makeText(this, "SceneKit Fluid initialized", Toast.LENGTH_SHORT).show();
            } catch (StateException | ModuleException e) {
                // No update need is detected during re-initialization.
                Toast.makeText(this, "failed to initialize SceneKit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_scene);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_scenekit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {
        btnSceneView = findViewById(R.id.btn_scene_view);
        btnArView = findViewById(R.id.btn_ar_view);
        btnFaceView = findViewById(R.id.btn_face_view);
        btnRenderView=findViewById(R.id.btn_render_view);
        btn2dSimulationView=findViewById(R.id.btn_fluid_simulation_view);
    }


    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener() {
        btnSceneView.setOnClickListener(v -> {
            Intent sceneIntent = new Intent(MainActivity.this, SceneViewActivity.class);
            isPermissionsGranted(sceneIntent);
        });

        btnArView.setOnClickListener(v -> {
            Intent arviewIntent = new Intent(MainActivity.this, ARViewActivity.class);
            isPermissionsGranted(arviewIntent);
        });

        btnFaceView.setOnClickListener(v -> {
            Intent faceviewIntent = new Intent(MainActivity.this, FaceViewActivity.class);
            isPermissionsGranted(faceviewIntent);
        });

        btnRenderView.setOnClickListener(v -> {
            if (!initialized) {
                initializeSceneKit();
                return;
            }
            Intent renderViewIntent = new Intent(MainActivity.this, SampleRenderActivity.class);
            isPermissionsGranted(renderViewIntent);
        });

        btn2dSimulationView.setOnClickListener(v -> {
            if (!initialized2D) {
                initializeSceneKitFluid();
                return;
            }
            Intent simulationViewIntent = new Intent(MainActivity.this, FluidSimulation2D.class);
            isPermissionsGranted(simulationViewIntent);
        });
    }

    /**
     * Permission Check
     */
    private void isPermissionsGranted(Intent intent) {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            String[] permission = new String[]{Manifest.permission.CAMERA};
            requestPermissions(permission, PERMISSION_REQUEST);
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST && grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_prm_req_scene_kit), Toast.LENGTH_LONG).show();
        }
    }
}
