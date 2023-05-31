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

package com.hms.explorehms.huawei.feature_drivekit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.CredentialManager;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_drivekit.model.DriveHelper;
import com.hms.explorehms.locationkit.Utils;
import com.huawei.cloud.base.auth.DriveCredential;
import com.huawei.cloud.base.util.StringUtils;
import com.huawei.cloud.client.exception.DriveCode;
import com.huawei.cloud.services.drive.DriveScopes;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.util.ArrayList;
import java.util.List;

import static com.huawei.hms.support.hwid.request.HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM;

public class DriveKitLoginActivity extends AppCompatActivity {

    private static final String TAG = "DriveKit";
    private static final int REQUEST_SIGN_IN_LOGIN = 4531;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private final DriveCredential.AccessMethod refreshAT = new DriveCredential.AccessMethod() {
        @Override
        public String refreshToken() {
            return CredentialManager.getAccessToken();
        }
    };
    AuthHuaweiId huaweiAccount;
    List<Scope> scopeList = new ArrayList<>();
    Button btnLogin;
    TextView txtFail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_kit_login);
        setupToolbar();

        initView();
        huaweiAccount = DriveHelper.mHuaweiAccount;
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.drive_link_doc));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_drive_login_auth);
        txtFail = findViewById(R.id.txt_drive_fail_tips);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS_STORAGE, PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    private void driveLogin() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_FILE));// Permissions to view and manage files.
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_APPDATA)); // Permissions to upload and store app data.
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE); // Basic account permissions.
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_METADATA)); // Permissions to view and manage file metadata, excluding file content.
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_METADATA_READONLY));// Permissions to view file metadata, excluding file content.
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE));// All permissions, except permissions for the app data folder.
        scopeList.add(new Scope(DriveScopes.SCOPE_DRIVE_READONLY));// Permissions to view file metadata and content.


        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(DEFAULT_AUTH_REQUEST_PARAM)
                .setAccessToken()
                .setIdToken()
                .setUid()
//                .setScopeList(scopeList)
                .createParams();
        // Call the account API to obtain account information.
        HuaweiIdAuthService client = HuaweiIdAuthManager.getService(DriveKitLoginActivity.this, authParams);
        startActivityForResult(client.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (permissions == null || grantResults == null) {
            Log.w(TAG, "onRequestPermissionsResult : permissions == null || grantResults == null");
            return;
        }

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean perm = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    showFailText(getStringResource(R.string.drive_permission_fail));
                    perm = false;
                    break;
                }
            }

            if (perm)
                driveLogin();
        }
    }

    // Abnormal process for obtaining account information. Obtain and save the related accessToken and unionID using this function.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult, requestCode = " + requestCode + ", resultCode = " + resultCode);

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            if (resultCode == PERMISSION_REQUEST_CODE) {
                showFailText(getResources().getString(R.string.drive_permission_fail));
            }
            return;
        }

        if (requestCode == REQUEST_SIGN_IN_LOGIN) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                huaweiAccount = authHuaweiIdTask.getResult();
                CredentialManager.setCredentials(huaweiAccount);
                int returnCode = init(huaweiAccount.getUnionId(), huaweiAccount.getAccessToken(), refreshAT);
                if (DriveCode.SUCCESS == returnCode) {
                    Utils.startActivity(this, FilesActivity.class);
                    Toast.makeText(this, getStringResource(R.string.drive_login_success), Toast.LENGTH_SHORT).show();
                } else if (DriveCode.SERVICE_URL_NOT_ENABLED == returnCode) {
                    showFailText(getStringResource(R.string.drive_not_enabled));
                } else {
                    showFailText(getStringResource(R.string.drive_login_fail));
                }
            } else {
                Log.d(TAG, "onActivityResult, signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
                Toast.makeText(getApplicationContext(), "onActivityResult, signIn failed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getStringResource(@StringRes int resource) {
        return getResources().getString(resource);
    }

    private void showFailText(String failText) {
        txtFail.setText(failText);
        Toast.makeText(this, getStringResource(R.string.Error), Toast.LENGTH_SHORT).show();
    }

    /**
     * Initialize Drive based on the context and HUAWEI ID information including unionId, countrycode, and accessToken.
     * When the current accessToken expires, register an AccessMethod and obtain a new accessToken.
     *
     * @param unionID   from HwID
     * @param at        access token
     * @param refreshAT a callback to refresh AT
     */
    public int init(String unionID, String at, DriveCredential.AccessMethod refreshAT) {

        if (StringUtils.isNullOrEmpty(unionID) || StringUtils.isNullOrEmpty(at)) {
            return DriveCode.ERROR;
        } else {
            for (Scope s : huaweiAccount.getRequestedScopes()) {
                if (!huaweiAccount.getAuthorizedScopes().contains(s)) {
                    return DriveCode.ERROR;
                }
            }
            DriveCredential.Builder builder = new DriveCredential.Builder(unionID, refreshAT);
            DriveHelper.mCredential = builder.build().setAccessToken(at);
            return DriveCode.SUCCESS;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}