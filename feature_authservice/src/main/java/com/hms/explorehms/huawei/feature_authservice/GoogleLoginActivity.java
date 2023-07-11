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

package com.hms.explorehms.huawei.feature_authservice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.hms.explorehms.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.GoogleAuthProvider;

/**
 * This shows how we login with Google Sign In with Auth Service.
 */
public class GoogleLoginActivity extends AppCompatActivity {
    protected AGConnectAuth auth;
    private static final int SIGN_CODE = 9901;
    private GoogleSignInClient client;
    private ConstraintLayout loginBtn;
    private ConstraintLayout logoutBtn;

    private TextView tvProfileDetails;

    private static final String TAG = GoogleLoginActivity.class.getSimpleName();

    /**
     * The method initializes the sets up necessary for variables.
     * It also initializes Google requirements.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        tvProfileDetails = findViewById(R.id.tvProfileDetails);

        auth = AGConnectAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            showResultDetail(auth.getCurrentUser());
        }

        GoogleSignInOptions options =
                new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.server_client_id))
                        .build();
        client = GoogleSignIn.getClient(this, options);


        loginBtn = findViewById(R.id.clLogin);
        logoutBtn = findViewById(R.id.clLogout);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGooglePlayServicesAvailable(getApplicationContext())) {
                    login();
                } else {
                    showSnackbarIfGmsNotSupported();
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    /**
     * It starts to login with Google
     */
    public void login() {
        startActivityForResult(client.getSignInIntent(), SIGN_CODE);
    }

    /**
     * It allows to user log out.
     */
    public void logout() {
        client.signOut();
        if (tvProfileDetails == null) {
            return;
        }
        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();
                tvProfileDetails.setText(getString(R.string.txt_message_for_instance_user_to_log_out));
                Log.d(TAG, getString(R.string.log_out_instance_user));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.log_out_instance_user));
            } catch (Exception e) {
                Log.e(TAG, "AGConnectAuth.getInstance signOut Exception : " + e.getMessage(), e);
                tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signOut Exception :%n%s", e.getMessage()));
            }
        } else {
            Log.w("TAG", getString(R.string.log_out_user));
            tvProfileDetails.setText(getString(R.string.txt_message_for_no_logged_user));
        }
    }

    /**
     * It handles sign in requests.
     * If the sign in gives an error, a toast message will on the screen.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_CODE) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(googleSignInAccount -> {
                        // create google credential
                        AGConnectAuthCredential credential = GoogleAuthProvider.credentialWithToken(googleSignInAccount.getIdToken());
                        // signIn with google credential
                        auth.signIn(credential)
                                .addOnSuccessListener(
                                        signInResult -> {
                                            showResultDetail(auth.getCurrentUser());
                                            loginSuccess();
                                        }
                                )
                                .addOnFailureListener(e -> Utils.showToastMessage(this, e.getMessage()));
                    })
                    .addOnFailureListener(e -> Utils.showToastMessage(this, e.getMessage()));
        }
    }

    /**
     * If the login successfully, it shows a toast message
     */
    protected void loginSuccess() {
        Utils.showToastMessage(this, getString(R.string.success_login_with_google));
        Log.d(TAG, getString(R.string.success_login_with_google));
    }

    /**
     * It displays the details of sign-in user's information, including a message.
     */
    public void showResultDetail(AGConnectUser signInResult) {
        if (tvProfileDetails == null) {
            return;
        }
        String signMsg = "Google " + " onSuccess : \n" +
                "profile Uid         : " + signInResult.getUid() + "\n" +
                "profile ProviderId  : " + signInResult.getProviderId() + "\n" +
                "profile DisplayName : " + signInResult.getDisplayName();

        Log.i(TAG, signMsg);
        tvProfileDetails.setText(signMsg);
    }

    /**
     * The Google Sign In need to Google SDK
     * Here It returns whether the phone has the Google SDK.
     * In this way, It informs the user if the Google SDK is not available.
     */
    public boolean isGooglePlayServicesAvailable(final Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    /**
     * If the user does not have the Google SDK, it shows a snackbar that the user cannot using Google Sign In.
     */
    public void showSnackbarIfGmsNotSupported() {
        View rootOfLayout = findViewById(R.id.clGoogleLogin);
        Snackbar snackbar = Snackbar
                .make(rootOfLayout, R.string.you_need_gms_supported_device, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}