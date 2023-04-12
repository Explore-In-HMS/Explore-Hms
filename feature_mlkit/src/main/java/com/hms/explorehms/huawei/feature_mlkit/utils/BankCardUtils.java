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

package com.hms.explorehms.huawei.feature_mlkit.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BankCardUtils {

    private static final String TAG = Utils.class.getSimpleName();

    private BankCardUtils() {
        throw new IllegalStateException("BankCardUtils class");
    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getJsonFromFile(Context context, String fileName, boolean isBr) {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try {
            AssetManager assetManager = context.getAssets();
            bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName), StandardCharsets.UTF_8));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
                if (isBr) {
                    stringBuilder.append("\n");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "getJsonFromFile : Exception : " + e.getMessage(), e);
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "getJsonFromFile : finally Exception : " + e.getMessage(), e);
            }
        }
        return stringBuilder.toString();
    }

    public static String substringBetween(String var0, String var1, String var2) {
        if (var0 != null && var1 != null && var2 != null) {
            int var3;
            int var4;
            return (var3 = var0.indexOf(var1)) != -1 && (var4 = var0.indexOf(var2, var1.length() + var3)) != -1 ? var0.substring(var1.length() + var3, var4) : var0;
        } else {
            return var0;
        }
    }
}

