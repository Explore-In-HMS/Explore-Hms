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

package com.hms.explorehms.huawei.feature_scenekit.faceview;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_scenekit.R;
import com.huawei.hms.scene.sdk.FaceView;
import com.huawei.hms.scene.sdk.common.LandmarkType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FaceViewActivity extends AppCompatActivity {

    //FaceView Object Configuration
    private final float[] position = {0.0f, 0.0f, 0.0f};
    private final float[] rotation = {1.0f, 0.0f, 0.0f, 0.0f};
    private final float[] scale = {1.0f, 1.0f, 1.0f};
    //

    private FaceView faceView;
    private SwitchCompat switchFaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faceview);
        if(!checkDeviceProcessor()){
            Toast.makeText(FaceViewActivity.this,"Your device is not supported by FaceView feature",Toast.LENGTH_SHORT).show();
        }
        setupToolbar();
        initUI();
        initListener();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_face_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {
        faceView = findViewById(R.id.view_faceview);
        switchFaceView = findViewById(R.id.switch_faceview);
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener() {
        switchFaceView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                faceView.clearResource();
                if (isChecked) {
                    // Load materials.
                    int index = faceView.loadAsset("FaceView/fox.glb", LandmarkType.TIP_OF_NOSE);
                    // Optional) Set the initial status of a face.
                    faceView.setInitialPose(index, position, rotation, scale);

                }
            }

        });


    }

    /**
     * Pause FaceView Service
     */
    @Override
    protected void onPause() {
        super.onPause();
        faceView.onPause();
    }

    /**
     * Resume FaceView Service
     */
    @Override
    protected void onResume() {
        super.onResume();
        faceView.onResume();
    }

    /**
     * Destroy FaceView Service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        faceView.destroy();
    }

    public static Boolean checkDeviceProcessor(){

        boolean processorOK = false;
        String hardware = "Hardware";
        try(BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"))) {

            String str;

            Map<String, String> output = new HashMap<>();

            while ((str = br.readLine ()) != null) {

                String[] data = str.split (":");

                if (data.length > 1) {

                    String key = data[0].trim ().replace (" ", "_");
                    if (key.equals(hardware)){
                        output.put(key, data[1].trim ());
                        break;
                    }
                }
            }

            /**
             * Farklı şekillerde kullanılabilir
             */

            if(output.get(hardware) != null && !Objects.equals(output.get(hardware), "")){
                String processorHardware = output.get(hardware);

                processorOK = processorHardware != null && (
                        processorHardware.contains("Kirin970") ||
                                processorHardware.contains("Kirin990") );

            }
            br.close();

        }catch (IOException e) {
            Log.e("ProcessorInfo",e.toString());
            processorOK = false;
        }

        return processorOK;
    }


}
