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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.speechRecognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AutomaticSpeechRecognitionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = AutomaticSpeechRecognitionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int ML_ASR_CAPTURE_CODE = 102;
    private static final int MSG_VOLUME_CHANGED = 103;

    private SpeechRecognizerManager speechRecognizerManager;

    CustomAudioRecordDialog customAudioRecordDialog;

    private boolean isAudioRecording = false;

    private final OnResultListener customRecordResultListener = new OnResultListener();

    private static final int PERMISSION_CODE_ASR_AUDIO = 1;

    String[] permissionRequestAsrAudio = {Manifest.permission.RECORD_AUDIO};

    @Nullable
    @BindView(R.id.spAsrLanguage)
    Spinner spAsrLanguage;

    @Nullable
    @BindView(R.id.spAsrSoundPickupType)
    Spinner spAsrSoundPickupType;

    private static final List<String> LANG_LIST = new ArrayList<>(Arrays.asList(
            "English", "French", "German", "Spanish", "Italian", "Chinese","Arabic"));

    private static final String LANGUAGE_EN = "en-US";
    private String languageCode = LANGUAGE_EN;

    private static final String TYPE_ASR = "ASR Plugin";
    private static final String TYPE_CUSTOM = "Custom";
    private String soundPickUpType = TYPE_ASR;

    private static final List<String> LANG_CODE = new ArrayList<>(Arrays.asList(
            LANGUAGE_EN, "fr-FR", "de-DE", "es-ES", "it-IT", "zh-CN","ar"));

    private static final List<String> SOUND_PICKUP_TYPE_LIST = new ArrayList<>(Arrays.asList(
            TYPE_ASR, TYPE_CUSTOM));

    @Nullable
    @BindView(R.id.ll_example)
    LinearLayout layoutSampleSentence;

    @Nullable
    @BindView(R.id.rl_result_record)
    ConstraintLayout layoutResultRecord;

    @Nullable
    @BindView(R.id.iv_close_record_result)
    ImageView btnCloseRecordResult;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.tv_record_result)
    TextView recordResults;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_automatic_speech_recognition);

        Utils.setApiKeyForRemoteMLApplication(getApplicationContext());
        setupToolbar();

        unbinder = ButterKnife.bind(this);

        createAndSetSpinners();

        customAudioRecordDialog = new CustomAudioRecordDialog(this, CustomAudioRecordDialog.TYPE_WITHOUT_COMPLETE_BUTTON);
        customAudioRecordDialog.setOnBackPressedListener(() -> {
            if (speechRecognizerManager != null) {
                speechRecognizerManager.destroy();
            }
        });
    }

    @OnClick({R.id.rl_btn_record, R.id.iv_close_record_result,})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.rl_btn_record:
                Log.i(TAG, "onclick for rl_btn_record : " + PERMISSION_CODE_ASR_AUDIO);
                if (Utils.haveNetworkConnection(this)) {
                    ActivityCompat.requestPermissions(this, permissionRequestAsrAudio, PERMISSION_CODE_ASR_AUDIO);
                } else {
                    DialogUtils.showDialogNetworkWarning(
                            this,
                            "NEED NETWORK PERMISSION",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not Record Audio and can not use Speech Recognition without Internet Connection!",
                            "YES GO", "CANCEL");
                }
                break;
            case R.id.iv_close_record_result:
                recordResults.setText(getString(R.string.txt_for_record_result));
                resultLogs.setText(getString(R.string.txt_for_record_result_log));
                setVisibleRecordResultLayout(false);
                break;
            default:
                Log.d(TAG, "Default case");
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_asr));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createAndSetSpinners() {

        ArrayAdapter<String> spLanguageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LANG_LIST);
        spLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAsrLanguage.setAdapter(spLanguageAdapter);

        spAsrLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageCode = LANG_CODE.get(position);
                Log.i(TAG, "createAndSetSpinner languageCode : " + languageCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing onNothingSelected
            }
        });

        ArrayAdapter<String> spSoundPickupTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SOUND_PICKUP_TYPE_LIST);
        spSoundPickupTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAsrSoundPickupType.setAdapter(spSoundPickupTypeAdapter);

        spAsrSoundPickupType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soundPickUpType = SOUND_PICKUP_TYPE_LIST.get(position);
                Log.i(TAG, "createAndSetSpinner soundPickUpType : " + soundPickUpType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing onNothingSelected
            }
        });
    }

    private String getDetailErrorPrompt(int errorCode) {
        switch (errorCode) {
            case MLAsrConstants.ERR_NO_NETWORK:
                return "ERROR: 11202 Network unavailable, please try again…";
            case MLAsrConstants.ERR_NO_UNDERSTAND:
                return "ERROR: 11204  The server could not parse your voice";
            case MLAsrConstants.ERR_SERVICE_UNAVAILABLE:
                return "ERROR: 11203 Service Unavailable";
            case MLAsrConstants.ERR_INVALIDATE_TOKEN:
                return "ERROR: 11219 Invalid Token";
            default:
                return "unDefined Error Code  ";
        }
    }

    /* ------------------------------------------------------------------------------------------ */
    //region Display ASR Plugin Audio Record Dialog Operations

    private void startRecodingOnAsrPlugin() {
        Log.i(TAG, "startRecodingOnAsrPlugin languageCode : " + languageCode);
        Intent intent = new Intent(this, MLAsrCaptureActivity.class)
                // Set the language that can be recognized to English. If this parameter is not set,
                // English is recognized by default. Example: "zh": Chinese; "en-US": English; "fr": French
                .putExtra(MLAsrCaptureConstants.LANGUAGE, languageCode)
                // Set whether to display text on the speech pickup UI.
                // MLAsrCaptureConstants.FEATURE_WORDFLUX: yes.
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX);
        this.startActivityForResult(intent, ML_ASR_CAPTURE_CODE);
        overridePendingTransition(R.anim.anim_asr_pop_up_slide_show, 0);
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    /* ------------------------------------------------------------------------------------------ */
    //region Display Custom Audio Record Dialog Operations

    private void startRecodingOnCustom() {
        Log.i(TAG, "startRecodingOnCustom languageCode : " + languageCode);
        if (speechRecognizerManager == null) {
            startListening();
        } else {
            speechRecognizerManager.destroy();
            startListening();
        }

        if (customAudioRecordDialog != null) {
            customAudioRecordDialog.show();
        }
        updateVolume();
    }

    private void startListening() {
        speechRecognizerManager = new SpeechRecognizerManager(this, languageCode, customRecordResultListener);
        speechRecognizerManager.startListening();
    }


    private class OnResultListener implements SpeechRecognizerManager.AudioRecordResultOnReady {
        @Override
        public void onResults(ArrayList<String> results) {
            if (results != null && !results.isEmpty()) {
                if (results.size() == 1) {
                    displaySuccessAnalyseResults(results.get(0));
                } else {
                    StringBuilder sb = new StringBuilder();
                    if (results.size() > 5) {
                        results = (ArrayList<String>) results.subList(0, 5);
                    }
                    for (String result : results) {
                        sb.append(result).append("\n");
                    }
                    Log.i(TAG, "OnResultListener onResults : results : " + sb.toString());
                    displaySuccessAnalyseResults(sb.toString());
                }
            } else {
                Log.i(TAG, "OnResultListener onResults : results is NULL or Empty!");
                setVisibleRecordResultLayout(false);
            }
        }

        @Override
        public void onError(int error) {
            Log.i(TAG, "OnResultListener onError : code : " + error);
            dismissCustomDialog();
            displayFailureAnalyseResults("AudioRecordResult onError : " + getDetailErrorPrompt(error));
        }

        @Override
        public void onFinish() {
            dismissCustomDialog();
        }
    }

    /* ------------------------------------------------------------------------------------------ */
    //region Display Custom Audio Record Dialog Volume Update Animations

    private void updateVolume() {
        isAudioRecording = true;
        new Thread(getVolumeLevelWithRunnable).start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_VOLUME_CHANGED) {
                customAudioRecordDialog.updateVolumeLevel(getRandomVolume());
            }
        }

        private int volume = 0;

        private int getRandomVolume() {
            if (volume < 12) {
                volume += 1;
            } else {
                volume = 1;
            }
            return volume;
        }

    };
    private final Runnable getVolumeLevelWithRunnable = () -> {
        while (isAudioRecording) {
            try {
                Thread.sleep(200);
                mHandler.sendEmptyMessage(MSG_VOLUME_CHANGED);
            } catch (InterruptedException e) {
                Log.e(TAG, "getVolumeLevelWithRunnable : " + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    };


    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    private void dismissCustomDialog() {
        runOnUiThread(() -> {
            isAudioRecording = false;
            if (customAudioRecordDialog != null) {
                customAudioRecordDialog.dismiss();
            }
        });
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations

    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Success Speech Recognition Results : " + text);
        setVisibleRecordResultLayout(true);
        recordResults.setText(text);

        String txtResult = "Success Speech Recognition Results : with " + text.length() + " characters :\n" + text;

        resultLogs.setText(txtResult);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Failure Speech Recognition Results : " + msg);
        setVisibleRecordResultLayout(false);
        recordResults.setText("");

        String txtResult = getString(R.string.txt_for_failure_speech_recognition) + "\n" + msg;
        resultLogs.setText(txtResult);

        Utils.showToastMessage(getApplicationContext(), "Speech Recognition Process was Failed : " + msg);
    }

    public void setVisibleRecordResultLayout(boolean visible) {
        if (visible) {
            layoutResultRecord.setVisibility(View.VISIBLE);
            layoutSampleSentence.setVisibility(View.GONE);
        } else {
            layoutResultRecord.setVisibility(View.GONE);
            layoutSampleSentence.setVisibility(View.VISIBLE);
        }
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_ASR_AUDIO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera permission -> Start Intent localAnalyzerWithCamera");
                if (soundPickUpType.equals(TYPE_ASR)) {
                    startRecodingOnAsrPlugin();
                } else {
                    startRecodingOnCustom();
                }
            } else {
                Log.w(TAG, "onRequestPermissionsResult : RecordAudio was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED RECORD AUDIO PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_sound_detection2,
                        "You can not Record Audio and can not use Speech Recognition without RecordAudio Permission!",
                        "YES GO", "CANCEL");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ML_ASR_CAPTURE_CODE) {
            switch (resultCode) {
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        String dataContent = "";
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            dataContent = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                        }
                        if (dataContent != null && !"".equals(dataContent)) {
                            displaySuccessAnalyseResults(dataContent);
                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    // Processing logic for recognition failure.
                    if (data != null) {
                        int errorCode = 0;
                        int subErrorCode = 0;
                        String msg = "";
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                                errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                            }
                            if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                                msg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                            }
                            if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                                subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                            }
                        }

                        String errorMsg = getDetailErrorPrompt(errorCode);
                        if (errorMsg.contains("unDefined")) {
                            errorMsg = msg;
                        }
                        if (subErrorCode != 0) {
                            errorMsg = errorMsg + " - subErrorCode : " + subErrorCode;
                        }
                        Log.i(TAG, "onActivityResult failed data error : " + errorMsg);
                        dismissCustomDialog();
                        displayFailureAnalyseResults("MLAsrCapture ActivityResult failed data error : " + errorMsg);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        isAudioRecording = false;
        if (speechRecognizerManager != null) {
            speechRecognizerManager.destroy();
        }
    }


}