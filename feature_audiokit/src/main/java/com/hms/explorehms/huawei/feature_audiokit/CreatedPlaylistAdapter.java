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
package com.hms.explorehms.huawei.feature_audiokit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CreatedPlaylistAdapter extends RecyclerView.Adapter<CreatedPlaylistAdapter.CreatedPlayListView> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private CreatedPlaylistAdapter.OnItemClickListener onItemClickListener;
    List<LocalAudioItem> localAudioList;
    List<Integer> myCreatedIndexList;

    public CreatedPlaylistAdapter(List<LocalAudioItem> localAudioList, List<Integer> myCreatedIndexList){
        this.localAudioList = localAudioList;
        this.myCreatedIndexList = myCreatedIndexList;
    }

    public void setOnItemClickListener(CreatedPlaylistAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class CreatedPlayListView extends RecyclerView.ViewHolder {
        TextView nameOfPlaylistTextView;
        TextView songCountTextView;

        public CreatedPlayListView(View itemView) {
            super(itemView);

            nameOfPlaylistTextView = itemView.findViewById(R.id.nameOfPlaylistTextView);
            songCountTextView = itemView.findViewById(R.id.songCountTextView);
        }
    }

    @NonNull
    @Override
    public CreatedPlayListView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.created_playlist_layout, parent, false);
        return new CreatedPlayListView(layoutView);
    }

    @Override
    public void onBindViewHolder(final CreatedPlayListView holder, final int position) {

        final LocalAudioItem localAudioItem = localAudioList.get(holder.getAdapterPosition());
        holder.nameOfPlaylistTextView.setText(localAudioItem.getNameOfPlaylist());

        String songAmount = localAudioItem.getIndexList().size() + " Songs Present";
        holder.songCountTextView.setText(songAmount);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return localAudioList.size();
    }
}
