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

package com.hms.explorehms.huawei.feature_modem5g_kit.hms5gkit.activitys.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityStack {
    private static final ActivityStack INSTANCE = new ActivityStack();

    private List<Activity> activities = new ArrayList<>();

    public static ActivityStack getInstance() {
        return INSTANCE;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
            activity.finish();
        }
    }
}
