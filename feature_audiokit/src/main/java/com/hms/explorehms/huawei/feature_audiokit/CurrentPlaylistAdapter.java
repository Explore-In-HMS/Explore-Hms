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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hms.api.bean.HwAudioPlayItem;
import com.huawei.hms.audiokit.player.manager.HwAudioQueueManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CurrentPlaylistAdapter extends RecyclerView.Adapter<CurrentPlaylistAdapter.PlayListView> {

    public interface OnItemClickListener {
        void onItemClick(List<HwAudioPlayItem> myCreatedIndexList, int position);
    }

    private CurrentPlaylistAdapter.OnItemClickListener onItemClickListener;
    List<HwAudioPlayItem> myPlayList;
    boolean isLocalAudioActivity;
    List<Integer> myCreatedIndexList = new ArrayList<>();
    Context context;
    HwAudioQueueManager hwAudioQueueManager;
    int[] isCheckedControlArray;

    public CurrentPlaylistAdapter(Context context, List<HwAudioPlayItem> myPlayList, boolean isLocalAudioActivity){
        this.myPlayList = myPlayList;
        this.isLocalAudioActivity = isLocalAudioActivity;
        this.context = context;
        isCheckedControlArray = new int[myPlayList.size()];
    }

    public CurrentPlaylistAdapter(Context context, HwAudioQueueManager hwAudioQueueManager, List<HwAudioPlayItem> myPlayList, boolean isLocalAudioActivity){
        this.myPlayList = myPlayList;
        this.isLocalAudioActivity = isLocalAudioActivity;
        this.context = context;
        this.hwAudioQueueManager = hwAudioQueueManager;
    }

    public void setOnItemClickListener(CurrentPlaylistAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    static class PlayListView extends RecyclerView.ViewHolder {
        TextView songNameTextView;
        TextView songArtistTextView;
        TextView durationTextView;
        CheckBox checkBox;
        ImageView optionsImageView;

        public PlayListView(View itemView) {
            super(itemView);

            songNameTextView = itemView.findViewById(R.id.songTitleTextView);
            songArtistTextView = itemView.findViewById(R.id.songArtistTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
            optionsImageView = itemView.findViewById(R.id.optionsImageView);
        }
    }

    @NonNull
    @Override
    public PlayListView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_detail_layout, parent, false);
        return new PlayListView(layoutView);
    }

    public List<Integer> getMyCreatedIndexList(){
        return myCreatedIndexList;
    }

    @Override
    public void onBindViewHolder(final PlayListView holder, final int position) {
        final HwAudioPlayItem currentItem = myPlayList.get(holder.getAdapterPosition());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.songNameTextView.setSelected(true);

        if(isLocalAudioActivity){
            holder.checkBox.setVisibility(View.VISIBLE);

            holder.checkBox.setChecked(isCheckedControlArray[holder.getAdapterPosition()] == 1);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isCheckedByUser) {

                    if(isCheckedByUser && currentItem != null){
                        myCreatedIndexList.add(holder.getAdapterPosition());
                        isCheckedControlArray[holder.getAdapterPosition()] = 1;
                    }
                    else if(!isCheckedByUser && currentItem != null){
                        myCreatedIndexList.remove(Integer.valueOf(holder.getAdapterPosition()));
                        isCheckedControlArray[holder.getAdapterPosition()] = 0;
                    }
                }
            });
        }
        else{
            holder.optionsImageView.setVisibility(View.VISIBLE);

            holder.optionsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, holder.optionsImageView);

                    popup.inflate(R.menu.options_menu_for_adapter);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.delete_from_list) {
                                if(hwAudioQueueManager != null){
                                    hwAudioQueueManager.removeListByItem(currentItem);
                                }
                                myPlayList.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());

                                if(myPlayList.size() == 0){
                                    Toast.makeText(context, "You removed the playlist completely", Toast.LENGTH_SHORT).show();
                                }

                                return true;
                            }
                            throw new IllegalStateException("Unexpected value: " + item.getItemId());
                        }
                    });

                    popup.show();
                }
            });
        }

        holder.songNameTextView.setText(currentItem.getAudioTitle());
        holder.songArtistTextView.setText(currentItem.getSinger());

        long durationOfSong = currentItem.getDuration();
        String totalDurationText = String.format(Locale.US, "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(durationOfSong),
                TimeUnit.MILLISECONDS.toSeconds(durationOfSong) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationOfSong))
        );

        holder.durationTextView.setText(totalDurationText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(myPlayList, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myPlayList.size();
    }
}