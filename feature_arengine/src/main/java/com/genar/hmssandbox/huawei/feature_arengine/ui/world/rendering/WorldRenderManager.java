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
package com.genar.hmssandbox.huawei.feature_arengine.ui.world.rendering;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.genar.hmssandbox.huawei.feature_arengine.R;
import com.genar.hmssandbox.huawei.feature_arengine.common.AREngineRuntimeException;
import com.genar.hmssandbox.huawei.feature_arengine.common.DisplayRotationManager;
import com.genar.hmssandbox.huawei.feature_arengine.common.TextDisplay;
import com.genar.hmssandbox.huawei.feature_arengine.common.TextureDisplay;
import com.genar.hmssandbox.huawei.feature_arengine.ui.world.GestureEvent;
import com.genar.hmssandbox.huawei.feature_arengine.ui.world.VirtualObject;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hiar.ARCamera;
import com.huawei.hiar.ARConfigBase;
import com.huawei.hiar.ARFrame;
import com.huawei.hiar.ARHitResult;
import com.huawei.hiar.ARLightEstimate;
import com.huawei.hiar.ARPlane;
import com.huawei.hiar.ARPoint;
import com.huawei.hiar.ARPose;
import com.huawei.hiar.ARSession;
import com.huawei.hiar.ARTrackable;
import com.huawei.hiar.ARWorldTrackingConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WorldRenderManager implements GLSurfaceView.Renderer {

    private static final String TAG = WorldRenderManager.class.getSimpleName();

    private static final int PROJ_MATRIX_OFFSET = 0;

    private static final float PROJ_MATRIX_NEAR = 0.1f;

    private static final float PROJ_MATRIX_FAR = 100.0f;

    private static final float MATRIX_SCALE_SX = -1.0f;

    private static final float MATRIX_SCALE_SY = -1.0f;

    private static final float[] BLUE_COLORS = new float[] {66.0f, 133.0f, 244.0f, 255.0f};

    private static final float[] GREEN_COLORS = new float[] {66.0f, 133.0f, 244.0f, 255.0f};

    private ARSession mSession;

    private ARWorldTrackingConfig mArWorldTrackingConfig;

    private final Activity mActivity;

    private final Context mContext;

    private final MaterialTextView mTextView;

    private MaterialTextView mSearchingTextView;

    private int frames = 0;

    private long lastInterval;

    private float fps;

    private TextureDisplay mTextureDisplay = new TextureDisplay();

    private TextDisplay mTextDisplay = new TextDisplay();

    private LabelDisplay mLabelDisplay = new LabelDisplay();

    private ObjectDisplay mObjectDisplay = new ObjectDisplay();

    private DisplayRotationManager mDisplayRotationManager;

    private ArrayBlockingQueue<GestureEvent> mQueuedSingleTaps;

    private VirtualObject mSelectedObj = null;

    private ArrayList<VirtualObject> mVirtualObjects = new ArrayList<>();

    private boolean mHaveSetEnvTextureData = false;

    /**
     * The constructor passes context and activity. This method will be called onCreate
     *
     * @param activity Activity
     * @param context Context
     */
    public WorldRenderManager(Activity activity, Context context) {
        mActivity = activity;
        mContext = context;
        mTextView = activity.findViewById(R.id.tv_world_track_arengine);
        mSearchingTextView = activity.findViewById(R.id.tv_world_searcing_arengine);
    }

    /**
     * Set ARSession, which will update and obtain the latest data in OnDrawFrame.
     *
     * @param arSession ARSession.
     */
    public void setArSession(ARSession arSession) {
        if (arSession == null) {
            Log.e(TAG, "setSession error, arSession is null!");
            return;
        }
        mSession = arSession;
    }

    /**
     * Set a gesture type queue.
     *
     * @param queuedSingleTaps Gesture type queue.
     */
    public void setQueuedSingleTaps(ArrayBlockingQueue<GestureEvent> queuedSingleTaps) {
        if (queuedSingleTaps == null) {
            Log.e(TAG, "setSession error, arSession is null!");
            return;
        }
        mQueuedSingleTaps = queuedSingleTaps;
    }

    /**
     * Set the DisplayRotationManage object, which will be used in onSurfaceChanged and onDrawFrame.
     *
     * @param displayRotationManager DisplayRotationManage is a customized object.
     */
    public void setDisplayRotationManage(DisplayRotationManager displayRotationManager) {
        if (displayRotationManager == null) {
            Log.e(TAG, "SetDisplayRotationManage error, displayRotationManage is null!");
            return;
        }
        mDisplayRotationManager = displayRotationManager;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the window color.
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        mTextureDisplay.init();
        mTextDisplay.setListener(new TextDisplay.OnTextInfoChangeListener() {
            @Override
            public void textInfoChanged(String text, float positionX, float positionY) {
                showWorldTypeTextView(text, positionX, positionY);
            }
        });

        mLabelDisplay.init(getPlaneBitmaps());

        mObjectDisplay.init(mContext);
    }

    /**
     * Create a thread for text display in the UI thread. This thread will be called back in TextureDisplay.
     *
     * @param text Gesture information displayed on the screen
     * @param positionX The left padding in pixels.
     * @param positionY The right padding in pixels.
     */
    private void showWorldTypeTextView(final String text, final float positionX, final float positionY) {
        mActivity.runOnUiThread(() -> {
            // Set the font size to be displayed on the screen.
            if (text != null) {
                mTextView.setText(text);
                mTextView.setPadding((int) positionX, (int) positionY, 0, 0);
            } else {
                mTextView.setText("");
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mTextureDisplay.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
        mDisplayRotationManager.updateViewportRotation(width, height);
        mObjectDisplay.setSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mSession == null) {
            return;
        }
        if (mDisplayRotationManager.getDeviceRotation()) {
            mDisplayRotationManager.updateArSessionDisplayGeometry(mSession);
        }

        try {
            mSession.setCameraTextureName(mTextureDisplay.getExternalTextureId());
            ARFrame arFrame = mSession.update();
            // Set the environment texture probe and mode after the camera is initialized.
//            setEnvTextureData();
            ARCamera arCamera = arFrame.getCamera();

            // The size of the projection matrix is 4 * 4.
            float[] projectionMatrix = new float[16];

            arCamera.getProjectionMatrix(projectionMatrix, PROJ_MATRIX_OFFSET, PROJ_MATRIX_NEAR, PROJ_MATRIX_FAR);
            mTextureDisplay.onDrawFrame(arFrame);
            StringBuilder sb = new StringBuilder();
            updateMessageData(arFrame, sb);
            mTextDisplay.onDrawFrame(sb);

            // The size of ViewMatrix is 4 * 4.
            float[] viewMatrix = new float[16];
            arCamera.getViewMatrix(viewMatrix, 0);
            for (ARPlane plane : mSession.getAllTrackables(ARPlane.class)) {
                if (plane.getType() != ARPlane.PlaneType.UNKNOWN_FACING
                        && plane.getTrackingState() == ARTrackable.TrackingState.TRACKING) {
                    hideLoadingMessage();
                    break;
                }
            }
            mLabelDisplay.onDrawFrame(mSession.getAllTrackables(ARPlane.class), arCamera.getDisplayOrientedPose(),
                    projectionMatrix);
            handleGestureEvent(arFrame, arCamera, projectionMatrix, viewMatrix);
            ARLightEstimate lightEstimate = arFrame.getLightEstimate();
            float lightPixelIntensity = 1;
            if (lightEstimate.getState() != ARLightEstimate.State.NOT_VALID) {
                lightPixelIntensity = lightEstimate.getPixelIntensity();
            }
            drawAllObjects(projectionMatrix, viewMatrix, lightPixelIntensity);
        } catch (AREngineRuntimeException e) {
            Log.e(TAG, "Exception on the ArDemoRuntimeException!");
        } catch (Exception t) {
            // This prevents the app from crashing due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread: ", t);
        }
    }
    private void setEnvTextureData() {
        if (!mHaveSetEnvTextureData) {
            float[] boundBox = mObjectDisplay.getBoundingBox();
            mSession.setEnvironmentTextureProbe(boundBox);
            Log.i(TAG, "setEnvironmentTextureProbe = " + Arrays.toString(boundBox));
            mSession.setEnvironmentTextureUpdateMode(ARSession.EnvironmentTextureUpdateMode.AUTO);
            mHaveSetEnvTextureData = true;
        }
    }

    private void drawAllObjects(float[] projectionMatrix, float[] viewMatrix, float lightPixelIntensity) {
        Iterator<VirtualObject> ite = mVirtualObjects.iterator();
        while (ite.hasNext()) {
            VirtualObject obj = ite.next();
            if (obj.getAnchor().getTrackingState() == ARTrackable.TrackingState.STOPPED) {
                ite.remove();
            }
            if (obj.getAnchor().getTrackingState() == ARTrackable.TrackingState.TRACKING) {
                mObjectDisplay.onDrawFrame(viewMatrix, projectionMatrix, lightPixelIntensity, obj);
            }
        }
    }

    private ArrayList<Bitmap> getPlaneBitmaps() {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_other_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_wall_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_floor_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_seat_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_table_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_ceiling_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_door_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_window_arengine));
        bitmaps.add(getPlaneBitmap(R.id.tv_world_plane_bed_arengine));
        return bitmaps;
    }

    private Bitmap getPlaneBitmap(int id) {
        TextView view = mActivity.findViewById(id);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = view.getDrawingCache();
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.setScale(MATRIX_SCALE_SX, MATRIX_SCALE_SY);
        if (bitmap != null) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    /**
     * Update the information to be displayed on the screen.
     *
     * @param sb String buffer.
     */
    private void updateMessageData(ARFrame arFrame, StringBuilder sb) {
        float fpsResult = doFpsCalculate();
        sb.append("FPS=").append(fpsResult).append(System.lineSeparator());

        ARLightEstimate lightEstimate = arFrame.getLightEstimate();

        if ((mSearchingTextView.getVisibility() != View.GONE)
                || (lightEstimate.getState() != ARLightEstimate.State.VALID)) {
            return;
        }

        // Obtain the estimated light data when the light intensity mode is enabled.
        if ((mArWorldTrackingConfig.getLightingMode() & ARConfigBase.LIGHT_MODE_AMBIENT_INTENSITY) != 0) {
            sb.append("PixelIntensity=").append(lightEstimate.getPixelIntensity()).append(System.lineSeparator());
        }

        // Obtain the texture data when the environment texture mode is enabled.
        if ((mArWorldTrackingConfig.getLightingMode() & ARConfigBase.LIGHT_MODE_ENVIRONMENT_LIGHTING) != 0) {
            sb.append("PrimaryLightIntensity=").append(lightEstimate.getPrimaryLightIntensity())
                    .append(System.lineSeparator());
            sb.append("PrimaryLightDirection=").append(Arrays.toString(lightEstimate.getPrimaryLightDirection()))
                    .append(System.lineSeparator());
            sb.append("PrimaryLightColor=").append(Arrays.toString(lightEstimate.getPrimaryLightColor()))
                    .append(System.lineSeparator());
            sb.append("LightShadowType=").append(lightEstimate.getLightShadowType()).append(System.lineSeparator());
            sb.append("LightShadowStrength=").append(lightEstimate.getShadowStrength()).append(System.lineSeparator());
            sb.append("LightSphericalHarmonicCoefficients=")
                    .append(Arrays.toString(lightEstimate.getSphericalHarmonicCoefficients()))
                    .append(System.lineSeparator());
        }
    }

    private float doFpsCalculate() {
        ++frames;
        long timeNow = System.currentTimeMillis();

        // Convert millisecond to second.
        if (((timeNow - lastInterval) / 1000.0f) > 0.5f) {
            fps = frames / ((timeNow - lastInterval) / 1000.0f);
            frames = 0;
            lastInterval = timeNow;
        }
        return fps;
    }

    private void hideLoadingMessage() {
        mActivity.runOnUiThread(() -> {
            if (mSearchingTextView != null) {
                mSearchingTextView.setVisibility(View.GONE);
                mSearchingTextView = null;
            }
        });
    }

    private void handleGestureEvent(ARFrame arFrame, ARCamera arCamera, float[] projectionMatrix, float[] viewMatrix) {
        GestureEvent event = mQueuedSingleTaps.poll();
        if (event == null) {
            return;
        }

        // Do not perform anything when the object is not tracked.
        if (arCamera.getTrackingState() != ARTrackable.TrackingState.TRACKING) {
            return;
        }

        int eventType = event.getType();
        switch (eventType) {
            case GestureEvent.GESTURE_EVENT_TYPE_DOUBLETAP:
                doWhenEventTypeDoubleTap(viewMatrix, projectionMatrix, event);
                break;
            case GestureEvent.GESTURE_EVENT_TYPE_SCROLL:
                if (mSelectedObj == null) {
                    break;
                }

                ARHitResult hitResultGestureEvent = hitTest4Result(arFrame, arCamera, event.getEventSecond());

                if (hitResultGestureEvent != null) {
                    mSelectedObj.setAnchor(hitResultGestureEvent.createAnchor());
                }
                break;

            case GestureEvent.GESTURE_EVENT_TYPE_SINGLETAPCONFIRMED:
                // Do not perform anything when an object is selected.
                if (mSelectedObj != null) {
                    mSelectedObj.setIsSelected(false);
                    mSelectedObj = null;
                }

                MotionEvent tap = event.getEventFirst();
                ARHitResult hitResult = null;

                hitResult = hitTest4Result(arFrame, arCamera, tap);

                if (hitResult == null) {
                    break;
                }
                doWhenEventTypeSingleTap(hitResult);
                break;
            default:
                Log.e(TAG, "Unknown motion event type, and do nothing.");
                break;
        }
    }

    private void doWhenEventTypeDoubleTap(float[] viewMatrix, float[] projectionMatrix, GestureEvent event) {
        if (mSelectedObj != null) {
            mSelectedObj.setIsSelected(false);
            mSelectedObj = null;
        }
        for (VirtualObject obj : mVirtualObjects) {
            if (mObjectDisplay.hitTest(viewMatrix, projectionMatrix, obj, event.getEventFirst())) {
                obj.setIsSelected(true);
                mSelectedObj = obj;
                break;
            }
        }
    }

    private void doWhenEventTypeSingleTap(ARHitResult hitResult) {
        // The hit results are sorted by distance. Only the nearest hit point is valid.
        // Set the number of stored objects to 10 to avoid the overload of rendering and AR Engine.
        if (mVirtualObjects.size() >= 16) {
            mVirtualObjects.get(0).getAnchor().detach();
            mVirtualObjects.remove(0);
        }

        ARTrackable currentTrackable = hitResult.getTrackable();
        if (currentTrackable instanceof ARPoint) {
            mVirtualObjects.add(new VirtualObject(hitResult.createAnchor(), BLUE_COLORS));
        } else if (currentTrackable instanceof ARPlane) {
            mVirtualObjects.add(new VirtualObject(hitResult.createAnchor(), GREEN_COLORS));
        } else {
            Log.i(TAG, "Hit result is not plane or point.");
        }
    }

    private ARHitResult hitTest4Result(ARFrame frame, ARCamera camera, MotionEvent event) {
        ARHitResult hitResult = null;
        List<ARHitResult> hitTestResults = frame.hitTest(event);

        for (int i = 0; i < hitTestResults.size(); i++) {
            // Determine whether the hit point is within the plane polygon.
            ARHitResult hitResultTemp = hitTestResults.get(i);
            if (hitResultTemp == null) {
                continue;
            }
            ARTrackable trackable = hitResultTemp.getTrackable();

            boolean isPlanHitJudge =
                    trackable instanceof ARPlane && ((ARPlane) trackable).isPoseInPolygon(hitResultTemp.getHitPose())
                            && (calculateDistanceToPlane(hitResultTemp.getHitPose(), camera.getPose()) > 0);

            // Determine whether the point cloud is clicked and whether the point faces the camera.
            boolean isPointHitJudge = trackable instanceof ARPoint
                    && ((ARPoint) trackable).getOrientationMode() == ARPoint.OrientationMode.ESTIMATED_SURFACE_NORMAL;

            // Select points on the plane preferentially.
            if (isPlanHitJudge || isPointHitJudge) {
                hitResult = hitResultTemp;
                if (trackable instanceof ARPlane) {
                    break;
                }
            }
        }
        return hitResult;
    }

    /**
     * Calculate the distance between a point in a space and a plane. This method is used
     * to calculate the distance between a camera in a space and a specified plane.
     *
     * @param planePose ARPose of a plane.
     * @param cameraPose ARPose of a camera.
     * @return Calculation results.
     */
    private static float calculateDistanceToPlane(ARPose planePose, ARPose cameraPose) {
        // The dimension of the direction vector is 3.
        float[] normals = new float[3];

        // Obtain the unit coordinate vector of a normal vector of a plane.
        planePose.getTransformedAxis(1, 1.0f, normals, 0);

        // Calculate the distance based on projection.
        return (cameraPose.tx() - planePose.tx()) * normals[0] // 0:x
                + (cameraPose.ty() - planePose.ty()) * normals[1] // 1:y
                + (cameraPose.tz() - planePose.tz()) * normals[2]; // 2:z
    }
}
