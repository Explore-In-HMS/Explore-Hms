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

package com.hms.explorehms.mapkit.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;
import com.hms.explorehms.mapkit.model.response.Step;
import com.hms.explorehms.mapkit.myEnums.DirectionActionType;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsHolder>{
    @NotNull
    private final Context context;
    private final List steps;

    @NotNull
    public DirectionsHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.direction_view_holder, parent, false);
        return new DirectionsHolder(view);
    }

    public int getItemCount() {
        return this.steps.size();
    }

    @RequiresApi(21)
    @SuppressLint({"UseCompatLoadingForDrawables"})
    public void onBindViewHolder(@NotNull DirectionsHolder holder, int position) {
        Step step = (Step)this.steps.get(position);
        if (step != null) {
            holder.getActionDescription().setText(step.getInstruction());
            holder.getActionDistanceTime().setText(context.getString(R.string.distance_time,step.getDistanceText(),step.getDurationText()));
            if(step.getAction().equals(DirectionActionType.STRAIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_go, null));
            }
            else if(step.getAction().equals(DirectionActionType.TURN_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_left, null));
            }

            else if(step.getAction().equals(DirectionActionType.TURN_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_right,null));
            }
            else if(step.getAction().equals(DirectionActionType.TURN_SLIGHT_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_left,null));
            }

            else if(step.getAction().equals(DirectionActionType.TURN_SLIGHT_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_right,null));
            }

            else if(step.getAction().equals(DirectionActionType.TURN_SHARP_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_left,null));
            }

            else if(step.getAction().equals(DirectionActionType.TURN_SHARP_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_right,null));
            }

            else if(step.getAction().equals(DirectionActionType.UTURN_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_return,null));
            }

            else if(step.getAction().equals(DirectionActionType.UTURN_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_return,null));
            }

            else if(step.getAction().equals(DirectionActionType.RAMP_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_left,null));
            }

            else if(step.getAction().equals( DirectionActionType.RAMP_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_right,null));
            }

            else if(step.getAction().equals( DirectionActionType.MERGE.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_go,null));
            }

            else if(step.getAction().equals( DirectionActionType.FORK_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_left,null));
            }

            else if(step.getAction().equals( DirectionActionType.FORK_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_right,null));
            }

            else if(step.getAction().equals( DirectionActionType.FERRY.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_go,null));
            }

            else if(step.getAction().equals( DirectionActionType.FERRY_TRAIN.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_go,null));
            }

            else if(step.getAction().equals( DirectionActionType.ROUNDABOUT_LEFT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_left,null));
            }

            else if(step.getAction().equals( DirectionActionType.ROUNDABOUT_RIGHT.getType())) {
                holder.getActionImage().setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_right,null));
            }
            else if(step.getAction().equals( DirectionActionType.END.getType())) {
                holder.getActionDescription().setText(step.getRoadName());
            }
            else {
                holder.getActionDescription().setText(step.getRoadName());
            }

            }
        else{
            holder.getActionDescription().setText("No suitable routes");
        }
        }



    @NotNull
    public final Context getContext() {
        return this.context;
    }

    public DirectionsAdapter(Context context, List steps) {
        this.context = context;
        this.steps = steps;
    }
}
