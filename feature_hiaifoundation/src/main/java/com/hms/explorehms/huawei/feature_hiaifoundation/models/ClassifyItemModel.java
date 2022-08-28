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

package com.hms.explorehms.huawei.feature_hiaifoundation.models;

import android.graphics.Bitmap;

public class ClassifyItemModel {
    private final String top1Result;
    private final String otherResults;
    private final String classifyTime;
    private final Bitmap classifyImg;

    public ClassifyItemModel(String top1Result, String otherResults, String classifyTime, Bitmap classifyImg) {
        this.top1Result = top1Result;
        this.otherResults = otherResults;
        this.classifyTime = classifyTime;
        this.classifyImg = classifyImg;
    }

    public String getTop1Result() {
        return top1Result;
    }

    public String getOtherResults() {
        return otherResults;
    }

    public String getClassifyTime() {
        return classifyTime;
    }

    public Bitmap getClassifyImg() {
        return classifyImg;
    }
}


