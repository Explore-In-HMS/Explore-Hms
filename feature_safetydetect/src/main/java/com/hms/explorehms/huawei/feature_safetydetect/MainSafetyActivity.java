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
package com.hms.explorehms.huawei.feature_safetydetect;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_safetydetect.adapter.SafetyDetectViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainSafetyActivity extends AppCompatActivity {

    private final Map<Fragment, String> fragmentList = new LinkedHashMap<>();
    Toolbar titleOfFragmentTextView;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_safety);
        fragmentList.put(new AppsCheckFragment(), getString(R.string.title_apps_check_entry));
        fragmentList.put(new SysIntegrityFragment(), getString(R.string.title_sys_integrity_entry));
        fragmentList.put(new URLCheckFragment(), getString(R.string.title_url_check_entry));
        fragmentList.put(new UserDetectFragment(), getString(R.string.title_user_detect_entry));
        fragmentList.put(new WifiDetectFragment(), getString(R.string.title_wifi_detect_entry));

        tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager2 = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager2);
        SafetyDetectViewPagerAdapter adapter =
                new SafetyDetectViewPagerAdapter(
                        getSupportFragmentManager(),
                        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                        fragmentList
                );

        viewPager2.setAdapter(adapter);
        doBinding();
        setIcons();

    }

    private void setIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_appcheck);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_sysintegrity);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_urlcheck);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_usercheck);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_wificheck);
    }

    private void doBinding() {
        titleOfFragmentTextView = findViewById(R.id.titleOfFragmentTextView);
        setSupportActionBar(titleOfFragmentTextView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, titleOfFragmentTextView, getString(R.string.url_safetydetect));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}
