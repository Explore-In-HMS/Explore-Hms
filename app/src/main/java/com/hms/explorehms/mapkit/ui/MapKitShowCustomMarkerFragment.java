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

package com.hms.explorehms.mapkit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentMapKitShowCustomMarkerBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.hms.explorehms.mapkit.data.MarkerData;
import com.hms.explorehms.mapkit.ui.adapter.InfoWindowAdapter;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;


public class MapKitShowCustomMarkerFragment extends BaseFragment {
    private int count = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentMapKitShowCustomMarkerBinding.inflate(getLayoutInflater()).getRoot();
    }

    @Override
    public void initializeUI() {
        hMap.setInfoWindowAdapter(new InfoWindowAdapter(getContext()));
        hMap.setOnMapClickListener(this::clickedOnMap);
    }

    private void addMarker(MarkerData markerData) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(markerData.getLat(), markerData.getLng()))
                .clusterable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        Marker marker = hMap.addMarker(options);
        marker.setTag(markerData);

        hMap.setMarkersClustering(true);
    }

    private void clickedOnMap(LatLng latLng) {
        MarkerData markerData = new MarkerData();
        markerData.setLat(latLng.latitude);
        markerData.setLng(latLng.longitude);
        markerData.setTitle(String.format("Marker - %d", count));
        markerData.setSnippet(latLng.latitude + " - " + latLng.longitude);
        this.count++;
        addMarker(markerData);
    }
}