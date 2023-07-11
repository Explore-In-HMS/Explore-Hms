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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor;

import android.graphics.Bitmap;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.HandKeyPointGraphic;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting;
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerSetting.Factory;

public class HandKeypointTransactor extends BaseTransactor<List<MLHandKeypoints>> {

    private static final String TAG = HandKeypointTransactor.class.getSimpleName();

    private static final int MAXHANDRESULTS = 3;

    private final MLHandKeypointAnalyzer analyzer;

    private long start;

    public HandKeypointTransactor() {
        MLHandKeypointAnalyzerSetting setting = new Factory()
                // MLHandKeypointAnalyzerSetting.TYPE_ALL indicates that all results are returned.
                // MLHandKeypointAnalyzerSetting.TYPE_KEYPOINT_ONLY indicates that only hand keypoint information is returned.
                // MLHandKeypointAnalyzerSetting.TYPE_RECT_ONLY indicates that only palm information is returned.
                .setSceneType(MLHandKeypointAnalyzerSetting.TYPE_ALL)
                // Set the maximum number of hand regions that can be detected in an image.
                // By default, a maximum of 10 hand regions can be detected.
                .setMaxHandResults(MAXHANDRESULTS)
                .create();
        this.analyzer = MLHandKeypointAnalyzerFactory.getInstance().getHandKeypointAnalyzer(setting);
    }

    @Override
    public void stop() {
        if (this.analyzer != null) {
            this.analyzer.stop();
        }
    }

    /**
     * detectInImage with MLFrame image object
     *
     * @param image MLFrame object
     * @return MLHandKeypointAnalyzer.asyncAnalyseFrame(image)
     */
    @Override
    public Task<List<MLHandKeypoints>> detectInImage(MLFrame image) {
        start = System.currentTimeMillis();
        return this.analyzer.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLHandKeypoints> results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d(TAG, "onSuccess hand detect time end to end:" + (System.currentTimeMillis() - start));
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        HandKeyPointGraphic graphic = new HandKeyPointGraphic(graphicOverlay, results);
        graphicOverlay.addGraphic(graphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "HandKeypoint onFailure : " + e.getMessage(), e);
    }
}

