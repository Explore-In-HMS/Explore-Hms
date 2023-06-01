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

package com.hms.explorehms.huawei.feature_scenekit.fluidSimulation3D;

import android.view.MotionEvent;
import android.view.View;

import com.huawei.hms.scene.sdk.render.FluidComponent;


public class GestureEvent implements View.OnTouchListener {
    private FluidComponent fluidComponent;
    private int surfaceWidth;
    private int surfaceHeight;
    private boolean sceneReady;

    public GestureEvent(FluidComponent fluidComponent, int surfaceWidth, int surfaceHeight) {
        this.fluidComponent = fluidComponent;
        this.surfaceWidth = surfaceWidth;
        this.surfaceHeight = surfaceHeight;
        sceneReady = fluidComponent != null && surfaceWidth != 0 && surfaceHeight != 0;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (!sceneReady) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                fluidComponent.setGesture(event.getX() / surfaceWidth, event.getY() / surfaceHeight);
                break;
            case MotionEvent.ACTION_UP:
                fluidComponent.setGesture(-1.0f, -1.0f);
                break;
            default:
                break;
        }
        return true;
    }
}
