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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_hiai.utils.PermissionUtils;

public class BaseServiceActivity extends AppCompatActivity{

    protected static final String TAG = "HiAiService";

    public Context baseContext;

    private static final int PERMISSION_REQUEST = 123;
    private int serviceGroup;

    private boolean askPermission = true;

    public BaseServiceActivity(int serviceGroup){
        this.serviceGroup = serviceGroup;
    }

    public void checkPermissions(){

        if(serviceGroup == ServiceGroupConstants.FACIAL)
            requestPermission(PermissionUtils.FACIAL);
        else if(serviceGroup == ServiceGroupConstants.IMAGE)
            requestPermission(PermissionUtils.IMAGE);
        else if(serviceGroup == ServiceGroupConstants.CODE)
            requestPermission(PermissionUtils.CODE);
        else if(serviceGroup == ServiceGroupConstants.VIDEO)
            requestPermission(PermissionUtils.VIDEO);
        else if(serviceGroup == ServiceGroupConstants.TEXT)
            requestPermission(PermissionUtils.TEXT);
        else if(serviceGroup == ServiceGroupConstants.BODY)
            requestPermission(PermissionUtils.BODY);
    }

    private void requestPermission(String[] permissions){
        for(String permission : permissions){
            if(baseContext.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED ){
                requestPermissions(permissions,PERMISSION_REQUEST);
            }
        }
    }

    public void showImageAlert(){
        Toast.makeText(this.baseContext,"Please select image from gallery or take picture",Toast.LENGTH_LONG).show();
    }

    public void showVideoAlert(){
        Toast.makeText(this.baseContext,"Please select video from gallery of take record video",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST && grantResults.length > 0) {
            for (int permission : grantResults) {
                if (permission == PackageManager.PERMISSION_DENIED) {
                    DialogUtils.permissionDeniedDialog(this.baseContext,
                            (dialog, which) -> {
                                dialog.dismiss();
                                checkPermissions();
                            },
                            (dialog, which) -> finish());
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(askPermission){
            checkPermissions();
            askPermission = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
