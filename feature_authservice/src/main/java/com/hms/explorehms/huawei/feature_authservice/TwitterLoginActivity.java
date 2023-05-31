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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.api.AGConnectApi;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.auth.TwitterAuthParam;
import com.huawei.agconnect.auth.TwitterAuthProvider;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.agconnect.remoteconfig.ConfigValues;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * This shows how we login with Twitter with Auth Service.
 */
public class TwitterLoginActivity extends AppCompatActivity {

    //unified sign in variables


    //region variablesAndObjects
    private static final String REMOTE_ERR = "REMOTE_ERR";
    private AGConnectConfig remoteConfig;
    private String twitterApiSecret;
    private String twitterApiKey;
    private static final String TAG = TwitterLoginActivity.class.getSimpleName();
    private static final String TWITTER_LOGIN_ON_FAILURE = " : onFailure : ";
    private static final String ESCAPE_STRING = "\n" + "...\n" + "\n";
    private static final String AGC_TWITTER_SIGN_IN_EXCEPTION = "AGConnectAuth.getInstance signIn Exception : ";

    private TwitterAuthClient twitterAuthClient;
    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @Nullable
    @BindView(R.id.tvProfileDetails)
    TextView tvProfileDetails;
    private ConstraintLayout mBtnTwitter;

    //endregion views
    String authCode = "";

    /**
     * The method initializes the sets up necessary for variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // important to call before setContentView

        setContentView(R.layout.activity_twitter_login);

        unbinder = ButterKnife.bind(this);
        setupToolbar();
        Utils.initializeAGConnectInstance(getApplicationContext());
        setRemoteConfigurationSettings();

        AGConnectApi.getInstance().getOptions().setOption("/twitter/client_id", "<your twitter client id>");
        AGConnectApi.getInstance().getOptions().setOption("/twitter/redirect_url", "<your redirect url>");

        //initTwitterOAuh20Button(); This method can work after Twitter OAuth 2.0 fixed for mobile devices.
    }

    private void initTwitterOAuh20Button(){

      /*  mBtnTwitter = findViewById(R.id.clLoginOAuth20);
        mBtnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
                if (user != null) {

                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://twitter.com/i/oauth2/authorize?client_id=TVpJazZmYVF5YXFjZjlLZU8xUjM6MTpjaQ&response_type=code&scope=tweet.read%20users.read%20offline.access&state=state&redirect_uri=https%3A%2F%2F99536292102546105.auth.agconnect.link&code_challenge_method=plain&code_challenge=challenge"));
                startActivity(intent);

                Log.e("thread-twitter", Thread.currentThread().getName());



            }
        });*/

    }
    //After getting authCode from Twitter authorization on based OAuth 2.0 this method will publish
    private void loginWithTwitterOAuthTwo(String authCodeTwitter) {

        /*AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if (user != null) {

        }

        TwitterAuthParam param = new TwitterAuthParam("<Client id>", authCodeTwitter, "challenge", "<redirect url>");
        AGConnectAuthCredential credential = TwitterAuthProvider.credentialWithAuthCode(param, true);
        AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        // onSuccess
                        AGConnectUser user = signInResult.getUser();
                        Log.e("TWITTER OAUTH2.0", "onSuccess");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // onFail
                        Log.e("TWITTER OAUTH2.0", "onFailure : " + e.getMessage());
                    }
                });*/

    }

    /**
     * It allows to set configuration settings remotely
     */
    private void setRemoteConfigurationSettings() {
        remoteConfig = AGConnectConfig.getInstance();
        remoteConfig.applyDefault(R.xml.remote_config);


        //Interval time can be changed.default values is 12 hours
        remoteConfig.fetch(0).addOnSuccessListener(new OnSuccessListener<ConfigValues>() {
            @Override
            public void onSuccess(ConfigValues configValues) {
                remoteConfig.apply(configValues);
                obtainData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(REMOTE_ERR, e.toString());
            }
        });
    }

    /**
     * It allows to obtain data from remote
     */
    private void obtainData() {

        Map<String, Object> allValues = remoteConfig.getMergedAll();
        if (allValues != null && allValues.size() > 0) {
            // ArrayList<RemoteResult> allResultFromRemote = new ArrayList<>();

            Set set = allValues.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                System.out.println(entry.getKey() + " " + entry.getValue());
                if (entry.getKey().toString().equalsIgnoreCase("twitter_api_secret")) {
                    twitterApiSecret = entry.getValue().toString();
                }
                if (entry.getKey().toString().equalsIgnoreCase("twitter_api_key")) {
                    twitterApiKey = entry.getValue().toString();
                }
                // allResultFromRemote.add(new RemoteResult(entry.getKey().toString(), entry.getValue().toString()));
            }
            initializeTwitterApi();

        } else {
            Toast.makeText(getApplicationContext(), "There is no value on Cloud", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.clLogin, R.id.clLogout})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.clLogin:
                loginWithTwitter();
                break;
            case R.id.clLogout:
                logOut();
                break;
            default:
                Log.e(TAG, "Default case");
                break;
        }
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_twitter));
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
     * It initialize the Twitter Api
     */
    private void initializeTwitterApi() {
        try {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(
                    twitterApiKey,
                    twitterApiSecret);

            TwitterConfig twitterConfig = new TwitterConfig.Builder(TwitterLoginActivity.this)
                    .logger(new DefaultLogger(Log.DEBUG))
                    .debug(true)
                    .twitterAuthConfig(authConfig)
                    .build();
            Twitter.initialize(twitterConfig);

            twitterAuthClient = new TwitterAuthClient();

        } catch (Exception e) {
            Log.e(TAG, "initializeTwitterApi : " + e.getMessage(), e);
        }
    }

    /**
     * It allows to login with Twitter SDK
     * It also sends message
     */
    private void loginWithTwitter() {
        if (tvProfileDetails == null) {
            return;
        }

        try {
            twitterAuthClient.authorize(TwitterLoginActivity.this, new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {

                    Log.d(TAG, "loginWithTwitter : onSuccess : user : " + result.data.getUserId() + " " + result.data.getUserName() + " token :" + result.data.getAuthToken().toString());

                    String msg = "onSuccess : Twitter ProfileDetail : \n" +
                            "UserId    : " + result.data.getUserId() + "\n" +
                            "UserName  : " + result.data.getUserName() + "\n" +
                            "AuthToken : " + result.data.getAuthToken().toString();

                    tvProfileDetails.setText(msg);

                    String token = result.data.getAuthToken().token;
                    String secret = result.data.getAuthToken().secret;
                    AGConnectAuthCredential credential = TwitterAuthProvider.credentialWithToken(token, secret);

                    transmitTwitterCredentialIntoAGC(credential);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.e(TAG, "loginWithTwitter onFailure : " + e.getMessage(), e);
                    Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_twitter) + TWITTER_LOGIN_ON_FAILURE + getResources().getString(R.string.login_with_twitter_download));
                    tvProfileDetails.setText(String.format("%s%s%s%s%s", tvProfileDetails.getText(), ESCAPE_STRING, getResources().getString(R.string.login_with_twitter), TWITTER_LOGIN_ON_FAILURE, getResources().getString(R.string.login_with_twitter_download)));
                }
            });
        } catch (Exception ex) {
            Utils.showToastMessage(this, "Twitter Application not Found!" + TWITTER_LOGIN_ON_FAILURE + ex.getMessage());
        }
    }

    /**
     * It generates a credential using the obtained access token, and then call AGConnectAuth.
     */
    private void transmitTwitterCredentialIntoAGC(AGConnectAuthCredential credential) {
        if (tvProfileDetails == null) {
            return;
        }

        try {
            AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener(signInResult -> {
                        Log.d(TAG, getResources().getString(R.string.login_with_twitter) + " : onSuccess : signInResult : " + signInResult.toString());
                        String msg = "onSuccess : userId : " + signInResult.getUser().getUid() + " displayName : " + signInResult.getUser().getDisplayName() + " isAnonymous : " + signInResult.getUser().isAnonymous();
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_twitter) + " : " + msg);
                        showResultDetail(tvProfileDetails.getText() + " \n\n" + getResources().getString(R.string.login_with_twitter), signInResult.getUser());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "loginWithTwitter onFailure : " + e.getMessage(), e);
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_twitter) + TWITTER_LOGIN_ON_FAILURE + e.getMessage());
                        tvProfileDetails.setText(String.format("%s%sAGConnectAuth.getInstance signIn onFailure : %s", tvProfileDetails.getText(), ESCAPE_STRING, e.getMessage()));
                    });

        } catch (Exception e) {
            Log.e(TAG, AGC_TWITTER_SIGN_IN_EXCEPTION + e.getMessage(), e);
            tvProfileDetails.setText(String.format("%s%s%s%s", tvProfileDetails.getText(), ESCAPE_STRING, AGC_TWITTER_SIGN_IN_EXCEPTION, e.getMessage()));
            Utils.showToastMessage(getApplicationContext(), "AGConnectAuth.getInstance signIn Exception :\n" + e.getMessage());
            tvProfileDetails.setText(String.format("%s%s%s%s", tvProfileDetails.getText(), ESCAPE_STRING, AGC_TWITTER_SIGN_IN_EXCEPTION, e.getMessage()));
        }
    }

    /**
     * It displays the details of sign-in user's information, including a message.
     */
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

    /**
     * It allows to user log out.
     */
    private void logOut() {
        twitterAuthClient.cancelAuthorize();

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

    /**
     * It handles sign in requests.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Twitter SDK
        twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "twitterAuthClient.onActivityResult");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
        unbinder.unbind();
    }


}