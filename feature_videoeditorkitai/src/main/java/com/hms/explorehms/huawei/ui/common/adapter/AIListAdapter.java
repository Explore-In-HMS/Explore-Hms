/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.hms.explorehms.huawei.ui.common.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.ui.common.bean.AIInfoData;
import com.hms.explorehms.huawei.ui.common.utils.ScreenUtil;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;

import java.util.ArrayList;
import java.util.List;

public class AIListAdapter extends RecyclerView.Adapter<AIListAdapter.ViewHolder> {

    private List<AIInfoData> infoDataList = new ArrayList<>();

    public AIListAdapter(List<AIInfoData> infoDataList) {
        this.infoDataList = infoDataList;
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AIInfoData infoData = infoDataList.get(position);

        if (infoData == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        int width = (ScreenUtil.getScreenWidth(holder.itemView.getContext()) - ScreenUtil.dp2px(44));
        int height = (width / 11 * 5) / 2;
        layoutParams.width = width;
        layoutParams.height = height;

        holder.ivLogo.setImageResource(infoData.getImgResId());
        if (!TextUtils.isEmpty(infoData.getTitle())) {
            holder.tvTitle.setText(infoData.getTitle());
        } else {
            holder.tvTitle.setText("");
        }
        if (!TextUtils.isEmpty(infoData.getSubTitle())) {
            holder.tvSubTitle.setText(infoData.getSubTitle());
        } else {
            holder.tvSubTitle.setText("");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(infoData, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoDataList == null ? 0 : infoDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;

        TextView tvTitle;

        TextView tvSubTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.iv_logo);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSubTitle = itemView.findViewById(R.id.tv_sub_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(AIInfoData infoData, int position);
    }
}
