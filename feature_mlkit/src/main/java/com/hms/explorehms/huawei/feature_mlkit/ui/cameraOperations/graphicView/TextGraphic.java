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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.huawei.hms.mlsdk.text.MLText;

public class TextGraphic extends BaseGraphic {

    private static final int TEXT_COLOR = Color.WHITE;
    private static final float TEXT_SIZE = 45.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final MLText.Base text;

    public TextGraphic(GraphicOverlay overlay, MLText.Base text) {
        super(overlay);
        this.text = text;
        this.rectPaint = new Paint();
        this.rectPaint.setColor(TextGraphic.TEXT_COLOR);
        this.rectPaint.setStyle(Paint.Style.STROKE);
        this.rectPaint.setStrokeWidth(TextGraphic.STROKE_WIDTH);
        this.textPaint = new Paint();
        this.textPaint.setColor(TextGraphic.TEXT_COLOR);
        this.textPaint.setTextSize(TextGraphic.TEXT_SIZE);
    }

    /**
     * Draw text boundaries accurately based on boundary points.
     *
     * @param canvas Canvas object
     */
    @Override
    public void draw(Canvas canvas) {
        if (this.text == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }

        // Draw text boundaries accurately based on boundary points.
        Point[] points = this.text.getVertexes();
        if (points != null && points.length == 4) {
            for (int i = 0; i < points.length; i++) {
                points[i].x = (int) this.translateX(points[i].x);
                points[i].y = (int) this.translateY(points[i].y);
            }
            float[] pts = {points[0].x, points[0].y, points[1].x, points[1].y,
                    points[1].x, points[1].y, points[2].x, points[2].y,
                    points[2].x, points[2].y, points[3].x, points[3].y,
                    points[3].x, points[3].y, points[0].x, points[0].y};
            float averageHeight = ((points[3].y - points[0].y) + (points[2].y - points[1].y)) / 2.0f;
            float textSize = averageHeight * 0.7f;
            float offset = averageHeight / 4;
            this.textPaint.setTextSize(textSize);
            canvas.drawLines(pts, this.rectPaint);
            Path path = new Path();
            path.moveTo(points[3].x, points[3].y - offset);
            path.lineTo(points[2].x, points[2].y - offset);
            canvas.drawLines(pts, this.rectPaint);
            canvas.drawTextOnPath(this.text.getStringValue(), path, 0, 0, this.textPaint);
        }
    }
}
