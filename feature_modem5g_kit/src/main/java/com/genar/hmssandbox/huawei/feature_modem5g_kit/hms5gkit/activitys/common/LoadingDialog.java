/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.common;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.genar.hmssandbox.huawei.feature_modem5g_kit.R;


public class LoadingDialog extends Dialog {
    private TextView loadingTv;

    LoadingDialog(Context context) {
        super(context);

        setContentView(R.layout.dialog_loading);
        loadingTv = findViewById(R.id.loading_tv);
        setCanceledOnTouchOutside(false);
    }

    /**
     * Set different prompt messages for the loading progress dialog
     *
     * @param message Prompt information displayed to users
     * @return build Mode design, you can chain call
     */
    public LoadingDialog setMessage(String message) {
        loadingTv.setText(message);
        return this;
    }
}
