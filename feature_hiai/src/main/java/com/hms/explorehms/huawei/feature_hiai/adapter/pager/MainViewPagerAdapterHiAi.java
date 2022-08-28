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
package com.hms.explorehms.huawei.feature_hiai.adapter.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.body_recognition.BodyRecognitionMain;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.code_recognition.CodeRecognitionMain;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.*;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.ImageRecognitionMain;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.TextRecognitionMain;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.video_technology.VideoTechnologyMain;

public class MainViewPagerAdapterHiAi extends FragmentStateAdapter {
    private static final int FRAGMENT_NUM = 6;

    public MainViewPagerAdapterHiAi(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    public Fragment createFragment(int position) {
        Fragment fragmentsHiAi = null;

        if(position == 0)
            fragmentsHiAi = new FacialRecognitionMain();
        else if(position == 1)
            fragmentsHiAi = new BodyRecognitionMain();
        else if(position == 2)
            fragmentsHiAi = new ImageRecognitionMain();
        else if(position == 3)
            fragmentsHiAi = new CodeRecognitionMain();
        else if(position == 4)
            fragmentsHiAi = new VideoTechnologyMain();
        else if(position == 5)
            fragmentsHiAi = new TextRecognitionMain();

        return fragmentsHiAi;
    }

    public int getItemCount() {
        return FRAGMENT_NUM;
    }
}
