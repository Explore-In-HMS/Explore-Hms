/**
 * Copyright 2021. Explore in HMS. All rights reserved.
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

/**
 * Bitrate info
 */
public class BitrateInfo {
    private int minBitrate;

    private int maxBitrate;

    private int currentBitrate;

    private int videoHeight;

    public int getMinBitrate() {
        return minBitrate;
    }

    public void setMinBitrate(int minBitrate) {
        this.minBitrate = minBitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    public int getCurrentBitrate() {
        return currentBitrate;
    }

    public void setCurrentBitrate(int currentBitrate) {
        this.currentBitrate = currentBitrate;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    @Override
    public String toString() {
        return "BitrateInfo{" +
                "minBitrate=" + minBitrate +
                ", maxBitrate=" + maxBitrate +
                ", currentBitrate=" + currentBitrate +
                ", videoHeight=" + videoHeight +
                '}';
    }
}
