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

package com.genar.hmssandbox.huawei;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.network.NetworkKit;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptions;
import com.huawei.hms.videokit.player.util.log.Logger;

public class SandboxApplication extends Application {

    private static final String TAG = "DENEME";

    private static WisePlayerFactory factory;

    public static WisePlayerFactory getWisePlayerFactory() {
        if (factory != null) {
            return factory;
        } else
            return null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Start the Dynamic Ability SDK.
        try {
            FeatureCompat.install(base);
        } catch (Exception e) {
            Log.w("DynamicAbility", "", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Pass the device ID to the setDeviceId method.
        WisePlayerFactoryOptions factoryOptions = new WisePlayerFactoryOptions.Builder().setDeviceId("xxx").build();

        // In the multi-process scenario, the onCreate method in Application is called multiple times.
        // The app needs to call the WisePlayerFactory.initFactory() API in the onCreate method of the app process (named "app package name") and WisePlayer process (named "app package name:player").
        WisePlayerFactory.initFactory(this, factoryOptions, new InitFactoryCallback() {
            @Override
            public void onSuccess(WisePlayerFactory wisePlayerFactory) {
                Logger.d(TAG, "onSuccess wisePlayerFactory:" + wisePlayerFactory);
                factory = wisePlayerFactory;
            }

            @Override
            public void onFailure(int errorCode, String msg) {
                Logger.e(TAG, "onFailure errorcode:" + errorCode + " reason:" + msg);
            }
        });
        NetworkKit.init(
                getApplicationContext(),
                new NetworkKit.Callback() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            Log.i(TAG, "init success");
                        } else {
                            Log.i(TAG, "init failed");
                        }
                    }
                });
    }
}
