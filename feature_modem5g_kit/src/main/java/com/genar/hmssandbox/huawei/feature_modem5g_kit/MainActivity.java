/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit;

import android.os.Handler;

import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.base.PermissionBaseActivity;
import com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.impl.HmsKitActivity;


public class MainActivity extends PermissionBaseActivity {
    @Override
    public void initView() {
        requestPermission();
    }

    private void requestPermission() {
        int time = 0; // Set the waiting time in milliseconds
        verifyStoragePermissions(flag -> {
            Handler handler = new Handler();
            // When the timer ends, jump to the HmsKitActivity
            handler.postDelayed(() -> gotoActivity(HmsKitActivity.class, true), time);
        });
    }
}
