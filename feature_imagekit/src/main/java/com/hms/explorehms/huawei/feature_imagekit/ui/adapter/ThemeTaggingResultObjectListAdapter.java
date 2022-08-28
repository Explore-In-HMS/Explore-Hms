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

package com.hms.explorehms.huawei.feature_imagekit.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseObjectList;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseObjectListBox;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageVisionUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ThemeTaggingResultObjectListAdapter extends ArrayAdapter<ThemeTaggingResponseObjectList> {

    private final LayoutInflater inflater;
    private final List<ThemeTaggingResponseObjectList> result;
    private final ThemeTaggingResultObjectAdapterListener listener;


    public ThemeTaggingResultObjectListAdapter(Context context, List<ThemeTaggingResponseObjectList> result, ThemeTaggingResultObjectAdapterListener listener) {
        super(context, 0, result);
        this.result = result;
        this.listener = listener;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public ThemeTaggingResponseObjectList getItem(int position) {
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
            convertView = inflater.inflate(R.layout.list_item_theme_tagging_object_list_imagekit, null);

            holder = new ViewHolder();
            holder.tvType = convertView.findViewById(R.id.tv_imagekit_theme_tagging_object_list_type);
            holder.tvPossibility = convertView.findViewById(R.id.tv_imagekit_theme_tagging_object_list_possibility);
            holder.btnShow = convertView.findViewById(R.id.btn_imagekit_theme_tagging_object_list_box_show);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ThemeTaggingResponseObjectList object = result.get(position);

        if (object != null) {

            holder.tvType.setText(ImageVisionUtils.getTypeOfTheme(object.type));
            holder.tvPossibility.setText(String.valueOf(object.possibility == null ? "Uncertain" : object.possibility));
            holder.btnShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShow(object.box);
                }
            });
        }

        return convertView;
    }

    public interface ThemeTaggingResultObjectAdapterListener {
        void onShow(ThemeTaggingResponseObjectListBox box);
    }

    private static class ViewHolder {
        MaterialTextView tvType;
        MaterialTextView tvPossibility;
        MaterialButton btnShow;
    }
}
