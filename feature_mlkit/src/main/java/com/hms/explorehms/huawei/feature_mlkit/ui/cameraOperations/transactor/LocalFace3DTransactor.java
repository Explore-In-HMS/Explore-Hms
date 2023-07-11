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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.face3d.ML3DFace;
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzer;
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzerSetting;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.Local3DFaceGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;

import java.io.IOException;
import java.util.List;

public class LocalFace3DTransactor extends BaseTransactor<List<ML3DFace>> {
    private static final String TAG = "LocalFaceTransactor";

    private final ML3DFaceAnalyzer detector;
    private Context mContext;


    public LocalFace3DTransactor(ML3DFaceAnalyzerSetting setting, Context context) {
        super(context);
        this.detector = MLAnalyzerFactory.getInstance().get3DFaceAnalyzer(setting);
        this.mContext = context;
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close face transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<List<ML3DFace>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<ML3DFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d("toby", "Total HMSFaceProc graphicOverlay start");
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        Log.d("toby", "Total HMSFaceProc hmsMLLocalFaceGraphic start");
        Local3DFaceGraphic hmsML3DLocalFaceGraphic = new Local3DFaceGraphic(graphicOverlay, faces, mContext);
        graphicOverlay.addGraphic(hmsML3DLocalFaceGraphic);
        graphicOverlay.postInvalidate();
        Log.d("toby", "Total HMSFaceProc graphicOverlay end");
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.d("toby", "Total HMSFaceProc graphicOverlay onFailure");
        Log.e(TAG, "Face detection failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }
}
