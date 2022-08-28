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

package com.hms.explorehms.huawei.feature_gameservice.fragments.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_gameservice.dao.ILoadItemClickListener;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ItemSaveGameservicesBinding;
import com.huawei.hms.jos.games.archive.ArchiveSummary;

import java.util.List;

public class LoadAdapterGameService extends RecyclerView.Adapter<LoadAdapterGameService.LoadAdapterViewHolder> {

    private List<ArchiveSummary> data;
    private ILoadItemClickListener listener;

    public LoadAdapterGameService(List<ArchiveSummary> data, ILoadItemClickListener listener) {
        this.data = data;
        this.listener = listener;

    }

    public void updateData(List<ArchiveSummary> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LoadAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LoadAdapterViewHolder(
                ItemSaveGameservicesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull LoadAdapterViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<ArchiveSummary> getList() {
        return this.data;
    }

    public class LoadAdapterViewHolder extends RecyclerView.ViewHolder {
        private ItemSaveGameservicesBinding binding;

        public LoadAdapterViewHolder(ItemSaveGameservicesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(ArchiveSummary archiveSummary, ILoadItemClickListener listener) {
            binding.tvSaveDescriptionGameserices.setText(
                    archiveSummary.getDescInfo()
            );
            binding.getRoot().setOnClickListener(view -> listener.onLoadItemClick(archiveSummary));
        }
    }
}
