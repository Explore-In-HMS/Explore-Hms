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

package com.hms.explorehms.onboarding.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hms.explorehms.onboarding.fragments.ContainerOnboardingFragment;
import com.hms.explorehms.onboarding.fragments.FifthOnboardingFragment;
import com.hms.explorehms.onboarding.fragments.FirstOnboardingFragment;
import com.hms.explorehms.onboarding.fragments.FourthOnboardingFragment;
import com.hms.explorehms.onboarding.fragments.SecondOnboardingFragment;
import com.hms.explorehms.onboarding.fragments.SixthOnboardingFragment;
import com.hms.explorehms.onboarding.fragments.ThirdOnboardingFragment;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FirstOnboardingFragment();
            case 1:
                return new SecondOnboardingFragment();
            case 2:
                return new ThirdOnboardingFragment();
            case 3:
                return new FourthOnboardingFragment();
            case 4:
                return new SixthOnboardingFragment();
            case 5:
                return new FifthOnboardingFragment();
            default:
                return new ContainerOnboardingFragment();
        }


      /*  if (position == 0) {
            return new FirstOnboardingFragment();
        } else if (position == 1) {
            return new SecondOnboardingFragment();
        } else if (position == 2) {
            return new ThirdOnboardingFragment();
        } else if (position == 3) {
            return new FourthOnboardingFragment();
        } else if (position == 4) {
            return new SixthOnboardingFragment();
        } else if( position ==5){
            return new FifthOnboardingFragment();
        } else {
            return new ContainerOnboardingFragment();//placeholder, it should never enter here
        }*/


    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
