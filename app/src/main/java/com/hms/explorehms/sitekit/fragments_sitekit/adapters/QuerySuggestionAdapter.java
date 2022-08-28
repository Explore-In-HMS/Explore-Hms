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

package com.hms.explorehms.sitekit.fragments_sitekit.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;
import com.hms.explorehms.sitekit.model.AddressInfo;
import com.hms.explorehms.sitekit.common.SiteKitResultItemClickListener;
import com.huawei.hms.site.api.model.AddressDetail;

import java.util.List;

public class QuerySuggestionAdapter extends RecyclerView.Adapter<QuerySuggestionAdapter.QuerySuggestionViewHolder> {


    private List<AddressInfo> data;
    private final SiteKitResultItemClickListener listener;

    public QuerySuggestionAdapter(List<AddressInfo> data, SiteKitResultItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void updateData(List<AddressInfo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private boolean isAddressValid(AddressDetail addressDetail) {
        if (addressDetail.getSubLocality() != null && addressDetail.getSubAdminArea() != null && addressDetail.getAdminArea() != null)
            return true;
        else
            return false;
    }

    @NonNull
    @Override
    public QuerySuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_search_sitekit, parent, false);
        return new QuerySuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuerySuggestionViewHolder holder, int position) {

        holder.bind(data.get(position), position, listener);

    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class QuerySuggestionViewHolder extends RecyclerView.ViewHolder {

        TextView tvSuggestionName;
        TextView tvSuggestionAddress;


        public QuerySuggestionViewHolder(View itemView) {
            super(itemView);
            tvSuggestionName = itemView.findViewById(R.id.text_view_suggestion_item_name);
            tvSuggestionAddress = itemView.findViewById(R.id.text_view_suggestion_item_secondary);
        }

        @SuppressLint("SetTextI18n")
        public void bind(AddressInfo addressInfo, int itemPosition, SiteKitResultItemClickListener listener) {

            String name = addressInfo.getName() != null ?
                    addressInfo.getName() : "-";

            String subLocality = addressInfo.getAddressDetail().getSubLocality() != null ?
                    addressInfo.getAddressDetail().getSubLocality() : "-";

            String subAdminArea = addressInfo.getAddressDetail().getSubAdminArea() != null ?
                    addressInfo.getAddressDetail().getSubAdminArea() : "-";

            String adminArea = addressInfo.getAddressDetail().getAdminArea() != null ?
                    addressInfo.getAddressDetail().getAdminArea() : "-";


            tvSuggestionName.setText(name);
            tvSuggestionAddress.setText(subLocality + ", " +
                    subAdminArea + ", " +
                    adminArea
            );

            itemView.setOnClickListener(view -> {
                listener.onItemClick(addressInfo);
            });

        }

        //file.getFileName() != null ? file.getFileName() : "-"

    }
}
