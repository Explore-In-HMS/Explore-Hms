/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampUtils {
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    // get current time
    public static String getCurDateStr() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(FORMAT);
        return df.format(new Date());
    }
}
