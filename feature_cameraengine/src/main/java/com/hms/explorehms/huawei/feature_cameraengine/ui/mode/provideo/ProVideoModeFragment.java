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

package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.provideo;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ProVideoModeFragment extends Fragment {

    //region UI Elements & Object References
    private static final String TAG = "CameraEngine";

    private final long PREVIEW_SURFACE_READY_TIMEOUT = 5000L;

    private CameraKit mCameraKit;

    private MediaRecorder mMediaRecorder;

    private boolean isGetInstance = false;

    private static final int VIDEO_ENCODING_BIT_RATE = 10000000;

    private static final int VIDEO_FRAME_RATE = 30;

    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();

    private Mode mMode;
    private ModeCharacteristics mModeCharacteristics;
    private ModeConfig.Builder modeConfigBuilder;

    private @Mode.Type int mCurrentModeType = Mode.Type.PRO_VIDEO_MODE;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private Semaphore mStartStopRecordLock = new Semaphore(1);

    private Surface mPreviewSurface;
    private Surface mVideoSurface;

    private Size mPreviewSize;
    private Size mCaptureSize;

    private File mFile;

    private Size mRecordSize;

    private boolean mIsFirstRecord = true;

    private HandlerThread mCameraKitThread;
    private Handler mCameraKitHandler;

    private float currentZoom = 1f;
    private float zoomValue = 0.5f;

    private String cameraId = "0";

    //UI
    private MaterialButton buttonHideIntroduction;
    private MaterialButton buttonShowIntroduction;

    private MaterialTextView tvSettingValue;
    private MaterialTextView tvModNotSupp;
    private MaterialTextView tvRecordTime;

    private ConstraintLayout clIntroduction;
    private ConstraintLayout clShowIntroduction;
    private ConstraintLayout clFunctions;
    private ConstraintLayout clSettings;
    private ConstraintLayout clSettingValue;

    private CardView cvStartRecord;
    private CardView cvPauseResume;
    private CardView cvZoomIn;
    private CardView cvZoomOut;
    private CardView cvLastImage;
    private CardView cvSettings;

    //Modes
    private CardView cvIso;
    private CardView cvExpHint;
    private CardView cvAwbType;
    private CardView cvWB;
    private CardView cvFocusDist;
    private CardView cvProMeter;
    private CardView cvExposureComp;
    private CardView cvFaceDetect;
    private CardView cvTargetFPS;
    private CardView cvNoiseRed;
    private CardView cvEdgeMode;
    private CardView cvToneMapMode;
    private CardView cvColorCorrectionMode;
    private CardView cvControlAeLock;
    private CardView cvControlAwbLock;

    private SeekBar seekBarISO;
    private SeekBar seekBarAwbType;
    private SeekBar seekBarWB;
    private SeekBar seekBarFocDist;
    private SeekBar seekBarProMeter;
    private SeekBar seekBarExposureComp;
    private SeekBar seekBarFaceDetect;
    private SeekBar seekBarNoiseRed;
    private SeekBar seekBarEdgeMode;
    private SeekBar seekBarToneMapMode;
    private SeekBar seekBarColorCorrectionMode;

    private Spinner spinnerTargetFrame;

    private ProgressBar pbCameraThread;

    private AutoFitTextureView mTextureView;

    private View view;

    private SwitchMaterial switchExpHint;
    private SwitchMaterial switchControlAeLock;
    private SwitchMaterial switchBarControlAwbLock;

    private ImageView ivStartStopRecord;
    private ImageView ivPauseResumeRecord;
    private ImageView ivFlash;

    //app setting
    private boolean isSettingsON = false;

    private int isoLevel;
    private int awbType;
    private int whiteBalance;
    private int faceDetectMode;
    private int noiseRedMode;
    private int edgeMode;
    private int toneMapMode;
    private int colorCorrMode;

    private boolean expHint;
    private boolean controlAeLock;
    private boolean controlAwbLock;

    private float focusDistance;
    private float exposureComp;

    private byte proMeter;

    private Range<Integer> targetFPS;

    private View lastSelectedView;
    private View lastShowedView;

    private boolean isRecording = false;
    private boolean isPaused = false;
    private boolean hasRecord = false;

    private RecordTimeHelper timerHelper;

//endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pro_video_mode_camera_engine,container,false);
        initUI();
        initListener();
        initTag();

        timerHelper = new RecordTimeHelper(requireActivity(),tvRecordTime);

        return view;
    }

    private void initUI(){
        cvStartRecord = view.findViewById(R.id.cv_start_stop_pro_vid_camera_eng);
        cvPauseResume= view.findViewById(R.id.cv_pause_resume_pro_vid_camera_eng);
        cvZoomIn = view.findViewById(R.id.cv_zoom_in_pro_vid_camera_engine);
        cvZoomOut = view.findViewById(R.id.cv_zoom_out_pro_vid_camera_engine);
        cvLastImage = view.findViewById(R.id.cv_last_image_pro_vid_camera_engine);
        cvSettings = view.findViewById(R.id.cv_settings_pro_vid_camera_engine);

        //17 setting
        cvIso = view.findViewById(R.id.cv_iso_level_pro_vid_camera_engine);
        cvExpHint = view.findViewById(R.id.cv_exposure_hint_level_pro_vid_camera_engine);
        cvAwbType = view.findViewById(R.id.cv_awb_type_pro_vid_camera_engine);
        cvWB = view.findViewById(R.id.cv_man_wb_pro_vid_camera_engine);
        cvFocusDist = view.findViewById(R.id.cv_foc_dist_pro_vid_camera_engine);
        cvProMeter = view.findViewById(R.id.cv_pro_m_pro_vid_camera_engine);
        cvExposureComp = view.findViewById(R.id.cv_exp_comp_pro_vid_camera_engine);
        cvFaceDetect = view.findViewById(R.id.cv_face_det_pro_vid_camera_engine);
        cvTargetFPS = view.findViewById(R.id.cv_target_fps_pro_vid_camera_engine);
        cvNoiseRed = view.findViewById(R.id.cv_noise_red_pro_vid_camera_engine);
        cvEdgeMode = view.findViewById(R.id.cv_edge_mode_pro_vid_camera_engine);
        cvToneMapMode = view.findViewById(R.id.cv_tonemap_mode_pro_vid_camera_engine);
        cvColorCorrectionMode = view.findViewById(R.id.cv_color_corr_mode_pro_vid_camera_engine);
        cvControlAeLock = view.findViewById(R.id.cv_contr_aelock_pro_vid_camera_engine);
        cvControlAwbLock = view.findViewById(R.id.cv_contr_awblock_pro_vid_camera_engine);

        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_pro_vid_camera_engine);
        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_pro_vid_camera_engine);

        clIntroduction = view.findViewById(R.id.cl_mode_introduction_pro_vid_camera_engine);
        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_pro_vid_camera_engine);
        clFunctions = view.findViewById(R.id.cl_pro_vid_functions_camera_engine);
        clSettings = view.findViewById(R.id.cl_pro_vidto_functions_plus_camera_engine);
        clSettingValue = view.findViewById(R.id.cl_night_functions_plus_level_camera_engine);

        mTextureView = view.findViewById(R.id.aftv_pro_vid_camera_engine);

        pbCameraThread = view.findViewById(R.id.pb_pro_vid_mode_camera_engine);

        seekBarISO = view.findViewById(R.id.seekbar_iso_pro_vid_camera_engine);
        seekBarAwbType = view.findViewById(R.id.seekbar_awb_type_pro_vid_camera_engine);
        seekBarWB = view.findViewById(R.id.seekbar_man_wb_pro_vid_camera_engine);
        seekBarFocDist = view.findViewById(R.id.seekbar_foc_dist_pro_vid_camera_engine);
        seekBarProMeter = view.findViewById(R.id.seekbar_pro_meter_pro_vid_camera_engine);
        seekBarExposureComp = view.findViewById(R.id.seekbar_exp_comp_pro_vid_camera_engine);
        seekBarFaceDetect = view.findViewById(R.id.seekbar_face_detect_mode_pro_vid_camera_engine);
        seekBarNoiseRed = view.findViewById(R.id.seekbar_noise_red_pro_vid_camera_engine);
        seekBarEdgeMode = view.findViewById(R.id.seekbar_edge_mode_pro_vid_camera_engine);
        seekBarToneMapMode = view.findViewById(R.id.seekbar_tone_map_mode_pro_vid_camera_engine);
        seekBarColorCorrectionMode = view.findViewById(R.id.seekbar_color_corr_mode_pro_vid_camera_engine);
        switchControlAeLock = view.findViewById(R.id.switch_cont_aelock_pro_vid_camera_engine);
        switchBarControlAwbLock = view.findViewById(R.id.switch_cont_awlock_vid_camera_engine);
        switchExpHint = view.findViewById(R.id.switch_exp_hint_pro_vid_camera_engine);

        ivStartStopRecord = view.findViewById(R.id.iv_start_stop_pro_vid_camera_engine);
        ivPauseResumeRecord = view.findViewById(R.id.iv_pause_resume_pro_vid_camera_engine);
        ivFlash = view.findViewById(R.id.iv_flash_pro_video_mode_camera_engine);

        spinnerTargetFrame = view.findViewById(R.id.spinner_target_frame_pro_vid_camera_engine);

        tvSettingValue = view.findViewById(R.id.tv_settings_value_vid_camera_engine);
        tvModNotSupp = view.findViewById(R.id.tv_mode_not_supp_pro_vid_camera_engine);
        tvRecordTime = view.findViewById(R.id.tv_time_pro_vid_camera_engine);
    }

    /**
     * Init listener of elements
     */
    private void initListener(){
        cvStartRecord.setOnClickListener(v -> {
            if(!isRecording)
                startRecording();
            else
                stopRecording();
            isRecording = !isRecording;
        });

        cvPauseResume.setOnClickListener(v -> {
            if(!isPaused)
                pauseRecording();
            else
                resumeRecording();

            isPaused = !isPaused;
        });

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
            if(mFile != null && hasRecord){
                ViewUtils.showDialogVideoView(requireActivity(),requireContext(),mFile,getString(R.string.txt_pro_v_camera_engine));
            }else{
                Toast.makeText(requireContext(),getString(R.string.txt_no_record_camera_engine),Toast.LENGTH_SHORT).show();
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
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(focusDistance));
        });
        cvProMeter.setOnClickListener(v -> {
            showRelatedView(seekBarProMeter);
            changeViewColor(cvProMeter);
            ViewUtils.showSettingOnCenter(tvSettingValue, "" + proMeter);
        });
        cvExposureComp.setOnClickListener(v -> {
            showRelatedView(seekBarExposureComp);
            changeViewColor(cvExposureComp);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(exposureComp));
        });
        cvFaceDetect.setOnClickListener(v -> {
            showRelatedView(seekBarFaceDetect);
            changeViewColor(cvFaceDetect);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(faceDetectMode));
        });
        cvTargetFPS.setOnClickListener(v -> {
            showRelatedView(spinnerTargetFrame);
            changeViewColor(cvTargetFPS);
            if(targetFPS != null){
                ViewUtils.showSettingOnCenter(tvSettingValue,targetFPS.getLower()+"-"+targetFPS.getUpper());
            }
        });
        cvNoiseRed.setOnClickListener(v -> {
            showRelatedView(seekBarNoiseRed);
            changeViewColor(cvNoiseRed);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(noiseRedMode));
        });
        cvEdgeMode.setOnClickListener(v -> {
            showRelatedView(seekBarEdgeMode);
            changeViewColor(cvEdgeMode);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(edgeMode));
        });
        cvToneMapMode.setOnClickListener(v -> {
            showRelatedView(seekBarToneMapMode);
            changeViewColor(cvToneMapMode);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(toneMapMode));
        });
        cvColorCorrectionMode.setOnClickListener(v -> {
            showRelatedView(seekBarColorCorrectionMode);
            changeViewColor(cvColorCorrectionMode);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(colorCorrMode));
        });

        cvControlAeLock.setOnClickListener(v -> {
            showRelatedView(switchControlAeLock);
            changeViewColor(cvControlAeLock);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(controlAeLock));
        });
        cvControlAwbLock.setOnClickListener(v -> {
            showRelatedView(switchBarControlAwbLock);
            changeViewColor(cvControlAwbLock);
            ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(controlAwbLock));
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
     * Create tag of UI element to relate each other
     */
    private void initTag(){
        seekBarISO.setTag(cvIso);
        seekBarAwbType.setTag(cvAwbType);
        seekBarWB.setTag(cvWB);
        seekBarFocDist.setTag(cvFocusDist);
        seekBarProMeter.setTag(cvProMeter);
        seekBarExposureComp.setTag(cvExposureComp);
        seekBarFaceDetect.setTag(cvFaceDetect);
        seekBarNoiseRed.setTag(cvNoiseRed);
        seekBarEdgeMode.setTag(cvEdgeMode);
        seekBarToneMapMode.setTag(cvToneMapMode);
        seekBarColorCorrectionMode.setTag(cvColorCorrectionMode);
        switchControlAeLock.setTag(cvControlAeLock);
        switchBarControlAwbLock.setTag(cvControlAwbLock);
        switchExpHint.setTag(cvExpHint);
    }

    /**
     * Create Pro Video mode
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
            Log.i(TAG,"Try to use camera with id " +cameraId);

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
                        this.cameraId = cameraId;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.msg_time_out_waiting_lock_camera_camera_engine),Toast.LENGTH_SHORT).show();
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
                mCaptureSize = captureSizes.stream().filter(size -> size.equals(new Size(1920,1080))).findFirst().orElse(new Size(1920, 1080));

                if(previewSizes.get(Metadata.FpsRange.HW_FPS_30) != null){

                    if(previewSizes.get(Metadata.FpsRange.HW_FPS_30).contains(new Size(1920,1080))){
                        final Size tmpPreviewSize = previewSizes.get(Metadata.FpsRange.HW_FPS_30).stream().filter(size -> size.equals(new Size(1920, 1080))).findFirst().get();

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
        mFile =  new File(requireContext().getExternalFilesDir(null), System.currentTimeMillis() + "_recording.mp4");
        mMediaRecorder.setOutputFile(mFile);
        mMediaRecorder.setVideoEncodingBitRate(VIDEO_ENCODING_BIT_RATE);
        mMediaRecorder.setVideoFrameRate(VIDEO_FRAME_RATE);
        mMediaRecorder.setVideoSize(size.getWidth(), size.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setOrientationHint(90);
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
     * Camera features have to check before implement.
     * If mode characteristic not exist, users won't see that setting
     */
    private void configureProMode(){

        List<CaptureRequest.Key<?>> parameters = mModeCharacteristics.getSupportedParameters();
        if ((parameters != null)) {
            if((parameters.contains(RequestKey.HW_PRO_SENSOR_ISO_VALUE))){
                setParameterSetting(RequestKey.HW_PRO_SENSOR_ISO_VALUE, seekBarISO, isoLevel);
            }
            if((parameters).contains(RequestKey.HW_PRO_AWB_TYPE)){
                setParameterSetting(RequestKey.HW_PRO_AWB_TYPE, seekBarAwbType, awbType);
            }
            if((parameters).contains(RequestKey.HW_PRO_MANUAL_WB_VALUE)){
                setParameterSetting(RequestKey.HW_PRO_MANUAL_WB_VALUE, seekBarWB, whiteBalance);
            }
            if((parameters).contains(RequestKey.HW_PRO_FOCUS_DISTANCE_VALUE)){
                setParameterSetting(RequestKey.HW_PRO_FOCUS_DISTANCE_VALUE, seekBarFocDist, focusDistance);
            }
            if((parameters).contains(RequestKey.HW_PRO_METERING_VALUE)){
                setParameterSetting(RequestKey.HW_PRO_METERING_VALUE, seekBarProMeter, proMeter);
            }
            if((parameters).contains(RequestKey.HW_EXPOSURE_COMPENSATION_VALUE)){
                setParameterSetting(RequestKey.HW_EXPOSURE_COMPENSATION_VALUE, seekBarExposureComp, exposureComp);
            }
            if((parameters).contains(CaptureRequest.STATISTICS_FACE_DETECT_MODE)){
                setParameterSetting(CaptureRequest.STATISTICS_FACE_DETECT_MODE, seekBarFaceDetect, faceDetectMode);
            }
            if((parameters).contains(CaptureRequest.NOISE_REDUCTION_MODE)){
                setParameterSetting(CaptureRequest.NOISE_REDUCTION_MODE, seekBarNoiseRed, noiseRedMode);
            }
            if((parameters).contains(CaptureRequest.EDGE_MODE)){
                setParameterSetting(CaptureRequest.EDGE_MODE, seekBarEdgeMode, edgeMode);
            }
            if((parameters).contains(CaptureRequest.TONEMAP_MODE)){
                setParameterSetting(CaptureRequest.TONEMAP_MODE, seekBarToneMapMode, toneMapMode);
            }
            if((parameters).contains(CaptureRequest.COLOR_CORRECTION_MODE)){
                setParameterSetting(CaptureRequest.COLOR_CORRECTION_MODE, seekBarColorCorrectionMode, colorCorrMode);
            }
            if((parameters).contains(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE)){
                List<android.util.Range<Integer>> values = mModeCharacteristics.getParameterRange(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE);
                if(values.size() > 0){
                    Collections.reverse(values);
                    ArrayAdapter<Range<Integer>> itemsAdapter = new ArrayAdapter<Range<Integer>>(requireContext(), android.R.layout.simple_list_item_1, values);
                    spinnerTargetFrame.setAdapter(itemsAdapter);
                    spinnerTargetFrame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            targetFPS = values.get(position);
                            int result = mMode.setParameter(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,targetFPS);
                            if(result == 0)
                                ViewUtils.showSettingOnCenter(tvSettingValue,targetFPS.getLower()+"-"+targetFPS.getUpper());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else{
                    addOnTouchListener(cvTargetFPS);
                }

            }
            if((parameters).contains(CaptureRequest.CONTROL_AE_LOCK)){
                List<Boolean> values = mModeCharacteristics.getParameterRange(CaptureRequest.CONTROL_AE_LOCK);
                if(values.size() > 0){
                    switchControlAeLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            controlAeLock = isChecked;
                            int result = mMode.setParameter(CaptureRequest.CONTROL_AE_LOCK,isChecked);
                            if(result == 0)
                                ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(controlAeLock));
                        }
                    });
                }else{
                    addOnTouchListener(cvControlAeLock);
                }
            }
            if((parameters).contains(CaptureRequest.CONTROL_AWB_LOCK)){
                List<Boolean> values = mModeCharacteristics.getParameterRange(CaptureRequest.CONTROL_AWB_LOCK);
                if(values.size() > 0){
                    switchBarControlAwbLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            controlAwbLock = isChecked;
                            int result = mMode.setParameter(CaptureRequest.CONTROL_AWB_LOCK,isChecked);
                            if(result == 0)
                                ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(controlAwbLock));
                        }
                    });
                }else{
                    addOnTouchListener(cvControlAwbLock);
                }
            }
            if((parameters).contains(RequestKey.HW_PRO_EXPOSURE_HINT)){
                List<Boolean> values = mModeCharacteristics.getParameterRange(RequestKey.HW_PRO_EXPOSURE_HINT);
                if(values.size()>0){
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
            }
        }

    }

    /**
     * Set values and listeners of seekBars that relate to settings of current mode
     * @param key KEY of current mode setting
     * @param seekbar SeekBar of current mode setting values
     * @param targetVal Variable of current mode setting
     * @param <T> Generic type of current key
     */
    public <T> void setParameterSetting(CaptureRequest.Key<T> key, SeekBar seekbar,Object targetVal) {
        Object[] t = new Object[]{targetVal};
        List<T> values = mModeCharacteristics.getParameterRange(key);
        if(values.size() > 0){
            seekbar.setMax(values.size() - 1);
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    t[0] = values.get(progress);
                    int result = mMode.setParameter(key, values.get(progress));
                    if(result == 0){
                        ViewUtils.showSettingOnCenter(tvSettingValue,String.valueOf(t[0]));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        }else{
            if(seekbar.getTag() instanceof View){
                View cardView = (View)seekbar.getTag();
                if(cardView != null){
                    addOnTouchListener(cardView);
                }
            }
        }
    }

    private void addOnTouchListener(View view){
        view.setOnClickListener(v -> Toast.makeText(requireContext(),getString(R.string.msg_this_mode_not_supported_camera_engine), Toast.LENGTH_SHORT).show());
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
            cvPauseResume.setVisibility(View.VISIBLE);

            if (!mIsFirstRecord) {
                setUpMediaRecorder(mRecordSize, mVideoSurface);
            }
            mIsFirstRecord = false;
            mMode.startRecording();
            mMediaRecorder.start();

            tvRecordTime.setVisibility(View.VISIBLE);
            timerHelper.startTimer();

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
     * Pause video record, update UI elements
     */
    private void pauseRecording(){
        timerHelper.pauseTimer();
        mMode.pauseRecording();
        mMediaRecorder.pause();
        ivPauseResumeRecord.setBackgroundResource(R.drawable.icon_play_camera_engine);
    }

    /**
     * Resume video record, update UI elements
     */
    private void resumeRecording(){
        timerHelper.resumeTimer();
        mMode.resumeRecording();
        mMediaRecorder.resume();
        ivPauseResumeRecord.setBackgroundResource(R.drawable.icon_pause_camera_engine);
    }

    /**
     * Stop video record, update UI elements
     */
    private void stopRecording(){
        try {
            acquireStartStopRecordLock();
            ivStartStopRecord.setBackgroundResource(R.drawable.icon_record_camera_engine);
            cvPauseResume.setVisibility(View.GONE);

            mMode.stopRecording();
            mMediaRecorder.stop();

            timerHelper.stopTimer();
            tvRecordTime.setVisibility(View.INVISIBLE);

            ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_pro_v_camera_engine));
            hasRecord = true;

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

        if(view != null){
            tvModNotSupp.setVisibility(View.INVISIBLE);

            if(lastShowedView != null)
                lastShowedView.setVisibility(View.INVISIBLE);

            clSettingValue.setVisibility(View.VISIBLE);
            view.animate().alpha(1.0f).setDuration(250);
            view.setVisibility(View.VISIBLE);

            lastShowedView = view;
        }
    }

    /**
     * Change color of setting when user tap to setting cardView
     * @param view target setting
     */
    private void changeViewColor(View view){

        if(lastSelectedView instanceof CardView){
            ((CardView)lastSelectedView).setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimaryCameraEngine));
        }
        if(view != null){
            ((CardView)view).setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorBlackCameraEngine));
            lastSelectedView = view;
        }
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
            Toast.makeText(requireActivity(),getString(R.string.msg_this_device_not_support_camera_kit_camera_engine),Toast.LENGTH_LONG).show();
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

            if(isRecording){
                stopRecording();
                clearInvalidFile();
                isRecording = false;
            }

            stopBackgroundThread(); // stop when pause activity or change fragment
        }
        super.onPause();
    }

    //region Listener, Callbacks & Configurations

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
                    configureProMode();
                }
            });
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
                    cvStartRecord.setEnabled(true);
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
