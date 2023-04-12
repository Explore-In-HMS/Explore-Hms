/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hms.explorehms.mapkit.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.databinding.FragmentMapKitShapeBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Polygon;
import com.huawei.hms.maps.model.PolygonOptions;
import com.huawei.hms.maps.model.Polyline;
import com.huawei.hms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

/**
 * It shows Draw Shape feature of Map Kit
 */
public class MapKitShapeFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return FragmentMapKitShapeBinding.inflate(getLayoutInflater()).getRoot();
    }

    /**
     * It initializes UI with call drawPolyline, drawPolygon, drawCircle
     */
    @Override
    public void initializeUI() {
        drawPolyline();
        drawPolygon();
        drawCircle();
    }

    /**
     * It draws Polyline
     */
    private void drawPolyline() {
        Polyline polyline = hMap.addPolyline(new PolylineOptions()
                .add(new LatLng(41.031498, 28.926077), new LatLng(41.038655, 28.937485),
                        new LatLng(41.047788, 28.945449), new LatLng(41.051068, 28.949624),
                        new LatLng(41.054332, 28.957105), new LatLng(41.057383, 28.962532),
                        new LatLng(41.061487, 28.967030), new LatLng(41.066024, 28.971175),
                        new LatLng(41.067387, 28.975641), new LatLng(41.066759, 28.993129),
                        new LatLng(41.066589, 29.000941), new LatLng(41.066930, 29.010527),
                        new LatLng(41.065800, 29.013646), new LatLng(41.064041, 29.015264),
                        new LatLng(41.059502, 29.016569), new LatLng(41.057872, 29.018227),
                        new LatLng(41.053821, 29.025553), new LatLng(41.036674, 29.043273)
                )
                .color(Color.BLUE)
                .width(3));

        polyline.setClickable(true);
        hMap.setOnPolylineClickListener(polyline1 -> toast(polyline1.getId()));
    }

    /**
     * It draws Polygon
     */
    public void drawPolygon() {
        Polygon polygon1 = hMap.addPolygon(new PolygonOptions().addAll(createRectangle(
                        new LatLng(40.908864, 29.048583), 0.01, 0.01))
                .strokeColor(Color.BLACK));
        Polygon polygon2 = hMap.addPolygon(new PolygonOptions().addAll(createRectangle(
                        new LatLng(40.880623, 29.061795), 0.01, 0.01))
                .strokeColor(Color.BLACK));
        Polygon polygon3 = hMap.addPolygon(new PolygonOptions().addAll(createRectangle(
                        new LatLng(40.850820, 29.143952), 0.005, 0.005))
                .strokeColor(Color.BLACK));

        polygon1.setClickable(true);
        polygon2.setClickable(true);
        polygon3.setClickable(true);
        hMap.setOnPolygonClickListener(polygon -> {
            if (polygon.equals(polygon1))
                toast("Kınalıada");
            else if (polygon.equals(polygon2))
                toast("Burgazadası");
            else if (polygon.equals(polygon3))
                toast("Sedef Adası");
        });
    }

    /**
     * It draws Circle
     */
    public void drawCircle() {
        Circle circle1 = hMap.addCircle(new CircleOptions()
                .center(new LatLng(41.275869, 28.725577))
                .radius(3000)
                .strokeColor(Color.MAGENTA));
        Circle circle2 = hMap.addCircle(new CircleOptions()
                .center(new LatLng(40.981512, 28.818513))
                .radius(2200)
                .strokeColor(Color.RED));
        Circle circle3 = hMap.addCircle(new CircleOptions()
                .center(new LatLng(40.898310, 29.308752))
                .radius(2200)
                .fillColor(Color.GRAY));
        circle2.setClickable(true);
        circle1.setClickable(true);
        circle3.setClickable(true);

        hMap.setOnCircleClickListener(circle -> {
            if (circle.equals(circle1))
                toast("Istanbul Airport");
            else if (circle.equals(circle2))
                toast("Atatürk Havalimanı");
            else if (circle.equals(circle3))
                toast("Sabiha Gokcen International Airport");
        });
    }

    /**
     * It creates Rectangle
     */
    public List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth));
    }
}