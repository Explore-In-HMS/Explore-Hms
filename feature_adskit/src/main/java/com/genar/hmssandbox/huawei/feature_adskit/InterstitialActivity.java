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
package com.genar.hmssandbox.huawei.feature_adskit;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.InterstitialAd;

import java.security.SecureRandom;
import java.util.Locale;

public class InterstitialActivity extends AppCompatActivity {

    private static final String TAG = InterstitialActivity.class.getSimpleName();

    private InterstitialAd interstitialAd;
    MaterialButton refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        setupToolbar();
        initializeUI();

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);
        loadInterstitialAd();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initializeUI() {

        refresh = findViewById(R.id.btnRefreshInterstitial);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitialAd();
            }
        });
    }

    private void loadInterstitialAd() {

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdId(getAdId(getRandomNumber(2, 1))); // Set an ad slot ID.
        interstitialAd.setAdListener(adListener);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        // Load an interstitial ad.
        AdParam adParam = new AdParam.Builder()
                //Set the location information passed by the app
                .setLocation(location)
                .setContentBundle(Utils.contentBundle)
                .build();
        interstitialAd.loadAd(adParam);

    }

    private String getAdId(int type) {
        if (type == 1) { // video
            return getString(R.string.ad_id_interstitial_video); // The value of video_ad_id is testb4znbuh3n2.
        } else {
            return getString(R.string.ad_id_interstitial_image); // The value of image_ad_id is teste9ih9j0rc3.
        }
    }



    private final AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "adListener.onAdLoaded");
            Utils.showToast(getApplicationContext(), "Ad loaded");
            // Display an interstitial ad.
            showInterstitial();
        }

        void showInterstitial() {
            // Display an interstitial ad.
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                Log.d(TAG, "interstitialAd is NULL or did not Loaded");
                Utils.showToast(getApplicationContext(), "Ad did not load");
            }
        }

        @Override
        public void onAdFailed(int errorCode) {
            String errorMessage = AdvancedAdUtils.getDetailsFromErrorCode(errorCode);
            Log.e(TAG, String.format(Locale.ROOT, "adListener.onAdFailed() with errorMessage %s.", errorMessage));
            Utils.showToast(getApplicationContext(), String.format(Locale.ROOT, "Ad failed to load with %s.", errorMessage));
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "adListener.onAdClosed");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "adListener.onAdClicked");
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "adListener.onAdOpened");
            super.onAdOpened();
        }
    };

    private int getRandomNumber(int max, int min) {
        int randomNum = new SecureRandom().nextInt((max - min) + 1) + min;
        Log.d(TAG, "getRandomNumber.randomNum : " + randomNum);
        return randomNum;
    }

}
