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

package com.genar.hmssandbox.huawei.feature_cloudstorage.fragments.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.feature_cloudstorage.R;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.IFileClickListener;
import com.genar.hmssandbox.huawei.feature_cloudstorage.databinding.ItemListCloudstorageBinding;
import com.genar.hmssandbox.huawei.feature_cloudstorage.model.CloudStorageFile;
import com.huawei.agconnect.cloud.storage.core.StorageReference;

import java.util.List;
import java.util.Map;

public class CloudStorageListAdapter extends RecyclerView.Adapter<CloudStorageListAdapter.CloudStorageListViewHolder> {

    private List<CloudStorageFile> data;
    private final Context context;
    private final IFileClickListener listener;


    public CloudStorageListAdapter(List<CloudStorageFile> data, Context context, IFileClickListener listener) {
        this.data = data;
        this.context = context;
        this.listener = listener;

    }

    public void updateData(List<CloudStorageFile> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CloudStorageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CloudStorageListViewHolder(ItemListCloudstorageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CloudStorageListViewHolder holder, int position) {

        holder.bind(data.get(position), listener);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class CloudStorageListViewHolder extends RecyclerView.ViewHolder {
        private ItemListCloudstorageBinding binding;

        public CloudStorageListViewHolder(ItemListCloudstorageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CloudStorageFile file, IFileClickListener listener) {

            StorageReference reference = file.getStorageReference();

            binding.tvFileName.setText(
                    context.getResources().getString(R.string.file_name_cloud_storage, reference.getName())
            );

            reference.getFileMetadata().addOnSuccessListener(fileMetadata -> {
                StringBuilder builder = new StringBuilder();
                builder.append("ContentType: " + fileMetadata.getContentType() + "\n" +
                        "CacheControl: " + fileMetadata.getCacheControl() + "\n" +
                        "ContentDisposition: " + fileMetadata.getContentDisposition() + "\n" +
                        "ContentEncoding: " + fileMetadata.getContentEncoding() + "\n" +
                        "ContentLanguage: " + fileMetadata.getContentLanguage()
                );

                if (!fileMetadata.getCustomMetadata().isEmpty()) {
                    Map customMetadata = fileMetadata.getCustomMetadata();

                    customMetadata.forEach((k, v) ->
                            builder.append("\n" + k + ": " + v));
                }
                binding.tvFileMetadataInfo.setText(context.getString(R.string.metadata_cloud_storage, builder.toString()));

            }).addOnFailureListener(e ->
                    binding.tvFileMetadataInfo.setText(
                            context.getResources().getString(R.string.metadata_cloud_storage,
                                    "There is no custom file metadata.")
                    ));

            binding.rootCloudstorage.setOnClickListener(view ->
                    listener.onFileClick(file));
        }
    }
}
