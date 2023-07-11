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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.scd.MLSceneDetection;

import java.util.List;

public class SceneDetectionGraphic extends BaseGraphic {

    private final Paint textPaint;
    private final GraphicOverlay overlay;
    private final List<MLSceneDetection> sceneDetectionsList;

    public SceneDetectionGraphic(GraphicOverlay overlay, Context context, List<MLSceneDetection> sceneDetectionsList) {
        super(overlay);
        this.overlay = overlay;
        this.sceneDetectionsList = sceneDetectionsList;
        this.textPaint = new Paint();
        this.textPaint.setTextSize(dp2px(context, 16));
    }

    @Override
    public synchronized void draw(Canvas canvas) {

        canvas.drawText("SceneCount：" + sceneDetectionsList.size(), overlay.getWidth() / 5, 500, textPaint);
        for (int i = 0; i < sceneDetectionsList.size(); i++) {
            textPaint.setColor(Utils.getColorOptionsRandomly());
            textPaint.setAlpha(200); // 128 = 0.5
            canvas.drawText("Scene：" + sceneDetectionsList.get(i).getResult(), overlay.getWidth() / 5, 100 * (i + 1) + 500, textPaint);
            canvas.drawText("Confidence：" + sceneDetectionsList.get(i).getConfidence(), overlay.getWidth() / 5, (100 * (i + 1)) + 550, textPaint);
        }
    }

    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }
}
