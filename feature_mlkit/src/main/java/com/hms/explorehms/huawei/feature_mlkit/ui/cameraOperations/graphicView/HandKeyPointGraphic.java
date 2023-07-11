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
import android.graphics.Path;
import android.graphics.Rect;

import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoint;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;

import java.util.ArrayList;
import java.util.List;

public class HandKeyPointGraphic extends BaseGraphic {

    private final List<MLHandKeypoints> results;

    private final Paint circlePaint;
    private final Paint linePaint;
    private final Paint rectPaint;
    private final Rect rect;

    /**
     * HandKeyPointGraphic constructor
     * makes set GraphicOverlay with overlay params and set
     * makes set and draws GraphicOverlay such as drawing hand contours and hand analyser contents
     * makes graphic overlay such as fingerLinePaints, jointPointPaints
     *
     * @param overlay
     * @param results
     */
    public HandKeyPointGraphic(GraphicOverlay overlay, List<MLHandKeypoints> results) {
        super(overlay);
        this.results = results;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        linePaint.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5f);
        rectPaint.setAntiAlias(true);

        rect = new Rect();

    }

    /**
     * draw method makes drawing analyser result context on the CameraSurfaceView
     * such as drawing hand drawing hand contours and hand analyser contents
     * such as TYPE_THUMB_FOURTH , TYPE_RING_FINGER_THIRD, TYPE_MIDDLE_FINGER_SECOND
     *
     * @param canvas Canvas object
     */
    @Override
    public synchronized void draw(Canvas canvas) {

        for (int i = 0; i < results.size(); i++) {
            MLHandKeypoints result = results.get(i);

            canvas.drawRect(rect, rectPaint);

            List<Path> paths = new ArrayList<>();

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FOURTH), result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_WRIST), result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FIRST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_THUMB_FIRST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FOURTH), result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_SECOND)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FIRST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_FOREFINGER_FIRST), result.getHandKeypoint(MLHandKeypoint.TYPE_WRIST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FOURTH), result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_THIRD), result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_SECOND)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FIRST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_MIDDLE_FINGER_FIRST), result.getHandKeypoint(MLHandKeypoint.TYPE_WRIST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FOURTH), result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_THIRD), result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_SECOND)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FIRST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_RING_FINGER_FIRST), result.getHandKeypoint(MLHandKeypoint.TYPE_WRIST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FOURTH), result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_THIRD)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_THIRD), result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_SECOND)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_SECOND), result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FIRST)));

            paths.add(getPath(result.getHandKeypoint(MLHandKeypoint.TYPE_LITTLE_FINGER_FIRST), result.getHandKeypoint(MLHandKeypoint.TYPE_WRIST)));

            for (int j = 0; j < paths.size(); j++) {
                if (paths.get(j) != null) {
                    canvas.drawPath(paths.get(j), linePaint);
                }
            }
            Rect translateRect = translateRect(result.getRect());
            canvas.drawRect(translateRect, linePaint);
            // Attention. If the coordinate points need to be mapped to the original image, use translateX and translateY to convert the input coordinates.

            for (MLHandKeypoint handKeypoint : result.getHandKeypoints()) {

                if (!(Math.abs(handKeypoint.getPointX() - 0f) == 0 && Math.abs(handKeypoint.getPointY() - 0f) == 0)) {
                    canvas.drawCircle(translateX(handKeypoint.getPointX()),
                            translateY(handKeypoint.getPointY()), 24f, circlePaint);
                }

            }
        }
    }

    private Path getPath(MLHandKeypoint p1, MLHandKeypoint p2) {
        if (p1 == null || p2 == null) {
            return null;
        }
        if (p1.getPointX() == 0f && p1.getPointY() == 0f) {
            return null;
        }

        if (p2.getPointX() == 0f && p2.getPointY() == 0f) {
            return null;
        }
        Path path = new Path();
        path.moveTo(translateX(p1.getPointX()),
                translateY(p1.getPointY()));
        path.lineTo(translateX(p2.getPointX()),
                translateY(p2.getPointY()));
        return path;
    }
}
