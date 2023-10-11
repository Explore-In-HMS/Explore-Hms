package com.kcode.lib.utils;

import android.content.Context;
import android.widget.Toast;
public class ToastUtils {

    public static void show(Context context, int msgId) {
        if (context != null)
            show(context, context.getResources().getString(msgId));
    }

    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
