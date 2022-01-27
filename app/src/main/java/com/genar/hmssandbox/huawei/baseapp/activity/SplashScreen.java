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
package com.genar.hmssandbox.huawei.baseapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.SharedPrefHelper;
import com.genar.hmssandbox.huawei.SharedPrefKey;
import com.genar.hmssandbox.huawei.onboarding.OnboardingActivity;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener{

    private ConstraintLayout clMain;

    private ImageView ivIcon;
    private ImageView ivLogo;
    private ImageView ivAppName;

    private ImageView ivAccountKit;
    private ImageView ivDynamicKit;
    private ImageView ivGameKit;
    private ImageView ivIapKit;
    private ImageView ivHiAiKit;
    private ImageView ivAdsKit;
    private ImageView ivMlKit;
    private ImageView ivPushKit;
    private ImageView ivMapKit;
    private ImageView ivAnalyticsKit;
    private ImageView ivSiteKit;
    private ImageView ivArKit;

    private TranslateAnimation animation;

    private ArrayList<ImageView> listIcons = new ArrayList<>();
    private ArrayList<Integer> iconDone = new ArrayList<>();

    private static final String TAG = "SplashScreen";

    private int count = 0;

    private boolean processDone = false;
    private boolean skipped = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initUI();
        addList();
        loadImages();

        clMain.setOnClickListener(this);
    }

    private void initUI(){
        clMain = findViewById(R.id.cl_splash_main);

        ivIcon = findViewById(R.id.iv_icon_app_splash_screen);
        ivLogo = findViewById(R.id.iv_huawei_logo_splash_screen);
        ivAppName = findViewById(R.id.iv_app_name_splash_screen);

        ivAccountKit = findViewById(R.id.iv_account_kit_splash_screen);     // 5
        ivDynamicKit = findViewById(R.id.iv_dynamic_kit_splash_screen);     // 4
        ivGameKit = findViewById(R.id.iv_game_kit_splash_screen);           // 9
        ivIapKit = findViewById(R.id.iv_iap_kit_splash_screen);             // 8
        ivHiAiKit = findViewById(R.id.iv_hiai_kit_splash_screen);           // 3
        ivAdsKit = findViewById(R.id.iv_ads_kit_splash_screen);             // 12
        ivMlKit = findViewById(R.id.iv_ml_kit_splash_screen);               // 11
        ivPushKit = findViewById(R.id.iv_push_kit_splash_screen);           // 2
        ivMapKit = findViewById(R.id.iv_map_kit_splash_screen);             // 1
        ivAnalyticsKit = findViewById(R.id.iv_analytics_kit_splash_screen); // 10
        ivSiteKit = findViewById(R.id.iv_site_kit_splash_screen);           // 7
        ivArKit = findViewById(R.id.iv_ar_kit_splash_screen);               // 6
    }

    private void addList(){
        listIcons.add(ivMapKit);
        listIcons.add(ivPushKit);
        listIcons.add(ivHiAiKit);
        listIcons.add(ivDynamicKit);
        listIcons.add(ivAccountKit);
        listIcons.add(ivArKit);
        listIcons.add(ivSiteKit);
        listIcons.add(ivIapKit);
        listIcons.add(ivGameKit);
        listIcons.add(ivAnalyticsKit);
        listIcons.add(ivMlKit);
        listIcons.add(ivAdsKit);
    }

    private void loadImages(){

        final Handler logoHandler = new Handler(Looper.getMainLooper());
        logoHandler.postDelayed(() -> runOnUiThread(() -> {
            Animation logoAnim = new AlphaAnimation(0.00f, 1.00f);
            logoAnim.setDuration(1000);
            logoAnim.setFillAfter(true);
            ivIcon.startAnimation(logoAnim);

            Animation nameAnim = new AlphaAnimation(0.00f, 1.00f);
            nameAnim.setDuration(500);
            nameAnim.setFillAfter(true);
            ivAppName.startAnimation(nameAnim);
        }),3500);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(iconDone.size() != listIcons.size()){
                    count = getOrder();
                    playAnimation(listIcons.get(count));
                    handler.postDelayed(this,150);
                }else{
                    Animation logoAnim = new AlphaAnimation(0.00f, 1.00f);
                    logoAnim.setDuration(3000);

                    logoAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Log.i(TAG, "onAnimationStart: ");
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            handler.postDelayed(() -> {
                                processDone = true;
                                startMain();
                            },500);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            Log.i(TAG, "onAnimationRepeat: ");
                        }
                    });

                    ivLogo.startAnimation(logoAnim);
                    ivLogo.setVisibility(View.VISIBLE);
                }
            }
        }, 0);
    }

    private int getOrder(){
        SecureRandom rnd = new SecureRandom();
        int nmb = rnd.nextInt(listIcons.size());
        while (iconDone.contains(nmb)){
            nmb = rnd.nextInt(listIcons.size());
        }
        iconDone.add(nmb);
        return nmb;
    }

    private void playAnimation(ImageView image){
        runOnUiThread(() -> {
            animation = new TranslateAnimation(0f,0f,-2000f,0f);
            animation.setDuration(1750);
            image.startAnimation(animation);
            image.setVisibility(View.VISIBLE);
        });
    }

    private void startMain(){
        if(processDone && !skipped){
            skipped = true;
            SharedPrefHelper sharedPrefHelper = SharedPrefHelper.getSharedPref(this);

            if (sharedPrefHelper.getBoolean(SharedPrefKey.IS_ONBOARDING_SHOWED)) {
                startActivity(new Intent(this, MainActivityTab.class));
            }else{
                startActivity(new Intent(this, OnboardingActivity.class));
            }

            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMain();
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: ");
    }

    @Override
    public void onClick(View v) {
        processDone = true;
        startMain();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}
