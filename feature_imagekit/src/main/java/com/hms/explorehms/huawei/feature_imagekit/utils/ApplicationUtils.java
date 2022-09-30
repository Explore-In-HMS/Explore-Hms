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

package com.hms.explorehms.huawei.feature_imagekit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.hms.explorehms.huawei.feature_imagekit.model.TokenResponseModel;
import com.hms.explorehms.huawei.feature_imagekit.ui.interfaces.APIServiceImageKit;
import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.agconnect.config.AGConnectServicesConfig;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApplicationUtils {

    private ApplicationUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String TAG = "IMAGEKIT";
    private static final String TOKEN_URL = "https://login.cloud.huawei.com/";

    public static String CLIENT_ID_TOKEN;
    public static String CLIENT_SECRET_TOKEN;
    public static String PROJECT_ID;
    public static String AUTH_API_KEY;
    public static String CLIENT_ID;


    public static JSONObject createAuthJson(Context context) {

        JSONObject authJson = new JSONObject();
        AGConnectOptions agConnectOptionsBuilder = new AGConnectOptionsBuilder().build(context);
        CLIENT_ID_TOKEN = agConnectOptionsBuilder.getString("client/app_id");
        Log.d("CLIENT ID TOKEN", CLIENT_ID_TOKEN);
        CLIENT_SECRET_TOKEN = agConnectOptionsBuilder.getString("client/client_secret");
        Log.d("CLIENT SECRET TOKEN", CLIENT_SECRET_TOKEN);

        PROJECT_ID = agConnectOptionsBuilder.getString("client/project_id");
        AUTH_API_KEY = agConnectOptionsBuilder.getString("client/api_key");
        CLIENT_ID = agConnectOptionsBuilder.getString("client/client_id");


        try {
            authJson.put("projectId", PROJECT_ID);
            authJson.put("appId", CLIENT_ID_TOKEN);
            authJson.put("authApiKey", AUTH_API_KEY);
            authJson.put("clientSecret", CLIENT_SECRET_TOKEN);
            authJson.put("clientId", CLIENT_ID);

        } catch (JSONException ex) {
            Log.e("EXPLOREHMS", ex.toString());
        }

        return authJson;
    }

    public static TokenResponseModel getToken() {

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(TOKEN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIServiceImageKit apiServiceImageKit = retrofit.create(APIServiceImageKit.class);


        Call<TokenResponseModel> call = apiServiceImageKit.getToken("client_credentials", CLIENT_ID_TOKEN, "8316388b7d593328b3003b44bbcf5c5247266f6e22920eef883c9810586c1e45");

        try {
            Response<TokenResponseModel> responseModel = call.execute();

            return responseModel.body();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    public static void openWebPage(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }
}
