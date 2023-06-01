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

package com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.utils;

import android.content.Context;
import android.util.Log;

import com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.activitys.IHmsKitActivity;
import com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.activitys.common.TranJson;
import com.huawei.hms5gkit.agentservice.constants.parameters.ModemSlice;
import com.huawei.hms5gkit.agentservice.constants.parameters.NetDiagnosis;
import com.huawei.hms5gkit.agentservice.controller.IConnectProcess;
import com.huawei.hms5gkit.agentservice.controller.IQueryModem;
import com.huawei.hms5gkit.agentservice.controller.IResProcess;
import com.huawei.hms5gkit.agentservice.controller.impl.QueryModemController;

import org.json.JSONException;

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

    public TranJson tranJson = new TranJson();
    private IResProcess mResProcess = response -> {
        if (response != null && response.getCode() == 0) {
            String key = response.getQueryParameters();
            String data = response.getValue();
            String content = TimeStampUtils.getCurDateStr() + " ";
            if (data != null) {
                if(key.equals(NetDiagnosis.NET) || key.equals(NetDiagnosis.NET_LTE_INFO) || key.equals(NetDiagnosis.NET_NR_INFO) ||
                        key.equals(NetDiagnosis.NET_LTE_REJ_CNT) || key.equals(NetDiagnosis.NET_LTE_REJ_INFOS ) || key.equals(NetDiagnosis.NET_LTE_PDN_REJ_CNT) ||
                        key.equals(NetDiagnosis.NET_LTE_PDN_REJ_INFOS) || key.equals(NetDiagnosis.NET_NR_REJ_CNT) || key.equals(NetDiagnosis.NET_NR_REJ_INFO) ||
                        key.equals(NetDiagnosis.NET_NR_PDU_REJ_CNT) || key.equals(NetDiagnosis.NET_NR_PDU_REJ_INFO)
                ){                                               //需要转换时间戳的消息
                    tranJson.setParamAndJsonStr(key,data);
                    queryModem(ModemSlice.MODEM_SLICE_CURTIMESTAMP);
                }else if(key.equals(ModemSlice.MODEM_SLICE_CURTIMESTAMP)){
                    try {
                        tranJson.setModemSlice(data);
                        String tmp = tranJson.TimeTran();
                        content += tranJson.getParamName() + " test request result: \"" + tmp + "\"";
                        Log.i(TAG, content);
                        mHmsKitActivity.showDataResult(content);
                        tranJson.clean();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, content);
                        mHmsKitActivity.showDataResult(content + "ModemSlice时间戳解析失败:" + e.toString());
                    }
                }
                else{
                    content += key + " request result: \"" + data + "\"";
                    Log.i(TAG, content);
                    mHmsKitActivity.showDataResult(content);
                }
            } else {
                content += key + " request result is null";
            }
            Log.i(TAG, content);
            mHmsKitActivity.showDataResult(content);
        }    else if (response != null && response.getCode() == 200) {  //约定周期上报响应码为200
            String data = response.getValue();
            String content = TimeStampUtils.getCurDateStr() + " ";
            if (data != null) {
                content += " NR failure event result: \"" + data + "\"";
            } else {
                content += " NR failure event result is null";
            }
            Log.i(TAG, content);
            mHmsKitActivity.showDataResult(content);
        }else {
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

    public boolean disable(String category) { return mQueryModem.disable(category);}

    public boolean enable(String category) { return mQueryModem.enable(category);}
}
