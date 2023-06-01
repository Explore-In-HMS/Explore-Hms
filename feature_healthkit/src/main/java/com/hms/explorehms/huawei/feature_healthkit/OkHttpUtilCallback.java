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

package com.hms.explorehms.huawei.feature_healthkit;

import android.util.Log;

import java.io.IOException;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *  Callback for OkHttp request
 *
 * @since 2020-09-27
 */
public class OkHttpUtilCallback implements Callback {
    private static final String TAG = "OkHttpUtilCallback";

    /**
     * Request error code
     */
    public static final String REQUEST_ERROR = "500";

    private final Consumer<String> consumer;

    public OkHttpUtilCallback(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    // If request fail, make a toast to indicate the failure.
    @Override
    public void onFailure(Call call, IOException e) {

        String stringBuilder = "Request error: " + call.request().url().toString() +
                " " +
                e.getMessage();
        Log.e(TAG, stringBuilder);
        consumer.accept(REQUEST_ERROR);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.d(TAG, "onResponse: " + response.toString());
        if (consumer == null) {
            return;
        }

        // Check whether the request is successful. If yes, invoke the Consumer to process the response. Otherwise, pass
        // REQUEST_ERROR code.
        if (response.isSuccessful() && (response.body() != null)) {
            consumer.accept(response.body().string());
        } else {
            consumer.accept(REQUEST_ERROR);
        }
    }
}