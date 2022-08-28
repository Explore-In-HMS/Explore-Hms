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
import com.hms.explorehms.databinding.FragmentMapKitGroundOverlayBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.GroundOverlayOptions;
import com.huawei.hms.maps.model.LatLng;


public class MapKitGroundOverlayFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return FragmentMapKitGroundOverlayBinding.inflate(getLayoutInflater()).getRoot();
    }


    @Override
    public void initializeUI() {
        LatLng latLng = new LatLng(41.130015078278966, 29.084749128736057);
        hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        setImages();
    }


    public void setImages() {
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_bridge);
        GroundOverlayOptions options1 = new GroundOverlayOptions().position(
                new LatLng(41.202703, 29.112048), 2000, 1000).image(descriptor);
        GroundOverlayOptions options2 = new GroundOverlayOptions().position(
                new LatLng(41.091284, 29.060929), 2000, 1000).image(descriptor);
        GroundOverlayOptions options3 = new GroundOverlayOptions().position(
                new LatLng(41.045499, 29.034380), 2000, 1000).image(descriptor);
        hMap.addGroundOverlay(options1);
        hMap.addGroundOverlay(options2);
        hMap.addGroundOverlay(options3);
    }
}