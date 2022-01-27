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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;

import com.genar.hmssandbox.huawei.feature_audioeditorkit.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class VideoAudioExtractionActivity extends AppCompatActivity {

    private MaterialButton playSource;
    private EditText extractedSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_audio_extraction);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toolbar toolBar = findViewById(R.id.toolbar_video_extraction);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
        Intent intent=getIntent();
        String str = intent.getStringExtra("audioPath");
        extractedSource.setText(str);
        initEvent();
    }

    public void initEvent(){
        playSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(extractedSource.getText().toString())), "audio/*");
                startActivity(intent);
            }
        });
    }


    public void initView(){
        playSource = findViewById(R.id.btn_open_source2);
        extractedSource = findViewById(R.id.et_extracted_source2);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}