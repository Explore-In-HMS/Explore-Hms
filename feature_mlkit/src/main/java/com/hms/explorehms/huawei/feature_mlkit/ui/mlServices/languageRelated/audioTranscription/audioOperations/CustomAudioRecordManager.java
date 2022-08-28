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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.audioOperations;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.speechRecognition.CustomAudioRecordDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomAudioRecordManager {

    private static final String TAG = CustomAudioRecordManager.class.getSimpleName();

    private MediaRecorder mRecorder;

    private final String mFileDir;

    private String mFilePath;

    private boolean mIsReady;

    private CustomAudioRecordDialog mRecordDialog;

    private boolean mIsRecording = false;

    private float mRecordingTime = 0F;

    // volume changed event when recording.
    private static final int MSG_VOLUME_CHANGED = 1001;

    private RecordingListener mListener;

    public interface RecordingListener {
        /**
         * callback for the ready event.
         */
        void recordingReady();

        /**
         * callback for the complete event
         *
         * @param durationTime duration time
         * @param filePath     file path of the recording file
         */
        void onComplete(float durationTime, String filePath);
    }

    // check it out and change it
    private final Handler mHandler = new Handler(message -> {
        if (message.what == MSG_VOLUME_CHANGED) {
            mRecordDialog.updateVolumeLevel(getVolumeLevel(12));
        }
        return true;
    });

    public CustomAudioRecordManager(String dir, CustomAudioRecordDialog recordDialog) {
        mRecordDialog = recordDialog;
        mFileDir = dir;
    }

    public void setOnRecordingStateListener(RecordingListener listener) {
        mListener = listener;
    }

    /**
     * prepare to record the audio
     */
    public void prepareAudio() {
        Log.i(TAG, "prepareAudio");
        try {
            mIsReady = false;
            File dir = new File(mFileDir);
            if (!dir.exists()) {
                try {
                    boolean isDirCreated = dir.createNewFile();
                    Log.i(TAG, String.valueOf(isDirCreated));
                } catch (IOException e) {
                    Log.e(TAG, "prepareAudio createNewFile : " + e.getMessage(), e);
                }
            }
            String fileName = generateFileName(".amr");

            File file = new File(dir, fileName);
            Log.i(TAG, "prepareAudio file : " + file.getPath());
            try {
                if (!file.exists()) {
                    Log.e(TAG, "prepareAudio file will be create");
                    boolean isFileCreated = file.createNewFile();
                    Log.i(TAG, String.valueOf(isFileCreated));
                }
            } catch (IOException e) {
                Log.e(TAG, "prepareAudio create new audio file exception : " + e.getMessage(), e);
            }

            mFilePath = file.getCanonicalPath();

            Log.i(TAG, "prepareAudio : mFilePath : " + mFilePath);

            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(file.getCanonicalPath());
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            mIsReady = true;
            if (mListener != null) {
                mListener.recordingReady();
            }
        } catch (IOException e) {
            Log.e(TAG, "prepareAudio Exception : " + e.getMessage(), e);
        }
    }

    private String generateFileName(String fileExtension) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        String fileName = formatter.format(curDate);
        fileName = fileName + fileExtension;
        Log.i(TAG, "generateFileName : " + fileName);
        return fileName;
    }

    public int getVolumeLevel(int maxLevel) {
        if (mIsReady) {
            try {
                float curLevel = (float) maxLevel * mRecorder.getMaxAmplitude() / 32768f;
                if (curLevel <= 0.2f) {
                    return 1;
                } else if (curLevel >= 0.2f && curLevel <= 0.4f) {
                    return 2;
                } else if (curLevel > 0.4f && curLevel <= 0.6f) {
                    return 3;
                } else if (curLevel > 0.6f && curLevel <= 0.8f) {
                    return 4;
                } else if (curLevel > 0.8f && curLevel <= 1.0f) {
                    return 5;
                } else if (curLevel > 1.0f && curLevel <= 1.2f) {
                    return 6;
                } else if (curLevel > 1.2 && curLevel <= 1.4f) {
                    return 7;
                } else if (curLevel > 1.4f && curLevel <= 1.6f) {
                    return 8;
                } else if (curLevel > 1.6f && curLevel <= 1.8f) {
                    return 9;
                } else if (curLevel > 1.8f && curLevel <= 2.0f) {
                    return 10;
                } else if (curLevel > 2.0f && curLevel <= 2.2f) {
                    return 11;
                } else if (curLevel > 2.2f) {
                    return 12;
                }
            } catch (Exception e) {
                Log.e(TAG, "getVolumeLevel Exception : " + e.getMessage(), e);
            }
        }
        return 1;
    }

    public void release() {
        Log.i(TAG, "release");
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public String getFilePath() {
        Log.i(TAG, "getFilePath : " + mFilePath);
        return mFilePath;
    }

    public void startRecording() {
        Log.i(TAG, "startRecording");
        mIsRecording = true;
        new Thread(mGetVolumeLevelRunnable).start();
    }

    private final Runnable mGetVolumeLevelRunnable = () -> {
        while (mIsRecording) {
            try {
                Thread.sleep(100);
                mRecordingTime += 0.1; // interval time 0.1s
                Log.i(TAG, "mGetVolumeLevelRunnable mRecordingTime : " + mRecordingTime);
                mHandler.sendEmptyMessage(MSG_VOLUME_CHANGED);
            } catch (InterruptedException e) {
                Log.e(TAG, "Get volume level failed: " + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    };

    private void initData() {
        mIsRecording = false;
        mRecordingTime = 0;
        mIsReady = false;
    }

    public void recordingComplete() {
        Log.i(TAG, "recordingComplete");
        release();
        if (mListener != null) {
            mListener.onComplete(mRecordingTime, getFilePath());
        }
        initData();
    }

}
