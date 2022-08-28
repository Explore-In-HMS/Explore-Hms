/*
 * Copyright 2020. Explore in HMS. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.hms.explorehms.mapkit.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;

import org.jetbrains.annotations.NotNull;

public final class DirectionsHolder extends RecyclerView.ViewHolder {
    @NotNull
    private final ImageView actionImage;
    @NotNull
    private final TextView actionDescription;
    @NotNull
    private final TextView actionDistanceTime;

    @NotNull
    public final ImageView getActionImage() {
        return this.actionImage;
    }

    @NotNull
    public final TextView getActionDescription() {
        return this.actionDescription;
    }

    @NotNull
    public final TextView getActionDistanceTime() {
        return this.actionDistanceTime;
    }

    public DirectionsHolder(@NotNull View view) {
        super(view);
        actionImage = view.findViewById(R.id.actionImage);
        actionDescription = view.findViewById(R.id.actionDescription);
        actionDistanceTime = view.findViewById(R.id.actionDistanceTime);
    }
}
