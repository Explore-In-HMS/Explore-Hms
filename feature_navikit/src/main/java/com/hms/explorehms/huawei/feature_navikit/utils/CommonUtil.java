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

