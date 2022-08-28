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

package com.hms.explorehms.huawei.feature_cameraengine.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.dualviewvideo.DualViewReferenceFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.hdr.HDRModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.night.NightModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.photo.PhotoModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.portrait.PortraitModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.prophoto.ProPhotoModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.provideo.ProVideoModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.recording.RecordingModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.supslowmo.SuperSlowMotionModeFragment;
import com.hms.explorehms.huawei.feature_cameraengine.ui.mode.ultrawideangle.UltraWideAngleModeFragment;

public class MainViewPagerAdapterCameraEngine extends FragmentStateAdapter {

    public MainViewPagerAdapterCameraEngine(FragmentManager fragmentManager, Lifecycle lifecycle){
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Fragment fragmentsCameraEngine = null;

        if(position == 0){
            fragmentsCameraEngine = new PhotoModeFragment();
        }else if(position == 1){
            fragmentsCameraEngine = new HDRModeFragment();
        }else if(position == 2){
            fragmentsCameraEngine = new NightModeFragment();
        }else if(position == 3){
            fragmentsCameraEngine = new UltraWideAngleModeFragment();
        }else if(position == 4){
            fragmentsCameraEngine = new PortraitModeFragment();
        }else if (position == 5){
            fragmentsCameraEngine = new SuperSlowMotionModeFragment();
        }else if(position == 6){
            fragmentsCameraEngine = new ProPhotoModeFragment();
        }else if(position == 7){
            fragmentsCameraEngine = new ProVideoModeFragment();
        }else if(position == 8){
            fragmentsCameraEngine = new RecordingModeFragment();
        }
        else if(position==9){
            fragmentsCameraEngine=new DualViewReferenceFragment();
        }
        /*else if(position == -1){
            fragmentsCameraEngine = new SlowMotionModeFragment(); //maintenance
        }else if(position == -1){
            fragmentsCameraEngine = new DualViewVideoFragment(); //maintenance
        }*/

        return fragmentsCameraEngine;
    }


    @Override
    public int getItemCount() {
        return 10;
    }
}
