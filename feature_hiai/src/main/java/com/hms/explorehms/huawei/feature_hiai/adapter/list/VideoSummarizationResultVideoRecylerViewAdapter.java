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
package com.hms.explorehms.huawei.feature_hiai.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_hiai.R;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hiai.vision.visionkit.image.detector.aestheticsmodel.AestheticVideoSummerization;

import java.util.List;

public class VideoSummarizationResultVideoRecylerViewAdapter extends RecyclerView.Adapter<VideoSummarizationResultVideoRecylerViewAdapter.ViewHolder> {

    private final Context context;
    private final List<AestheticVideoSummerization> videos;

    public VideoSummarizationResultVideoRecylerViewAdapter(Context context, List<AestheticVideoSummerization> listItems) {
        this.context = context;
        this.videos = listItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_video_summarization_result_hiai, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.video = videos.get(position);

        holder.videoView.setVideoPath(holder.video.getSrcVideo());
        holder.videoView.seekTo(Long.valueOf(holder.video.getStartFrameTimeStamp() / 1000).intValue());//microsecond to millisecond conversion

        holder.textPart.setText(String.format(context.getResources().getString(R.string.txt_video_summarization_summary_video_part_x_hiai), position + 1));
        holder.tvStartEndFrame.setText(String.format(
                context.getResources().getString(R.string.txt_video_summarization_summary_video_start_end_time_hiai),
                holder.video.getStartFrameTimeStamp() / 1000000,
                holder.video.getEndFrameTimeStamp() / 1000000));

        holder.seekBarCurrentPos.setEnabled(false);


        holder.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.videoView.start();

                holder.videoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int currentPos = holder.videoView.getCurrentPosition();

                        long sizeOfPart = holder.video.getEndFrameTimeStamp() - holder.video.getStartFrameTimeStamp();

                        holder.seekBarCurrentPos.post(new Runnable() {
                            @Override
                            public void run() {
                                holder.seekBarCurrentPos.setProgress(100 * currentPos / Math.toIntExact(sizeOfPart / 1000));
                            }
                        });

                        if (holder.video.getEndFrameTimeStamp() / 1000 <= currentPos) {
                            holder.videoView.stopPlayback();
                        } else {
                            holder.videoView.postDelayed(this, 500);
                        }
                    }
                }, 0);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;
        public Button buttonStart;
        public MaterialTextView textPart;
        public SeekBar seekBarCurrentPos;
        public MaterialTextView tvStartEndFrame;
        public AestheticVideoSummerization video;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.vw_video_summarization_video);
            buttonStart = itemView.findViewById(R.id.btn_video_summarization_run_video);
            textPart = itemView.findViewById(R.id.tv_video_summarization_part_hiai);
            seekBarCurrentPos = itemView.findViewById(R.id.seekbar_video_sum_current_pos_hiai);
            tvStartEndFrame = itemView.findViewById(R.id.tv_video_summarization_part_start_end_hiai);
        }
    }
}
