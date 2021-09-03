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
package com.genar.hmssandbox.huawei.feature_videokit.file_readers;

public class VideoItemClass {
    String description;
    String sources;
    String subtitle;
    String thumb;
    String title;

    public VideoItemClass() {
    }

    public VideoItemClass(String description, String sources, String subtitle, String thumb, String title) {
        this.description = description;
        this.sources = sources;
        this.subtitle = subtitle;
        this.thumb = thumb;
        this.title = title;
    }

    public VideoItemClass(String description, String sources, String thumb, String title) {
        this.description = description;
        this.sources = sources;
        this.thumb = thumb;
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
