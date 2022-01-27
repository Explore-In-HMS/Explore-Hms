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

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.genar.hmssandbox.huawei.feature_avpipelinekit.R;
import com.huawei.hms.avpipeline.api.MediaMeta;
import com.huawei.hms.avpipeline.api.MediaParcel;
import com.huawei.hms.avpipeline.sdk.MediaPlayer;

public class PlayerActivitySound extends PlayerActivity {
    private static final String TAG = "AVP-PlayerActivitySound";
    private TextView mEventView;
    private TextView soundtext;
    @Override
    protected void initAllView() {
        super.initAllView();
        soundtext=findViewById(R.id.tv_info_sound);
        soundtext.setVisibility(View.VISIBLE);
        mSwitch.setVisibility(View.GONE);
        mEventView = findViewById(R.id.soundEvent);
    }

    @Override
    protected int getPlayerType() {
        return MediaPlayer.PLAYER_TYPE_AUDIO;
    }

    @Override
    protected void setGraph() {
        MediaMeta meta = new MediaMeta();
        meta.setString(MediaMeta.MEDIA_GRAPH_PATH, getExternalFilesDir(null).getPath() + "/AudioPlayerGraphSD.xml");
        mPlayer.setParameter(meta);
    }

    @Override
    protected void setListener() {
        mPlayer.setOnMsgInfoListener(new MediaPlayer.OnMsgInfoListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void OnMsgInfo(MediaPlayer mp, int param1, final int param2, MediaParcel parcel) {
                if (param1 != MediaPlayer.EVENT_INFO_SOUND_SED) return;
                Log.i(TAG, "got sound event:" + param2);
                if (param2 >= 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mEventView.setText("Detected Sound : "+MediaPlayer.SoundEvent.values()[param2].name());

                        }
                    });

                }
                else{
                    mEventView.setText("No Sound Detected");
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventView.setText("");
    }
}
