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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.ItemAutocompleteSitekitBinding;
import com.huawei.hms.site.api.model.Site;

import java.util.List;

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.AutocompleteViewHolder> {


    private List<Site> data;
    private Context context;
    private ItemAutocompleteSitekitBinding binding;

    public AutocompleteAdapter(List<Site> data, Context context) {
        this.data = data;
        this.context = context;
    }


    public void updateData(List<Site> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (data != null && !data.isEmpty()) {
            data.clear();
            notifyDataSetChanged();
        }
    }


    @NonNull
    @Override
    public AutocompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemAutocompleteSitekitBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AutocompleteViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteViewHolder holder, int position) {
        Site site = data.get(position);
        binding.tvDetailName.setText(context.getResources().getString(R.string.address_item_name, site.getName()));
        binding.tvDetailCountry.setText(context.getResources().getString(R.string.address_item_country, site.getAddress().getCountry()));
        binding.tvDetailAdminArea.setText(context.getResources().getString(R.string.address_item_admin_area, site.getAddress().getAdminArea()));
        binding.tvDetailLocality.setText(context.getResources().getString(R.string.address_item_locality, site.getAddress().getLocality()));
        binding.tvDetailSubLocality.setText(context.getResources().getString(R.string.address_item_sublocality, site.getAddress().getSubLocality()));
        binding.tvDetailSubAdminArea.setText(context.getResources().getString(R.string.address_item__sub_admin_area, site.getAddress().getSubAdminArea()));
        binding.tvDetailThoroughfare.setText(context.getResources().getString(R.string.address_item__thoroughfare, site.getAddress().getThoroughfare()));
        binding.tvDetailStreetNumber.setText(context.getResources().getString(R.string.address_item__street_number, site.getAddress().getStreetNumber()));
        binding.tvDetailPostalCode.setText(context.getResources().getString(R.string.address_item__postal_code, site.getAddress().getPostalCode()));
        binding.tvDetailLat.setText(context.getResources().getString(R.string.address_item_latitude, site.getLocation().getLat()));
        binding.tvDetailLn.setText(context.getResources().getString(R.string.address_item_longitude, site.getLocation().getLng()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AutocompleteViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public AutocompleteViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

    }
}
