/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

package com.hms.explorehms.huawei.feature_videoeditorkit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hms.explorehms.huawei.feature_videoeditorkit.R;
import com.hms.explorehms.huawei.feature_videoeditorkit.custom.RoundImage;
import com.huawei.hms.videoeditor.ui.api.DraftInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.WorksHolder> {
    private Context context;

    private List<DraftInfo> list;

    private SimpleDateFormat mSimpleDateFormat;

    private DraftInfo draftInfo;

    public DraftAdapter(Context context) {
        this.context = context;
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public void setData(List<DraftInfo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WorksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.adapter_draft_itme, parent, false);
        return new WorksHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull WorksHolder holder, @SuppressLint("RecyclerView") int position) {
        draftInfo = list.get(position);
        Glide.with(context).load(list.get(position).getDraftCoverPath()).into(holder.mWorkimage);
        holder.mTime.setText(mSimpleDateFormat.format(draftInfo.getDraftCreateTime()));
        holder.mWorkimage.setOnClickListener(v -> {
            selectedListener.onStyleSelected(position);
        });

        holder.mWorkimage.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                longSelectedListener.onStyleLongSelected(v, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class WorksHolder extends RecyclerView.ViewHolder {
        RoundImage mWorkimage;

        private final TextView mTime;

        public WorksHolder(@NonNull View itemView) {
            super(itemView);
            mWorkimage = itemView.findViewById(R.id.works_item_image);
            mTime = itemView.findViewById(R.id.time);
        }
    }

    OnStyleSelectedListener selectedListener;

    OnStyleLongSelectedListener longSelectedListener;

    public void setSelectedListener(OnStyleSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public void setLongSelectedListener(OnStyleLongSelectedListener longSelectedListener) {
        this.longSelectedListener = longSelectedListener;
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(int position);
    }

    public interface OnStyleLongSelectedListener {
        void onStyleLongSelected(View v, int position);
    }
}