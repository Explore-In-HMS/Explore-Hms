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

import android.util.Log;
import android.widget.CompoundButton;

import com.huawei.hms.avpipeline.api.MediaMeta;

public class PlayerActivitySRenabled extends PlayerActivity {
    private static final String TAG = "AVP-PlayerActivitySRenabled";

    @Override
    protected void initAllView() {
        super.initAllView();
        mSwitch.setChecked(true);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mPlayer == null) {
                    return;
                }
                if (isFastClick()) {
                    Log.w(TAG, "onCheckedChanged: click too fast, now button is "+b);
                    makeToastAndRecordLog(Log.INFO, "click button too fast");
                    mSwitch.setChecked(!b);
                    return;
                }
                Log.i(TAG, "switch SR ? " + b);
                MediaMeta meta = new MediaMeta();
                meta.setInt32(MediaMeta.MEDIA_ENABLE_CV, b ? 1 : 0);
                mPlayer.setParameter(meta);
            }
        });
    }

    @Override
    protected void setGraph() {
        MediaMeta meta = new MediaMeta();
        meta.setString(MediaMeta.MEDIA_GRAPH_PATH, getExternalFilesDir(null).getPath() + "/PlayerGraphCV.xml");
        meta.setInt32(MediaMeta.MEDIA_ENABLE_CV, 1);
        mPlayer.setParameter(meta);
    }
}
