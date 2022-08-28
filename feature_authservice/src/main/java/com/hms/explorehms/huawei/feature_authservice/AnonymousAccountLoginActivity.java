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
import com.huawei.agconnect.auth.AGConnectUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AnonymousAccountLoginActivity extends AppCompatActivity {

    //region variablesAndObjects

    private static final String TAG = AnonymousAccountLoginActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int TV_PROFILE_DETAILS = R.id.tvProfileDetails;

    @Nullable
    @BindView(TV_PROFILE_DETAILS)
    TextView tvProfileDetails;

    //endregion views


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_account_login);
        setupToolbar();

        unbinder = ButterKnife.bind(this);

        Utils.initializeAGConnectInstance(getApplicationContext());
    }

    private static final int BTN_CL_LOGIN = R.id.clLogin;
    private static final int BTN_CL_LOGOUT = R.id.clLogout;

    @OnClick({R.id.clLogin, R.id.clLogout})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case BTN_CL_LOGIN:
                if (!Utils.isLoggedInAgcUser()) {
                    loginWithAnonymously();
                }
                break;
            case BTN_CL_LOGOUT:
                logOut();
                break;
            default:
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_anonymous));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loginWithAnonymously() {
        try {
            AGConnectAuth.getInstance().signInAnonymously()
                    .addOnSuccessListener(signInResult -> {
                        AGConnectUser user = signInResult.getUser();
                        Log.i(TAG, "loginWithAnonymously onSuccess : userId : " + user.getUid() + " displayName : " + user.getDisplayName() + " isAnonymous : " + user.isAnonymous());
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_anonymously) + " : onSuccess : userId : " + user.getUid() + " displayName : " + user.getDisplayName() + " isAnonymous : " + user.isAnonymous());
                        showResultDetail("SignIn with Anonymously", signInResult.getUser());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "loginWithAnonymously onFailure : " + e.getMessage(), e);
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_anonymously) + " : onFailure : " + e.getMessage());
                    });

        } catch (Exception e) {
            if (tvProfileDetails != null) {
                String resultMessage = "AGConnectAuth.getInstance signIn Exception : " + e.getMessage();
                tvProfileDetails.setText(resultMessage);
            }
            Log.e(TAG, "AGConnectAuth.getInstance signIn Exception : " + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), "AGConnectAuth.getInstance signIn Exception :\n" + e.getMessage());
        }
    }

    public void showResultDetail(String msg, AGConnectUser signInResult) {
        String signMsg = msg + " onSuccess : \n\n" +
                "user uid         : " + signInResult.getUid() + "\n" +
                "user email       : " + signInResult.getEmail() + "\n" +
                "user providerId  : " + signInResult.getProviderId() + "\n";
        Log.i(TAG, signMsg);
        if (tvProfileDetails != null) {
            tvProfileDetails.setText(signMsg);
        }
    }

    private void logOut() {
        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();
                if (tvProfileDetails != null) {
                    tvProfileDetails.setText(getString(R.string.txt_for_profile_details_log_out_message));
                }
                Log.d(TAG, getString(R.string.txt_for_profile_details_log_out_message));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.txt_for_profile_details_log_out_message));
            } catch (Exception e) {
                String resultMessage = "AGConnectAuth.getInstance signOut Exception :\n" + e.getMessage();
                Log.e(TAG, resultMessage + e.getMessage(), e);

                if (tvProfileDetails != null) {
                    tvProfileDetails.setText(resultMessage);
                }
            }
        } else {
            Log.w(TAG, "logOut : No logged in user");
            if (tvProfileDetails != null) {
                tvProfileDetails.setText(getString(R.string.txt_for_tv_profile_detail_log_out_message));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
        unbinder.unbind();
    }
}