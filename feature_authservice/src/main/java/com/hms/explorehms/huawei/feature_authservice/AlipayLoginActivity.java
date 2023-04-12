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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.api.AGConnectApi;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlipayLoginActivity extends AppCompatActivity {

    private static final String TAG = AlipayLoginActivity.class.getSimpleName();
    private Unbinder unbinder;

    private Activity activity;

    private Boolean isLogIn = false;
    private TextView tv_alipay_introduction;

    private static final int TV_PROFILE_DETAILS = R.id.tvProfileDetails;

    @Nullable
    @BindView(TV_PROFILE_DETAILS)
    TextView tvProfileDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //First settings before setContentView
        activity = this;

        setContentView(R.layout.activity_alipay_login);

        //First settings after setContentView
        unbinder = ButterKnife.bind(this);
        Utils.initializeAGConnectInstance(getApplicationContext());


        setupToolbar();
        setOptionAGC();

        initViews();
        initListeners();

    }

    private void initViews(){
        tv_alipay_introduction = findViewById(R.id.tv_alipay_login_introduction);
    }
    private void initListeners(){
        tv_alipay_introduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://developer.huawei.com/consumer/jp/doc/development/AppGallery-connect-Guides/agc-auth-android-alipay-0000001412658170"));
                startActivity(intent);
            }
        });
    }

    /**
     * Set parameters required for users to sign in with an Alipay account.
     * To do so, you need to call an API to set the parameters.
     */
    public void setOptionAGC(){
       /* AGConnectApi.getInstance().getOptions().setOption("/alipay/app_id", "your alipay app_id");
        AGConnectApi.getInstance().getOptions().setOption("/alipay/app_name", "your alipay app_name");
        AGConnectApi.getInstance().getOptions().setOption("/alipay/pid", "your alipay pid");
        AGConnectApi.getInstance().getOptions().setOption("/alipay/target_id", "your alipay target_id");
        AGConnectApi.getInstance().getOptions().setOption("/alipay/sign", "your alipay sign");
        AGConnectApi.getInstance().getOptions().setOption("/alipay/sign_type", "your alipay sign_type");*/
    }

    private static final int CL_LOGIN = R.id.clLogin;
    private static final int CL_LOG_OUT = R.id.clLogout;

    @OnClick({R.id.clLogin, R.id.clLogout})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case CL_LOGIN:
                loginWithAlipay();
                break;
            case CL_LOG_OUT:
                if (isLogIn){
                    logOut();
                    isLogIn = false;
                }else{
                    Utils.showToastMessage(AlipayLoginActivity.this, getString(R.string.sign_out_failed));
                }
                break;
            default:
                Log.e(TAG, "Default case");
                break;
        }
    }

    private void logOut(){
        AGConnectAuth.getInstance().signOut();
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

    public void loginWithAlipay(){

        Toast.makeText(AlipayLoginActivity.this, getString(R.string.alipay_login_redirect), Toast.LENGTH_SHORT).show();

       /* if(tvProfileDetails == null){
            return;
        }
        AGConnectAuth.getInstance().signIn(activity, AGConnectAuthCredential.Alipay_Provider)
                .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                    @Override
                    public void onSuccess(SignInResult signInResult) {
                        isLogIn = true;
                        AGConnectUser user =  signInResult.getUser();

                        //onSuccess
                        String msg = "onSuccess : Alipay ProfileDetail : \n" +
                                "Uid      : " + user.getUid() + "\n" +
                                "Token       : " + user.getToken(true) + "\n" +
                                "DisplayName : " + user.getDisplayName();

                        tvProfileDetails.setText(msg);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        String failMsg = "onFailure : Alipay - " + e.getMessage();
                        tvProfileDetails.setText(failMsg);
                        Utils.showToastMessage(getApplicationContext(), getString(R.string.failed_alipay_login) + e.getMessage());
                    }
                });*/
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_alipay));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}