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

package com.hms.explorehms.huawei.feature_connectapi.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hms.explorehms.huawei.feature_connectapi.fragment.CommentsApiFragment;
import com.hms.explorehms.huawei.feature_connectapi.fragment.ConnectApiOverviewFragment;
import com.hms.explorehms.huawei.feature_connectapi.fragment.PMSApiFragment;
import com.hms.explorehms.huawei.feature_connectapi.fragment.ProjectManagmentFragment;
import com.hms.explorehms.huawei.feature_connectapi.fragment.PublishingApiFragment;
import com.hms.explorehms.huawei.feature_connectapi.fragment.ReporsApiFragment;

public class ViewPagerTabsAdapter extends FragmentStateAdapter {

    public ViewPagerTabsAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = ConnectApiOverviewFragment.newInstance();
                break;
            case 1:
                fragment = CommentsApiFragment.newInstance();

                break;
            case 2:
                fragment = PMSApiFragment.newInstance();

                break;
            case 3:
                fragment = ProjectManagmentFragment.newInstance();

                break;
            case 4:
                fragment = PublishingApiFragment.newInstance();

                break;
            case 5:
                fragment = ReporsApiFragment.newInstance();
                break;
        }
        return fragment;
    }


    @Override
    public int getItemCount() {
        // Check and Change tab count for new feature
        return 5;
    }
}
