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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.utils.hiai_service_utils.ImageRecognitionUtils;
import com.huawei.hiai.vision.visionkit.image.detector.LabelContent;

import java.util.List;

public class ImageCategoryLabelingLabelContentsAdapter extends ArrayAdapter<LabelContent> {

    public static final String TAG = "HiAiService";

    private final LayoutInflater inflater;

    private final Context context;
    private final List<LabelContent> contents;

    public ImageCategoryLabelingLabelContentsAdapter(Context context, List<LabelContent> contents){
        super(context,0,contents);

        this.context = context;
        this.contents = contents;

        inflater = LayoutInflater.from(this.context);
    }


    @Override
    public int getCount() {
        return contents.size();
    }

    @Nullable
    @Override
    public LabelContent getItem(int position) {
        return contents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contents.get(position).hashCode();
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_image_category_labeling_label_contents_hiai,null);

            holder = new ViewHolder();
            holder.tvLabelId = convertView.findViewById(R.id.tv_image_category_label_content_label_id);
            holder.tvLabelProbability = convertView.findViewById(R.id.tv_image_category_label_content_label_probability);
            holder.tvLabelIdOrder = convertView.findViewById(R.id.tv_image_category_label_content_id);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        LabelContent content = contents.get(position);

        if(content != null){

            int labelID = content.getLabelId();
            String contentName = ImageRecognitionUtils.IMAGE_CATEGORY_LABELING_CONTENTS.get(labelID);

            holder.tvLabelId.setText(contentName);
            holder.tvLabelProbability.setText(String.valueOf(content.getProbability()));
            holder.tvLabelIdOrder.setText(String.format(context.getResources().getString(R.string.txt_image_category_labeling_label_content_hiai),position + 1));
        }

        Log.i(TAG,String.valueOf(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvLabelId;
        TextView tvLabelProbability;
        TextView tvLabelIdOrder;
    }
}
