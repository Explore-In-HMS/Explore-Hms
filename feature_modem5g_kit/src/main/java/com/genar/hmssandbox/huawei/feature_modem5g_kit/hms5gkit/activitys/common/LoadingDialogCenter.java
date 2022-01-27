/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.common;

import android.content.Context;

public class LoadingDialogCenter {
    private static volatile LoadingDialogCenter sInstance = null;

    private LoadingDialog mLoadingDialog;

    public static LoadingDialogCenter getInstance() {
        if (sInstance == null) {
            synchronized (LoadingDialogCenter.class) {
                if (sInstance == null) {
                    sInstance = new LoadingDialogCenter();
                }
            }
        }
        return sInstance;
    }

    public void showLoadingDialog(Context context, String message) {
        dismissLoadingDialog();

        mLoadingDialog = new LoadingDialog(context);
        mLoadingDialog.setMessage(message).show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}
