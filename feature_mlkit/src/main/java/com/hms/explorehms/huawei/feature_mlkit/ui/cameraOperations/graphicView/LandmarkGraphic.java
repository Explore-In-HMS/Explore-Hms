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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.huawei.hms.mlsdk.common.MLCoordinate;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;

public class LandmarkGraphic extends BaseGraphic {

    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final MLRemoteLandmark landmark;
    private final Paint boxPaint;
    private final Paint textPaint;
    private final GraphicOverlay overlay;

    public LandmarkGraphic(GraphicOverlay overlay, MLRemoteLandmark landmark) {
        super(overlay);
        this.overlay = overlay;
        this.landmark = landmark;
        this.boxPaint = new Paint();
        this.boxPaint.setColor(Color.WHITE);
        this.boxPaint.setStyle(Paint.Style.STROKE);
        this.boxPaint.setStrokeWidth(STROKE_WIDTH);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(TEXT_SIZE);
    }


    @Override
    public void draw(Canvas canvas) {
        Rect boundingBox = this.landmark.getBorder();
        if (null != boundingBox) {
            RectF rect = new RectF(boundingBox);
            rect.left = this.translateX(rect.left);
            rect.top = this.translateY(rect.top);
            rect.right = this.translateX(rect.right);
            rect.bottom = this.translateY(rect.bottom);
            canvas.drawRect(rect, this.boxPaint);
        }

        int x = 0;
        int y = this.overlay.getHeight() / 2;

        canvas.drawText(this.landmark.getLandmark(), x, y, this.textPaint);
        if (this.landmark.getPositionInfos() == null || this.landmark.getPositionInfos().isEmpty()) {
            canvas.drawText("Unknown location", x, y + 50, this.textPaint);
        } else {
            for (MLCoordinate location : this.landmark.getPositionInfos()) {
                canvas.drawText("Lat: " + location.getLat() + " ,Lng:" + location.getLng(), x, y + 100, this.textPaint);
            }
            canvas.drawText("confidence: " + this.landmark.getPossibility(), x, y + 150, this.textPaint);
        }
    }

    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }

}
