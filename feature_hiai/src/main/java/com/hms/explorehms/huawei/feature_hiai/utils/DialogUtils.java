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
package com.hms.explorehms.huawei.feature_hiai.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.hms.explorehms.huawei.feature_hiai.R;

public class DialogUtils {

    public static void createInfoDialog(Context context, String infoTitle, String info) {

        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogHiAi).create();
        alertDialog.setIcon(R.drawable.icon_info_hiai);
        alertDialog.setTitle(infoTitle);
        alertDialog.setMessage(info);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        Window view = alertDialog.getWindow();
        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view.setBackgroundDrawableResource(R.drawable.view_border_gray_hiai);

        alertDialog.show();
    }


    public static void processorNotOKDialog(Context context) {

        AlertDialog processorDialog = new AlertDialog.Builder(context, R.style.AlertDialogHiAi).create();
        processorDialog.setIcon(R.drawable.icon_warn_hiai);
        processorDialog.setTitle("Incompatible Processor");
        processorDialog.setMessage("- Device processor is not compatible for this service. \n\n- HUAWEI HiAI Engine supports only mobile phones using the Kirin 970 or 990 chip at present.");
        processorDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> dialog.dismiss());
        processorDialog.setCancelable(false);

        Window view = processorDialog.getWindow();
        if (view != null) {
            view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            view.setBackgroundDrawableResource(R.drawable.view_border_gray_hiai);
        }
        processorDialog.show();
    }

    public static void permissionDeniedDialog(Context context, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {

        AlertDialog permissionDialog = new AlertDialog.Builder(context, R.style.AlertDialogHiAi).create();
        permissionDialog.setIcon(R.drawable.icon_warn_hiai);
        permissionDialog.setTitle("Please Allow Permissions");
        permissionDialog.setMessage("Please allow permissions to use HiAi services");
        permissionDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", positive);
        permissionDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "BACK TO MAIN", negative);
        permissionDialog.setCancelable(false);

        Window view = permissionDialog.getWindow();
        if (view != null) {
            view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            view.setBackgroundDrawableResource(R.drawable.view_border_gray_hiai);
        }
        permissionDialog.show();
    }
}
