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

package com.hms.explorehms.huawei.feature_cameraengine.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.hms.explorehms.huawei.feature_cameraengine.R;

public class DialogUtils {

    public static void permissionDeniedDialog(Context context, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        AlertDialog permissionDialog = new AlertDialog.Builder(context, R.style.AlertDialogCameraEngine).create();
        permissionDialog.setIcon(R.drawable.icon_warn_camera_engine);
        permissionDialog.setTitle("Please Allow Permissions");
        permissionDialog.setMessage("Please allow all permissions to use Camera Engine");
        permissionDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", positive);
        permissionDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "BACK TO MAIN", negative);
        permissionDialog.setCancelable(false);

        Window view = permissionDialog.getWindow();
        if (view != null) {
            view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            view.setBackgroundDrawableResource(R.drawable.view_light_border_light_gray_camera_engine);
        }
        permissionDialog.show();
    }

}
