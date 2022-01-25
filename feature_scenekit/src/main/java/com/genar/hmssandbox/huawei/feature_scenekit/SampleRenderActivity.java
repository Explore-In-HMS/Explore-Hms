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

package com.genar.hmssandbox.huawei.feature_scenekit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.widget.Toast;

import com.genar.hmssandbox.huawei.Util;
import com.huawei.hms.scene.math.Quaternion;
import com.huawei.hms.scene.math.Vector3;
import com.huawei.hms.scene.sdk.render.Animator;
import com.huawei.hms.scene.sdk.render.Camera;
import com.huawei.hms.scene.sdk.render.Light;
import com.huawei.hms.scene.sdk.render.Model;
import com.huawei.hms.scene.sdk.render.Node;
import com.huawei.hms.scene.sdk.render.RenderView;
import com.huawei.hms.scene.sdk.render.Renderable;
import com.huawei.hms.scene.sdk.render.Resource;
import com.huawei.hms.scene.sdk.render.ResourceFactory;
import com.huawei.hms.scene.sdk.render.Texture;
import com.huawei.hms.scene.sdk.render.Transform;

import java.lang.ref.WeakReference;
import java.util.List;

public class SampleRenderActivity extends AppCompatActivity {

    private RenderView renderView;
    private Node cameraNode;
    private Node lightNode;
    private Node shadowLightNode;
    private boolean destroyed = false;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private Model model;
    private Texture skyBoxTexture;
    private Texture specularEnvTexture;
    private Texture diffuseEnvTexture;
    private Node modelNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_render);
        renderView = findViewById(R.id.render_view);
        prepareScene();
        setupToolbar();
        loadModel();
        loadTextures();
        addGestureEventListener();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_render_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_scenekit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void prepareScene() {
        // Obtain the screen information for setting the aspect ratio of the camera window.
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        // Obtain the scene instance of the rendering view and create a node to hold the Camera component.
        cameraNode = renderView.getScene().createNode("mainCameraNode");
        // Add the Camera component to the node, and set the projection mode, near clipping plane, far clipping plane, field of view (FOV), aspect ratio of the camera window, and activation status.
        cameraNode.addComponent(Camera.descriptor())
                .setProjectionMode(Camera.ProjectionMode.PERSPECTIVE)
                .setNearClipPlane(.1f)
                .setFarClipPlane(1000.f)
                .setFOV(60.f)
                .setAspect((float) displayMetrics.widthPixels / displayMetrics.heightPixels)
                .setActive(true);
        // Obtain the Transform component of the node and change the node position.
        cameraNode.getComponent(Transform.descriptor())
                .setPosition(new Vector3(0, 5.f, 30.f));

        // Obtain the scene instance of the rendering view and create a node to hold the Light component.
        lightNode = renderView.getScene().createNode("mainLightNode");
        // Add a Light component to the node and set the light type, light color, light intensity, and whether to cast shadows.
        lightNode.addComponent(Light.descriptor())
                .setType(Light.Type.POINT)
                .setColor(new Vector3(1.f, 1.f, 1.f))
                .setIntensity(1.f)
                .setCastShadow(false);
        // Obtain the Transform component of the node and change the node position.
        lightNode.getComponent(Transform.descriptor())
                .setPosition(new Vector3(3.f, 3.f, 3.f));


        shadowLightNode = renderView.getScene().createNode("shadowLight");
        shadowLightNode.addComponent(Light.descriptor())
                // Set the light source to directional light.
                .setType(Light.Type.DIRECTIONAL)
                .setColor(new Vector3(1.f, 1.f, 1.f))
                .setIntensity(1.f)
                .setCastShadow(true);
        shadowLightNode.getComponent(Transform.descriptor())
                // Set the position, target, and up vector of a directional light.
                // Here, we set a directional light shining from right to left horizontally.
                .lookAt(new Vector3(1.f, 0.f, 0.f), new Vector3(0, 0, 0), new Vector3(0, 1, 0));


    }

    private void loadModel() {
        // Obtain the model builder instance, set the resource URI, and load the resource.
        Model.builder()
                // Replace the resource URI with the actual one.
                .setUri(Uri.parse("RenderView/scene.gltf"))
                .load(this, new ModelLoadEventListener(new WeakReference<>(this)));
    }


    private void loadTextures() {
        // Obtain the texture builder instance, set the resource URI, and load the resource.
        Texture.builder()
                // Replace the skybox texture URI with the actual one.
                .setUri(Uri.parse("RenderView/skyboxTexture.dds"))
                .load(this, new SkyBoxTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
                // Replace the specular map URI with the actual one.
                .setUri(Uri.parse("RenderView/specularEnvTexture.dds"))
                .load(this, new SpecularEnvTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
                // Replace the diffuse map URI with the actual one.
                .setUri(Uri.parse("RenderView/diffuseEnvTexture.dds"))
                .load(this, new DiffuseEnvTextureLoadEventListener(new WeakReference<>(this)));
    }

    private static final class ModelLoadEventListener implements Resource.OnLoadEventListener<Model> {
        // Use weak reference to prevent memory leakage.
        private final WeakReference<SampleRenderActivity> weakRef;


        public ModelLoadEventListener(WeakReference<SampleRenderActivity> weakRef) {
            this.weakRef = weakRef;
        }


        @Override
        public void onLoaded(Model model) {
            SampleRenderActivity sampleActivity = weakRef.get();
            // If the activity has been destroyed, release the loaded model immediately.
            if (sampleActivity == null || sampleActivity.destroyed) {
                Model.destroy(model);
                return;
            }

            // Save the model to a local variable.
            sampleActivity.model = model;
            // Load the model to the scene.
            sampleActivity.modelNode = sampleActivity.renderView.getScene().createNodeFromModel(model);
            // Obtain the Transform component of the model node and set the node position and scaling.
            sampleActivity.modelNode.getComponent(Transform.descriptor())
                    .setPosition(new Vector3(0.f, 0.f, 0.f))
                    .scale(new Vector3(0.02f, 0.02f, 0.02f));

            sampleActivity.modelNode.traverseDescendants(descendant -> {
                // Obtain the Renderable component.
                Renderable renderable = descendant.getComponent(Renderable.descriptor());
                // Enable the function of casting shadows for a descendant node when it has a Renderable component.
                if (renderable != null) {
                    renderable
                            .setCastShadow(true);
                }});

            sampleActivity.modelNode.traverseDescendants(descendant -> {
                // Enable the function of receiving shadows for a descendant node when it has a Renderable component.
                Renderable renderable = descendant.getComponent(Renderable.descriptor());
                if (renderable != null) {
                    renderable
                            .setReceiveShadow(true);
                }
            });

            Animator animator = sampleActivity.modelNode.getComponent(Animator.descriptor());
            if (animator != null) {
                // Obtain the names of all animations in the model.
                List<String> animations = animator.getAnimations();
                if (animations.isEmpty()) {
                    return;
                }
                // Set the animation playback parameters, including whether to play in an inverse order, whether to enable repeat mode, playback speed, and first animation segment.
                animator
                        .setInverse(false)
                        .setRecycle(true)
                        .setSpeed(1.0f)
                        .play(animations.get(0));
            }



        }


        @Override
        public void onException(Exception e) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class SkyBoxTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SampleRenderActivity> weakRef;


        public SkyBoxTextureLoadEventListener(WeakReference<SampleRenderActivity> weakRef) {
            this.weakRef = weakRef;
        }


        @Override
        public void onLoaded(Texture texture) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }

            // Save the skybox texture to a local variable.
            sampleActivity.skyBoxTexture = texture;
            // Set the skybox texture for the scene.
            sampleActivity.renderView.getScene().setSkyBoxTexture(texture);
        }


        @Override
        public void onException(Exception e) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load texture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class SpecularEnvTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SampleRenderActivity> weakRef;


        public SpecularEnvTextureLoadEventListener(WeakReference<SampleRenderActivity> weakRef) {
            this.weakRef = weakRef;
        }


        @Override
        public void onLoaded(Texture texture) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }


            // Save the specular map to a local variable.
            sampleActivity.specularEnvTexture = texture;
            // Set the specular map for the scene.
            sampleActivity.renderView.getScene().setSpecularEnvTexture(texture);
        }


        @Override
        public void onException(Exception e) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load texture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class DiffuseEnvTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<SampleRenderActivity> weakRef;


        public DiffuseEnvTextureLoadEventListener(WeakReference<SampleRenderActivity> weakRef) {
            this.weakRef = weakRef;
        }


        @Override
        public void onLoaded(Texture texture) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                Texture.destroy(texture);
                return;
            }


            // Save the diffuse map to a local variable.
            sampleActivity.diffuseEnvTexture = texture;
            // Set the diffuse map for the scene.
            sampleActivity.renderView.getScene().setDiffuseEnvTexture(texture);
        }


        @Override
        public void onException(Exception e) {
            SampleRenderActivity sampleActivity = weakRef.get();
            if (sampleActivity == null || sampleActivity.destroyed) {
                return;
            }
            Toast.makeText(sampleActivity, "failed to load texture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void addGestureEventListener() {
        // Create a slide gesture processor.
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (modelNode != null) {
                    // Rotate the model node after a slide gesture is captured.
                    modelNode.getComponent(Transform.descriptor())
                            .rotate(new Quaternion(Vector3.UP, -0.009f * distanceX));
                }
                return true;
            }
        });
        // Create a pinch gesture processor.
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (modelNode != null) {
                    // Resize the model node after a pinch gesture is captured.
                    float factor = detector.getScaleFactor();
                    modelNode.getComponent(Transform.descriptor())
                            .scale(new Vector3(factor, factor, factor));
                }
                return true;
            }
        });
        renderView.addOnTouchEventListener((e) -> {
            // Pass the gesture event to the pinch gesture processor.
            boolean result = scaleGestureDetector.onTouchEvent(e);
            // Pass the gesture event to the slide gesture processor.
            result = gestureDetector.onTouchEvent(e) || result;
            // Return whether the event has been processed.
            return result;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume rendering in the rendering view.
        renderView.resume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Pause rendering in the rendering view.
        renderView.pause();
    }


    @Override
    protected void onDestroy() {
        destroyed = true;
        renderView.destroy();
        // Destroy the rendering view.
        if (model != null) {
            // Explicitly clean up the model.
            Model.destroy(model);
        }
        if (skyBoxTexture != null) {
            // Explicitly clean up skybox textures.
            Texture.destroy(skyBoxTexture);
        }
        if (specularEnvTexture != null) {
            // Explicitly clean up specular maps.
            Texture.destroy(specularEnvTexture);
        }
        if (diffuseEnvTexture != null) {
            // Explicitly clean up diffuse maps.
            Texture.destroy(diffuseEnvTexture);
        }
        ResourceFactory.getInstance().gc();
        super.onDestroy();
    }

}