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

package com.genar.hmssandbox.huawei.feature_audioeditorkit.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.feature_audioeditorkit.R;
import com.genar.hmssandbox.huawei.feature_audioeditorkit.util.SampleConstant;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.audioeditor.sdk.HAEAudioExpansion;
import com.huawei.hms.audioeditor.sdk.HAEConstant;
import com.huawei.hms.audioeditor.sdk.HAEErrorCode;
import com.huawei.hms.audioeditor.sdk.OnTransformCallBack;
import com.huawei.hms.audioeditor.sdk.util.FileUtil;
import com.huawei.hms.audioeditor.ui.api.AudioInfo;
import com.huawei.hms.audioeditor.ui.common.bean.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AudioFormatActivity extends AppCompatActivity {
    private static final String TAG = "AudioFormatActivity";

    private final int SELECT_AUDIOS_REQUEST_CODE = 1000;

    private final String AUDIO_PATH = "audioPath";
    private final String AUDIO_NAME = "audioName";

    private TextView pathAudioFormat;
    private RadioGroup radioGroupAudioFormat;
    private MaterialButton transferAudioFormat;
    private MaterialButton playSource;
    private EditText source;
    private TextView audioName;
    private ProgressBar progressBar;
    private String transferFormat = "";

    private boolean isTansforming = false;

    private List<String> mAudioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_format);
        Toolbar toolBar = findViewById(R.id.toolbar_audio);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
        initData(savedInstanceState);
        initEvent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAudioList != null && mAudioList.size() > 0) {
            outState.putString(AUDIO_PATH, mAudioList.get(0));
            outState.putString(AUDIO_NAME, audioName.getText().toString());
        }
    }

    private void initView() {
        pathAudioFormat = findViewById(R.id.path_fragment_audio_format);
        radioGroupAudioFormat = findViewById(R.id.radio_group_fragment_audio_format);
        transferAudioFormat = findViewById(R.id.transfer_fragment_audio_format);
        progressBar = findViewById(R.id.progress_recycler_view_layout_audio_format_item);
        audioName = findViewById(R.id.audio_name);
        source=findViewById(R.id.et_extracted_source1);
        playSource=findViewById(R.id.btn_open_source1);
    }

    private void initData(Bundle savedInstanceState) {
        mAudioList = new ArrayList<>();
        if (savedInstanceState != null) {
            String oldAudioPath = savedInstanceState.getString(AUDIO_PATH);
            String oldAudioName = savedInstanceState.getString(AUDIO_NAME);
            if (!TextUtils.isEmpty(oldAudioPath)) {
                mAudioList.add(oldAudioPath);
            }
            audioName.setText(TextUtils.isEmpty(oldAudioName) ? "" : oldAudioName);
        } else {
            try {
                Intent intent = new Intent(SampleConstant.CHOOSE_AUDIO_ACTION);
                startActivityForResult(intent, SELECT_AUDIOS_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                // "can't find the choose audio activity"
            }
        }

        pathAudioFormat.setText(
                String.format(
                        Locale.ROOT, getString(R.string.save_path), FileUtil.getAudioFormatStorageDirectory(this)));
    }


    private void convertAllAudio() {
        if (mAudioList != null && !mAudioList.isEmpty()) {
            String filePath = mAudioList.get(0);
            int start = filePath.lastIndexOf("/");
            int end = filePath.lastIndexOf(".");
            String name = filePath.substring(start, end);
            String outPutPath = FileUtil.getAudioFormatStorageDirectory(getBaseContext()) + name + "." + transferFormat;

            transformAudio(filePath,outPutPath);

        }
    }

    private final void transformAudio(String srcFile,String outPutPath) {
        int start = srcFile.lastIndexOf("/");
        int end = srcFile.lastIndexOf(".");
        String name = srcFile.substring(start, end);
        HAEAudioExpansion.getInstance()
                .transformAudio(
                        getBaseContext(),
                        srcFile,
                        outPutPath,
                        new OnTransformCallBack() {
                            @Override
                            public void onProgress(int progress) {
                                isTansforming = true;
                                progressBar.setProgress(progress);
                            }

                            @Override
                            public void onFail(int errorCode) {
                                isTansforming = false;
                                if (errorCode == HAEErrorCode.FAIL_FILE_EXIST) {
                                    Toast.makeText(
                                            AudioFormatActivity.this,
                                            "file exists",
                                            Toast.LENGTH_LONG)
                                            .show();
                                    source.setVisibility(View.VISIBLE);
                                    source.setText(outPutPath);
                                    playSource.setVisibility(View.VISIBLE );
                                    isTansforming = false;
                                    Toast.makeText(getBaseContext(), "Success: " + outPutPath, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AudioFormatActivity.this, "ErrorCode : " + errorCode, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                            @Override
                            public void onSuccess(String outPutPath) {
                                source.setVisibility(View.VISIBLE);
                                source.setText(outPutPath);
                                playSource.setVisibility(View.VISIBLE );
                                isTansforming = false;
                                Toast.makeText(getBaseContext(), "Success: " + outPutPath, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                isTansforming = false;
                                Toast.makeText(getBaseContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
    }


    protected void initEvent() {

        radioGroupAudioFormat.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        source.setVisibility(View.GONE);
                        playSource.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        if (checkedId == R.id.radio_button_1_fragment_audio_format) {
                            transferFormat = SampleConstant.AUDIO_TYPE_MP3;
                        } else if (checkedId == R.id.radio_button_2_fragment_audio_format) {
                            transferFormat = SampleConstant.AUDIO_TYPE_WAV;
                        } else if (checkedId == R.id.radio_button_3_fragment_audio_format) {
                            transferFormat = SampleConstant.AUDIO_TYPE_FLAC;
                        }
                    }
                });

        transferAudioFormat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isTansforming) {
                            Toast.makeText(getBaseContext(), "There is currently a format conversion task in progress", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (transferFormat.isEmpty()) {
                            Toast.makeText(
                                            getBaseContext(),
                                           "Empty",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            convertAllAudio();
                        }
                    }
                });

        playSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(source.getText().toString())), "audio/*");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            finish();
            return;
        }
        if (resultCode == SampleConstant.RESULT_CODE && requestCode == SELECT_AUDIOS_REQUEST_CODE && data != null) {
            if (data.hasExtra(Constant.EXTRA_SELECT_RESULT)) {
                ArrayList<AudioInfo> list =
                        (ArrayList<AudioInfo>) data.getSerializableExtra(SampleConstant.EXTRA_SELECT_RESULT);
                if (list != null && !list.isEmpty()) {
                    for (AudioInfo audioInfo : list) {
                        mAudioList.add(audioInfo.getAudioPath());
                    }
                    audioName.setText(list.get(0).getAudioName());
                }
            }
            if (data.hasExtra(HAEConstant.AUDIO_PATH_LIST)) {
                mAudioList = (ArrayList<String>) data.getSerializableExtra(HAEConstant.AUDIO_PATH_LIST);
                if (mAudioList != null && !mAudioList.isEmpty()) {
                    File file = new File(mAudioList.get(0));
                    audioName.setText(file.getName());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTansforming) {
            HAEAudioExpansion.getInstance().cancelTransformAudio();
        }
    }
}
