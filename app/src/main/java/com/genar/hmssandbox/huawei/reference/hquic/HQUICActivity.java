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
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class HQUICActivity extends AppCompatActivity {
    private static final String TAG = "HQUICActivity";
    private static final String URL = "https://developer.huawei.com";
    private static final String METHOD = "GET";
    private static final int CAPACITY = 102400;
    private TextView callText;
    private String callStr;
    private HQUICService hquicService;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_q_u_i_c);
        setupToolbar();
        init();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_hquic);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_txt_hquic));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void init() {
        callText = findViewById(R.id.call_text);
        createHQUIC();

        MaterialButton button = findViewById(R.id.btn_hquick);
        button.setOnClickListener(v -> {
            hQUICTest();
        });
    }

    private void createHQUIC() {
        hquicService = new HQUICService(this);
        hquicService.setCallback(
                new UrlRequest.Callback() {
                    @Override
                    public void onRedirectReceived(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, String s) {
                        Log.i(TAG, "onRedirectReceived: method is called");
                        urlRequest.followRedirect();
                    }

                    @Override
                    public void onResponseStarted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {
                        Log.i(TAG, "onResponseStarted: method is called");
                        urlRequest.read(ByteBuffer.allocateDirect(CAPACITY));
                    }

                    @Override
                    public void onReadCompleted(
                            UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, ByteBuffer byteBuffer) {
                        Log.i(TAG, "onReadCompleted: method is called");
                        urlRequest.read(ByteBuffer.allocateDirect(CAPACITY));
                    }

                    @Override
                    public void onSucceeded(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {
                        Log.i(TAG, "onSucceeded: method is called");
                        Log.i(TAG, "onSucceeded: protocol is " + urlResponseInfo.getNegotiatedProtocol());
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        callStr += ("duration -> " + duration + "ms" + System.lineSeparator());
                        callStr += ("protocol -> " + urlResponseInfo.getNegotiatedProtocol() + System.lineSeparator());
                        List<Map.Entry<String, String>> list = urlResponseInfo.getAllHeadersAsList();
                        StringBuilder builder = new StringBuilder();
                        for (Map.Entry<String, String> stringStringEntry : list) {
                            builder
                                    .append(callStr)
                                    .append(stringStringEntry.getKey())
                                    .append(" -> ")
                                    .append(stringStringEntry.getValue())
                                    .append(System.lineSeparator());
                        }
                        callStr = builder.toString();
                        runOnUiThread(() -> callText.setText(callStr));
                    }

                    @Override
                    public void onFailed(
                            UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException error) {
                        Log.e(TAG, "onFailed: method is called ", error);
                        callStr += "onFailed: method is called " + error;
                        runOnUiThread(
                                () -> callText.setText(callStr));
                    }
                });
    }

    public void hQUICTest() {
        callStr = "";
        startTime = System.currentTimeMillis();
        hquicService.sendRequest(URL, METHOD);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}