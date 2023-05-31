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

package com.hms.explorehms.huawei.feature_imagekit.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseTag;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ThemeTaggingResultTagsListAdapter extends ArrayAdapter<ThemeTaggingResponseTag> {

    private final LayoutInflater inflater;
    private final List<ThemeTaggingResponseTag> result;

    public ThemeTaggingResultTagsListAdapter(Context context, List<ThemeTaggingResponseTag> result) {
        super(context, 0, result);
        this.result = result;

        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public ThemeTaggingResponseTag getItem(int position) {
        return result.get(position);
    }

    @Override
    public long getItemId(int position) {
        return result.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_theme_tagging_tags_imagekit, null);

            holder = new ThemeTaggingResultTagsListAdapter.ViewHolder();
            holder.tvType = convertView.findViewById(R.id.tv_imagekit_theme_tagging_tags_type);
            holder.tvConfidence = convertView.findViewById(R.id.tv_imagekit_theme_tagging_tags_confidence);

            convertView.setTag(holder);
        } else {
            holder = (ThemeTaggingResultTagsListAdapter.ViewHolder) convertView.getTag();
        }

        ThemeTaggingResponseTag object = result.get(position);

        if (object != null) {

            holder.tvType.setText(object.tagName);
            holder.tvConfidence.setText(String.valueOf(object.tagConfidence));
        }

        return convertView;
    }

    private static class ViewHolder {
        MaterialTextView tvType;
        MaterialTextView tvConfidence;
        MaterialButton btnShow;

    }

}
