/**
 * Copyright 2020. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.genar.hmssandbox.huawei.feature_videokit.entity;

import java.io.Serializable;

/**
 * Play data entity class
 */
public class PlayEntity implements Serializable {
    private static final long serialVersionUID = -5470245891598196978L;

    /**
     * The video name
     */
    private String name;

    /**
     * The url type:
     * 0: a single broadcast address
     * 1: multiple broadcast address
     */
    private int urlType = 0;

    /**
     * The video url
     */
    private String url;

    /**
     * Huawei video ID
     */
    private String appId;

    /**
     * Video format
     */
    private int videoFormat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUrlType() {
        return urlType;
    }

    public void setUrlType(int urlType) {
        this.urlType = urlType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(int videoFormat) {
        this.videoFormat = videoFormat;
    }
}
