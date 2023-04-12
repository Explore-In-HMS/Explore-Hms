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
package com.hms.explorehms.huawei.feature_mlkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.hms.explorehms.huawei.feature_mlkit.ui.tabsfragments.ViewPagerTabsAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class MLMainActivity extends AppCompatActivity {

    //region variables
    @Nullable
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @Nullable
    @BindView(R.id.viewpager)
    ViewPager2 viewPager;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_ml_main);

        ButterKnife.bind(this);
        viewPager.setAdapter(new ViewPagerTabsAdapter(MLMainActivity.this.getSupportFragmentManager(), getLifecycle()));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText( getResources().getString(R.string.tab_text) );
                    break;
                case 1:
                    tab.setText( getResources().getString(R.string.tab_lang) );
                    break;
                case 2:
                    tab.setText( getResources().getString(R.string.tab_img) );
                    break;
                case 3:
                    tab.setText( getResources().getString(R.string.tab_body) );
                    break;
                case 4:
                    tab.setText(getResources().getString(R.string.tab_custom));
                    break;
                default:
                    break;
            }
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