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

package com.genar.hmssandbox.huawei.feature_gameservice.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewbinding.ViewBinding;

import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.EventsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;

abstract class BaseFragmentGameServices<T extends ViewBinding> extends Fragment {

    protected AuthHuaweiId authHuaweiId = HuaweiIdAuthManager.getAuthResult();
    protected EventsClient eventsClient;

    protected NavController navController;
    protected T view;

    abstract T bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    abstract void initializeUI();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = bindView(inflater, container, savedInstanceState);
        }
        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        JosAppsClient client = JosApps.getJosAppsClient(requireActivity());
        client.init();
        eventsClient = Games.getEventsClient(requireActivity());

        navController = Navigation.findNavController(view);


        initializeUI();
    }

    protected void setTitle(String title) {
        getActivity().setTitle(title);
    }
}

