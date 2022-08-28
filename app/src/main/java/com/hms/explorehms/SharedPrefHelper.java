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

package com.hms.explorehms;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {

    private static final String PREF_HELPER = "HMSExploreHMS_Prefs";
    private static SharedPrefHelper sharedPrefHelper;
    private static SharedPreferences sharedPreferences;


    public static SharedPrefHelper getSharedPref(Activity activity) {
        if (sharedPreferences == null) {
            syncInit(activity);
        }
        return sharedPrefHelper;
    }

    private static synchronized void syncInit(Activity activity) {
        if (sharedPreferences == null) {
            sharedPreferences = activity.getSharedPreferences(PREF_HELPER, Context.MODE_PRIVATE);
            sharedPrefHelper = new SharedPrefHelper();
        }
    }

    public void saveBoolean(SharedPrefKey key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key.toString(), value);
        editor.apply();
    }

    public boolean getBoolean(SharedPrefKey key) {
        return sharedPreferences.getBoolean(key.toString(), false);
    }
}
