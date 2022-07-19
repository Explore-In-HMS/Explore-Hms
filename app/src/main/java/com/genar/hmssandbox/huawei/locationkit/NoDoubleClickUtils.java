package com.genar.hmssandbox.huawei.locationkit;

public final class NoDoubleClickUtils {
    private static final int SPACE_TIME = 500;

    private static long lastClickTime;

    public NoDoubleClickUtils() {
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= SPACE_TIME;
    }
}
