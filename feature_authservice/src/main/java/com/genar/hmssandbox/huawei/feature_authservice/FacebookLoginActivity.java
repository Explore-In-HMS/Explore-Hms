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

package com.genar.hmssandbox.huawei.feature_authservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.FacebookAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.huawei.agconnect.auth.AGConnectAuthCredential.Facebook_Provider;

public class FacebookLoginActivity extends AppCompatActivity {

    //region variablesAndObjects

    private static final String TAG = FacebookLoginActivity.class.getSimpleName();

    private CallbackManager facebookCallbackManager = CallbackManager.Factory.create();

    private Unbinder unbinder;

    private Activity activity;

    private static final int TV_PROFILE_DETAILS = R.id.tvProfileDetails;

    @Nullable
    @BindView(TV_PROFILE_DETAILS)
    TextView tvProfileDetails;

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // important to call before setContentView
        FacebookSdk.sdkInitialize(getApplicationContext());
        activity=this;

        setContentView(R.layout.activity_facebook_login);
        setupToolbar();

        unbinder = ButterKnife.bind(this);

        Utils.initializeAGConnectInstance(getApplicationContext());

        facebookCallbackManager = CallbackManager.Factory.create();

        /*
         * Note :
         * Don't forget facebook manifest declaration meta-data and facebook activity
         * and app information strings facebook_app_id, facebook_app_secret
         */
    }

    private static final int CL_LOGIN = R.id.clLogin;
    private static final int CL_LOG_OUT = R.id.clLogout;

    @OnClick({R.id.clLogin, R.id.clLogout})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case CL_LOGIN:
                loginWithFacebook();
                break;
            case CL_LOG_OUT:
                logOut();
                break;
            default:
                Log.e(TAG,"Default case");
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar,  getResources().getString(R.string.url_auth_service_facebook));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loginWithFacebook() {
        if(tvProfileDetails == null){
            return;
        }
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String token = loginResult.getAccessToken().getToken();

                String msg = "onSuccess : Facebook ProfileDetail : \n" +
                        "UserId      : " + loginResult.getAccessToken().getUserId() + "\n" +
                        "Token       : " + loginResult.getAccessToken().getToken() + "\n" +
                        "LastRefresh : " + loginResult.getAccessToken().getLastRefresh();

                tvProfileDetails.setText(msg);

                transmitFacebookAccessTokenIntoAGC(token);

                AGConnectAuth.getInstance().getCurrentUser().link(activity, Facebook_Provider)
                        .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                            @Override
                            public void onSuccess(SignInResult signInResult) {
                                // onSuccess
                                AGConnectUser user = signInResult.getUser();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.d(TAG, "Failed to link account" + " : onCancel");
                            }
                        });
            }

            @Override
            public void onCancel() {
                Log.d(TAG, getResources().getString(R.string.login_with_facebook) + " : onCancel");
                Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_facebook) + " : onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                if(tvProfileDetails == null){
                    return;
                }
                Log.e(TAG, getResources().getString(R.string.login_with_facebook) + " : onError : " + error.getMessage(), error);
                Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_facebook) + " :  onError : " + error.getMessage());
                tvProfileDetails.setText(String.format("%s\n...\n%s onFailure : %s", tvProfileDetails.getText(), getResources().getString(R.string.login_with_facebook), error.getMessage()));
            }
        });
    }

    private void transmitFacebookAccessTokenIntoAGC(String accessToken) {
        if(tvProfileDetails == null){
            return;
        }
        try {
            AGConnectAuthCredential credential = FacebookAuthProvider.credentialWithToken(accessToken);
            AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener(signInResult -> {
                        Log.d(TAG, getResources().getString(R.string.login_with_facebook) + " : onSuccess : signInResult : " + signInResult.toString());
                        showResultDetail(tvProfileDetails.getText() + " \n\n" + getResources().getString(R.string.login_with_facebook), signInResult.getUser());
                        String msg = "onSuccess : userId : " + signInResult.getUser().getUid() + " displayName : " + signInResult.getUser().getDisplayName() + " isAnonymous : " + signInResult.getUser().isAnonymous();
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_facebook) + " : " + msg);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "loginWithFacebook onFailure : " + e.getMessage(), e);
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_facebook) + " : onFailure : " + e.getMessage());
                        tvProfileDetails.setText(String.format("%s\n...\n\nAGConnectAuth.getInstance signIn onFailure : %s", tvProfileDetails.getText(), e.getMessage()));
                    });

        } catch (Exception e) {
            Log.e(TAG, "FacebookAccessTokenIntoAGC AGConnectAuth.getInstance signIn Exception : " + e.getMessage(), e);
            tvProfileDetails.setText(String.format("%s\n...\n\nAGConnectAuth.getInstance signIn Exception : \n%s", tvProfileDetails.getText(), e.getMessage()));
            Utils.showToastMessage(getApplicationContext(), "FacebookAccessTokenIntoAGC \nAGConnectAuth.getInstance signIn Exception :\n" + e.getMessage());
        }
    }


    public void showResultDetail(String msg, AGConnectUser signInResult) {
        if(tvProfileDetails == null){
            return;
        }
        String signMsg = "\n\nAGConnectAuth " + msg + " onSuccess : \n" +
                "profile Uid         : " + signInResult.getUid() + "\n" +
                "profile ProviderId  : " + signInResult.getProviderId() + "\n" +
                "profile DisplayName : " + signInResult.getDisplayName() + "\n" +
                "profile Email       : " + signInResult.getEmail() + "\n" +
                "profile Phone       : " + signInResult.getPhone() + "\n";

        Log.i(TAG, signMsg);
        tvProfileDetails.setText(signMsg);
    }


    private void logOut() {

        LoginManager.getInstance().logOut();

        if (tvProfileDetails == null) {
            return;
        }

        tvProfileDetails.setText(getString(R.string.txt_for_facebook_login_message));

        if (Utils.isLoggedInAgcUser()) {
            try{
                AGConnectAuth.getInstance().signOut();
                String logOutResultMessage = "InstanceUser logged out.";
                tvProfileDetails.setText(logOutResultMessage);
                Log.d(TAG, logOutResultMessage);
                Utils.showToastMessage(getApplicationContext(),logOutResultMessage );
            }catch (Exception e){
                String resultTvProfileDetailsText = "AGConnectAuth.getInstance signOut Exception :\n" + e.getMessage();
                Log.e(TAG, resultTvProfileDetailsText, e);
                tvProfileDetails.setText(resultTvProfileDetailsText);
            }
        }else{
            Log.w(TAG, "logOut : "+ getString(R.string.txt_for_logged_user_message));
            tvProfileDetails.setText(getString(R.string.txt_for_logged_user_message));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
        unbinder.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}