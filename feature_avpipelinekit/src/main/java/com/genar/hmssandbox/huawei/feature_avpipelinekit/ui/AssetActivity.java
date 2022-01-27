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

package com.genar.hmssandbox.huawei.feature_avpipelinekit.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.feature_avpipelinekit.R;
import com.huawei.hms.avpipeline.api.MediaMeta;
import com.huawei.hms.avpipeline.sdk.AVPLoader;
import com.huawei.hms.avpipeline.sdk.Asset;
import com.huawei.hms.avpipeline.sdk.Asset.Track;
import com.huawei.hms.avpipeline.sdk.Asset.TrackGroup;

public class AssetActivity extends AppCompatActivity {
    private static final String TAG = "AVP-AssetActivity";
    private static final int MSG_CREATE = 1;
    private static final int MSG_START = 2;
    private static final int MSG_STOP = 3;
    private Asset mAsset;
    private String mFilePath = null;
    private TextView mTextView;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CREATE: {
                    initFwk();
                    break;
                }
                case MSG_START: {
                    parseMedia();
                    break;
                }
                case MSG_STOP: {
                    if (mAsset != null) {
                        mAsset.release();
                        mAsset = null;
                    }
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    private void initFwk() {
        if (AVPLoader.isInit()) {
            Log.d(TAG, "avp framework already inited");
            return;
        }
        boolean ret = AVPLoader.initFwk(getApplicationContext());
        if (ret) {
            makeToastAndRecordLog(Log.INFO, "avp framework load succ");
        } else {
            makeToastAndRecordLog(Log.ERROR, "avp framework load failed");
        }
    }

    private void parseMedia() {
        if (mFilePath == null) {
            return;
        }
        Log.i(TAG, "start to parse media file " + mFilePath);
        mAsset = Asset.create();
        if (mAsset == null) {
            makeToastAndRecordLog(Log.ERROR, "create Asset failed");
            return;
        }
        int ret = mAsset.setUrl(mFilePath);
        if (ret != 0) {
            makeToastAndRecordLog(Log.ERROR, "setUrl failed");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("file: " + mFilePath + "\n\n");
        sb.append("duration(us): " + mAsset.getDuration() + "\n");
        sb.append("bitRate(bps): " + mAsset.getBitRate() + "\n");
        sb.append("frameRate: " + mAsset.getFrameRate().toString() + "\n");
        sb.append("width: " + mAsset.getWidth() + "\n");
        sb.append("height: " + mAsset.getHeight() + "\n");
        sb.append("audioChannelCnt: " + mAsset.getAudioChannelCnt() + "\n");
        sb.append("audioSampleRate: " + mAsset.getAudioSampleRate() + "\n");
        MediaMeta meta = mAsset.getMetaData();
        if (meta != null) {
            sb.append("meta:\n" + meta.toString() + "\n");
        }
        StringBuilder sbTracks = new StringBuilder();
        for (TrackGroup oneGroup : mAsset.getAllTrackGroup()) {
            if (oneGroup == null) continue;
            for (Track oneTrack : oneGroup.getAllTracks()) {
                if (oneTrack == null) continue;
                sbTracks.append(oneTrack.toString());
            }
        }
        sb.append(sbTracks);
        mTextView.setText(sb.toString());
    }

    void makeToastAndRecordLog(int priority, String msg) {
        Log.println(priority, TAG, msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);
        mHandler.sendEmptyMessage(MSG_CREATE);
        Button selectBtn = findViewById(R.id.selectFileBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "user is choosing file");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivityForResult(Intent.createChooser(intent, "\n" +
                            "Please select file"), 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(AssetActivity.this, "Please install a file manager", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mTextView = findViewById(R.id.textViewInfo);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.sendEmptyMessage(MSG_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.sendEmptyMessage(MSG_STOP);
        mTextView.setText("");
    }

    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1 || resultCode != RESULT_OK) {
            makeToastAndRecordLog(Log.ERROR, "startActivityForResult failed");
            return;
        }
        Uri fileuri = data.getData();
        if (!DocumentsContract.isDocumentUri(this, fileuri)) {
            makeToastAndRecordLog(Log.ERROR, "this uri is not Document Uri");
            return;
        }
        String uriAuthority = fileuri.getAuthority();
        if (!uriAuthority.equals("com.android.externalstorage.documents")) {
            makeToastAndRecordLog(Log.ERROR, "this uri is:" + uriAuthority + ", but we need external storage document");
            return;
        }
        String docId = DocumentsContract.getDocumentId(fileuri);
        String[] split = docId.split(":");
        if (!split[0].equals("primary")) {
            makeToastAndRecordLog(Log.ERROR, "this document id is:" + docId + ", but we need primary:*");
            return;
        }
        mFilePath = Environment.getExternalStorageDirectory() + "/" + split[1];
    }
}
