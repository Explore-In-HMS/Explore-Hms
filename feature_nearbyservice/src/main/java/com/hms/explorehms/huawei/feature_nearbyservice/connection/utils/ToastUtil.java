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

package com.hms.explorehms.huawei.feature_nearbyservice.connection.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Display util.
 *
 * @since 2020-01-13
 */
public class ToastUtil {


    private ToastUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static Toast toast;

    /**
     * short time display, at the top of the page.
     *
     * @param msg message to display.
     */
    public static synchronized void showShortToastTop(Context context, String msg) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
            } else {
                toast.setText(msg);
            }
            toast.show();
    }

    /**
     * Short time, bottom.
     *
     * @param msg message to be display.
     */
    public static synchronized void showShortToast(Context context, String msg) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
    }

    /**
     * Long time, bottom.
     *
     * @param msg message to be display.
     */
    public static synchronized void showLongToast(Context context, String msg) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.show();
    }
}
