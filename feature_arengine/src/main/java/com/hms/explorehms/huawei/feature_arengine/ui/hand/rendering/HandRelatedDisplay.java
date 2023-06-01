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
package com.hms.explorehms.huawei.feature_arengine.ui.hand.rendering;

import com.huawei.hiar.ARHand;

import java.util.Collection;

public interface HandRelatedDisplay {

    /**
     * Init render.
     */
    void init();

    /**
     * Render objects, call per frame
     *
     * @param hands ARHands
     * @param projectionMatrix Camera projection matrix.
     */
    void onDrawFrame(Collection<ARHand> hands, float[] projectionMatrix);
}
