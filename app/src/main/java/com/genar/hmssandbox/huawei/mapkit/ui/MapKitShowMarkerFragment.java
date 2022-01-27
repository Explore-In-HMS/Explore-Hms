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

package com.genar.hmssandbox.huawei.mapkit.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.databinding.FragmentMapKitShowMarkerBinding;
import com.genar.hmssandbox.huawei.mapkit.base.BaseFragment;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapKitShowMarkerFragment extends BaseFragment {
    private List<LatLng> dummyMarkerList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return FragmentMapKitShowMarkerBinding.inflate(getLayoutInflater()).getRoot();

    }

    @Override
    public void initializeUI() {
        initDummyMarkerList();
        addMarketToMap();
        hMap.getUiSettings().setMarkerClusterColor(Color.RED);
        hMap.getUiSettings().setMarkerClusterTextColor(Color.RED);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_mapkit);
        hMap.getUiSettings().setMarkerClusterIcon(icon);
        hMap.getUiSettings().setLogoPosition(Gravity.BOTTOM|Gravity.END);
        hMap.getUiSettings().setLogoPadding(10,10,50,30);
    }

    private void addMarketToMap() {
        for (LatLng latLng : dummyMarkerList) {
            hMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLng.latitude, latLng.longitude))
                    .title(latLng.toString())
                    .clusterable(true));
        }
        hMap.setMarkersClustering(true);
    }

    private void initDummyMarkerList() {
        dummyMarkerList = new ArrayList<>();
        dummyMarkerList.add(new LatLng(41.014570, 28.936722));
        dummyMarkerList.add(new LatLng(41.010448, 28.934392));
        dummyMarkerList.add(new LatLng(41.008182, 28.937911));
        dummyMarkerList.add(new LatLng(41.009651, 28.941996));
        dummyMarkerList.add(new LatLng(41.006443, 28.944181));
        dummyMarkerList.add(new LatLng(41.009206, 28.945253));
        dummyMarkerList.add(new LatLng(41.005253, 28.981806));
        dummyMarkerList.add(new LatLng(41.007763, 28.983726));
        dummyMarkerList.add(new LatLng(41.009512, 28.981859));
        dummyMarkerList.add(new LatLng(41.010851, 28.985548));
        dummyMarkerList.add(new LatLng(41.013039, 28.983513));
        dummyMarkerList.add(new LatLng(41.005470, 28.984879));
        dummyMarkerList.add(new LatLng(41.035455, 28.954376));
        dummyMarkerList.add(new LatLng(41.035980, 28.956637));
        dummyMarkerList.add(new LatLng(41.035540, 28.959335));
        dummyMarkerList.add(new LatLng(41.035057, 28.961424));
        dummyMarkerList.add(new LatLng(41.033884, 28.963033));
        dummyMarkerList.add(new LatLng(41.036781, 28.961960));
    }
}