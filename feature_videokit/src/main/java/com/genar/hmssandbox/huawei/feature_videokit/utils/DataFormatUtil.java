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

import com.genar.hmssandbox.huawei.feature_videokit.R;
import com.genar.hmssandbox.huawei.feature_videokit.entity.PlayEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Process the data util
 */
public class DataFormatUtil {
    /**
     * Parse json data
     *
     * @param value Video json string value
     * @return Video list entity
     */
    public static List<PlayEntity> getPlayList(String value) {
        List<PlayEntity> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(value);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                if (jsonObject != null) {
                    PlayEntity playEntity = new PlayEntity();
                    if (TextUtils.isEmpty(jsonObject.optString("appId"))) {
                        playEntity.setAppId("104");
                    } else {
                        playEntity.setAppId(jsonObject.optString("appId"));
                    }
                    playEntity.setUrl(jsonObject.optString("url"));
                    playEntity.setUrlType(jsonObject.optInt("urlType"));
                    playEntity.setName(jsonObject.optString("name"));
                    playEntity.setVideoFormat(jsonObject.optInt("videoFormat"));
                    list.add(playEntity);
                }
            }
        } catch (JSONException e) {
            LogUtil.i("parse local data error:" + e.getMessage());
        }
        return list;
    }

    /**
     * Access to assets in the XML data
     *
     * @param context Context
     * @return Video list entity
     */
    public static List<PlayEntity> getPlayList(Context context) {
        try {
            List<PlayEntity> playList =
                DataFormatUtil.getPlayList(FileUtil.parseAssetsFile(context, FileUtil.ENCODE_UTF_8));
            return playList;
        } catch (Exception e) {
            LogUtil.i("get play list error : " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Get play speed string value
     *
     * @param playSpeed Play speed
     * @return play speed string value
     */
    public static String getPlaySpeedString(float playSpeed) {
        if (isFloatEqual(playSpeed, 0.5f)) {
            return "0.5x";
        } else if (isFloatEqual(playSpeed, 0.75f)) {
            return "0.75x";
        } else if (isFloatEqual(playSpeed, 1.0f)) {
            return "1.0x";
        } else if (isFloatEqual(playSpeed, 1.25f)) {
            return "1.25x";
        } else if (isFloatEqual(playSpeed, 1.5f)) {
            return "1.5x";
        } else if (isFloatEqual(playSpeed, 1.75f)) {
            return "1.75x";
        } else if (isFloatEqual(playSpeed, 2.0f)) {
            return "2.0x";
        } else {
            return "1.0x";
        }
    }

    /**
     * Compare the two float values are equal
     *
     * @param originalValue Float value
     * @param targetValue Float value
     * @return Is equal
     */
    public static boolean isFloatEqual(float originalValue, float targetValue) {
        return Math.abs(originalValue - targetValue) < 0.05;
    }

    /**
     * Get video height text
     * @param context Context
     * @param videoHeight The video height
     * @return video height text
     */
    public static String getVideoQuality(Context context, int videoHeight) {
        switch (videoHeight) {
            case Constants.DISPLAY_HEIGHT_SMOOTH:
                return context.getString(R.string.fluency_definition, videoHeight);
            case Constants.DISPLAY_HEIGHT_SD:
                return context.getString(R.string.standard_definition, videoHeight);
            case Constants.DISPLAY_HEIGHT_HD:
                return context.getString(R.string.high_definition, videoHeight);
            case Constants.DISPLAY_HEIGHT_BLUE_RAY:
                return context.getString(R.string.super_definition, videoHeight);
            default:
                return context.getString(R.string.auto_definition);
        }
    }
}
