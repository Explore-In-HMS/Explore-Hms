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

package com.hms.explorehms.huawei.feature_operationanalysis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_operationanalysis.adapter.OperationAnalysisPagerAdapter;
import com.hms.explorehms.huawei.feature_operationanalysis.fragments.ConversionFragment;
import com.hms.explorehms.huawei.feature_operationanalysis.fragments.NotificationFragment;
import com.hms.explorehms.huawei.feature_operationanalysis.fragments.OverviewFragment;
import com.hms.explorehms.huawei.feature_operationanalysis.fragments.PurchasersFragment;
import com.hms.explorehms.huawei.feature_operationanalysis.fragments.RevenueFragment;
import com.hms.explorehms.huawei.feature_operationanalysis.fragments.SubscriptionFragment;
import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.util.LinkedHashMap;
import java.util.Map;

public class OperationMainActivity extends AppCompatActivity {
    private final Map<Fragment, String> fragmentList = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_main);
        setupToolbar();
        fragmentList.put(new OverviewFragment(), "Overview");
        fragmentList.put(new RevenueFragment(), "Revenue");
        fragmentList.put(new PurchasersFragment(), "Purchasers");
        fragmentList.put(new ConversionFragment(), "Conversion");
        fragmentList.put(new SubscriptionFragment(), "Subscription");
        fragmentList.put(new NotificationFragment(), "Notification");

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager2 = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager2);
        OperationAnalysisPagerAdapter adapter =
                new OperationAnalysisPagerAdapter(
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
        Util.setToolbar(this, toolbar, getString(R.string.url_distranalys));
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