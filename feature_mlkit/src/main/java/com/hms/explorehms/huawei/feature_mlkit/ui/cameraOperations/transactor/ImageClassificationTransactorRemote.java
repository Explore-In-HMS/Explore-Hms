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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ImageClassificationGraphicRemote;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.classification.MLImageClassification;
import com.huawei.hms.mlsdk.classification.MLImageClassificationAnalyzer;
import com.huawei.hms.mlsdk.classification.MLRemoteClassificationAnalyzerSetting;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageClassificationTransactorRemote extends BaseTransactor<List<MLImageClassification>> {
    private static final String TAG = "RemoteImgClassification";

    private final MLImageClassificationAnalyzer detector;

    private final Context mContext;

    public ImageClassificationTransactorRemote(Context context) {
        super();
        // You can use default or custom analyzer type and setting options with createCustomMLClassificationAnalyzer()
        this.detector = createCustomMLClassificationAnalyzer();
        this.mContext = context;
        Utils.setApiKeyForRemoteMLApplication(context);
    }

    /**
     * You can show MLImageClassificationAnalyzer types
     */
    public MLImageClassificationAnalyzer createCustomMLClassificationAnalyzer() {
        // Method 1: Use customized parameter settings for on-cloud recognition.
        MLRemoteClassificationAnalyzerSetting cloudSetting =
                new MLRemoteClassificationAnalyzerSetting.Factory()
                        .setMinAcceptablePossibility(0.5f)
                        .create();
        // Method 2: Use default parameter settings for on-cloud recognition.
        // Important method --> MLImageClassificationAnalyzer cloudAnalyzer = MLAnalyzerFactory.getInstance().getRemoteImageClassificationAnalyzer();
        return MLAnalyzerFactory.getInstance().getRemoteImageClassificationAnalyzer(cloudSetting);
    }

    @Override
    public Task<List<MLImageClassification>> detectInImage(MLFrame image) {
        return detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLImageClassification> classificationsResults, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        List<String> classificationList = new ArrayList<>();
        for (int i = 0; i < classificationsResults.size(); ++i) {
            MLImageClassification classification = classificationsResults.get(i);
            if (classification.getName() != null) {
                classificationList.add(classification.getName());
            }
        }
        ImageClassificationGraphicRemote remoteImageClassificationGraphic =
                new ImageClassificationGraphicRemote(graphicOverlay, this.mContext, classificationList);
        graphicOverlay.addGraphic(remoteImageClassificationGraphic);
        graphicOverlay.postInvalidate();

    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "Image classification remote onFailure : " + e.getMessage(), e);
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
