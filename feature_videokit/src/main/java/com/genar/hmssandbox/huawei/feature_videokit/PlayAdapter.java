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
package com.genar.hmssandbox.huawei.feature_videokit;

import android.annotation.SuppressLint;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.feature_videokit.file_readers.VideoItemClass;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.PlayView> {

    List<VideoItemClass> myVideoList;
    private PlayAdapter.OnItemClickListener onItemClickListener;

    public PlayAdapter(List<VideoItemClass> myVideoList) {
        this.myVideoList = myVideoList;
    }

    public void setOnItemClickListener(PlayAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PlayView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_layout_item, parent, false);
        return new PlayView(layoutView);
    }

    @Override
    public void onBindViewHolder(final PlayView holder, final int position) {

        VideoItemClass videoItem = myVideoList.get(holder.getAdapterPosition());

        holder.titleOfVideoTextView.setText(videoItem.getTitle());
        holder.videoDescTextView.setText(videoItem.getDescription());

        if (URLUtil.isValidUrl(videoItem.getThumb()))
            Picasso.get().load(videoItem.getThumb()).into(holder.videoImageView);
        else
            holder.videoImageView.setImageResource(R.drawable.ic_default_video);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(myVideoList, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myVideoList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(List<VideoItemClass> myVideoList, int position);
    }

    static class PlayView extends RecyclerView.ViewHolder {
        TextView titleOfVideoTextView;
        TextView videoDescTextView;
        ImageView videoImageView;

        @SuppressLint("ClickableViewAccessibility")
        public PlayView(View itemView) {
            super(itemView);

            titleOfVideoTextView = itemView.findViewById(R.id.titleOfVideoTextView);
            videoDescTextView = itemView.findViewById(R.id.videoDescTextView);
            videoImageView = itemView.findViewById(R.id.videoImageView);

            videoDescTextView.setOnTouchListener((v, event) -> {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            });

            //Enabling scrolling on TextView.
            videoDescTextView.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}
