
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

package com.hms.explorehms.huawei.ui.mediaeditor.repository;

import com.hms.explorehms.huawei.ui.common.bean.CloudMaterialBean;

public class LoadUrlEvent {
    private CloudMaterialBean content;

    private int previousPosition;

    private int position;

    public LoadUrlEvent() {

    }

    public CloudMaterialBean getContent() {
        return content;
    }

    public void setContent(CloudMaterialBean content) {
        this.content = content;
    }

    public int getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(int previousPosition) {
        this.previousPosition = previousPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "LoadUrlEvent{" + "content=" + content + ", previousPosition=" + previousPosition + ", position="
            + position + '}';
    }
}
