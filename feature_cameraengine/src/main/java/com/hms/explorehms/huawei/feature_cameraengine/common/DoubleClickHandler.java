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

package com.hms.explorehms.huawei.feature_cameraengine.common;

import android.view.View;

public class DoubleClickHandler implements View.OnClickListener{

    private boolean isRunning= false;
    private int resetInTime = 500;
    private int counter=0;

    private DoubleTapCallback listener;

    public DoubleClickHandler(DoubleTapCallback callback)
    {
        listener = callback;
    }

    @Override
    public void onClick(View v) {

        if(isRunning)
        {
            if(counter == 1)
                listener.onDoubleClick(v);
        }
        counter++;

        if(!isRunning)
        {
            isRunning=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(resetInTime);
                        isRunning = false;
                        counter=0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public interface DoubleTapCallback {
        void onDoubleClick(View v);
    }
}


