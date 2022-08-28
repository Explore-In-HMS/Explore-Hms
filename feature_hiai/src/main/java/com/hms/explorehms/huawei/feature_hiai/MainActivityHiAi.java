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
package com.hms.explorehms.huawei.feature_hiai;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.adapter.pager.MainViewPagerAdapterHiAi;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class MainActivityHiAi extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hiai);

        initUI();
        initAdapter();
        setupToolbar();
    }

    private void initUI() {
        tabLayout = findViewById(R.id.tl_main_hiai);
        viewPager = findViewById(R.id.vp_main_hiai);
    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.txt_doc_link_main_hiai));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initAdapter() {
        viewPager.setAdapter(new MainViewPagerAdapterHiAi(MainActivityHiAi.this.getSupportFragmentManager(), getLifecycle()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {
            if (position == 0)
                tab.setText("Face");
            else if (position == 1)
                tab.setText("Body");
            else if (position == 2)
                tab.setText("Image");
            else if (position == 3)
                tab.setText("Code");
            else if (position == 4)
                tab.setText("Video");
            else if (position == 5)
                tab.setText("Text");

        });
        tabLayoutMediator.attach();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}
