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

package com.hms.explorehms.huawei.feature_productmanagement;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_productmanagement.adapter.ProductManagementPagerAdapter;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.ActivatingProductFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.AddingProductFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.ConversionRuleDescriptionFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.DeactivatingProductFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.DeletingProductFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.ManagingProductMarketingFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.ManagingSubscriptionGroupsFragment;
import com.hms.explorehms.huawei.feature_productmanagement.fragments.ModifyingProductFragment;
import com.google.android.material.tabs.TabLayout;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductManagementActivity extends AppCompatActivity {
    private final Map<Fragment, String> fragmentList = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        setupToolbar();

        fragmentList.put(new AddingProductFragment(), "Adding a Product");
        fragmentList.put(new ModifyingProductFragment(), "Modifying Product Information");
        fragmentList.put(new DeactivatingProductFragment(), "Deactivating a Product");
        fragmentList.put(new ActivatingProductFragment(), "Activating a Product");
        fragmentList.put(new DeletingProductFragment(), "Deleting a Product");
        fragmentList.put(new ManagingSubscriptionGroupsFragment(), "Managing Subscription Groups");
        fragmentList.put(new ManagingProductMarketingFragment(), "Managing Product Marketing");
        fragmentList.put(new ConversionRuleDescriptionFragment(), "Conversion Rule Description");

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager2 = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager2);
        ProductManagementPagerAdapter adapter =
                new ProductManagementPagerAdapter(
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
        Util.setToolbar(this, toolbar, getString(R.string.url_productmanagement));
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