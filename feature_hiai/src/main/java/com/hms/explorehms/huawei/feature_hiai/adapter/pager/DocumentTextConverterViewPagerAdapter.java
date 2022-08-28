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

import android.graphics.Bitmap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter.fragment.DocumentTextConverterCoordinatesFragment;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter.fragment.DocumentTextConverterRefineFragment;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter.fragment.DocumentTextConverterSuperResolutionFragment;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter.fragment.DocumentTextConverterTextFragment;
import com.huawei.hiai.vision.visionkit.image.detector.DocCoordinates;
import com.huawei.hiai.vision.visionkit.text.Text;


public class DocumentTextConverterViewPagerAdapter extends FragmentStateAdapter {
    private static final int FRAGMENT_NUM = 4;

    private DocCoordinates coordinates;
    private Bitmap refineBitmap;
    private Bitmap superResolutionBitmap;
    private Text text;
    private Bitmap orgImage;

    public DocumentTextConverterViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle,
                                                 DocCoordinates coordinates,
                                                 Bitmap refineBitmap,
                                                 Bitmap superResolutionBitmap,
                                                 Text text,
                                                 Bitmap orgImage) {
        super(fragmentManager,lifecycle);

        this.coordinates = coordinates;
        this.refineBitmap = refineBitmap;
        this.superResolutionBitmap = superResolutionBitmap;
        this.text = text;
        this.orgImage = orgImage;
    }

    public Fragment createFragment(int position) {
        Fragment fragmentsHiAi = null;

        switch (position) {
            case 0:
                fragmentsHiAi = new DocumentTextConverterCoordinatesFragment(this.coordinates, this.orgImage);
                break;
            case 1:
                fragmentsHiAi = new DocumentTextConverterRefineFragment(this.refineBitmap);
                break;
            case 2:
                fragmentsHiAi = new DocumentTextConverterTextFragment(this.text, this.orgImage);
                break;
            case 3:
                fragmentsHiAi = new DocumentTextConverterSuperResolutionFragment(this.superResolutionBitmap);
                break;
        }

        return fragmentsHiAi;
    }

    public int getItemCount() {
        return FRAGMENT_NUM;
    }
}
