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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Permission control
 */
public class PermissionUtils {
    /**
     * Have permission
     * 
     * @param context Context
     * @param permissionValue permission
     * @return Have permission
     */
    @SuppressLint("WrongConstant")
    public static boolean checkPermission(Context context, String permissionValue) {
        if (!isMNC()) {
            return true;
        }

        if (StringUtil.isEmpty(permissionValue)) {
            return false;
        }

        if (PermissionChecker.checkSelfPermission(context, permissionValue) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Is M version
     *
     * @return Is M version
     */
    public static boolean isMNC() {
        return Build.VERSION.SDK_INT >= 23;
    }

    /**
     * Apply for permission
     *
     * @param activity Activity
     * @param permissions Permission
     * @param code Code
     * @return Have permission
     */
    public static boolean requestPermissionsIfNeed(Activity activity, String[] permissions, int code) {
        if (activity == null || permissions == null) {
            return false;
        }
        List<String> requestList = new ArrayList<>();
        for (String permission : permissions) {
            boolean request = !checkPermission(activity, permission);
            if (request) {
                requestList.add(permission);
            }
        }
        if (requestList.size() > 0) {
            if (isMNC()) {
                activity.requestPermissions(requestList.toArray(new String[requestList.size()]), code);
            }
            return true;
        }
        return false;
    }
}
