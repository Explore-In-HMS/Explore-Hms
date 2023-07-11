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

package com.hms.explorehms.huawei.ui.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hms.explorehms.huawei.feature_videoeditorkitai.R;

public class ToastUtils {

    private static ToastUtils utils = null;

    private Toast toast = null;

    private Handler handler = new Handler();

    public static ToastUtils getInstance() {
        synchronized (ToastUtils.class) {
            if (utils == null) {
                utils = new ToastUtils();
            }
        }
        return utils;
    }

    @SuppressLint({"ShowToast", "UseCompatLoadingForDrawables"})
    public void showToast(Context context, CharSequence text, int duration) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(context.getApplicationContext(), text, duration);

        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        View view = toast.getView();
        if (view != null) {
            view.setBackgroundColor(Color.TRANSPARENT);
            TextView textView = view.findViewById(tvToastId);
            if (textView != null) {
                textView.setBackground(context.getDrawable(R.drawable.bg_toast_show));
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(context.getResources().getColor(R.color.clip_color_E6FFFFFF));
                textView.setPadding(SizeUtils.dp2Px(context, 16), SizeUtils.dp2Px(context, 8), SizeUtils.dp2Px(context, 16),
                        SizeUtils.dp2Px(context, 8));
            }
        }
        toast.setGravity(Gravity.CENTER, 0, -SizeUtils.dp2Px(context, 30));

        handler.postDelayed(() -> {
            toast.show();
        }, 200);
    }
}
