/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hms.explorehms.huawei.ui.mediaexport.utils;

import com.hms.explorehms.huawei.ui.common.utils.ReflectionUtils;

import java.lang.reflect.Method;

public class SystemPropertiesInvokeUtil {
    public static boolean getBoolean(final String key, final boolean def) {
        Method getMethod =
            ReflectionUtils.getMethod("android.os.SystemProperties", "getBoolean", String.class, boolean.class);
        Object object = ReflectionUtils.invoke(getMethod, null, key, def);
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue();
        }

        return def;
    }

    public static int getInt(String key, int def) {
        Method getIntMethod =
            ReflectionUtils.getMethod("android.os.SystemProperties", "getInt", String.class, int.class);
        Object object = ReflectionUtils.invoke(getIntMethod, null, key, def);
        if (object instanceof Integer) {
            return ((Integer) object).intValue();
        }

        return def;
    }

    public static String getString(String key, String def) {
        Method getMethod = ReflectionUtils.getMethod("android.os.SystemProperties", "get", String.class, String.class);
        Object object = ReflectionUtils.invoke(getMethod, null, key, def);
        if (object instanceof String) {
            return (String) object;
        }
        return def;
    }
}
