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
package com.hms.explorehms.huawei.feature_accountkit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.CredentialManager;
import com.hms.explorehms.Util;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * It shows the user profile information, such as Username, Email and Id Token
 * It allows to user log out and revoke authorization.
 */
public class UserDetailActivity extends AppCompatActivity {
    private AccountAuthService mAuthManager;
    private AccountAuthParams mAuthParam;
    private static final int SIGNATURE_ALGORITHM_TYPE = 2;

    @BindView(R.id.detail_profile_image)
    CircleImageView profileImage;
    @BindView(R.id.detail_txt_username)
    TextView username;
    @BindView(R.id.detail_txt_name)
    TextView name;
    @BindView(R.id.detail_txt_idtoken)
    TextView idtoken;
    @BindView(R.id.detail_txt_email)
    TextView email;
    @BindView(R.id.detail_btn_logOut)
    Button btnLogOut;
    @BindView(R.id.detail_btn_revoke)
    Button btnRevoke;

    /**
     * The method initializes the sets up necessary for variables.
     * It also load user profile image by using Picasso library.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        setupToolbar();

        Picasso.get()
                .load(CredentialManager.getProfilePic())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.login)
                .into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // onSuccessState
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(CredentialManager.getProfilePic())
                                .placeholder(R.drawable.login)
                                .error(R.drawable.profile_image)
                                .into(profileImage);
                    }
                });
        name.setText(CredentialManager.getFullname());
        username.setText(CredentialManager.getDisplaName());
        idtoken.setText(CredentialManager.getIDToken());
        email.setText(CredentialManager.getEmail());


    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Called when the user presses the "back" button in the toolbar.
     * It handles the behavior for navigation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * It allows to user log out.
     * It also finishes this activity after log out and showing a toast message to user.
     */
    @OnClick(R.id.detail_btn_logOut)
    public void logOut() {
        List<Scope> scopeList = new ArrayList<>();
        scopeList.add(HuaweiIdAuthAPIManager.HUAWEIID_BASE_SCOPE);
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setIdTokenSignAlg(SIGNATURE_ALGORITHM_TYPE)
                .setIdToken()
                .setScopeList(scopeList)
                .setCarrierId()
                .createParams();
        mAuthManager = AccountAuthManager.getService(UserDetailActivity.this, mAuthParam);
        Task<Void> signOutTask = mAuthManager.signOut();

        Activity activity = this;
        signOutTask.addOnCompleteListener(task -> {
            Toast.makeText(activity, "Sign Out Successfull", Toast.LENGTH_SHORT).show();
            CredentialManager.clearAuthorization();
            activity.finish();
            Log.i("DEBUG", "signOut complete");
        });
    }

    /**
     * It allows to user revoke authorization.
     * When the user signs out, his token is still valid. It uses this function to revoke it.
     */
    @OnClick(R.id.detail_btn_revoke)
    public void revoke() {
        Activity activity = this;
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setProfile()
                .setAuthorizationCode()
                .createParams();
        mAuthManager = AccountAuthManager.getService(UserDetailActivity.this, mAuthParam);
        mAuthManager.cancelAuthorization().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    //do some thing while cancel success
                    CredentialManager.clearAuthorization();
                    activity.finish();
                    Util.startActivity(activity, LoginActivity.class);
                    Log.i("DEBUG", "onSuccess: ");
                } else {
                    //do some thing while cancel success
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        int statusCode = ((ApiException) exception).getStatusCode();
                        Log.i("ERROR", "onFailure: " + statusCode);
                    }
                }
            }
        });
    }
}