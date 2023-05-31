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

package com.hms.explorehms.huawei.feature_audioeditorkit.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_audioeditorkit.R;
import com.huawei.hms.audioeditor.sdk.AudioParameters;
import com.huawei.hms.audioeditor.sdk.ChangeVoiceOption;
import com.huawei.hms.audioeditor.sdk.HAEChangeVoiceStream;
import com.huawei.hms.audioeditor.sdk.HAEChangeVoiceStreamCommon;
import com.huawei.hms.audioeditor.sdk.HAEEqualizerStream;
import com.huawei.hms.audioeditor.sdk.HAEErrorCode;
import com.huawei.hms.audioeditor.sdk.HAENoiseReductionStream;
import com.huawei.hms.audioeditor.sdk.HAESceneStream;
import com.huawei.hms.audioeditor.sdk.HAESoundFieldStream;
import com.huawei.hms.audioeditor.sdk.HAEVoiceBeautifierStream;
import com.huawei.hms.audioeditor.sdk.VoiceBeautifierType;
import com.huawei.hms.audioeditor.sdk.VoiceTypeCommon;
import com.huawei.hms.audioeditor.sdk.asset.HAEAudioAsset;
import com.huawei.hms.audioeditor.sdk.util.SmartLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioTypeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "AudioTypeActivity";
    private Button beginChangeCommon;
    ;
    private Button beginPlay;

    private Button getAudioType;
    private TextView setAudioType;
    private AudioTrack mAudioTrack;
    private AudioTrack mChangeVoiceAudioTrack;
    private volatile boolean isPlaying;

    private static final int TYPE_NONE = 0;
    private static final int TYPE_CHANGE_SOUND = 1;
    private static final int TYPE_REDUCTION = 2;
    private static final int TYPE_ENV = 3;
    private static final int TYPE_SOUND_FILED = 4;
    private static final int TYPE_EQ = 5;
    private static final int TYPE_CHANGE_SOUND_COMMON = 6;
    private static final int TYPE_VOICE_BEAUTIFIER = 7;
    private int currentType = TYPE_NONE;

    @SuppressLint("SdCardPath")
    private String pcmFilePath = "/sdcard/changeSound.pcm";

    private volatile boolean unFinish = true;
    private static final int BIT_DEPTH = 16;
    private static final int CHANNEL_COUNT = 2;
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 7056;
    private static final int CHANGE_VOICE_BUFFER_SIZE = 7056;

    // Save to File
    private boolean saveToFile = false;
    private String saveToFilePath = "/sdcard/cachePcm/changeSound-saved";
    private FileOutputStream saveToFileStream = null;

    private HAEChangeVoiceStream haeChangeVoiceStream;
    private HAEChangeVoiceStreamCommon haeChangeVoiceStreamCommon;
    private HAEVoiceBeautifierStream haeVoiceBeautifierStream;
    private HAENoiseReductionStream haeNoiseReductionStream;
    private HAESceneStream haeSceneStream;
    private HAESoundFieldStream haeSoundFieldStream;
    private HAEEqualizerStream haeEqualizerStream;

    private ChangeVoiceOption changeVoiceOption;

    private HAEAudioAsset audioAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_type);
        setupToolbar();

        initView();
        initAllAbility();
        File savePcmPath = new File(saveToFilePath);
        if (!savePcmPath.exists()) {
            if (!savePcmPath.mkdirs()) {
                SmartLog.i(TAG, "mkdirs failed");
            }
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt__link));
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    private void initView() {
        Spinner spinnerTypes = findViewById(R.id.spinner_audio_type);
        spinnerTypes.setOnItemSelectedListener(this);
        beginPlay = findViewById(R.id.btn_play_original_sound);
        beginPlay.setOnClickListener(this);
        beginChangeCommon = findViewById(R.id.btn_change_voice_common);
        beginChangeCommon.setOnClickListener(this);
        getAudioType = findViewById(R.id.btn_get_audio_type);
        getAudioType.setOnClickListener(this);
        setAudioType = findViewById(R.id.tv_show_audio_type);
    }

    private void initAllAbility() {
        audioAsset = new HAEAudioAsset("");
        haeChangeVoiceStream = new HAEChangeVoiceStream();
        changeVoiceOption = new ChangeVoiceOption();
        changeVoiceOption.setSpeakerSex(ChangeVoiceOption.SpeakerSex.MALE);
        changeVoiceOption.setVoiceType(ChangeVoiceOption.VoiceType.SEASONED);
        haeChangeVoiceStream.changeVoiceOption(changeVoiceOption);

        haeChangeVoiceStreamCommon = new HAEChangeVoiceStreamCommon();
        haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.SEASONED);

        haeNoiseReductionStream = new HAENoiseReductionStream();

        haeVoiceBeautifierStream = new HAEVoiceBeautifierStream();
        haeVoiceBeautifierStream.setVoiceBeautifierType(VoiceBeautifierType.CLEAR);

        haeSceneStream = new HAESceneStream();
        haeSceneStream.setEnvironmentType(AudioParameters.ENVIRONMENT_TYPE_BROADCAST);

        haeSoundFieldStream = new HAESoundFieldStream();
        haeSoundFieldStream.setSoundType(AudioParameters.SOUND_FIELD_WIDE);

        haeEqualizerStream = new HAEEqualizerStream();
        haeEqualizerStream.setEqParams(AudioParameters.EQUALIZER_POP_VALUE);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change_voice_common:
                changeVoiceCommon();
                break;
            case R.id.btn_play_original_sound:
                beginDealPcmFile(TYPE_NONE);
                break;
            case R.id.btn_get_audio_type:
                getAudioType();
                break;
            default:
                break;
        }
    }

    private void getAudioType() {
        String audioType = String.valueOf(audioAsset.getVoiceType());
        setAudioType.setText(audioType);
    }

    private void changeVoiceCommon() {
        if (isPlaying) {
            return;
        }
        int res = haeChangeVoiceStreamCommon.setAudioFormat(BIT_DEPTH, CHANNEL_COUNT, SAMPLE_RATE);
        if (res != HAEErrorCode.SUCCESS) {
            Toast.makeText(AudioTypeActivity.this, "err code : " + res, Toast.LENGTH_SHORT).show();
            return;
        }
        beginDealPcmFile(TYPE_CHANGE_SOUND_COMMON);
    }

    private void beginDealPcmFile(int type) {
        if (isPlaying) {
            return;
        }
        isPlaying = true;

        currentType = type;
        new Thread(() -> {
            AssetManager.AssetInputStream fileInputStream = null;
            try {
                fileInputStream = (AssetManager.AssetInputStream) getAssets().open("stream.pcm");
                int bufferSize;
                bufferSize = CHANGE_VOICE_BUFFER_SIZE;
                byte[] buffer = new byte[bufferSize];
                byte[] resultByte = null;
                if (saveToFile) {
                    saveToFileStream = new FileOutputStream(new File(saveToFilePath + "_" + System.currentTimeMillis() + ".pcm"));
                }
                while (fileInputStream.read(buffer) != -1 && unFinish) {
                    if (currentType == TYPE_CHANGE_SOUND) {
                        resultByte = haeChangeVoiceStream.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else if (currentType == TYPE_CHANGE_SOUND_COMMON) {
                        resultByte = haeChangeVoiceStreamCommon.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else if (currentType == TYPE_VOICE_BEAUTIFIER) {
                        resultByte = haeVoiceBeautifierStream.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else if (currentType == TYPE_REDUCTION) {
                        resultByte = haeNoiseReductionStream.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else if (currentType == TYPE_ENV) {
                        resultByte = haeSceneStream.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else if (currentType == TYPE_SOUND_FILED) {
                        resultByte = haeSoundFieldStream.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else if (currentType == TYPE_EQ) {
                        resultByte = haeEqualizerStream.applyPcmData(buffer);
                        playPcm(resultByte);
                    } else {
                        playPcm(buffer);
                    }

                    // Save to File
                    if (saveToFile && resultByte != null) {
                        saveToFileStream.write(resultByte);
                    }
                }
            } catch (IOException e) {
                SmartLog.e(TAG, e.getMessage());
            } finally {
                releaseAllAbility();
                isPlaying = false;
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        SmartLog.e(TAG, e.getMessage());
                    }
                }

                if (saveToFileStream != null) {
                    try {
                        saveToFileStream.close();
                    } catch (IOException e) {
                        SmartLog.e(TAG, e.getMessage());
                    }
                    saveToFileStream = null;
                }
            }
        }).start();
    }

    private void playPcm(byte[] pcmData) {
        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE, AudioTrack.MODE_STREAM);

            mAudioTrack.play();
        }
        if (mChangeVoiceAudioTrack == null) {
            mChangeVoiceAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, CHANGE_VOICE_BUFFER_SIZE, AudioTrack.MODE_STREAM);

            mChangeVoiceAudioTrack.play();
        }
        if (pcmData != null && pcmData.length > 0) {
            if (currentType == TYPE_CHANGE_SOUND) {
                if (mChangeVoiceAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    mChangeVoiceAudioTrack.write(pcmData, 0, pcmData.length);
                }
            } else {
                if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    mAudioTrack.write(pcmData, 0, pcmData.length);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        unFinish = false;
        releaseAudioTrack();
        releaseAllAbility();
        super.onDestroy();
    }

    private void releaseAllAbility() {
        if (haeChangeVoiceStream != null) {
            haeChangeVoiceStream.release();
        }
        if (haeVoiceBeautifierStream != null) {
            haeVoiceBeautifierStream.release();
        }
        if (haeChangeVoiceStreamCommon != null) {
            haeChangeVoiceStreamCommon.release();
        }
        if (haeNoiseReductionStream != null) {
            haeNoiseReductionStream.release();
        }
        if (haeSceneStream != null) {
            haeSceneStream.release();
        }
        if (haeSoundFieldStream != null) {
            haeSoundFieldStream.release();
        }
        if (haeEqualizerStream != null) {
            haeEqualizerStream.release();
        }
    }

    private void releaseAudioTrack() {
        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                mAudioTrack.stop();
            }
            mAudioTrack.release();
            mAudioTrack = null;
        }

        if (mChangeVoiceAudioTrack != null) {
            if (mChangeVoiceAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                mChangeVoiceAudioTrack.stop();
            }
            mChangeVoiceAudioTrack.release();
            mChangeVoiceAudioTrack = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedType = adapterView.getSelectedItem().toString();

        if (selectedType.toUpperCase().equals(VoiceTypeCommon.SEASONED.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.SEASONED);
            audioAsset.changeVoiceType(VoiceTypeCommon.SEASONED);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.CUTE.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.CUTE);
            audioAsset.changeVoiceType(VoiceTypeCommon.CUTE);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.FEMALE.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.FEMALE);
            audioAsset.changeVoiceType(VoiceTypeCommon.FEMALE);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.MALE.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.MALE);
            audioAsset.changeVoiceType(VoiceTypeCommon.MALE);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.MONSTER.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.MONSTER);
            audioAsset.changeVoiceType(VoiceTypeCommon.MONSTER);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.TRILL.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.TRILL);
            audioAsset.changeVoiceType(VoiceTypeCommon.TRILL);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.NORMAL.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.NORMAL);
            audioAsset.changeVoiceType(VoiceTypeCommon.NORMAL);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.CYBERPUNK.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.CYBERPUNK);
            audioAsset.changeVoiceType(VoiceTypeCommon.CYBERPUNK);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.WAR.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.WAR);
            audioAsset.changeVoiceType(VoiceTypeCommon.WAR);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.MIX.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.MIX);
            audioAsset.changeVoiceType(VoiceTypeCommon.MIX);
        } else if (selectedType.toUpperCase().equals(VoiceTypeCommon.SYNTH.toString())) {
            haeChangeVoiceStreamCommon.changeVoiceType(VoiceTypeCommon.SYNTH);
            audioAsset.changeVoiceType(VoiceTypeCommon.SYNTH);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}