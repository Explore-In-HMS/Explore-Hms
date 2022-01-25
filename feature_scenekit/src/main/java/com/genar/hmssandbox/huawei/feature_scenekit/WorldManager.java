package com.genar.hmssandbox.huawei.feature_scenekit;


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
