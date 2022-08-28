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

import android.content.Context;

import com.huawei.hms.network.file.api.Result;

public abstract class AUpDownloadEngine {
    long startTime = 0;
    Context context;
    EventListener listener;

    /**
     * Event listener. Events of download or upload tasks are called to the activity for display on the UI.
     */
    interface EventListener {
        /**
         * Callback to be called when the task starts.
         */
        void onEngineStart();

        /**
         * Callback to be called during task execution.
         *
         * @param onProgress Task execution progress.
         */
        void onProgress(int onProgress);

        /**
         * Callback to be called if an error occurs during task execution.
         *
         * @param message Error message.
         */
        void onException(String message);

        /**
         * Callback to be called when the task is executed successfully.
         *
         * @param message Message indicating that the task is executed successfully.
         */
        void onSuccess(String message);
    }

    public AUpDownloadEngine(Context context, EventListener listener) {
        this.context = context;
        this.listener = listener;
        initManager();
    }

    /**
     * Check the result, and call the exception callback if an error occurs.
     *
     * @param result Result to be checked.
     */
    void checkResult(Result result) {
        if (result.getCode() != Result.SUCCESS) {
            listener.onException("action failed:" + result);
        }
    }

    /**
     * Initialize the task manager.
     */
    abstract void initManager();

    /**
     * Perform download.
     */
    abstract void download();

    /**
     * Pause download.
     */
    abstract void pause();

    /**
     * Resume download.
     */
    abstract void resume();

    /**
     * Cancel download.
     */
    abstract void cancel();

    /**
     * Perform put upload.
     */
    abstract void uploadForPut();

    /**
     * Perform post upload.
     */
    abstract void uploadForPost();

}
