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
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.FaceGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FaceTransactor extends BaseTransactor<List<MLFace>> {

    private static final String TAG = FaceTransactor.class.getSimpleName();

    private final MLFaceAnalyzer detector;
    private boolean isOpenFeatures;
    private Context mContext;

    public FaceTransactor(MLFaceAnalyzerSetting options, Context context, boolean isOpenFeatures) {
        super(context);
        this.detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
        this.isOpenFeatures = isOpenFeatures;
        this.mContext = context;
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(FaceTransactor.TAG, "Exception thrown while trying to close face transactor: " + e.getMessage());
        }
    }

    /**
     * detectInImage with MLFrame image object
     *
     * @param image MLFrame object
     * @return MLFaceAnalyzer.asyncAnalyseFrame(image)
     */
    @Override
    protected Task<List<MLFace>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d("toby", "Total HMSFaceProc graphicOverlay start");
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        Log.d("toby", "Total HMSFaceProc hmsMLLocalFaceGraphic start");
        FaceGraphic hmsMLLocalFaceGraphic = new FaceGraphic(graphicOverlay, faces, mContext, this.isOpenFeatures);
        graphicOverlay.addGraphic(hmsMLLocalFaceGraphic);
        graphicOverlay.postInvalidate();
        Log.d("toby", "Total HMSFaceProc graphicOverlay end");
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "onFailure : Face detection failed: " + e.getMessage(), e);
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }

}
