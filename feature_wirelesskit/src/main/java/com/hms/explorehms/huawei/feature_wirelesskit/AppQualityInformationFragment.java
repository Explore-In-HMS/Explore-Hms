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

package com.hms.explorehms.huawei.feature_wirelesskit;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AppQualityInformationFragment extends Fragment {

    TextView appQualityInfoTextView;
    TextView titleTextView3;
    ImageView appQualityImageView;
    ImageView innerImageView2;
    ConstraintLayout qoeInfoInnerConstraintLayout2;
    boolean isOpened = false;

    public AppQualityInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view3 = inflater.inflate(R.layout.fragment_app_quality_information, container, false);
        appQualityInfoTextView = view3.findViewById(R.id.appQualityInfoTextView);
        appQualityInfoTextView.setText(R.string.app_quality_information);
        titleTextView3 = view3.findViewById(R.id.titleTextView3);
        titleTextView3.setText(getString(R.string.report_app_quality));
        appQualityImageView = view3.findViewById(R.id.appQualityImageView);
        innerImageView2 = view3.findViewById(R.id.innerImageView2);
        qoeInfoInnerConstraintLayout2 = view3.findViewById(R.id.qoeInfoInnerConstraintLayout2);
        qoeInfoInnerConstraintLayout2.setVisibility(View.GONE);

        appQualityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenImageView();
            }
        });

        innerImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCloseImageView();
            }
        });

        return view3;
    }

    public void toOpenImageView(){
        isOpened = true;
        qoeInfoInnerConstraintLayout2.setVisibility(View.VISIBLE);
        appQualityImageView.setVisibility(View.GONE);
        appQualityInfoTextView.setVisibility(View.GONE);
    }

    public void toCloseImageView(){
        isOpened = false;
        qoeInfoInnerConstraintLayout2.setVisibility(View.GONE);
        appQualityImageView.setVisibility(View.VISIBLE);
        appQualityInfoTextView.setVisibility(View.VISIBLE);
    }
}