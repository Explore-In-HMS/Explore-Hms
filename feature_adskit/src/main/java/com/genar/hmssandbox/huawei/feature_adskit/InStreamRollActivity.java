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

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.MediaMuteListener;
import com.huawei.hms.ads.instreamad.InstreamAd;
import com.huawei.hms.ads.instreamad.InstreamAdLoadListener;
import com.huawei.hms.ads.instreamad.InstreamAdLoader;
import com.huawei.hms.ads.instreamad.InstreamMediaChangeListener;
import com.huawei.hms.ads.instreamad.InstreamMediaStateListener;
import com.huawei.hms.ads.instreamad.InstreamView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InStreamRollActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = InStreamRollActivity.class.getSimpleName();

    InstreamAdLoader instreamAdLoader;
    List<InstreamAd> instreamAds = new ArrayList<>();

    RelativeLayout instreamContainer;
    InstreamView instreamView;
    ImageView whyThisAd;

    TextView videoContent;
    MaterialButton skipAd;
    TextView countDown;
    MaterialButton callToAction;

    MaterialButton loadButton;
    MaterialButton registerButton;
    MaterialButton muteButton;
    MaterialButton pauseButton;

    int maxAdDuration;
    String whyThisAdUrl;
    boolean isMuted = false;

    AdParam adParam;

    MaterialButton btnCreateInStreamAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instreamroll);
        setupToolbar();
        initializeUiViews();
        initializeInstreamAdView();
        createAndLoaderInStreamAd();
        loadInStreamAd();
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

    private void initializeUiViews() {

        btnCreateInStreamAd = findViewById(R.id.btnCreateInStreamAd);
        btnCreateInStreamAd.setOnClickListener(this);

        instreamContainer = findViewById(R.id.instream_ad_container);
        videoContent = findViewById(R.id.instream_video_content);
        skipAd = findViewById(R.id.instream_skip);
        skipAd.setOnClickListener(v -> {
            if (null != instreamView) {
                instreamView.onClose();
                instreamView.destroy();
                instreamContainer.setVisibility(View.GONE);

                //
                // There is a bug try recreate and load ad after skippedAd.. created ticket about this. will be fixes coming soon.
                //
                //Log.d(TAG, "skipped Ad, and createAndLoaderInStreamAd new Ad loaded again.");
                //Utils.showToast(getApplicationContext(), "You skipped Ad, new Ad should be created and loaded again.");
                //btnCreateInStreamAd.setVisibility(View.VISIBLE);
                //hideAdButtons();
                startActivity(new Intent(InStreamRollActivity.this, InStreamRollActivity.class));
                finish();
            }
        });

        countDown = findViewById(R.id.instream_count_down);
        callToAction = findViewById(R.id.instream_call_to_action);
        whyThisAd = findViewById(R.id.instream_why_this_ad);

        loadButton = findViewById(R.id.instream_load);
        registerButton = findViewById(R.id.instream_register);
        muteButton = findViewById(R.id.instream_mute);
        pauseButton = findViewById(R.id.instream_pause_play);

        loadButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        muteButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);

    }


    private void initializeInstreamAdView() {
        instreamView = findViewById(R.id.instream_view);
        instreamView.setInstreamMediaChangeListener(instreamMediaChangeListener);
        instreamView.setInstreamMediaStateListener(instreamMediaStateListener);
        instreamView.setMediaMuteListener(mediaMuteListener);
        instreamView.setOnInstreamAdClickListener(() -> Log.d(TAG, "setOnInstreamAdClickListener"));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnCreateInStreamAd:
                if (instreamView == null) {
                    initializeInstreamAdView();
                }
                createAndLoaderInStreamAd();
                loadInStreamAd();
                break;
            case R.id.instream_load:
                loadInStreamAd();
                break;
            case R.id.instream_register:
                if (null == instreamAds || instreamAds.size() == 0) {
                    playNormalVideo();
                } else {
                    playInstreamAds(instreamAds);
                }
                break;
            case R.id.instream_mute:
                if (isMuted) {
                    instreamView.unmute();
                    muteButton.setText("Mute");
                } else {
                    instreamView.mute();
                    muteButton.setText("UnMute");
                }
                break;
            case R.id.instream_pause_play:
                if (instreamView.isPlaying()) {
                    instreamView.pause();
                    pauseButton.setText("Play");
                } else {
                    instreamView.play();
                    pauseButton.setText("Pause");
                }
                break;
            default: // default state
                break;
        }
    }

    private void loadInStreamAd() {
        Log.d(TAG, "loadInStreamAd");

        if (null != instreamAdLoader) {

            loadButton.setText("Loading");
            instreamContainer.setVisibility(View.VISIBLE);

            Location location = new Location(LocationManager.GPS_PROVIDER);
            adParam = new AdParam.Builder()
                    //Set the location information passed by the app
                    .setLocation(location)
                    .setContentBundle(Utils.contentBundle)
                    .build();
            // can customize ad RequestOptions parameters
            //adParam = AdvancedAdUtils.editAndGetAdParam(1, 0, 1, "J"); // eg : ads for child

            instreamAdLoader.loadAd(adParam);

        } else {
            Log.d(TAG, "instreamAdLoader id NULL. You must create Ad before loading Ad");
            Utils.showToast(getApplicationContext(), "nstreamAdLoader id NULL. You must create Ad before loading Ad");
        }
    }

    private void createAndLoaderInStreamAd() {
        Log.d(TAG, "createAndLoaderInStreamAd");
        /**
         * if the maximum total duration is 60 seconds and the maximum number of roll ads is eight,
         * at most four 15-second roll ads or two 30-second roll ads will be returned.
         * If the maximum total duration is 120 seconds and the maximum number of roll ads is four,
         * no more roll ads will be returned after whichever is reached.
         */
        int totalDuration = 60;
        int maxCountRoll = 4;
        // "testy3cglm3pj0" is a dedicated test ad slot ID. Before releasing your app, replace the test ad slot ID with the formal one.
        InstreamAdLoader.Builder builder = new InstreamAdLoader.Builder(getApplicationContext(), getString(R.string.ad_id_roll));

        // Before reusing InstreamAdLoader to load ads, ensure that the previous request is complete.
        // Set the maximum total duration and number of roll ads that can be loaded.
        instreamAdLoader = builder
                .setTotalDuration(totalDuration)
                .setMaxCount(maxCountRoll)
                .setInstreamAdLoadListener(instreamAdLoadListener)
                .build();

    }


    private InstreamAdLoadListener instreamAdLoadListener = new InstreamAdLoadListener() {
        @Override
        public void onAdLoaded(final List<InstreamAd> ads) {
            if (null == ads || ads.size() == 0) {
                playNormalVideo();
                return;
            }
            Iterator<InstreamAd> it = ads.iterator();
            while (it.hasNext()) {
                InstreamAd ad = it.next();
                if (ad.isExpired()) {
                    it.remove();
                }
            }
            if (ads.size() == 0) {
                playNormalVideo();
                return;
            }
            loadButton.setText("Ad Loaded");
            instreamAds = ads;

            Log.d(TAG, "onAdLoaded, ad size: " + ads.size() + ", you can PlayInStreamAd.");
            Utils.showToast(getApplicationContext(), "onAdLoaded, ad size: " + ads.size() + ",  you can PlayInStreamAd.");

            playInstreamAds(instreamAds);

            hideLoadAndCreateAdButtons();
        }

        @Override
        public void onAdFailed(int errorCode) {
            // Called when roll ads fail to be loaded.
            String errorMessage = AdvancedAdUtils.getDetailsFromErrorCode(errorCode);
            Log.e(TAG, "instreamAdLoader.onAdFailed() with errorMessage " + errorMessage);
            Utils.showToast(getApplicationContext(), "Ad failed to load with " + errorMessage);
            loadButton.setText("Load Ad");

            showLoadAndCreateAdButtons();

            playNormalVideo();
        }

        void showLoadAndCreateAdButtons() {
            loadButton.setVisibility(View.VISIBLE);
            btnCreateInStreamAd.setVisibility(View.VISIBLE);
        }

        void hideLoadAndCreateAdButtons() {
            loadButton.setVisibility(View.GONE);
            btnCreateInStreamAd.setVisibility(View.GONE);
        }
    };


    /**
     * inStreamMediaChangeListener
     */
    private InstreamMediaChangeListener instreamMediaChangeListener = new InstreamMediaChangeListener() {

        @Override
        public void onSegmentMediaChange(InstreamAd instreamAd) {
            // Switch from one roll ad to another.
            Log.d(TAG, "InstreamMediaChangeListener.onSegmentMediaChange() : InstreamAd : " + instreamAd.toString());
            whyThisAdUrl = null;
            whyThisAdUrl = instreamAd.getWhyThisAd();
            Log.d(TAG, "InstreamMediaChangeListener.onSegmentMediaChange() : whyThisAd : " + whyThisAdUrl);
            if (!TextUtils.isEmpty(whyThisAdUrl)) {
                whyThisAd.setVisibility(View.VISIBLE);
                whyThisAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(whyThisAdUrl)));
                    }
                });
            } else {
                whyThisAd.setVisibility(View.GONE);
            }

            String cta = instreamAd.getCallToAction();
            Log.d(TAG, "InstreamMediaChangeListener.onSegmentMediaChange() : callToAction : " + cta);
            if (!TextUtils.isEmpty(cta)) {
                callToAction.setVisibility(View.VISIBLE);
                callToAction.setText(cta);
                instreamView.setCallToActionView(callToAction);
            }
        }
    };

    private InstreamMediaStateListener instreamMediaStateListener = new InstreamMediaStateListener() {
        @Override
        public void onMediaProgress(int percent, int playTime) {
            // Playback process.
            updateCountDown(playTime);
        }

        @Override
        public void onMediaStart(int playTime) {
            // Start playback.
            Log.d(TAG, "InstreamMediaStateListener.onMediaStart() : playTime : " + playTime);
            updateCountDown(playTime);
            showAdButtons();
        }

        @Override
        public void onMediaPause(int playTime) {
            // Pause playback.
            Log.d(TAG, "InstreamMediaStateListener.onMediaPause() : playTime : " + playTime);
            updateCountDown(playTime);
        }

        @Override
        public void onMediaStop(int playTime) {
            // Stop playback.
            Log.d(TAG, "InstreamMediaStateListener.onMediaStop() : playTime : " + playTime);
            updateCountDown(playTime);
            hideAdButtons();
        }

        @Override
        public void onMediaCompletion(int playTime) {
            // Playback is complete.
            Log.d(TAG, "InstreamMediaStateListener.onMediaCompletion() : playTime : " + playTime);
            updateCountDown(playTime);
            playNormalVideo();
            hideAdButtons();
        }

        @Override
        public void onMediaError(int playTime, int errorCode, int extra) {
            // Playback error.
            Log.d(TAG, "InstreamMediaStateListener.onMediaCompletion() : playTime : " + playTime);
            updateCountDown(playTime);
        }

        void updateCountDown(long playTime) {
            final String time = String.valueOf(Math.round((maxAdDuration - playTime) / (double)1000));
            runOnUiThread(() -> countDown.setText(time + "s"));
        }

        void hideAdButtons() {
            muteButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
        }
    };

    private void showAdButtons() {
        muteButton.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
    }

    // play your normal video content.
    private void playNormalVideo() {
        Log.d(TAG, "playNormalVideo");
        hideAdViews();
        videoContent.setText("Normal Video Playing...");
    }

    private void hideAdViews() {
        instreamContainer.setVisibility(View.GONE);
    }

    private void playInstreamAds(List<InstreamAd> ads) {
        Log.d(TAG, "playInstreamAds");
        maxAdDuration = getMaxInstreamDuration(ads);
        instreamContainer.setVisibility(View.VISIBLE);
        loadButton.setText("Load Ad");

        instreamView.setInstreamAds(ads);
        showAdButtons();
    }


    private MediaMuteListener mediaMuteListener = new MediaMuteListener() {
        @Override
        public void onMute() {
            // Mute a roll ad.
            Log.d(TAG, "MediaMuteListener.onMute()");
            isMuted = true;
        }

        @Override
        public void onUnmute() {
            // Unmute a roll ad.
            Log.d(TAG, "MediaMuteListener.onUnmute()");
            isMuted = false;
        }
    };

    private int getMaxInstreamDuration(List<InstreamAd> ads) {
        int duration = 0;
        for (InstreamAd ad : ads) {
            duration += ad.getDuration();
        }
        return duration;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != instreamView && !instreamView.isPlaying()) {
            instreamView.play();
            pauseButton.setText("Pause");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (null != instreamView && instreamView.isPlaying()) {
            instreamView.pause();
            pauseButton.setText("Play");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instreamView != null) {
            instreamView.removeInstreamMediaStateListener();
            instreamView.removeInstreamMediaChangeListener();
            instreamView.removeMediaMuteListener();
            instreamView.destroy();
        }
    }
}
