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

package com.hms.explorehms.huawei.ui.mediaexport.utils;

import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.FRAME_RATE_24;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.FRAME_RATE_25;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.FRAME_RATE_30;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.FRAME_RATE_50;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.FRAME_RATE_60;

import android.util.Size;

public class ExportUtils {
    public static Size convertProgressToResolution(int progress) {
        switch (progress) {
            case 0:
                return new Size(853, 480);
            case 1:
                return new Size(1280, 720);
            case 3:
                return new Size(2560, 1440);
            case 4:
                return new Size(3840, 2160);
            default:
            case 2:
                return new Size(1920, 1080);
        }
    }

    public static int convertProgressToFrameRate(int progress) {
        switch (progress) {
            case 0:
                return FRAME_RATE_24;
            case 2:
                return FRAME_RATE_30;
            case 3:
                return FRAME_RATE_50;
            case 4:
                return FRAME_RATE_60;
            case 1:
            default:
                return FRAME_RATE_25;
        }
    }
}
