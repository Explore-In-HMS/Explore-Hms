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

package com.hms.explorehms.mapkit.myEnums;

import org.jetbrains.annotations.NotNull;

public enum ActivityTitle {
    WALK("Walking"),
    DRIVE("Driving"),
    CYCLE("Cycling");

    private final String type;

    ActivityTitle(String type) {
        this.type = type;
    }

    @NotNull
    public final String getType() {
        return this.type;
    }

}

