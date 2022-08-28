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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.cameraOperations;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.huawei.hms.common.size.Size;
import com.huawei.hms.mlsdk.common.LensEngine;

import java.io.IOException;

public class CameraSurfaceView extends ViewGroup {

    private static final String TAG = CameraSurfaceView.class.getSimpleName();

    private Context mContext;
    private SurfaceView mSurfaceView;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private LensEngine mLensEngine;

    private GraphicOverlay mOverlay;

    /**
     * CameraSurfaceView constructor
     *
     * @param context
     * @param attrs   :
     */
    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;

        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(mSurfaceView);
    }

    /**
     * start method gets LensEngine object and sets this SurfaceView's lensEngine
     *
     * @param lensEngine
     * @throws IOException
     */
    public void start(LensEngine lensEngine) throws IOException {
        if (lensEngine == null) {
            stop();
        }

        mLensEngine = lensEngine;

        if (mLensEngine != null) {
            mStartRequested = true;
            startIfReady();
        }
    }

    /**
     * start method gets LensEngine and GraphicOverlay objects and sets this SurfaceView's GraphicOverlay
     *
     * @param lensEngine
     * @param overlay
     * @throws IOException
     */
    public void start(LensEngine lensEngine, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(lensEngine);
    }

    /**
     * stop method makes close LensEngine
     */
    public void stop() {
        if (mLensEngine != null) {
            mLensEngine.close();
        }
    }

    /**
     * release method makes release LensEngine
     */
    public void release() {
        if (mLensEngine != null) {
            mLensEngine.release();
            mLensEngine = null;
        }
    }

    /**
     * startIfReady method gets LensEngine dimensions and edits setCameraInfo
     *
     * @throws IOException
     */
    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            mLensEngine.run(mSurfaceView.getHolder());
            if (mOverlay != null) {
                Size size = mLensEngine.getDisplayDimension();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    mOverlay.setCameraInfo(min, max, mLensEngine.getLensType());
                } else {
                    mOverlay.setCameraInfo(max, min, mLensEngine.getLensType());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surface) {
            mSurfaceAvailable = true;
            try {
                startIfReady();
            } catch (IOException e) {
                Log.e(TAG, "Could not start camera source.", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface) {
            mSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //This method will be trigger when surface is changed.
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int previewWidth = 320;
        int previewHeight = 240;
        if (mLensEngine != null) {
            Size size = mLensEngine.getDisplayDimension();
            if (size != null) {
                previewWidth = size.getWidth();
                previewHeight = size.getHeight();
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            int tmp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = tmp;
        }

        final int viewWidth = right - left;
        final int viewHeight = bottom - top;

        int childWidth;
        int childHeight;
        int childXOffset = 0;
        int childYOffset = 0;
        float widthRatio = (float) viewWidth / (float) previewWidth;
        float heightRatio = (float) viewHeight / (float) previewHeight;

        // To fill the view with the camera preview, while also preserving the correct aspect ratio,
        // it is usually necessary to slightly oversize the child and to crop off portions along one
        // of the dimensions.  We scale up based on the dimension requiring the most correction, and
        // compute a crop offset for the other dimension.
        if (widthRatio > heightRatio) {
            childWidth = viewWidth;
            childHeight = (int) ((float) previewHeight * widthRatio);
            childYOffset = (childHeight - viewHeight) / 2;
        } else {
            childWidth = (int) ((float) previewWidth * heightRatio);
            childHeight = viewHeight;
            childXOffset = (childWidth - viewWidth) / 2;
        }

        for (int i = 0; i < getChildCount(); ++i) {
            // One dimension will be cropped.  We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            getChildAt(i).layout(
                    -1 * childXOffset, -1 * childYOffset,
                    childWidth - childXOffset, childHeight - childYOffset);
        }

        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }
}
