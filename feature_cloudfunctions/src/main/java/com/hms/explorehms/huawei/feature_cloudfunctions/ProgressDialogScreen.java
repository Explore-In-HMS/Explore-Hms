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

package com.hms.explorehms.huawei.feature_cloudfunctions;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

/**
 * It handles Progress Dialog to show user progress of function.
 */
public class ProgressDialogScreen {

    private Dialog dialog;

    /**
     * It initializes dialog by Dialog
     */
    public ProgressDialogScreen(Context context) {

        dialog = new Dialog(context, android.R.style.Theme_Black);

        View view = LayoutInflater.from(context).inflate(R.layout.item_loading_screen_cloudfunction, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(view);
    }

    /**
     * It shows Progress Dialog
     */
    public void showProgressDialog() {
        dialog.show();
    }

    /**
     * It dismiss Progress Dialog
     */
    public void dismissProgressDialog() {
        dialog.dismiss();
    }
}
