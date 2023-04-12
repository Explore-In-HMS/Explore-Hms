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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSegmentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImageSegmentationActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ImageSegmentationActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_STORAGE_FOR_CUT_OUT = 1;
    private static final int PERMISSION_CODE_STORAGE_FOR_SEGMENTATION = 2;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM = 3;
    String[] permissionRequestCameraAndStorageForStream = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_image_segmentation);
        unbinder = ButterKnife.bind(this);
        setupToolbar();
    }

    @OnClick({R.id.btn_seg_cutOut, R.id.btn_seg_segmentation, R.id.btn_seg_substitution})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_seg_cutOut:
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_CUT_OUT);
                break;
            case R.id.btn_seg_segmentation:
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SEGMENTATION);
                break;
            case R.id.btn_seg_substitution:
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForStream, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM);
                break;
            default:
                Log.i(TAG, "Default");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_is));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_CUT_OUT) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.startActivity(ImageSegmentationActivity.this, SegmentationWithCutOutOfObjectsActivity.class);
            } else {
                DialogUtils.showDialogPermissionWarning(this,
                        String.valueOf(R.string.storage_permission),
                        String.valueOf(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        String.valueOf(R.string.without_storage_permission),
                        String.valueOf(R.string.yes_go), String.valueOf(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_SEGMENTATION) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Utils.startActivity(ImageSegmentationActivity.this, SegmentationWithColorOfObjectsActivity.class);
            } else {
                DialogUtils.showDialogPermissionWarning(this,
                        String.valueOf(R.string.camera_and_storage_permission),
                        String.valueOf(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        String.valueOf(R.string.without_camera_and_storage_permission),
                        String.valueOf(R.string.yes_go), String.valueOf(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.startActivity(ImageSegmentationActivity.this, ImageSegmentationCameraActivity.class);
            } else {
                DialogUtils.showDialogPermissionWarning(this,
                        String.valueOf(R.string.camera_and_storage_permission),
                        String.valueOf(R.string.permission_settings_allow),
                        R.drawable.icons_switch_camera_black,
                        String.valueOf(R.string.without_camera_and_storage_permission),
                        String.valueOf(R.string.yes_go), String.valueOf(R.string.cancel));
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}