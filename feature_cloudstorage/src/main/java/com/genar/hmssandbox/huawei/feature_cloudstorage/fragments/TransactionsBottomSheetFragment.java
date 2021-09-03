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

package com.genar.hmssandbox.huawei.feature_cloudstorage.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genar.hmssandbox.huawei.feature_cloudstorage.R;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.IUpdateListTrigger;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.ProgressDialogCloudStorage;
import com.genar.hmssandbox.huawei.feature_cloudstorage.databinding.ItemBottomsheetTransactionsCloudstorageBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.DownloadTask;
import com.huawei.agconnect.cloud.storage.core.StorageException;
import com.huawei.agconnect.cloud.storage.core.StorageReference;

import java.io.File;

public class TransactionsBottomSheetFragment extends BottomSheetDialogFragment {

    private ItemBottomsheetTransactionsCloudstorageBinding binding;
    private static final String TAG = "TransactionsFragmentCloudStorage";

    ProgressDialogCloudStorage progressDialogCloudStorage;
    private AGCStorageManagement storageManagement;
    private final String folderName;
    private final String fileName;
    private final IUpdateListTrigger trigger;

    public TransactionsBottomSheetFragment(String folderName, String fileName, IUpdateListTrigger trigger) {
        this.folderName = folderName;
        this.fileName = fileName;
        this.trigger = trigger;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ItemBottomsheetTransactionsCloudstorageBinding.inflate(inflater, container, false);
        // get the views and attach the listener
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageManagement = AGCStorageManagement.getInstance();

        progressDialogCloudStorage = new ProgressDialogCloudStorage(requireContext());
        initUI();
    }

    private void initUI() {
        binding.tvDownloadCloudstorage.setOnClickListener(view -> downloadFile());
        binding.tvDeleteCloudstorage.setOnClickListener(view -> deleteFile());
    }

    private void deleteFile() {
        if (fileName.equals("init.txt")) {
            Toast.makeText(requireContext(), "Can't delete init file.", Toast.LENGTH_SHORT).show();
        } else {
            StorageReference reference = storageManagement.getStorageReference(
                    folderName + getString(R.string.special_character) + fileName
            );
            reference.delete().addOnSuccessListener(o -> trigger.updateList()).addOnFailureListener(e -> {
                Toast.makeText(requireContext(), "Something went wrong, try again! \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "deleteFile: error " + e.getMessage());
            });
        }
        this.dismiss();
    }

    private void downloadFile() {
        progressDialogCloudStorage.showProgressDialog();
        StorageReference reference = storageManagement.getStorageReference(
                folderName + "/" + fileName
        );

        final String PATH = getString(R.string.string_path);
        String folderPath = Environment.getExternalStorageDirectory().getPath() + PATH;
        File folder = new File(folderPath + getString(R.string.special_character) + folderName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            File file = new File(folderPath + getString(R.string.special_character) + folderName + getString(R.string.special_character) + fileName);
            DownloadTask task = reference.getFile(file);
            task.addOnFailureListener(exception -> {
                progressDialogCloudStorage.dismissProgressDialog();
                Log.e(TAG, "Download task failed: " + exception.getMessage());
                if (((StorageException) exception).getCode() == 11016) {
                    Toast.makeText(requireContext(), "File already downloaded.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(downloadResult -> {
                progressDialogCloudStorage.dismissProgressDialog();
                trigger.updateList();
                this.dismiss();
            }).addOnProgressListener(downloadResult -> {
                //OnProgressListener
            }).addOnPausedListener(downloadResult -> {
                //OnPausedListener
            });
        } else {
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            this.dismiss();
        }

    }

}
