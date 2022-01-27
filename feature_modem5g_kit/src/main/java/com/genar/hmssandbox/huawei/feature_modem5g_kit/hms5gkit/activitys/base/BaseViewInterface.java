/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.base;

import android.os.Bundle;

public interface BaseViewInterface {
    void init(Bundle savedInstanceState);
    void initView();
    void initData();
    void initNav();
    void regReceiver();
    void unRegReceiver();
}
