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

package com.hms.explorehms.huawei.ui.mediaeditor.menu;

import android.text.TextUtils;

import java.util.List;

public class DeviceModelConfigBean {
    private List<DeviceModel> deviceModels;

    public String getDeviceModels(String deviceType) {
        if (deviceModels == null || deviceModels.isEmpty()) {
            return null;
        }
        if (TextUtils.isEmpty(deviceType)) {
            return null;
        }
        StringBuilder models = new StringBuilder();

        for (DeviceModel deviceModel : deviceModels) {
            if (deviceModel == null) {
                continue;
            }
            if (deviceType.equals(deviceModel.getKey().trim())) {
                models.append(deviceModel.getValue());
            }
        }
        return models.toString();
    }
}
