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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_hiai.R;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hiai.vision.visionkit.text.table.TableCell;

import java.util.List;

public class TableRecognitionElementsAdapter extends RecyclerView.Adapter<TableRecognitionElementsAdapter.ViewHolder> {

    private final List<TableCell> cells;

    public TableRecognitionElementsAdapter(List<TableCell> cells) {
        this.cells = cells;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_table_recognition_table_cell_hiai, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TableCell cell = cells.get(position);

        StringBuilder builder = new StringBuilder();

        for (String word : cell.getWord()) {
            builder.append(word);
            builder.append(" ");
        }

        holder.tvCount.setText(String.valueOf(position + 1));
        holder.word.setText(builder.toString());
        holder.startColumn.setText(String.valueOf(cell.getStartColumn()));
        holder.startRow.setText(String.valueOf(cell.getStartRow()));
        holder.endColumn.setText(String.valueOf(cell.getEndColumn()));
        holder.endRow.setText(String.valueOf(cell.getEndRow()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MaterialTextView word;
        public MaterialTextView startColumn;
        public MaterialTextView startRow;
        public MaterialTextView endColumn;
        public MaterialTextView endRow;
        public MaterialTextView tvCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCount = itemView.findViewById(R.id.tv_table_recognition_cell_count);
            word = itemView.findViewById(R.id.tv_table_recognition_table_cell_word);
            startColumn = itemView.findViewById(R.id.tv_table_recognition_table_start_col);
            startRow = itemView.findViewById(R.id.tv_table_recognition_table_start_row);
            endColumn = itemView.findViewById(R.id.tv_table_recognition_table_end_col);
            endRow = itemView.findViewById(R.id.tv_table_recognition_table_end_row);
        }
    }

}
