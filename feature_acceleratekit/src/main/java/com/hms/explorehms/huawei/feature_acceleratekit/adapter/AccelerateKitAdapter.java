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

package com.hms.explorehms.huawei.feature_acceleratekit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_acceleratekit.R;
import com.hms.explorehms.huawei.feature_acceleratekit.model.ListItemAccelerateKit;

import java.util.List;

public class AccelerateKitAdapter extends RecyclerView.Adapter<AccelerateKitAdapter.AccelerateKitViewHolder> {


    private List<ListItemAccelerateKit> data;
    private Context context;

    public AccelerateKitAdapter(List<ListItemAccelerateKit> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void updateData(List<ListItemAccelerateKit> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccelerateKitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_acceleratekit_list, parent, false);
        return new AccelerateKitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccelerateKitViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AccelerateKitViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvThreadCount;
        private final TextView tvTime;

        public AccelerateKitViewHolder(View itemView) {
            super(itemView);
            tvThreadCount = itemView.findViewById(R.id.tvThreadCount);
            tvTime = itemView.findViewById(R.id.tvTimeAccelerateKit);
        }

        private void bind(ListItemAccelerateKit item) {
            tvThreadCount.setText(context.getString(R.string.accelerate_kit_thread_count, item.getThreadCount()));
            tvTime.setText(context.getString(R.string.accelerate_kit_time, item.getTime()));
        }
    }
}
