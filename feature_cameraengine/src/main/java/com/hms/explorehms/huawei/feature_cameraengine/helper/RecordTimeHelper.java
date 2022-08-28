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

package com.hms.explorehms.huawei.feature_cameraengine.helper;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hms.explorehms.huawei.feature_cameraengine.R;
import com.google.android.material.textview.MaterialTextView;

public class RecordTimeHelper {

    private MaterialTextView timerTextView;
    private Activity activity;
    private Handler timer;
    private int time = 0;
    private int state = 0;

    public RecordTimeHelper(Activity activity, MaterialTextView textView){
        this.timerTextView = textView;
        this.activity = activity;

        timer = new Handler(Looper.getMainLooper());
    }

    public void startTimer(){
        state = 0;
        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(state == 0){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ++time;
                            int min = time / 60;
                            int sec = time - min * 60;
                            Log.i("CameraEngine","min : "+min+", sec :"+sec);
                            timerTextView.setText(String.format(activity.getResources().getString(R.string.txt_record_timer_camera_engine),min,sec));
                        }
                    });
                    timer.postDelayed(this,1000);
                }else if(state == 1){
                    timer.postDelayed(this,1000);
                }else if(state == -1){
                    time = 0;
                    state = 0;
                }
            }
        },1000);
    }

    public void pauseTimer(){ state = 1; }

    public void resumeTimer(){ state = 0; }

    public int stopTimer(){ state = -1; return time; }
}
