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

package com.hms.explorehms.huawei.feature_scenekit;


import com.huawei.hms.scene.engine.iphysics.utils.ParticleSystemInfo;
import com.huawei.hms.scene.sdk.fluid.ParticleSystem;
import com.huawei.hms.scene.sdk.fluid.SceneKitFluid;
import com.huawei.hms.scene.sdk.fluid.World;

public class WorldManager {
    private World world = null;
    private ParticleSystem particleSystem = null;

    // Obtain the physical world.
    public World acquire() {
        return world;
    }

    // Initialize the physical world.
    public void init() {
        // Create a physical world.
        world = SceneKitFluid.getInstance().createWorld(0, 0);
        // Create a particle system.
        ParticleSystemInfo info = new ParticleSystemInfo();
        info.setRadius(0.05f);
        info.setViscosity(0.f);
        particleSystem = world.createParticleSystem(info);
    }

    // Obtain the particle system in the physical world.
    public ParticleSystem getParticleSystem() {return particleSystem;}
}
