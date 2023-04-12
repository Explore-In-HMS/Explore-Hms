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

package com.hms.explorehms.onboarding;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.databinding.ActivityOnboardingBinding;
import com.hms.explorehms.onboarding.adapter.OnboardingAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.hms.explorehms.databinding.ActivityOnboardingBinding binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        OnboardingAdapter adapter = new OnboardingAdapter(this);
        binding.pagerOnboarding.setAdapter(adapter);

        new TabLayoutMediator(binding.tabDotsOnboarding, binding.pagerOnboarding,
                (tab, position) -> {
                    //no naming for the tabs
                }
        ).attach();
    }
}