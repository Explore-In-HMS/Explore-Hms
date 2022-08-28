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
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.huawei.hms.mlsdk.objects.MLObject;

public class ObjectGraphic extends BaseGraphic {

    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final MLObject object;
    private final Paint boxPaint;
    private final Paint textPaint;

    public ObjectGraphic(GraphicOverlay overlay, MLObject object) {
        super(overlay);
        this.object = object;
        this.boxPaint = new Paint();
        this.boxPaint.setColor(Color.WHITE);
        this.boxPaint.setStyle(Style.STROKE);
        this.boxPaint.setStrokeWidth(ObjectGraphic.STROKE_WIDTH);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(ObjectGraphic.TEXT_SIZE);
    }

    /**
     * Draw object boundaries accurately based on boundary points.
     * and draw some details.
     *
     * @param canvas Canvas object
     */
    @Override
    public void draw(Canvas canvas) {

        RectF rect = new RectF(this.object.getBorder());
        rect.left = this.translateX(rect.left);
        rect.top = this.translateY(rect.top);
        rect.right = this.translateX(rect.right);
        rect.bottom = this.translateY(rect.bottom);

        canvas.drawRect(rect, this.boxPaint);

        String trackingId = "trackingId : " + this.object.getTracingIdentity();
        canvas.drawText(trackingId, rect.left, rect.top - 30, this.textPaint);

        String objCategory = getCategoryNameAndSetColors(this.object.getTypeIdentity());
        canvas.drawText(objCategory, rect.centerX() - 50, rect.centerY(), this.textPaint);


        if (this.object.getTypePossibility() != null) {
            String confidence = "confidence : " + this.object.getTypePossibility();
            canvas.drawText(confidence, rect.left, rect.bottom + 50L, this.textPaint);
        }
    }

    private String getCategoryNameAndSetColors(int category) {
        switch (category) {
            case MLObject.TYPE_OTHER:
                this.boxPaint.setColor(Color.parseColor("#ffffff"));
                this.textPaint.setColor(Color.parseColor("#ffffff"));
                return "Unknown";
            case MLObject.TYPE_FURNITURE:
                this.boxPaint.setColor(Color.parseColor("#FF6D00"));
                this.textPaint.setColor(Color.parseColor("#FF6D00"));
                return "Home good";
            case MLObject.TYPE_GOODS:
                this.boxPaint.setColor(Color.parseColor("#e67e22"));
                this.textPaint.setColor(Color.parseColor("#e67e22"));
                return "Fashion good";
            case MLObject.TYPE_PLACE:
                this.boxPaint.setColor(Color.parseColor("#6200EA"));
                this.textPaint.setColor(Color.parseColor("#6200EA"));
                return "Place";
            case MLObject.TYPE_PLANT:
                this.boxPaint.setColor(Color.parseColor("#27ae60"));
                this.textPaint.setColor(Color.parseColor("#27ae60"));
                return "Plant";
            case MLObject.TYPE_FOOD:
                this.boxPaint.setColor(Color.parseColor("#FFD600"));
                this.textPaint.setColor(Color.parseColor("#FFD600"));
                return "Food";
            case MLObject.TYPE_FACE:
                this.boxPaint.setColor(Color.parseColor("#0091EA"));
                this.textPaint.setColor(Color.parseColor("#0091EA"));
                return "Face";
            default: // fall out
        }
        return "";
    }
}

