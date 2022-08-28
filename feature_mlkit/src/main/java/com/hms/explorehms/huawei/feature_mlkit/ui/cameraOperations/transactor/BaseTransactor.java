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
import android.graphics.ImageFormat;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.CameraConfiguration;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.NV21ToBitmapConverter;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.nio.ByteBuffer;

public abstract class BaseTransactor<T> implements ImageTransactor {

    private static final String TAG = BaseTransactor.class.getSimpleName();
    // To keep the latest images and its metadata.
    private ByteBuffer latestImage;

    private FrameMetadata latestImageMetaData;

    // To keep the images and metadata in process.
    private ByteBuffer transactingImage;

    private FrameMetadata transactingMetaData;

    private NV21ToBitmapConverter converter = null;

    protected BaseTransactor() {
    }

    protected BaseTransactor(Context context) {
        this.converter = new NV21ToBitmapConverter(context);
    }

    /**
     * start processLatestImage with ByteBuffer,GraphicOverlay and FrameMetadata objects
     *
     * @param data           ByteBuffer object
     * @param frameMetadata  FrameMetadata object
     * @param graphicOverlay GraphicOverlay object
     */
    @Override
    public synchronized void process(ByteBuffer data, final FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        this.latestImage = data;
        this.latestImageMetaData = frameMetadata;
        if (this.transactingImage == null && this.transactingMetaData == null) {
            this.processLatestImage(graphicOverlay);
        }
    }

    @Override
    public void process(Bitmap bitmap, GraphicOverlay graphicOverlay) {
        MLFrame frame = new MLFrame.Creator().setBitmap(bitmap).create();
        this.detectInVisionImage(bitmap, frame, null, graphicOverlay);
    }

    /**
     * start process and detectInVisionImage with graphicOverlay
     *
     * @param graphicOverlay
     */
    private synchronized void processLatestImage(GraphicOverlay graphicOverlay) {
        this.transactingImage = this.latestImage;
        this.transactingMetaData = this.latestImageMetaData;
        this.latestImage = null;
        this.latestImageMetaData = null;
        Bitmap bitmap = null;
        if (this.transactingImage != null && this.transactingMetaData != null) {
            int width;
            int height;
            width = this.transactingMetaData.getWidth();
            height = this.transactingMetaData.getHeight();
            MLFrame.Property metadata = new MLFrame.Property.Creator().setFormatType(ImageFormat.NV21)
                    .setWidth(width)
                    .setHeight(height)
                    .setQuadrant(this.transactingMetaData.getRotation())
                    .create();

            if (this.isFaceDetection()) {
                Log.d(TAG, "Total HMSFaceProc getBitmap start");
                bitmap = this.converter.getBitmap(this.transactingImage, this.transactingMetaData);
                Log.d(TAG, "Total HMSFaceProc getBitmap end");
                Bitmap resizeBitmap = BitmapUtils.scaleBitmap(bitmap, CameraConfiguration.DEFAULT_HEIGHT,
                        CameraConfiguration.DEFAULT_WIDTH);
                Log.d(TAG, "Total HMSFaceProc resizeBitmap end");
                this.detectInVisionImage(bitmap, MLFrame.fromBitmap(resizeBitmap), this.transactingMetaData,
                        graphicOverlay);
            } else {
                bitmap = BitmapUtils.getBitmap(this.transactingImage, this.transactingMetaData);
                this.detectInVisionImage(bitmap, MLFrame.fromByteBuffer(this.transactingImage, metadata),
                        this.transactingMetaData, graphicOverlay);
            }
        }
    }

    /**
     * detectInVisionImage with BaseTransactor process
     *
     * @param bitmap
     * @param image
     * @param metadata
     * @param graphicOverlay
     */
    private void detectInVisionImage(final Bitmap bitmap, MLFrame image, final FrameMetadata metadata,
                                     final GraphicOverlay graphicOverlay) {
        this.detectInImage(image).addOnSuccessListener(results -> {
            if (metadata == null || metadata.getCameraFacing() == CameraConfiguration.getCameraFacing()) {
                BaseTransactor.this.onSuccess(bitmap, results, metadata, graphicOverlay);
            }
            BaseTransactor.this.processLatestImage(graphicOverlay);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "detectInVisionImage onFailure : " + e.getMessage(), e);
            BaseTransactor.this.onFailure(e);
        });
    }

    @Override
    public void stop() {
    }

    /**
     * Detect image with MLFrame object
     *
     * @param image MLFrame object
     * @return Task object
     */
    protected abstract Task<T> detectInImage(MLFrame image);

    /**
     * Callback that executes with a successful detection result.
     *
     * @param originalCameraImage hold the original image from camera, used to draw the background image.
     * @param results             T object
     * @param frameMetadata       FrameMetadata object
     * @param graphicOverlay      GraphicOverlay object
     */
    protected abstract void onSuccess(Bitmap originalCameraImage, T results, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);

    /**
     * Callback that executes with failure detection result.
     *
     * @param exception Exception object
     */
    protected abstract void onFailure(Exception exception);

    @Override
    public boolean isFaceDetection() {
        return false;
    }
}
