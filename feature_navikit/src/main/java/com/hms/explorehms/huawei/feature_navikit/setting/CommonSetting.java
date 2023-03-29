package com.hms.explorehms.huawei.feature_navikit.setting;

public class CommonSetting {
    private static String serverSite = "China";

    public static String getServerSite() {
        return serverSite;
    }

    public static void setServerSite(String serverSite) {
        CommonSetting.serverSite = serverSite;
    }
}
