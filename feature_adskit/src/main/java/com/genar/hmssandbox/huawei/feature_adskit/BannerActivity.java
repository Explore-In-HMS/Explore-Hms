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

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;

import java.util.Locale;

public class BannerActivity extends AppCompatActivity {

    private static final String TAG = BannerActivity.class.getSimpleName();
    private static final int REFRESH_TIME = 5;
    private FrameLayout adFrameLayout;
    private BannerView bannerView;
    private BannerView defBannerView;
    private AdParam adParam;
    private ConstraintLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        setupToolbar();
        initializeUI();
        loadDefaultBannerAd();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void initializeUI() {
        rootView = findViewById(R.id.root_view);
        MaterialButton loadAd = findViewById(R.id.btnLoadBannerAd);
        loadAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBannerAd();
            }
        });
        adFrameLayout = findViewById(R.id.ad_frame);
        bannerView = findViewById(R.id.hw_banner_view);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        adParam = new AdParam.Builder()
                //Set the location information passed by the app
                .setLocation(location)
                .setContentBundle(Utils.contentBundle)
                .build();
        // can customize ad RequestOptions parameters
        //adParam = AdvancedAdUtils.editAndGetAdParam(1, 0, 1, "J"); // eg : ads for child
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadDefaultBannerAd() {

        // Load the default banner ad.
        defBannerView = new BannerView(this);
        defBannerView.setAdId(getString(R.string.ad_id_banner));
        defBannerView.setBackgroundColor(getBannerViewBackground(Utils.getRandomNumber(6, 1)));
        defBannerView.setBannerAdSize(getBannerAdSize(Utils.getRandomNumber(8, 1)));
        defBannerView.setBannerRefresh(REFRESH_TIME);
        defBannerView.setAdListener(adListener);

        defBannerView.loadAd(adParam);

        rootView.addView(defBannerView);

    }

    private void loadBannerAd() {
        Log.d(TAG, "adListener.loadBannerAd()....");

        if (defBannerView != null) {
            rootView.removeView(defBannerView);
            defBannerView.destroy();
        }
        Log.d(TAG, "adListener.loadBannerAd() : Layout.removeView(defBannerView); ");


        if (bannerView != null) {
            adFrameLayout.removeView(bannerView);
            bannerView.destroy();
        }

        Log.d(TAG, "adListener.loadBannerAd() : create new bannerView; ");

        bannerView = new BannerView(this);
        bannerView.setAdId(getString(R.string.ad_id_banner));
        bannerView.setBackgroundColor(getBannerViewBackground(Utils.getRandomNumber(6, 1)));
        bannerView.setBannerAdSize(getBannerAdSize(Utils.getRandomNumber(8, 1)));
        bannerView.setBannerRefresh(REFRESH_TIME);
        bannerView.setAdListener(adListener);
        bannerView.loadAd(adParam);

        adFrameLayout.addView(bannerView);

    }

    /**
     * Ad listener.
     */
    private final AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            // Called when an ad is loaded successfully.
            Log.d(TAG, "adListener.onAdLoaded() ");
            Utils.showToast(getApplicationContext(), "Ad loaded.");
        }

        @Override
        public void onAdFailed(int errorCode) {
            String errorMessage = AdvancedAdUtils.getDetailsFromErrorCode(errorCode);
            Log.e(TAG, String.format(Locale.ROOT, "adListener.onAdFailed() with errorMessage %s.", errorMessage));
            Utils.showToast(getApplicationContext(), (String.format(Locale.ROOT, "Ad failed to load with %s.", errorMessage)));
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "adListener.onAdOpened() ");
            // Called when an ad is opened.
            Utils.showToast(getApplicationContext(), "Ad opened ");
        }

        @Override
        public void onAdClicked() {
            // Called when a user taps an ad.
            Log.d(TAG, "adListener.onAdClicked() ");
            Utils.showToast(getApplicationContext(), "Ad clicked");
        }

        @Override
        public void onAdLeave() {
            // Called when a user has left the app.
            Log.d(TAG, "adListener.onAdLeave() ");
            Utils.showToast(getApplicationContext(), "Ad Leave");
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "adListener.onAdClosed() ");
            // Called when an ad is closed.
            Utils.showToast(getApplicationContext(), "Ad closed");
        }
    };


    /**
     * @param whichSize : int
     *                  1 : BANNER_SIZE_320_50
     *                  2 : BANNER_SIZE_320_100
     *                  3 : BANNER_SIZE_300_250
     *                  4 : BANNER_SIZE_360_144
     *                  5 : BANNER_SIZE_360_57
     *                  6 : BANNER_SIZE_SMART
     *                  7 : BANNER_SIZE_DYNAMIC
     *                  other : BANNER_SIZE_320_100
     * @return adSize id : such as BannerAdSize.BANNER_SIZE_320_100
     */
    private BannerAdSize getBannerAdSize(int whichSize) {

        BannerAdSize adSize = null;
        switch (whichSize) {
            case 1:
                adSize = BannerAdSize.BANNER_SIZE_320_50;
                break;
            case 2:
                adSize = BannerAdSize.BANNER_SIZE_320_100;
                break;
            case 3:
                adSize = BannerAdSize.BANNER_SIZE_300_250;
                break;
            case 4:
                adSize = BannerAdSize.BANNER_SIZE_360_144;
                break;
            case 5:
                adSize = BannerAdSize.BANNER_SIZE_360_57;
                break;
            case 6:
                adSize = BannerAdSize.BANNER_SIZE_SMART;
                break;
            case 7:
                adSize = BannerAdSize.BANNER_SIZE_DYNAMIC;
                break;
            default:
                adSize = BannerAdSize.BANNER_SIZE_320_100;
                break;
        }
        return adSize;
    }

    /**
     * @param whichColor : int
     *                   1 : black
     *                   2 : red
     *                   3 : blue
     *                   4 : green
     *                   5 : yellow
     *                   other : transparent
     * @return color id : such as for BLACK = -16777216;
     */
    public static int getBannerViewBackground(int whichColor) {
        int color;
        switch (whichColor) {
            case 1:
                color = Color.BLACK;
                break;
            case 2:
                color = Color.RED;
                break;
            case 3:
                color = Color.BLUE;
                break;
            case 4:
                color = Color.GREEN;
                break;
            case 5:
                color = Color.YELLOW;
                break;
            default:
                color = Color.TRANSPARENT;
                break;
        }
        return color;
    }

}
