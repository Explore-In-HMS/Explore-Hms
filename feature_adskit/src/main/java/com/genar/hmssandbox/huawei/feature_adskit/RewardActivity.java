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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;

import java.security.SecureRandom;
import java.util.Locale;

public class RewardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RewardActivity.class.getSimpleName();

    TextView textViewScore;
    MaterialButton btnPlayForReward;
    MaterialButton btnShowRewardVideo;

    private int score = 1;
    private static final int DEFAULT_SCORE = 10;
    private static final int PLUS_SCORE = 1;
    private static final int MINUS_SCORE = 5;
    private static final int RANGE = 2;

    private RewardAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        setupToolbar();
        // Initialize the HUAWEI Ads SDK.
        HwAds.init(this);

        initializeUI();

        createAndLoadRewardAd();

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
        textViewScore = findViewById(R.id.textViewScore);
        setScore(score);

        btnPlayForReward = findViewById(R.id.btnPlayForReward);
        btnShowRewardVideo = findViewById(R.id.btnShowRewardVideo);
        btnPlayForReward.setOnClickListener(this);
        btnShowRewardVideo.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnPlayForReward:
                play();
                break;
            case R.id.btnShowRewardVideo:
                rewardAdShow();
                break;
            default: //default state
                break;
        }
    }


    private void createAndLoadRewardAd() {

        if (rewardedAd == null) {
            rewardedAd = new RewardAd(RewardActivity.this, getString(R.string.ad_id_reward));
        }

        RewardAdLoadListener rewardAdLoadListener = new RewardAdLoadListener() {
            @Override
            public void onRewardAdFailedToLoad(int errorCode) {
                String errorMessage = AdvancedAdUtils.getDetailsFromErrorCode(errorCode);
                Log.e(TAG, String.format(Locale.ROOT, "RewardAdStatusListener.onRewardAdFailedToLoad() with errorMessage %s.", errorMessage));
                Utils.showToast(getApplicationContext(), String.format(Locale.ROOT, "RewardAd FailedToLoad with %s.", errorMessage));
            }

            @Override
            public void onRewardedLoaded() {
                Log.d(TAG, "rewardAdLoadListener.onRewardedLoaded.");
                Utils.showToast(getApplicationContext(), "onRewardedLoaded");
            }
        };

        Location location = new Location(LocationManager.GPS_PROVIDER);

        rewardedAd.loadAd(new AdParam.Builder()
                //Set the location information passed by the app
                .setLocation(location)
                .setContentBundle(Utils.contentBundle)
                .build(), rewardAdLoadListener);
    }


    private void rewardAdShow() {
        if (rewardedAd.isLoaded()) {
            rewardedAd.show(RewardActivity.this, new RewardAdStatusListener() {
                @Override
                public void onRewardAdClosed() {
                    Log.d(TAG, "RewardAdStatusListener.onRewardAdClosed() Ad will reCreate");
                    createAndLoadRewardAd();
                }

                @Override
                public void onRewardAdFailedToShow(int errorCode) {
                    String errorMessage = AdvancedAdUtils.getDetailsFromErrorCode(errorCode);
                    Log.e(TAG, String.format(Locale.ROOT, "RewardAdStatusListener.onRewardAdFailedToShow() with errorMessage %s.", errorMessage));
                    Utils.showToast(getApplicationContext(), String.format(Locale.ROOT, "RewardAd FailedToShow with %s.", errorMessage));

                }

                @Override
                public void onRewardAdOpened() {
                    Log.d(TAG, "RewardAdStatusListener.onRewardAdOpened()");
                    Utils.showToast(getApplicationContext(), "onRewardAdOpened");
                }

                @Override
                public void onRewarded(Reward reward) {
                    // You are advised to grant a reward immediately and at the same time, check whether the reward
                    // takes effect on the server. If no reward information is configured, grant a reward based on the
                    // actual scenario.
                    int addScore = reward.getAmount() == 0 ? DEFAULT_SCORE : reward.getAmount();

                    Log.d(TAG, "rewardAdShow() : reward.getAmount() addScore : " + addScore);
                    Utils.showToast(getApplicationContext(), "Watch video show finished , add " + addScore + " scores");

                    score += addScore;

                    setScore(score);

                    createAndLoadRewardAd();
                }
            });
        }
    }

    private void setScore(int score) {
        textViewScore.setText("Score : " + score);
    }

    private void play() {
        // If the score is 0, a message is displayed, asking users to watch the ad in exchange for scores.
        if (score == 0) {
            Utils.showToast(getApplicationContext(), "Watch video ad to add score");
            return;
        }

        // The value 0 or 1 is returned randomly. If the value is 1, the score increases by 1. If the value is 0, the
        // score decreases by 5. If the score is a negative number, the score is set to 0.
        int random = new SecureRandom().nextInt(RANGE);
        Log.d(TAG, "play.random : " + random);
        if (random == 1) {
            score += PLUS_SCORE;
            Utils.showToast(getApplicationContext(), "You win！");
        } else {
            score -= MINUS_SCORE;
            score = score < 0 ? 0 : score;
            Utils.showToast(getApplicationContext(), "You lose！");
        }
        setScore(score);
    }
}
