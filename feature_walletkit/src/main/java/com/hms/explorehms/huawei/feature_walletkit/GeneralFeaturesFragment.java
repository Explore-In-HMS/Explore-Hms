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

package com.hms.explorehms.huawei.feature_walletkit;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD;


public class GeneralFeaturesFragment extends Fragment {

    public GeneralFeaturesFragment() {
        // Required empty public constructor
    }

    TextView explanationTextView;
    String explanation1;
    String explanation2;
    String explanation3;
    String link;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        explanation1 = getString(R.string.walletkit_explanation1);
        explanation2 = getString(R.string.walletkit_explanation2);
        explanation3 = getString(R.string.walletkit_explanation3);
        link = getString(R.string.references);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view1 = inflater.inflate(R.layout.fragment_general_features_wallet, container,false);

        explanationTextView = view1.findViewById(R.id.explanationTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            explanationTextView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        explanationTextView.setText(new StringBuilder(explanation1 + explanation2 + explanation3));

        return view1;
    }
}