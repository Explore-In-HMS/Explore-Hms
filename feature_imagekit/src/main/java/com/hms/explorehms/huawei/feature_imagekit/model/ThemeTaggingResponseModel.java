/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.hms.explorehms.huawei.feature_imagekit.model;

import java.io.Serializable;
import java.util.List;

public class ThemeTaggingResponseModel implements Serializable {
    public int resultCode;
    public String serviceId;
    public String requestId;
    public List<ThemeTaggingResponseTag> tags;
    public List<ThemeTaggingResponseObjectList> objectList;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<ThemeTaggingResponseTag> getTags() {
        return tags;
    }

    public void setTags(List<ThemeTaggingResponseTag> tags) {
        this.tags = tags;
    }

    public List<ThemeTaggingResponseObjectList> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<ThemeTaggingResponseObjectList> objectList) {
        this.objectList = objectList;
    }
}
