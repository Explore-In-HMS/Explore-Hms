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

package com.hms.explorehms.baseapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hms.explorehms.R;
import com.hms.explorehms.baseapp.adapter.KitCategoryAdapter;
import com.hms.explorehms.baseapp.model.KitCategoryModel;
import com.hms.explorehms.baseapp.model.KitModel;

import java.util.ArrayList;

public class HmsCoreFragment extends Fragment {
    private View view;

    KitCategoryAdapter kitCategoryAdapter;
    ArrayList<KitCategoryModel> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hms_core, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        categoryList = new ArrayList<>();
        categoryList.add(
                new KitCategoryModel("App Services", KitModel.getKitsByCategory(KitModel.HmsCategory.AppServices)));
        categoryList.add(
                new KitCategoryModel("Graphics", KitModel.getKitsByCategory(KitModel.HmsCategory.Graphics)));
        categoryList.add(
                new KitCategoryModel("Media", KitModel.getKitsByCategory(KitModel.HmsCategory.Media)));
        categoryList.add(
                new KitCategoryModel("AI", KitModel.getKitsByCategory(KitModel.HmsCategory.AI)));
        categoryList.add(
                new KitCategoryModel("Smart Device", KitModel.getKitsByCategory(KitModel.HmsCategory.SmartDevice)));
        categoryList.add(
                new KitCategoryModel("Security", KitModel.getKitsByCategory(KitModel.HmsCategory.Security)));
        categoryList.add(
                new KitCategoryModel("System", KitModel.getKitsByCategory(KitModel.HmsCategory.System)));

        RecyclerView rv_kitCategory = view.findViewById(R.id.rv_kitCategory_hmsCore);
        rv_kitCategory.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_kitCategory.setLayoutManager(layoutManager);
        kitCategoryAdapter = new KitCategoryAdapter(getActivity(), categoryList);
        rv_kitCategory.setAdapter(kitCategoryAdapter);
    }


}
