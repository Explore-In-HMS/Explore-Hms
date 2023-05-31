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

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.huawei.hms.scene.math.Vector3;
import com.huawei.hms.scene.sdk.render.Camera;
import com.huawei.hms.scene.sdk.render.Light;
import com.huawei.hms.scene.sdk.render.Node;
import com.huawei.hms.scene.sdk.render.RenderView;
import com.huawei.hms.scene.sdk.render.Resource;
import com.huawei.hms.scene.sdk.render.Texture;
import com.huawei.hms.scene.sdk.render.Transform;

import java.lang.ref.WeakReference;


public class XRenderView extends RenderView {
    private boolean isDestroyed = false;
    private Texture backgroundTexture;
    private Texture radianceTexture;
    private Texture irradianceTexture;

    public XRenderView(Context context) {
        super(context);
        prepareScene(context);
        Texture.builder()
            .setUri(Uri.parse("Scene/background.dds"))
            .load(context, new BackgroundTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
            .setUri(Uri.parse("Scene/2-specular_venice_sunset.dds"))
            .load(context, new RadianceTextureLoadEventListener(new WeakReference<>(this)));
        Texture.builder()
            .setUri(Uri.parse("Scene/2-diffuse_venice_sunset.dds"))
            .load(context, new IrradianceTextureLoadEventListener(new WeakReference<>(this)));
    }

    @Override
    public void destroy() {
        isDestroyed = true;
        if (backgroundTexture != null) {
            Texture.destroy(backgroundTexture);
        }
        if (radianceTexture != null) {
            Texture.destroy(radianceTexture);
        }
        if (irradianceTexture != null) {
            Texture.destroy(irradianceTexture);
        }
        super.destroy();
    }

    private void prepareScene(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Node cameraNode = getScene().createNode("mainCameraNode");
        cameraNode.addComponent(Camera.descriptor())
            .setProjectionMode(Camera.ProjectionMode.PERSPECTIVE)
            .setNearClipPlane(.01f)
            .setFarClipPlane(100.f)
            .setFOV(60.f)
            .setAspect((float)displayMetrics.widthPixels / displayMetrics.heightPixels)
            .setActive(true);
        cameraNode.getComponent(Transform.descriptor())
            .setPosition(new Vector3(0, 0, 5.f));

        Node lightNode = getScene().createNode("mainLightNode");
        lightNode.addComponent(Light.descriptor())
            .setType(Light.Type.POINT)
            .setColor(new Vector3(1, 1, 1))
            .setCastShadow(false)
            .setIntensity(1.0f);
        lightNode.getComponent(Transform.descriptor())
            .setPosition(new Vector3(0.0f, 2.5f, 2.5f));
    }

    private static final class BackgroundTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<XRenderView> weakRef;

        BackgroundTextureLoadEventListener(WeakReference<XRenderView> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            XRenderView renderView = weakRef.get();
            if (renderView == null || renderView.isDestroyed) {
                Texture.destroy(texture);
                return;
            }
            renderView.backgroundTexture = texture;
            renderView.getScene().setBackground(texture);
        }

        @Override
        public void onException(Exception exception) {
            XRenderView backGroundRenderView = weakRef.get();
            if (backGroundRenderView == null || backGroundRenderView.isDestroyed) {
                return;
            }
            Toast.makeText(backGroundRenderView.getContext(),
                "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class RadianceTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<XRenderView> weakRef;

        RadianceTextureLoadEventListener(WeakReference<XRenderView> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            XRenderView renderView = weakRef.get();
            if (renderView == null || renderView.isDestroyed) {
                Texture.destroy(texture);
                return;
            }

            renderView.radianceTexture = texture;
            renderView.getScene().setSpecularEnvTexture(texture);
        }

        @Override
        public void onException(Exception exception) {
            XRenderView radianceRenderView = weakRef.get();
            if (radianceRenderView == null || radianceRenderView.isDestroyed) {
                return;
            }
            Toast.makeText(radianceRenderView.getContext(),
                "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static final class IrradianceTextureLoadEventListener implements Resource.OnLoadEventListener<Texture> {
        private final WeakReference<XRenderView> weakRef;

        IrradianceTextureLoadEventListener(WeakReference<XRenderView> weakRef) {
            this.weakRef = weakRef;
        }

        @Override
        public void onLoaded(Texture texture) {
            XRenderView renderView = weakRef.get();
            if (renderView == null || renderView.isDestroyed) {
                Texture.destroy(texture);
                return;
            }

            renderView.irradianceTexture = texture;
            renderView.getScene().setDiffuseEnvTexture(texture);
        }

        @Override
        public void onException(Exception exception) {
            XRenderView irradianceRenderView = weakRef.get();
            if (irradianceRenderView == null || irradianceRenderView.isDestroyed) {
                return;
            }
            Toast.makeText(irradianceRenderView.getContext(),
                    "failed to load texture: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
