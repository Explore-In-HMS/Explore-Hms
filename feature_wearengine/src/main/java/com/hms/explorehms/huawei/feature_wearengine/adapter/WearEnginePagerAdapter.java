/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hms.explorehms.huawei.feature_wearengine.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WearEnginePagerAdapter extends FragmentPagerAdapter {
    private final Map<Fragment, String> fragmentList;
    private final List<Fragment> listKeys;

    public WearEnginePagerAdapter(@NonNull FragmentManager fm, int behavior, Map<Fragment, String> fragmentList) {
        super(fm, behavior);
        this.fragmentList = fragmentList;
        Set<Fragment> keys = fragmentList.keySet();
        listKeys = new ArrayList<>(keys);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return listKeys.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(listKeys.get(position));
    }
}
