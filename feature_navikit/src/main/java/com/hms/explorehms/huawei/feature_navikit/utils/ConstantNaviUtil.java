package com.hms.explorehms.huawei.feature_navikit.utils;

import android.content.Context;

import com.huawei.agconnect.config.AGConnectServicesConfig;


public class ConstantNaviUtil {

    public String API_KEY = "";

    public ConstantNaviUtil(Context context) {
        API_KEY = AGConnectServicesConfig.fromContext(context).getString("client/api_key");
    }
}
