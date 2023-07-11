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

package com.hms.explorehms.baseapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;
import com.hms.explorehms.baseapp.library.SpacesItemDecoration;
import com.hms.explorehms.baseapp.model.KitCategoryModel;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class KitCategoryAdapter extends RecyclerView.Adapter<KitCategoryAdapter.CategoryViewHolder> {

    private final Activity activity;
    private final List<KitCategoryModel> list;


    public KitCategoryAdapter(Activity activity, List<KitCategoryModel> list){
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.rv_kit_category_item,parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        KitCategoryModel category = list.get(position);

        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryTitle;
        private final ImageView ivArrow;
        private final RecyclerView rvKitRecyclerView;
        private final ConstraintLayout clTitleContainer;

        private KitAdapter kitAdapter;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.lbl_category);
            ivArrow = itemView.findViewById(R.id.iv_title_arrow);
            rvKitRecyclerView = itemView.findViewById(R.id.rv_kits);
            rvKitRecyclerView.setVisibility(View.GONE);
            rvKitRecyclerView.setNestedScrollingEnabled(false);
            clTitleContainer = itemView.findViewById(R.id.container_title);

        }

        public void bind(KitCategoryModel category) {
            clTitleContainer.setOnClickListener(v -> {
//                ivArrow.animate().rotationBy(180).setDuration(300).start();
                if (rvKitRecyclerView.getVisibility() == View.VISIBLE) {
//                    animateArrowCollapse(ivArrow);
                    ivArrow.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(),R.drawable.ic_expand_arrow,null));
                    collapse(rvKitRecyclerView);
                    animateArrowCollapse(ivArrow);
                } else {
//                    animateArrowExpand(ivArrow);
                    ivArrow.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(),R.drawable.ic_collapse_arrow,null));
                    expand3(rvKitRecyclerView);
                    animateArrowExpand(ivArrow);
                }
            });
            categoryTitle.setText(category.getCategoryName());

            if(kitAdapter == null){
                int columnCount = 3;
                rvKitRecyclerView.addItemDecoration(new SpacesItemDecoration(16, columnCount));
            }

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activity, 3);
            rvKitRecyclerView.setLayoutManager(layoutManager);
            kitAdapter = new KitAdapter(activity, category.getKitList());
            rvKitRecyclerView.setAdapter(kitAdapter);
        }

        private void expand3(final RecyclerView v){
            int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.AT_MOST);
            int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
            final int targetHeight = v.getMeasuredHeight();

            v.getLayoutParams().height =1 ;

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT
                            : (int) (targetHeight * interpolatedTime);
                    v.scheduleLayoutAnimation();
                    v.requestLayout();

                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    v.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(a);
        }

        private void collapse(final View v) {
            final int initialHeight = v.getMeasuredHeight();
            Animation a = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if(interpolatedTime == 1){
                        rvKitRecyclerView.setVisibility(View.GONE);
                    }else{
                        v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
        }

        private void animateArrowExpand(ImageView image) {
            RotateAnimation rotate =
                   new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setFillAfter(true);
           image.setAnimation(rotate);
           image.startAnimation(rotate);
        }

        private void animateArrowCollapse(ImageView image) {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setFillAfter(true);
            image.setAnimation(rotate);
            image.startAnimation(rotate);
        }
    }
}
