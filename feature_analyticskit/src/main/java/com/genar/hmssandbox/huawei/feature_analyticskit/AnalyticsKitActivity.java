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
package com.genar.hmssandbox.huawei.feature_analyticskit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.huawei.hms.analytics.type.HAEventType.SUBMITSCORE;
import static com.huawei.hms.analytics.type.HAParamType.SCORE;

//predefined models

public class AnalyticsKitActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView tvQuestions;
    private TextView tvScore;

    private ImageView imgAnalyticsKit;

    private int curQuestionIdx = 0;
    private int score = 0;

    private final int[] questions = {R.string.q1, R.string.q2, R.string.q3, R.string.q4, R.string.q5};
    private final boolean[] answers = {true, true, false, false, true};


    HiAnalyticsInstance mHiAnalyticsInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_kit);

        HiAnalyticsTools.enableLog();
        // Sets context and data processing location. DE -> Germany
        mHiAnalyticsInstance = HiAnalytics.getInstance(this,"DE");

        //Sets the app installation source
        mHiAnalyticsInstance.setChannel("AppGallery");

        //Sets whether to collect system attributes. Only userAgent attribute is supported now.
        mHiAnalyticsInstance.setPropertyCollection("userAgent",true);

        Toolbar toolBar = findViewById(R.id.toolbar_analytics);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, getResources().getString(R.string.url_txt_analyticskit));
        initUIElements();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUIElements() {
        tvQuestions = findViewById(R.id.tv_questions);
        tvScore = findViewById(R.id.tv_score);
        imgAnalyticsKit = findViewById(R.id.imgAnaltyicsKit);
        imgAnalyticsKit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showDialogImagePeekView(AnalyticsKitActivity.this, getApplicationContext(), imgAnalyticsKit);
            }
        });

        MaterialButton btnTrue = findViewById(R.id.btn_true);
        btnTrue.setOnClickListener(this);

        MaterialButton btnFalse = findViewById(R.id.btn_false);
        btnFalse.setOnClickListener(this);

        MaterialButton btnPostScore = findViewById(R.id.btn_postScore);
        btnPostScore.setOnClickListener(this);

        MaterialButton btnArrayListBundleActivity = findViewById(R.id.btn_arrayListBundle);
        btnArrayListBundleActivity.setOnClickListener(this);

        TextView tvInfoLink = findViewById(R.id.tvAutomaticallyCollectedLink);
        tvInfoLink.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(getResources().getString(R.string.automatically_collected_events_link)));
            startActivity(browserIntent);
        });

        tvQuestions.setText(questions[curQuestionIdx]);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_true:
                answerTrueState();
                break;
            case R.id.btn_false:
                answerFalseState();
                break;
            case R.id.btn_postScore:
                postScore();
                refresh();
                break;
            case R.id.btn_arrayListBundle:
                Intent openEcommerceActivityIntent = new Intent(this, EcommerceExampleActivity.class);
                startActivity(openEcommerceActivityIntent);
            default: //default state
                break;
        }
    }

    private void answerTrueState() {
        checkAnswer(true);
        reportAnswerEvent("true");
    }

    private void answerFalseState() {
        checkAnswer(false);
        reportAnswerEvent("false");
    }

    private void nextQuestion() {
        curQuestionIdx = (curQuestionIdx + 1) % questions.length;
        tvQuestions.setText(questions[curQuestionIdx]);
    }

    @SuppressLint("SetTextI18n")
    private void checkAnswer(boolean answer) {
        if (answer == answers[curQuestionIdx]) {
            score += 20;
            tvScore.setText(getString(R.string.your_score_is, score));
        }
        nextQuestion();
    }

    private void reportAnswerEvent(String answer) {
        // Report a customzied Event
        // Event Name: Answer
        // Event Parameters:
        //  -- question: String
        //  -- answer:String
        //  -- answerTime: String

        Bundle bundle = new Bundle();
        bundle.putString("question", tvQuestions.getText().toString().trim());
        bundle.putString("answer", answer);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        bundle.putString("answerTime", sdf.format(new Date()));

        //report customized event
        mHiAnalyticsInstance.onEvent("Answer", bundle);
    }

    private void postScore() {

        if (score == 0) {
            Toast.makeText(this, "Please answer question.", Toast.LENGTH_SHORT).show();
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong(SCORE, score);

            //report predefined event
            mHiAnalyticsInstance.onEvent(SUBMITSCORE, bundle);
            Toast.makeText(this, "Your score posted to the AppGalleryConnect", Toast.LENGTH_SHORT).show();
        }
    }

    private void refresh() {
        curQuestionIdx = 0;
        score = 0;
        tvScore.setText(getString(R.string.your_score));
        tvQuestions.setText(questions[curQuestionIdx]);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        FeatureCompat.install(newBase);
    }


}