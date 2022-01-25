/**
 * Copyright 2020. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.genar.hmssandbox.huawei.feature_videokit.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * File tools
 */
public class FileUtil {
    public static final String PLAY_FILE_NAME = "video_kit_demo.txt";

    public static final String ENCODE_UTF_8 = "utf-8";

    /**
     * Get assets files in the directory
     *
     * @param context Context
     * @param charsetName Encode type
     * @return The file content string
     */
    public static String parseAssetsFile(Context context, String charsetName) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(context.getAssets().open(PLAY_FILE_NAME), charsetName);
            char[] fileBuffer = new char[1024];
            int count = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((count = reader.read(fileBuffer, 0, 1024)) > 0) {
                stringBuilder.append(fileBuffer, 0, count);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            LogUtil.i("get assets file error :" + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                LogUtil.i("close InputStreamReader error :" + e.getMessage());
            }
        }
        return StringUtil.emptyStringValue();
    }

    /**
     * Create file
     * @param filePath File path
     * @return Create or not
     */
    public static boolean createFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (!file.exists()) {
                return file.mkdirs();
            } else {
                return true;
            }
        }
        return false;
    }
}