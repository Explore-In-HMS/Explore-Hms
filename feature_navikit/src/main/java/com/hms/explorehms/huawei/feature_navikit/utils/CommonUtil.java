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

package com.hms.explorehms.huawei.feature_navikit.utils;

import com.hms.explorehms.huawei.feature_navikit.R;
import com.hms.explorehms.huawei.feature_navikit.setting.CommonSetting;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;


public class CommonUtil {
    public static void changeServerSite(int checkedId, MapNavi mapNavi) {
        if (mapNavi == null) {
            return;
        }

        switch (checkedId) {
            case R.id.dr2:
            case R.id.bus_dr2:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                CommonSetting.setServerSite(DevServerSiteConstant.DR2);
                break;
            case R.id.dr3:
            case R.id.bus_dr3:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                CommonSetting.setServerSite(DevServerSiteConstant.DR3);
                break;
            case R.id.dr4:
            case R.id.bus_dr4:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                CommonSetting.setServerSite(DevServerSiteConstant.DR4);
                break;
            default:
                mapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                CommonSetting.setServerSite(DevServerSiteConstant.DR1);
                break;
        }
    }
}

