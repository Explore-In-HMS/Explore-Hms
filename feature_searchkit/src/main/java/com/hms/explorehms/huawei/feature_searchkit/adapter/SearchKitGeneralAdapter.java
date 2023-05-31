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

package com.hms.explorehms.huawei.feature_searchkit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class SearchKitGeneralAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<T> data;

    protected SearchKitGeneralAdapter(List<T> data) {
        this.data = data;
    }

    public abstract int getLayoutResId();

    public abstract void onBindData(T model, int position, View itemView);

    public void updateData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (data != null && !data.isEmpty()) {
            data.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBindData(data.get(position), position, holder.itemView);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutResId(), parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }
}
