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

package com.genar.hmssandbox.huawei.reference.hquic;

import android.content.Context;
import android.util.Log;

import com.huawei.hms.hquic.HQUICManager;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.net.MalformedURLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HQUICService {
    private static final String TAG = "HQUICService";
    private static final int DEFAULT_PORT = 443;
    private static final int DEFAULT_ALTERNATE_PORT = 443;
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private CronetEngine cronetEngine;
    private final Context context;
    private UrlRequest.Callback callback;

    public HQUICService(Context context) {
        this.context = context;
        init();
    }

    /**
     * Asynchronous initialization.
     */
    public void init() {
        HQUICManager.asyncInit(
                context,
                new HQUICManager.HQUICInitCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "HQUICManager asyncInit success");
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.w(TAG, "HQUICManager asyncInit fail");
                    }
                });
    }

    /**
     * Create a Cronet engine.
     *
     * @param url URL.
     * @return cronetEngine Cronet engine.
     */
    private CronetEngine createCronetEngine(String url) {
        if (cronetEngine != null) {
            return cronetEngine;
        }
        CronetEngine.Builder builder = new CronetEngine.Builder(context);
        builder.enableQuic(true);
        builder.addQuicHint(getHost(url), DEFAULT_PORT, DEFAULT_ALTERNATE_PORT);
        cronetEngine = builder.build();
        return cronetEngine;
    }

    /**
     * Construct a request
     *
     * @param url Request URL.
     * @param method method Method type.
     * @return UrlRequest urlrequest instance.
     */
    private UrlRequest buildRequest(String url, String method) {
        CronetEngine mCronetEngine = createCronetEngine(url);
        UrlRequest.Builder requestBuilder =
                mCronetEngine.newUrlRequestBuilder(url, callback, executor).setHttpMethod(method);
        return requestBuilder.build();
    }

    /**
     *  Send a request to the URL.
     *
     * @param url Request URL.
     * @param method Request method type.
     */
    public void sendRequest(String url, String method) {
        Log.i(TAG, "callURL: url is " + url + "and method is " + method);
        UrlRequest urlRequest = buildRequest(url, method);
        if (null != urlRequest) {
            urlRequest.start();
        }
    }

    /**
     * Parse the domain name to obtain the host name.
     *
     * @param url Request URL.
     * @return host Host name.
     */
    private String getHost(String url) {
        String host = null;
        try {
            java.net.URL url1 = new java.net.URL(url);
            host = url1.getHost();
        } catch (MalformedURLException e) {
            Log.e(TAG, "getHost: ", e);
        }
        return host;
    }

    public void setCallback(UrlRequest.Callback mCallback) {
        callback = mCallback;
    }
}
