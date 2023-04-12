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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.soundDetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.sounddect.MLSoundDetectConstants;
import com.huawei.hms.mlsdk.sounddect.MLSoundDetectListener;
import com.huawei.hms.mlsdk.sounddect.MLSoundDetector;

import java.text.DecimalFormat;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SoundDetectionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = SoundDetectionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private MLSoundDetector soundDetector;

    private static String[] voiceTypeArray;

    private Vector<String> vectorLogList;

    private boolean isRecording = false;

    private ElapsedTimeAndSetTimerWithTimerHandler elapsedTimeAndSetTimerWithTimerHandler;

    private static final int timerHandlerMessageCode = 1;

    private long elapsedRealtime;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    @Nullable
    @BindView(R.id.tv_record_time)
    TextView tvRecordTime;

    @Nullable
    @BindView(R.id.iv_record_sdtc_audio)
    ImageView ivRecordAudio;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private static final int PERMISSION_CODE_AUDIO_RECORD = 1;
    String[] permissionRequestAudioRecord = {Manifest.permission.RECORD_AUDIO};

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_sound_detection);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        voiceTypeArray = getResources().getStringArray(R.array.sdtc_voice_type_ordered_13);

        createSoundDetectorAndSetListenerCallback();

        createTimeHandlerForRecordTimer();

        createVectorListForDetectedVoiceTypes();

        setRecordButtonState();

    }


    public void createSoundDetectorAndSetListenerCallback() {
        soundDetector = MLSoundDetector.createSoundDetector();
        soundDetector.setSoundDetectListener(soundDetectorListener);
    }

    private void createTimeHandlerForRecordTimer() {
        elapsedTimeAndSetTimerWithTimerHandler = new ElapsedTimeAndSetTimerWithTimerHandler();
    }

    private void createVectorListForDetectedVoiceTypes() {
        vectorLogList = new Vector<>();
    }

    @OnClick({R.id.iv_record_sdtc_audio})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record_sdtc_audio:
                if (isRecording) {
                    stopRecording(getResources().getString(R.string.sdtc_stop) + "\nCancel Record");
                } else {
                    if (Utils.haveNetworkConnection(this)) {
                        ActivityCompat.requestPermissions(this, permissionRequestAudioRecord, PERMISSION_CODE_AUDIO_RECORD);
                    } else {
                        DialogUtils.showDialogNetworkWarning(
                                this,
                                "NEED NETWORK!",
                                "Would You Like To Go To Settings To Open Network?",
                                R.drawable.icon_settings,
                                "You can not Record Audio and can not RealTimeTranscription without Network!",
                                "YES GO", "CANCEL");
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_sdtc));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private final MLSoundDetectListener soundDetectorListener = new MLSoundDetectListener() {
        @Override
        public void onSoundSuccessResult(Bundle result) {

            int voiceType = result.getInt(MLSoundDetector.RESULTS_RECOGNIZED);

            Log.d(TAG, "soundDetectorListener : onSoundSuccessResult : voiceType : " + voiceType);
            // You can look at the link for sequential voiceType numbers. :
            // https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/mlsounddectconstants-0000001054132798-V5
            if (voiceType > 0 && voiceType < 13) {
                Log.d(TAG, "soundDetectorListener : onSoundSuccessResult : voiceType String : " + voiceTypeArray[voiceType]);
                vectorLogList.add(voiceTypeArray[voiceType]);
            }

            StringBuilder text = new StringBuilder();
            for (String log : vectorLogList) {
                text.append(log).append("\n");
            }
            if (vectorLogList.size() > 10) {
                vectorLogList.remove(0);
            }
            Log.d(TAG, "soundDetectorListener : onSoundSuccessResult : resultLogs : " + text);
            resultLogs.setText(text.toString());
        }

        @Override
        public void onSoundFailResult(int errorCode) {
            String errorDetail = getDetailErrorPrompt(errorCode);
            String errorMessage = "MLSoundDetectorListener onError : errorCode : " + errorCode + " - message : " + errorDetail;
            Log.e(TAG, errorMessage);
            resultLogs.setText(errorMessage);
            stopRecording(getResources().getString(R.string.sdtc_stop) + "\n" + errorMessage);
        }

        private String getDetailErrorPrompt(int errorCode) {
            switch (errorCode) {
                case MLSoundDetectConstants.SOUND_DETECT_ERROR_NO_MEM:
                    return "ERROR: 12201 Memory Error! Check the memory size of the device in the current running state.";
                case MLSoundDetectConstants.SOUND_DETECT_ERROR_FATAL_ERROR:
                    return "ERROR: 12202 Critical Error! You can submit a ticket online. Huawei technical support will handle it in time.";
                case MLSoundDetectConstants.SOUND_DETECT_ERROR_AUDIO:
                    return "ERROR: 12203 Microphone Error! Check whether the microphone is in use.";
                case MLSoundDetectConstants.SOUND_DETECT_ERROR_INTERNAL:
                    return "ERROR: 12298 Internal Error! You can submit a ticket online. Huawei technical support will handle it in time.";
                default:
                    return "";
            }
        }
    };


    private void startRecording() {
        boolean startSuccess = soundDetector.start(getApplicationContext());
        if (startSuccess) {
            showProgress();
            isRecording = true;
            setRecordButtonState();
            elapsedTimeAndSetTimerWithTimerHandler.sendMessage(Message.obtain(elapsedTimeAndSetTimerWithTimerHandler, timerHandlerMessageCode));
            Log.i(TAG, getResources().getString(R.string.sdtc_start));
            Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.sdtc_start));
        }
    }

    private void stopRecording(String msg) {
        hideProgress();
        soundDetector.stop();
        isRecording = false;
        elapsedRealtime = 0;
        setRecordButtonState();
        elapsedTimeAndSetTimerWithTimerHandler.removeCallbacksAndMessages(null);
        Log.i(TAG, msg);
        Utils.showToastMessage(getApplicationContext(), msg);
    }

    @SuppressLint("HandlerLeak")
    private class ElapsedTimeAndSetTimerWithTimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == timerHandlerMessageCode) {
                if (0 == elapsedRealtime) {
                    elapsedRealtime = SystemClock.elapsedRealtime();
                }
                int time = (int) ((SystemClock.elapsedRealtime() - elapsedRealtime) / 1000);
                String mm = new DecimalFormat("00").format(time / 60);
                String ss = new DecimalFormat("00").format(time % 60);

                tvRecordTime.setText(mm + ":" + ss);

                Message message = Message.obtain();
                message.what = timerHandlerMessageCode;
                sendMessageDelayed(message, 1000);
            }
        }
    }

    private void setRecordButtonState() {
        if (isRecording) {
            ivRecordAudio.setImageDrawable(getResources().getDrawable(R.drawable.icons_stop_record));
        } else {
            ivRecordAudio.setImageDrawable(getResources().getDrawable(R.drawable.icons_start_record));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERMISSION_CODE_AUDIO_RECORD) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted record audio permission -> startRecording");
                startRecording();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : record audio was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(
                        this,
                        "NEED RECORD AUDIO PERMISSIONS",
                        "Would You Like To Go To Settings To Open Network?",
                        R.drawable.icon_folder,
                        "You can not Record Audio and can not Detection Sounds and Voices without Record Audio Permission!",
                        "YES GO", "CANCEL");
            }
        }
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundDetector.destroy();
        unbinder.unbind();
        if (elapsedTimeAndSetTimerWithTimerHandler != null) {
            elapsedTimeAndSetTimerWithTimerHandler.removeCallbacksAndMessages(null);
        }
    }
}