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

package com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.activitys.common;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.hms.explorehms.huawei.feature_modem5g_kit.R;


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
