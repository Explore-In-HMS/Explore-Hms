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

package com.genar.hmssandbox.huawei.feature_imagekit.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.genar.hmssandbox.huawei.feature_imagekit.model.TokenResponseModel;
import com.genar.hmssandbox.huawei.feature_imagekit.ui.interfaces.APIServiceImageKit;

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

    private static final String TAG = "IMAGEAKIT";
    private static final String TOKEN_URL = "https://login.cloud.huawei.com/";

    private static final String CLIENT_ID_TOKEN = "102418005";
    private static final String CLIENT_SECRET_TOKEN = "1870f7224f66a6421ee91c199336325185241538b5e2d46d6d37c7aff6ae44f0";


    public static JSONObject createAuthJson() {

        JSONObject authJson = new JSONObject();

        try {
            authJson.put("projectId", "736430079244518230");
            authJson.put("appId", CLIENT_ID_TOKEN);
            authJson.put("authApiKey", "CV8RiFSCwQTFPxl1ET8PWacetyb/E3+HjejRkuQHJ/RSczHVZzPXC7pNRBPPpSoJvuigzxm5tRMzvee57oVD3djKVLNc");
            authJson.put("clientSecret", "5B36A8AC121ECC195E19025A48BDB3A499CF8D91D931F5CC444CA7C57C7A1758");
            authJson.put("clientId", "386886499523626112");

        } catch (JSONException ex) {
            Log.e("SandBox", ex.toString());
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


        Call<TokenResponseModel> call = apiServiceImageKit.getToken("client_credentials", CLIENT_ID_TOKEN, CLIENT_SECRET_TOKEN);

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
