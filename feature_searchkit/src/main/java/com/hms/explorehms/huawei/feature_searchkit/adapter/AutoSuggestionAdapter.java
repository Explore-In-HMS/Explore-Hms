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

package com.hms.explorehms.huawei.feature_searchkit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_searchkit.R;
import com.hms.explorehms.huawei.feature_searchkit.listeners.AutoSuggestClickListenerSearchKit;
import com.huawei.hms.searchkit.bean.SuggestObject;

import java.util.List;

public class AutoSuggestionAdapter extends RecyclerView.Adapter<AutoSuggestionAdapter.AutoSuggestionViewHolder> {


    private List<SuggestObject> data;
    private AutoSuggestClickListenerSearchKit autoSuggestClickListenerSearchKit;


    public AutoSuggestionAdapter(List<SuggestObject> data, AutoSuggestClickListenerSearchKit autoSuggestClickListenerSearchKit) {
        this.data = data;
        this.autoSuggestClickListenerSearchKit= autoSuggestClickListenerSearchKit;
    }

    public void updateData(List<SuggestObject> data) {
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
    public AutoSuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_auto_suggestion_searchkit, parent, false);
        return new AutoSuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AutoSuggestionViewHolder holder, int position) {
        holder.bind(data.get(position), autoSuggestClickListenerSearchKit);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AutoSuggestionViewHolder extends RecyclerView.ViewHolder {

        TextView tvSuggestionText;

        public AutoSuggestionViewHolder(View itemView) {
            super(itemView);
            this.tvSuggestionText = itemView.findViewById(R.id.tvAutoSuggestText_searchkit);
        }

        public void bind(SuggestObject data, AutoSuggestClickListenerSearchKit autoSuggestClickListenerSearchKit) {
            tvSuggestionText.setText(data.name);

            itemView.setOnClickListener(view -> autoSuggestClickListenerSearchKit.onItemClick(data.name));
        }

    }
}
