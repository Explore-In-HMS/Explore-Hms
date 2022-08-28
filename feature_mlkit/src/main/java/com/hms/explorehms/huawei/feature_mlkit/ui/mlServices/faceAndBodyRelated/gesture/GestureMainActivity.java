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

package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.gesture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GestureMainActivity extends AppCompatActivity {
    private static final String TAG = GestureMainActivity.class.getSimpleName();
    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    String[] permissionRequestCamera = {Manifest.permission.CAMERA};
    private Unbinder unbinder;
    private static final int PERMISSION_CODE_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_main);
        unbinder = ButterKnife.bind(this);
        setupToolbar();

    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @OnClick({R.id.btn_gestureWithImage,R.id.btn_gestureCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gestureWithImage:
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case R.id.btn_gestureCameraStream:
                ActivityCompat.requestPermissions(this, permissionRequestCamera, PERMISSION_CODE_CAMERA);
                break;
            default:
                Log.i(TAG, "Default");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> Start Intent FaceDetectionActivity");
                Utils.startActivity(GestureMainActivity.this, GestureActivity.class);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icons_switch_camera_black,
                        "You can not use FaceDetection with Stream without Camera Permission!",
                        "YES GO", "CANCEL");
            }
        }
        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> Start Intent FaceDetectionActivity");
                final Intent intent = new Intent(GestureMainActivity.this, GestureImageActivity.class);
                intent.putExtra("model_type", "Cloud Classification");
                intent.putExtra("picture_type", "select image");
                GestureMainActivity.this.startActivity(intent);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icons_switch_camera_black,
                        "You can not use FaceDetection with Stream without Camera Permission!",
                        "YES GO", "CANCEL");
            }
        }

    }
    }
