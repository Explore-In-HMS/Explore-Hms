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

package com.hms.explorehms.huawei.feature_cloudstorage.fragments.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hms.explorehms.huawei.feature_cloudstorage.R;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.List;

public class DownloadedFilesAdapter extends BaseAdapter {

    private List<File> mImageList;
    private Context mContext;

    public DownloadedFilesAdapter(List<File> files, Context mContext) {
        this.mImageList = files;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_file_item, null);
            holder.iv = convertView.findViewById(R.id.iv_photo_cloudstorage);
            holder.tv = convertView.findViewById(R.id.tvFileName);

            holder.tv.setText(mImageList.get(position).getName());
            String endsWith = mImageList.get(position).getName().substring(mImageList.get(position).getName().lastIndexOf(".") + 1);
            switch (endsWith) {
                case "pdf":
                case "doc":
                case "docx":
                case "txt":
                case "xlsx":
                case "xls":
                    convertView.setTag(holder);
                    Glide.with(mContext).load(R.drawable.file_default).into(holder.iv);
                    break;
                default:
                    convertView.setTag(holder);
                    Glide.with(mContext).load(mImageList.get(position)).into(holder.iv);
                    break;
            }
        } else {
            if (convertView.getTag() instanceof ViewHolder) {
                holder = (ViewHolder) convertView.getTag();
                Glide.with(mContext).load(mImageList.get(position)).into(holder.iv);
            }
        }
        return convertView;
    }

    public void updateData(List<File> files) {
        this.mImageList = files;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView iv;
        MaterialTextView tv;
    }

}
