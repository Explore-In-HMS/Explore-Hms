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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.body_recognition.video_portrait_segmentation;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.common.util.concurrent.ListenableFuture;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.image.segmentation.ImageSegmentation;
import com.huawei.hiai.vision.image.segmentation.SegConfiguration;
import com.huawei.hiai.vision.visionkit.common.VisionConfiguration;
import com.huawei.hiai.vision.visionkit.image.ImageResult;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("UnsafeExperimentalUsageError")
public class VideoPortraitSegmentationActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final String TAG = "VideoPortraitSegmentationActivity";
    //UI
    private MaterialButton btnStartSegmentation;
    private SwitchMaterial switchMaterial;
    private ImageView ivImage;

    //Stream Camera
    private Preview preview;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;
    private PreviewView viewFinder;
    private ExecutorService cameraExecutor;
    private int cameraStyle = CameraSelector.LENS_FACING_FRONT;

    //VPS
    private ImageSegmentation imageSegmentation;

    private boolean isRunning = false;

    public VideoPortraitSegmentationActivity() {
        super(ServiceGroupConstants.BODY);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_portrait_segmentation_hiai);
        baseContext = this;

        initUI();
        setupToolbar();
        initListeners();
        try {
            initService();
        } catch (Exception e) {
            Log.e(TAG, "Initialization Error : " + e.toString());
        }
        createSegmentationObjects();

    }

    @Override
    public void initUI() {
        btnStartSegmentation = findViewById(R.id.btn_video_portrait_segmentation_start);
        switchMaterial = findViewById(R.id.switch_video_portrait_segmentation_camera_switch);
        viewFinder = findViewById(R.id.pv_video_portrait_segmentation_hiai);
        ivImage = findViewById(R.id.iv_video_portrait_seg_res_image);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_video_portrait_seg_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.msg_video_portrait_segmentation_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnStartSegmentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btnStartSegmentation.setVisibility(View.GONE);
                    ivImage.setPadding(0, 0, 0, 0);

                    startCamera();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cameraStyle = CameraSelector.LENS_FACING_FRONT;
                    switchMaterial.setText(getString(R.string.txt_video_portrait_segmentation_front_camera_hiai));

                } else {
                    cameraStyle = CameraSelector.LENS_FACING_BACK;
                    switchMaterial.setText(getString(R.string.txt_video_portrait_segmentation_back_camera_hiai));
                }

                if (isRunning)
                    startCamera();
            }
        });
    }

    @Override
    public void initService() {
        VisionBase.init(VideoPortraitSegmentationActivity.this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i(TAG, "onServiceConnect");
                Toast.makeText(getApplicationContext(), "Service Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnect() {
                Log.i(TAG, "onServiceDisconnect");
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSegmentationObjects() {
        /**
         * Constant configurations sets one time
         */
        imageSegmentation = new ImageSegmentation(getApplicationContext());

        SegConfiguration mSegmentationConfiguration = new SegConfiguration.Builder()
                .setProcessMode(VisionConfiguration.MODE_IN)
                .setSegmentationType(SegConfiguration.TYPE_PORTRAIT) // Use TYPE_PORTRAIT_SEGMENTATION_VIDEO to video data, this example based on to stream camera data
                .setOutputType(SegConfiguration.OUTPUT_TYPE_BYTEARRAY)
                .build();

        imageSegmentation.setConfiguration(mSegmentationConfiguration);
    }

    private void startCamera() {

        cameraExecutor = Executors.newSingleThreadExecutor();

        ListenableFuture<ProcessCameraProvider> cameraProvider = ProcessCameraProvider.getInstance(getApplicationContext());

        cameraProvider.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider processCameraProvider = cameraProvider.get();

                    preview = new Preview.Builder().build();

                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraStyle).build();

                    processCameraProvider.unbindAll();

                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                    imageAnalyzer = new ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //sonuncu frame
                            .setTargetResolution(new Size(1080, 1440))
                            .build();

                    isRunning = true;

                    runOnUiThread(() -> {
                        if (cameraStyle == CameraSelector.LENS_FACING_FRONT) {
                            ivImage.setRotation(270);
                            ivImage.setScaleY(-1);
                        } else {
                            ivImage.setRotation(90);
                            ivImage.setScaleY(1);
                        }

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(16, 16, 16, 16);
                        ivImage.setLayoutParams(params);
                    });

                    imageAnalyzer.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                        @Override
                        public void analyze(@NonNull ImageProxy image) {
                            getResult(image);
                        }
                    });

                    camera = processCameraProvider.bindToLifecycle(VideoPortraitSegmentationActivity.this, cameraSelector, imageAnalyzer, preview);


                } catch (ExecutionException | InterruptedException e) {
                    Log.e(TAG, e.toString());
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }, ContextCompat.getMainExecutor(getApplicationContext()));
    }

    private Bitmap imageToBitmap(Image image) {
        try {
            byte[] bytes;

            Image.Plane[] planes = image.getPlanes();
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];
            //U and V are swapped
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);

            bytes = out.toByteArray();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }

    public void getResult(ImageProxy imageProxy) {
        if (imageProxy != null && imageProxy.getImage() != null) {

            Bitmap bitmap = imageToBitmap(imageProxy.getImage());

            if (bitmap != null) {
                VisionImage visionImage = VisionImage.fromBitmap(bitmap);

                ImageResult imageResult = new ImageResult();

                int resultCode = imageSegmentation.doSegmentation(visionImage, imageResult, null);

                if (resultCode == 0) {
                    runOnUiThread(() -> {
                        ivImage.setImageBitmap(imageResult.getBitmap());

                        imageProxy.close();
                    });
                }
            } else
                imageProxy.close();
        }
    }
}
