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

package com.hms.explorehms.huawei.feature_imagekit.application;

import android.content.Context;
import android.graphics.Bitmap;

import com.huawei.hms.image.vision.bean.ImageLayoutInfo;

public class AppSession {

    private static AppSession mInstance;

    private Context imageLayoutResultContext;
    private ImageLayoutInfo imageLayoutInfo;
    private Bitmap imageLayoutBitmap;

    public static AppSession getInstance(){
        if(mInstance == null){
            mInstance = new AppSession();
        }
        return mInstance;
    }

    public Context getImageLayoutResultContext() {
        return imageLayoutResultContext;
    }

    public void setImageLayoutResultContext(Context imageLayoutResultContext) {
        this.imageLayoutResultContext = imageLayoutResultContext;
    }

    public ImageLayoutInfo getImageLayoutInfo() {
        return imageLayoutInfo;
    }

    public void setImageLayoutInfo(ImageLayoutInfo imageLayoutInfo) {
        this.imageLayoutInfo = imageLayoutInfo;
    }


    public Bitmap getImageLayoutBitmap() {
        return imageLayoutBitmap;
    }

    public void setImageLayoutBitmap(Bitmap imageLayoutBitmap) {
        this.imageLayoutBitmap = imageLayoutBitmap;
    }
}
