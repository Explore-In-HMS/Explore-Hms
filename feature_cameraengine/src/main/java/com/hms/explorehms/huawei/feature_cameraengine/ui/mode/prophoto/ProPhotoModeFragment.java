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

package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.prophoto;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CaptureRequest;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_cameraengine.R;
import com.hms.explorehms.huawei.feature_cameraengine.application.AppSession;
import com.hms.explorehms.huawei.feature_cameraengine.ui.common.AutoFitTextureView;
import com.hms.explorehms.huawei.feature_cameraengine.common.DoubleClickHandler;
import com.hms.explorehms.huawei.feature_cameraengine.util.ViewUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
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

public class ProPhotoModeFragment extends Fragment {

    //region UI Elements & Object References
    private final String TAG = "CameraEngine";

    private final long PREVIEW_SURFACE_READY_TIMEOUT = 5000L;

    private CameraKit mCameraKit;

    private boolean isGetInstance = false;

    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();

    private Mode mMode;
    private ModeCharacteristics mModeCharacteristics;
    private ModeConfig.Builder modeConfigBuilder;

    private @Mode.Type int mCurrentModeType = Mode.Type.PRO_PHOTO_MODE;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private Size mPreviewSize;
    private Size mCaptureSize;

    private File mFile;

    private HandlerThread mCameraKitThread;
    private Handler mCameraKitHandler;

    private float currentZoom = 1f;
    private float zoomValue = 0.5f;

    private String cameraId = "0";

    //UI
    private MaterialButton buttonHideIntroduction;
    private MaterialButton buttonShowIntroduction;

    private MaterialTextView tvSettingValue;

    private ConstraintLayout clIntroduction;
    private ConstraintLayout clShowIntroduction;
    private ConstraintLayout clFunctions;
    private ConstraintLayout clSettings;
    private ConstraintLayout clSettingValue;

    private CardView cvCaptureImage;
    private CardView cvZoomIn;
    private CardView cvZoomOut;
    private CardView cvLastImage;
    private CardView cvSettings;
    private CardView cvIso;
    private CardView cvExpTime;
    private CardView cvExpHint;
    private CardView cvAwbType;
    private CardView cvWB;
    private CardView cvFocusDist;
    private CardView cvProMeter;

    private ProgressBar pbCameraThread;

    private AutoFitTextureView mTextureView;

    private ImageView ivLastPhoto;
    private ImageView ivFlash;

    private View view;

    private SeekBar seekBarISO;
    private SeekBar seekBarExpTime;
    private SeekBar seekBarAwbType;
    private SeekBar seekBarWB;
    private SeekBar seekBarFocDist;
    private SeekBar seekBarProMeter;

    private SwitchMaterial switchExpHint;

    //app setting
    private boolean isSettingsON = false;

    private int isoLevel;
    private int exposureTime;
    private int awbType;
    private int whiteBalance;
    private boolean expHint;
    private float focusDistance;
    private byte proMeter;

    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pro_photo_mode_camera_engine,container,false);
        initUI();
        initListener();

        return view;
    }

    private void initUI(){
        cvCaptureImage = view.findViewById(R.id.cv_capture_image_pro_pho_camera_eng);
        cvZoomIn = view.findViewById(R.id.cv_zoom_in_pro_pho_camera_engine);
        cvZoomOut = view.findViewById(R.id.cv_zoom_out_pro_pho_camera_engine);
        cvLastImage = view.findViewById(R.id.cv_last_image_pro_pho_camera_engine);
        cvSettings = view.findViewById(R.id.cv_settings_pro_pho_camera_engine);
        cvIso = view.findViewById(R.id.cv_iso_level_pro_p_camera_engine);
        cvExpTime = view.findViewById(R.id.cv_exposure_time_level_pro_p_camera_engine);
        cvExpHint = view.findViewById(R.id.cv_exposure_hint_level_pro_p_camera_engine);
        cvAwbType = view.findViewById(R.id.cv_awb_type_pro_p_camera_engine);
        cvWB = view.findViewById(R.id.cv_man_wb_pro_p_camera_engine);
        cvFocusDist = view.findViewById(R.id.cv_foc_dist_pro_p_camera_engine);
        cvProMeter = view.findViewById(R.id.cv_pro_m_pro_p_camera_engine);

        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_pro_pho_camera_engine);
        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_pro_pho_camera_engine);

        clIntroduction = view.findViewById(R.id.cl_mode_introduction_pro_pho_camera_engine);
        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_pro_pho_camera_engine);
        clFunctions = view.findViewById(R.id.cl_pro_pho_functions_camera_engine);
        clSettings = view.findViewById(R.id.cl_pro_photo_functions_plus_camera_engine);
        clSettingValue = view.findViewById(R.id.cl_night_functions_plus_level_camera_engine);

        mTextureView = view.findViewById(R.id.aftv_pro_pho_camera_engine);

        pbCameraThread = view.findViewById(R.id.pb_pro_pho_mode_camera_engine);

        seekBarISO = view.findViewById(R.id.seekbar_iso_pro_p_camera_engine);
        seekBarExpTime = view.findViewById(R.id.seekbar_exp_time_pro_p_camera_engine);
        seekBarAwbType = view.findViewById(R.id.seekbar_awb_type_pro_p_camera_engine);
        seekBarWB = view.findViewById(R.id.seekbar_man_wb_pro_p_camera_engine);
        seekBarFocDist = view.findViewById(R.id.seekbar_foc_dist_pro_p_camera_engine);
        seekBarProMeter = view.findViewById(R.id.seekbar_pro_meter_pro_p_camera_engine);

        switchExpHint = view.findViewById(R.id.switch_exp_hint_pro_p_camera_engine);

        tvSettingValue = view.findViewById(R.id.tv_settings_value_p_camera_engine);

        ivLastPhoto = view.findViewById(R.id.iv_last_photo_pro_photo_camera_engine);
        ivFlash = view.findViewById(R.id.iv_flash_pro_photo_mode_camera_engine);
    }

    /**
     * Init listener of elements
     */
    private void initListener(){
        cvCaptureImage.setOnClickListener(v -> captureImage());

        buttonHideIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clIntroduction.animate().alpha(0.0f).setDuration(250);
                clShowIntroduction.setVisibility(View.VISIBLE);
            }
        });

        buttonShowIntroduction.setOnClickListener(v -> {
            clIntroduction.animate().alpha(1.0f).setDuration(250);
            clShowIntroduction.setVisibility(View.GONE);
        });

        cvZoomIn.setOnClickListener(v -> {
            currentZoom += zoomValue;
            int result = mMode.setZoom(currentZoom);

            if(result == 0)
                ViewUtils.showSettingOnCenter(tvSettingValue,String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
            else
                currentZoom -= zoomValue;
        });

        cvZoomOut.setOnClickListener(v -> {
            currentZoom -= zoomValue;

            int result = mMode.setZoom(currentZoom);

            if(result == 0)
                ViewUtils.showSettingOnCenter(tvSettingValue,String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
            else
                currentZoom += zoomValue;
        });

        cvLastImage.setOnClickListener(v -> {
            if(mFile != null){
                ViewUtils.showDialogImagePeekView(getActivity(),getContext(),mFile,getString(R.string.txt_pro_p_camera_engine));
            }else{
                Toast.makeText(getContext(),getString(R.string.txt_no_picture_camera_engine),Toast.LENGTH_SHORT).show();
            }
        });

        cvSettings.setOnClickListener(v -> {
            if(!isSettingsON) {
                clSettings.animate().alpha(1.0f).setDuration(250);
                clSettings.setVisibility(View.VISIBLE);
            }else{
                clSettings.animate().alpha(0.0f).setDuration(250);
                clSettings.setVisibility(View.INVISIBLE);
                clSettingValue.setVisibility(View.INVISIBLE);
                changeViewColor(null);
            }
            isSettingsON = !isSettingsON;
        });

        cvIso.setOnClickListener(v -> {
            showRelatedView(seekBarISO);
            changeViewColor(cvIso);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(isoLevel));
        });
        cvExpTime.setOnClickListener(v -> {
            showRelatedView(seekBarExpTime);
            changeViewColor(cvExpTime);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(exposureTime));
        });
        cvExpHint.setOnClickListener(v -> {
            showRelatedView(switchExpHint);
            changeViewColor(cvExpHint);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(expHint));
        });
        cvAwbType.setOnClickListener(v -> {
            showRelatedView(seekBarAwbType);
            changeViewColor(cvAwbType);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(awbType));
        });
        cvWB.setOnClickListener(v -> {
            showRelatedView(seekBarWB);
            changeViewColor(cvWB);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(whiteBalance));
        });

        cvFocusDist.setOnClickListener(v -> {
            showRelatedView(seekBarFocDist);
            changeViewColor(cvFocusDist);
        });
        cvProMeter.setOnClickListener(v -> {
            showRelatedView(seekBarProMeter);
            changeViewColor(cvProMeter);
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
     * Create Pro Photo mode
     * @param cameraId : current camera, rear or front
     */
    private void createMode(String cameraId){
        if(!isGetInstance){
            try {
                mCameraKit = CameraKit.getInstance(getContext());
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
                            throw new RuntimeException("Time out waiting to lock camera opening");
                        }
                        mCameraKit.createMode(cameraId,mCurrentModeType,mModeStateCallback,mCameraKitHandler);
                        this.cameraId = cameraId;
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
        //TODO CAPTURE START
        pbCameraThread.setVisibility(View.VISIBLE);
        Log.i(TAG, "captureImage begin");
        if (mMode != null) {
            mMode.setImageRotation(90);
            mFile = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + "pic.jpg");
            mMode.takePicture();
        }
        Log.i(TAG, "captureImage end");
        Toast.makeText(requireContext(),getString(R.string.msg_capture_completed_camera_engine),Toast.LENGTH_LONG).show();
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
     * State of camera : READY. After mode creation
     */
    private void cameraReady(){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbCameraThread.setVisibility(View.GONE);
                clFunctions.animate().alpha(1.0f).setDuration(250);
                ivFlash.setVisibility(View.VISIBLE);

                changeFlashState();
            }
        });
    }

    /**
     * State of camera : NOT READY. Before mode creation
     */
    private void cameraNotReady(){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbCameraThread.setVisibility(View.VISIBLE);
                clFunctions.animate().alpha(0.0f).setDuration(0);
                clSettings.setVisibility(View.INVISIBLE);
                clSettingValue.setVisibility(View.INVISIBLE);
                ivFlash.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Show related view of setting when user tap to setting cardView
     * @param view target setting
     */
    private void showRelatedView(View view){
        seekBarISO.setVisibility(View.INVISIBLE);
        seekBarExpTime.setVisibility(View.INVISIBLE);
        seekBarAwbType.setVisibility(View.INVISIBLE);
        seekBarWB.setVisibility(View.INVISIBLE);
        seekBarFocDist.setVisibility(View.INVISIBLE);
        seekBarProMeter.setVisibility(View.INVISIBLE);
        switchExpHint.setVisibility(View.INVISIBLE);

        clSettingValue.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(250);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Change color of setting when user tap to setting cardView
     * @param view target setting
     */
    private void changeViewColor(View view){
        cvIso.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvProMeter.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvFocusDist.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvAwbType.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvExpHint.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvWB.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvExpTime.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));

        if(view != null)
            ((CardView)view).setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBlackCameraEngine));
    }

    /**
     * LifeCyle
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

    private void configureProMode(){
        // Query the functions supported by the mode.
        List<CaptureRequest.Key<?>> parameters = mModeCharacteristics.getSupportedParameters();

        if ((parameters != null)) {

            if((parameters.contains(RequestKey.HW_PRO_SENSOR_ISO_VALUE))){
                List<Integer> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_SENSOR_ISO_VALUE);

                seekBarISO.setMax(values.size() - 1);
                seekBarISO.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        isoLevel = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_PRO_SENSOR_ISO_VALUE, values.get(progress));
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(isoLevel));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters).contains(RequestKey.HW_PRO_SENSOR_EXPOSURE_TIME_VALUE)){
                List<Integer> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_SENSOR_EXPOSURE_TIME_VALUE);

                seekBarExpTime.setMax(values.size() - 1);
                seekBarExpTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        exposureTime = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_PRO_SENSOR_EXPOSURE_TIME_VALUE, values.get(progress));
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(exposureTime));
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters).contains(RequestKey.HW_PRO_AWB_TYPE)){
                List<Integer> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_AWB_TYPE);

                seekBarAwbType.setMax(values.size() - 1);

                seekBarAwbType.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        awbType = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_PRO_AWB_TYPE, values.get(progress));
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(awbType));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters).contains(RequestKey.HW_PRO_MANUAL_WB_VALUE)){
                List<Integer> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_MANUAL_WB_VALUE);

                seekBarWB.setMax(values.size() - 1);

                seekBarWB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        whiteBalance = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_PRO_MANUAL_WB_VALUE, values.get(progress));
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(whiteBalance));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

            }

            if((parameters).contains(RequestKey.HW_PRO_EXPOSURE_HINT)){
                List<Boolean> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_EXPOSURE_HINT);

                switchExpHint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        expHint = isChecked;
                        int result =  mMode.setParameter(RequestKey.HW_PRO_EXPOSURE_HINT, isChecked);
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(expHint));
                    }
                });

            }

            if((parameters).contains(RequestKey.HW_PRO_FOCUS_DISTANCE_VALUE)){
                List<Float> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_FOCUS_DISTANCE_VALUE);

                seekBarFocDist.setMax(values.size() -1 );
                seekBarFocDist.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        focusDistance = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_PRO_FOCUS_DISTANCE_VALUE, values.get(progress));
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(focusDistance));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters).contains(RequestKey.HW_PRO_METERING_VALUE)){
                List<Byte> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_METERING_VALUE);

                seekBarProMeter.setMax(values.size() - 1);
                seekBarProMeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        proMeter = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_PRO_METERING_VALUE, values.get(progress));
                        if(result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,""+proMeter);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

        }

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

                    //TODO CAPTURE FINISHED
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbCameraThread.setVisibility(View.GONE);
                            ViewUtils.showDialogImagePeekView(getActivity(),getContext(),mFile,getString(R.string.txt_pro_p_camera_engine));
                            cvLastImage.setVisibility(View.VISIBLE);
                            ViewUtils.setCaptureOnGalleryButton(mFile,ivLastPhoto,requireActivity());
                        }
                    });
                    break;
                default:
                    break;
            }
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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cvCaptureImage.setEnabled(true);
                }
            });

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    configureProMode();
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
