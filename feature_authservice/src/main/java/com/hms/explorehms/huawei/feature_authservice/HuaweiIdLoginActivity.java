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

package com.hms.explorehms.huawei.feature_authservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HuaweiIdLoginActivity extends AppCompatActivity {

    //region variablesAndObjects

    private static final String TAG = HuaweiGameLoginActivity.class.getSimpleName();

    private static final int ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID = 101;

    private Unbinder unbinder;

    @Nullable
    @BindView(value = R.id.tvProfileDetails)
    TextView tvProfileDetails;

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huawei_id_login);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        Utils.initializeAGConnectInstance(getApplicationContext());
    }


    @OnClick({R.id.clLogin, R.id.clLogout,})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.clLogin:
                if (!Utils.isLoggedInAgcUser()) {
                    loginWithHuaweiId();
                }
                break;
            case R.id.clLogout:
                logOut();
                break;
            default:
                Log.e(TAG, "Default case");
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_huawei_id));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loginWithHuaweiId() {
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        HuaweiIdAuthService authService = HuaweiIdAuthManager.getService(HuaweiIdLoginActivity.this, authParams);
        startActivityForResult(authService.getSignInIntent(), ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID);
        // another huaweiIdAuthParamsHelper method
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID) {
            if (resultCode == 0) {
                Log.w(TAG, "loginWithHuaweiId : getCurrentUser : onActivityResult No any data detected");
                Utils.showToastMessage(getApplicationContext(), "loginWithHuaweiId Cancelled and No Data!");
            } else {
                Task<AuthHuaweiId> task = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
                if (task.isSuccessful()) {
                    AuthHuaweiId huaweiAccount = task.getResult();
                    Log.i(TAG, "getCurrentUser onSuccess huaweiAccount = " + huaweiAccount.getDisplayName() + " - AccessToken = " + huaweiAccount.getAccessToken());
                    showProfileDetail(task.getResult());
                    transmitHuaweiAccountAccessTokenIntoAGC(huaweiAccount.getAccessToken());
                } else {
                    Log.e(TAG, "getCurrentUser onFailure : " + task.getException().getMessage(), task.getException());
                    Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huaweiid) + " : onFailure : " + task.getException().getMessage());
                }
            }
        }

    }

    private void transmitHuaweiAccountAccessTokenIntoAGC(String accessToken) {
        if (tvProfileDetails == null) {
            return;
        }

        try {
            AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
            AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener(signInResult -> {
                        String msg = "onSuccess : userId : " + signInResult.getUser().getUid() + " displayName : " + signInResult.getUser().getDisplayName() + " isAnonymous : " + signInResult.getUser().isAnonymous();
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huaweiid) + " : " + msg);
                        showResultDetail(tvProfileDetails.getText() + "\n\n" + getResources().getString(R.string.login_with_huaweiid), signInResult.getUser());
                    }).addOnFailureListener(e -> {
                Log.e(TAG, "loginWithHuaweiId onFailure : " + e.getMessage(), e);
                Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huaweiid) + " : onFailure : " + e.getMessage());
            });
        } catch (Exception e) {
            Log.e(TAG, "AGConnectAuth.getInstance signIn Exception : " + e.getMessage(), e);
            tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signIn Exception : %n%s", e.getMessage()));
            Utils.showToastMessage(getApplicationContext(), "AGConnectAuth.getInstance signIn Exception :\n" + e.getMessage());
        }
    }


    public void showProfileDetail(AuthHuaweiId huaweiAccount) {

        String msg = "ProfileDetail : \n" +
                "DisplayName : " + huaweiAccount.getDisplayName() + "\n" +
                "GivenName   : " + huaweiAccount.getGivenName() + "\n" +
                "FamilyName  : " + huaweiAccount.getFamilyName() + "\n" +
                "AccessToken : " + huaweiAccount.getAccessToken() + "\n" +
                "AvatarUrl   : " + huaweiAccount.getAvatarUriString() + "\n";

        Log.i(TAG, msg);

        if (tvProfileDetails == null) {
            return;
        }

        tvProfileDetails.setText(msg);
    }


    public void showResultDetail(String msg, AGConnectUser signInResult) {
        if (tvProfileDetails == null) {
            return;
        }
        String signMsg = msg + " onSuccess : \n\n" +
                "user Uid         : " + signInResult.getUid() + "\n" +
                "user ProviderId  : " + signInResult.getProviderId() + "\n" +
                "user DisplayName : " + signInResult.getDisplayName() + "\n";

        Log.i(TAG, signMsg);
        tvProfileDetails.setText(signMsg);
    }

    private void logOut() {
        if (tvProfileDetails == null) {
            return;
        }

        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();
                tvProfileDetails.setText(getString(R.string.txt_message_for_instance_user_to_log_out));
                Log.d(TAG, "InstanceUser logged out!");
                Utils.showToastMessage(getApplicationContext(), "InstanceUser logged out.");
            } catch (Exception e) {
                Log.e(TAG, "AGConnectAuth.getInstance signOut Exception : " + e.getMessage(), e);
                tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signOut Exception :%n%s", e.getMessage()));
            }
        } else {
            Log.w(TAG, "logOut : No logged in user");
            tvProfileDetails.setText(getString(R.string.txt_message_for_no_logged_user));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
        unbinder.unbind();
    }

}