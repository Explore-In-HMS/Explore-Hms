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
package com.genar.hmssandbox.huawei.feature_adskit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;

public class Utils {


    private Utils() {
        // private constructor
    }

    private static final String TAG = Utils.class.getSimpleName();
    public static String contentBundle = "";
    public static final int ACTIVATE_STYLE = 2; //1->Confirmation pop-up  2->Banner at the bottom


    static {
        try {
            //Sample string for setContentBundle() method
            contentBundle = new JSONObject()
                    .put("channelCategoryCode","TV series")
                    .put("title","Game of Thrones")
                    .put("tags","fantasy")
                    .put("relatedPeople","David Benioff")
                    .put("content","Nine noble families fight for control over the lands of Westeros.")
                    .put("contentID","123123")
                    .put("category","classics")
                    .put("subcategory","historical drama")
                    .put("thirdCategory","mystery")
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param max
     * @param min
     * @return randomNumber between min - max
     */
    public static int getRandomNumber(int max, int min) {
        int randomNum = new SecureRandom().nextInt((max - min) + 1) + min;
        Log.d(TAG, "getRandomNumber.randomNum : " + randomNum);
        return randomNum;
    }

}
