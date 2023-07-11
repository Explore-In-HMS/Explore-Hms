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

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ImageClassificationGraphicLocal;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.classification.MLImageClassification;
import com.huawei.hms.mlsdk.classification.MLImageClassificationAnalyzer;
import com.huawei.hms.mlsdk.classification.MLLocalClassificationAnalyzerSetting;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.io.IOException;
import java.util.List;

public class ImageClassificationTransactorLocal extends BaseTransactor<List<MLImageClassification>> {

    private static final String TAG = ImageClassificationTransactorLocal.class.getSimpleName();

    private final MLImageClassificationAnalyzer detector;

    private final Context mContext;

    public ImageClassificationTransactorLocal(Context context) {
        this.mContext = context;
        // You can use default or custom analyzer type and setting options with createCustomMLClassificationAnalyzer()
        this.detector = createCustomMLClassificationAnalyzer();
    }

    /**
     * You can show MLImageClassificationAnalyzer types
     */
    public MLImageClassificationAnalyzer createCustomMLClassificationAnalyzer() {
        // Method 1: Use customized parameter settings for on-device recognition.
        MLLocalClassificationAnalyzerSetting deviceSetting =
                new MLLocalClassificationAnalyzerSetting.Factory()
                        .setMinAcceptablePossibility(0.8f)
                        .create();
        // Method 2: Use default parameter settings for on-device recognition.
        // Important method --> MLImageClassificationAnalyzer deviceAnalyzer = MLAnalyzerFactory.getInstance().getLocalImageClassificationAnalyzer();
        return MLAnalyzerFactory.getInstance().getLocalImageClassificationAnalyzer(deviceSetting);
    }


    @Override
    public Task<List<MLImageClassification>> detectInImage(MLFrame image) {
        Log.d(TAG, "detectInImage()");
        return detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLImageClassification> classificationsResults, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        ImageClassificationGraphicLocal hmsMLImageClassificationGraphic =
                new ImageClassificationGraphicLocal(graphicOverlay, mContext, classificationsResults);
        graphicOverlay.addGraphic(hmsMLImageClassificationGraphic);
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "Image classification local onFailure : " + e.getMessage(), e);
    }


    @Override
    public void stop() {
        try {
            super.stop();
            detector.stop();
        } catch (IOException e) {
            Log.e(TAG, "detector.stop exception : " + e.getMessage(), e);
        }
    }

}
