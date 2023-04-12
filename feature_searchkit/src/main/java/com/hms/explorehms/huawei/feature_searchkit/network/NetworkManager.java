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
package com.hms.explorehms.huawei.feature_searchkit.network;

import android.content.Context;
import android.util.Log;

import com.huawei.secure.android.common.ssl.SecureSSLSocketFactory;
import com.huawei.secure.android.common.ssl.SecureX509TrustManager;
import com.huawei.secure.android.common.ssl.hostname.StrictHostnameVerifier;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    private static final String TAG = "Network Manager";

    private static NetworkManager networkManager;

    /**
     * 获取NetworkManager的实例
     *
     * @return NetworkManager的实例
     */
    public static NetworkManager getInstance() {
        if (networkManager == null) {
            syncInit();
        }
        return networkManager;
    }

    /**
     * 单例
     */
    private static synchronized void syncInit() {
        if (networkManager == null) {
            networkManager = new NetworkManager();
        }
    }

    public QueryService createService(Context context, String baseUrl) {
        QueryService queryService = null;
        Retrofit retrofit = null;
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        try {
            SSLSocketFactory ssf = SecureSSLSocketFactory.getInstance(context);
            X509TrustManager xtm = new SecureX509TrustManager(context);
            clientBuilder.sslSocketFactory(ssf, xtm);
            clientBuilder.hostnameVerifier(new StrictHostnameVerifier());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        OkHttpClient client =
                clientBuilder
                        .retryOnConnectionFailure(true)
                        .readTimeout(5000, TimeUnit.MILLISECONDS)
                        .connectTimeout(5000, TimeUnit.MILLISECONDS)
                        .build();
        try {
            retrofit =
                    builder.client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
            queryService = retrofit.create(QueryService.class);
        } catch (Exception e) {
            Log.e(TAG, "createRestClient error: " + e.getMessage());
        }
        return queryService;
    }

}
