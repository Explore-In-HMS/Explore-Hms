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

package com.hms.explorehms.mapkit.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.hms.explorehms.databinding.CustomMarkerInfoMapkitBinding;
import com.hms.explorehms.mapkit.data.Constants;
import com.hms.explorehms.mapkit.data.MarkerData;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.Marker;

public class InfoWindowAdapter implements HuaweiMap.InfoWindowAdapter {

    private final Context context;
    private CustomMarkerInfoMapkitBinding binding;

    public InfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        try {
            binding = CustomMarkerInfoMapkitBinding.inflate(LayoutInflater.from(context));
            MarkerData markerData = (MarkerData) marker.getTag();
            binding.title.setText(markerData.getTitle());
            binding.snippet.setText(markerData.getSnippet());
            return binding.getRoot();
        } catch (Exception e) {
            Log.d(Constants.TAG, "getInfoContents: " + e.getMessage());
            return null;
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}