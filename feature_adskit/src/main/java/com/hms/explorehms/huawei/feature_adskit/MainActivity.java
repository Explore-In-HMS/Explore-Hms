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
package com.hms.explorehms.huawei.feature_adskit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.hms.explorehms.Util;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

/**
 * This activity shows the all Ads options
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //region initializeVariables
    MaterialButton btnCreateSplashAd;
    MaterialButton btnCreateBannerAd;
    MaterialButton btnCreateInterstitialAd;
    MaterialButton btnCreateRewardAd;
    MaterialButton btnCreateNativeAd;
    MaterialButton btnInsStreamAdRollAd;
    MaterialButton btnExSplashAd;
    Toolbar toolbar;
    //endregion


    /**
     * The method initializes the sets up necessary for UI, toolbar and Ads.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ads);
        setupToolbar();
        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);
        //Obtains the style of an app activation reminder pop-up
        HwAds.getAppActivateStyle();

        //Sets the style for an app activation reminder pop-up after the app installation.
        HwAds.setAppActivateStyle(Utils.ACTIVATE_STYLE);

        //Sets whether to enable an app activation reminder pop-up.
        HwAds.setAppInstalledNotify(true);

        //Checks whether an app activation reminder pop-up is enabled
        HwAds.isAppInstalledNotify();

        initializeUI();

    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_txt_adskit));
    }

    /**
     * It starts a count down
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * It initializes the UI, and click listeners.
     */
    private void initializeUI() {

        btnCreateSplashAd = findViewById(R.id.btnCreateSplashAd);
        btnCreateBannerAd = findViewById(R.id.btnCreateBannerAd);
        btnCreateInterstitialAd = findViewById(R.id.btnCreateInterstitialAd);
        btnCreateRewardAd = findViewById(R.id.btnCreateRewardAd);
        btnCreateNativeAd = findViewById(R.id.btnCreateNativeAd);
        btnInsStreamAdRollAd = findViewById(R.id.btnInsStreamAdRollAd);
        btnExSplashAd = findViewById(R.id.btnExSplashAd);

        btnCreateSplashAd.setOnClickListener(this);
        btnCreateBannerAd.setOnClickListener(this);
        btnCreateInterstitialAd.setOnClickListener(this);
        btnCreateRewardAd.setOnClickListener(this);
        btnCreateNativeAd.setOnClickListener(this);
        btnInsStreamAdRollAd.setOnClickListener(this);
        btnExSplashAd.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCreateSplashAd:
                createIntentAndStartActivity(SplashActivity.class);
                Handler mainHandler = new Handler();
                mainHandler.postDelayed(this::finish, 500);
                break;
            case R.id.btnCreateBannerAd:
                createIntentAndStartActivity(BannerActivity.class);
                break;
            case R.id.btnCreateInterstitialAd:
                createIntentAndStartActivity(InterstitialActivity.class);
                break;
            case R.id.btnCreateRewardAd:
                createIntentAndStartActivity(RewardActivity.class);
                break;
            case R.id.btnCreateNativeAd:
                createIntentAndStartActivity(NativeActivity.class);
                break;
            case R.id.btnInsStreamAdRollAd:
                createIntentAndStartActivity(InStreamRollActivity.class);
                break;
            case R.id.btnExSplashAd:
                createIntentAndStartActivity(ExSplashActivity.class);
                break;
            default: // default state
                break;
        }
    }

    /**
     * It creates and starts a activity
     */
    public void createIntentAndStartActivity(Class<?> tClass) {
        Intent intent = new Intent(MainActivity.this, tClass);
        startActivity(intent);
    }

    /**
     * Called when the activity is attaching to its context.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}
