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
package com.hms.explorehms.huawei.feature_hiai.utils.hiai_service_utils;

import java.util.HashMap;
import java.util.Map;

public class TextRecognitionUtils {

    private TextRecognitionUtils() {
    }

    private static final HashMap<Integer, String> TEXT_LANGUAGES = new HashMap<>();

    public static Map<Integer, String> getTextLanguages() {
        if (TEXT_LANGUAGES.size() == 0) {
            TEXT_LANGUAGES.put(0, "CHINESE");
            TEXT_LANGUAGES.put(2, "OTHER");
            TEXT_LANGUAGES.put(6, "ENGLISH");
        }

        return TEXT_LANGUAGES;
    }
}
