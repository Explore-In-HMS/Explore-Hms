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

package com.hms.explorehms.baseapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;
import com.hms.explorehms.baseapp.adapter.KitCategoryAdapter;
import com.hms.explorehms.baseapp.model.KitCategoryModel;
import com.hms.explorehms.baseapp.model.KitModel;

import java.util.ArrayList;

public class AppGalleryConnectFragment extends Fragment {
    private View view;

    KitCategoryAdapter kitCategoryAdapter;
    ArrayList<KitCategoryModel> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_gallery_connect, container, false);

        initUI();

        return view;
    }

    private void initUI() {

        categoryList = new ArrayList<>();
        categoryList.add(
                new KitCategoryModel("Release", KitModel.getKitsByCategory(KitModel.HmsCategory.Release)));
        categoryList.add(
                new KitCategoryModel("Build", KitModel.getKitsByCategory(KitModel.HmsCategory.Build)));
        categoryList.add(
                new KitCategoryModel("Growing", KitModel.getKitsByCategory(KitModel.HmsCategory.Growing)));
        categoryList.add(
                new KitCategoryModel("Quality", KitModel.getKitsByCategory(KitModel.HmsCategory.Quality)));
        categoryList.add(
                new KitCategoryModel("Earn", KitModel.getKitsByCategory(KitModel.HmsCategory.Earn)));
        categoryList.add(
                new KitCategoryModel("Analytics", KitModel.getKitsByCategory(KitModel.HmsCategory.Analytics)));

        RecyclerView rv_kitCategory = view.findViewById(R.id.rv_kitCategory_appGallery);
        rv_kitCategory.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_kitCategory.setLayoutManager(layoutManager);
        kitCategoryAdapter = new KitCategoryAdapter(getActivity(), categoryList);
        rv_kitCategory.setAdapter(kitCategoryAdapter);
    }
}