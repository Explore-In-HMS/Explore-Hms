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
package com.genar.hmssandbox.huawei.feature_hiai.utils.hiai_service_utils;

import java.util.HashMap;
import java.util.Map;

public class TextRecognitionUtils {

    private TextRecognitionUtils() {
    }

    private static final HashMap<Integer,String> TEXT_LANGUAGES = new HashMap<>();

    public static Map<Integer,String> getTextLanguages(){
        if(TEXT_LANGUAGES.size() == 0){
            TEXT_LANGUAGES.put(1,"CHINESE");
            TEXT_LANGUAGES.put(2,"SPANISH");
            TEXT_LANGUAGES.put(3,"ENGLISH");
            TEXT_LANGUAGES.put(4,"PORTUGUESE");
            TEXT_LANGUAGES.put(5,"ITALIAN");
            TEXT_LANGUAGES.put(6,"GERMAN");
            TEXT_LANGUAGES.put(7,"FRENCH");
            TEXT_LANGUAGES.put(8,"RUSSIAN");
            TEXT_LANGUAGES.put(9,"JAPANESE");
            TEXT_LANGUAGES.put(10,"KOREAN");
        }

        return TEXT_LANGUAGES;
    }
}
