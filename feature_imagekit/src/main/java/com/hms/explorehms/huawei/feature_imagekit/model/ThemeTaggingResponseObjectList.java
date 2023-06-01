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

package com.hms.explorehms.huawei.feature_imagekit.model;

import java.io.Serializable;

public class ThemeTaggingResponseObjectList implements Serializable {
    public int type;
    public Float possibility;
    public ThemeTaggingResponseObjectListBox box;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Float getPossibility() {
        return possibility;
    }

    public void setPossibility(Float possibility) {
        this.possibility = possibility;
    }

    public ThemeTaggingResponseObjectListBox getBox() {
        return box;
    }

    public void setBox(ThemeTaggingResponseObjectListBox box) {
        this.box = box;
    }
}
