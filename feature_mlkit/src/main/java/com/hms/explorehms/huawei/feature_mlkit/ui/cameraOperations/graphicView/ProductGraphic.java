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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProduct;

import java.util.List;

public class ProductGraphic extends BaseGraphic {

    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    private final GraphicOverlay overlay;
    private final List<MLVisionSearchProduct> products;
    private final Paint boxPaint;
    private final Paint textPaint;

    public ProductGraphic(GraphicOverlay overlay, List<MLVisionSearchProduct> products) {
        super(overlay);
        this.overlay = overlay;
        this.products = products;
        this.boxPaint = new Paint();
        this.boxPaint.setColor(Color.WHITE);
        this.boxPaint.setStyle(Style.STROKE);
        this.boxPaint.setStrokeWidth(ProductGraphic.STROKE_WIDTH);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(ProductGraphic.TEXT_SIZE);
    }

    /**
     * Draw object boundaries accurately based on boundary points.
     * and draw some details.
     *
     * @param canvas Canvas object
     */
    @Override
    public void draw(Canvas canvas) {

        float x = this.overlay.getWidth() / 2.6f;
        float y = this.overlay.getHeight() / 1.3f;

        for (MLVisionSearchProduct productVisionSearch : this.products) {

            String productId = "productId : " + productVisionSearch.getProductId();
            canvas.drawText(productId, x, y, this.textPaint);

            String productPosb = "productPosb : " + productVisionSearch.getPossibility();
            canvas.drawText(productPosb, x, y + 20, this.textPaint);

            String productUrl = "productUrl : " + productVisionSearch.getProductUrl();
            canvas.drawText(productUrl, x, y + 40, this.textPaint);
        }

    }

}

