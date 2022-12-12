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


import com.hms.explorehms.huawei.feature_hiaifoundation.models.ModelInfo;
import com.hms.explorehms.huawei.feature_hiaifoundation.utils.ModelManager;

import java.util.ArrayList;
import java.util.Objects;

public class SyncClassifyActivity extends NpuClassifyActivity {

    private static final String TAG = SyncClassifyActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    protected void runModel(ModelInfo modelInfo, ArrayList<byte[]> inputData) {
        outputDataList = ModelManager.runModelSync(modelInfo, inputData);

        if (outputDataList == null) {
            Log.e(TAG, "Sync runModel outputdata is null");

            return;
        }

        inferenceTime = ModelManager.GetTimeUseSync();
        for(float[] outputData : outputDataList){
            Log.i(TAG, "runModel outputdata length : " + outputData.length);

            postProcess(outputData);
        }
    }

    @Override
    protected ArrayList<ModelInfo> loadModel(ArrayList<ModelInfo> modelInfo) {
        return ModelManager.loadModelSync(modelInfo);
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
