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

package com.genar.hmssandbox.huawei.feature_networkkit;

import android.content.Context;
import android.util.Log;

import com.huawei.hms.network.file.api.GlobalRequestConfig;
import com.huawei.hms.network.file.api.Progress;
import com.huawei.hms.network.file.api.Response;
import com.huawei.hms.network.file.api.Result;
import com.huawei.hms.network.file.api.exception.InterruptedException;
import com.huawei.hms.network.file.api.exception.NetworkException;
import com.huawei.hms.network.file.download.api.DownloadManager;
import com.huawei.hms.network.file.download.api.FileRequestCallback;
import com.huawei.hms.network.file.download.api.GetRequest;

import java.io.Closeable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class DownloadEngine extends AUpDownloadEngine {
    private static final String TAG = "DownloadEngine";
    DownloadManager downloadManager;
    GetRequest getRequest;

    FileRequestCallback callback;

    public DownloadEngine(Context context, EventListener listener) {
        super(context, listener);
    }

    @Override
    void initManager() {
        GlobalRequestConfig commonConfig = DownloadManager.newGlobalRequestConfigBuilder()
                .retryTimes(1)
                .build();

        downloadManager = new DownloadManager.Builder("downloadManager")
                .commonConfig(commonConfig)
                .build(context);

        callback = new FileRequestCallback() {
            @Override
            public GetRequest onStart(GetRequest request) {
                Log.i(TAG, "activity new onStart:" + request);
                listener.onEngineStart();
                startTime = System.currentTimeMillis();
                return request;
            }

            @Override
            public void onProgress(GetRequest request, Progress progress) {
                Log.i(TAG, "onProgress:" + progress);
                listener.onProgress(progress.getProgress());
            }

            @Override
            public void onSuccess(Response<GetRequest, File, Closeable> response) {
                String filePath = "";
                if (response.getContent() != null) {
                    filePath = response.getContent().getAbsolutePath();
                }
                long timeUsed = System.currentTimeMillis() - startTime;
                Log.i(TAG, "onSuccess timeUsed:" + timeUsed + " for " + filePath);
                listener.onSuccess("timeused:" + timeUsed);
            }

            @Override
            public void onException(GetRequest getRequest, NetworkException e, Response<GetRequest, File, Closeable> response) {
                if (e instanceof InterruptedException) {
                    String errorMsg = "download exception for paused or canceled";
                    Log.w(TAG, errorMsg);
                    listener.onException(errorMsg);
                } else {
                    String errorMsg = "download exception for request:" + getRequest.getId() +
                            "\n\ndetail : " + e.getMessage();
                    if (e.getCause() != null) {
                        errorMsg += " , cause : " +
                                e.getCause().getMessage();                    }
                    Log.e(TAG, errorMsg);
                    listener.onException(errorMsg);
                }
            }
        };
    }

    @Override
    void download() {
        testDownload(context);
    }

    @Override
    void pause() {
        if (downloadManager == null) {
            Log.e(TAG, "can not pause without download");
            return;
        }
        if (getRequest == null) {
            if (listener != null) {
                listener.onException("request is null!");
            }
            return;
        }
        Result result = downloadManager.pauseRequest(getRequest.getId());
        checkResult(result);
    }

    @Override
    void resume() {
        if (downloadManager == null) {
            Log.e(TAG, "can not resume without download");
            return;
        }
        Result result = downloadManager.resumeRequest(getRequest, callback);
        checkResult(result);
    }

    @Override
    void cancel() {
        if (downloadManager == null) {
            Log.e(TAG, "nothing to cancel");
            return;
        }
        if (getRequest == null) {
            if (listener != null) {
                listener.onException("request is null!");
            }
            return;
        }
        Result result = downloadManager.cancelRequest(getRequest.getId());
        checkResult(result);
    }

    @Override
    void uploadForPut() {
    }

    @Override
    void uploadForPost() {
    }

    private void testDownload(Context context) {
        if (downloadManager == null) {
            Log.e(TAG, "can not download without init");
            return;
        }

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put("header1", "value1");
        httpHeader.put("header2", "value2");

        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("param1", "value1");
        httpParams.put("param2", "value2");

        // replace the url
        String normalUrl = "https://gdown.baidu.com/data/wisegame/10a3a64384979a46/ee3710a3a64384979a46542316df73d4.apk";
        // replace the path to store the file
        String downloadFilePath = context.getExternalCacheDir().getPath() + File.separator + "test.apk";
        getRequest = DownloadManager.newGetRequestBuilder()
                .headers(httpHeader)
                .params(httpParams)
                .filePath(downloadFilePath)
                .url(normalUrl)
                .build();

        Result result = downloadManager.start(getRequest, callback);
        checkResult(result);
    }
}
