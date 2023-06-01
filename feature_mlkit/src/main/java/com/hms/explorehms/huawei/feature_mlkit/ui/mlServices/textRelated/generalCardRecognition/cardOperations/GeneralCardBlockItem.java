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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.cardOperations;

import android.graphics.Rect;

/**
 * Re-encapsulate the return result of OCR in Block
 */
public class GeneralCardBlockItem {
    public final String text;
    public final Rect rect;

    public GeneralCardBlockItem(String text, Rect rect) {
        this.text = text;
        this.rect = rect;
    }
}
