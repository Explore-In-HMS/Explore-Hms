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

package com.hms.explorehms.huawei.feature_scenekit.fluidSimulation3D;

import static com.huawei.hms.scene.sdk.render.FluidComponent.FluidOperateType.ADD;
import static com.huawei.hms.scene.sdk.render.FluidComponent.FluidOperateType.REDUCE;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_scenekit.R;
import com.huawei.hms.scene.math.Vector3;
import com.huawei.hms.scene.sdk.render.Animator;
import com.huawei.hms.scene.sdk.render.FluidComponent;
import com.huawei.hms.scene.sdk.render.Model;
import com.huawei.hms.scene.sdk.render.Node;
import com.huawei.hms.scene.sdk.render.Resource;
import com.huawei.hms.scene.sdk.render.ResourceFactory;
import com.huawei.hms.scene.sdk.render.SdfSphereShape;
import com.huawei.hms.scene.sdk.render.Texture;
import com.huawei.hms.scene.sdk.render.Transform;

import java.lang.ref.WeakReference;


public class Fluid3DActivity extends Activity implements SensorEventListener {
    private XRenderView renderView;
    private Model islandModel;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int rotation;
    private Node fluidNode;
    private GestureEvent gesture;
    private Texture fluidTexture;
    private float setVolume = 0.5f;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scenekit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (fluidNode == null) {
            return false;
        }
        FluidComponent fluidComponent = fluidNode.getComponent(FluidComponent.descriptor());
        if (fluidComponent == null) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.add_item:
                if (setVolume < 0.8f) {
                    setVolume = setVolume + 0.1f;
                    // 默认最大体积750*10,每加减水一次体积变化1/10
                    fluidComponent.addOrReduceFluid(ADD, 750.0f);
                } else {
                    Toast.makeText(this,"Fluid is full",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.reduce_item:
                if (setVolume > 0.f) {
                    setVolume = setVolume - 0.1f;
                    fluidComponent.addOrReduceFluid(REDUCE, 750.0f);
                } else {
                    Toast.makeText(this,"Fluid is gone",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        renderView = new XRenderView(this);
        setContentView(renderView);

        createFluidScene();

        Texture.builder()
            .setUri(Uri.parse("Scene/fluid.dds"))
            .load(this, new FluidTextureLoadEventListener(new WeakReference<>(this)));
        Model.builder()
            .setUri(Uri.parse("Island/island2.glb"))
            .load(this, new ModelLoadEventListener(new WeakReference<>(this)));
    }

    private void createFluidScene() {
        fluidNode = renderView.getScene().createNode("fluidNode");

        FluidComponent fluidComponent = fluidNode.addComponent(FluidComponent.descriptor());
        if (fluidComponent != null) {
            SdfSphereShape sphere = fluidComponent.createSdfSphereShape();
            sphere.setRadius(12.0f);
            fluidComponent.setFluidVolume(0.5f);
        }
        fluidNode.getComponent(Transform.descriptor())
            .setPosition(new Vector3(0, 0, 1.5f))
            .setScale(new Vector3(2, 2, 2));

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        gesture = new GestureEvent(fluidComponent, displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (gesture != null) {
            gesture.onTouch(renderView, motionEvent);
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (fluidNode == null) {
            return;
        }
        FluidComponent fluidComponent = fluidNode.getComponent(FluidComponent.descriptor());
        if (fluidComponent == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int sensorX = (int) event.values[0];
            int sensorY = (int) event.values[1];
            int gravityX = 0;
            int gravityY = 0;
            int gravityZ = (int) event.values[2]; // 2: accelerometer sensor z part

            rotation = this.getWindowManager().getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    gravityX = -sensorX;
                    gravityY = -sensorY;
                    break;
                case Surface.ROTATION_90:
                    gravityX = sensorY;
                    gravityY = -sensorX;
                    break;
                case Surface.ROTATION_180:
                    gravityX = sensorX;
                    gravityY = sensorY;
                    break;
                case Surface.ROTATION_270:
                    gravityX = -sensorY;
                    gravityY = sensorX;
                    break;
                default:
                    break;
            }
            Vector3 gravity = new Vector3(gravityX, gravityY, -gravityZ);
            if (gravity.length() > 0.1f) {
                gravity = gravity.normalize();
                gravity.multiply(10.0f);
                fluidComponent.setGravity(gravity);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private static final class FluidTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<Fluid3DActivity> weakRef;

        FluidTextureLoadEventListener(WeakReference<Fluid3DActivity> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            Fluid3DActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.isDestroyed()) {
                Texture.destroy(texture);
                return;
            }

            sampleActivity.fluidTexture = texture;
            sampleActivity.renderView.getScene().setFluidTexture(texture);
        }

        @Override
        public void onException(Exception exception) {
            Fluid3DActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.isDestroyed()) {
                return;
            }
            Toast.makeText(sampleActivity.renderView.getContext(),
                "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class ModelLoadEventListener implements Resource.OnLoadEventListener<Model> {
        private final WeakReference<Fluid3DActivity> weakRef;

        ModelLoadEventListener(WeakReference<Fluid3DActivity> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Model model) {
            Fluid3DActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.isDestroyed()) {
                Model.destroy(model);
                return;
            }
            sampleActivity.islandModel = model;
            Node treeNode = sampleActivity.renderView.getScene().createNodeFromModel(model);
            treeNode.getComponent(Transform.descriptor())
                .setPosition(new Vector3(0.05f, -0.2f, 1.5f))
                .setScale(new Vector3(0.3f, 0.3f, 0.3f));
            if (treeNode.getComponent(Animator.descriptor()) != null) {
                treeNode.getComponent(Animator.descriptor()).play("animate1");
            }
        }

        @Override
        public void onException(Exception exception) {
            Fluid3DActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.isDestroyed()) {
                return;
            }
            Toast.makeText(sampleActivity,
                "failed to load model: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        renderView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        renderView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        renderView.destroy();
        if (fluidTexture != null) {
            Texture.destroy(fluidTexture);
        }
        if (islandModel != null) {
            Model.destroy(islandModel);
        }
        ResourceFactory.getInstance().gc();
    }
}
