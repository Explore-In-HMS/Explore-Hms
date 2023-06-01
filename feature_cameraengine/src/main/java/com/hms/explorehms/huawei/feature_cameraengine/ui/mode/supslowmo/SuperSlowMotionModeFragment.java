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

package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.supslowmo;

import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
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
import com.huawei.camera.camerakit.ActionStateCallback;
import com.huawei.camera.camerakit.CameraKit;
import com.huawei.camera.camerakit.Metadata;
import com.huawei.camera.camerakit.Mode;
import com.huawei.camera.camerakit.ModeCharacteristics;
import com.huawei.camera.camerakit.ModeConfig;
import com.huawei.camera.camerakit.ModeStateCallback;
import com.huawei.camera.camerakit.RequestKey;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SuperSlowMotionModeFragment extends Fragment {

    //region UI Elements & Object References
    private final String TAG = "CameraEngine";

    private CameraKit mCameraKit;

    private boolean isGetInstance = false;

    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();

    private Mode mMode;
    private ModeCharacteristics mModeCharacteristics;
    private ModeConfig.Builder modeConfigBuilder;

    private @Mode.Type int mCurrentModeType = Mode.Type.SUPER_SLOW_MOTION;

    private Surface mPreviewSurface;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

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

    private CardView cvRecordOrStop;
    private CardView cvZoomIn;
    private CardView cvZoomOut;
    private CardView cvLastVideo;

    private ImageView ivRecord;
    private ImageView ivFlash;

    private ProgressBar pbCameraThread;

    private AutoFitTextureView mTextureView;

    private View view;

    private int mRecordFps;
    private Size mRecordSize;

    private String cameraId = "0";

    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_super_slow_mo_camera_engine,container,false);
        initUI();
        initListener();

        return view;
    }

    private void initUI(){
        cvRecordOrStop = view.findViewById(R.id.cv_start_stop_super_slow_mo_camera_eng);
        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_super_slow_mo_camera_engine);
        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_super_slow_mo_camera_engine);
        clIntroduction = view.findViewById(R.id.cl_mode_introduction_super_slow_mo_camera_engine);
        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_super_slow_mo_camera_engine);
        clFunctions = view.findViewById(R.id.cl_super_slow_mo_functions_camera_engine);
        mTextureView = view.findViewById(R.id.aftv_super_slow_mo_camera_engine);
        pbCameraThread = view.findViewById(R.id.pb_super_slow_mo_mode_camera_engine);
        cvZoomIn = view.findViewById(R.id.cv_zoom_in_super_slow_mo_camera_engine);
        cvZoomOut = view.findViewById(R.id.cv_zoom_out_super_slow_mo_camera_engine);
        tvZoomLevel = view.findViewById(R.id.tv_zoom_level_super_slow_mo_camera_engine);
        cvLastVideo = view.findViewById(R.id.cv_last_video_super_slow_mo_camera_engine);
        ivRecord = view.findViewById(R.id.iv_record_slow_mo_camera_engine);
        ivFlash = view.findViewById(R.id.iv_flash_super_slow_mode_camera_engine);
    }

    /**
     * Init listener of elements
     */
    private void initListener(){
        cvRecordOrStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
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

        cvLastVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFile != null){
                    ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_super_slow_mode_camera_engine) + " - "+ mRecordFps + "fps");
                }else
                    Toast.makeText(getContext(),getString(R.string.txt_no_record_camera_engine),Toast.LENGTH_SHORT).show();
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
     * Create Super Slow Motion Mode
     * @param cameraId : current camera, rear or front
     */
    private void createMode(String cameraId) {
        if(!isGetInstance){
            try {
                mCameraKit = CameraKit.getInstance(requireContext());
                if (mCameraKit == null) {
                    Log.e(TAG, "initCamerakit: this devices not support camerakit or not installed!");
                    return;
                }
            }catch (NoSuchMethodError e){
                Log.e(TAG,"This version CameraEngine does not contain VersionInfoInterface");
            }finally {
                isGetInstance = true;
            }
        }

        if(mCameraKit == null){
            return;
        }

        // Query the camera ID list.
        String[] cameraList = mCameraKit.getCameraIdList();
        if ((cameraList != null) && (cameraList.length > 0)) {
            Log.i(TAG, "Try to use camera with id " + cameraId);

            boolean cameraExist = false;
            for (String cm : cameraList){
                if(cm.equals(cameraId)){
                    cameraExist = true;
                    break;
                }
            }

            if(cameraExist){

                // Query the modes supported by the current device.
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
                }else {
                    try {
                        if (!mCameraOpenCloseLock.tryAcquire(2000, TimeUnit.MILLISECONDS)) {
                            throw new RuntimeException(getString(R.string.msg_time_out_waiting_lock_camera_camera_engine));
                        }
                        mCameraKit.createMode(cameraId, mCurrentModeType, mModeStateCallback, mCameraKitHandler);
                        this.cameraId = cameraId;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /** Obtain the mode object from the onCreated callback, and initialize ModeConfig.
     * Builder by using the behavior status callback, data callback, and the corresponding thread as input parameters.
     * Configure the preview surface and recording size, and call the configure method to set the configuration items to mMode to activate the mode.
     */
    private void configMode() {
        try {
            mCameraOpenCloseLock.acquire();

            mModeCharacteristics = mCameraKit.getModeCharacteristics(cameraId, mCurrentModeType);
            List<Size> previewSizes = mModeCharacteristics.getSupportedPreviewSizes(SurfaceTexture.class);
            mModeCharacteristics.getParameterRange(RequestKey.HW_SUPER_SLOW_CHECK_AREA);
            Map<Integer, List<Size>> recordSizes = mModeCharacteristics.getSupportedVideoSizes(MediaRecorder.class);
            // Obtain the recording frame rate and resolution. The two configuration items must be set in pairs based on the map returned by the modeCharacteristics.getSupportedVideoSizes().
            if (recordSizes.containsKey(Metadata.FpsRange.HW_FPS_960)) {
                mRecordFps = Metadata.FpsRange.HW_FPS_960;
                mRecordSize = recordSizes.get(mRecordFps).get(0);
            } else {
                Log.e(TAG, "prepareConfig: Internal error");
                return;
            }
            Log.d(TAG, "prepareConfig: recordFps = " + mRecordFps + ", recordSize = " + mRecordSize);
            // The video resolution must be the same as the preview resolution.
            if (!previewSizes.contains(mRecordSize)) {
                Log.e(TAG, "preparePreviewSurface: the previewSize and recordSize should be the same, Internal error!");
                return;
            }
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            if (surfaceTexture != null) {
                surfaceTexture.setDefaultBufferSize(mRecordSize.getWidth(), mRecordSize.getHeight());
                mPreviewSurface = new Surface(surfaceTexture);
            }
            modeConfigBuilder = mMode.getModeConfigBuilder();
            modeConfigBuilder.setStateCallback(mActionStateCallback, mCameraKitHandler);
            modeConfigBuilder.setVideoFps(mRecordFps);
            modeConfigBuilder.addVideoSize(mRecordSize);
            modeConfigBuilder.addPreviewSurface(mPreviewSurface);
            mMode.configure();
        } catch (InterruptedException e) {
            Log.e(TAG, "prepareModeConfig fail " + e.getMessage());
        } finally {
            mCameraOpenCloseLock.release();
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
     * Start video record, set recording path to current mode and update UI elements
     */
    private void startRecord() {
        ivRecord.setBackgroundResource(R.drawable.icon_stop_camera_engine);
        mFile = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + "superSlowMo.mp4");
        mMode.startRecording(mFile);
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
     * State of camera : NOT READY. Before mode creation
     */
    private void cameraNotReady(){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbCameraThread.setVisibility(View.VISIBLE);
                clFunctions.animate().alpha(0.0f).setDuration(0);
                ivFlash.setVisibility(View.GONE);
            }
        });
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

    /* The mode needs to be released when the app is closed or switched to the background. */
    @Override
    public void onPause() {
        if (mCameraKitHandler != null) {
            mCameraKitHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mMode!= null) {
                        mMode.release();
                        mMode = null;
                    }
                }
            });
            stopBackgroundThread();
        }
        super.onPause();
    }


    //region Listener, Callbacks & Configurations

    /* Use the action status callback as an example to obtain the callback of the execution status result, for example, preparing for, starting, ending, and saving Super slow-mo recording. */
    private ActionStateCallback mActionStateCallback = new ActionStateCallback() {
        @Override
        public void onPreview(Mode mode, int state, PreviewResult result) {
            if (state == PreviewResult.State.PREVIEW_STARTED) {
                Log.i(TAG, "onPreview Started");
                cameraReady();
            }
        }

        @Override
        public void onRecording(Mode mode, int state, RecordingResult result) {
            switch (state) {

                //The bottom-layer initialization is not ready.
                case RecordingResult.State.ERROR_RECORDING_NOT_READY:
                    requireActivity().runOnUiThread(
                            () -> Toast.makeText(getActivity(), getString(R.string.txt_not_ready_camera_engine), Toast.LENGTH_SHORT).show());
                    break;
                // Recording is stopped.
                case RecordingResult.State.RECORDING_STOPPED:
                    requireActivity().runOnUiThread(() -> {
                        ivRecord.setBackgroundResource(R.drawable.icon_record_camera_engine);
                        pbCameraThread.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(),getString(R.string.msg_record_stopped_saving_camera_engine),Toast.LENGTH_SHORT).show();
                    });
                    break;
                // The recorded file is completed & saved.
                case RecordingResult.State.RECORDING_FILE_SAVED:
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pbCameraThread.setVisibility(View.GONE);
                        }
                    });
                    ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_super_slow_mode_camera_engine) + " - "+ mRecordFps + "fps");
                    break;
                default:
                    break;
            }
        }
    };

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
                    cvRecordOrStop.setEnabled(true);
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
