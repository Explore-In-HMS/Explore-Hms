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

import java.util.List;

public class ImageClassificationGraphicRemote extends BaseGraphic {

    private static final int MAX_LENGTH = 30;
    private final Paint textPaint;
    private final GraphicOverlay overlay;
    private final Context mContext;
    private List<String> classifications;

    public ImageClassificationGraphicRemote(GraphicOverlay overlay, Context context, List<String> classifications) {
        super(overlay);
        this.overlay = overlay;
        this.mContext = context;
        this.classifications = classifications;
        this.textPaint = new Paint();
        this.textPaint.setTextSize(dp2px(this.mContext, 14));
    }

    @Override
    public synchronized void draw(Canvas canvas) {
        float x = 0f;
        int index = 0;
        float space = dp2px(this.mContext, 16);
        float y = this.overlay.getHeight() - dp2px(this.mContext, 30);

        this.textPaint.setColor(Utils.getColorOptionsRandomly());

        for (String classification : this.classifications) {
            if (classification.length() > MAX_LENGTH) {
                canvas.drawText(classification.substring(0, MAX_LENGTH), x, y, this.textPaint);
                y = y - space;
                canvas.drawText(classification.substring(MAX_LENGTH), x, y, this.textPaint);
            } else {
                index++;
                if (index == 1) {
                    x = dp2px(this.mContext, 12);
                } else if (index == 2) {
                    x = this.overlay.getWidth() / 2.6f;
                }
                canvas.drawText(classification, x, y, this.textPaint);
                if (index == 2) {
                    y = y - space;
                    index = 0;
                }
            }
        }
    }

    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }

}
