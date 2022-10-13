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
import com.huawei.hms.network.httpclient.Interceptor;
import com.huawei.hms.network.httpclient.Request;
import com.huawei.hms.network.httpclient.Response;
import com.huawei.hms.network.httpclient.ResponseBody;
import com.huawei.hms.network.httpclient.Submit;

import java.io.IOException;
import java.nio.charset.Charset;

public class HttpClientSample {
    private static final String TAG = "HttpClientSample";
    private static final String URL = "https://developer.huawei.com";
    private HttpClient httpClient;
    private Request request;
    private Callback<ResponseBody> callback;
    private EventListener eventListener;

    public HttpClientSample(EventListener eventListener) {
        this.eventListener = eventListener;
        // Create an HttpClient object as needed.
        createClient();
        // Create a Request object as needed.
        createRequest();
        createCallback();
    }

    /**
     * Create an HttpClient object.
     *
     * @return HttpClient object.
     */
    public HttpClient createClient() {
        httpClient = new HttpClient.Builder()
                // (Optional) Set the request timeout interval, in milliseconds.
                .callTimeout(10000)
                // (Optional) Set the connect timeout interval, in milliseconds.
                .connectTimeout(10000)
                // (Optional) Execute only once upon each call.
                .addInterceptor(new TestCustomInterceptor())
                // (Optional) Determine the number of execution times based on the number of retry times.
                .addNetworkInterceptor(new TestCustomInterceptor())
                // (Optional) Set the read timeout interval, in milliseconds.
                .readTimeout(10000)
                // (Optional) Set the number of retry times.
                .retryTimeOnConnectionFailure(3)
                // (Optional) Set the writing timeout interval, in milliseconds.
                .writeTimeout(10000)
                // (Optional) Set whether to activate the QUIC protocol. By default, the QUIC protocol is disabled.
                .enableQuic(false)
                // (Optional) Set the SSL certificate.
                // .sslSocketFactory(xxx, xxx)
                // (Optional) Set the host name verification algorithm.
                // .hostnameVerifier(xxx)
                // (Optional) Set the HTTP protocol cache.
                // .cache(directory,maxSize)
                // (Optional) Set the HTTP proxy class.
                // .proxy(proxy)
                .build();

        return httpClient;
    }

    /**
     * Create a Request object.
     */
    public void createRequest() {
        Request request1 = httpClient.newRequest()
                .url(URL)
                .method("GET")
                .build();

        this.request = request1;
    }

    String getResponseInfo(Response<?> responseObj) {
        String callStr = "";
        callStr += "response Code" + " -> ";
        callStr += responseObj.getCode() + System.lineSeparator();
        callStr += "response url" + " -> ";
        callStr += responseObj.getUrl() + System.lineSeparator();
        if (!TextUtils.isEmpty(responseObj.getMessage())) {
            callStr += "response getMessage" + " -> ";
            callStr += responseObj.getMessage() + System.lineSeparator();
        }
        return callStr;
    }

    void processResponse(Response<?> response, String detail) {
        String callStr = getResponseInfo(response);
        if (eventListener != null) {
            eventListener.onSuccess(callStr);
        }
        Log.i(TAG, "response onResponse : " + callStr + "\n\ndetail:" + detail);
    }

    /**
     * Create an asynchronous request callback object.
     */
    public void createCallback() {
        this.callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Submit<ResponseBody> submit, Response<ResponseBody> response)
                    throws IOException {
                String body = "";
                if (response != null && response.getBody() != null) {
                    body = new String(response.getBody().bytes(), Charset.forName("UTF-8"));
                }
                processResponse(response, body);
            }

            @Override
            public void onFailure(Submit<ResponseBody> submit, Throwable throwable) {
                String errorMsg = "response onFailure : ";
                if (throwable != null) {
                    errorMsg += throwable.getMessage();
                    if (throwable.getCause() != null) {
                        errorMsg += ", cause : " + throwable.getMessage();
                    }
                }
                if (eventListener != null) {
                    eventListener.onException(errorMsg);
                }
            }
        };
    }

    /**
     * Use HttpClient to send a synchronous request.
     */
    public void httpClientExecute() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<ResponseBody> response = httpClient.newSubmit(request).execute();
                    processResponse(response, "");
                } catch (Exception e) {
                    if (eventListener != null) {
                        eventListener.onException("response newSubmit failed : " + e.getMessage());
                    }
                    Log.w(TAG, "response onFailure : " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Use HttpClient to send an asynchronous request.
     */
    public void httpClientEnqueue() {
        Submit<ResponseBody> submit = httpClient.newSubmit(request);
        submit.enqueue(callback);
    }

    private static class TestCustomInterceptor extends Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            return chain.proceed(chain.request());
        }
    }
}
