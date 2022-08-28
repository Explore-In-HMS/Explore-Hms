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

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.BaseGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.TextGraphic;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextTransactor extends BaseTransactor<MLText> {
    private static final String TAG = "TextRecProc";
    private final MLTextAnalyzer detector;

    int mCount = 0;

    public TextTransactor() {
        String language = "en"; //  edit this default language English
        Log.d(TextTransactor.TAG, "language:" + language);
        MLLocalTextSetting options = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_TRACKING_MODE)
                .setLanguage(language)
                .create();
        this.detector = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(options);
    }

    @Override
    public void stop() {
        try {
            this.detector.close();
        } catch (IOException e) {
            Log.e(TextTransactor.TAG,
                    "Exception thrown while trying to close text transactor: " + e.getMessage());
        }
    }

    /**
     * detectInImage with MLFrame image object
     *
     * @param image MLFrame object
     * @return MLTextAnalyzer.asyncAnalyseFrame(image)
     */
    @Override
    protected Task<MLText> detectInImage(MLFrame image) {
        ByteBuffer latestImage = image.getByteBuffer();
        return this.detector.asyncAnalyseFrame(image);
    }


    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull MLText results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        List<MLText.Block> blocks = results.getBlocks();
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) && originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        if (blocks.size() > 0) {
            this.mCount = 0;
        } else {
            this.mCount++;
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<MLText.TextLine> lines = blocks.get(i).getContents();
            for (int j = 0; j < lines.size(); j++) {
                // Display by line, without displaying empty lines.
                if (lines.get(j).getStringValue() != null && lines.get(j).getStringValue().trim().length() != 0) {
                    BaseGraphic textGraphic = new TextGraphic(graphicOverlay,
                            lines.get(j));
                    graphicOverlay.addGraphic(textGraphic);
                }
            }
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Text detection onFailure : " + e.getMessage(), e);
    }
}
