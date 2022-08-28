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

package com.hms.explorehms.huawei.feature_networkkit;

import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.network.httpclient.Callback;
import com.huawei.hms.network.httpclient.HttpClient;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.Submit;
import com.huawei.hms.network.restclient.RestClient;

import java.io.IOException;
import java.util.HashMap;


public class RestClientSample {
    private static final String TAG = "RestClientAnnoSample";
    private static final String URL = "https://developer.huawei.com";
    private RestClient restClient;
    private SampleService sampleService;
    private Callback<String> callback;
    private EventListener eventListener;

    public RestClientSample(EventListener listener) {
        Log.i(TAG, "RestClientSample create");
        this.eventListener = listener;

        // Create a RestClient instance.
        restClient = new RestClient.Builder()
                // (Optional) Set a base URL, which will be concatenated before the URL set in the POST and GET annotation.
                .baseUrl(URL)
                // (Optional) Add the adapter factory for Submit, that is, encapsulate Submit again to adapt to other SDKs.
                // .addSubmitAdapterFactory(Related factory)
                // (Optional) Set to return the callback thread pool where ResultCallback is located.
                // .callbackExecutor(Executor)
                .httpClient(new HttpClient.Builder().build())
                // (Optional) Set whether to initialize all APIs in advance. If true is passed, all APIs will be initiated when RestClient.create(SampleService.class) is called. If false is passed, each API will be initiated when it is called the first time.
                .validateEagerly(true)
                .build();

        // Initialize the request API instance.
        sampleService = restClient.create(SampleService.class);
        createCallback();
    }

    String getResponseInfo(Response<?> responseObj) {
        String responseInfo = "";
        responseInfo += "response Code" + " -> ";
        responseInfo += responseObj.getCode() + System.lineSeparator();
        responseInfo += "response url" + " -> ";
        responseInfo += responseObj.getUrl() + System.lineSeparator();
        if (!TextUtils.isEmpty(responseObj.getMessage())) {
            responseInfo += "response getMessage" + " -> ";
            responseInfo += responseObj.getMessage() + System.lineSeparator();
        }
        return responseInfo;
    }

    void processResponse(Response<?> response, String detail) {
        String callStr = getResponseInfo(response);
        if (eventListener != null) {
            eventListener.onSuccess(callStr);
        }
        Log.i(TAG, "response onResponse : " + callStr + "\n\ndetail : " + detail);
    }

    /**
     * Create an asynchronous request callback object.
     */
    public void createCallback() {
        callback = new Callback<String>() {
            @Override
            public void onResponse(Submit<String> submit, Response<String> response)
                    throws IOException {
                String body = "";
                if (response != null && response.getBody() != null) {
                    body = response.getBody();
                    processResponse(response, body);
                }
            }

            @Override
            public void onFailure(Submit<String> submit, Throwable exception) {
                String errorMsg = "response onFailure : ";
                if (exception != null) {
                    errorMsg += exception.getMessage();
                    if (exception.getCause() != null) {
                        errorMsg += ", cause : " + exception.getMessage();
                    }
                }
                if (eventListener != null) {
                    eventListener.onException(errorMsg);
                }
            }
        };

    }

    /**
     * Use RestClient (annotation mode) to send a synchronous request.
     */
    public void annoExecute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<String> response = sampleService.getTest(URL).execute();
                    processResponse(response, "");
                } catch (Exception e) {
                    if (eventListener != null) {
                        eventListener.onException("response execute failed : " + e.getMessage());
                    }
                    Log.w(TAG, "response onFailure : " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Use RestClient (annotation mode) to send an asynchronous request.
     */
    public void annoEnqueue() {
        sampleService.getTest(URL).enqueue(callback);
    }

    /**
     * Use an API object with the @QueryMap annotation to send a synchronous request.
     */
    public void annoQueryExecute() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("password", "password");
                    hashMap.put("username", "username");

                    Response<String> response = sampleService.getQueryMap(URL, hashMap).execute();
                    Log.i(TAG, "response getCode  = " + response.getCode());
                    Log.i(TAG, "response getUrl = " + response.getUrl());
                    Log.i(TAG, "response getMessage = " + response.getMessage());
                    Log.i(TAG, "response = " + response.getBody());
                } catch (Exception e) {
                    Log.w(TAG, "response onFailure  = " + e.getMessage());
                }
            }
        }).start();
    }
}
