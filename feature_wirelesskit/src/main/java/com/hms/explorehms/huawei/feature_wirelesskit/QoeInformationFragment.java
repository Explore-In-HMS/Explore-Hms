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

package com.hms.explorehms.huawei.feature_wirelesskit;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class QoeInformationFragment extends Fragment {

    TextView qoeInfoTextView;
    TextView titleTextView1;
    ImageView supportImageView;
    ImageView innerImageView;
    ConstraintLayout qoeInfoInnerConstraintLayout;
    boolean isOpened = false;

    public QoeInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_qoe_information, container, false);
        qoeInfoTextView = view1.findViewById(R.id.qoeInfoTextView);
        supportImageView = view1.findViewById(R.id.supportImageView);
        innerImageView = view1.findViewById(R.id.innerImageView);
        titleTextView1 = view1.findViewById(R.id.titleTextView1);
        titleTextView1.setText(getString(R.string.network_qoe_long));
        qoeInfoInnerConstraintLayout = view1.findViewById(R.id.qoeInfoInnerConstraintLayout);
        qoeInfoInnerConstraintLayout.setVisibility(View.GONE);

        supportImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenImageView();
            }
        });

        innerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCloseImageView();
            }
        });

        qoeInfoTextView.setText(R.string.long_general_explanation);
        return view1;
    }

    public void toOpenImageView(){
        isOpened = true;
        qoeInfoInnerConstraintLayout.setVisibility(View.VISIBLE);
        supportImageView.setVisibility(View.GONE);
        qoeInfoTextView.setVisibility(View.GONE);
    }

    public void toCloseImageView(){
        isOpened = false;
        qoeInfoInnerConstraintLayout.setVisibility(View.GONE);
        supportImageView.setVisibility(View.VISIBLE);
        qoeInfoTextView.setVisibility(View.VISIBLE);
    }
}