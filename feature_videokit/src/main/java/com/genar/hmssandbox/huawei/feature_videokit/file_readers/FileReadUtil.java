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
package com.genar.hmssandbox.huawei.feature_videokit.file_readers;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileReadUtil {

    private static final String FILE_NAME = "video_kit_demo.txt";

    public FileReadUtil() {
    }

    public static String getJSONData(Context context, String textFileName) {
        String strJSON;
        StringBuilder buf = new StringBuilder();
        InputStream json;
        try {
            json = context.getAssets().open(textFileName);

            BufferedReader in = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8));

            while ((strJSON = in.readLine()) != null) {
                buf.append(strJSON);
            }
            in.close();
        } catch (IOException e) {
            e.getLocalizedMessage();
        }

        return buf.toString();
    }

    public static List<VideoItemClass> videoListRetriever(Context context) throws JSONException {
        String jsonString = getJSONData(context, FILE_NAME) ;
        JSONObject obj = new JSONObject(jsonString);
        JSONArray arr = obj.getJSONArray("videos");
        List<VideoItemClass> videoList = new ArrayList<>();


        for (int i = 0; i < arr.length(); i++){
            VideoItemClass videoItem = new VideoItemClass();
            videoItem.setDescription(arr.getJSONObject(i).getString("description"));
            videoItem.setSources(arr.getJSONObject(i).getString("sources"));
            videoItem.setTitle(arr.getJSONObject(i).getString("title"));
            videoItem.setThumb(arr.getJSONObject(i).getString("thumb"));
            videoItem.setSubtitle(arr.getJSONObject(i).getString("subtitle"));

            videoList.add(videoItem);
        }
        return videoList;
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
