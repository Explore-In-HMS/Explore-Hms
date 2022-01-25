package com.genar.hmssandbox.huawei.feature_scenekit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.os.Bundle;


import com.genar.hmssandbox.huawei.Util;
import com.huawei.hms.scene.engine.iphysics.utils.Color;
import com.huawei.hms.scene.engine.iphysics.utils.ParticleGroupInfo;
import com.huawei.hms.scene.engine.iphysics.utils.PolygonShape;
import com.huawei.hms.scene.engine.iphysics.utils.Vector2;
import com.huawei.hms.scene.sdk.fluid.Body;
import com.huawei.hms.scene.sdk.fluid.ParticleSystem;
import com.huawei.hms.scene.sdk.fluid.World;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.huawei.hms.scene.engine.iphysics.utils.ParticleGroupInfo.ParticleFlag.MIX_COLOR;
import static com.huawei.hms.scene.engine.iphysics.utils.ParticleGroupInfo.ParticleFlag.WATER;

public class FluidSimulation2D extends AppCompatActivity {
    private WorldManager worldManager = new WorldManager();
    private Render render = new Render();
    private GLSurfaceView mainView;
    ByteBuffer positionBuffer = ByteBuffer.allocateDirect(40000).order(ByteOrder.nativeOrder());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fluid_simulation_2d);
        worldManager.init();
        render.init();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_simulation_view);
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

    @Override
    protected void onResume() {
        super.onResume();
        mainView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainView.onPause();
    }

    public class Render implements GLSurfaceView.Renderer {
        float width = 3.0f;
        float height = 3.0f;
        float thick = 1.0f;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            gl10.glClearColor(0f, 0f, 0f, 0f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {
            gl10.glViewport(0 , 0 , i, i1);
            width = i;
            height = i1;
        }

        private void init() {
            mainView = findViewById(R.id.world);
            mainView.setRenderer(this);
            resetBorder();
            resetNodes();
        }

        // Create borders.
        private void resetBorder() {
            World world = worldManager.acquire();
            Body border = world.createBody(World.BodyType.STATIC_BODY);
            PolygonShape borderShape = new PolygonShape(0.f, 0.f, new Vector2(0.f, 0.f), 0.f);

            // Top border.
            borderShape.setBox(width, thick, new Vector2(width / 2, height + thick), 0.f);
            border.addPolygonShape(borderShape);
            // Bottom border.
            borderShape.setBox(width, thick, new Vector2(width / 2, -thick), 0.f);
            border.addPolygonShape(borderShape);
            // Left border.
            borderShape.setBox(thick, height, new Vector2(-thick, height / 2), 0.f);
            border.addPolygonShape(borderShape);
            // Right border.
            borderShape.setBox(thick, height, new Vector2(width + thick, height / 2), 0.f);
            border.addPolygonShape(borderShape);
        }

        // Draw particles.
        private void draw() {
            positionBuffer.rewind();
            ParticleSystem system = worldManager.getParticleSystem();
            byte[] positionByte = new byte[positionBuffer.capacity()];
            system.copyPositionBuffer(system.getParticleCount(), positionByte);
            positionBuffer.put(positionByte);
            positionBuffer.position(0);

            GLES10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            GLES10.glLoadIdentity();
            GLES10.glTranslatef( -1, -1, 0);
            GLES10.glScalef(2.f  / 3, 2.f / 3, 1);
            GLES10.glColor4f(0.1f, 0.5f, 1f, 1.0f);
            GLES10.glPointSize(20.f);
            GLES10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            GLES10.glVertexPointer(2, GL10.GL_FLOAT, 0, positionBuffer);
            GLES10.glDrawArrays(GL10.GL_POINTS, 0, system.getParticleCount());
            GLES10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }

        // Create particles.
        private void resetNodes() {
            ParticleGroupInfo groupDef = new ParticleGroupInfo(WATER | MIX_COLOR);
            groupDef.setColor(new Color((short) 30, (short) 144, (short) 255, (short) 220));

            // Set a shape for the particle group.
            PolygonShape shape = new PolygonShape(0.f, 0.f, new Vector2(0.f, 0.f), 0);
            shape.setBox(3.0f * 0.2f, 3.0f * 0.2f, new Vector2(3.0f / 2, 3.0f / 2), 0);
            groupDef.setShape(shape);

            ParticleSystem system = worldManager.getParticleSystem();
            // Create a particle group.
            system.addParticles(groupDef);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            // Use physical simulation for frame updates.
            World world = worldManager.acquire();
            world.setGravity(0, new Random().nextFloat() * (-1));
            world.singleStep(1 / 60f);

            // Render particles.
            draw();
        }
    }


}
