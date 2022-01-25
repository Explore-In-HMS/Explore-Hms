/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.utils;

import android.content.Context;
import android.util.Log;

import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.IHmsKitActivity;
import com.huawei.hms5gkit.agentservice.controller.IConnectProcess;
import com.huawei.hms5gkit.agentservice.controller.IQueryModem;
import com.huawei.hms5gkit.agentservice.controller.IResProcess;
import com.huawei.hms5gkit.agentservice.controller.impl.QueryModemController;

public class ConnectHmsKitUtils {
    private static final String TAG = "[5ghmskit] ConnectHmsKitUtils";

    private ConnectHmsKitUtils() {
    }

    private IHmsKitActivity mHmsKitActivity;

    public void setHmsKitActivity(IHmsKitActivity hmsKitActivity) {
        mHmsKitActivity = hmsKitActivity;
    }

    private volatile static ConnectHmsKitUtils connectHmsKitUtils;

    public static ConnectHmsKitUtils getInstance() {
        if (connectHmsKitUtils == null) {
            synchronized (ConnectHmsKitUtils.class) {
                if (connectHmsKitUtils == null) {
                    connectHmsKitUtils = new ConnectHmsKitUtils();
                }
            }
        }
        return connectHmsKitUtils;
    }

    private IResProcess mResProcess = response -> {
        if (response != null && response.getCode() == 0) {
            String key = response.getQueryParameters();
            String data = response.getValue();
            String content = TimeStampUtils.getCurDateStr() + " ";
            if (data != null) {
                content += key + " request result: \"" + data + "\"";
            } else {
                content += key + " request result is null";
            }
            Log.i(TAG, content);
            mHmsKitActivity.showDataResult(content);
        } else {
            if (response != null) {
                String content = TimeStampUtils.getCurDateStr() + " error code: "
                        + response.getCode() + ",\t" + response.getMsg();
                Log.e(TAG, content);
                mHmsKitActivity.showDataResult(content);
            } else {
                Log.e(TAG, "response is null");
            }
        }
    };

    private IConnectProcess mConnectProcess = response -> {
        if (response == null) {
            Log.e(TAG, "ConnectProcess callback response data is null");
            return;
        }
        String content;
        if (response.getCode() != 0) {
            content = TimeStampUtils.getCurDateStr() + " connect error code: " + response.getCode() +
                    ", error msg: " + response.getMsg();
            Log.e(TAG, content);
        } else {
            content = TimeStampUtils.getCurDateStr() + " connect code: " + response.getCode() +
                    ", msg: " + response.getMsg();
            Log.i(TAG, content);
        }
        mHmsKitActivity.showDataResult(content);
    };

    private IQueryModem mQueryModem = QueryModemController.getInstance();

    public boolean registerCallback(Context context) {
        return mQueryModem.registerCallback(context, mResProcess, mConnectProcess);
    }

    public boolean getConnectStatus() {
        return mQueryModem.getAidlConnectStatus();
    }

    public boolean queryModem(String requestName) {
        // For input parameters, please refer to the Lte, Nr and Bearer classes
        // under the com.huawei.hms5gkit.agentservice.constants.parameters package in 5G Kit SDK
        // Lte
        // Nr
        // Bearer
        return mQueryModem.queryModem(requestName);
    }

    public void unRegisterCallback() {
        mQueryModem.unRegisterCallback();
    }
}
