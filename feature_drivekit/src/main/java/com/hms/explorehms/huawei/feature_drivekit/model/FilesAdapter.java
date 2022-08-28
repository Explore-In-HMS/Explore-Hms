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

package com.hms.explorehms.huawei.feature_drivekit.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_drivekit.FileDetailActivity;
import com.hms.explorehms.huawei.feature_drivekit.R;
import com.huawei.cloud.services.drive.model.File;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    private static final String TAG = "FilesAdapter";

    Context context;
    List<File> fileList;

    public FilesAdapter(Context context, List<File> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_files, parent, false);

        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {

        File file = fileList.get(position);

        holder.bind(file);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void updateFileAdapter(List<File> list) {
        fileList = list;
        notifyDataSetChanged();
    }

    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.cv_file)
        CardView cvContainer;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.file_image)
        ImageView fileImage;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_filename)
        TextView tvFilename;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.tv_creationdate)
        TextView tvCreationDate;

        FileViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

        void bind(final File file) {

            String ext = file.getFileName().substring(file.getFileName().lastIndexOf(".") + 1);
            int placeholder = R.drawable.icon_drive_image;
            switch (ext) {
                case "doc":
                case "docx":
                case "xls":
                case "xlsx":
                case "pdf":
                    placeholder = R.drawable.icon_drive_file;
                    break;
                case "png":
                case "jpeg":
                case "jpg":
                case "icon":
                case "gif":
                    placeholder = R.drawable.icon_drive_image;
                    break;
                default:
                    Log.i(TAG,"Default Case");
            }


            Picasso.get()
                    .load(file.getIconDownloadLink())
                    .networkPolicy(NetworkPolicy.NO_STORE)
                    .placeholder(placeholder)
                    .into(fileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            //this method will be invoke when it is succeed.
                        }

                        @Override
                        public void onError(Exception e) {
                            //this method will be invoke when an error occurred.
                        }
                    });

            tvFilename.setText(file.getFileName());
            tvCreationDate.setText(dateTimeToString(file.getCreatedTime().getValue()));

            cvContainer.setOnClickListener(v -> {
                Intent intent = new Intent(context, FileDetailActivity.class);
                DriveHelper.setSelectedFile(file);
                context.startActivity(intent);
            });
        }

        String dateTimeToString(Long timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
            return dateFormat.format(timestamp);
        }
    }
}
