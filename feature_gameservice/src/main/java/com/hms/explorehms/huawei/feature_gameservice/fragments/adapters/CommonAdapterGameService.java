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

package com.hms.explorehms.huawei.feature_gameservice.fragments.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import java.util.List;

public abstract class CommonAdapterGameService<T, B extends ViewBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    protected B view;
    private List<T> data;

    protected CommonAdapterGameService(List<T> data) {
        this.data = data;
    }

    public abstract B inflateView(@NonNull ViewGroup parent, int viewType);

    public abstract void onBindData(T data, int position);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindData(data.get(position), position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflateView(parent, viewType);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(B itemView) {
            super(itemView.getRoot());
        }
    }
}
