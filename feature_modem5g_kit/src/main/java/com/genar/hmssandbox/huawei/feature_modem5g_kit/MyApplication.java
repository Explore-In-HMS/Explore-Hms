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
