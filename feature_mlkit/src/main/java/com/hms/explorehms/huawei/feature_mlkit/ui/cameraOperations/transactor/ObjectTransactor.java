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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ObjectGraphic;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.objects.MLObject;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzer;
import com.huawei.hms.mlsdk.objects.MLObjectAnalyzerSetting;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ObjectTransactor extends BaseTransactor<List<MLObject>> {

    private static final String TAG = ObjectTransactor.class.getSimpleName();

    private final MLObjectAnalyzer detector;

    public ObjectTransactor(MLObjectAnalyzerSetting options) {
        this.detector = MLAnalyzerFactory.getInstance().getLocalObjectAnalyzer(options);
    }

    @Override
    public void stop() {
        super.stop();
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close object transactor: " + e.getMessage());
        }
    }

    /**
     * detectInImage with MLFrame image object
     *
     * @param image MLFrame object
     * @return MLObjectAnalyzer.asyncAnalyseFrame(image)
     */
    @Override
    public Task<List<MLObject>> detectInImage(MLFrame image) {
        Log.e(TAG, "TEST : detectInImage");
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLObject> results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.e(TAG, "TEST : onSuccess : results : " + results.size());

        for (MLObject object : results) {
            ObjectGraphic objectGraphic = new ObjectGraphic(graphicOverlay, object);
            graphicOverlay.addGraphic(objectGraphic);
        }
        graphicOverlay.postInvalidate();

        /* !!!  check it out for List ot single MLObject !!!
            ObjectGraphic objectGraphic = new ObjectGraphic(graphicOverlay, results);
            graphicOverlay.addGraphic(objectGraphic);
            graphicOverlay.postInvalidate();
         */

    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Object detection failed: " + e.getMessage(), e);
    }
}
