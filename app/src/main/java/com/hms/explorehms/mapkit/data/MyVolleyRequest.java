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

package com.hms.explorehms.mapkit.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hms.explorehms.mapkit.model.response.DirectionResponse;
import com.google.gson.Gson;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.maps.HuaweiMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * It handles Volley request
 */
public final class MyVolleyRequest {
    private RequestQueue mRequestQueue;
    private Context context;
    private IVolley iVolley;
    private static MyVolleyRequest mInstance;

    private final RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
            if (mRequestQueue == null) {
                this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
            }


        }

        RequestQueue mRequestQueue = this.mRequestQueue;
        if (mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return mRequestQueue;
    }

    private final void addToRequestQueue(Request req) {
        this.getRequestQueue().add(req);
    }

    public final void postRequest(@NotNull final String dirReq, @NotNull final String directionType, @NotNull HuaweiMap hMap, @NotNull final IVolley callBack) throws UnsupportedEncodingException, JSONException {
        final Gson gson = new Gson();
        Response.ErrorListener err;
        err = error -> Log.d("ERROR", "onErrorResponse: " + error.getMessage());
        final Listener res = response -> {
            JSONObject myResponse = (JSONObject) response;
            DirectionResponse mdirectionResponse = gson.fromJson(myResponse.toString(), DirectionResponse.class);
            Log.d("mapres", "onResponse: " + response);
            callBack.onSuccess(mdirectionResponse);
        };
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, this.getUrl(directionType), new JSONObject(dirReq), res, err) {
        };
        this.addToRequestQueue(jsonObjectRequest);
    }

    @Nullable
    public final String getUrl(@NotNull String directionType) throws UnsupportedEncodingException {
        String API_KEY = AGConnectServicesConfig.fromContext(context).getString("client/api_key");
        return "https://mapapi.cloud.huawei.com/mapApi/v1/routeService/" + directionType + "?key=" + URLEncoder.encode((API_KEY), "utf-8");
    }

    public MyVolleyRequest(Context context, IVolley iVolley) {
        this.context = context;
        this.iVolley = iVolley;
        this.mRequestQueue = this.getRequestQueue();
    }

    public MyVolleyRequest(@NotNull Context context) {
        this.context = context;
        this.mRequestQueue = this.getRequestQueue();
    }

    public static final class Companion {
        @NotNull
        public final synchronized MyVolleyRequest getInstance(@NotNull Context context, @NotNull IVolley iVolley) {
            if (MyVolleyRequest.mInstance == null) {
                MyVolleyRequest.mInstance = new MyVolleyRequest(context, iVolley);
            }

            MyVolleyRequest mInstance = MyVolleyRequest.mInstance;
            if (mInstance == null) {
                mInstance =
                        new MyVolleyRequest(
                                context,
                                iVolley
                        );
            }

            return mInstance;
        }

        private Companion() {
        }

    }


}
