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

package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.photo;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_cameraengine.R;
import com.hms.explorehms.huawei.feature_cameraengine.application.AppSession;
import com.hms.explorehms.huawei.feature_cameraengine.ui.common.AutoFitTextureView;
import com.hms.explorehms.huawei.feature_cameraengine.common.DoubleClickHandler;
import com.hms.explorehms.huawei.feature_cameraengine.util.ViewUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.camera.camerakit.ActionDataCallback;
import com.huawei.camera.camerakit.ActionStateCallback;
import com.huawei.camera.camerakit.CameraKit;
import com.huawei.camera.camerakit.Metadata;
import com.huawei.camera.camerakit.Mode;
import com.huawei.camera.camerakit.ModeCharacteristics;
import com.huawei.camera.camerakit.ModeConfig;
import com.huawei.camera.camerakit.ModeStateCallback;
import com.huawei.camera.camerakit.RequestKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PhotoModeFragment extends Fragment {

    //region UI Elements & Object References
    private final String TAG = "CameraEngine";

    private final long PREVIEW_SURFACE_READY_TIMEOUT = 5000L;

    private CameraKit mCameraKit;

    private boolean isGetInstance = false;

    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();

    private Mode mMode;
    private ModeCharacteristics mModeCharacteristics;
    private ModeConfig.Builder modeConfigBuilder;

    private @Mode.Type int mCurrentModeType = Mode.Type.NORMAL_MODE;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private Size mPreviewSize;
    private Size mCaptureSize;

    private File mFile;

    private HandlerThread mCameraKitThread;
    private Handler mCameraKitHandler;

    private float currentZoom = 1f;
    private float zoomValue = 0.5f;

    //UI
    private MaterialButton buttonHideIntroduction;
    private MaterialButton buttonShowIntroduction;

    private MaterialTextView tvZoomLevel;

    private ConstraintLayout clIntroduction;
    private ConstraintLayout clShowIntroduction;
    private ConstraintLayout clFunctions;

    private CardView cvCaptureImage;
    private CardView cvZoomIn;
    private CardView cvZoomOut;
    private CardView cvLastImage;

    private ImageView ivLastPhoto;
    private ImageView ivDoubleTap;
    private ImageView ivFlash;

    private ProgressBar pbCameraThread;

    private AutoFitTextureView mTextureView;

    private View view;

    private String cameraId = "0";

    private boolean doubleTapShowed = false;

    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_photo_mode_camera_engine,container,false);
        initUI();
        initListener();

        return view;
    }

    private void initUI(){
        cvCaptureImage = view.findViewById(R.id.cv_capture_image_photo_camera_eng);
        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_photo_camera_engine);
        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_photo_camera_engine);
        clIntroduction = view.findViewById(R.id.cl_mode_introduction_photo_camera_engine);
        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_photo_camera_engine);
        clFunctions = view.findViewById(R.id.cl_photo_functions_camera_engine);
        mTextureView = view.findViewById(R.id.aftv_photo_camera_engine);
        pbCameraThread = view.findViewById(R.id.pb_photo_mode_camera_engine);
        cvZoomIn = view.findViewById(R.id.cv_zoom_in_photo_camera_engine);
        cvZoomOut = view.findViewById(R.id.cv_zoom_out_photo_camera_engine);
        cvLastImage = view.findViewById(R.id.cv_last_image_photo_camera_engine);
        tvZoomLevel = view.findViewById(R.id.tv_settings_value_photo_camera_engine);
        ivLastPhoto = view.findViewById(R.id.iv_last_photo_photo_camera_engine);
        ivDoubleTap = view.findViewById(R.id.iv_double_tap_photo_mode_camera_engine);
        ivFlash = view.findViewById(R.id.iv_flash_photo_mode_camera_engine);

    }

    /**
     * Init listener of elements
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initListener(){
        cvCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        buttonHideIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clIntroduction.animate().alpha(0.0f).setDuration(250);
                clShowIntroduction.setVisibility(View.VISIBLE);
            }
        });

        buttonShowIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clIntroduction.animate().alpha(1.0f).setDuration(250);
                clShowIntroduction.setVisibility(View.GONE);
            }
        });

        cvZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentZoom += zoomValue;
                int result = mMode.setZoom(currentZoom);

                if(result == 0)
                    ViewUtils.showSettingOnCenter(tvZoomLevel,String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
                else
                    currentZoom -= zoomValue;
            }
        });

        cvZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentZoom -= zoomValue;

                int result = mMode.setZoom(currentZoom);

                if(result == 0)
                    ViewUtils.showSettingOnCenter(tvZoomLevel,String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
                else
                    currentZoom += zoomValue;

            }
        });

        cvLastImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFile != null){
                    ViewUtils.showDialogImagePeekView(getActivity(),getContext(),mFile,getString(R.string.txt_photo_mode_camera_engine));
                }else{
                    Toast.makeText(getContext(),getString(R.string.txt_no_picture_camera_engine),Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTextureView.setOnClickListener(new DoubleClickHandler(v -> {
            if(cameraId.equals("0")){
                createMode("1");
            }
            else{
                createMode("0");
            }
        }));

        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSession.getInstance().setFlashOn(!AppSession.getInstance().isFlashOn());
                changeFlashState();
            }
        });
    }

    /**
     * Create Photo mode
     * @param cameraId : current camera, rear or front
     */
    private void createMode(String cameraId){
        if(!isGetInstance){
            try {
                mCameraKit = CameraKit.getInstance(requireContext());
            }catch (NoSuchMethodError e){
                Log.e(TAG,"This version CameraEngine does not contain VersionInfoInterface");
            }finally {
                isGetInstance = true;
            }
        }

        if(mCameraKit == null){
            return;
        }

        String[] cameraList = mCameraKit.getCameraIdList();
        if((cameraList != null) && (cameraList.length > 0)){
            Log.i(TAG,"Try to use camera with id " + cameraId);

            boolean cameraExist = false;
            for (String cm : cameraList){
                if(cm.equals(cameraId)){
                    cameraExist = true;
                    break;
                }
            }

            if(cameraExist){
                int[] modes = mCameraKit.getSupportedModes(cameraId);

                boolean modeAvailable = false;
                for(int mode : modes){
                    if(mode == mCurrentModeType){
                        modeAvailable = true;
                        break;
                    }
                }

                if(!modeAvailable){
                    if(cameraId.equals("0")){
                        Toast.makeText(requireContext(),getString(R.string.msg_rear_camera_does_not_sup_mod_camera_engine),Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(requireContext(),getString(R.string.msg_front_camera_does_not_sup_mod_camera_engine),Toast.LENGTH_LONG).show();
                    }
                }else{
                    try{
                        if(!mCameraOpenCloseLock.tryAcquire(2000, TimeUnit.MILLISECONDS)){
                            throw new RuntimeException(getString(R.string.msg_time_out_waiting_lock_camera_camera_engine));
                        }
                        mCameraKit.createMode(cameraId,mCurrentModeType,mModeStateCallback,mCameraKitHandler);
                        changeCameraInfo(cameraId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.txt_camera_not_exist_camera_engine),Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Change visibility of some elements according to current camera
     * @param cameraId : Current camera ID
     */
    private void changeCameraInfo(String cameraId){
        this.cameraId = cameraId;
        boolean zoom = false;
        if(cameraId.equals("0"))
            zoom = true;
        if(zoom){
            cvZoomIn.setVisibility(View.VISIBLE);
            cvZoomOut.setVisibility(View.VISIBLE);
        }else{
            cvZoomIn.setVisibility(View.INVISIBLE);
            cvZoomOut.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Config preview size,capture size and callbacks of mode, this step have to done after mode creation
     */
    private void configMode() {
        Log.i(TAG, "configMode begin");
        List<Size> previewSizes = mModeCharacteristics.getSupportedPreviewSizes(SurfaceTexture.class);
        List<Size> captureSizes = mModeCharacteristics.getSupportedCaptureSizes(ImageFormat.JPEG);
        Log.d(TAG, "configMode: captureSizes = " + captureSizes.size() + ";previewSizes=" + previewSizes.size());
        mCaptureSize = captureSizes.stream().findFirst().orElse(new Size(4000, 3000));
        final Size tmpPreviewSize = previewSizes.
                stream().filter(new Predicate<Size>() {
            @Override
            public boolean test(Size size) {
                return Math.abs((1.0f * size.getHeight() / size.getWidth()) - (1.0f * mCaptureSize.getHeight() / mCaptureSize.getWidth())) < 0.01;
            }
        }).findFirst().get();

        Log.i(TAG, "configMode: mCaptureSize = " + mCaptureSize + ";mPreviewSize=" + mPreviewSize);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextureView.setAspectRatio(tmpPreviewSize.getHeight(), tmpPreviewSize.getWidth());
            }
        });
        waitTextureViewSizeUpdate(tmpPreviewSize);
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if (texture == null) {
            Log.e(TAG, "configMode: texture=null!");
            return;
        }
        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);
        modeConfigBuilder.addPreviewSurface(surface).addCaptureImage(mCaptureSize, ImageFormat.JPEG);
        modeConfigBuilder.setDataCallback(actionDataCallback, mCameraKitHandler);
        modeConfigBuilder.setStateCallback(actionStateCallback, mCameraKitHandler);
        if (mMode != null) {
            mMode.configure();
        }
        Log.i(TAG, "configMode end");
    }

    private void waitTextureViewSizeUpdate(Size targetPreviewSize) {
        if (mPreviewSize == null) {
            mPreviewSize = targetPreviewSize;
            mPreviewSurfaceChangedDone.close();
            mPreviewSurfaceChangedDone.block(PREVIEW_SURFACE_READY_TIMEOUT);
        } else {
            if (targetPreviewSize.getHeight() * mPreviewSize.getWidth()
                    - targetPreviewSize.getWidth() * mPreviewSize.getHeight() == 0) {
                mPreviewSize = targetPreviewSize;
            } else {
                mPreviewSize = targetPreviewSize;
                mPreviewSurfaceChangedDone.close();
                mPreviewSurfaceChangedDone.block(PREVIEW_SURFACE_READY_TIMEOUT);
            }
        }
    }

    /**
     * Camera kit initialization
     * @return if getting instance is successful return true, else return false and warn user
     */
    private boolean initCameraKit() {
        mCameraKit = CameraKit.getInstance(getContext());
        if (mCameraKit == null) {
            Log.e(TAG, "initCamerakit: this devices not support camerakit or not installed!");
            return false;
        }
        return true;
    }

    /**
     * Start camera kit background thread. Call when camera kit instance is created or onResume of fragment
     */
    private void startBackgroundThread() {
        Log.d(TAG, "startBackgroundThread");
        if (mCameraKitThread == null) {
            mCameraKitThread = new HandlerThread("CameraBackground");
            mCameraKitThread.start();
            mCameraKitHandler = new Handler(mCameraKitThread.getLooper());
            Log.d(TAG, "startBackgroundTThread: mCameraKitThread.getThreadId()=" + mCameraKitThread.getThreadId());
        }
    }

    /**
     * Stop camera kit background thread. Call onPause of fragment
     */
    private void stopBackgroundThread() {
        Log.d(TAG, "stopBackgroundThread");
        if (mCameraKitThread != null) {
            mCameraKitThread.quitSafely();
            try {
                mCameraKitThread.join();
                mCameraKitThread = null;
                mCameraKitHandler = null;
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException in stopBackgroundThread " + e.getMessage());
            }
        }
    }

    /**
     * Capture image by this mode, if mode is null there will be no capture
     * Rotation changes according to current camera ID
     */
    private void captureImage() {
        pbCameraThread.setVisibility(View.VISIBLE);
        Log.i(TAG, "captureImage begin");
        if (mMode != null) {
            if(cameraId.equals("0")){
                mMode.setImageRotation(90);
            }else if(cameraId.equals("1")){
                mMode.setImageRotation(270);
            }
            mFile = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + "_photoMode_pic.jpg");
            mMode.takePicture();
        }
        Log.i(TAG, "captureImage end");
        Toast.makeText(requireContext(),getString(R.string.msg_capture_completed_camera_engine),Toast.LENGTH_LONG).show();
    }

    /**
     * State of camera : READY. After mode creation
     */
    private void cameraReady(){
        requireActivity().runOnUiThread(() -> {
            pbCameraThread.setVisibility(View.GONE);
            clFunctions.animate().alpha(1.0f).setDuration(250);
            ivFlash.setVisibility(View.VISIBLE);


            if(!doubleTapShowed){
                doubleTapShowed = true;
                ViewUtils.showDoubleTap(requireContext(),ivDoubleTap);
            }

            changeFlashState();
        });
    }

    /**
     * State of camera : NOT READY. Before mode creation
     */
    private void cameraNotReady(){
        requireActivity().runOnUiThread(() -> {
            clFunctions.animate().alpha(0.0f).setDuration(0).setDuration(0);
            pbCameraThread.setVisibility(View.VISIBLE);
            ivFlash.setVisibility(View.GONE);
        });
    }

    /**
     * Open or Close flash of mode
     */
    private void changeFlashState( ){
        if(!AppSession.getInstance().isFlashOn()){
            ViewUtils.lightEffect(ivFlash,false);
            mMode.setFlashMode(Metadata.FlashMode.HW_FLASH_CLOSE);
        }else{
            ViewUtils.lightEffect(ivFlash,true);
            mMode.setFlashMode(Metadata.FlashMode.HW_FLASH_ALWAYS_OPEN);
        }
    }

    /**
     * Lifecycle
     */
    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        cameraNotReady();
        if (!initCameraKit()) {
            Toast.makeText(getActivity(),getString(R.string.msg_this_device_not_support_camera_kit_camera_engine),Toast.LENGTH_LONG).show();
            return;
        }

        startBackgroundThread();
        if (mTextureView != null) {
            if (mTextureView.isAvailable()) {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
                mCameraKitHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        createMode(cameraId);
                    }
                });
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        if (mMode != null) {
            mCameraKitHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mCameraOpenCloseLock.acquire();
                        mMode.release();
                        mMode = null;
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
                    } finally {
                        Log.d(TAG, "closeMode:");
                        mCameraOpenCloseLock.release();
                    }
                }
            });

            stopBackgroundThread(); // stop when pause activity or change fragment
        }
        super.onPause();
    }

    //region Listener, Callbacks & Configurations

    private void configurePhotoMode(){
        // Query whether the AI capability is supported. If true is returned, AI scenario identification is supported.
        boolean supported = mModeCharacteristics.getSupportedSceneDetection();

        // Enable AI scene identification. You can disable it afterwards by setting the input parameter to false.
        mMode.setSceneDetection(supported);
    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {

                    if(mCameraKitHandler == null){
                        startBackgroundThread();
                    }
                    mCameraKitHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createMode(cameraId);
                        }
                    });
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                    mPreviewSurfaceChangedDone.open();
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                }
            };

    private final ActionDataCallback actionDataCallback = new ActionDataCallback() {
        @Override
        public void onImageAvailable(Mode mode, @Type int type, Image image) {
            Log.d(TAG, "onImageAvailable: save img");
            switch (type) {
                case Type.TAKE_PICTURE: {
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(mFile);
                        output.write(bytes);
                    } catch (IOException e) {
                        Log.e(TAG, "IOException when write in run");
                    } finally {
                        image.close();
                        if (output != null) {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Log.e(TAG, "IOException when close in run");
                            }
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private final ActionStateCallback actionStateCallback = new ActionStateCallback() {
        @Override
        public void onPreview(Mode mode, int state, PreviewResult result) {
            if (state == PreviewResult.State.PREVIEW_STARTED) {
                Log.i(TAG, "onPreview Started");
                cameraReady();
            }
        }

        @Override
        public void onTakePicture(Mode mode, int state, TakePictureResult result) {
            switch (state) {
                case TakePictureResult.State.ERROR_CAPTURE_NOT_READY:
                    Toast.makeText(requireContext(),getString(R.string.msg_wait_for_current_process_camera_engine),Toast.LENGTH_SHORT).show();
                case TakePictureResult.State.CAPTURE_STARTED:
                    Log.d(TAG, "onState: STATE_CAPTURE_STARTED");
                    break;
                case TakePictureResult.State.CAPTURE_COMPLETED:
                    Log.d(TAG, "onState: STATE_CAPTURE_COMPLETED");

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbCameraThread.setVisibility(View.GONE);
                            ViewUtils.showDialogImagePeekView(getActivity(),getContext(),mFile,getString(R.string.txt_photo_mode_camera_engine));
                            cvLastImage.setVisibility(View.VISIBLE);

                            ViewUtils.setCaptureOnGalleryButton(mFile,ivLastPhoto,requireActivity());
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onSceneDetection(Mode mode, int state, @Nullable SceneDetectionResult result) {
            super.onSceneDetection(mode, state, result);

            // Confirm the effect of the scene to be enabled. For example, the scene "flower" is identified here.
            // After the scene is enabled, the parameters are adjusted to make the flowers look brighter.
            mMode.setParameter(RequestKey.HW_SCENE_EFFECT_ENABLE, true);
        }
    };

    private final ModeStateCallback mModeStateCallback = new ModeStateCallback() {
        @Override
        public void onCreated(Mode mode) {
            Log.d(TAG, "mModeStateCallback onModeOpened: ");
            mCameraOpenCloseLock.release();
            mMode = mode;
            mModeCharacteristics = mode.getModeCharacteristics();
            modeConfigBuilder = mMode.getModeConfigBuilder();

            configMode();
        }

        @Override
        public void onCreateFailed(String cameraId, int modeType, int errorCode) {
            Log.d(TAG,
                    "mModeStateCallback onCreateFailed with errorCode: " + errorCode + " and with cameraId: " + cameraId);
            mCameraOpenCloseLock.release();
        }

        @Override
        public void onConfigured(Mode mode) {
            Log.d(TAG, "mModeStateCallback onModeActivated : ");
            mMode.startPreview();

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cvCaptureImage.setEnabled(true);
                }
            });

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    configurePhotoMode();
                }
            });
        }

        @Override
        public void onConfigureFailed(Mode mode, int errorCode) {
            Log.d(TAG, "mModeStateCallback onConfigureFailed with cameraId: " + mode.getCameraId());
            mCameraOpenCloseLock.release();
        }

        @Override
        public void onFatalError(Mode mode, int errorCode) {
            Log.d(TAG, "mModeStateCallback onFatalError with errorCode: " + errorCode + " and with cameraId: "
                    + mode.getCameraId());
            mCameraOpenCloseLock.release();
        }

        @Override
        public void onReleased(Mode mode) {
            Log.d(TAG, "mModeStateCallback onModeReleased: ");
            mCameraOpenCloseLock.release();
        }
    };

    //endregion
}
