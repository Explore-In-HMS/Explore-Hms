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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.textToSpeech;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment;
import com.huawei.hms.mlsdk.tts.MLTtsCallback;
import com.huawei.hms.mlsdk.tts.MLTtsConfig;
import com.huawei.hms.mlsdk.tts.MLTtsConstants;
import com.huawei.hms.mlsdk.tts.MLTtsEngine;
import com.huawei.hms.mlsdk.tts.MLTtsError;
import com.huawei.hms.mlsdk.tts.MLTtsSpeaker;
import com.huawei.hms.mlsdk.tts.MLTtsWarn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TextToSpeechActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = TextToSpeechActivity.class.getSimpleName();

    private Unbinder unbinder;

    private MLTtsEngine ttsEngine;

    private ArrayAdapter<String> spAdapterSoundType;

    private static String audioFileNamePcm;
    private static String audioFileNameWav;
    private static MediaPlayer mediaPlayer;

    private static final int PERMISSION_CODE_STORAGE_SAVE_AUDIO_FILE = 1;
    private static final int PERMISSION_CODE_STORAGE_PLAY_AUDIO_FILE = 1;
    String[] permissionRequestStorageAudioFile = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private String speechParamLanguage = "English";
    private String speechParamLanguageCode;
    private String speechParamSoundType = "en-US-st-1";
    private String speechParamPlayMode = "Queuing Mode";

    private float speechParamSpeed = 1.1f; // 1-2
    private float speechParamVolume = 1.5f; // 1-2

    private boolean isPauseSpeech = false;
    private int mediaPlayerLastPosition = 0;

    //region languageVariables

    private static final List<String> LIST_LANGUAGE = new ArrayList<>(Arrays.asList(
            "English", "French", "Italian", "Spanish", "German", "Chinese"));

    private static final List<String> LIST_SOUND_TYPE = new ArrayList<>(
            Arrays.asList("en-US-st-1", "en-US-st-2", "fr-FR-st-1", "it-IT-st-1", "es-ES-st-1", "de-DE-st-1", "zh-Hans-st-1", "zh-Hans-st-2"));

    private static final Map<String, String> LIST_SOUND_TYPE_EN = new HashMap<>();

    private static final Map<String, String> LIST_SOUND_TYPE_FR = new HashMap<>();
    private static final Map<String, String> LIST_SOUND_TYPE_IT = new HashMap<>();
    private static final Map<String, String> LIST_SOUND_TYPE_ES = new HashMap<>();
    private static final Map<String, String> LIST_SOUND_TYPE_DE = new HashMap<>();
    private static final Map<String, String> LIST_SOUND_TYPE_ZH = new HashMap<>();

    private static final List<String> LIST_PLAY_MODE = new ArrayList<>(Arrays.asList("Queuing Mode", "Clear Mode"));

    //endregion

    //region views

    @Nullable
    @BindView(R.id.spTtsLanguage)
    Spinner spTtsLanguage;

    @Nullable
    @BindView(R.id.spTtsSoundType)
    Spinner spTtsSoundType;

    @Nullable
    @BindView(R.id.spTtsPlayMode)
    Spinner spTtsPlayMode;

    @Nullable
    @BindView(R.id.et_inputTextToSpeech)
    EditText etInputTextToSpeech;

    @Nullable
    @BindView(R.id.btn_delete)
    ImageView btnDeleteText;

    @Nullable
    @BindView(R.id.tv_inputTextCharCount)
    TextView tvInputTextCharCount;

    @Nullable
    @BindView(R.id.tv_volume)
    TextView tvVolume;

    @Nullable
    @BindView(R.id.sb_volume)
    SeekBar sbVolume;

    @Nullable
    @BindView(R.id.tv_speed)
    TextView tvSpeed;

    @Nullable
    @BindView(R.id.sb_speed)
    SeekBar sbSpeed;

    @Nullable
    @BindView(R.id.btn_speak)
    Button btnSpeak;

    @Nullable
    @BindView(R.id.btn_playSpeech)
    Button btnPlaySpeech;

    @Nullable
    @BindView(R.id.btn_pauseSpeech)
    Button btnPauseSpeech;

    @Nullable
    @BindView(R.id.btn_stopSpeech)
    Button btnStopSpeech;


    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @BindView(R.id.resultLogs)
    TextView resultLogs;

    //endregion views

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        Utils.setApiKeyForRemoteMLApplication(getApplicationContext());
        setupToolbar();

        speechParamLanguageCode = getString(R.string.en_us);
        unbinder = ButterKnife.bind(this);

        createAndSetSpinners();

        sbSpeed.setOnSeekBarChangeListener(onSeekBarChangeListener);
        sbVolume.setOnSeekBarChangeListener(onSeekBarChangeListener);
        initializeMaps();
        createMlTtsEngineAndSetConfigAndSetTtsCallback();

        getLanguageAndSpeakerCodeListsFromTtsEngine();

        // for take permissions and prepare mediaPlayer with invoke createAudioFileAndSetFilePaths
        ActivityCompat.requestPermissions(this, permissionRequestStorageAudioFile, PERMISSION_CODE_STORAGE_SAVE_AUDIO_FILE);

        updateLength(tvInputTextCharCount, etInputTextToSpeech.getText().toString().length());

        etInputTextToSpeech.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //This is a method which will be triggered before the text changed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.i(TAG, "onTextChanged start : " + start + " : before : " + before + " : count : " + count);
                updateLength(tvInputTextCharCount, charSequence.toString().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i(TAG, "afterTextChanged editable : " + editable.toString() + " : length : " + editable.toString().length());
            }
        });

    }

    private void initializeMaps() {
        LIST_SOUND_TYPE_EN.put("EN Female", "en-US-st-1");
        LIST_SOUND_TYPE_EN.put("EN Male", "en-US-st-2");

        LIST_SOUND_TYPE_FR.put("FR Female", "fr-FR-st-1");
        LIST_SOUND_TYPE_IT.put("IT Female", "it-IT-st-1");
        LIST_SOUND_TYPE_ES.put("ES Female", "es-ES-st-1");
        LIST_SOUND_TYPE_DE.put("DE Female", "de-DE-st-1");
        LIST_SOUND_TYPE_ZH.put("ZH Female", "zh-Hans-st-1");
        LIST_SOUND_TYPE_ZH.put("ZH Male", "zh-Hans-st-2");

    }

    private void createAudioFileAndSetFilePaths() throws IOException {
        String audioPath = FileUtils.initFile(this);
        audioFileNameWav = audioPath + "/tts.wav";
        audioFileNamePcm = audioPath + "/tts.pcm";
        audioFileNameWav = FileTransformUtils.convertWaveFilePcmToWav(
                audioFileNamePcm,
                audioFileNameWav,
                16000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFileNameWav);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "createAudioFileAndSetFilePath IOException : " + e.getMessage(), e);
            Utils.showToastMessage(getApplicationContext(), "createAudioFileAndSetFilePaths mediaPlayer exception : " + e.getMessage());
        }
    }


    @OnClick({R.id.btn_speak, R.id.btn_playSpeech, R.id.btn_pauseSpeech, R.id.btn_stopSpeech, R.id.btn_delete})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btn_speak:
                hideSoftKeyboard(this, etInputTextToSpeech);
                doTextToSpeech();
                break;
            case R.id.btn_playSpeech:
                ActivityCompat.requestPermissions(this, permissionRequestStorageAudioFile, PERMISSION_CODE_STORAGE_PLAY_AUDIO_FILE);
                break;
            case R.id.btn_pauseSpeech:
                pauseOrResumeMediaPlayerAndTtsEngine();
                break;
            case R.id.btn_stopSpeech:
                stopMediaPlayerAndTtsEngineForAudioSpeech();
                break;
            case R.id.btn_delete:
                etInputTextToSpeech.setText("");
                hideSoftKeyboard(this, etInputTextToSpeech);
                break;
            default:
                Log.i(TAG, "Default case");
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_lrs_tts));
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

        if (requestCode == PERMISSION_CODE_STORAGE_SAVE_AUDIO_FILE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission -> set and edit createAudioFileAndSetFilePaths");
                try {
                    createAudioFileAndSetFilePaths();
                } catch (IOException e) {
                    Log.e(TAG, "onRequestPermissionsResult: " + e.getMessage());
                }
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not Save And Play Audio File without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }
        if (requestCode == PERMISSION_CODE_STORAGE_PLAY_AUDIO_FILE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission -> set and edit playMediaPlayerForAudioSpeech");
                // Use the system player to play the cached audio.
                playMediaPlayerForAudioSpeech(audioFileNameWav);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not Read And Play Audio File without Storage Permission!",
                        getString(R.string.yes_go), getString(R.string.cancel));

            }
        }

    }


    private final OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            String text = progress * 100 / seekBar.getMax() + "%";
            switch (seekBar.getId()) {
                case R.id.sb_volume:
                    Log.i(TAG, "SeekBarChangeLister Volume : " + progress);
                    if (progress == 0) {
                        String text2 = 100 / seekBar.getMax() + "%";
                        seekBar.setProgress(1);
                        tvVolume.setText(text2);
                    } else {
                        tvVolume.setText(text);
                    }
                    break;
                case R.id.sb_speed:
                    Log.i(TAG, "SeekBarChangeLister Speed : " + progress);
                    if (progress == 0) {
                        String text2 = 100 / seekBar.getMax() + "%";
                        seekBar.setProgress(1);
                        tvSpeed.setText(text2);
                    } else {
                        tvSpeed.setText(text);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //This method will be triggered on seekbar touch

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getId()) {
                case R.id.sb_volume:  // volumeSeek
                    speechParamVolume = seekBar.getProgress() * 20 / (100 * 10f);
                    Log.i(TAG, "SeekBarChangeLister speechParamVolume : " + speechParamVolume);
                    updateTtsEngineConfig();
                    break;
                case R.id.sb_speed:  // speedSeek
                    speechParamSpeed = seekBar.getProgress() * 20 / (100 * 10f);
                    Log.i(TAG, "SeekBarChangeLister speechParamSpeed : " + speechParamSpeed);
                    updateTtsEngineConfig();
                    break;
                default:
                    break;
            }
        }
    };

    private final Map<String, String> mlTtsEngineSpeakTaskMap = new HashMap<>();

    private void createMlTtsEngineAndSetConfigAndSetTtsCallback() {

        Log.w(TAG, "createMlTtsEngineAndSetConfigAndSetTtsCallback language：" + speechParamLanguageCode + "   " + " person : " + speechParamSoundType);
        Log.w(TAG, "createMlTtsEngineAndSetConfigAndSetTtsCallback volume：" + speechParamVolume + "   " + " speed : " + speechParamSpeed);
        MLTtsConfig mlTtsConfig = new MLTtsConfig()
                .setVolume(speechParamVolume)
                .setSpeed(speechParamSpeed)
                .setPerson(speechParamSoundType)
                .setLanguage(speechParamLanguageCode);

        ttsEngine = new MLTtsEngine(mlTtsConfig);

        // Set playback callback
        ttsEngine.setTtsCallback(mlTtsCallback);
    }

    private void getLanguageAndSpeakerCodeListsFromTtsEngine() {

        List<String> mlTtsEngineLanguageCodeList = ttsEngine.getLanguages();
        Log.d("getLanguageAndSpeakerCodeListsFromTtsEngine languageList", mlTtsEngineLanguageCodeList.toString());
        speechParamLanguageCode = mlTtsEngineLanguageCodeList.get(0);
        Log.d("getLanguageAndSpeakerCodeListsFromTtsEngine default lang code ", speechParamLanguageCode);

        List<MLTtsSpeaker> mlTtsEngineSpeakerCodeList = ttsEngine.getSpeaker(speechParamLanguageCode);
        Log.d("getLanguageAndSpeakerCodeListsFromTtsEngine seakerList", mlTtsEngineSpeakerCodeList.size() + " " + mlTtsEngineSpeakerCodeList.toString());
        speechParamSoundType = mlTtsEngineSpeakerCodeList.get(0).getName();
    }


    private void updateTtsEngineConfig() {
        Log.w(TAG, "updateTtsEngineConfig language：" + speechParamLanguageCode + "   " + " person : " + speechParamSoundType);
        Log.w(TAG, "updateTtsEngineConfig volume：" + speechParamVolume + "   " + " speed : " + speechParamSpeed);
        MLTtsConfig mlTtsConfig = new MLTtsConfig()
                .setVolume(speechParamVolume)
                .setSpeed(speechParamSpeed)
                .setPerson(speechParamSoundType)
                .setLanguage(speechParamLanguageCode);
        // Set the volume. Range: 0–2. 1.0 indicates normal volume.
        // Set the speech speed. Range: 0–2. 1.0 indicates normal speed.
        // Set the speaker timbre.
        // MLTtsConstants.TTS_SPEAKER_FEMALE_ZH: Chinese female voice.
        // MLTtsConstants.TTS_SPEAKER_MALE_ZH: Chinese male voice.
        // Set the English timbre.
        // MLTtsConstants.TTS_SPEAKER_FEMALE_EN: English female voice.
        // MLTtsconstants.TTS_SPEAKER_MALE_EN: English male voice.
        // MLTtsConstants.TTS_SPEAKER_FEMALE_FR: French female voice.
        // MLTtsconstants.TTS_SPEAKER_FEMALE_ES: Spanish female voice.
        // Set the text converted from speech to Chinese.
        // setLanguage( MLTtsConstants.TTS_EN_US)
        // Set the text converted from speech to Chinese.
        // MLTtsConstants.TTS_EN_US: converts text to English.
        // MLTtsConstants.TTS_ZH_HANS: converts text to Chinese.
        // MLTtsConstants.TTS_LAN_ES_ES: converts text to Spanish.
        // MLTtsConstants.TTS_LAN_FR_FR: converts text to French.
        ttsEngine.updateConfig(mlTtsConfig);
        //  check it out this :
        // List<MLTtsSpeaker> mlTtsSpeakerCodeList = ttsEngine.getSpeaker(speechParamLanguageCode);
    }

    /* ------------------------------------------------------------------------------------------ */
    //region Spinners Operations

    private void createAndSetSpinners() {
        // Currently, Mandarin Chinese standard male and female voices
        // (including Chinese-English bilingual speech),
        // English standard male and female voices,
        // French standard female voice,
        // and Spanish standard female voice are available.

        ArrayAdapter<String> spAdapterLang = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LIST_LANGUAGE);
        spAdapterLang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTtsLanguage.setAdapter(spAdapterLang);

        spAdapterSoundType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LIST_SOUND_TYPE);
        spAdapterSoundType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTtsSoundType.setAdapter(spAdapterSoundType);
        spTtsSoundType.setSelection(0);
        spAdapterSoundType.notifyDataSetChanged();

        // set after soundtype spinner cause related edit with these selection
        spTtsLanguage.setSelection(0);
        spAdapterLang.notifyDataSetChanged();

        ArrayAdapter<String> spAdapterPlayMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LIST_PLAY_MODE);
        spAdapterPlayMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTtsPlayMode.setAdapter(spAdapterPlayMode);
        spTtsPlayMode.setSelection(0);
        spAdapterPlayMode.notifyDataSetChanged();

        spTtsLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speechParamLanguage = LIST_LANGUAGE.get(position);
                Log.i(TAG, "createAndSetSpinners speechParamLanguage : " + position + " : " + speechParamLanguage);
                editAndUpdateSpinnersSoundTypeByLanguage(speechParamLanguage, position + 1);

                updateTtsEngineConfig();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be triggered when nothing is selected.
            }
        });


        spTtsSoundType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i(TAG, "onItemSelected id : " + id);
                Log.i(TAG, "onItemSelected position : " + position);
                Log.i(TAG, "onItemSelected getSelectedItem : " + spTtsSoundType.getSelectedItem());
                Log.i(TAG, "onItemSelected getSelectedItemId : " + spTtsSoundType.getSelectedItemId());
                Log.i(TAG, "onItemSelected getSelectedItemPosition : " + spTtsSoundType.getSelectedItemPosition());

                speechParamSoundType = getCurrentSoundType(speechParamLanguageCode, spTtsSoundType.getSelectedItem().toString(), position);

                Log.i(TAG, "createAndSetSpinners speechParamSoundType : " + position + " : " + speechParamSoundType);

                updateTtsEngineConfig();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be triggered when nothing is selected.
            }
        });

        spTtsPlayMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speechParamPlayMode = LIST_PLAY_MODE.get(position);
                Log.i(TAG, "createAndSetSpinners speechParamPlayMode : " + position + " : " + speechParamPlayMode);
                updateTtsEngineConfig();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This method will be triggered when nothing is selected.
            }
        });
    }

    // check it out and edit with parametric method.
    private void editAndUpdateSpinnersSoundTypeByLanguage(String selectedLang, int selectedIndex) {
        Log.i(TAG, "editAndUpdateSpinnersSoundTypeByLanguage selectedIndex : " + selectedIndex + " selectedLang : " + selectedLang);
        List<String> keysList = new ArrayList<>(LIST_SOUND_TYPE_EN.keySet());
        switch (selectedIndex) {
            case 1:
                speechParamLanguageCode = getString(R.string.en_us);
                keysList = new ArrayList<>(LIST_SOUND_TYPE_EN.keySet());
                break;
            case 2:
                speechParamLanguageCode = "fr-FR";
                keysList = new ArrayList<>(LIST_SOUND_TYPE_FR.keySet());
                break;
            case 3:
                speechParamLanguageCode = "it-IT";
                keysList = new ArrayList<>(LIST_SOUND_TYPE_IT.keySet());
                break;
            case 4:
                speechParamLanguageCode = "es-ES";
                keysList = new ArrayList<>(LIST_SOUND_TYPE_ES.keySet());
                break;
            case 5:
                speechParamLanguageCode = "de-DE";
                keysList = new ArrayList<>(LIST_SOUND_TYPE_DE.keySet());
                break;
            case 6:
                speechParamLanguageCode = "zh-Hans";
                keysList = new ArrayList<>(LIST_SOUND_TYPE_ZH.keySet());
                break;
            default:
                Log.i(TAG, "Default");
        }
        spAdapterSoundType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, keysList);
        spTtsSoundType.setAdapter(spAdapterSoundType);
        spTtsSoundType.setSelection(0);
        spAdapterSoundType.notifyDataSetChanged();
    }

    private String getCurrentSoundType(String languageCode, String selectedTypeKey, int selectedPosition) {
        Log.i(TAG, "getCurrentListSoundType languageCode : " + languageCode);
        Log.i(TAG, "getCurrentListSoundType selectedTypeKey : " + selectedTypeKey);
        Log.i(TAG, "getCurrentListSoundType selectedPosition : " + selectedPosition);
        switch (languageCode) {
            case "en-US":
                speechParamSoundType = LIST_SOUND_TYPE_EN.get(selectedTypeKey);
                break;
            case "fr-FR":
                speechParamSoundType = LIST_SOUND_TYPE_FR.get(selectedTypeKey);
                break;
            case "it-IT":
                speechParamSoundType = LIST_SOUND_TYPE_IT.get(selectedTypeKey);
                break;
            case "es-ES":
                speechParamSoundType = LIST_SOUND_TYPE_ES.get(selectedTypeKey);
                break;
            case "de-DE":
                speechParamSoundType = LIST_SOUND_TYPE_DE.get(selectedTypeKey);
                break;
            case "zh-Hans":
                speechParamSoundType = LIST_SOUND_TYPE_ZH.get(selectedTypeKey);
                break;
            default:
                speechParamSoundType = LIST_SOUND_TYPE_EN.get(selectedTypeKey);
        }
        return speechParamSoundType;
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */


    /* ------------------------------------------------------------------------------------------ */
    //region MediaPlayer Play Pause Resume Stop Operations with TTSEngine Callback

    private void playMediaPlayerForAudioSpeech(String audioFilePath) {
        Log.d(TAG, "playMediaPlayerForAudioSpeech");
        restartMediaPlayer(audioFilePath);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void pauseOrResumeMediaPlayerAndTtsEngine() {
        if (mediaPlayer == null) {
            Utils.showToastMessage(getApplicationContext(), "You should allow storage permission before to save audio file and use mediaPlayer!");
            ActivityCompat.requestPermissions(this, permissionRequestStorageAudioFile, PERMISSION_CODE_STORAGE_SAVE_AUDIO_FILE);
        } else {
            isPauseSpeech = !isPauseSpeech;
            btnPauseSpeech.setText(isPauseSpeech ? getString(R.string.resume) : getString(R.string.pause));
            if (isPauseSpeech) {
                btnPauseSpeech.setText(getString(R.string.resume));
                ttsEngine.pause();
                mediaPlayerLastPosition = mediaPlayer.getCurrentPosition();
                Log.i(TAG, "pauseOrResumeMediaPlayerAndTtsEngine pause mediaPlayerLastPosition : " + mediaPlayerLastPosition);
            } else {
                btnPauseSpeech.setText(getString(R.string.pause));
                ttsEngine.resume();
                Log.i(TAG, "pauseOrResumeMediaPlayerAndTtsEngine resume mediaPlayerLastPosition : " + mediaPlayerLastPosition);
            }
        }
    }

    // check it out for file not found exception
    private void restartMediaPlayer(String path) {
        Log.i(TAG, "restartMediaPlayer with path : " + path);
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            Log.e(TAG, "restartMediaPlayer IOException : " + e.getMessage(), e);
        }
    }

    private void stopMediaPlayerAndTtsEngineForAudioSpeech() {
        Log.i(TAG, "stopMediaPlayerAndTtsEngineForAudioSpeech");
        if (mediaPlayer == null) {
            Utils.showToastMessage(getApplicationContext(), "You should allow storage permission before to save audio file and use mediaPlayer!");
            ActivityCompat.requestPermissions(this, permissionRequestStorageAudioFile, PERMISSION_CODE_STORAGE_SAVE_AUDIO_FILE);
        } else {
            ttsEngine.stop();
        }
    }

    public void doTextToSpeech() {

        if (Utils.haveNetworkConnection(this)) {
            if (ttsEngine == null) {
                return;
            }
            if (isPauseSpeech) {
                isPauseSpeech = false;
                btnPauseSpeech.setText(getString(R.string.pause));
            }
            sendMLTtsCallbackHandlerMsg(MESSAGE_TYPE_INFO, null);
            String text = etInputTextToSpeech.getText().toString();
            if (text.isEmpty()) {
                Utils.showToastMessage(getApplicationContext(), "Please Enter the Input Text for Speech");
            }

            String id = ttsEngine.speak(
                    text,
                    speechParamPlayMode.contains("Queuing") ? MLTtsEngine.QUEUE_FLUSH :
                            MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM
            );
            mlTtsEngineSpeakTaskMap.put(id, text);
        } else {
            DialogUtils.showDialogNetworkWarning(
                    this,
                    "NEED NETWORK PERMISSION",
                    "Would You Like To Go To Settings To Open Network?",
                    R.drawable.icon_settings,
                    "You can not Text To Speech without Internet Connection!",
                    getString(R.string.yes_go), getString(R.string.cancel));
        }
    }

    private static final String NO_NETWORK = "0104";
    private static final String SPEAK_ABNORMAL = "7002";

    private Handler ttsCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            Log.i(TAG, "MLTtsCallback HandlerMessageInfo");
            if (message.what == MESSAGE_TYPE_INFO) {
                String extension = (String) message.obj;
                if (extension == null) {
                    btnSpeak.setText("Speech - Speak");
                    return false;
                }
                if (NO_NETWORK.equals(extension)) {
                    Log.e(TAG, "MLTtsCallback HandlerMessageInfo NO_NETWORK : extension : " + extension + getString(R.string.message) + message.obj.toString());
                    Utils.showToastMessage(getApplicationContext(), "MLTtsCallback HandlerMessage : NO_NETWORK!");
                } else if (SPEAK_ABNORMAL.equals(extension)) {
                    Log.e(TAG, "MLTtsCallback HandlerMessageInfo SPEAK_ABNORMAL : extension : " + extension + getString(R.string.message) + message.obj.toString());
                    Log.e(TAG, "MLTtsCallback HandlerMessageInfo SPEAK_ABNORMAL : Language and speaker do not match!");
                    Utils.showToastMessage(getApplicationContext(), "MLTtsCallback HandlerMessage : SPEAK_ABNORMAL! \nLanguage and speaker do not match!");
                } else {
                    Log.e(TAG, "MLTtsCallback HandlerMessageInfo : extension : " + extension + getString(R.string.message) + message.obj.toString());
                    Log.e(TAG, "MLTtsCallback HandlerMessageInfo : MLTtsEngine Service is unavailable and abnormal!");
                    Utils.showToastMessage(getApplicationContext(), "MLTtsCallback HandlerMessage : MLTtsEngine Service is unavailable and abnormal!");
                }
                btnSpeak.setText("Replay Speech - Speak");
            }
            return false;
        }
    });


    private static final int MESSAGE_TYPE_INFO = 1;

    MLTtsCallback mlTtsCallback = new MLTtsCallback() {
        @Override
        public void onError(String taskId, MLTtsError err) {
            Log.e(TAG, "MLTtsCallback onError : " + err.getErrorMsg() + " : " + err.getExtension());
            sendMLTtsCallbackHandlerMsg(MESSAGE_TYPE_INFO, (String) err.getExtension());
        }

        @Override
        public void onWarn(String taskId, MLTtsWarn warn) {
            Log.e(TAG, "MLTtsCallback onWarn : " + warn.getWarnMsg() + " : " + warn.getExtension());
        }

        @Override
        public void onRangeStart(String taskId, int start, int end) {
            Log.i(TAG, "MLTtsCallback onRangeStart : start : " + start + " : end : " + end);
        }

        @Override
        public void onAudioAvailable(String s, MLTtsAudioFragment mlTtsAudioFragment, int i, Pair<Integer, Integer> pair, Bundle bundle) {
            Log.e(TAG, "MLTtsCallback onAudioAvailable : i : " + i + " : s : " + s + " : AUDIO_FILE_NAME_PCM : " + audioFileNamePcm);
            FileUtils.writeBufferToFile(mlTtsAudioFragment.getAudioData(), audioFileNamePcm, true);
        }

        @Override
        public void onEvent(String taskId, int eventID, Bundle bundle) {
            Log.i(TAG, "MLTtsCallback onEvent : " + eventID);
            // check it out for file not found exception
            // The synthesis is complete.
            if (eventID == MLTtsConstants.EVENT_SYNTHESIS_COMPLETE) {
                try {
                    audioFileNameWav = FileTransformUtils.convertWaveFilePcmToWav(
                            audioFileNamePcm,
                            audioFileNameWav,
                            16000,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT
                    );
                } catch (IOException e) {
                    Log.e(TAG, "onEvent: " + e.getMessage());
                }
                restartMediaPlayer(audioFileNameWav);
            }


            if (eventID == MLTtsConstants.EVENT_PLAY_START || eventID == MLTtsConstants.EVENT_PLAY_RESUME) {
                Log.i(TAG, "MLTtsCallback onEvent EVENT_PLAY_STOP : " + eventID);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (etInputTextToSpeech != null) {
                            etInputTextToSpeech.setTextColor(Color.parseColor("#2196F3"));
                        }
                    }
                });

               /* runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });*/

            }

            if (eventID == MLTtsConstants.EVENT_PLAY_STOP || eventID == MLTtsConstants.EVENT_PLAY_PAUSE) {
                Log.i(TAG, "MLTtsCallback onEvent EVENT_PLAY_STOP : " + eventID);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (etInputTextToSpeech != null) {
                            etInputTextToSpeech.setTextColor(Color.parseColor("#607D8B"));
                        }
                    }
                });

            }
        }
    };

    private void sendMLTtsCallbackHandlerMsg(int id, String str) {
        Message msg = new Message();
        msg.what = id;
        msg.obj = str;
        ttsCallbackHandler.sendMessage(msg);
    }


    //endregion
    /* ------------------------------------------------------------------------------------------ */



    /* ------------------------------------------------------------------------------------------ */
    //region Display Result Operations

    private void updateLength(TextView view, int length) {
        view.setText(String.format(Locale.ENGLISH, "%d words", length));
    }

    //endregion --------------------------------------------------------------------------------- //
    /* ------------------------------------------------------------------------------------------ */

    // check it  out and move it to utils class
    public static void hideSoftKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (ttsEngine != null) {
            ttsEngine.stop();
            ttsEngine = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (ttsEngine != null) {
            ttsEngine.stop();
            ttsEngine = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

}
