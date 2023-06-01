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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
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

/**
 * This shows how we display InStream Roll Ad with Ads Kit.
 */
public class InStreamRollActivity extends AppCompatActivity {

    private AppCompatButton btnGetAdvertiserInfoInroll;

    private TextView skipAd;
    private TextView countDown;
    private TextView callToAction;

    private ImageView muteIcon;


    private RelativeLayout instreamContainer;
    private InstreamView instreamView;
    private ImageView whyThisAd;
    private int currentTime;

    private Context context;
    private int maxAdDuration;
    private String whyThisAdUrl;
    private boolean isMuted = false;

    private InstreamAdLoader adLoader;
    private List<InstreamAd> instreamAds = new ArrayList<>();

    private VideoView videoView;
    private MediaController mediaController;

    /**
     * The method initializes the sets up necessary for UI, toolbar and Ads.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        // setTitle(R.string.instream_ad);
        setContentView(R.layout.activity_instreamroll);
        setupToolbar();
        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);

        initInstreamAdView();
        initButtons();
        configAdLoader();

        mediaController = new MediaController(InStreamRollActivity.this);
        videoView = findViewById(R.id.video);
        Uri uri = Uri.parse("https://storage.googleapis.com/gvabox/media/samples/stock.mp4");
        videoView.setVideoURI(uri);
        videoView.setMediaController(mediaController);
        videoView.start();
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_ads);
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
     * It starts a count down
     */
    private void startCountDown() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initInstreamAdView();
                currentTime = videoView.getCurrentPosition();
                adLoader.loadAd(new AdParam.Builder().build());
                videoView.pause();
                videoView.setVisibility(View.GONE);
            }
        }, 20000);


    }

    /**
     * This listener listens onSegmentMediaChange
     */
    private InstreamMediaChangeListener mediaChangeListener = new InstreamMediaChangeListener() {
        @Override
        public void onSegmentMediaChange(InstreamAd instreamAd) {
            whyThisAdUrl = null;
            whyThisAdUrl = instreamAd.getWhyThisAd();
            //Log.i(TAG, "onSegmentMediaChange, whyThisAd: " + whyThisAdUrl);
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
            //Get advertiser info
            /*
            if (null != instreamAd && !instreamAd.hasAdvertiserInfo()) {
                btnGetAdvertiserInfoInroll.setVisibility(View.GONE); // Hide the advertiser information icon when no advertiser information is delivered.
            }*/

            String cta = instreamAd.getCallToAction();
            if (!TextUtils.isEmpty(cta)) {
                callToAction.setVisibility(View.VISIBLE);
                callToAction.setText(cta);
                instreamView.setCallToActionView(callToAction);
            }
        }
    };

    /**
     * This listener listens onMediaProgress, onMediaStart, onMediaPause, onMediaStop, onMediaCompletion and onMediaError
     */
    private InstreamMediaStateListener mediaStateListener = new InstreamMediaStateListener() {
        @Override
        public void onMediaProgress(int per, int playTime) {
            updateCountDown(playTime);

        }

        @Override
        public void onMediaStart(int playTime) {
            updateCountDown(playTime);
        }

        @Override
        public void onMediaPause(int playTime) {
            updateCountDown(playTime);
        }

        @Override
        public void onMediaStop(int playTime) {
            updateCountDown(playTime);
        }

        @Override
        public void onMediaCompletion(int playTime) {
            updateCountDown(playTime);
            removeInstream();
            playVideo();

        }

        @Override
        public void onMediaError(int playTime, int errorCode, int extra) {
            updateCountDown(playTime);
        }
    };

    /**
     * This listener listens onMute and onUnmute
     */
    private MediaMuteListener mediaMuteListener = new MediaMuteListener() {
        @Override
        public void onMute() {
            isMuted = true;
        }

        @Override
        public void onUnmute() {
            isMuted = false;

        }
    };

    /**
     * This methods inits Instream AdView and if clicked skipAd, it starts removeInstream and playVideo
     */
    private void initInstreamAdView() {
        instreamContainer = findViewById(R.id.instream_ad_container);
        instreamView = new InstreamView(getApplicationContext());
        instreamContainer.addView(instreamView, 0);
        skipAd = findViewById(R.id.instream_skip);
        skipAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeInstream();
                playVideo();
            }
        });

        countDown = findViewById(R.id.instream_count_down);
        callToAction = findViewById(R.id.instream_call_to_action);
        whyThisAd = findViewById(R.id.instream_why_this_ad);

        instreamView.setInstreamMediaChangeListener(mediaChangeListener);
        instreamView.setInstreamMediaStateListener(mediaStateListener);
        instreamView.setMediaMuteListener(mediaMuteListener);
        instreamView.setOnInstreamAdClickListener(new InstreamView.OnInstreamAdClickListener() {
            @Override
            public void onClick() {
                //  Toast.makeText(context, "ad is clicked.", Toast.LENGTH_SHORT).show();
                //use 3rd party tracking if needed.
            }
        });
    }

    /**
     * This methods removes Instream
     */
    private void removeInstream() {
        if (null != instreamView) {
            instreamView.onClose();
            instreamView.destroy();
            instreamContainer.removeView(instreamView);
            instreamContainer.setVisibility(View.GONE);
            instreamAds.clear();
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.mic_icon:
                    if (isMuted) {
                        instreamView.unmute();
                        muteIcon.setImageResource(R.drawable.add_mic_on);

                    } else {
                        instreamView.mute();
                        muteIcon.setImageResource(R.drawable.ad_mic_off);

                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * This methods inits buttons
     */
    private void initButtons() {
        muteIcon = findViewById(R.id.mic_icon);
        muteIcon.setOnClickListener(clickListener);
        btnGetAdvertiserInfoInroll = findViewById(R.id.btn_get_advertiser_info_inroll);
        //This button click listener works before ad loaded, it's showing a warning to user.
        btnGetAdvertiserInfoInroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(InStreamRollActivity.this, getString(R.string.ads_kit_advertiser_info_fail));
            }
        });

    }

    private void showAdvertiserInfo(InstreamAd instreamAd, InstreamView instreamView){
        if (instreamAd.hasAdvertiserInfo()){
            instreamView.showAdvertiserInfoDialog(btnGetAdvertiserInfoInroll, false);
        }else{
            Utils.showToast(InStreamRollActivity.this, getString(R.string.ads_kit_advertiser_info_fail));
        }
    }

    /**
     * This listener handles onAdLoaded and onAdFailed
     */
    private InstreamAdLoadListener instreamAdLoadListener = new InstreamAdLoadListener() {
        @Override
        public void onAdLoaded(final List<InstreamAd> ads) {
            if (null == ads || ads.size() == 0) {
                playVideo();
                return;
            }
            Iterator<InstreamAd> it = ads.iterator();
            while (it.hasNext()) {
                InstreamAd ad = it.next();
                btnGetAdvertiserInfoInroll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAdvertiserInfo(ad, instreamView);
                    }
                });
                if (ad.isExpired()) {
                    it.remove();
                }
            }
            if (ads.size() == 0) {
                playVideo();
                return;
            }
            instreamAds = ads;
            playInstreamAds(instreamAds);
        }

        @Override
        public void onAdFailed(int errorCode) {
            //Log.w(TAG, "onAdFailed: " + errorCode);
            //Toast.makeText(context, "onAdFailed: " + errorCode, Toast.LENGTH_SHORT).show();
            playVideo();
        }
    };

    /**
     * It creates InstreamAdLoader with test id
     */
    private void configAdLoader() {
        /**
         * if the maximum total duration is 60 seconds and the maximum number of roll ads is eight,
         * at most four 15-second roll ads or two 30-second roll ads will be returned.
         * If the maximum total duration is 120 seconds and the maximum number of roll ads is four,
         * no more roll ads will be returned after whichever is reached.
         */
        int totalDuration = 60;
        int maxCount = 4;
        InstreamAdLoader.Builder builder = new InstreamAdLoader.Builder(context, getString(R.string.ad_id_roll));
        adLoader = builder.setTotalDuration(totalDuration)
                .setMaxCount(maxCount)
                .setInstreamAdLoadListener(instreamAdLoadListener)
                .build();
    }

    // play your normal video content.
    private void playVideo() {
        hideAdViews();
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
        videoView.seekTo(currentTime);


    }

    /**
     * It hides AdViews
     */
    private void hideAdViews() {
        instreamContainer.setVisibility(View.GONE);
    }

    /**
     * It plays InstreamAds
     */
    private void playInstreamAds(List<InstreamAd> ads) {
        maxAdDuration = getMaxInstreamDuration(ads);
        instreamContainer.setVisibility(View.VISIBLE);
        instreamView.setInstreamAds(ads);
    }

    /**
     * It updates CountDown
     */
    private void updateCountDown(long playTime) {
        final String time = String.valueOf(Math.round((maxAdDuration - playTime) / 1000));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countDown.setText(time + "s");
            }
        });
    }

    /**
     * It returns maximum of Instream Duration
     */
    private int getMaxInstreamDuration(List<InstreamAd> ads) {
        int duration = 0;
        for (InstreamAd ad : ads) {
            duration += ad.getDuration();
        }
        return duration;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != instreamView && instreamView.isPlaying()) {
            instreamView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDown();
        if (null != instreamView && !instreamView.isPlaying()) {
            instreamView.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != instreamView) {
            instreamView.removeInstreamMediaStateListener();
            instreamView.removeInstreamMediaChangeListener();
            instreamView.removeMediaMuteListener();
            instreamView.destroy();
        }
    }
}
