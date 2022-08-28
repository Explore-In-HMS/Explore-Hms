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

package com.hms.explorehms.pushkit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hms.explorehms.R;

import java.util.Map;

/**
 * Adapter of listview that for data variable of Remote Message object
 */
public class NotificationDataAdapter extends BaseAdapter {

    private final Context context;
    private final Map<String,String> data;
    private final String[] mKeys;


    /**
     * @param context context
     * @param data data variable of received Remote Message object
     */
    public NotificationDataAdapter(Context context, Map<String,String> data){
        this.context = context;
        this.data = data;
        mKeys = data.keySet().toArray(new String[0]);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String key = mKeys[position];
        String val = getItem(position).toString();

        LayoutInflater inflater = LayoutInflater.from(this.context);

        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item_msg_data_push_kit,null);

            holder = new ViewHolder();

            holder.tvData = convertView.findViewById(R.id.tv_msg_data_data_push_kit);
            holder.tvValue = convertView.findViewById(R.id.tv_msg_data_value_push_kit);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvData.setText(key);
        holder.tvValue.setText(val);

        return convertView;
    }

    private static class ViewHolder {
        TextView tvData;
        TextView tvValue;
    }
}
