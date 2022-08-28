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
package com.hms.explorehms.huawei.feature_mlkit.ui.tabsfragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
                fragment = TextRelatedServicesFragment.newInstance();
                break;
            case 1:
                fragment = LanguageRelatedServicesFragment.newInstance();
                break;
            case 2:
                fragment = ImageRelatedServicesFragment.newInstance();
                break;
            case 3:
                fragment = FaceAndBodyRelatedServicesFragment.newInstance();
                break;
            default:
                break;
        }
        return fragment;
    }


    @Override
    public int getItemCount() {
        // Check and Change tab count for new feature
        return 4;
    }
}
