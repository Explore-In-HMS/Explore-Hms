package com.hms.explorehms.huawei.feature_navikit.utils;

import com.hms.explorehms.huawei.feature_navikit.R;
import com.huawei.hms.navi.navibase.MapNavi;
import com.huawei.hms.navi.navibase.model.DevServerSiteConstant;


public class CommonUtil {
    public static void changeBusServerSite(int checkedId) {
        switch (checkedId) {
            case R.id.bus_dr1:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
            case R.id.bus_dr2:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                break;
            case R.id.bus_dr3:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                break;
            case R.id.bus_dr4:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                break;
            default:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
        }
    }

    public static void changeServerSite(int checkedId) {
        switch (checkedId) {
            case R.id.dr1:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
            case R.id.dr2:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR2);
                break;
            case R.id.dr3:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR3);
                break;
            case R.id.dr4:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR4);
                break;
            default:
                MapNavi.setDevServerSite(DevServerSiteConstant.DR1);
                break;
        }
    }
}

