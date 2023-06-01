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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.video_technology;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.video_technology.video_summarization.VideoSummarizationActivity;

public class VideoTechnologyMain extends Fragment {

    private View view;

    private CardView cvVideoSummarization;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_video_technology_hiai, container, false);

        initUI();
        initListener();

        return view;
    }

    private void initUI(){

        cvVideoSummarization = view.findViewById(R.id.cv_video_summarization);
    }

    private void initListener() {
        cvVideoSummarization.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), VideoSummarizationActivity.class);
            startActivity(intent);
        });
    }
}
