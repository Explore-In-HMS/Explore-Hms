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

package com.hms.explorehms.huawei.feature_hiaifoundation.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_hiaifoundation.R;
import com.hms.explorehms.huawei.feature_hiaifoundation.models.ClassifyItemModel;

import java.util.List;

public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ClassifyViewHolder> {

    private final List<ClassifyItemModel> classifyItemModelList;

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 2;

    private View headView;

    public void setHeaderView(View headerView) {
        headView = headerView;
        notifyItemInserted(0);
    }

    public ClassifyAdapter(List<ClassifyItemModel> details) {
        this.classifyItemModelList = details;
    }

    @Override
    public int getItemViewType(int position) {
        if (headView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }

        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public ClassifyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (headView != null && viewType == TYPE_HEADER) {
            return new ClassifyViewHolder(headView);
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_classify, viewGroup, false);
        return new ClassifyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassifyViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_NORMAL) {
            holder.classifyTop1Result.setText(classifyItemModelList.get(position - 1).getTop1Result());
            holder.classifyOthersResult.setText(classifyItemModelList.get(position - 1).getOtherResults());
            holder.classifyTime.setText(classifyItemModelList.get(position - 1).getClassifyTime());
            holder.classifyImg.setImageBitmap(classifyItemModelList.get(position - 1).getClassifyImg());
        }
    }

    @Override
    public int getItemCount() {
        if (headView == null) {
            return classifyItemModelList.size();
        } else {
            return classifyItemModelList.size() + 1;
        }
    }

    class ClassifyViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView classifyTop1Result;
        TextView classifyOthersResult;
        TextView classifyTime;
        ImageView classifyImg;

        ClassifyViewHolder(View itemView) {
            super(itemView);

            if (itemView == headView) {
                return;
            }

            cv = itemView.findViewById(R.id.classify_item);
            classifyTop1Result = itemView.findViewById(R.id.tv_top1Result);
            classifyOthersResult = itemView.findViewById(R.id.tv_otherResults);
            classifyTime = itemView.findViewById(R.id.tv_inferenceTime);
            classifyImg = itemView.findViewById(R.id.imgClassify);
        }
    }
}
