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

package com.hms.explorehms.huawei.feature_drivekit.model;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_drivekit.R;
import com.hms.explorehms.baseapp.library.ProgressDialogScreen;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.cloud.services.drive.model.HistoryVersion;

import java.text.DecimalFormat;
import java.util.List;

public class HistoryVersionAdapter extends RecyclerView.Adapter<HistoryVersionAdapter.HistoryVersionViewHolder> {

    private static final String TAG = "HistoryVersionAdapter";


    Context context;
    List<HistoryVersion> list;
    DriveHelper dHelper;

    public HistoryVersionAdapter(Context context, List<HistoryVersion> list, DriveHelper dHelper) {
        this.context = context;
        this.list = list;
        this.dHelper = dHelper;
    }

    @NonNull
    @Override
    public HistoryVersionAdapter.HistoryVersionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryVersionAdapter.HistoryVersionViewHolder holder, int position) {
        //onBindViewHolder
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HistoryVersionViewHolder extends RecyclerView.ViewHolder {

        HistoryVersion version;

        MaterialTextView tvVersionSize;
        MaterialTextView tvEditedTime;
        MaterialTextView tvLastEditor;

        MaterialButton btnVersionDelete;

        public HistoryVersionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private double roundTwoDecimals(double d) {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.parseDouble(twoDForm.format(d));
        }


        private String dateTimeToString(Long timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
            return dateFormat.format(timestamp);
        }


        public void bind(final HistoryVersion version) {
            this.version = version;

            tvVersionSize = itemView.findViewById(R.id.tv_version_filesize);
            tvEditedTime = itemView.findViewById(R.id.tv_editedtime);
            tvLastEditor = itemView.findViewById(R.id.tv_lastEditor);
            btnVersionDelete = itemView.findViewById(R.id.btn_versiondelete);

            double versionSize = (double) version.getSize() / (double) 1024;

            tvVersionSize.setText(String.format("%s KB", roundTwoDecimals(versionSize)));
            tvEditedTime.setText(dateTimeToString(version.getEditedTime().getValue()));
            tvLastEditor.setText(version.getLastEditor().getDisplayName());

            btnVersionDelete.setOnClickListener(v -> {
                final ProgressDialogScreen progressDialog = new ProgressDialogScreen(context);
                progressDialog.showProgressDialog();

                Future<Boolean> result = Async.submit(() -> dHelper.deleteHistoryVersion(DriveHelper.getSelectedFile().getId(), version.getId()));

                result.addSuccessCallback(result1 -> {
                    if (result1) {
                        list.remove(version);
                        notifyDataSetChanged();
                        Toast.makeText(context, "History version is deleted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "History version could not be deleted!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismissProgressDialog();
                });

                result.addFailureCallback(t -> {
                    progressDialog.dismissProgressDialog();
                    Log.d(TAG, "History version delete error on AsyncTask", t);
                    Toast.makeText(context, "Error occurred while deleting the version!", Toast.LENGTH_LONG).show();
                });
            });
        }
    }
}
