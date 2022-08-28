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
package com.hms.explorehms.huawei.feature_operationanalysis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 3;

    private int mode = NONE;

    private final Matrix matrix = new Matrix();

    private final PointF last = new PointF();
    private final PointF start = new PointF();
    private final float minScale = 0.4f;
    private float maxScale = 8f;
    private float[] m;

    private float saveScale = 1f;
    private float right;
    private float bottom;
    private float originalBitmapWidth;
    private float originalBitmapHeight;

    private ScaleGestureDetector mScaleDetector;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        super.setClickable(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int bmHeight = getBmHeight();
        int bmWidth = getBmWidth();
        if (bmHeight == 0){
            bmHeight = 1;
        }if (bmWidth == 0){
            bmWidth = 1;
        }
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        //Fit to screen.
        float scale = width > height ? height / bmHeight :  width / bmWidth;

        matrix.setScale(scale, scale);
        saveScale = 1f;

        originalBitmapWidth = scale * bmWidth;
        originalBitmapHeight = scale * bmHeight;

        // Center the image
        float redundantYSpace = (height - originalBitmapHeight);
        float redundantXSpace = (width - originalBitmapWidth);

        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        setImageMatrix(matrix);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        matrix.getValues(m);
        float x = m[Matrix.MTRANS_X];
        float y = m[Matrix.MTRANS_Y];
        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            //when one finger is touching
            //set the mode to DRAG
            case MotionEvent.ACTION_DOWN:
                last.set(event.getX(), event.getY());
                start.set(last);
                mode = DRAG;
                break;
            //when two fingers are touching
            //set the mode to ZOOM
            case MotionEvent.ACTION_POINTER_DOWN:
                last.set(event.getX(), event.getY());
                start.set(last);
                mode = ZOOM;
                break;
            //when a finger moves
            //If mode is applicable move image
            case MotionEvent.ACTION_MOVE:
                //if the mode is ZOOM or
                //if the mode is DRAG and already zoomed
                if (mode == ZOOM || (mode == DRAG && saveScale > minScale)) {
                    float deltaX = curr.x - last.x;// x difference
                    float deltaY = curr.y - last.y;// y difference
                    float scaleWidth = Math.round(originalBitmapWidth * saveScale);// width after applying current scale
                    float scaleHeight = Math.round(originalBitmapHeight * saveScale);// height after applying current scale

                    boolean limitX = false;
                    boolean limitY = false;

                    //if scaleWidth is smaller than the views width
                    //in other words if the image width fits in the view
                    //limit left and right movement
                    if (scaleWidth < getWidth()) {
                        deltaX = 0;
                        limitY = true;
                    }
                    //if scaleHeight is smaller than the views height
                    //in other words if the image height fits in the view
                    //limit up and down movement
                    else if (scaleHeight < getHeight()) {
                        deltaY = 0;
                        limitX = true;
                    }
                    //if the image doesnt fit in the width or height
                    //limit both up and down and left and right
                    else {
                        limitX = true;
                        limitY = true;
                    }

                    if (limitY) {
                        if (y + deltaY > 0) {
                            deltaY = -y;
                        } else  if (y + deltaY < -bottom) {
                            deltaY = -(y + bottom);
                        }

                    }

                    if (limitX) {
                        if (x + deltaX > 0) {
                            deltaX = -x;
                        } else if (x + deltaX < -right) {
                            deltaX = -(x + right);
                        }

                    }
                    //move the image with the matrix
                    matrix.postTranslate(deltaX, deltaY);
                    //set the last touch location to the current
                    last.set(curr.x, curr.y);
                }
                break;
            //first finger is lifted
            case MotionEvent.ACTION_UP:
                mode = NONE;
                int xDiff = (int) Math.abs(curr.x - start.x);
                int yDiff = (int) Math.abs(curr.y - start.y);
                if (xDiff < CLICK && yDiff < CLICK)
                    performClick();
                break;
            // second finger is lifted
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            default:
                break;
        }
        setImageMatrix(matrix);
        invalidate();
        return true;
    }

    public void setMaxZoom(float x) {
        maxScale = x;
    }

    private int getBmWidth() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return 0;
    }

    private int getBmHeight() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return 0;
    }




    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = saveScale * scaleFactor;

            if (newScale < maxScale && newScale > minScale) {
                saveScale = newScale;
                float width = getWidth();
                float height = getHeight();

                right = (originalBitmapWidth * saveScale) - width;
                bottom = (originalBitmapHeight * saveScale) - height;

                float scaledBitmapWidth = originalBitmapWidth * saveScale;
                float scaledBitmapHeight = originalBitmapHeight * saveScale;

                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    matrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                } else {
                    matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                }
            }
            return true;
        }

    }




}
