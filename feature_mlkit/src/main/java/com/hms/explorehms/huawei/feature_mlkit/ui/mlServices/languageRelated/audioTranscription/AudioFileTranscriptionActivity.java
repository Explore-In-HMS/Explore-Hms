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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.audioOperations.CustomAudioRecordManager;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.audioOperations.FileUtils;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.speechRecognition.CustomAudioRecordDialog;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.aft.MLAftErrors;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftEngine;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftListener;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftResult;
import com.huawei.hms.mlsdk.aft.cloud.MLRemoteAftSetting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AudioFileTranscriptionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = AudioFileTranscriptionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final String LANGUAGE_EN = "en-US";
    private static String languageCode = LANGUAGE_EN;

    private static final int PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD = 1;
    String[] permissionRequestStorageAndAudioRecord = {Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private static final List<String> LANG_LIST = new ArrayList<>(Arrays.asList(
            "English", "Chinese"));

    private static final List<String> LANG_CODE = new ArrayList<>(Arrays.asList(
            LANGUAGE_EN, "zh"));

    private MLRemoteAftEngine aftEngineAnalyzer;

    CustomAudioRecordManager audioRecordManager;
    CustomAudioRecordDialog customAudioRecordDialog;


    private AlertDialog dialogAudioRecordConvertingProcess;

    String aftAnalyzerTaskId;

    //region views

    @Nullable
    @BindView(R.id.spAftLanguage)
    Spinner spAftLanguage;

    @Nullable
    @BindView(R.id.ll_example)
    LinearLayout layoutSampleSentence;

    @Nullable
    @BindView(R.id.rl_result_record)
    ConstraintLayout layoutResultRecord;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.tv_record_result)
    TextView recordResults;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    //endregion views

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_audio_file_transcription);

        Utils.setApiKeyForRemoteMLApplication(getApplicationContext());

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createAndSetSpinner();

        createMLRemoteAftEngineAndSetListenerCallback();

        createRecordDialogAndRegisterRecordingListener();

    }


    @OnClick({R.id.rl_btn_record, R.id.iv_close_record_result})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.rl_btn_record:
                Log.i(TAG, "onclick for rl_btn_record : " + PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD);
                if (Utils.haveNetworkConnection(this)) {
                    ActivityCompat.requestPermissions(this, permissionRequestStorageAndAudioRecord, PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD);
                } else {
                    DialogUtils.showDialogNetworkWarning(
                            this,
                            "NEED NETWORK!",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not Record Audio and can not transcript this without Network!",
                            "YES GO", "CANCEL");
                }
                break;
            case R.id.iv_close_record_result:
                recordResults.setText("Record Transcription Result Will Be Here.");
                resultLogs.setText("Transcription Result Descriptions Will Be Here!");
                setVisibleRecordResultLayout(false);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_aft));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_STORAGE_AND_AUDIO_RECORD) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage and record audio permission -> Start Intent showAudioRecordDialogAndPrepareAudio");
                showAudioRecordDialogAndPrepareAudio();
            } else {
                Log.w(TAG, "onRequestPermissionsResult : storage and record audio was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(
                        this,
                        "NEED STORAGE and RECORD AUDIO PERMISSION",
                        "Would You Like To Go To Settings To Open Network?",
                        R.drawable.icon_folder,
                        "You can not Record Audio and can not transcript this without Storage and Record Audio Permission!",
                        "YES GO", "CANCEL");
            }
        }

    }


    /* ------------------------------------------------------------------------------------------ */
    //region MLRemoteAftEngine And MLRemoteAftListener Callback

    public void createMLRemoteAftEngineAndSetListenerCallback() {
        aftEngineAnalyzer = MLRemoteAftEngine.getInstance();
        aftEngineAnalyzer.init(getApplicationContext()); // maybe need this
        aftEngineAnalyzer.setAftListener(mlRemoteAftListener);
    }

    private final MLRemoteAftListener mlRemoteAftListener = new MLRemoteAftListener() {

        @Override
        public void onInitComplete(String taskId, Object object) {
            Log.i(TAG, "MLRemoteAftListener onInitComplete : taskId " + taskId);
            aftEngineAnalyzer.startTask(taskId);
        }

        @Override
        public void onUploadProgress(String taskId, double progress, Object object) {
            Log.i(TAG, "MLRemoteAftListener onUploadProgress : taskId " + taskId);

        }

        @Override
        public void onEvent(String taskId, int eventId, Object object) {
            Log.i(TAG, getString(R.string.ml_remote_aft_listener_on_event) + taskId + " - eventId : " + eventId);

        }

        @Override
        public void onResult(String taskId, MLRemoteAftResult mlRemoteAftResult, Object object) {
            Log.i(TAG, getString(R.string.ml_remote_aft_listener_on_event) + taskId);

            if (mlRemoteAftResult != null) {
                Log.i(TAG, "MLRemoteAftListener onEvent : mlRemoteAftResult.isComplete " + mlRemoteAftResult.isComplete());
                if (!mlRemoteAftResult.isComplete()) {
                    return;
                }
                if (mlRemoteAftResult.getText() != null) {
                    dismissAudioRecordConvertingProcessDialog();
                    Log.i(TAG, getString(R.string.ml_remote_aft_listener_on_event) + taskId + " - mlRemoteAftResult : " + mlRemoteAftResult.getText());
                    displaySuccessAnalyseResults(mlRemoteAftResult.getText());
                }

                List<MLRemoteAftResult.Segment> segmentList = mlRemoteAftResult.getSegments();
                if (segmentList != null && segmentList.size() != 0) {
                    for (MLRemoteAftResult.Segment segment : segmentList) {
                        Log.i(TAG, "MLRemoteAftListener onEvent : mlRemoteAftResult Segment text is : " + segment.getText() + getString(R.string.start_time_is) + segment.getStartTime() + getString(R.string.end_time_is) + segment.getEndTime());
                    }
                }

                List<MLRemoteAftResult.Segment> words = mlRemoteAftResult.getWords();
                if (words != null && words.size() != 0) {
                    for (MLRemoteAftResult.Segment word : words) {
                        Log.i(TAG, "MLRemoteAftListener onEvent : mlRemoteAftResult Word  text is : " + word.getText() + getString(R.string.start_time_is) + word.getStartTime() + getString(R.string.end_time_is) + word.getEndTime());
                    }
                }

                List<MLRemoteAftResult.Segment> sentences = mlRemoteAftResult.getSentences();
                if (sentences != null && sentences.size() != 0) {
                    for (MLRemoteAftResult.Segment sentence : sentences) {
                        Log.i(TAG, "MLRemoteAftListener onEvent : mlRemoteAftResult Sentence  text is : " + sentence.getText() + getString(R.string.start_time_is) + sentence.getStartTime() + getString(R.string.end_time_is) + sentence.getEndTime());
                    }
                }
            }


        }

        @Override
        public void onError(String taskId, int errorCode, String message) {
            // Transcription error callback function.
            String errorDetail = getDetailErrorPrompt(errorCode);
            Log.i(TAG, "MLRemoteAftListener onError : taskId " + taskId + " - errorCode : " + errorCode + " - message : " + message + " : " + errorDetail);
            dismissAudioRecordConvertingProcessDialog();
            displayFailureAnalyseResults(errorDetail + " : message : " + message);

        }

        private String getDetailErrorPrompt(int errorCode) {
            switch (errorCode) {
                case MLAftErrors.ERR_NETCONNECT_FAILED:
                    return "ERROR: 11108 Abnormal network connection";
                case MLAftErrors.ERR_AUDIO_FILE_SIZE_OVERFLOW:
                    return "ERROR: 11103 Audio record file size overflow";
                case MLAftErrors.ERR_ILLEGAL_PARAMETER:
                    return "ERROR: 11106 Illegal parameter";
                case MLAftErrors.ERR_INTERNAL:
                    return "ERROR: 11198 Internal error";
                case MLAftErrors.ERR_AUTHORIZE_FAILED:
                    return "ERROR: 11116 Authentication failed";
                case MLAftErrors.ERR_AUDIO_TRANSCRIPT_FAILED:
                    return "ERROR: 11111 Audio Transcript failed";
                case MLAftErrors.ERR_FILE_NOT_FOUND:
                    return "ERROR: 11105 File Not Found";
                case MLAftErrors.ERR_ENGINE_BUSY:
                    return "ERROR: 11107 Aft Engine is Busy";
                case MLAftErrors.ERR_RESULT_WHEN_UPLOADING:
                    return "ERROR: 11109 Result When Uploading failed";
                default:
                    return "";
            }
        }
    };


    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Audio Recording Operations

    private void createRecordDialogAndRegisterRecordingListener() {
        customAudioRecordDialog = new CustomAudioRecordDialog(this, CustomAudioRecordDialog.TYPE_WITH_COMPLETE_BUTTON);
        customAudioRecordDialog.setOnCompleteListener(() -> {
            Log.i(TAG, "createRecordDialogAndRegisterRecordingListener setOnCompleteListener ");
            audioRecordManager.recordingComplete();
            customAudioRecordDialog.dismiss();
        });
        customAudioRecordDialog.setOnBackPressedListener(() -> {
            Log.i(TAG, "createRecordDialogAndRegisterRecordingListener setOnBackPressedListener ");
            audioRecordManager.release();
            customAudioRecordDialog.dismiss();
        });
        //String dirPath = Environment.getExternalStorageDirectory() + "/audio_path";
        String dirPath = getApplicationContext().getExternalFilesDir("audio_path").getPath();
        Log.i(TAG, "createRecordDialogAndRegisterRecordingListener dirPath : " + dirPath);
        audioRecordManager = new CustomAudioRecordManager(dirPath, customAudioRecordDialog);
        audioRecordManager.setOnRecordingStateListener(recordingStateListener);
    }

    private CustomAudioRecordManager.RecordingListener recordingStateListener = new CustomAudioRecordManager.RecordingListener() {
        @Override
        public void recordingReady() {
            Log.i(TAG, "RecordingListener : recordingReady");
            audioRecordManager.startRecording();
        }

        @Override
        public void onComplete(float durationTime, String filePath) {
            Log.i(TAG, "RecordingListener : onComplete durationTime:" + durationTime + " filePath:" + filePath);
            File fileToTrans = new File(filePath);
            Uri uri = Uri.fromFile(fileToTrans);
            Log.i(TAG, "RecordingListener : onComplete start asr in path : " + uri.toString());
            aftAnalyzerTaskId = startAftTransfer(uri);
            Log.i(TAG, "RecordingListener : aftAnalyzerTaskId  " + aftAnalyzerTaskId);

            showAudioRecordConvertingProcessDialog();

            Log.i(TAG, "RecordingListener : FileUtils.deleteQuietly : " + fileToTrans);
            FileUtils.deleteQuietly(fileToTrans);
        }

        private void showAudioRecordConvertingProcessDialog() {
            Log.i(TAG, "showAudioRecordConvertingProcessDialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(AudioFileTranscriptionActivity.this);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.dialog_audio_record_save_progress, null);
            builder.setView(dialogView);
            dialogAudioRecordConvertingProcess = builder.create();
            dialogAudioRecordConvertingProcess.show();
            Button cancelBtn = dialogView.findViewById(R.id.convert_cancel);
            cancelBtn.setOnClickListener(view -> {
                Log.i(TAG, "showAudioRecordConvertingProcessDialog cancelBtn click");
                if (dialogAudioRecordConvertingProcess != null) {
                    Log.i(TAG, "showAudioRecordConvertingProcess dismiss destroyTask " + aftAnalyzerTaskId);
                    dialogAudioRecordConvertingProcess.dismiss();
                    aftEngineAnalyzer.destroyTask(aftAnalyzerTaskId);
                }
            });
        }
    };

    public String startAftTransfer(Uri uri) {
        Log.i(TAG, "startAftTransfer " + uri.toString() + " " + uri.getAuthority() + " " + uri.getEncodedAuthority());
        String taskId;
        //Log.i(TAG, "startAftTransfer : uri timeDuration " + getMediaPlayerDurationWithUri(getApplicationContext(), uri));
        MLRemoteAftSetting setting = new MLRemoteAftSetting.Factory()
                // Set the transcription language code, complying with the BCP 47 standard.
                // Currently, Mandarin Chinese and English are supported.
                .setLanguageCode(languageCode)
                // true: Return the text transcription result of each audio segment and the corresponding time shift.
                // false: Return only the text transcription result of the audio file
                .enableWordTimeOffset(true)
                // true: Return the text transcription result of time shift of a sentence in the audio file.
                // false: Return only the text transcription result of the audio file.
                .enableSentenceTimeOffset(true)
                // true: Punctuations will be automatically added to the converted text.
                // false: Punctuations will not be automatically added to the converted text.
                .enablePunctuation(true)
                .create();
        taskId = aftEngineAnalyzer.shortRecognize(uri, setting);
        Log.i(TAG, "startAftTransfer : aftEngineAnalyzer.shortRecognize taskId : " + taskId);
        return taskId;

    }

    public static long getMediaPlayerDurationWithUri(Context context, Uri uri) {
        long mediaPlayerDuration = 0;
        if (context == null || uri == null) {
            return 0;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            Log.i(TAG, "getDuration mediaPlayer.setDataSource : " + uri.getPath() + " : " + uri.toString());
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.prepare();
            mediaPlayerDuration = mediaPlayer.getDuration();
        } catch (IOException e) {
            Log.e(TAG, "getDuration Exception : " + " " + e.getMessage(), e);
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        return mediaPlayerDuration;
    }

    private void showAudioRecordDialogAndPrepareAudio() {
        if (customAudioRecordDialog != null) {
            customAudioRecordDialog.show();
        }
        Log.i(TAG, "showDialogStartRecord : call saudioRecordManager.prepareAudio()");
        audioRecordManager.prepareAudio();
    }

    //endregion
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region Audio Record Converting Process Dialog


    private void dismissAudioRecordConvertingProcessDialog() {
        if (dialogAudioRecordConvertingProcess != null) {
            Log.i(TAG, "dismissAudioRecordConvertingProcessDialog remove waiting dialog");
            dialogAudioRecordConvertingProcess.dismiss();
        }
    }

    /* ------------------------------------------------------------------------------------------ */



    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations

    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Audio File Transcription Success Results : " + text);
        setVisibleRecordResultLayout(true);
        recordResults.setText(text);
        resultLogs.setText("Audio File Transcription Success Results : with " + text.length() + " characters :\n" + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Audio File Transcription was Failed Results : " + msg);
        setVisibleRecordResultLayout(false);
        recordResults.setText(msg);
        resultLogs.setText("Audio File Transcription was Failed Results : \n" + msg);
        Utils.showToastMessage(getApplicationContext(), "Audio File Transcription was Failed : \n" + msg);
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




    /* ------------------------------------------------------------------------------------------ */
    //region Spinners Operations

    private void createAndSetSpinner() {

        ArrayAdapter<String> spAdapterLang = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LANG_LIST);
        spAdapterLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAftLanguage.setAdapter(spAdapterLang);

        spAftLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageCode = LANG_CODE.get(position);
                Log.i(TAG, "createAndSetSpinner languageCode : " + languageCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be trigger when nothing is selected.
            }
        });

    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}