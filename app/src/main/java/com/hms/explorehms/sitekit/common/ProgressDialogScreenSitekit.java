/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.hms.explorehms.sitekit.common;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.hms.explorehms.R;

public class ProgressDialogScreenSitekit {
    private Dialog dialog;

    public ProgressDialogScreenSitekit(Context context) {

        dialog = new Dialog(context, android.R.style.Theme_Black);

        View view = LayoutInflater.from(context).inflate(R.layout.item_loading_screen_sitekit, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setContentView(view);
    }

    public void showProgressDialog() {
        dialog.show();
    }

    public void dismissProgressDialog() {
        dialog.dismiss();
    }
}
