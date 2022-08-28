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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.cameraOperations;

import android.util.Log;
import android.util.SparseArray;

import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.face.MLFace;

public class FaceAnalyzerTransactor implements MLAnalyzer.MLTransactor<MLFace> {

    private static final String TAG = FaceAnalyzerTransactor.class.getSimpleName();

    private final GraphicOverlay mGraphicOverlay;

    public FaceAnalyzerTransactor(GraphicOverlay ocrGraphicOverlay) {
        this.mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    public void transactResult(MLAnalyzer.Result<MLFace> result) {
        this.mGraphicOverlay.clear();
        SparseArray<MLFace> faceSparseArray = result.getAnalyseList();
        Log.d(TAG, "transactResult : " + faceSparseArray.toString());
        for (int i = 0; i < faceSparseArray.size(); i++) {
            // step 4: add on-device face graphic
            MLFaceGraphic graphic = new MLFaceGraphic(this.mGraphicOverlay, faceSparseArray.valueAt(i));
            this.mGraphicOverlay.add(graphic);
            // finish
        }
    }

    @Override
    public void destroy() {
        this.mGraphicOverlay.clear();
    }

}