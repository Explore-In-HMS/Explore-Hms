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
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.ImageUtils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationClassification;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StillImageSegmentationTransactor extends BaseTransactor<MLImageSegmentation> {

    private static final String TAG = StillImageSegmentationTransactor.class.getSimpleName();

    private final MLImageSegmentationAnalyzer detector;
    private final Bitmap originBitmap;
    private Bitmap backgroundBitmap;
    private final ImageView imageView;
    private final int detectCategory;
    private int color;
    private ImageSegmentationResultCallBack imageSegmentationResultCallBack;

    /**
     * @param options        Options.
     * @param originBitmap   Foreground, picture to replace.
     * @param imageView      ImageView.
     * @param detectCategory -1 represents all detections, others represent the type of replacement color currently detected.
     */
    public StillImageSegmentationTransactor(MLImageSegmentationSetting options, Bitmap originBitmap, ImageView imageView, int detectCategory) {
        this.originBitmap = originBitmap;
        this.backgroundBitmap = null;
        this.imageView = imageView;
        this.detectCategory = detectCategory;
        this.color = Color.WHITE;
        this.detector = createCustomMLImageSegmentationAnalyzer(options);
    }

    /**
     * Replace background.
     *
     * @param options          Options.
     * @param originBitmap     Foreground, picture to replace.
     * @param backgroundBitmap Background.
     * @param imageView        ImageView.
     * @param detectCategory   -1 represents all detections, others represent the type of replacement color currently detected.
     */
    public StillImageSegmentationTransactor(MLImageSegmentationSetting options, Bitmap originBitmap, Bitmap backgroundBitmap, ImageView imageView, int detectCategory) {
        this.originBitmap = originBitmap;
        this.backgroundBitmap = backgroundBitmap;
        this.imageView = imageView;
        this.detectCategory = detectCategory;
        this.color = Color.WHITE;
        this.detector = createCustomMLImageSegmentationAnalyzer(options);
    }


    /**
     * You can show MLImageSegmentationAnalyzer types
     */
    public MLImageSegmentationAnalyzer createCustomMLImageSegmentationAnalyzer(MLImageSegmentationSetting options) {
        MLImageSegmentationAnalyzer analyzer;
        if (options == null) {
            // Method 1: Use default parameter settings to configure the image segmentation analyzer.
            // The default mode is human body segmentation in fine mode.
            // All segmentation results of human body segmentation are returned
            //  (
            //   pixel-level label information, human body image with a transparent background,
            //   gray-scale image with a white human body and black background,
            //   and an original image for segmentation
            //  )
            analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer();
        } else {
            /* MLImageSegmentationSetting Descriptions
             Method 2: Use MLImageSegmentationSetting to customize the image segmentation analyzer.

            MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory()
                    // Set whether to support fine segmentation. The value true indicates fine segmentation, and the value false indicates fast segmentation.
                    .setExact(false)
                    // Set the human body segmentation mode.
                    .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                   .setAnalyzerType(MLImageSegmentationSetting.IMAGE_SEG)
                    // Set returned result types.
                    // MLImageSegmentationScene.ALL: All segmentation results are returned (pixel-level label information, human body image with a transparent background, gray-scale image with a white human body and black background, and an original image for segmentation).
                   // MLImageSegmentationScene.MASK_ONLY: Only pixel-level label information and an original image for segmentation are returned.
                    // MLImageSegmentationScene.FOREGROUND_ONLY: A human body image with a transparent background and an original image for segmentation are returned.
                    // MLImageSegmentationScene.GRAYSCALE_ONLY: A gray-scale image with a white human body and black background and an original image for segmentation are returned.
                    .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)
                    .create();
             */
            analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(options);
        }
        return analyzer;
    }


    /**
     * Sets the drawn color of detected features.
     *
     * @param color : int
     */
    public void setColor(int color) {
        this.color = color;
    }

    // Return to processed image.
    public void setImageSegmentationResultCallBack(ImageSegmentationResultCallBack callBackFromActivity) {
        imageSegmentationResultCallBack = callBackFromActivity;
    }


    @Override
    protected Task<MLImageSegmentation> detectInImage(MLFrame frame) {
        return detector.asyncAnalyseFrame(frame);
    }

    protected SparseArray<MLImageSegmentation> analyseFrame(MLFrame frame) {
        return detector.analyseFrame(frame);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull MLImageSegmentation results,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        int[] pixels;
        if (results.getMasks() == null) {
            Log.i(TAG, "detection failed, none mask return");
            return;
        }
        // If the originBitmap is automatically recycled, the callback is complete.
        if (originBitmap.isRecycled()) {
            return;
        }
        if (detectCategory == -1) {
            pixels = byteArrToIntArr(results.getMasks());
        } else if (backgroundBitmap == null) {
            pixels = changeColor(results.getMasks());
        } else {
            // If the backgroundBitmap is automatically recycled, the callback is complete.
            if (backgroundBitmap.isRecycled()) {
                return;
            }
            pixels = changeBackground(results.getMasks());
        }
        Bitmap processedBitmap = Bitmap.createBitmap(pixels, 0, originBitmap.getWidth(), originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(processedBitmap);
        if (imageSegmentationResultCallBack != null) {
            imageSegmentationResultCallBack.callResultBitmap(processedBitmap);
        }
        graphicOverlay.postInvalidate();
    }


    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "onFailure : " + e.getMessage(), e);
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

    private int[] byteArrToIntArr(byte[] masks) {
        int[] results = new int[masks.length];
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] == MLImageSegmentationClassification.TYPE_HUMAN) {
                results[i] = Color.BLACK;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_SKY) {
                results[i] = Color.BLUE;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_GRASS) {
                results[i] = Color.DKGRAY;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_FOOD) {
                results[i] = Color.YELLOW;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_CAT) {
                results[i] = Color.LTGRAY;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_BUILD) {
                results[i] = Color.CYAN;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_FLOWER) {
                results[i] = Color.RED;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_WATER) {
                results[i] = Color.GRAY;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_SAND) {
                results[i] = Color.MAGENTA;
            } else if (masks[i] == MLImageSegmentationClassification.TYPE_MOUNTAIN) {
                results[i] = Color.GREEN;
            } else {
                results[i] = Color.WHITE;
            }
        }
        return results;
    }

    /**
     * Cut out the desired element, the background is white.
     *
     * @param masks : byte[]
     * @return int[]
     */
    private int[] changeColor(byte[] masks) {
        int[] results = new int[masks.length];
        int[] originPixels = new int[originBitmap.getWidth() * originBitmap.getHeight()];
        originBitmap.getPixels(originPixels, 0, originBitmap.getWidth(), 0, 0, originBitmap.getWidth(), originBitmap.getHeight());
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] == detectCategory) {
                results[i] = color;
            } else {
                results[i] = originPixels[i];
            }
        }
        return results;
    }

    /**
     * Replace background image.
     *
     * @param masks : byte[]
     * @return int[]
     */
    private int[] changeBackground(byte[] masks) {
        // Make the background and foreground images the same size.
        if (backgroundBitmap != null && !ImageUtils.equalImageSize(originBitmap, backgroundBitmap)) {
            backgroundBitmap = ImageUtils.resizeImageToForegroundImage(originBitmap, backgroundBitmap);
        }
        int[] results = new int[masks.length];
        int[] originPixels = new int[originBitmap.getWidth() * originBitmap.getHeight()];
        int[] backgroundPixels = new int[originPixels.length];
        originBitmap.getPixels(originPixels, 0, originBitmap.getWidth(), 0, 0, originBitmap.getWidth(), originBitmap.getHeight());
        if (null != backgroundBitmap) {
            backgroundBitmap.getPixels(backgroundPixels, 0, originBitmap.getWidth(), 0, 0, originBitmap.getWidth(), originBitmap.getHeight());
        }
        for (int i = 0; i < masks.length; i++) {
            if (masks[i] == detectCategory) {
                results[i] = backgroundPixels[i];
            } else {
                results[i] = originPixels[i];
            }
        }
        return results;
    }

    /**
     * Image segmentation result callback
     */
    public static interface ImageSegmentationResultCallBack {
        /**
         * Save bitmap
         *
         * @param bitmap bitmap
         */
        void callResultBitmap(Bitmap bitmap);
    }

}