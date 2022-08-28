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

package com.hms.explorehms.huawei.feature_healthkit.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_healthkit.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Huawei ID signing In and authorization By restful API
 *
 * @since 2020-09-23
 */
public class HealthKitCloudLogin extends AppCompatActivity {
    private static final String TAG = "HealthKitCloudLogin";

    // the app ID information generated by the Developer Alliance when creating the Server application
    private static final String CLIENT_ID = "{appid}";

    // the secret information generated by the Developer Alliance when creating the Server application
    private static final String CLIENT_SECRET = "{secret}";

    // The value of redirect_uri must be the same as the Redirect_URI registered by developer, this only for example.
    private static final String REDIRECT_URI = "http://www.example.com";

    // URL to query access token
    private static final String CLOUD_TOKEN = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

    // okhttp3 OkHttpClient to send request
    private final OkHttpClient mClient = new OkHttpClient();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_cloud_login);
        setupToolbar();
        // init WebView config
        initWebView();
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_health_kit_loginn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void initWebView() {
        // webview to html
        WebView mWebView = this.findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        // Customize the WebViewClient and Override the shouldOverrideUrlLoading method to query AccessToken after
        // Huawei ID sign In success. It is used only in the sample code, in the actual scenario, this query can be
        // processed on the callback page.
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG, request.toString());
                String url = request.getUrl().toString();
                // Intercept redirect_uri to query access token.
                if (url.startsWith(REDIRECT_URI)) {
                    String code = request.getUrl().getQueryParameter("code");
                    queryAccessToken(code);
                    return true;
                }

                // others only call super method
                return super.shouldOverrideUrlLoading(view, request);
            }

        });
        /**
         * URL of Huawei ID signing In and authorization Health Kit scopes
         * The value of redirect_uri must be the same as the Redirect_URI registered by developer, this only for example.
         */
        String cloudAuth = "https://oauth-login.cloud.huawei.com/oauth2/v3/authorize?response_type=code&client_id=" + CLIENT_ID
                + "&redirect_uri=http%3a%2f%2fwww.example.com&scope=https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fheightweight.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fgoals.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Findex.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fstep.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fdistance.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fspeed.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fcalories.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fpulmonary.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fstrength.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Factivity.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Flocation.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fbodyfat.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fsleep.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fheartrate.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fstress.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Frelaxtraining.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fnutrition.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fhearthealth.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fbloodglucose.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fbloodpressure.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Foxygensaturation.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fbodytemperature.both+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Freproductive.both&access_type=offline&display=touch";
        mWebView.loadUrl(cloudAuth);
    }

    /**
     * Use the authorization code to generate the access token.
     *
     * @param code Authorization Code
     */
    private void queryAccessToken(String code) {
        // 1. Build access token request
        Request request = buildAtRequest(code);

        // 2. Sending an asynchronous HTTP Request to generate the access token, and build user-defined Callback for
        // response. This Callback init with an anonymous Consumer to get access token and start
        // HealthKitAuthCloudActivity for check HUAWEI Health authorization result.
        mClient.newCall(request).enqueue(new com.hms.explorehms.huawei.feature_healthkit.OkHttpUtilCallback(response -> {
            // Parse response to get access token
            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
            String accessToken = jsonObject.get("access_token").getAsString();

            // Set accessToken result to HealthKitAuthCloudActivity
            Intent intent = new Intent();
            intent.putExtra("accessToken", accessToken);
            setResult(RESULT_OK, intent);
            finish();
        }));
    }

    /**
     * Build restful request to generate the access token
     *
     * @param code Authorization Code from sign in HUAWEI ID
     * @return Request to generate the access token
     */
    private Request buildAtRequest(String code) {
        RequestBody requestBody = new FormBody.Builder().add("code", code)
            .add("grant_type", "authorization_code")
            .add("client_id", CLIENT_ID)
            .add("client_secret", CLIENT_SECRET)
            .add("redirect_uri", REDIRECT_URI)
            .build();

        return new Request.Builder().url(CLOUD_TOKEN)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build();
    }
}
