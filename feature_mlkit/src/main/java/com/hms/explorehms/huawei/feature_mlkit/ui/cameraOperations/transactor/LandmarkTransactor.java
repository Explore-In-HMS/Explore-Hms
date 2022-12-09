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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.LandmarkGraphic;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer;

import java.util.List;

public class LandmarkTransactor extends BaseTransactor<List<MLRemoteLandmark>> {

    private static final String TAG = LandmarkTransactor.class.getSimpleName();

    private final MLRemoteLandmarkAnalyzer detector;

    public LandmarkTransactor(Context context) {
        super();
        Utils.setApiKeyForRemoteMLApplication(context);
        this.detector = createCustomAnalyzer();
    }

    /**
     * You can createCustom MLRemoteLandmarkAnalyzer types
     */
    public MLRemoteLandmarkAnalyzer createCustomAnalyzer() {
        // Method 1: Use default parameter settings.
        return MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer();
        // Method 2: Use customized parameter settings.
        /*
          setLargestNumOfReturns: maximum number of recognition results.
          setPatternType: analyzer mode.
          MLRemoteLandmarkAnalyzerSetting.STEADY_PATTERN: The value 1 indicates the stable mode.
          MLRemoteLandmarkAnalyzerSetting.NEWEST_PATTERN: The value 2 indicates the latest mode.

          MLRemoteLandmarkAnalyzerSetting settings = new MLRemoteLandmarkAnalyzerSetting.Factory()
                .setLargestNumOfReturns(2)
                .setPatternType(MLRemoteLandmarkAnalyzerSetting.STEADY_PATTERN)
                .create();
         */
        //MLRemoteLandmarkAnalyzer analyzer = MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer(settings);
    }


    @Override
    public Task<List<MLRemoteLandmark>> detectInImage(MLFrame image) {
        return detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLRemoteLandmark> landmarksResults, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        if (landmarksResults != null && !landmarksResults.isEmpty()) {
            graphicOverlay.clear();
            for (MLRemoteLandmark landmark : landmarksResults) {
                LandmarkGraphic landmarkGraphic = new LandmarkGraphic(graphicOverlay, landmark);
                graphicOverlay.addGraphic(landmarkGraphic);
            }
            graphicOverlay.postInvalidate();
        }

    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "Landmark Recognition onFailure : " + e.getMessage(), e);
    }
}
