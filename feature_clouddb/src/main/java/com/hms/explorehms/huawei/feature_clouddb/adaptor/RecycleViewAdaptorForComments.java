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
package com.hms.explorehms.huawei.feature_clouddb.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_clouddb.R;
import com.hms.explorehms.huawei.feature_clouddb.model.BookComment;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdaptorForComments extends RecyclerView.Adapter<RecycleViewAdaptorForComments.ViewHolder>{

    private List<BookComment> bookComments;
    private OnCommentItemClickListener itemSelectListener;

    private ArrayList<CardView>allCardView = new ArrayList<>();

    public RecycleViewAdaptorForComments(List<BookComment>bookComments,OnCommentItemClickListener itemClickListener){
        this.bookComments = bookComments;
        itemSelectListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVwComment ;
        TextView txtVwCommentDate;
        TextView txtVwPersonName;
        TextView txtVwBookName;
        TextView txtPrintingHouse;
        CardView cardVwRow;
        ImageView selectedImg;

        private View viewHolderForListener;

        public ViewHolder(View view){
            super(view);
            txtVwComment = view.findViewById(R.id.txtVwRecyclComment);
            txtVwCommentDate = view.findViewById(R.id.txtVwRcycleDate);
            txtVwPersonName = view.findViewById(R.id.txtRecyRowPersonName);
            txtVwBookName = view.findViewById(R.id.txtVwBookName);
            txtPrintingHouse = view.findViewById(R.id.txtVwPrintingHouse);
            cardVwRow = view.findViewById(R.id.recyclerCardRow);
            selectedImg = view.findViewById(R.id.imgVwSelectedItem);

            viewHolderForListener = view;
        }

        public void initialize(final BookComment item, final OnCommentItemClickListener action){
            viewHolderForListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for(int i=0;i<allCardView.size();i++){
                        if(allCardView.get(i).getId() == getAdapterPosition()){
                            allCardView.get(i).findViewById(R.id.imgVwSelectedItem).setVisibility(View.VISIBLE);
                        }else{
                            allCardView.get(i).findViewById(R.id.imgVwSelectedItem).setVisibility(View.INVISIBLE);
                        }
                    }
                    action.onItemClick(item,getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_recycle_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtVwComment.setText(bookComments.get(position).getComment());
        holder.txtVwBookName.setText(bookComments.get(position).getBookName());
        holder.txtVwCommentDate.setText(bookComments.get(position).getCommentDate());
        holder.txtVwPersonName.setText(bookComments.get(position).getPersonFullname());
        holder.txtPrintingHouse.setText(bookComments.get(position).getPrintingHouse());

        holder.cardVwRow.setId(position);
        allCardView.add(holder.cardVwRow);

        holder.initialize(bookComments.get(position),itemSelectListener);
    }

    @Override
    public int getItemCount() {

        return this.bookComments.size();
    }

}

