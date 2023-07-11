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

package com.hms.explorehms.sitekit.fragments_sitekit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;
import com.hms.explorehms.sitekit.model.AddressInfo;
import com.hms.explorehms.sitekit.common.SiteKitResultItemClickListener;


import java.util.List;

public class SiteKitGeneralAdapter extends RecyclerView.Adapter<SiteKitGeneralAdapter.SiteKitGeneralViewHolder> {

    private List<AddressInfo> data;
    private final SiteKitResultItemClickListener listener;

    public SiteKitGeneralAdapter(List<AddressInfo> data, SiteKitResultItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void updateData(List<AddressInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SiteKitGeneralViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_detail_sitekit, parent, false);
        return new SiteKitGeneralViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SiteKitGeneralViewHolder holder, int position) {
        holder.bind(data.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SiteKitGeneralViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress1;
        TextView tvAddressName;
        TextView tvAddressCountry;
        TextView tvAddressAdminArea;

        SiteKitGeneralViewHolder(View itemView) {
            super(itemView);
            tvAddress1 = itemView.findViewById(R.id.tv_address_1);
            tvAddressName = itemView.findViewById(R.id.tv_address_name);
            tvAddressCountry = itemView.findViewById(R.id.tv_address_country);
            tvAddressAdminArea = itemView.findViewById(R.id.tv_address_admin_area);
        }

        public void bind(AddressInfo addressInfo, int itemPosition, SiteKitResultItemClickListener listener) {
            tvAddress1.setText(itemView.getContext().getString(R.string.address_item_title, (itemPosition + 1)));
            tvAddressName.setText(itemView.getContext().getString(R.string.address_item_name, addressInfo.getName()));
            tvAddressCountry.setText(itemView.getContext().getString(R.string.address_item_country, addressInfo.getAddressDetail().getCountry()));
            tvAddressAdminArea.setText(itemView.getContext().getString(R.string.address_item_admin_area, addressInfo.getAddressDetail().getAdminArea()));

            itemView.setOnClickListener(view -> listener.onItemClick(addressInfo));
        }
    }
}
