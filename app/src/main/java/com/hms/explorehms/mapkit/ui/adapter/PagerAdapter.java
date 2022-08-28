/*
 * Copyright 2020. Explore in HMS. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hms.explorehms.mapkit.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hms.explorehms.mapkit.myEnums.ActivityTitle;
import com.hms.explorehms.mapkit.ui.CyclingFragment;
import com.hms.explorehms.mapkit.ui.DrivingFragment;
import com.hms.explorehms.mapkit.ui.WalkingFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PagerAdapter extends FragmentPagerAdapter {
    private final int totalItem;

    @NotNull
    public Fragment getItem(int position) {
        Fragment fm;
        switch (position) {
            case 0:
                fm = new WalkingFragment();
                break;
            case 1:
                fm = new DrivingFragment();
                break;
            case 2:
                fm = new CyclingFragment();
                break;
            default:
                fm = new WalkingFragment();
        }

        return fm;
    }

    public int getCount() {
        return this.totalItem;
    }

    @Nullable
    public CharSequence getPageTitle(int position) {
        CharSequence cs;
        switch (position) {
            case 0:
                cs = ActivityTitle.WALK.getType();
                break;
            case 1:
                cs = ActivityTitle.DRIVE.getType();
                break;
            case 2:
                cs = ActivityTitle.CYCLE.getType();
                break;
            default: cs = ActivityTitle.WALK.getType();
        }

        return cs;
    }

    public PagerAdapter(FragmentManager fm) {
        super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.totalItem = 3;
    }
}
