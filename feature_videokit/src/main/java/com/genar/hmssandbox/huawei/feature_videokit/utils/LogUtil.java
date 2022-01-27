/**
 * Copyright 2020. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.genar.hmssandbox.huawei.feature_videokit.utils;

import android.util.Log;

/**
 * Log tools
 */
public class LogUtil {
    private static final String TAG = "videoKitDemo";

    public static final void d(String tag, String msg) {
        Log.d(getTag(tag), msg);
    }

    public static final void d(String msg) {
        Log.d(getTag(""), msg);
    }

    public static final void i(String tag, String msg) {
        Log.i(getTag(tag), msg);
    }

    public static final void i(String msg) {
        Log.i(getTag(""), msg);
    }

    public static final void w(String tag, String msg) {
        Log.w(getTag(tag), msg);
    }

    public static final void e(String tag, String msg) {
        Log.e(getTag(tag), msg);
    }

    private static String getTag(String tag) {
        if (StringUtil.isEmpty(tag)) {
            return TAG;
        } else {
            return TAG + ":" + tag;
        }
    }
}
