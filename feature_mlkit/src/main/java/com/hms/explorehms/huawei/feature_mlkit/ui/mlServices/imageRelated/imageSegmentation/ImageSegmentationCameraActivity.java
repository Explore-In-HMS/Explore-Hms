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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSegmentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEngine;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.LensEnginePreview;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ImageSegmentationTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.ImageUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImageSegmentationCameraActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ImageSegmentationCameraActivity.class.getSimpleName();

    private Unbinder unbinder;

    private CameraConfiguration cameraConfiguration = null;
    private int facingCamera = CameraConfiguration.CAMERA_FACING_FRONT;

    private OrientationEventListener mOrientationListener;

    private Bitmap backgroundBitmap;

    private Bitmap processImageBitmap;

    private ImageSegmentationTransactor transactor;

    private MLImageSegmentationSetting setting;

    private String imgPathTakedPhoto;

    private Boolean isBlur = false;

    private RenderScript renderScript;

    private LensEngine mLensEngine;

    @Nullable
    @BindView(R.id.live_preview)
    LensEnginePreview lensEnginePreview;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;

    @Nullable
    @BindView(R.id.btnSelectPicture)
    ImageView btnSelectPicture;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_image_segmentation_camera);

        unbinder = ButterKnife.bind(this);

        Utils.hideNavigationBarActivity(this);

        if (savedInstanceState != null) {
            facingCamera = savedInstanceState.getInt("facingCamera");
        }

        setBackgroundBitmapStreamImage();


        createCameraConfigurationAndEditIt();

        createLensEngine();

        // transactor needs this for Allocation in and Allocation out process with blue effect
        renderScript = RenderScript.create(this);

        initOrientationListener();

    }

    @OnClick({R.id.btnTakePhoto, R.id.btnSelectPicture, R.id.btnCameraSwitch, R.id.blur, R.id.ivInfo, R.id.back})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btnTakePhoto:
                takeAndSavePhoto();
                break;
            case R.id.btnSelectPicture:
                showTakedPhotoWithActionView();
                break;
            case R.id.btnCameraSwitch:
                switchCameraAndRestartViews();
                break;
            case R.id.blur:
                setBlurView();
                break;
            case R.id.ivInfo:
                Utils.openWebPage(this, getResources().getString(R.string.link_irs_is));
                break;
            case R.id.back:
                onBackPressed();
                break;
            default:
                Log.i(TAG,"Default");
        }
    }

    private void setBackgroundBitmapStreamImage() {

        InputStream is = getApplication().getResources().openRawResource(+R.drawable.test_image_segmentation_bg_sky);
        backgroundBitmap = BitmapFactory.decodeStream(is);

    }

    private void createCameraConfigurationAndEditIt() {
        cameraConfiguration = new CameraConfiguration();
        cameraConfiguration.setCameraFacing(facingCamera);
        cameraConfiguration.setFps(6.0f);
        cameraConfiguration.setPreviewWidth(CameraConfiguration.DEFAULT_WIDTH);
        cameraConfiguration.setPreviewHeight(CameraConfiguration.DEFAULT_HEIGHT);
    }

    private void showTakedPhotoWithActionView() {
        if (imgPathTakedPhoto == null) {
            Log.i(TAG, "showTakedPhotoOnApp imgPathTakedPhoto is NULL!");
            Utils.showToastMessage(getApplicationContext(), "Taken Photo image Path is NULL! Please take a photo before try view it.");
        } else {
            Intent intent = new Intent();
            File imgFile = new File(imgPathTakedPhoto);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
            } else {
                intent = new Intent(Intent.ACTION_VIEW);

                Uri imgUri = FileProvider.getUriForFile(this, "com.hms.explorehms.huawei.provider", imgFile);

                Log.i(TAG, "showTakedPhotoWithActionView imgUri : " + imgUri.toString());

                intent.setDataAndType(imgUri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            this.startActivity(intent);
        }
    }


    private void createLensEngine() {
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, cameraConfiguration, graphicOverlay);
        }
        try {
            setting = new MLImageSegmentationSetting.Factory()
                    .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                    .setExact(false)
                    .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)
                    .create();

            transactor = new ImageSegmentationTransactor(getApplicationContext(), setting, backgroundBitmap);
            transactor.setImageSegmentationResultCallBack(imageSegmentationResultCallBack);
            mLensEngine.setMachineLearningFrameTransactor(transactor);

        } catch (Exception e) {
            Log.e(TAG, "createLensEngine Exception : Unable to create lensEngine and transactor : " + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), "Unable to create lensEngine and transactor : " + e.getMessage());
        }
    }

    private final ImageSegmentationTransactor.ImageSegmentationResultCallBack imageSegmentationResultCallBack = new ImageSegmentationTransactor.ImageSegmentationResultCallBack() {
        @Override
        public void callResultBitmap(Bitmap bitmap) {
            processImageBitmap = bitmap;
        }
    };

    private void startLensEngine() {
        if (mLensEngine != null) {
            try {
                if (lensEnginePreview != null) {
                    lensEnginePreview.start(mLensEngine, true);
                }
            } catch (IOException e) {
                Log.e(TAG, "startLensEngine Exception : Unable to start lensEngine : " + e.getMessage(), e);
                Utils.showToastMessage(getApplicationContext(), "Unable to start lensEngine : " + e.getMessage());
                mLensEngine.release();
                mLensEngine = null;
                imgPathTakedPhoto = null;
            }
        }
    }

    private void restartLensEngine() {
        startLensEngine();
        if (mLensEngine != null) {
            Camera mCamera = mLensEngine.getCamera();
            try {
                //check it out
                mCamera.setPreviewTexture(lensEnginePreview.getSurfaceTexture());
            } catch (IOException e) {
                Log.e(TAG, "restartLensEngine Exception : Unable to reStart lensEngine : " + e.getMessage(), e);
                Utils.showToastMessage(getApplicationContext(), "Unable to reStart lensEngine : " + e.getMessage());
            }
        }
    }

    private void releaseLensEngine() {
        if (mLensEngine != null) {
            mLensEngine.release();
            mLensEngine = null;
        }
    }


    private void takeAndSavePhoto() {
        if (processImageBitmap == null) {
            Log.e(TAG, "saveTakedPhoto : The image is null, unable to save! ");
            Utils.showToastMessage(getApplicationContext(), "Unable to save taked photo. Because  processImageBitmap is NULL!");
        } else {
            // save current image to gallery.
            ImageUtils imageUtils = new ImageUtils(getApplicationContext());
            imageUtils.setImageUtilCallBack(path -> {
                imgPathTakedPhoto = path;
                Log.i(TAG, "ImageUtils.ImageUtilCallBack.callSavePath : path : " + imgPathTakedPhoto);
            });

            imageUtils.saveToAlbum(processImageBitmap);

            Utils.createVibration(getApplicationContext(), 200);
            Utils.showToastMessage(getApplicationContext(), "Photo will saving to gallery. Check it out on image select button.");

            // get saved photo to and show on image select button.

            Matrix matrix = new Matrix();
            matrix.postScale(0.3f, 0.3f);
            Bitmap resizedBitmap = Bitmap.createBitmap(processImageBitmap, 0, 0, processImageBitmap.getWidth(), processImageBitmap.getHeight(), matrix, true);
            btnSelectPicture.setImageBitmap(resizedBitmap);
        }

    }


    private void switchCameraAndRestartViews() {
        if (mLensEngine != null) {
            if (facingCamera == CameraConfiguration.CAMERA_FACING_FRONT) {
                facingCamera = CameraConfiguration.CAMERA_FACING_BACK;
            } else {
                facingCamera = CameraConfiguration.CAMERA_FACING_FRONT;
            }
            cameraConfiguration.setCameraFacing(facingCamera);
            setting = new MLImageSegmentationSetting.Factory()
                    .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                    .create();

            transactor = new ImageSegmentationTransactor(getApplicationContext(), setting, backgroundBitmap);
            transactor.setImageSegmentationResultCallBack(imageSegmentationResultCallBack);
            mLensEngine.setMachineLearningFrameTransactor(this.transactor);
        }
        lensEnginePreview.stop();
        restartLensEngine();
    }


    private void setBlurView() {
        isBlur = !isBlur;
        if (transactor != null) {
            transactor.setBlur(isBlur);
            transactor.setRenderScript(renderScript);
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

        mOrientationListener.disable();

        if (transactor != null) {
            transactor.setBlur(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            renderScript.releaseAllContexts();
        } else {
            renderScript.finish();
        }
    }
}