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

package com.genar.hmssandbox.huawei.reference.datasecurity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;


public class GeneralFeaturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_features_datasecurity);
        setupToolbar();
        setExplanations();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.link_developer_datasecurity_engine));
    }

    private void setExplanations() {
        String explanation1 = getString(R.string.datasecurity_engine_explanation1);
        String explanation2 = getString(R.string.datasecurity_engine_explanation2);
        String explanation3 = getString(R.string.datasecurity_engine_explanation3);

        TextView explanationTextView = findViewById(R.id.explanationTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            explanationTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        explanationTextView.setText(new StringBuilder(explanation1 + explanation2 + explanation3));
    }


    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }
}