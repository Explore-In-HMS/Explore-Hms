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
package com.genar.hmssandbox.huawei.feature_accountkit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.CredentialManager;
import com.genar.hmssandbox.huawei.Util;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//Round 5 update
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGN_IN_LOGIN = 140;
    private static final int REQUEST_SIGN_IN_LOGIN_CODE = 141;
    private static final int REQUEST_SIGN_IN_AUTH_CODE = 142;

    //To specify the signature algorithm type for the ID token (2: RS256  1:PS256)
    private static final int SIGNATURE_ALGORITHM_TYPE = 2;

    @BindView(R.id.btn_signin_idtoken)
    Button btnSigninIdtoken;

    @BindView(R.id.btn_signin_authcode)
    Button btnSigninAuthcode;
    @BindView(R.id.btn_signin_viasms)
    Button btnSigninViaSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupToolbar();

        try {
            silentSignIn();
        } catch (Exception e) {
            Log.e(TAG, "silentSignIn: " + e.getMessage());
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_accountkit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_doc_accountkit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_signin_idtoken)
    /**
     * Opens a Huawei ID Authorization page to Sign In.
     */
    public void signInWithHuaweiId(View view) {

        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE); // Basic account permissions.

        //Old method
        /*HuaweiIdAuthParams mHuaweiIdAuthParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setIdToken()
                .setScopeList(scopeList)
                .createParams();
        CredentialManager.setHuaweiIdAuthService(HuaweiIdAuthManager.getService(LoginActivity.this, mHuaweiIdAuthParams));
        startActivityForResult(CredentialManager.getHuaweiIdAuthService().getSignInIntent(), REQUEST_SIGN_IN_LOGIN);*/

        AccountAuthParams accountAuthParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setIdTokenSignAlg(SIGNATURE_ALGORITHM_TYPE)
                .setIdToken()
                .setScopeList(scopeList)
                .setCarrierId()
                .createParams();
        AccountAuthService accountAuthService = AccountAuthManager.getService(LoginActivity.this, accountAuthParams);
        startActivityForResult(accountAuthService.getSignInIntent(),REQUEST_SIGN_IN_LOGIN);

    }

    @OnClick(R.id.btn_signin_authcode)
    public void signInWithAuthCode() {
        //Old way
        /*HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setEmail()
                .setAuthorizationCode().createParams();
        CredentialManager.setHuaweiIdAuthService(HuaweiIdAuthManager.getService(LoginActivity.this, authParams));
        startActivityForResult(CredentialManager.getHuaweiIdAuthService().getSignInIntent(), REQUEST_SIGN_IN_AUTH_CODE);*/

        AccountAuthParams authParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAuthorizationCode()
                .setIdTokenSignAlg(SIGNATURE_ALGORITHM_TYPE)
                .setCarrierId()
                .createParams();
        AccountAuthService accountAuthService = AccountAuthManager.getService(LoginActivity.this,authParams);
        startActivityForResult(accountAuthService.getSignInIntent(),REQUEST_SIGN_IN_AUTH_CODE);
    }

    @OnClick(R.id.btn_signin_silent)
    /**
     * Sign in method without requiring a user action. Works only if authorization is not revoked.
     */
    public void silentSignIn() {
        /*HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setIdToken()
                .createParams();
        CredentialManager.setHuaweiIdAuthService(HuaweiIdAuthManager.getService(LoginActivity.this, authParams));

        Task<AuthHuaweiId> task = CredentialManager.getHuaweiIdAuthService().silentSignIn(); */

        AccountAuthParams accountAuthParams = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdTokenSignAlg(SIGNATURE_ALGORITHM_TYPE)
                .setCarrierId()
                .createParams();

        AccountAuthService accountAuthService = AccountAuthManager.getService(LoginActivity.this,accountAuthParams);
        Task<AuthAccount>task = accountAuthService.silentSignIn();

        final Activity activity = this;

        task.addOnSuccessListener(authHuaweiId -> {
            //CredentialManager.setCredentials(authHuaweiId);
            Toast.makeText(activity, "Silent SignIn Successfull", Toast.LENGTH_SHORT).show();
            //activity.finish();
            //Util.startActivity(activity, UserDetailActivity.class);
            // Obtain HUAWEI ID information.
            Log.i("DEBUG", "displayName:" + authHuaweiId.getDisplayName());
            Log.i("CARRIER ID: ", String.valueOf(authHuaweiId.getCarrierId()));
        });

        task.addOnFailureListener(e -> {
            Toast.makeText(activity, "Silent Sign In Failed", Toast.LENGTH_SHORT).show();
            // Obtain HUAWEI ID information.
            Log.i("ERROR", "Silent Sign In Failed");
        });
    }

    @OnClick(R.id.btn_signin_viasms)
    public void signInViaSms() {
        Intent intent=new Intent(this,SmsReaderInfoActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);

        switch (requestCode) {
            case REQUEST_SIGN_IN_LOGIN:

            case REQUEST_SIGN_IN_LOGIN_CODE:

            case REQUEST_SIGN_IN_AUTH_CODE:

                if (authHuaweiIdTask.isSuccessful()) {
                    AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();

                    CredentialManager.setCredentials(huaweiAccount);

                    StringBuilder sb = new StringBuilder();
                    sb.append("Welcome back ");
                    sb.append(CredentialManager.getUserName());

                    Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();

                    finish();
                    Util.startActivity(this, UserDetailActivity.class);
                    Log.i("DEBUG", "signIn success " + huaweiAccount.getDisplayName());
                    Log.i("DEBUG", "signIn success " + huaweiAccount.getCarrierId());

                } else {
                    Toast.makeText(this, "Error ->" + "signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode(), Toast.LENGTH_SHORT).show();
                    Log.i("ERROR ->", "signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
                }
                break;
            default: // default state
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }


}
