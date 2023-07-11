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

package com.hms.explorehms.huawei.feature_audiokit;

import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;

public class AudioVolumeContentObserver extends ContentObserver {

    private final OnAudioVolumeChangedListener mListener;
    private final AudioManager mAudioManager;
    private final int mAudioStreamType;
    private int mLastVolume;

    public AudioVolumeContentObserver(
            @NonNull Handler handler,
            @NonNull AudioManager audioManager,
            int audioStreamType,
            @NonNull OnAudioVolumeChangedListener listener) {

        super(handler);
        mAudioManager = audioManager;
        mAudioStreamType = audioStreamType;
        mListener = listener;
        mLastVolume = audioManager.getStreamVolume(mAudioStreamType);
    }

    /**
     * Depending on the handler this method may be executed on the UI thread
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (mAudioManager != null && mListener != null) {
            int maxVolume = mAudioManager.getStreamMaxVolume(mAudioStreamType);
            int currentVolume = mAudioManager.getStreamVolume(mAudioStreamType);
            if (currentVolume != mLastVolume) {
                mLastVolume = currentVolume;
                mListener.onAudioVolumeChanged(currentVolume, maxVolume);
            }
        }
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }
}
