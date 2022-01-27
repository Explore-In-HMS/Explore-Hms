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

package com.genar.hmssandbox.huawei.reference.paidapps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.genar.hmssandbox.huawei.R;


public class AppGalleryDRMServiceLiteSDKFragment extends Fragment {

    public AppGalleryDRMServiceLiteSDKFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view3 = inflater.inflate(R.layout.fragment_app_gallery_drm_service_lite_sdk, container, false);
        TextView descAppGalleryDrmServiceLiteSdk = view3.findViewById(R.id.descAppGalleryDrmServiceLiteSdk);
        descAppGalleryDrmServiceLiteSdk.setText(R.string.desc_app_gallery_drm_service_lite_sdk);
        return view3;
    }
}