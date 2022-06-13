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

import com.genar.hmssandbox.huawei.baseapp.util.DatabaseAppUtils;
import com.genar.hmssandbox.huawei.baseapp.util.DatabaseMaterialAppUtils;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.network.NetworkKit;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.LogConfigInfo;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

public class SandboxApplication extends Application {

    private static final String TAG = "DENEME";

    public static SandboxApplication app;

    private static WisePlayerFactory wisePlayerFactory = null;


    @Override
    public void onCreate() {
        super.onCreate();
        HuaweiMobileServicesUtil.setApplication(this);
        DatabaseAppUtils.initDatabase(this);
        DatabaseMaterialAppUtils.initDatabase(this);
        app = this;
        initAutoSize();
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
        initPlayer();
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

    private void initAutoSize() {
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
    }

    private void initPlayer() {
        // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
        WisePlayerFactoryOptionsExt.Builder factoryOptions =
                new WisePlayerFactoryOptionsExt.Builder().setDeviceId("xxx");
        LogConfigInfo logCfgInfo =
                new LogConfigInfo(Constants.LEVEL_DEBUG, "", Constants.LOG_FILE_NUM, Constants.LOG_FILE_SIZE);
        factoryOptions.setLogConfigInfo(logCfgInfo);
        WisePlayerFactory.initFactory(this, factoryOptions.build(), initFactoryCallback);
    }

    /**
     * Player initialization callback
     */
    private static final InitFactoryCallback initFactoryCallback = new InitFactoryCallback() {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            Log.i(TAG, "init player factory success");
            setWisePlayerFactory(wisePlayerFactory);
        }

        @Override
        public void onFailure(int errorCode, String reason) {
            Log.w(TAG, "init player factory fail reason :" + reason + ", errorCode is " + errorCode);
        }
    };

    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }

    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        SandboxApplication.wisePlayerFactory = wisePlayerFactory;
    }


}
