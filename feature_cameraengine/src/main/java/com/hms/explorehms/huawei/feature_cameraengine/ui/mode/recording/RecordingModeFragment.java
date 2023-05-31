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

package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.recording;

import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaCodec;
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
import com.hms.explorehms.huawei.feature_cameraengine.ui.common.AutoFitTextureView;
import com.hms.explorehms.huawei.feature_cameraengine.common.DoubleClickHandler;
import com.hms.explorehms.huawei.feature_cameraengine.helper.RecordTimeHelper;
import com.hms.explorehms.huawei.feature_cameraengine.util.ViewUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RecordingModeFragment extends Fragment {

    //region UI elements & Object References
    private static final String TAG = "CameraKit";

    private final long PREVIEW_SURFACE_READY_TIMEOUT = 5000L;

    private boolean isGetInstance = false;

    private static final int VIDEO_ENCODING_BIT_RATE = 10000000;

    private static final int VIDEO_FRAME_RATE = 30;

    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();

    private Size mCaptureSize;
    private Size mPreviewSize;

    private Surface mPreviewSurface;
    private Surface mVideoSurface;

    private Mode mMode;
    private ModeCharacteristics mModeCharacteristics;
    private ModeConfig.Builder modeConfigBuilder;

    private @Mode.Type int mCurrentModeType = Mode.Type.VIDEO_MODE;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private Semaphore mStartStopRecordLock = new Semaphore(1);

    private CameraKit mCameraKit;

    private MediaRecorder mMediaRecorder;

    private File mFile = null;

    private Size mRecordSize;

    private boolean mIsFirstRecord = true;

    private float currentZoom = 1f;
    private float zoomValue = 0.5f;

    private String cameraId = "0";

    private HandlerThread mCameraKitThread;
    private Handler mCameraKitHandler;

    private boolean isRecording = false;
    private boolean isPaused = false;

    private RecordTimeHelper timer;

    //UI
    private AutoFitTextureView mTextureView;

    private MaterialButton buttonHideIntroduction;
    private MaterialButton buttonShowIntroduction;

    private MaterialTextView tvSettingValue;
    private MaterialTextView tvRecordTime;

    private ConstraintLayout clIntroduction;
    private ConstraintLayout clShowIntroduction;
    private ConstraintLayout clFunctions;
    private ConstraintLayout clSettings;
    private ConstraintLayout clSettingValue;

    private CardView cvStartStopRecord;
    private CardView cvPauseResumeRecord;
    private CardView cvZoomIn;
    private CardView cvZoomOut;
    private CardView clLastRecord;
    private CardView cvSettings;
    private CardView cvSettingAI;
    private CardView cvSettingFilter;
    private CardView cvSettingFilterLevel;
    private CardView cvSettingVideoStabilization;

    private SeekBar seekBarAI;
    private SeekBar seekBarFilter;
    private SeekBar seekBarFilterLevel;

    private SwitchMaterial switchBarVideoStabl;

    private ProgressBar pbCameraThread;

    private ImageView ivStartStopRecord;
    private ImageView ivPauseResumeRecord;
    private ImageView ivDoubleTap;

    private View view;

    private boolean isSettingsOn;
    private boolean doubleTapShowed = false;

    //values
    private byte settingAI;
    private byte settingFilter;
    private int settingFilterLevel;
    private boolean settingVideoStabl;

    private boolean hasRecord = false;

    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recording_mode_camera_engine,container,false);
        initUI();
        initListener();
        return view;
    }

    private void initUI(){
        cvStartStopRecord = view.findViewById(R.id.cv_start_stop_recording_camera_eng);
        cvPauseResumeRecord = view.findViewById(R.id.cv_pause_resume_recording_camera_eng);
        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_recording_camera_engine);
        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_recording_camera_engine);
        clIntroduction = view.findViewById(R.id.cl_mode_introduction_recording_camera_engine);
        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_recording_camera_engine);
        clFunctions = view.findViewById(R.id.cl_recording_functions_camera_engine);
        clSettings = view.findViewById(R.id.cl_recording_functions_plus_camera_engine);
        clSettingValue = view.findViewById(R.id.cl_recording_functions_plus_level_camera_engine);
        mTextureView = view.findViewById(R.id.aftv_recording_camera_engine);
        pbCameraThread = view.findViewById(R.id.pb_recording_mode_camera_engine);
        cvZoomIn = view.findViewById(R.id.cv_zoom_in_recording_camera_engine);
        cvZoomOut = view.findViewById(R.id.cv_zoom_out_recording_camera_engine);
        clLastRecord = view.findViewById(R.id.cv_last_image_recording_camera_engine);
        cvSettings = view.findViewById(R.id.cv_settings_recording_pho_camera_engine);
        tvRecordTime = view.findViewById(R.id.tv_time_recording_camera_engine);
        ivStartStopRecord = view.findViewById(R.id.iv_record_start_stop_recording_camera_engine);
        ivPauseResumeRecord = view.findViewById(R.id.iv_record_pause_resume_recording_camera_engine);
        cvSettingAI = view.findViewById(R.id.cv_ai_recording_camera_engine);
        cvSettingFilter = view.findViewById(R.id.cv_filter_recording_camera_engine);
        cvSettingFilterLevel = view.findViewById(R.id.cv_filter_level_recording_camera_engine);
        cvSettingVideoStabilization = view.findViewById(R.id.cv_stabilization_recording_camera_engine);
        seekBarAI = view.findViewById(R.id.seekbar_ai_recording_camera_engine);
        seekBarFilter = view.findViewById(R.id.seekbar_filter_recording_camera_engine);
        seekBarFilterLevel = view.findViewById(R.id.seekbar_filter_level_recording_camera_engine);
        switchBarVideoStabl = view.findViewById(R.id.switch_video_stab_camera_engine);
        tvSettingValue = view.findViewById(R.id.tv_settings_value_recording_camera_engine);
        ivDoubleTap = view.findViewById(R.id.iv_double_tap_recording_mode_camera_engine);
        timer = new RecordTimeHelper(requireActivity(),this.tvRecordTime);
    }

    /**
     * Init listener of elements
     */
    private void initListener(){
        cvStartStopRecord.setOnClickListener(v -> {
            if(!isRecording){
                startRecording();
            }
            else{
                stopRecording();
            }
            isRecording = !isRecording;
        });

        cvPauseResumeRecord.setOnClickListener(v -> {
            if(!isPaused){
                pauseRecording();
            }
            else{
                resumeRecording();
            }
            isPaused = !isPaused;
        });

        buttonHideIntroduction.setOnClickListener(v -> {
            clIntroduction.animate().alpha(0.0f).setDuration(250);
            clShowIntroduction.setVisibility(View.VISIBLE);
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

        clLastRecord.setOnClickListener(v -> {
            if(mFile != null && hasRecord){
                ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_recording_mode_camera_engine));
            }else{
                Toast.makeText(getContext(),getString(R.string.txt_no_record_camera_engine),Toast.LENGTH_SHORT).show();
            }
        });

        cvSettings.setOnClickListener(v -> {
            if(!isSettingsOn) {
                clSettings.animate().alpha(1.0f).setDuration(250);
                clSettings.setVisibility(View.VISIBLE);
            }else{
                clSettings.animate().alpha(0.0f).setDuration(250);
                clSettings.setVisibility(View.INVISIBLE);
                clSettingValue.setVisibility(View.INVISIBLE);
                changeViewColor(null);
            }
            isSettingsOn = !isSettingsOn;
        });

        cvSettingAI.setOnClickListener(v -> {
            showRelatedView(seekBarAI);
            changeViewColor(cvSettingAI);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingAI));
        });

        cvSettingFilter.setOnClickListener(v -> {
            showRelatedView(seekBarFilter);
            changeViewColor(cvSettingFilter);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingFilter));
        });

        cvSettingFilterLevel.setOnClickListener(v -> {
            showRelatedView(seekBarFilterLevel);
            changeViewColor(cvSettingFilterLevel);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingFilterLevel));
        });

        cvSettingVideoStabilization.setOnClickListener(v -> {
            showRelatedView(switchBarVideoStabl);
            changeViewColor(cvSettingVideoStabilization);
            ViewUtils.showSettingOnCenter(tvSettingValue, String.valueOf(settingVideoStabl));
        });

        mTextureView.setOnClickListener(new DoubleClickHandler(v -> {
            if(!isRecording){
                if(cameraId.equals("0")){
                    createMode("1");
                }
                else{
                    createMode("0");
                }
            }
        }));
    }

    /**
     * Create Recording mode
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
        try {
            mCameraOpenCloseLock.acquire();
            Map<Integer, List<Size>> previewSizes = mModeCharacteristics.getSupportedVideoSizes(MediaRecorder.class);
            List<Size> captureSizes = mModeCharacteristics.getSupportedCaptureSizes(ImageFormat.JPEG);

            if(captureSizes != null && captureSizes.size() > 0){
                mCaptureSize = captureSizes.stream().findFirst().orElse(new Size(1920, 1080));

                if(previewSizes.get(Metadata.FpsRange.HW_FPS_30) != null){
                    final Size tmpPreviewSize = previewSizes.get(Metadata.FpsRange.HW_FPS_30).
                            stream().filter(size -> Math.abs((1.0f * size.getHeight() / size.getWidth()) - (1.0f * mCaptureSize.getHeight() / mCaptureSize.getWidth())) < 0.01).findFirst().get();

                    Log.i(TAG, "configMode: mCaptureSize = " + mCaptureSize + ";mPreviewSize=" + mPreviewSize);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextureView.setAspectRatio(tmpPreviewSize.getHeight(), tmpPreviewSize.getWidth());
                        }
                    });

                    waitTextureViewSizeUpdate(tmpPreviewSize);

                    mRecordSize = tmpPreviewSize;
                }
            }

            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            if (texture == null) {
                Log.e(TAG, "activeVideoModePreview: texture=null!");
                return;
            }

            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewSurface = new Surface(texture);
            mVideoSurface = MediaCodec.createPersistentInputSurface();
            setUpMediaRecorder(mRecordSize, mVideoSurface);
            modeConfigBuilder.addPreviewSurface(mPreviewSurface);
            modeConfigBuilder.addVideoSurface(mVideoSurface);
            modeConfigBuilder.setStateCallback(mActionStateCallback,mCameraKitHandler);
            if (mMode != null) {
                mIsFirstRecord = true;
                mMode.configure();
            }
        }catch (InterruptedException e){
            Log.e(TAG,"prepareModeConfig fail "+e.getMessage());
        }finally {
            mCameraOpenCloseLock.release();
        }

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

        mCameraKit = CameraKit.getInstance(requireContext());
        if (mCameraKit == null) {
            Log.e(TAG, getString(R.string.msg_this_device_not_support_camera_kit_camera_engine));
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
     * Start video record, update UI elements
     */
    private void startRecording() {
        try {
            acquireStartStopRecordLock();
            ivStartStopRecord.setBackgroundResource(R.drawable.icon_stop_camera_engine);
            ivPauseResumeRecord.setBackgroundResource(R.drawable.icon_pause_camera_engine);
            cvPauseResumeRecord.setVisibility(View.VISIBLE);
            tvRecordTime.setVisibility(View.VISIBLE);

            if (!mIsFirstRecord) {
                setUpMediaRecorder(mRecordSize, mVideoSurface);
            }
            mIsFirstRecord = false;
            mMode.startRecording();
            mMediaRecorder.start();

            timer.startTimer();

            Log.d(TAG, "Recording starts!");
        } catch (InterruptedException e) {
            Log.e(TAG, "acquiremStartStopRecordLock failed");
        } catch (IllegalStateException e) {
            Log.e(TAG, "mMediaRecorder prepare not well!");
            clearInvalidFile();
        } finally {
            releaseStartStopRecordLock();
        }
    }

    /**
     * Stop video record, update UI elements
     */
    private void stopRecording(){
        try {
            acquireStartStopRecordLock();
            lockRecording();
            releaseRecording();
            timer.stopTimer();
        } catch (InterruptedException e) {
            Log.e(TAG, "acquiremStartStopRecordLock failed");
        } catch (IllegalStateException e) {
            Log.e(TAG, "mMediaRecorder stop state error");
        } catch (RuntimeException stopException) {
            Log.e(TAG, "going to clean up the invalid output file");
            clearInvalidFile();
        } finally {
            releaseStartStopRecordLock();
        }
    }

    /**
     * Pause video record, update UI elements
     */
    private void pauseRecording(){
        mMode.pauseRecording();
        mMediaRecorder.pause();
        timer.pauseTimer();
        ivPauseResumeRecord.setBackgroundResource(R.drawable.icon_play_camera_engine);
    }

    /**
     * Resume video record, update UI elements
     */
    private void resumeRecording(){
        mMode.resumeRecording();
        mMediaRecorder.resume();
        timer.resumeTimer();
        ivPauseResumeRecord.setBackgroundResource(R.drawable.icon_pause_camera_engine);
    }

    /**
     * Update UI elements & stop recording
     */
    private void lockRecording(){
        ivStartStopRecord.setBackgroundResource(R.drawable.icon_record_camera_engine);
        pbCameraThread.setVisibility(View.VISIBLE);
        clFunctions.setVisibility(View.INVISIBLE);
        clSettings.setVisibility(View.INVISIBLE);
        clSettingValue.setVisibility(View.INVISIBLE);
        cvPauseResumeRecord.setVisibility(View.GONE);
        tvRecordTime.setVisibility(View.INVISIBLE);


        mMode.stopRecording();
        mMediaRecorder.stop();
    }

    /**
     * Update UI elements
     */
    private void releaseRecording(){
        clFunctions.setVisibility(View.VISIBLE);
        pbCameraThread.setVisibility(View.INVISIBLE);
        ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_recording_mode_camera_engine));
        hasRecord = true;
    }

    /**
     * Acquire record thread when record start or stop
     * @throws InterruptedException
     */
    private void acquireStartStopRecordLock() throws InterruptedException {
        if (mStartStopRecordLock != null) {
            mStartStopRecordLock.acquire();
        } else {
            Log.d(TAG, "acquiremStartStopRecordLock, mStartStopRecordLock refer null");
        }
    }

    /**
     * Release record thread when record start or stop
     */
    private void releaseStartStopRecordLock() {
        if (mStartStopRecordLock != null) {
            if (mStartStopRecordLock.availablePermits() < 1) {
                mStartStopRecordLock.release();
            }
        } else {
            Log.d(TAG, "release lock, but it is null");
        }
    }

    /**
     * Clear video file if file is not valid
     */
    private void clearInvalidFile() {
        if (mFile.exists()) {
            mFile.delete();
            mFile = null;
            Log.d(TAG, "invalid video file deleted!");
        }
    }

    /**
     * Set up media recorder to record video by current mode
     * @param size video size
     * @param surface video surface of current mode
     */
    private void setUpMediaRecorder(Size size, Surface surface) {
        if(mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mFile =  new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + "_recording.mp4");
        mMediaRecorder.setOutputFile(mFile);
        mMediaRecorder.setVideoEncodingBitRate(VIDEO_ENCODING_BIT_RATE);
        mMediaRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
        mMediaRecorder.setVideoSize(size.getWidth(), size.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if(cameraId.equals("0"))
            mMediaRecorder.setOrientationHint(90);
        else
            mMediaRecorder.setOrientationHint(270);
        mMediaRecorder.setInputSurface(surface);
        try {
            mMediaRecorder.prepare();
            Log.d(TAG, "mMediaRecorder prepare done!");
        } catch (IOException e) {
            Log.e(TAG, "mMediaRecorder prepare ioe exception " + e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "mMediaRecorder prepare state error");
        }
    }

    /**
     * Show related view of setting when user tap to setting cardView
     * @param view target setting
     */
    private void showRelatedView(View view){
        seekBarAI.setVisibility(View.INVISIBLE);
        seekBarFilter.setVisibility(View.INVISIBLE);
        seekBarFilterLevel.setVisibility(View.INVISIBLE);
        switchBarVideoStabl.setVisibility(View.INVISIBLE);

        clSettingValue.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(250);
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Change color of setting when user tap to setting cardView
     * @param view target setting
     */
    private void changeViewColor(View view){
        cvSettingAI.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvSettingFilter.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvSettingFilterLevel.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));
        cvSettingVideoStabilization.setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryCameraEngine));

        if(view != null)
            ((CardView)view).setCardBackgroundColor(ContextCompat.getColor(getContext(),R.color.colorBlackCameraEngine));
    }

    /**
     * State of camera : READY. After mode creation
     */
    private void cameraReady(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbCameraThread.setVisibility(View.GONE);
                clFunctions.animate().alpha(1.0f).setDuration(250);
                tvRecordTime.setVisibility(View.VISIBLE);

                if(!doubleTapShowed){
                    doubleTapShowed = true;
                    ViewUtils.showDoubleTap(requireContext(),ivDoubleTap);
                }
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
                tvRecordTime.setVisibility(View.INVISIBLE);
                clSettings.setVisibility(View.INVISIBLE);
                clSettingValue.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * LifeCyle
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mTextureView != null) {
            Log.d(TAG, "onResume: setSurfaceTextureListener: ");
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
    private void configureTransform(TextureView view, Size previewSize, Size viewSize) {
        if ((getActivity() == null) || (view == null) || (previewSize == null) || (viewSize == null)) {
            return;
        }
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewSize.getWidth(), viewSize.getHeight());
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if ((Surface.ROTATION_90 == rotation) || (Surface.ROTATION_270 == rotation)) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(((float) viewSize.getHeight()) / previewSize.getHeight(),
                    ((float) viewSize.getWidth()) / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2),
                    centerX, centerY);
        }
        view.setTransform(matrix);
    }

    private void configureRecordingMode(){
        List<CaptureRequest.Key<?>> parameters = mModeCharacteristics.getSupportedParameters();

        if (parameters != null){
            // If the AI movie feature is supported,
            if((parameters.contains(RequestKey.HW_AI_MOVIE))){
                // Query the supported range of the AI movie feature.
                List<Byte> values = mModeCharacteristics.getParameterRange(RequestKey.HW_AI_MOVIE);

                seekBarAI.setMax(values.size() - 1);
                seekBarAI.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        settingAI = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_AI_MOVIE,values.get(progress));
                        if (result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingAI));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters.contains(RequestKey.HW_FILTER_EFFECT))){
                // Query the supported range of the AI movie feature.
                List<Byte> values = mModeCharacteristics.getParameterRange(RequestKey.HW_FILTER_EFFECT);

                seekBarFilter.setMax(values.size() - 1);
                seekBarFilter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        settingFilter = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_FILTER_EFFECT,values.get(progress));
                        if (result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingFilter));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters.contains(RequestKey.HW_FILTER_LEVEL))){
                // Query the supported range of the AI movie feature.
                List<Integer> values = mModeCharacteristics.getParameterRange(RequestKey.HW_FILTER_LEVEL);

                seekBarFilterLevel.setMax(values.size() - 1);
                seekBarFilterLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        settingFilterLevel = values.get(progress);
                        int result = mMode.setParameter(RequestKey.HW_FILTER_LEVEL,values.get(progress));
                        if (result == 0)
                            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingFilterLevel));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }

            if((parameters.contains(RequestKey.HW_FILTER_LEVEL))){
                // Query the supported range of the AI movie feature.
                List<Integer> values = mModeCharacteristics.getParameterRange(RequestKey.HW_FILTER_LEVEL);

                if(values.size() > 0){
                    switchBarVideoStabl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            settingVideoStabl = isChecked;
                            int result = mMode.setParameter(RequestKey.HW_VIDEO_STABILIZATION,isChecked);
                            if(result == 0)
                                ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(settingVideoStabl));
                        }
                    });
                }
            }


        }

    }

    private final ActionStateCallback mActionStateCallback = new ActionStateCallback() {
        @Override
        public void onPreview(Mode mode, int state, PreviewResult result) {
            if (state == PreviewResult.State.PREVIEW_STARTED) {
                Log.i(TAG, "onPreview Started");
                cameraReady();
            }

            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    configureRecordingMode();
                }
            });
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
            mMode.startPreview();
            requireActivity().runOnUiThread(() -> {
                if ((mTextureView == null) || (mPreviewSize == null)) {
                    return;
                }
                configureTransform(mTextureView, mPreviewSize,
                        new Size(mTextureView.getWidth(), mTextureView.getHeight()));
            });

        }

        @Override
        public void onConfigureFailed(Mode mode, int errorCode) {
            Log.d(TAG, "mModeStateCallback onConfigureFailed with cameraId: " + mode.getCameraId());
            mCameraOpenCloseLock.release();
        }

        @Override
        public void onReleased(Mode mode) {
            Log.d(TAG, "mModeStateCallback onModeReleased: ");
            mCameraOpenCloseLock.release();
        }

        @Override
        public void onFatalError(Mode mode, int errorCode) {
            Log.d(TAG, "mModeStateCallback onFatalError with errorCode: " + errorCode + " and with cameraId: "
                    + mode.getCameraId());
            mCameraOpenCloseLock.release();
        }
    };
    //endregion
}
