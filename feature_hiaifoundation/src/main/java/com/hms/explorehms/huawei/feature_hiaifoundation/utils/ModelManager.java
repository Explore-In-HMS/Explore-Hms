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

package com.hms.explorehms.huawei.feature_hiaifoundation.utils;

import android.util.Log;


import com.hms.explorehms.huawei.feature_hiaifoundation.models.ModelInfo;

import java.util.ArrayList;

public class ModelManager {

    private static final String TAG = ModelManager.class.getSimpleName();

    private ModelManager() {
    }

    public static boolean loadJNISo() {
        try {
            System.loadLibrary("hiaijni");

            return true;
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "failed to load native library: " + e.getMessage());

            return false;
        }
    }

    public static native ArrayList<float[]> runModelSync(ModelInfo modelInfo, ArrayList<byte[]> buf);

    public static native long GetTimeUseSync();

    public static native void runModelAsync(ModelInfo modelInfo, ArrayList<byte[]> buf, ModelManagerListener listener);

    public static native ArrayList<ModelInfo> loadModelAsync(ArrayList<ModelInfo> modelInfo);

    public static native ArrayList<ModelInfo> loadModelSync(ArrayList<ModelInfo> modelInfo);

    /**
     *
     * @param offlinemodelpath   /xxx/xxx/xxx/xx.om
     * @return ture : it can run on NPU
     *          false: it should run on CPU
     */
    public static native boolean modelCompatibilityProcessFromFile(String offlinemodelpath);

    //public static native boolean modelCompatibilityProcessFromBuffer(byte[] onlinemodelbuffer,byte[] modelparabuffer,String framework,String offlinemodelpath);
}
