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

package com.hms.explorehms.huawei.feature_connectapi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hms.explorehms.huawei.feature_connectapi.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentsApiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsApiFragment extends Fragment {



    public CommentsApiFragment() {
        // Required empty public constructor
    }


    public static CommentsApiFragment newInstance() {

        return  new CommentsApiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments_api, container, false);
    }

   /* public static QueryResponse queryDevReviews(String appId, String content, Long beginTime, Long endTime,
                                                String countries, String ratings, String apkVersions, String auditStates,
                                                String devReplyStates, String langs, Integer sort, Integer page, Integer limit) {
        return
    }*/
}