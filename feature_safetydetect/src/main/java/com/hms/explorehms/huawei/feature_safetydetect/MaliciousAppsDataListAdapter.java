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
package com.hms.explorehms.huawei.feature_safetydetect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsData;
import com.huawei.hms.support.api.safetydetect.AppsCheckConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple BaseAdapter which is used to list malicious apps data through the ListView widget.
 *
 * @since 4.0.0.300
 */
public class MaliciousAppsDataListAdapter extends BaseAdapter {
    private final List<MaliciousAppsData> maliciousAppsData = new ArrayList<>();
    private final Context context;
    private final String emptyCheck;

    public MaliciousAppsDataListAdapter(List<MaliciousAppsData> data, String emptyCheck, Context context) {
        if (data.size() != 0) {
            maliciousAppsData.addAll(data);
        }
        this.context = context;
        this.emptyCheck = emptyCheck;
    }

    @Override
    public int getCount() {
        if (maliciousAppsData.size() != 0)
            return maliciousAppsData.size();
        else
            return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView = LayoutInflater.from(context).inflate(R.layout.item_list_app, parent, false);
        TextView txtAppPackageName = mView.findViewById(R.id.txt_aName);
        TextView txtAppCategory = mView.findViewById(R.id.txt_aCategory);

        if (!emptyCheck.equals("empty")) {
            txtAppCategory.setVisibility(View.VISIBLE);
            final MaliciousAppsData oneMaliciousAppsData = this.maliciousAppsData.get(position);
            txtAppPackageName.setText(oneMaliciousAppsData.getApkPackageName());
            txtAppCategory.setText(getCategory(oneMaliciousAppsData.getApkCategory()));
        } else {
            txtAppPackageName.setText(R.string.potential_malicious_apps_found);
            txtAppCategory.setVisibility(View.GONE);
        }
        return mView;
    }

    private String getCategory(int apkCategory) {
        if (apkCategory == AppsCheckConstants.VIRUS_LEVEL_RISK) {
            return context.getString(R.string.app_type_risk);
        } else if (apkCategory == AppsCheckConstants.VIRUS_LEVEL_VIRUS) {
            return context.getString(R.string.app_type_virus);
        }
        return context.getString(R.string.app_type_unknown);
    }
}
