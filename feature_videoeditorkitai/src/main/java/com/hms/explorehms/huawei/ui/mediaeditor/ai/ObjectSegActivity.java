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

package com.hms.explorehms.huawei.ui.mediaeditor.ai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hms.explorehms.huawei.ui.common.BaseActivity;
import com.hms.explorehms.huawei.ui.common.bean.Constant;
import com.hms.explorehms.huawei.utils.SmartLog;
import com.hms.explorehms.huawei.ui.common.utils.FileUtil;
import com.huawei.hms.videoeditor.ai.HVEAIInitialCallback;
import com.huawei.hms.videoeditor.ai.HVEAIObjectSeg;
import com.huawei.hms.videoeditor.ai.HVEAIProcessCallback;
import com.huawei.hms.videoeditor.ai.HVEPosition2D;
import com.hms.explorehms.huawei.ui.common.utils.ScreenUtil;
import com.hms.explorehms.huawei.ui.common.utils.TimeUtils;
import com.hms.explorehms.huawei.ui.common.utils.ToastUtils;
import com.hms.explorehms.huawei.ui.common.utils.ToastWrapper;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectSegActivity extends BaseActivity {
    public static final String TAG = "ObjectSegActivity";

    private static final int MAX_SIZE = 512;

    private static final String PHOTO_PATH = "photo_path";

    private String mPhotoOutputDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "DCIM" + File.separator + "Camera" + File.separator + Constant.LOCAL_VIDEO_SAVE_PATH;

    private String mPhotoOutputName =
            TimeUtils.formatTimeByUS(System.currentTimeMillis(), Constant.LOCAL_VIDEO_SAVE_TIME) + ".jpg";

    private TextView tvTitle;

    private ImageView imageView;

    private ImageView ivLine;

    private String mPhotoPath = "";

    private List<HVEPosition2D> mOriginPointList = new ArrayList<>();

    private List<HVEPosition2D> mDrawPointList = new ArrayList<>();

    private HVEAIObjectSeg objectSeg;

    private Bitmap mOriginBitmap;

    private Bitmap mShowBitmap;

    private Bitmap mCutBitmap;

    public static void startActivity(Activity activity, String photoPath) {
        Intent intent = new Intent(activity, ObjectSegActivity.class);
        intent.putExtra(PHOTO_PATH, photoPath);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_object_seg);
        initView();
        initData();
    }

    private void initView() {
        SafeIntent safeIntent = new SafeIntent(getIntent());
        mPhotoPath = safeIntent.getStringExtra(PHOTO_PATH);
        findViewById(R.id.iv_certain).setVisibility(View.GONE);
        findViewById(R.id.tv_certain).setVisibility(View.VISIBLE);
        imageView = findViewById(R.id.image_view);
        ivLine = findViewById(R.id.iv_line);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cut_second_menu_segmentation);
        findViewById(R.id.iv_close).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.tv_certain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCutBitmap != null) {
                    String bitmapPath = saveCutBitmap(mCutBitmap);
                    if (!TextUtils.isEmpty(bitmapPath)) {
                        boolean isSuccess = FileUtil.saveImageToSystemAlbum(ObjectSegActivity.this, bitmapPath);
                        if (isSuccess) {
                            ToastUtils.getInstance()
                                    .showToast(ObjectSegActivity.this, getString(R.string.save_to_gallery_success),
                                            Toast.LENGTH_SHORT);
                        }
                    }
                }
                onBackPressed();
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {
        if (!TextUtils.isEmpty(mPhotoPath)) {
            mOriginBitmap = BitmapFactory.decodeFile(mPhotoPath);
            if (mOriginBitmap != null) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                ViewGroup.LayoutParams layoutParamsLine = ivLine.getLayoutParams();
                int width = ScreenUtil.getScreenWidth(this) - ScreenUtil.dp2px(32);
                int bitmapWidth = mOriginBitmap.getWidth();
                int bitmapHeight = mOriginBitmap.getHeight();
                float desRate = (float) bitmapHeight / bitmapWidth;
                int height = (int) (width * desRate);
                layoutParams.width = width;
                layoutParams.height = height;
                layoutParamsLine.width = width;
                layoutParamsLine.height = height;
                Glide.with(getApplicationContext()).load(mPhotoPath).into(imageView);
                imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (imageView.getWidth() != 0 && imageView.getHeight() != 0) {
                            mShowBitmap = Bitmap.createScaledBitmap(mOriginBitmap, imageView.getWidth(),
                                    imageView.getHeight(), true);
                        }
                        return true;
                    }
                });
            }
        }
        objectSeg = new HVEAIObjectSeg();
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        SmartLog.d(TAG, "imageView setOnTouchListener ACTION_DOWN");
                        mDrawPointList = new ArrayList<>();
                        mDrawPointList.add(new HVEPosition2D(event.getX(), event.getY()));
                        mOriginPointList = new ArrayList<>();
                        mOriginPointList
                                .add(new HVEPosition2D(getOriginPointX(event.getX()), getOriginPointY(event.getY())));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        SmartLog.d(TAG, "imageView setOnTouchListener ACTION_MOVE");
                        mDrawPointList.add(new HVEPosition2D(event.getX(), event.getY()));
                        mOriginPointList
                                .add(new HVEPosition2D(getOriginPointX(event.getX()), getOriginPointY(event.getY())));
                        break;
                    case MotionEvent.ACTION_UP:
                        drawLine(mDrawPointList);
                        SmartLog.d(TAG, "imageView setOnTouchListener ACTION_UP");
                        if (objectSeg != null && !TextUtils.isEmpty(mPhotoPath)) {
                            objectSeg.initEngine(new HVEAIInitialCallback() {
                                @Override
                                public void onProgress(int progress) {
                                    SmartLog.d(TAG, "onProgress:" + progress);
                                }

                                @Override
                                public void onSuccess() {
                                    SmartLog.d(TAG, "onSuccess");
                                    objectSeg.objectSegImageDetect(mPhotoPath, mOriginPointList,
                                            new HVEAIProcessCallback<byte[]>() {
                                                @Override
                                                public void onProgress(int progress) {

                                                }

                                                @Override
                                                public void onSuccess(byte[] masks) {
                                                    runOnUiThread(() -> {
                                                        mCutBitmap = resizeOriginBitmap(masks);
                                                        if (mCutBitmap != null) {
                                                            Glide.with(getApplicationContext())
                                                                    .load(mCutBitmap)
                                                                    .into(imageView);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(int errorCode, String errorMessage) {
                                                    SmartLog.e(TAG, errorMessage);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastWrapper
                                                                    .makeText(ObjectSegActivity.this,
                                                                            getString(R.string.ai_exception))
                                                                    .show();
                                                        }
                                                    });
                                                }
                                            });
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {

                                }
                            });
                        }
                        break;
                    default:
                        SmartLog.d(TAG, "OnTouch run in default case");
                        break;
                }
                return true;
            }
        });
    }

    private float getOriginPointX(float drawPointX) {
        float originPointX = 0F;
        if (mShowBitmap == null || mOriginBitmap == null) {
            return originPointX;
        }
        if (mShowBitmap.getWidth() == 0 || mOriginBitmap.getWidth() == 0) {
            return originPointX;
        }
        return drawPointX / mShowBitmap.getWidth() * mOriginBitmap.getWidth();
    }

    private float getOriginPointY(float drawPointY) {
        float originPointY = 0F;
        if (mShowBitmap == null || mOriginBitmap == null) {
            return originPointY;
        }
        if (mShowBitmap.getWidth() == 0 || mOriginBitmap.getWidth() == 0) {
            return originPointY;
        }
        return drawPointY / mShowBitmap.getHeight() * mOriginBitmap.getHeight();
    }

    private void drawLine(List<HVEPosition2D> points) {
        Bitmap lineBitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(lineBitmap);
        Paint paint = new Paint();
        Path path = new Path();
        int size = points.size();
        path.moveTo(points.get(0).xPos, points.get(0).yPos);
        for (int i = 1; i < size; i++) {
            path.lineTo(points.get(i).xPos, points.get(i).yPos);
        }
        paint.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5);
        canvas.drawBitmap(lineBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
        ivLine.setImageBitmap(lineBitmap);
    }

    private Bitmap resizeOriginBitmap(byte[] masks) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(mOriginBitmap, MAX_SIZE, MAX_SIZE, true);
        int[] pixels = new int[MAX_SIZE * MAX_SIZE];
        resizedBitmap.getPixels(pixels, 0, MAX_SIZE, 0, 0, MAX_SIZE, MAX_SIZE);
        for (int i = 0; i < masks.length; i++) {
            pixels[i] = (pixels[i] & 0x00ffffff) | (masks[i] << 25);
        }
        Bitmap resizedCutBitmap = Bitmap.createBitmap(pixels, 0, MAX_SIZE, MAX_SIZE, MAX_SIZE, Bitmap.Config.ARGB_8888);
        Bitmap resultBitmap = null;
        if (resizedCutBitmap != null && mShowBitmap != null) {
            resultBitmap =
                    Bitmap.createScaledBitmap(resizedCutBitmap, mShowBitmap.getWidth(), mShowBitmap.getHeight(), true);
        }
        return resultBitmap;
    }

    private String saveCutBitmap(Bitmap cutBitmap) {
        String result;
        try {
            File file = new File(mPhotoOutputDir, mPhotoOutputName);
            FileUtil.saveBitmap(cutBitmap, 100, file);
            result = file.getCanonicalPath();
        } catch (IOException e) {
            result = null;
            SmartLog.e(TAG, e.getMessage());
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (objectSeg != null) {
            objectSeg.releaseEngine();
        }
    }
}
