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
package com.hms.explorehms.scankit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.R;
import com.hms.explorehms.scankit.draw.ScanResultView;

import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzer;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.io.IOException;
import java.util.Objects;

import static com.huawei.hms.ml.scan.HmsScanBase.ALL_SCAN_TYPE;


public class CommonActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PHOTO = 0X1113;
    private static final String TAG = "CommonActivity";
    private SurfaceHolder surfaceHolder;
    private CameraOperation cameraOperation;
    private SurfaceCallBack surfaceCallBack;
    private CommonHandler handler;
    private boolean isShow;
    private int mode;
    private TextView mscanTips;
    public static final String SCAN_RESULT = "SCAN_RESULT";
    public ScanResultView scanResultView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common);

        setupToolbar();
        int defaultValue = -1;
        mode = getIntent().getIntExtra(ScanKitActivity.DECODE_MODE, defaultValue);
        ImageView mscanArs = findViewById(R.id.scan_ars);
        mscanTips = findViewById(R.id.scan_tip);
        if (mode == ScanKitActivity.SCAN_MULTIPROCESSOR_ASYNC || mode == ScanKitActivity.SCAN_MULTIPROCESSOR_SYNC){
            mscanArs.setVisibility(View.INVISIBLE);
            mscanTips.setText(R.string.scan_showresult);
            AlphaAnimation disappearAnimation = new AlphaAnimation(1, 0);
            disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //This method is called when the animation start
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mscanTips != null) {
                        mscanTips.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //This method is called when the animation repeat
                }
            });
            disappearAnimation.setDuration(3000);
            mscanTips.startAnimation(disappearAnimation);
        }
        cameraOperation = new CameraOperation();
        surfaceCallBack = new SurfaceCallBack();
        SurfaceView cameraPreview = findViewById(R.id.surfaceView);
        adjustSurface(cameraPreview);
        surfaceHolder = cameraPreview.getHolder();
        isShow = false;

        setPictureScanOperation();

        scanResultView = findViewById(R.id.scan_result_view);
    }

    private void setupToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar_common);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void adjustSurface(SurfaceView cameraPreview) {
        FrameLayout.LayoutParams paramSurface = (FrameLayout.LayoutParams) cameraPreview.getLayoutParams();
        if (getSystemService(Context.WINDOW_SERVICE) != null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display defaultDisplay = windowManager.getDefaultDisplay();
            Point outPoint = new Point();
            defaultDisplay.getRealSize(outPoint);
            float screenWidth = outPoint.x;
            float screenHeight = outPoint.y;
            float rate;
            if (screenWidth / (float) 1080 > screenHeight / (float) 1920) {
                rate = screenWidth / (float) 1080;
                int targetHeight = (int) (1920 * rate);
                paramSurface.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                paramSurface.height = targetHeight;
                int topMargin = (int) (-(targetHeight - screenHeight) / 2);
                if (topMargin < 0) {
                    paramSurface.topMargin = topMargin;
                }
            } else {
                rate = screenHeight / (float) 1920;
                int targetWidth = (int) (1080 * rate);
                paramSurface.width = targetWidth;
                paramSurface.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                int leftMargin = (int) (-(targetWidth - screenWidth) / 2);
                if (leftMargin < 0) {
                    paramSurface.leftMargin = leftMargin;
                }
            }
        }
    }

    private void setPictureScanOperation() {
        ImageView imgBtn = findViewById(R.id.img_btn);
        imgBtn.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            CommonActivity.this.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShow) {
            initCamera();
        } else {
            surfaceHolder.addCallback(surfaceCallBack);
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quit();
            handler = null;
        }
        cameraOperation.close();
        if (!isShow) {
            surfaceHolder.removeCallback(surfaceCallBack);
        }
        super.onPause();
    }

    private void initCamera() {
        try {
            cameraOperation.open(surfaceHolder);
            if (handler == null) {
                handler = new CommonHandler(this, cameraOperation, mode);
            }
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || requestCode != REQUEST_CODE_PHOTO) {
            return;
        }
        try {
            // Image-based scanning mode
            if (mode == ScanKitActivity.SCAN_MULTIPROCESSOR_SYNC) {
                decodeMultiSyn(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
            } else if (mode == ScanKitActivity.SCAN_MULTIPROCESSOR_ASYNC) {
                decodeMultiAsyn(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
            }
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    private void decodeMultiAsyn(Bitmap bitmap) {
        MLFrame image = MLFrame.fromBitmap(bitmap);
        HmsScanAnalyzer analyzer = new HmsScanAnalyzer.Creator(this).setHmsScanTypes(ALL_SCAN_TYPE).create();
        analyzer.analyzInAsyn(image).addOnSuccessListener(hmsScans -> {
            if (hmsScans != null && hmsScans.size() > 0 && hmsScans.get(0) != null && !TextUtils.isEmpty(hmsScans.get(0).getOriginalValue())) {
                HmsScan[] infos = new HmsScan[hmsScans.size()];
                Intent intent = new Intent(this, DisplayMulActivity.class);
                intent.putExtra(SCAN_RESULT, hmsScans.toArray(infos));
                CommonActivity.this.finish();
                startActivity(intent);
            }
        }).addOnFailureListener(e -> Log.w(TAG, e));
    }

    private void decodeMultiSyn(Bitmap bitmap) {
        MLFrame image = MLFrame.fromBitmap(bitmap);
        HmsScanAnalyzer analyzer = new HmsScanAnalyzer.Creator(this).setHmsScanTypes(ALL_SCAN_TYPE).create();
        SparseArray<HmsScan> result = analyzer.analyseFrame(image);
        if (result != null && result.size() > 0 && result.valueAt(0) != null && !TextUtils.isEmpty(result.valueAt(0).getOriginalValue())) {
            HmsScan[] info = new HmsScan[result.size()];
            for (int index = 0; index < result.size(); index++) {
                info[index] = result.valueAt(index);
            }
            Intent intent = new Intent(this, DisplayActivity.class );
            intent.putExtra(SCAN_RESULT, info);
            CommonActivity.this.finish();
            startActivity(intent);
        }
    }

    class SurfaceCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            if (!isShow) {
                isShow = true;
                initCamera();
            }
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            //This method will be called whenever surface is changed.
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            isShow = false;
        }
    }
}
