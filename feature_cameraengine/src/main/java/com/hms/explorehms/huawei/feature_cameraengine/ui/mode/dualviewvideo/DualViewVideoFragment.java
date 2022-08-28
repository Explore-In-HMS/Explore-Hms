//package com.hms.explorehms.huawei.feature_cameraengine.ui.mode.dualviewvideo;
//
//import android.graphics.SurfaceTexture;
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
//import com.hms.explorehms.huawei.feature_cameraengine.util.ViewUtils;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textview.MaterialTextView;
//import com.huawei.camera.camerakit.CameraKit;
//import com.huawei.camera.camerakit.Mode;
//import com.huawei.camera.camerakit.ModeCharacteristics;
//import com.huawei.camera.camerakit.ModeConfig;
//import com.huawei.camera.camerakit.ModeStateCallback;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Semaphore;
//
//public class DualViewVideoFragment extends Fragment {
//
//    private final String TAG = "CameraEngine";
//
//    private final long PREVIEW_SURFACE_READY_TIMEOUT = 5000L;
//
//    private CameraKit mCameraKit;
//
//    private boolean isGetInstance = false;
//
//    private final ConditionVariable mPreviewSurfaceChangedDone = new ConditionVariable();
//
//    private Mode mMode;
//    private Mode[] mModeArray;
//    private ModeCharacteristics mModeCharacteristic;
//    private List<ModeCharacteristics> mModeCharacteristicList;
//    private ModeConfig.Builder modeConfigBuilder;
//    private List<ModeStateCallback> mModeStateCallback;
//
//    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
//
//    private Size mPreviewSize;
//    private Size mCaptureSize;
//
//    private File mFile;
//
//    private HandlerThread mCameraKitThread;
//    private Handler mCameraKitHandler;
//
//    private float currentZoom = 1f;
//    private float zoomValue = 0.5f;
//
//    private String[] cameraList;
//    private List<Integer> cameraTypeList;
//
//    private String mCurrentCameraId;
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
//    private CardView cvCaptureImage;
//    private CardView cvZoomIn;
//    private CardView cvZoomOut;
//    private CardView cvLastImage;
//
//    private ProgressBar pbCameraThread;
//
//    private AutoFitTextureView mTextureView;
//
//    private View view;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        view = inflater.inflate(R.layout.fragment_hdr_mode_camera_engine,container,false);
//        initUI();
//        initListener();
//
//
//        return view;
//    }
//
//    private void initUI(){
//        cvCaptureImage = view.findViewById(R.id.cv_capture_image_hdr_camera_eng);
//        buttonHideIntroduction = view.findViewById(R.id.btn_hide_intro_hdr_camera_engine);
//        buttonShowIntroduction = view.findViewById(R.id.btn_show_introduction_hdr_camera_engine);
//        clIntroduction = view.findViewById(R.id.cl_mode_introduction_hdr_camera_engine);
//        clShowIntroduction = view.findViewById(R.id.cl_show_introduction_hdr_camera_engine);
//        clFunctions = view.findViewById(R.id.cl_hdr_functions_camera_engine);
//        mTextureView = view.findViewById(R.id.aftv_hdr_camera_engine);
//        pbCameraThread = view.findViewById(R.id.pb_hdr_mode_camera_engine);
//        cvZoomIn = view.findViewById(R.id.cv_zoom_in_hdr_camera_engine);
//        cvZoomOut = view.findViewById(R.id.cv_zoom_out_hdr_camera_engine);
//        cvLastImage = view.findViewById(R.id.cv_last_image_hdr_camera_engine);
//        tvZoomLevel = view.findViewById(R.id.tv_zoom_level_hdr_camera_engine);
//
//    }
//
//    private void initListener(){
//        cvCaptureImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                captureImage();
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
//                if(currentZoom != 35){ //max zoom
//                    currentZoom = currentZoom + zoomValue;
//                    int result = mMode.setZoom(currentZoom);
//
//                    if(result == 0)
//                        tvZoomLevel.setText(String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
//                }
//            }
//        });
//
//        cvZoomOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(currentZoom != 1){
//                    currentZoom = currentZoom - zoomValue;
//
//                    int result = mMode.setZoom(currentZoom);
//
//                    if(result == 0)
//                        tvZoomLevel.setText(String.format(getString(R.string.txt_zoom_level_camera_engine),currentZoom));
//                }
//            }
//        });
//
//        cvLastImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mFile != null){
//                    ViewUtils.showDialogImagePeekView(getActivity(),getContext(),mFile,getString(R.string.txt_hdr_mode_camera_engine));
//                }else{
//                    Toast.makeText(getContext(),getString(R.string.txt_no_picture_camera_engine),Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void createMode(String mCurrentCameraId){
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
//        cameraList = mCameraKit.getCameraIdList();
//        if((cameraList != null) && (cameraList.length > 0)){
//
//            if (mModeStateCallback.size() != 0) {
//                releaseMultiViewMode();
//            }
//
//            // Initialize the input parameters of the mode creation method.
//            initialMultiView();
//            // Call the API to create a mode.
//            mCameraKit.createMultiViewMode(cameraTypeList, mModeStateCallback, mCameraKitHandler);
//        }
//    }
//
//    private void initialMultiView(){
//        // Add a camera combination.
//        cameraTypeList = new ArrayList<>();
//        cameraTypeList.add(Mode.CameraType.BACK_MAIN);
//        if ("0".equals(mCurrentCameraId)){
//            cameraTypeList.add(Mode.CameraType.BACK_WIDE);
//        } else {
//            cameraTypeList.add(Mode.CameraType.FRONT);
//        }
//        // Initialize the query object of the capability corresponding to the mode.
//        mModeCharacteristicList =  mCameraKit.getMultiViewModeCharacteristics(cameraTypeList);
//        mModeArray = new Mode[cameraTypeList.size()];
//        // Initialize the stateCallback method of the mode.
//        mModeStateCallback = new ArrayList<>();
//        getModeStateCallbackList();
//        // Initialize the preview surface and video surface.
//        getPreViewSurfaceAndVideoSurface();
//    }
//
//    private void getModeStateCallbackList(){
//
//        ModeStateCallback mModeStateCallback = new ModeStateCallback() {
//            @Override
//            public void onCreated(Mode mode) {
//                Log.d(TAG, "mModeStateCallback onModeOpened: ");
//                mCameraOpenCloseLock.release();
//                mMode = mode;
//                mModeCharacteristic = mode.getModeCharacteristics();
//                modeConfigBuilder = mMode.getModeConfigBuilder();
//
//                configMode(1);
//            }
//
//            @Override
//            public void onCreateFailed(String cameraId, int modeType, int errorCode) {
//                Log.d(TAG,
//                        "mModeStateCallback onCreateFailed with errorCode: " + errorCode + " and with cameraId: " + cameraId);
//                mCameraOpenCloseLock.release();
//            }
//
//            @Override
//            public void onConfigured(Mode mode) {
//                Log.d(TAG, "mModeStateCallback onModeActivated : ");
//                mMode.startPreview();
//
//                requireActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        cvCaptureImage.setEnabled(true);
//                    }
//                });
//            }
//
//            @Override
//            public void onConfigureFailed(Mode mode, int errorCode) {
//                Log.d(TAG, "mModeStateCallback onConfigureFailed with cameraId: " + mode.getCameraId());
//                mCameraOpenCloseLock.release();
//            }
//
//            @Override
//            public void onFatalError(Mode mode, int errorCode) {
//                Log.d(TAG, "mModeStateCallback onFatalError with errorCode: " + errorCode + " and with cameraId: "
//                        + mode.getCameraId());
//                mCameraOpenCloseLock.release();
//            }
//
//            @Override
//            public void onReleased(Mode mode) {
//                Log.d(TAG, "mModeStateCallback onModeReleased: ");
//                mCameraOpenCloseLock.release();
//            }
//        };
//
//        mModeCharacteristicList.add(mModeCharacteristic);
//    }
//
//
//    private void getPreViewSurfaceAndVideoSurface() {
//        // Set the horizontal offset of the small window relative to the screen.
//        int xOffset = 24;
//        // Set the vertical offset of the small window relative to the screen.
//        int yOffset = 16;
//        // Set the width and height of the texture.
//        setSurface(mTextureView);
//        // Initialize OpenGL.
//        mTwinsVideoExtension = new TwinsVideoExtension(requireContext(), mTextureView);
//        // Pass the size of the image drawn by OpenGL (mTotalPreviewSize), preview resolution of the full-screen image (mBayerPreviewSize), and preview resolution of the small-window image (mMonoPreviewSize),
//        // Size of the small window (mSmallViewSize) and offset of the small window.
//        mGLRenderThread = mTwinsVideoExtension.initGLRenderThread(mTotalPreviewSize, mBayerPreviewSize, mMonoPreviewSize, mSmallViewSize, xOffset, yOffset);
//        // Obtain the surface of the large window.
//        bayerSurface = mGLRenderThread.getFilteredSurface();
//        // Obtain the surface of the small window.
//        monoSurface = mGLRenderThread.getFilteredWideSurface();
//        mDualPreviewSurfaces.clear();
//        // Switch the images captured by the front and rear cameras.
//        if (mIsSwap) {
//            mDualPreviewSurfaces.add(new Surface(monoSurface));
//            mDualPreviewSurfaces.add(new Surface(bayerSurface));
//        } else {
//            mDualPreviewSurfaces.add(new Surface(bayerSurface));
//            mDualPreviewSurfaces.add(new Surface(monoSurface));
//        }
//
//        //generate the video surface
//    }
//
//    private void configMode(int index) {
//        mModeConfigBuilderList.get(index).addVideoSurface(mVideoSurfaceList.get(index));
//        mModeConfigBuilderList.get(index).addPreviewSurface(mDualPreviewSurfaces.get(index));
//        mModeArray[index].configure();
//    }
//
//    private boolean initCameraKit() {
//        mCameraKit = CameraKit.getInstance(getContext());
//        if (mCameraKit == null) {
//            Log.e(TAG, "initCamerakit: this devices not support camerakit or not installed!");
//            return false;
//        }
//        return true;
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
//    private void captureImage() {
//        //TODO CAPTURE START
//        pbCameraThread.setVisibility(View.VISIBLE);
//        Log.i(TAG, "captureImage begin");
//        if (mMode != null) {
//            mMode.setImageRotation(90);
//            mFile = new File(getContext().getExternalFilesDir(null), System.currentTimeMillis() + "pic.jpg");
//            mMode.takePicture();
//        }
//        Log.i(TAG, "captureImage end");
//    }
//
//    //LifeCyle
//
//    @Override
//    public void onResume() {
//        Log.d(TAG, "onResume: ");
//        super.onResume();
//        cameraNotReady();
//        if (!initCameraKit()) {
//            Toast.makeText(getActivity(),"this devices not support camerakit or not installed",Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        startBackgroundThread();
//        if (mTextureView != null) {
//            if (mTextureView.isAvailable()) {
//                mCameraKitHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        createMode(mCurrentCameraId);
//                    }
//                });
//            }
//
//            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        Log.d(TAG, "onPause: ");
//
//        super.onPause();
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
//    /**
//     * Listener & Callbacks
//     */
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
//                            createMode(mCurrentCameraId);
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
//}
