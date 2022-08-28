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
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.util.SparseArray;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageSegmentationTransactor extends BaseTransactor<MLImageSegmentation> {

    private static final String TAG = ImageSegmentationTransactor.class.getSimpleName();

    private final MLImageSegmentationAnalyzer detector;
    private final Context context;
    private Bitmap foregroundBitmap;
    private Bitmap backgroundBitmap;
    private boolean isBlur = false;
    private RenderScript renderScript;
    private ImageSegmentationResultCallBack imageSegmentationResultCallBack;

    /**
     * Constructor for real-time replacement background.
     *
     * @param context          context.
     * @param options          MLImageSegmentationSetting.
     * @param backgroundBitmap background image.
     */
    public ImageSegmentationTransactor(Context context, MLImageSegmentationSetting options, Bitmap backgroundBitmap) {
        this.context = context;
        this.backgroundBitmap = backgroundBitmap;
        this.detector = createCustomMLImageSegmentationAnalyzer(options);
    }

    /**
     * You can show MLImageSegmentationAnalyzer types
     */
    public MLImageSegmentationAnalyzer createCustomMLImageSegmentationAnalyzer(MLImageSegmentationSetting options) {
        MLImageSegmentationAnalyzer analyzer;
        if (options == null) {
            /* Method 1: Use default parameter settings to configure the image segmentation analyzer.
             The default mode is human body segmentation in fine mode.
             All segmentation results of human body segmentation are returned
              (
               pixel-level label information, human body image with a transparent background,
               gray-scale image with a white human body and black background,
               and an original image for segmentation
              )
            */
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

    // Interface for obtaining processed image data.
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
    protected void onSuccess(@Nullable Bitmap originalCameraImage, @NonNull MLImageSegmentation results, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        if (results.getForeground() == null) {
            Log.e(TAG, "onSuccess but MLImageSegmentation results.getForeground is NULL. Detection failed!");
            return;
        }
        foregroundBitmap = results.foreground;

        // Replace background.
        Bitmap resultBitmap = changeNextBackground(foregroundBitmap);

        if (frameMetadata.getCameraFacing() == CameraConfiguration.CAMERA_FACING_FRONT) {
            resultBitmap = convert(resultBitmap);
        }
        if (imageSegmentationResultCallBack != null) {
            imageSegmentationResultCallBack.callResultBitmap(resultBitmap);
        }
        CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, resultBitmap);
        graphicOverlay.addGraphic(imageGraphic);
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


    /**
     * Replace the images in the assets directory as the background image in order.
     *
     * @param foregroundBitmap
     * @return Bitmap
     */
    private Bitmap changeNextBackground(Bitmap foregroundBitmap) {
        Bitmap result;
        if (backgroundBitmap == null) {
            Log.i(ImageSegmentationTransactor.TAG, "changeNextBackground No Background Image!");
            Utils.showToastMessage(context, "No Background Image!");
            throw new NullPointerException("No background image!");
        }

        if (!equalToForegroundImageSize()) {
            backgroundBitmap = resizeImageToForegroundImage(backgroundBitmap);
        }
        int[] pixels = new int[backgroundBitmap.getWidth() * backgroundBitmap.getHeight()];
        backgroundBitmap.getPixels(pixels, 0, backgroundBitmap.getWidth(), 0, 0,
                backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        result = BitmapUtils.joinBitmap(isBlur ? blur(backgroundBitmap, 20) : backgroundBitmap, foregroundBitmap);
        return result;
    }

    /**
     * Blur Bitmap
     *
     * @param bitmap Original Bitmap
     * @param radius Blur Radius (1-25)
     * @return Bitmap
     */
    private Bitmap blur(Bitmap bitmap, int radius) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap);
        Allocation in = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation out = Allocation.createTyped(renderScript, in.getType());
        ScriptIntrinsicBlur scriptintrinsicblur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptintrinsicblur.setRadius(radius);
        scriptintrinsicblur.setInput(in);
        scriptintrinsicblur.forEach(out);
        out.copyTo(outBitmap);
        return outBitmap;
    }

    /**
     * Stretch background image size to foreground image's.
     *
     * @param bitmap bitmap
     * @return Bitmap object
     */
    private Bitmap resizeImageToForegroundImage(Bitmap bitmap) {
        float scaleWidth = ((float) foregroundBitmap.getWidth() / bitmap.getWidth());
        float scaleHeigth = ((float) foregroundBitmap.getHeight() / bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeigth);

        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    private boolean equalToForegroundImageSize() {
        Log.i(ImageSegmentationTransactor.TAG, "equalToForegroundImageSize : FOREGREOUND SIZE;" + foregroundBitmap.getWidth() + ", height:" + foregroundBitmap.getHeight());
        return backgroundBitmap.getHeight() == foregroundBitmap.getHeight() && backgroundBitmap.getWidth() == foregroundBitmap.getWidth();
    }


    /**
     * Convert bitmap when Front camera image
     *
     * @param bitmap
     * @return Bitmap
     */
    private Bitmap convert(Bitmap bitmap) {
        Matrix m = new Matrix();
        m.setScale(-1, 1);// horizontal flip.
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    public void setBlur(Boolean blur) {
        isBlur = blur;
    }

    public void setRenderScript(RenderScript renderScript) {
        this.renderScript = renderScript;
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

