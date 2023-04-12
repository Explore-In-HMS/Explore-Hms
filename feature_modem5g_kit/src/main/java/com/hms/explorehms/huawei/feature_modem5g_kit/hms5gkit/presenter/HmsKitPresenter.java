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

package com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.presenter;

import android.content.Context;
import android.util.Log;

import com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.activitys.IHmsKitActivity;
import com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.utils.ConnectHmsKitUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class HmsKitPresenter {
    private static final String TAG = "[5ghmskit] HmsKitPresenter";
    private static final long SLEEP_TIME = 100L;

    private ConnectHmsKitUtils mHmsKitUtils = ConnectHmsKitUtils.getInstance();
    private WeakReference<IHmsKitActivity> mHmsKitActivity;
    private Context mContext;

    public HmsKitPresenter(Context context, IHmsKitActivity hmsKitActivity) {
        mContext = context;
        mHmsKitActivity = new WeakReference<>(hmsKitActivity);
        ConnectHmsKitUtils.getInstance().setHmsKitActivity(hmsKitActivity);
    }

    public boolean getConnectStatus() {
        return mHmsKitUtils.getConnectStatus();
    }

    // register
    public void registerCallback() {
        Log.i(TAG, "registerCallback");
        boolean flag = mHmsKitUtils.registerCallback(mContext);
        if (flag) {
            Log.i(TAG, "registerCallback success");
        } else {
            Log.i(TAG, "registerCallback failed");
        }
    }

    public void unRegisterCallback() {
        Log.i(TAG, "unRegisterCallback");
        mHmsKitUtils.unRegisterCallback();
        if (mHmsKitActivity != null && mHmsKitActivity.get() != null) {
            mHmsKitActivity.get().showUnRegisterResult();
        }
    }

    public void queryModem(List<String> selected) {
        Log.i(TAG, "queryModem start");
        new Thread(() -> {
            for (String queryItem : selected) {
                if (!mHmsKitUtils.queryModem(queryItem)) {
                    Log.e(TAG, queryItem + " query modem failed.");
                } else {
                    Log.i(TAG, queryItem + " query modem success.");
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mHmsKitActivity != null && mHmsKitActivity.get() != null) {
                mHmsKitActivity.get().showQueryResult();
            }
        }).start();
    }

    public void enable(List<String> selected) {
        Log.i(TAG, "enableEvent start");
        new Thread(() -> {
            for (String enableEventItem : selected) {
                if (!mHmsKitUtils.enable(enableEventItem)) {
                    Log.e(TAG, enableEventItem + " event enable failed.");
                } else {
                    Log.i(TAG, enableEventItem + " event enable success.");
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mHmsKitActivity != null && mHmsKitActivity.get() != null) {
                mHmsKitActivity.get().showQueryResult();
            }
        }).start();
    }


    public void disable(List<String> selected) {
        Log.i(TAG, "disableEvent start");
        new Thread(() -> {
            for (String disableEventItem : selected) {
                if (!mHmsKitUtils.disable(disableEventItem)) {
                    Log.e(TAG, disableEventItem + " event disable failed.");
                } else {
                    Log.i(TAG, disableEventItem + " event disable success.");
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mHmsKitActivity != null && mHmsKitActivity.get() != null) {
                mHmsKitActivity.get().showQueryResult();
            }
        }).start();
    }

}
