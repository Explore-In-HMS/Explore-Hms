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
import com.huawei.hms.network.file.upload.api.BodyRequest;
import com.huawei.hms.network.file.upload.api.FileEntity;
import com.huawei.hms.network.file.upload.api.FileUploadCallback;
import com.huawei.hms.network.file.upload.api.UploadManager;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadEngine extends AUpDownloadEngine {
    private static final String TAG = "UploadEngine";
    UploadManager upManager;
    BodyRequest request;
    FileUploadCallback callback;

    public UploadEngine(Context context, EventListener listener) {
        super(context, listener);
    }

    @Override
    void initManager() {
        GlobalRequestConfig commonConfig = UploadManager.newGlobalRequestConfigBuilder()
                .retryTimes(1)
                .build();
        upManager = (UploadManager) new UploadManager
                .Builder("upoloadManager")
                .commonConfig(commonConfig)
                .build(context);

        callback = new FileUploadCallback() {
            @Override
            public BodyRequest onStart(BodyRequest request) {
                Log.i(TAG, "onStart:" + request);
                listener.onEngineStart();
                startTime = System.currentTimeMillis();
                return request;
            }

            @Override
            public void onProgress(BodyRequest request, Progress progress) {
                Log.i(TAG, "onProgress:" + progress);
                listener.onProgress(progress.getProgress());
            }

            @Override
            public void onSuccess(Response<BodyRequest, String, Closeable> response) {
                Log.i(TAG, "onSuccess:" + response.getContent());
                listener.onSuccess("timeused:" + (System.currentTimeMillis() - startTime));
            }
             @Override
            public void onException(BodyRequest bodyRequest, NetworkException e, Response<BodyRequest, String, Closeable> response) {
                if (e instanceof InterruptedException) {
                    String errorMsg = "upload onException for canceled";
                    Log.w(TAG, errorMsg);
                    listener.onException(errorMsg);
                } else {
                    String errorMsg = "upload exception for request:" + bodyRequest.getId() +
                            "\n\ndetail : " + e.getMessage();
                    if (e.getCause() != null) {
                        errorMsg += " , cause : " +
                                e.getCause().getMessage();
                    }
                    Log.e(TAG, errorMsg);
                    listener.onException(errorMsg);
                }
            }
        };
    }

    @Override
    void download() {
    }

    @Override
    void pause() {
    }

    @Override
    void resume() {
    }

    @Override
    void cancel() {
        if (upManager == null) {
            Log.e(TAG, "nothing to cancel");
            return;
        }
        if (request == null) {
            if (listener != null) {
                listener.onException("request is null!");
            }
            return;
        }
        Result result = upManager.cancelRequest(request.getId());
        checkResult(result);
    }

    @Override
    void uploadForPut() {
        testUpload(true);
    }

    @Override
    void uploadForPost() {
        testUpload(false);
    }

    void testUpload(boolean usePut) {
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put("header1", "value1");
        httpHeader.put("header2", "value2");

        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("param1", "value1");
        httpParams.put("param2", "value2");

        // replace the url for upload
        final String normalUrl = "https://path/upload";
        if (usePut) {
            // upload file for http put
            List<FileEntity> fileList = new ArrayList<>();
            // replace the file path
            String filePath1 = context.getString(R.string.filepath1);
            fileList.add(new FileEntity(new File(filePath1)));

            request = UploadManager.newPutRequestBuilder()
                    .url(normalUrl)
                    .fileParams(fileList)
                    .params(httpParams)
                    .headers(httpHeader)
                    .build();
        } else {
            // upload file for http post
            // replace the file path
            String filePath1 = context.getString(R.string.filepath1);
            String filePath2 = context.getString(R.string.filepath2);

            request = UploadManager.newPostRequestBuilder()
                    .url(normalUrl)
                    .fileParams("file1", new FileEntity(new File(filePath1)))
                    .fileParams("file2", new FileEntity(new File(filePath2)))
                    .params(httpParams)
                    .headers(httpHeader)
                    .build();
        }

        if (upManager == null) {
            Log.e(TAG, "nothing to cancel");
            return;
        }

        Result result = upManager.start(request, callback);
        checkResult(result);
    }
}
