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

package com.hms.explorehms.huawei.feature_imagekit;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_imagekit.ui.adapter.ViewPagerTabsAdapterImagekit;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class MainActivityImageKit extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_imagekit);

        setupToolbar();

        initUI();
        initAdapter();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_imagekit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_txt_imagekitmain));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        tabLayout = findViewById(R.id.tl_main_imagekit);
        viewPager = findViewById(R.id.vp_main_imagekit);

    }

    private void initAdapter() {
        viewPager.setAdapter(new ViewPagerTabsAdapterImagekit(MainActivityImageKit.this.getSupportFragmentManager(), getLifecycle()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {
            if (position == 0)
                tab.setText("Image Vision");
            else if (position == 1)
                tab.setText("Image Render");
        });
        tabLayoutMediator.attach();

        viewPager.setNestedScrollingEnabled(true);
        viewPager.invalidate();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}
