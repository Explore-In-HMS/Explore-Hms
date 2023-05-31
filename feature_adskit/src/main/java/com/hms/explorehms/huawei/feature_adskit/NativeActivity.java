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

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.VideoOperator;
import com.huawei.hms.ads.nativead.DetailedCreativeType;
import com.huawei.hms.ads.nativead.MediaView;
import com.huawei.hms.ads.nativead.NativeAd;
import com.huawei.hms.ads.nativead.NativeAdConfiguration;
import com.huawei.hms.ads.nativead.NativeAdLoader;
import com.huawei.hms.ads.nativead.NativeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This shows how we display Native Ad with Ads Kit.
 */
public class NativeActivity extends AppCompatActivity {


    private static final String TAG = NativeActivity.class.getSimpleName();
    private Button btnGetAdvertiserInfoNative;

    /**
     * The method initializes the sets up necessary for UI, toolbar and Ads.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        setupToolbar();

        initView();

        NativeView smallNativeView = findViewById(R.id.native_ad_small);
        NativeView largeNativeView = findViewById(R.id.native_ad_large);
        NativeView videoNativeView = findViewById(R.id.native_ad_video);

        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);
        // load ads based on ad id to native ad views
        loadAd(getString(R.string.ad_id_native_video), videoNativeView, 0);
        loadAd(getString(R.string.ad_id_native_small), smallNativeView, 0);
        loadAd(getString(R.string.ad_id_native), largeNativeView, DetailedCreativeType.BIG_IMG);

    }

    private void initView(){
        btnGetAdvertiserInfoNative = findViewById(R.id.btn_get_advertiser_info_native);
    }

    private void initAdvertiserButtonClick(NativeAd nativeAd, NativeView nativeView){
        btnGetAdvertiserInfoNative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativeAd.hasAdvertiserInfo()){
                    nativeView.showAdvertiserInfoDialog(btnGetAdvertiserInfoNative, true);
                    Utils.showToast(NativeActivity.this, getString(R.string.advertise_info_showing));
                }else{
                    Utils.showToast(NativeActivity.this, getString(R.string.ads_kit_advertiser_info_fail));
                }
            }
        });
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
     * The method loads ads with showNativeAd
     */
    private void loadAd(String adId, final NativeView nativeView, int type) {
        List<Integer> detailedCreativeTypeList = new ArrayList<>();
        final NativeAdLoader.Builder builder = new NativeAdLoader.Builder(this, adId);
        builder.setNativeAdLoadedListener(nativeAd -> {
            //AdvertiserInfo check
            if (!nativeAd.hasAdvertiserInfo()){
                //btnGetAdvertiserInfoNative.setVisibility(View.GONE);
                initAdvertiserButtonClick(nativeAd, nativeView);
            }else{
                initAdvertiserButtonClick(nativeAd, nativeView);
            }
            Log.d(TAG, "onNativeAdLoaded : Ad Loaded successfully.");
            // Display native ad.
            showNativeAd(nativeAd, nativeView);

            nativeAd.setDislikeAdListener(() -> {
                Log.d(TAG, "setDislikeAdListener : Ad is closed!");
                // Call this method when an ad is closed.
                Utils.showToast(getApplicationContext(), "DislikeAdListener : Ad is closed");
            });

        }).setAdListener(adListener);


        NativeAdConfiguration adConfiguration = new NativeAdConfiguration.Builder()
                .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.TOP_RIGHT)
                // Set custom attributes.
                .build();

        NativeAdLoader nativeAdLoader = builder.setNativeAdOptions(adConfiguration).build();

        Log.d(TAG, "loadAd() : nativeAdLoader.loadAd() ");

        Location location = new Location(LocationManager.GPS_PROVIDER);

        // Add a specified creative type (large image).
        if (type != 0) {
            detailedCreativeTypeList.add(type);
            nativeAdLoader.loadAd(new AdParam.Builder()
                    //Set the location information passed by the app
                    .setLocation(location)
                    .setDetailedCreativeTypeList(detailedCreativeTypeList)
                    .setContentBundle(Utils.contentBundle)
                    .build());
        } else {
            nativeAdLoader.loadAd(new AdParam.Builder()
                    //Set the location information passed by the app
                    .setLocation(location)
                    .setContentBundle(Utils.contentBundle)
                    .build());
        }


    }

    /**
     * Display native ad.
     *
     * @param nativeAd native ad object that contains ad materials.
     */
    private void showNativeAd(NativeAd nativeAd, NativeView nativeView) {
        Log.d(TAG, "showNativeAd");

        // Register a native ad material view.
        nativeView.setTitleView(nativeView.findViewById(R.id.ad_title));
        nativeView.setMediaView((MediaView) nativeView.findViewById(R.id.ad_media));
        nativeView.setAdSourceView(nativeView.findViewById(R.id.ad_source));
        nativeView.setCallToActionView(nativeView.findViewById(R.id.ad_call_to_action));

        // Populate a native ad material view.
        ((TextView) nativeView.getTitleView()).setText(nativeAd.getTitle());
        nativeView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (null != nativeAd.getAdSource()) {
            ((TextView) nativeView.getAdSourceView()).setText(nativeAd.getAdSource());
        }

        nativeView.getAdSourceView()
                .setVisibility(null != nativeAd.getAdSource() ? View.VISIBLE : View.INVISIBLE);

        if (null != nativeAd.getCallToAction()) {
            ((Button) nativeView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        nativeView.getCallToActionView()
                .setVisibility(null != nativeAd.getCallToAction() ? View.VISIBLE : View.INVISIBLE);

        // Obtain a video controller.
        VideoOperator videoOperator = nativeAd.getVideoOperator();

        // Check whether a native ad contains video materials.
        if (videoOperator.hasVideo()) {
            Log.d(TAG, "initNativeAdView() : videoOperator.hasVideo ");
            // Add a video lifecycle event listener.
            videoOperator.setVideoLifecycleListener(videoLifecycleListener);
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

    }

    /**
     * The method handles playing and ending videos
     */
    private VideoOperator.VideoLifecycleListener videoLifecycleListener = new VideoOperator.VideoLifecycleListener() {
        @Override
        public void onVideoStart() {
            Log.d(TAG, "videoOperator.onVideoStart() : Video playback state: starting to be played ");
            Utils.showToast(getApplicationContext(), "Video playback state: starting to be played ");
        }

        @Override
        public void onVideoPlay() {
            Log.d(TAG, "videoOperator.onVideoStart() : Video playback state: being played ");
            Utils.showToast(getApplicationContext(), "Video playback state: being played  ");
        }

        @Override
        public void onVideoEnd() {
            Log.d(TAG, "videoOperator.onVideoStart() : Video playback state: playback completed.");
            // If there is a video, load a new native ad only after video playback is complete.
            Utils.showToast(getApplicationContext(), ("Video playback state: playback completed. "));
        }
    };

    /**
     * The method handles Ads attitudes
     */
    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "adListener.onAdLoaded");
            Utils.showToast(getApplicationContext(), "Ad loaded ");
        }

        @Override
        public void onAdFailed(int errorCode) {
            String errorMessage = AdvancedAdUtils.getDetailsFromErrorCode(errorCode);
            Log.e(TAG, String.format(Locale.ROOT, "NativeAd.onAdFailed() with errorMessage %s.", errorMessage));
            Utils.showToast(getApplicationContext(), String.format(Locale.ROOT, "NativeAd onAdFailed with %s.", errorMessage));
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "adListener.onAdClosed");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "adListener.onAdClicked");
            Utils.showToast(getApplicationContext(), "onAdClicked");
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "adListener.onAdOpened");
            Utils.showToast(getApplicationContext(), "onAdOpened");
            super.onAdOpened();
        }
    };

}
