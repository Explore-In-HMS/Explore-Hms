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

package com.genar.hmssandbox.huawei.reference.integrationcheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.genar.hmssandbox.huawei.R;

public class SubmitAndBrowseFragment extends Fragment {

    public SubmitAndBrowseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view2 = inflater.inflate(R.layout.fragment_submit_and_browse, container, false);

        TextView text3 = view2.findViewById(R.id.text3);
        TextView text4 = view2.findViewById(R.id.text4);
        TextView text5 = view2.findViewById(R.id.text5);

        text3.setText(R.string.text_3);
        text4.setText(R.string.text_4);
        text5.setText(R.string.text_5);

        return view2;
    }
}