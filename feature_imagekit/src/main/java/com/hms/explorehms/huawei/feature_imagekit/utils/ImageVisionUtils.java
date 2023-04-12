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

package com.hms.explorehms.huawei.feature_imagekit.utils;

import java.util.HashMap;
import java.util.Map;

public class ImageVisionUtils {
    private static HashMap<String, String> filterNames;

    private ImageVisionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, String> getFilters() {
        if (filterNames == null) {
            filterNames = new HashMap<>();
            filterNames.put("0", "Original");
            filterNames.put("1", "Black & White");
            filterNames.put("2", "Brown tone");
            filterNames.put("3", "Lazy");
            filterNames.put("4", "Freesia");
            filterNames.put("5", "Fuji");
            filterNames.put("6", "Peach pink");
            filterNames.put("7", "Sea salt");
            filterNames.put("8", "Mint");
            filterNames.put("9", "Reed");
            filterNames.put("10", "Vintage");
            filterNames.put("11", "Marshmallow");
            filterNames.put("12", "Moss");
            filterNames.put("13", "Sunlight");
            filterNames.put("14", "Time");
            filterNames.put("15", "Haze blue");
            filterNames.put("16", "Sunflower");
            filterNames.put("17", "Hard");
            filterNames.put("18", "Bronze yellow");
            filterNames.put("19", "Monochromic tone");
            filterNames.put("20", "Yellow-green tone");
            filterNames.put("21", "Yellow tone");
            filterNames.put("22", "Green tone");
            filterNames.put("23", "Cyan tone");
            filterNames.put("24", "Violet tone");
        }

        return filterNames;
    }

    public static String[] getStickerAdapter() {

        return new String[]{
                "sticker_1_editable.png",
                "sticker_2_editable.png",
                "sticker_3_editable.png",
                "sticker_4_editable.png",
                "sticker_5_editable.png",
                "sticker_6_editable.png",
                "sticker_7_editable.png",
                "sticker_8_editable.png",
                "sticker_9_editable.png",
                "sticker_10_editable.png",
                "sticker_11_editable.png",
                "sticker_12_editable.png",
                "sticker_13_editable.png",
                "sticker_14_editable.png",
                "sticker_15_editable.png",
                "sticker_16_editable.png",
        };
    }

    public static String getTypeOfTheme(int type) {
        String t = "NONE";

        switch (type) {
            case 0:
                t = "OTHER";
                break;
            case 1:
                t = "GOODS";
                break;
            case 2:
                t = "FOOD";
                break;
            case 3:
                t = "FURNITURE";
                break;
            case 4:
                t = "PLANT";
                break;
            case 5:
                t = "PLACE";
                break;
            case 6:
                t = "FACE";
                break;
            default:
                break;
        }

        return t;
    }
}
