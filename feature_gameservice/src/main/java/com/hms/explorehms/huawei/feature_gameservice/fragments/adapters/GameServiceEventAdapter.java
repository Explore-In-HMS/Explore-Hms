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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_gameservice.R;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ItemEventsGameservicesBinding;
import com.huawei.hms.jos.games.event.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameServiceEventAdapter extends RecyclerView.Adapter<GameServiceEventAdapter.GameServiceEventsViewHolder> {


    private List<Event> itemList;
    private Context context;

    public GameServiceEventAdapter(List<Event> list, Context context) {
        this.itemList = list;
        this.context = context;
    }

    public void updateList(List<Event> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameServiceEventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GameServiceEventsViewHolder(ItemEventsGameservicesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GameServiceEventsViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class GameServiceEventsViewHolder extends RecyclerView.ViewHolder {
        private ItemEventsGameservicesBinding binding;

        public GameServiceEventsViewHolder(ItemEventsGameservicesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event) {
            Picasso.get().load(event.getThumbnailUri()).into(
                    binding.imgEventGameservices
            );

            binding.tvEventIdGameservices.setText(context.getResources().getString(R.string.event_id_gameservices, event.getEventId()));
            binding.tvEventNameGameservices.setText(context.getResources().getString(R.string.event_name_gameservices, event.getName()));
            binding.tvEventValueGameservices.setText(context.getResources().getString(R.string.event_value_gameservices, String.valueOf(event.getValue())));


        }
    }
}
