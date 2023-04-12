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

package com.hms.explorehms.huawei.feature_scenekit.sceneview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.huawei.hms.scene.sdk.*;

public class SceneSampleView extends SceneView {

    /**
     * SceneSampleView class constructor
     * @param context Context object
     */
    public SceneSampleView(Context context) {
        super(context);

    }

    /**
     * SceneSampleView class constructor
     * @param context Context object
     * @param attributeSet AttributeSet interface object
     */
    public SceneSampleView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    /**
     * Create and initialize SceneView by surfaceCreated
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);

        loadScene("SceneView/scene.gltf");
        loadSpecularEnvTexture("SceneView/specularEnvTexture.dds");
        loadDiffuseEnvTexture("SceneView/diffuseEnvTexture.dds");

        /**
         * -- optional --
         *
         * call loadSkyBox to load skybox materials
         * loadSkyBox("path");
         */
    }
}
