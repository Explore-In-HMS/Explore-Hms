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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView;

public class FrameMetadata {

    private final int width;

    private final int height;

    private final int rotation;

    private final int cameraFacing;

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRotation() {
        return this.rotation;
    }

    public int getCameraFacing() {
        return this.cameraFacing;
    }

    /**
     * @param width
     * @param height
     * @param rotation : portrait or landscape camera orientation
     * @param facing   : front or back camera facing
     */
    private FrameMetadata(int width, int height, int rotation, int facing) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.cameraFacing = facing;
    }

    public static class Builder {
        private int width;

        private int height;

        private int rotation;

        private int cameraFacing;

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setRotation(int rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setCameraFacing(int facing) {
            this.cameraFacing = facing;
            return this;
        }

        public FrameMetadata build() {
            return new FrameMetadata(this.width, this.height, this.rotation, this.cameraFacing);
        }
    }
}