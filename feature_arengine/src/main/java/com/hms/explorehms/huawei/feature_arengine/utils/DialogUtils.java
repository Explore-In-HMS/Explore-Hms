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
package com.hms.explorehms.huawei.feature_arengine.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.huawei.feature_arengine.R;

public class DialogUtils {

    private DialogUtils(){
        //Constructor method
    }
    public static void permissionDeniedDialog(Context context, DialogInterface.OnClickListener positive ){

        AlertDialog permissionDialog = new AlertDialog.Builder(context, R.style.AlertDialogAREngine).create();
        permissionDialog.setIcon(R.drawable.icon_warn_arengine);
        permissionDialog.setTitle("Please Allow Permissions");
        permissionDialog.setMessage("Please allow permissions to use AR Engine services");
        permissionDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK", positive);
        permissionDialog.setCancelable(false);

        Window view=((AlertDialog)permissionDialog).getWindow();
        if(view != null){
            view.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorWhiteArEngine)));
            view.setBackgroundDrawableResource(R.drawable.view_border_gray_arengine);
        }
        permissionDialog.show();
    }

    public static void warningDialog(Context context){

        AlertDialog permissionDialog = new AlertDialog.Builder(context, R.style.AlertDialogAREngine).create();
        permissionDialog.setIcon(R.drawable.icon_warn_arengine);
        permissionDialog.setTitle("Maintenance");
        permissionDialog.setMessage("Feature is currently under maintenance");
        permissionDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        permissionDialog.setCancelable(false);

        Window view=((AlertDialog)permissionDialog).getWindow();
        if(view != null){
            view.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorWhiteArEngine)));
            view.setBackgroundDrawableResource(R.drawable.view_border_gray_arengine);
        }
        permissionDialog.show();
    }
}
