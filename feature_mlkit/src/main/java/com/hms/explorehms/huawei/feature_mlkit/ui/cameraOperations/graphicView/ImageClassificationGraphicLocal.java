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
import android.graphics.Paint;

import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.classification.MLImageClassification;

import java.util.List;

public class ImageClassificationGraphicLocal extends BaseGraphic {

    private final Paint textPaint;
    private final GraphicOverlay overlay;
    private final Context mContext;
    private final List<MLImageClassification> classifications;

    public ImageClassificationGraphicLocal(GraphicOverlay overlay, Context context, List<MLImageClassification> classifications) {
        super(overlay);
        this.overlay = overlay;
        this.mContext = context;
        this.classifications = classifications;
        this.textPaint = new Paint();
        this.textPaint.setTextSize(dp2px(this.mContext, 16));
    }

    @Override
    public synchronized void draw(Canvas canvas) {
        float x = this.overlay.getWidth() / 2.6f;
        float y = this.overlay.getHeight() / 1.3f;

        for (MLImageClassification classification : this.classifications) {
            this.textPaint.setColor(Utils.getColorOptionsRandomly());
            canvas.drawText(classification.getName(), x, y, this.textPaint);
            y = y - dp2px(this.mContext, 18);
        }
    }

    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }

}
