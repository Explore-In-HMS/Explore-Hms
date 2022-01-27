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

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.WindowManager;

/**
 * Mobile data acquisition
 */
public class DeviceUtil {
    private static final float FLOAT_VALUE = 0.5f;

    /**
     * Get the resolution of the mobile phone from the dp value converted to the px
     *
     * @param context Context
     * @param dpValue Dp value
     * @return Px value
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + FLOAT_VALUE);
    }

    /**
     * Whether the vertical screen
     *
     * @param context Context
     * @return boolean Whether the vertical screen
     */
    public static boolean isPortrait(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Whether the full screen
     * @param activity activity
     * @return boolean Whether the full screen
     */
    public static boolean isFullScreen(Activity activity) {
        if (activity == null) {
            return false;
        }
        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)== WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        } else {
            return false;
        }
    }
}
