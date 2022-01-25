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
package com.genar.hmssandbox.huawei.feature_audiokit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AudioKillService extends Service {

    //Custom service to stop playback when the activity is destroyed.
    //This service will destroy itself along with the music in the background.

    private static final String TAG = "AudioKillService";

    public AudioKillService() {
        /*
               Constructor method.
        */
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand---startId: " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            Log.w(TAG, "run: kill my self.");
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (RuntimeException e) {
            Log.e(TAG, TAG, e);
        }
        super.onTaskRemoved(rootIntent);
    }
}