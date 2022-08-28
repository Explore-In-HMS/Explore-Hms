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
import android.os.Handler;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.SkeletonGraphic;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.skeleton.MLSkeleton;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzer;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerFactory;
import com.huawei.hms.mlsdk.skeleton.MLSkeletonAnalyzerSetting;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SkeletonTransactor extends BaseTransactor<List<MLSkeleton>> {

    private static final String TAG = SkeletonTransactor.class.getSimpleName();

    private final MLSkeletonAnalyzer detector;
    private int zeroCount = 0;
    private Handler mHandler;

    public SkeletonTransactor(MLSkeletonAnalyzerSetting setting, Context context) {
        super(context);
        this.detector = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
    }

    public SkeletonTransactor(Context context, Handler handler) {
        super(context);
        this.mHandler = handler;
        MLSkeletonAnalyzerSetting setting = new MLSkeletonAnalyzerSetting.Factory()
                .create();
        detector = MLSkeletonAnalyzerFactory.getInstance().getSkeletonAnalyzer(setting);
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close skeleton transactor: " + e.getMessage());
        }
    }

    /**
     * detectInImage with MLFrame image object
     *
     * @param image MLFrame object
     * @return MLSkeletonAnalyzer.asyncAnalyseFrame(image)
     */
    @Override
    public Task<List<MLSkeleton>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLSkeleton> MLSkeletons,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d("toby", "Total MLSkeletons graphicOverlay start");
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }

        if (MLSkeletons == null || MLSkeletons.isEmpty()) {
            return;
        }
        Log.d("toby", "Total MLSkeletons hmsMLLocalFaceGraphic start");
        SkeletonGraphic hmsMLLocalSkeletonGraphic = new SkeletonGraphic(graphicOverlay, MLSkeletons);
        graphicOverlay.addGraphic(hmsMLLocalSkeletonGraphic);
        graphicOverlay.postInvalidate();
        Log.d("toby", "Total MLSkeletons graphicOverlay end");

        /* check it out
        if (!HumanSkeletonActivity.isOpenStatus()) {
           return;
        }
        */

        if (mHandler == null) {
            return;
        }
        compareSimilarity(MLSkeletons);
    }

    private void compareSimilarity(List<MLSkeleton> skeletons) {
        float similarity = 0f;

        float result = detector.caluteSimilarity(skeletons, skeletons);
        if (result > similarity) {
            similarity = result;
        }
        Log.d(TAG, "similarity : " + similarity);

        // Filters out the second similarity record if the similarity between two consecutive 0s is not displayed.
        if (similarity == 0) {
            zeroCount++;
            if (zeroCount == 1) {
                return;
            } else {
                zeroCount = 0;
            }
        } else {
            zeroCount = 0;
        }

    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Skeleton detection onFailure : " + e.getMessage(), e);
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }

}
