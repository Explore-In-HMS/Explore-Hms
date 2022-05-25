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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_authservice.util.Utils;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivityAuth extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = MainActivityAuth.class.getSimpleName();

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.tvSupports)
    TextView tvSupports;

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        Utils.initializeAGConnectInstance(getApplicationContext());

    }


    @OnClick({R.id.clLoginWithAnonymous, R.id.clLoginWithPhone, R.id.clLoginWithMail,
            R.id.clLoginWithHuaweiId, R.id.clLoginWithHuaweiGame,
            R.id.clLoginWithGoogle2, R.id.clLoginWithGooglePlay2,
            R.id.clLoginWithFacebook, R.id.clLoginWithTwitter, R.id.reauthenticate,
            R.id.clLoginWithWeChat, R.id.clLoginWithQQ, R.id.clLoginWithWeibo,
            R.id.clLoginWithVk})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.clLoginWithAnonymous:
                Utils.startActivity(MainActivityAuth.this, AnonymousAccountLoginActivity.class);
                break;
            case R.id.clLoginWithPhone:
                Utils.startActivity(MainActivityAuth.this, PhoneLoginActivity.class);
                break;
            case R.id.clLoginWithMail:
                Utils.startActivity(MainActivityAuth.this, EmailLoginActivity.class);
                break;
            case R.id.clLoginWithHuaweiId:
                Utils.startActivity(MainActivityAuth.this, HuaweiIdLoginActivity.class);
                break;
            case R.id.clLoginWithHuaweiGame:
                Utils.startActivity(MainActivityAuth.this, HuaweiGameLoginActivity.class);
                break;
            case R.id.clLoginWithFacebook:
                Utils.startActivity(MainActivityAuth.this, FacebookLoginActivity.class);
                break;
            case R.id.clLoginWithTwitter:
                Utils.startActivity(MainActivityAuth.this, TwitterLoginActivity.class);
                break;

            case R.id.reauthenticate:
                Utils.startActivity(MainActivityAuth.this, ReauthenticateActivity.class);
                break;

            case R.id.clLoginWithGoogle2:
                showSupportMessage("LoginWith Google");
                Utils.openWebPage(MainActivityAuth.this, getResources().getString(R.string.url_auth_service_google));
                break;
            case R.id.clLoginWithGooglePlay2:
                showSupportMessage("LoginWith GooglePlay");
                Utils.openWebPage(MainActivityAuth.this, getResources().getString(R.string.url_auth_service_google_play));
                break;
            case R.id.clLoginWithQQ:
                showSupportMessage("LoginWith QQ");
                Utils.openWebPage(MainActivityAuth.this, getResources().getString(R.string.url_auth_service_qq));
                break;
            case R.id.clLoginWithWeChat:
                showSupportMessage("LoginWith WeChat");
                Utils.openWebPage(MainActivityAuth.this, getResources().getString(R.string.url_auth_service_wechat));
                break;
            case R.id.clLoginWithWeibo:
                showSupportMessage("LoginWith Weibo");
                Utils.openWebPage(MainActivityAuth.this, getResources().getString(R.string.url_auth_service_weibo));
                break;
            case R.id.clLoginWithVk:
                showSupportMessage("Login With Vk");
                Utils.openWebPage(MainActivityAuth.this, getResources().getString(R.string.url_auth_service_vk));
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
        Util.setToolbar(this, toolbar, getString(R.string.url_txt_authservicemain));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " - resultCode : " + resultCode);

    }


    private void showSupportMessage(String which) {
        Utils.showToastMessage(getApplicationContext(), which + " " + getResources().getString(R.string.feature_supported_but_not_use_in_app_now));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}