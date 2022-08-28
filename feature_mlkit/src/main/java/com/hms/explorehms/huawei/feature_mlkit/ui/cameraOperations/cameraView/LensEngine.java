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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ImageTransactor;
import com.huawei.hms.common.size.Size;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.Map;

import androidx.annotation.RequiresPermission;

/**
 * Manages the camera and allows UI updates on top of it (e.g. overlaying extra Graphics or
 * displaying extra information). This receives preview frames from the camera at a specified rate,
 * sending those frames to child classes' detectors / classifiers as fast as it is able to process.
 *
 * @since 2019-12-26
 */
@SuppressLint("MissingPermission")
public class LensEngine {
    private static final String TAG = "LensEngine";
    protected Activity activity;
    private static Camera camera;
    private static Thread transactingThread;
    private static FrameTransactingRunnable transactingRunnable;
    private static final Object transactorLock = new Object();
    private static ImageTransactor frameTransactor;
    private static CameraSelector selector;
    private static final Map<byte[], ByteBuffer> bytesToByteBuffer = new IdentityHashMap<>();
    private static GraphicOverlay overlay;


    public LensEngine(Activity activity, CameraConfiguration configuration, GraphicOverlay graphicOverlay) {
        this.activity = activity;
        transactingRunnable = new FrameTransactingRunnable();
        selector = new CameraSelector(activity, configuration);
        overlay = graphicOverlay;
        overlay.clear();
    }

    /**
     * Stop the camera and release the resources of the camera and analyzer.
     */
    public void release() {
        synchronized (transactorLock) {
            stop();
            transactingRunnable.release();
            if (frameTransactor != null) {
                frameTransactor.stop();
                frameTransactor = null;
            }
        }
    }

    /**
     * Turn on the camera and start sending preview frames to the analyzer for detection.
     *
     * @throws IOException IO Exception
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.CAMERA)
    public synchronized LensEngine run() throws IOException {
        if (camera != null) {
            return this;
        }
        camera = createCamera();
        camera.startPreview();
        initializeOverlay();
        transactingThread = new Thread(transactingRunnable);
        transactingRunnable.setActive(true);
        transactingThread.start();
        return this;
    }

    /**
     * Take pictures.
     *
     * @param pictureCallback Callback function after obtaining photo data.
     */
    public synchronized void takePicture(Camera.PictureCallback pictureCallback) {
        synchronized (transactorLock) {
            if (camera != null) {
                camera.takePicture(null, null, null, pictureCallback);
            }
        }
    }

    public synchronized Camera getCamera() {
        return camera;
    }

    private void initializeOverlay() {
        if (overlay != null) {
            int min;
            int max;
            if (frameTransactor.isFaceDetection()) {
                min = CameraConfiguration.DEFAULT_HEIGHT;
                max = CameraConfiguration.DEFAULT_WIDTH;
                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    overlay.setCameraInfo(min, max, 0);
                } else {
                    overlay.setCameraInfo(max, min, 0);
                }
            } else {
                Size size = getPreviewSize();
                min = Math.min(size.getWidth(), size.getHeight());
                max = Math.max(size.getWidth(), size.getHeight());
                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    overlay.setCameraInfo(min, max, getFacing());
                } else {
                    overlay.setCameraInfo(max, min, getFacing());
                }
            }

            overlay.clear();
        }
    }

    /**
     * Get camera preview size.
     *
     * @return Size Size of camera preview.
     */
    public Size getPreviewSize() {
        return selector.getPreviewSize();
    }

    /**
     * @return get cameraSelector Facing : front or back camera facing
     */
    public int getFacing() {
        return selector.getFacing();
    }

    /**
     * Turn off the camera and stop transmitting frames to the analyzer.
     */
    public synchronized void stop() {
        transactingRunnable.setActive(false);
        if (transactingThread != null) {
            try {
                transactingThread.join();
            } catch (InterruptedException e) {
                Log.d(TAG, "Frame transacting thread interrupted on release.");
                transactingThread.interrupt();
            }
            transactingThread = null;
        }
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallbackWithBuffer(null);
            try {
                camera.setPreviewDisplay(null);
                camera.setPreviewTexture(null);
            } catch (Exception e) {
                Log.e(TAG, "Failed to clear camera preview: " + e);
            }
            camera.release();
            camera = null;
        }
        bytesToByteBuffer.clear();
    }

    @SuppressLint("InlinedApi")
    private Camera createCamera() throws IOException {
        Camera newCamera = selector.createCamera();
        newCamera.setPreviewCallbackWithBuffer(new CameraPreviewCallback());
        newCamera.addCallbackBuffer(createPreviewBuffer(selector.getPreviewSize()));
        newCamera.addCallbackBuffer(createPreviewBuffer(selector.getPreviewSize()));
        newCamera.addCallbackBuffer(createPreviewBuffer(selector.getPreviewSize()));
        newCamera.addCallbackBuffer(createPreviewBuffer(selector.getPreviewSize()));
        return newCamera;
    }

    /**
     * Create a buffer for the camera preview callback. The size of the buffer is based on the camera preview size and the camera image format.
     *
     * @param previewSize Preview size
     * @return Image data from the camera
     */
    @SuppressLint("InlinedApi")
    private byte[] createPreviewBuffer(Size previewSize) {
        int bitsPerPixel = ImageFormat.getBitsPerPixel(ImageFormat.NV21);
        long sizeInBits = (long) previewSize.getHeight() * previewSize.getWidth() * bitsPerPixel;
        int bufferSize = (int) Math.ceil(sizeInBits / 8.0d) + 1;

        byte[] byteArray = new byte[bufferSize];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        if (!buffer.hasArray() || (buffer.array() != byteArray)) {
            throw new IllegalStateException("Failed to create valid buffer for lensEngine.");
        }
        bytesToByteBuffer.put(byteArray, buffer);
        return byteArray;
    }

    private static class CameraPreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            LensEngine.transactingRunnable.setNextFrame(data, camera);
        }
    }

    public void setMachineLearningFrameTransactor(ImageTransactor transactor) {
        synchronized (transactorLock) {
            if (frameTransactor != null) {
                frameTransactor.stop();
            }
            frameTransactor = transactor;
        }
    }

    /**
     * It is used to receive the frame captured by the camera and pass it to the analyzer.
     */
    private static class FrameTransactingRunnable implements Runnable {
        private final Object lock = new Object();
        private boolean active = true;
        private ByteBuffer pendingFrameData;

        FrameTransactingRunnable() {
        }

        /**
         * Frees the transactor and can safely perform this operation only after the associated thread has completed.
         */
        @SuppressLint("Assert")
        void release() {
            synchronized (lock) {
                if(transactingThread != null){
                    assert (transactingThread.getState() == Thread.State.TERMINATED);
                }
            }
        }

        void setActive(boolean active) {
            synchronized (lock) {
                this.active = active;
                lock.notifyAll();
            }
        }

        /**
         * Sets the frame data received from the camera. Adds a previously unused frame buffer (if exit) back to the camera.
         */
        void setNextFrame(byte[] data, Camera camera) {
            synchronized (lock) {
                if (pendingFrameData != null) {
                    camera.addCallbackBuffer(pendingFrameData.array());
                    pendingFrameData = null;
                }
                if (!LensEngine.bytesToByteBuffer.containsKey(data)) {
                    Log.d(TAG, "Skipping frame. Could not find ByteBuffer associated with the image "
                            + "data from the camera.");
                    return;
                }
                pendingFrameData = LensEngine.bytesToByteBuffer.get(data);
                lock.notifyAll();
            }
        }

        @SuppressLint("InlinedApi")
        @SuppressWarnings("GuardedBy")
        @Override
        public void run() {
            ByteBuffer data;

            while (true) {
                synchronized (lock) {
                    while (active && (pendingFrameData == null)) {
                        try {
                            // Waiting for next frame.
                            lock.wait();
                        } catch (InterruptedException e) {
                            Log.w(TAG, "Frame transacting loop terminated.", e);
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (!active) {
                        pendingFrameData = null;
                        return;
                    }
                    data = pendingFrameData;
                    pendingFrameData = null;
                }
                try {
                    synchronized (LensEngine.transactorLock) {
                        Log.d(TAG, "Process an image");
                        LensEngine.frameTransactor.process(
                                data,
                                new FrameMetadata.Builder()
                                        .setWidth(LensEngine.selector.getPreviewSize().getWidth())
                                        .setHeight(LensEngine.selector.getPreviewSize().getHeight())
                                        .setRotation(LensEngine.selector.getRotation())
                                        .setCameraFacing(LensEngine.selector.getFacing())
                                        .build(),
                                LensEngine.overlay
                        );
                    }
                } catch (Exception t) {
                    Log.e(TAG, "Exception thrown from receiver.", t);
                } finally {
                    LensEngine.camera.addCallbackBuffer(data.array());
                }
            }
        }
    }
}
