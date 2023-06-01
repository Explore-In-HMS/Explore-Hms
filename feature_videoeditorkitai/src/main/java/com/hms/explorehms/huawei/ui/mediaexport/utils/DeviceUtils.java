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

import android.text.TextUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DeviceUtils {
    private static final Pattern PATTERN = Pattern.compile("sm\\d+");

    public static String getDeviceModel() {
        return SystemPropertiesInvokeUtil.getString("ro.product.model", "");
    }

    public static String getCPUModel() {
        String cpuModel = SystemPropertiesInvokeUtil.getString("ro.product.vendor.device", "");
        if (TextUtils.isEmpty(cpuModel)) {
            cpuModel = SystemPropertiesInvokeUtil.getString("ro.board.platform", "");
        }
        if ("lahaina".equalsIgnoreCase(cpuModel)) {
            String chipsetVersion = SystemPropertiesInvokeUtil.getString("ro.comp.chipset_version", "");
            if (TextUtils.isEmpty(chipsetVersion)) {
                return cpuModel;
            }

            Matcher matcher = PATTERN.matcher(chipsetVersion.toLowerCase(Locale.ENGLISH));
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return cpuModel;
    }
}
