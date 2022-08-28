//package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.slowmo;
//
//import android.graphics.SurfaceTexture;
//import android.hardware.camera2.CaptureRequest;
//import android.media.MediaCodec;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.ConditionVariable;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.util.Log;
//import android.util.Size;
//import android.view.LayoutInflater;
//import android.view.Surface;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.cardview.widget.CardView;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.fragment.app.Fragment;
//
//import com.hms.explorehms.huawei.feature_cameraengine.R;
//import com.hms.explorehms.huawei.feature_cameraengine.ui.common.AutoFitTextureView;
//import com.hms.explorehms.huawei.feature_cameraengine.common.DoubleClickHandler;
//import com.hms.explorehms.huawei.feature_cameraengine.util.ViewUtils;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textview.MaterialTextView;
//import com.huawei.camera.camerakit.ActionStateCallback;
//import com.huawei.camera.camerakit.CameraKit;
//import com.huawei.camera.camerakit.Metadata;
//import com.huawei.camera.camerakit.Mode;
//import com.huawei.camera.camerakit.ModeCharacteristics;
//import com.huawei.camera.camerakit.ModeConfig;
//import com.huawei.camera.camerakit.ModeStateCallback;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Semaphore;
//import java.util.concurrent.TimeUnit;
//
//public class SlowMotionModeFragment extends Fragment {
//    private final String TAG = "CameraEngine";
//
//    private static final long PREVIEW_SURFACE_READY_TIMEOUT = 5000L;
//
//    private static final int VIDEO_ENCODING_BIT_RATE = 10000000;
//
//    private CameraKit mCameraKit;
//
//    private boolean isGetInstance = false;
//
//    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();
//
//    private Mode mMode;
//    private ModeCharacteristics mModeCharacteristics;
//    private ModeConfig.Builder modeConfigBuilder;
//
//    private @Mode.Type int mCurrentModeType = Mode.Type.SLOW_MOTION_MODE;
//
//    private Surface mPreviewSurface;
//    private Surface mVideoSurface;
//    private MediaRecorder mMediaRecorder;
//
//    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
//    private Semaphore mStartStopRecordLock = new Semaphore(1);
//
//    private File mFile;
//
//    private HandlerThread mCameraKitThread;
//    private Handler mCameraKitHandler;
//
//    private float currentZoom = 1f;
//    private float zoomValue = 0.5f;
//
//    //UI
//    private MaterialButton buttonHideIntroduction;
//    private MaterialButton buttonShowIntroduction;
//
//    private MaterialTextView tvZoomLevel;
//
//    private ConstraintLayout clIntroduction;
//    private ConstraintLayout clShowIntroduction;
//    private ConstraintLayout clFunctions;
//
//    private CardView cvRecordOrStop;
//    private CardView cvZoomIn;
//    private CardView cvZoomOut;
//    private CardView cvLastVideo;
//
//    private ImageView ivRecord;
//
//    private ProgressBar pbCameraThread;
//
//    private AutoFitTextureView mTextureView;
//
//    private View view;
//
//    private String cameraId = "0";
//
//    private boolean mIsFirstRecord = true;
//
//    private int mRecordFps;
//    private Size mRecordSize;
//
//    private boolean isRecording = false;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        view = inflater.inflate(R.layout.fragment_slow_mo_camera_engine,container,false);
//        initUI();
//        initListener();
//
//        return view;
//    }
//    private void initUI(){
//        cvRecordOrStop = view.findViewById(R.id.cv_start_stop_slow_mo_camera_eng);
//        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_slow_mo_camera_engine);
//        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_slow_mo_camera_engine);
//        clIntroduction = view.findViewById(R.id.cl_mode_introduction_slow_mo_camera_engine);
//        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_slow_mo_camera_engine);
//        clFunctions = view.findViewById(R.id.cl_slow_mo_functions_camera_engine);
//        mTextureView = view.findViewById(R.id.aftv_slow_mo_camera_engine);
//        pbCameraThread = view.findViewById(R.id.pb_slow_mo_mode_camera_engine);
//        cvZoomIn = view.findViewById(R.id.cv_zoom_in_slow_mo_camera_engine);
//        cvZoomOut = view.findViewById(R.id.cv_zoom_out_slow_mo_camera_engine);
//        tvZoomLevel = view.findViewById(R.id.tv_zoom_level_slow_mo_camera_engine);
//        cvLastVideo = view.findViewById(R.id.cv_last_video_slow_mo_camera_engine);
//        ivRecord = view.findViewById(R.id.iv_record_slow_mo_camera_engine);
//    }
//
//    private void initListener(){
//        cvRecordOrStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!isRecording)
//                    startRecording();
//                else
//                    stopRecording();
//
//                isRecording = !isRecording;
//            }
//        });
//
//        buttonHideIntroduction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clIntroduction.animate().alpha(0.0f).setDuration(250);
//                clShowIntroduction.setVisibility(View.VISIBLE);
//            }
//        });
//
//        buttonShowIntroduction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clIntroduction.animate().alpha(1.0f).setDuration(250);
//                clShowIntroduction.setVisibility(View.GONE);
//            }
//        });
//
//        cvZoomIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentZoom += zoomValue;
//                int result = mMode.setZoom(currentZoom);
//
//                if(result == 0)
//                    ViewUtils.showSettingOnCenter(tvZoomLevel,String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
//                else
//                    currentZoom -= zoomValue;
//            }
//        });
//
//        cvZoomOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currentZoom -= zoomValue;
//
//                int result = mMode.setZoom(currentZoom);
//
//                if(result == 0)
//                    ViewUtils.showSettingOnCenter(tvZoomLevel,String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
//                else
//                    currentZoom += zoomValue;
//            }
//        });
//
//        cvLastVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mFile != null){
//                    ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_super_slow_mode_camera_engine) + " - "+ mRecordFps + "fps");
//                }else
//                    Toast.makeText(getContext(),getString(R.string.txt_no_record_camera_engine),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        mTextureView.setOnClickListener(new DoubleClickHandler(v -> {
//            if(cameraId.equals("0")){
//                createMode("1");
//            }
//            else{
//                createMode("0");
//            }
//        }));
//    }
//
//    private void createMode(String cameraId){
//        if(!isGetInstance){
//            try {
//                mCameraKit = CameraKit.getInstance(requireContext());
//            }catch (NoSuchMethodError e){
//                Log.e(TAG,"This version CameraEngine does not contain VersionInfoInterface");
//            }finally {
//                isGetInstance = true;
//            }
//        }
//
//        if(mCameraKit == null){
//            return;
//        }
//        String[] cameraList = mCameraKit.getCameraIdList();
//        if((cameraList != null) && (cameraList.length > 0)){
//            Log.i(TAG,"Try to use camera with id " + cameraId);
//
//            boolean cameraExist = false;
//            for (String cm : cameraList){
//                if(cm.equals(cameraId)){
//                    cameraExist = true;
//                    break;
//                }
//            }
//
//            if(cameraExist){
//                int[] modes = mCameraKit.getSupportedModes(cameraId);
//
//                boolean modeAvailable = false;
//                for(int mode : modes){
//                    if(mode == mCurrentModeType){
//                        modeAvailable = true;
//                        break;
//                    }
//                }
//
//                if(!modeAvailable){
//                    if(cameraId.equals("0")){
//                        Toast.makeText(requireContext(),"Rear camera does not support current mode",Toast.LENGTH_LONG).show();
//                    }else{
//                        Toast.makeText(requireContext(),"Front camera does not support current mode",Toast.LENGTH_LONG).show();
//                    }
//                }else{
//                    try{
//                        if(!mCameraOpenCloseLock.tryAcquire(2000, TimeUnit.MILLISECONDS)){
//                            throw new RuntimeException("Time out waiting to lock camera opening");
//                        }
//                        mCameraKit.createMode(cameraId,mCurrentModeType,mModeStateCallback,mCameraKitHandler);
//                        changeCameraInfo(cameraId);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }else{
//                Toast.makeText(requireContext(),"Camera not exist",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void changeCameraInfo(String cameraId){
//        this.cameraId = cameraId;
//        boolean zoom = false;
//        if(cameraId.equals("0"))
//            zoom = true;
//        if(zoom){
//            cvZoomIn.setVisibility(View.VISIBLE);
//            cvZoomOut.setVisibility(View.VISIBLE);
//        }else{
//            cvZoomIn.setVisibility(View.INVISIBLE);
//            cvZoomOut.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private void configMode() {
//        try {
//            mCameraOpenCloseLock.acquire();
//            Map<Integer, List<Size>> videoSizes = mModeCharacteristics.getSupportedVideoSizes(MediaRecorder.class);
//            List<Size> previewSizes = mModeCharacteristics.getSupportedPreviewSizes(SurfaceTexture.class);
//
//
//            // Obtain the recording frame rate and resolution. The two configuration items must be set in pairs based on the map returned by the modeCharacteristics.getSupportedVideoSizes().
//            if(videoSizes.containsKey(Metadata.FpsRange.HW_FPS_480) && videoSizes.get(Metadata.FpsRange.HW_FPS_480).get(0) != null){
//                mRecordFps = Metadata.FpsRange.HW_FPS_480;
//            }else if(videoSizes.containsKey(Metadata.FpsRange.HW_FPS_240) && videoSizes.get(Metadata.FpsRange.HW_FPS_240).get(0) != null){
//                mRecordFps = Metadata.FpsRange.HW_FPS_240;
//            }else if(videoSizes.containsKey(Metadata.FpsRange.HW_FPS_120) && videoSizes.get(Metadata.FpsRange.HW_FPS_120).get(0) != null){
//                mRecordFps = Metadata.FpsRange.HW_FPS_120;
//            }else if(videoSizes.containsKey(Metadata.FpsRange.HW_FPS_60) && videoSizes.get(Metadata.FpsRange.HW_FPS_60).get(0) != null){
//                mRecordFps = Metadata.FpsRange.HW_FPS_60;
//            }else{
//                return;
//            }
//
//            mRecordSize = videoSizes.get(mRecordFps).get(0);
//
//            // The video resolution must be the same as the preview resolution.
//            if(!previewSizes.contains(mRecordSize)){
//                Log.e(TAG, "preparePreviewSurface: the previewSize and recordSize should be the same, Internal error!");
//                return;
//            }
//
//            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
//            if(surfaceTexture != null){
//                surfaceTexture.setDefaultBufferSize(previewSizes.get(0).getWidth(), previewSizes.get(0).getHeight());
//                mPreviewSurface = new Surface(surfaceTexture);
//            }
//
//            mVideoSurface = MediaCodec.createPersistentInputSurface();
//            setUpMediaRecorder(mRecordSize,mVideoSurface,mRecordFps);
//            modeConfigBuilder = mMode.getModeConfigBuilder();
//            modeConfigBuilder.setVideoFps(mRecordFps);
//            modeConfigBuilder.addPreviewSurface(mPreviewSurface);
//            modeConfigBuilder.addVideoSurface(mVideoSurface);
//            modeConfigBuilder.setStateCallback(mActionStateCallback, mCameraKitHandler);
//            if (mMode != null) {
//                mIsFirstRecord = true;
//                mMode.configure();
//            }
//
//        }catch (Exception e){
//            Log.e(TAG,e.toString());
//        }finally {
//            mCameraOpenCloseLock.release();
//        }
//
//    }
//
//    private void setUpMediaRecorder(Size size, Surface surface,int frameRate) {
//        if(mMediaRecorder == null)
//            mMediaRecorder = new MediaRecorder();
//        mMediaRecorder.reset();
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mFile =  new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + "_slowMoRecording.mp4");
//        mMediaRecorder.setOutputFile(mFile);
//        mMediaRecorder.setVideoEncodingBitRate(VIDEO_ENCODING_BIT_RATE);
//        mMediaRecorder.setVideoFrameRate(frameRate);
//        mMediaRecorder.setVideoSize(size.getWidth(), size.getHeight());
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mMediaRecorder.setOrientationHint(90);
//        mMediaRecorder.setInputSurface(surface);
//        try {
//            mMediaRecorder.prepare();
//            Log.d(TAG, "mMediaRecorder prepare done!");
//        } catch (IOException e) {
//            Log.e(TAG, "mMediaRecorder prepare ioe exception " + e);
//        } catch (IllegalStateException e) {
//            Log.e(TAG, "mMediaRecorder prepare state error");
//        }
//    }
//
//    private boolean initCameraKit() {
//        mCameraKit = CameraKit.getInstance(requireContext());
//        if (mCameraKit == null) {
//            Log.e(TAG, "initCamerakit: this devices not support camerakit or not installed!");
//            return false;
//        }
//        return true;
//    }
//
//
//    private void startRecording() {
//        try {
//            acquireStartStopRecordLock();
//            ivRecord.setBackgroundResource(R.drawable.icon_stop_camera_engine);
//
//            if (!mIsFirstRecord) {
//                setUpMediaRecorder(mRecordSize, mVideoSurface,240);
//            }
//            mIsFirstRecord = false;
//
//            mMode.startRecording();
//            mMediaRecorder.start();
//
//        }catch (Exception e){
//            Log.e(TAG,e.toString());
//        }finally {
//            releaseStartStopRecordLock();
//        }
//
//    }
//
//    private void stopRecording(){
//        try {
//            acquireStartStopRecordLock();
//
//            ivRecord.setBackgroundResource(R.drawable.icon_record_camera_engine);
//            mMode.stopRecording();
//            mMediaRecorder.stop();
//
//            ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_recording_mode_camera_engine));
//        }catch (Exception e){
//            Log.e(TAG,e.toString());
//        }finally {
//            releaseStartStopRecordLock();
//        }
//
//    }
//
//    private void acquireStartStopRecordLock() throws InterruptedException {
//        if (mStartStopRecordLock != null) {
//            mStartStopRecordLock.acquire();
//        } else {
//            Log.d(TAG, "acquiremStartStopRecordLock, mStartStopRecordLock refer null");
//        }
//    }
//
//    private void releaseStartStopRecordLock() {
//        if (mStartStopRecordLock != null) {
//            if (mStartStopRecordLock.availablePermits() < 1) {
//                mStartStopRecordLock.release();
//            }
//        } else {
//            Log.d(TAG, "release lock, but it is null");
//        }
//    }
//
//    private void startBackgroundThread() {
//        Log.d(TAG, "startBackgroundThread");
//        if (mCameraKitThread == null) {
//            mCameraKitThread = new HandlerThread("CameraBackground");
//            mCameraKitThread.start();
//            mCameraKitHandler = new Handler(mCameraKitThread.getLooper());
//            Log.d(TAG, "startBackgroundTThread: mCameraKitThread.getThreadId()=" + mCameraKitThread.getThreadId());
//        }
//    }
//
//    private void stopBackgroundThread() {
//        Log.d(TAG, "stopBackgroundThread");
//        if (mCameraKitThread != null) {
//            mCameraKitThread.quitSafely();
//            try {
//                mCameraKitThread.join();
//                mCameraKitThread = null;
//                mCameraKitHandler = null;
//            } catch (InterruptedException e) {
//                Log.e(TAG, "InterruptedException in stopBackgroundThread " + e.getMessage());
//            }
//        }
//    }
//
//    private void cameraReady(){
//        requireActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                pbCameraThread.setVisibility(View.GONE);
//                clFunctions.animate().alpha(1.0f).setDuration(250);
//                tvZoomLevel.setVisibility(View.VISIBLE);
//            }
//        });
//
//    }
//
//    private void cameraNotReady(){
//        requireActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                pbCameraThread.setVisibility(View.VISIBLE);
//                clFunctions.animate().alpha(0.0f).setDuration(0);
//                tvZoomLevel.setVisibility(View.INVISIBLE);
//            }
//        });
//    }
//
//    //LifeCyle
//    @Override
//    public void onResume() {
//        Log.d(TAG, "onResume: ");
//        super.onResume();
//        cameraNotReady();
//        if (!initCameraKit()) {
//            Toast.makeText(requireActivity(),"this devices not support camerakit or not installed",Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        startBackgroundThread();
//        if (mTextureView != null) {
//            if (mTextureView.isAvailable()) {
//                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
//                mCameraKitHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        createMode(cameraId);
//                    }
//                });
//            } else {
//                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
//            }
//        }
//    }
//
//    /* The mode needs to be released when the app is closed or switched to the background. */
//    @Override
//    public void onPause() {
//        if (mCameraKitHandler != null) {
//            mCameraKitHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mMode!= null) {
//                        mMode.release();
//                        mMode = null;
//                    }
//                }
//            });
//            stopBackgroundThread();
//        }
//        super.onPause();
//    }
//
//    //Callbacks
//
//    /* Use the action status callback as an example to obtain the callback of the execution status result, for example, preparing for, starting, ending, and saving Super slow-mo recording. */
//    private ActionStateCallback mActionStateCallback = new ActionStateCallback() {
//        @Override
//        public void onPreview(Mode mode, int state, PreviewResult result) {
//            if (state == PreviewResult.State.PREVIEW_STARTED) {
//                Log.i(TAG, "onPreview Started");
//                cameraReady();
//            }
//        }
//
//        @Override
//        public void onRecording(Mode mode, int state, RecordingResult result) {
//            switch (state) {
//
//                //The bottom-layer initialization is not ready.
//                case RecordingResult.State.ERROR_RECORDING_NOT_READY:
//                    requireActivity().runOnUiThread(
//                            () -> Toast.makeText(getActivity(), "Not Ready", Toast.LENGTH_SHORT).show());
//                    break;
//                // Recording is stopped.
//                case RecordingResult.State.RECORDING_STOPPED:
//                    requireActivity().runOnUiThread(() -> {
//                        ivRecord.setBackgroundResource(R.drawable.icon_record_camera_engine);
//                        pbCameraThread.setVisibility(View.VISIBLE);
//                    });
//                    break;
//                // The recorded file is completed & saved.
//                case RecordingResult.State.RECORDING_FILE_SAVED:
//                    requireActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pbCameraThread.setVisibility(View.GONE);
//                        }
//                    });
//                    ViewUtils.showDialogVideoView(getActivity(),getContext(),mFile,getString(R.string.txt_super_slow_mode_camera_engine) + " - "+ mRecordFps + "fps");
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private final TextureView.SurfaceTextureListener mSurfaceTextureListener =
//            new TextureView.SurfaceTextureListener() {
//                @Override
//                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
//
//                    if(mCameraKitHandler == null){
//                        startBackgroundThread();
//                    }
//                    mCameraKitHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            createMode(cameraId);
//                        }
//                    });
//                }
//
//                @Override
//                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
//                    mPreviewSurfaceChangedDone.open();
//                }
//
//                @Override
//                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
//                    return true;
//                }
//
//                @Override
//                public void onSurfaceTextureUpdated(SurfaceTexture texture) {
//                }
//            };
//
//    private final ModeStateCallback mModeStateCallback = new ModeStateCallback() {
//        @Override
//        public void onCreated(Mode mode) {
//            Log.d(TAG, "mModeStateCallback onModeOpened: ");
//            mCameraOpenCloseLock.release();
//            mMode = mode;
//            mModeCharacteristics = mode.getModeCharacteristics();
//            modeConfigBuilder = mMode.getModeConfigBuilder();
//
//            configMode();
//        }
//
//        @Override
//        public void onCreateFailed(String cameraId, int modeType, int errorCode) {
//            Log.d(TAG,
//                    "mModeStateCallback onCreateFailed with errorCode: " + errorCode + " and with cameraId: " + cameraId);
//            mCameraOpenCloseLock.release();
//        }
//
//        @Override
//        public void onConfigured(Mode mode) {
//            Log.d(TAG, "mModeStateCallback onModeActivated : ");
//            mMode.startPreview();
//
//            requireActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    cvRecordOrStop.setEnabled(true);
//                }
//            });
//        }
//
//        @Override
//        public void onConfigureFailed(Mode mode, int errorCode) {
//            Log.d(TAG, "mModeStateCallback onConfigureFailed with cameraId: " + mode.getCameraId());
//            mCameraOpenCloseLock.release();
//        }
//
//        @Override
//        public void onFatalError(Mode mode, int errorCode) {
//            Log.d(TAG, "mModeStateCallback onFatalError with errorCode: " + errorCode + " and with cameraId: "
//                    + mode.getCameraId());
//            mCameraOpenCloseLock.release();
//        }
//
//        @Override
//        public void onReleased(Mode mode) {
//            Log.d(TAG, "mModeStateCallback onModeReleased: ");
//            mCameraOpenCloseLock.release();
//        }
//    };
//}
