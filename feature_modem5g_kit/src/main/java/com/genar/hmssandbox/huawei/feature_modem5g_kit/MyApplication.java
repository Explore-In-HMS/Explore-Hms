/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context gHmsTestAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        gHmsTestAppContext = getApplicationContext();
    }

    public static Context getContext() {
        return gHmsTestAppContext;
    }
}
