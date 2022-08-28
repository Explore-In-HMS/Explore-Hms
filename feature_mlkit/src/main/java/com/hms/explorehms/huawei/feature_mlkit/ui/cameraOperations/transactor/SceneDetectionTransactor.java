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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.SceneDetectionGraphic;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.scd.MLSceneDetection;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzer;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerFactory;
import com.huawei.hms.mlsdk.scd.MLSceneDetectionAnalyzerSetting;

import java.util.List;

public class SceneDetectionTransactor extends BaseTransactor<List<MLSceneDetection>> {

    private static final String TAG = SceneDetectionTransactor.class.getSimpleName();

    private MLSceneDetectionAnalyzer detector;

    private final Context mContext;

  /*
    private float confidence = 30.0f; // confidence for credibility of the scene detection scenario
  */

    public SceneDetectionTransactor(Context context, String confidence) {
        this.mContext = context;
        createCustomMLSceneDetectionAnalyzer(confidence);
    }


    /**
     * You can show MLSceneDetectionAnalyzer types
     *
     * @param confidenceMaxResult : if value isEmpty, analyzer will create by Method1, else analyzer will create by Method2 with setConfidence
     */
    public void createCustomMLSceneDetectionAnalyzer(String confidenceMaxResult) {
        Log.d(TAG, "createCustomMLSceneDetectionAnalyzer : confidenceMaxResult : " + confidenceMaxResult);
        if (confidenceMaxResult.isEmpty()) {
            // Method 1: Use default parameter settings.
            this.detector = MLSceneDetectionAnalyzerFactory.getInstance().getSceneDetectionAnalyzer();
        } else {
            // Method 2: Create a scene detection analyzer instance based on the customized configuration.
            MLSceneDetectionAnalyzerSetting setting = new MLSceneDetectionAnalyzerSetting.Factory().setConfidence(Float.parseFloat(confidenceMaxResult) / 100).create();
            this.detector = MLSceneDetectionAnalyzerFactory.getInstance().getSceneDetectionAnalyzer(setting);
        }
    }


    @Override
    public Task<List<MLSceneDetection>> detectInImage(MLFrame image) {
        Log.d(TAG, "detectInImage()");
        return detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLSceneDetection> sceneDetectionsResults, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        if (sceneDetectionsResults != null && !sceneDetectionsResults.isEmpty()) {
            graphicOverlay.clear();
            // For synchronous display, if surfaceTexture is used, the preview image needs to be drawn.
            // If surfaceView is used asynchronously, the preview image does not need to be drawn.
            // This step can be added or not based on the setting of synchronous or asynchronous display.
            if (originalCameraImage != null) {
                CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
                graphicOverlay.addGraphic(imageGraphic);
            }

            // Create a layer on the camera preview image and draw the result.
            SceneDetectionGraphic graphic = new SceneDetectionGraphic(graphicOverlay, mContext, sceneDetectionsResults);
            graphicOverlay.addGraphic(graphic);
            graphicOverlay.postInvalidate();
        }
    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "MLSceneDetection onFailure : " + e.getMessage(), e);
    }

    @Override
    public void stop() {
        super.stop();
        detector.stop();
    }

}
