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

package com.hms.explorehms.huawei.feature_phasedrelease;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_phasedrelease.adapter.PhasedReleasePagerAdapter;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.CancelingReleaseFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.ChangingPhasedReleaseFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.OverviewFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.RemovingPhasedFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.RestoringReleaseFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.RollingBackPhasedFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.SuspendingReleaseFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.UpdatingReleaseFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.UpgradingPhasedReleaseFragment;
import com.hms.explorehms.huawei.feature_phasedrelease.fragments.ViewingHistoryFragment;
import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.util.LinkedHashMap;
import java.util.Map;

public class PhasedReleaseActivity extends AppCompatActivity {
    private final Map<Fragment, String> fragmentList = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phased_release);
        setupToolbar();
        setupPagerAdapter();

    }

    private void setupPagerAdapter() {
        fragmentList.put(new OverviewFragment(), "Submitting a Phased Release");
        fragmentList.put(new SuspendingReleaseFragment(), "Suspending Release by Phase");
        fragmentList.put(new RestoringReleaseFragment(), "Restoring Release by Phase");
        fragmentList.put(new UpdatingReleaseFragment(), "Updating Release by Phase");
        fragmentList.put(new CancelingReleaseFragment(), "Canceling Release by Phase");
        fragmentList.put(new ViewingHistoryFragment(), "Viewing History of Release by Phase");
        fragmentList.put(new ChangingPhasedReleaseFragment(), "Changing Phased Release to Full Release");
        fragmentList.put(new UpgradingPhasedReleaseFragment(), "Upgrading a Phased Release Version");
        fragmentList.put(new RollingBackPhasedFragment(), "Rolling Back a Phased Release Version");
        fragmentList.put(new RemovingPhasedFragment(), "Removing a Phased Release Version from Sale");

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager2 = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager2);
        PhasedReleasePagerAdapter adapter =
                new PhasedReleasePagerAdapter(
                        getSupportFragmentManager(),
                        FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                        fragmentList
                );

        viewPager2.setAdapter(adapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_txt_pahsedrelease));
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