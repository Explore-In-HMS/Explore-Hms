package com.kcode.lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
public class PublicFunctionUtils {
    private final static String NAME = "app_update";

    public static long getLastCheckTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preferences.getLong("update_time", 0);
    }

    public static void setLastCheckTime(Context context, long time) {
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putLong("update_time", time).apply();
    }
}
