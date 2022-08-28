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
package com.hms.explorehms.scankit.action;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.util.Log;

import com.huawei.hms.ml.scan.HmsScan;

public class LocationAction {

    private static final String TAG = "LocationAction";

    public static final String GAODE_PKG = "com.autonavi.minimap";

    private LocationAction() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean checkMapAppExist(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(GAODE_PKG, 0);
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return packageInfo != null;

    }

    public static Intent getLocationInfo(HmsScan.LocationCoordinate locationCoordinate) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("androidamap://viewMap?lat=" + locationCoordinate.getLatitude() + "&lon=" + locationCoordinate.getLongitude()));
    }
}