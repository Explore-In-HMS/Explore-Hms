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

package com.hms.explorehms.huawei.feature_imagekit.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hms.explorehms.huawei.feature_imagekit.ui.fragment.ImageRenderServicesMain;
import com.hms.explorehms.huawei.feature_imagekit.ui.fragment.ImageVisionServicesMain;

public class ViewPagerTabsAdapterImagekit extends FragmentStateAdapter {
    private static final int FRAGMENT_NUM = 2;
    public ViewPagerTabsAdapterImagekit(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public Fragment createFragment(int position) {
        Fragment fragmentsImageKit = null;

        if(position == 0){
            fragmentsImageKit = new ImageVisionServicesMain();
        }else if(position ==1){
            fragmentsImageKit = new ImageRenderServicesMain();
        }

        return fragmentsImageKit;
    }

    public int getItemCount() {
        return FRAGMENT_NUM;
    }
}
