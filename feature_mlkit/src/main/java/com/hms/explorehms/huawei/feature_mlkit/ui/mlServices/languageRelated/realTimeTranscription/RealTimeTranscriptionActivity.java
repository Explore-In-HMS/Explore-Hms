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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.realTimeTranscription;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.AudioFileTranscriptionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscription;
import com.huawei.hms.mlsdk.speechrtt.MLSpeechRealTimeTranscriptionConstants;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RealTimeTranscriptionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = AudioFileTranscriptionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private RealTimeTranscriptionManager realTimeTranscriptionManager;

    private boolean isSelected = false;

    private final StringBuilder stringBufferRecognizerResult = new StringBuilder();

    private static final String LANGUAGE_EN = MLSpeechRealTimeTranscriptionConstants.LAN_EN_US;
    private static final String LANGUAGE_FR = MLSpeechRealTimeTranscriptionConstants.LAN_FR_FR;
    private static final String LANGUAGE_ZH = MLSpeechRealTimeTranscriptionConstants.LAN_ZH_CN;

    private String mLanguage = LANGUAGE_EN;


    @Nullable
    @BindView(R.id.tv_rtt_output)
    TextView tvRttOutput;

    @Nullable
    @BindView(R.id.tv_rtt_record_result)
    TextView tvRttRecordResult;

    @Nullable
    @BindView(R.id.iv_recorder_rtt_audio_step)
    ImageView ivRecorderRttAudioStep;

    @Nullable
    @BindView(R.id.spinner_rtt_language)
    Spinner spinnerRttLanguage;

    @Nullable
    @BindView(R.id.iv_record_rtt_audio)
    ImageView ivBtnRecordRttAudio;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    private static final int PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD = 1;
    String[] permissionRequestStorageAndAudioRecord = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_transcription);

        Utils.setApiKeyForRemoteMLApplication(getApplicationContext());
        setupToolbar();

        unbinder = ButterKnife.bind(this);

        if (isSelected) {
            isSelected = false;
            ivBtnRecordRttAudio.setImageDrawable(getResources().getDrawable(R.drawable.icons_stop_record));
        } else {
            isSelected = true;
            ivBtnRecordRttAudio.setImageDrawable(getResources().getDrawable(R.drawable.icons_start_record));
        }

        spinnerRttLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) spinnerRttLanguage.getSelectedItem();
                if (str.equals("English")) {
                    mLanguage = LANGUAGE_EN;
                } else if (str.equals("French")) {
                    mLanguage = LANGUAGE_FR;
                } else if (str.equals("Chinese")) {
                    mLanguage = LANGUAGE_ZH;
                }

                Log.i(TAG, "spinnerRttLanguage Selected : " + mLanguage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be trigger when nothing is selected.
            }
        });


    }


    @OnClick({R.id.iv_record_rtt_audio})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record_rtt_audio:
                if (Utils.haveNetworkConnection(this)) {
                    ActivityCompat.requestPermissions(this, permissionRequestStorageAndAudioRecord, PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD);
                } else {
                    DialogUtils.showDialogPermissionWarning(this,
                            "NEED NETWORK!",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not Record Audio and can not RealTimeTranscription without Network!",
                            "YES GO", "CANCEL");
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_rtt));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopRealTimeTranscription();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage and record audio permission -> Start Intent setViewsStartSpeechListerForRealTimeTranscription");
                startSpeechListerForRealTimeTranscriptionAndSetViews();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : storage and record audio was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(
                        this,
                        "NEED RECORD AUDIO and STORAGE PERMISSIONS",
                        "Would You Like To Go To Settings To Open Network?",
                        R.drawable.icon_folder,
                        "You can not Record Audio and can not  RealTimeTranscription without Storage and Record Audio Permission!",
                        "YES GO", "CANCEL");
            }
        }
    }


    private void startSpeechListerForRealTimeTranscriptionAndSetViews() {
        if (isSelected) {
            showProgress();
            isSelected = false;
            ivBtnRecordRttAudio.setImageDrawable(getResources().getDrawable(R.drawable.icons_stop_record));
            if (realTimeTranscriptionManager == null) {
                Log.d(TAG, "starting  SpeechLister For RealTime Transcription");
                setSpeechListener();
                tvRttOutput.setText(getResources().getString(R.string.you_may_speak_your_speak_will_be_text));
                tvRttRecordResult.setText("");
                animationDrawableStartOrStop(true);
            } else if (!realTimeTranscriptionManager.isMlAsrLongRecognizerIsListening()) {
                realTimeTranscriptionManager.destroy();
                setSpeechListener();
                tvRttOutput.setText(getResources().getString(R.string.you_may_speak_your_speak_will_be_text));
                tvRttRecordResult.setText("");
            }
        } else {
            hideProgress();
            isSelected = true;
            ivBtnRecordRttAudio.setImageDrawable(getResources().getDrawable(R.drawable.icons_start_record));
            if (realTimeTranscriptionManager != null) {
                Log.d(TAG, "stopping  SpeechLister For RealTime Transcription");
                if (stringBufferRecognizerResult.length() > 0) {
                    stringBufferRecognizerResult.delete(0, stringBufferRecognizerResult.length() - 1);
                }
                tvRttOutput.setText(getResources().getString(R.string.speech_recognizer_turned_off));

                MLSpeechRealTimeTranscription.getInstance().destroy();
                realTimeTranscriptionManager = null;

                animationDrawableStartOrStop(false);
            }
        }
    }

    public void animationDrawableStartOrStop(boolean isStart) {
        if (isStart) {
            ivRecorderRttAudioStep.setImageResource(R.drawable.animlist_sound_record);
            AnimationDrawable animationDrawable = (AnimationDrawable) ivRecorderRttAudioStep.getDrawable();
            animationDrawable.start();
        } else {
            ivRecorderRttAudioStep.setImageResource(R.drawable.animlist_sound_record);
            AnimationDrawable animationDrawable = (AnimationDrawable) ivRecorderRttAudioStep.getDrawable();
            animationDrawable.stop();
        }
    }

    private void setSpeechListener() {
        realTimeTranscriptionManager = new RealTimeTranscriptionManager(mLanguage, new RealTimeTranscriptionManager.onResultsReady() {
            @Override
            public void onRecognizingResults(ArrayList<String> results, int status) {
                if (results != null && !results.isEmpty()) {
                    if (results.size() == 1) {
                        tvRttOutput.setText(results.get(0));
                        if (status == RealTimeTranscriptionManager.RESULT_FINAL) {
                            stringBufferRecognizerResult.append(results.get(0));
                            tvRttRecordResult.setText(stringBufferRecognizerResult.toString());
                            Log.d(TAG, "onRecognizingResults RESULT_FINAL : " + stringBufferRecognizerResult.toString());
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        if (results.size() > 5) {
                            results = (ArrayList<String>) results.subList(0, 5);
                        }
                        for (String result : results) {
                            sb.append(result).append("\n");
                        }
                        tvRttOutput.setText(sb.toString());
                        Log.d(TAG, "onRecognizingResults : " + sb.toString());


                        if (status == RealTimeTranscriptionManager.RESULT_FINAL) {
                            if (!TextUtils.isEmpty(stringBufferRecognizerResult.toString())) {
                                //stringBufferRecognizerResult.append(",");
                            }
                            stringBufferRecognizerResult.append(sb.toString());
                            tvRttRecordResult.setText(stringBufferRecognizerResult.toString());
                        }
                    }
                    hideProgress();
                }
            }

            @Override
            public void onError(int errorCode) {
                String errorDetail = getDetailErrorPrompt(errorCode);
                Log.i(TAG, "MLRealTimeTranscription Recognizing onError : errorCode : " + errorCode + " : " + errorDetail);
                tvRttRecordResult.setText(errorDetail);
                tvRttOutput.setText("");
                hideProgress();
            }
        });
    }


    private String getDetailErrorPrompt(int errorCode) {
        String errorText;
        switch (errorCode) {
            case MLSpeechRealTimeTranscriptionConstants.ERR_NO_NETWORK:
                errorText = "ERROR: 13202 : " + getResources().getString(R.string.err_no_network);
                break;
            case MLSpeechRealTimeTranscriptionConstants.ERR_SERVICE_UNAVAILABLE:
                errorText = "ERROR: 13203 : " + getResources().getString(R.string.err_service_unavailable);
                break;
            case MLSpeechRealTimeTranscriptionConstants.ERR_INVALIDE_TOKEN:
                errorText = "ERROR: 13219 : " + getResources().getString(R.string.err_invalidate_token);
                break;
            default:
                errorText = "ERROR: " + errorCode + " : Undefined Error.";
                break;
        }
        return errorText;
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        stopRealTimeTranscription();
    }

    public void stopRealTimeTranscription() {
        if (realTimeTranscriptionManager != null) {
            MLSpeechRealTimeTranscription.getInstance().destroy();
        }
    }

}