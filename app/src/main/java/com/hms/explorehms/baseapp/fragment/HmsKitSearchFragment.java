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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hms.explorehms.R;
import com.hms.explorehms.baseapp.adapter.KitAdapter;
import com.hms.explorehms.baseapp.model.KitModel;
import com.hms.explorehms.baseapp.library.SpacesItemDecoration;

import java.util.ArrayList;

public class HmsKitSearchFragment extends Fragment {

    View view;
    RecyclerView rv_kitSearch;
    KitAdapter kitAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hms_kit_search, container, false);

        InitUI();

        return view;
    }

    private void InitUI(){
        rv_kitSearch = view.findViewById(R.id.rv_kitSearch);

        if(kitAdapter == null){
            int columnCount = 3;
            rv_kitSearch.addItemDecoration(new SpacesItemDecoration(16, columnCount));
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rv_kitSearch.setLayoutManager(layoutManager);
        kitAdapter = new KitAdapter(getActivity(), new ArrayList<>());
        rv_kitSearch.setAdapter(kitAdapter);
    }

    public void FilterKitList(String searchText){
        ArrayList<KitModel> resultList = new ArrayList<>();

        for (KitModel kitModel : KitModel.KitList) {
            if (kitModel.getKitName().toLowerCase().contains(searchText)) {
                resultList.add(kitModel);
            }
        }
        kitAdapter.updateList(resultList);
    }
}