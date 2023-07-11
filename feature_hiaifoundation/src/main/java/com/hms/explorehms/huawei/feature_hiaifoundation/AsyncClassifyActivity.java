/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hms.explorehms.huawei.feature_hiaifoundation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hms.explorehms.huawei.feature_hiaifoundation.models.ModelInfo;
import com.hms.explorehms.huawei.feature_hiaifoundation.utils.ModelManager;
import com.hms.explorehms.huawei.feature_hiaifoundation.utils.ModelManagerListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class AsyncClassifyActivity extends NpuClassifyActivity {

    private static final String TAG = AsyncClassifyActivity.class.getSimpleName();


    ModelManagerListener listener = new ModelManagerListener() {

        @Override
        public void OnProcessDone(final int taskId, final ArrayList<float[]> outputList, final float inferencetime) {

            Log.e(TAG, " java layer OnProcessDone: " + taskId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (taskId > 0) {
                       for(float[] output:outputList){
                            Toast toast = Toast.makeText(AsyncClassifyActivity.this, "run model success. taskId is:" + taskId, Toast.LENGTH_SHORT);
                            toast.show();
                            Log.i(TAG, " run model success. taskId is: " + taskId);

                            outputData = output;
                            inferenceTime = inferencetime/1000;
                            Log.i(TAG, " run model success. outputData is: " + Arrays.toString(outputData));
                            postProcess(outputData);
                       }
                    } else {
                        Toast toast = Toast.makeText(AsyncClassifyActivity.this, "run model fail. taskId is:" + taskId, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });

        }

        @Override
        public void onServiceDied() {
            Log.e(TAG, "onServiceDied: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void runModel(ModelInfo modelInfo, ArrayList<byte[]> inputDataList) {
        ModelManager.runModelAsync(modelInfo, inputDataList, listener);
    }

    @Override
    protected ArrayList<ModelInfo> loadModel(ArrayList<ModelInfo> modelInfo) {
        return ModelManager.loadModelAsync(modelInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
