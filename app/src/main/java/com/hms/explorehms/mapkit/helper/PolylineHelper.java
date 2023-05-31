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

package com.hms.explorehms.mapkit.helper;

import android.graphics.Color;

import com.hms.explorehms.mapkit.model.response.DirectionResponse;
import com.hms.explorehms.mapkit.model.response.Path;
import com.huawei.hms.maps.model.Polyline;
import com.hms.explorehms.mapkit.model.response.Route;
import com.hms.explorehms.mapkit.model.response.Step;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * This helper class for draw a polyline.
 * First it determines the path, then it determines route for the path. It then draws a polyline for this route.
 */
public class PolylineHelper {

    private Polyline mPolyline;

    // TODO drawing polyline on map.
    public void drawPolyline(DirectionResponse directionResponse, HuaweiMap hMap) {
        ArrayList<LatLng> pathList = new ArrayList<>();
        if (directionResponse.getRoutes() != null && !directionResponse.getRoutes().isEmpty()) {
            Route route = directionResponse.getRoutes().get(0);
            if (route.getPaths() != null) {
                findRoute(route, pathList);
            }
        }
        mPolyline = hMap.addPolyline(
                new PolylineOptions().addAll(pathList).color(Color.BLUE).width(4f)
        );
    }

    /**
     * It finds a route for path
     */
    private void findRoute(
            Route route,
            ArrayList<LatLng> pathList
    ) {
        for (Path i : route.getPaths()) {
            if (i.getSteps() != null) {
                findPath(i, pathList);
            }

        }
    }

    /**
     * It finds path for lat-lng
     */
    private void findPath(
            Path path,
            ArrayList<LatLng> pathList
    ) {
        for (Step j : path.getSteps()) {
            if (j.getPolyline() != null && !j.getPolyline().isEmpty()) {
                for (com.hms.explorehms.mapkit.model.response.Polyline k : j.getPolyline()) {
                    pathList.add(new LatLng(k.getLat(), k.getLng()));
                }
            }
        }
    }
}
