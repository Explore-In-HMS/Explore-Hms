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

package com.genar.hmssandbox.huawei.feature_appgallerykit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;


public class InitializeAppFragment extends Fragment {


    public InitializeAppFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view4 = inflater.inflate(R.layout.fragment_initialize_app, container, false);
        TextView descAppGalleryKitInitializeApp = view4.findViewById(R.id.descAppGalleryKitInitializeApp);
        descAppGalleryKitInitializeApp.setText(R.string.desc_app_gallery_kit_initialize_app);
        init();
        return view4;
    }

    private void init() {
        JosAppsClient appsClient = JosApps.getJosAppsClient(getActivity());
        appsClient.init();
        Log.i("InitializeAppFragment", "init success");
    }
}