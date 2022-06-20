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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.IUpdateListTrigger;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.ProgressDialogCloudStorage;
import com.genar.hmssandbox.huawei.feature_cloudstorage.databinding.ItemBottomsheetfragmentCloudstorageBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.FileMetadata;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hms.utils.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class AddFileBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "AddFileBottomSheetFragment";
    private ItemBottomsheetfragmentCloudstorageBinding binding;
    private final IUpdateListTrigger trigger;
    private AGCStorageManagement storageManagement;
    private String folderName;
    private ProgressDialogCloudStorage progressDialogCloudStorage;
    private Uri uri;


    public AddFileBottomSheetFragment(IUpdateListTrigger trigger) {
        this.trigger = trigger;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ItemBottomsheetfragmentCloudstorageBinding.inflate(inflater, container, false);
        // get the views and attach the listener
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageManagement = AGCStorageManagement.getInstance();

        progressDialogCloudStorage = new ProgressDialogCloudStorage(requireContext());
        initStorage();
        initUI();
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        binding.btnSelectFileCloudstorage.setOnClickListener(view -> selectFile());

        binding.btnAddFile.setOnClickListener(view -> {
            if (binding.tvFileUriCloudstorage.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please select a file.", Toast.LENGTH_SHORT).show();
            } else {
                addFile();
            }
        });
    }

    private void addFile() {
        progressDialogCloudStorage.showProgressDialog();
        StorageReference reference = storageManagement.getStorageReference(
                folderName +
                        uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1));

        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            byte[] arr = IOUtils.toByteArray(is);

            HashMap<String, String> cust = new HashMap<>();
            if ((!binding.edtMetadataValueCloudstorage.getEditableText().toString().isEmpty() ||
                    !binding.edtMetadataTitleCloudstorage.getEditableText().toString().isEmpty()) &&
                    !twoInputEmptyValidator(binding.edtMetadataTitleCloudstorage, binding.edtMetadataValueCloudstorage)) {
                return;
            }
            cust.put(binding.edtMetadataTitleCloudstorage.getEditableText().toString(), binding.edtMetadataValueCloudstorage.getEditableText().toString());

            FileMetadata attribute = new FileMetadata();
            attribute.setCacheControl("no-store");
            attribute.setContentDisposition("attachment;filename=" + uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1));
            attribute.setContentEncoding("identity");
            attribute.setContentLanguage("en-US");
            attribute.setCustomMetadata(cust);

            UploadTask task = reference.putBytes(arr, attribute);

            task.addOnFailureListener(exception -> {
                progressDialogCloudStorage.dismissProgressDialog();
                Toast.makeText(requireContext(), "File adding failed." + exception.getMessage(), Toast.LENGTH_SHORT).show();
                this.dismiss();
            }).addOnSuccessListener(uploadResult -> {
                progressDialogCloudStorage.dismissProgressDialog();
                Toast.makeText(requireContext(), "File added successfully.", Toast.LENGTH_SHORT).show();
                trigger.updateList();
                this.dismiss();
            });
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void initStorage() {
        if (AGConnectAuth.getInstance() != null && AGConnectAuth.getInstance().getCurrentUser() != null) {
            AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            folderName = "test-" + uid.substring(0, 6) + "/";
        }
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    101);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(requireContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            uri = data.getData();
            binding.tvFileUriCloudstorage.setText(uri.toString());

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Boolean twoInputEmptyValidator(TextInputEditText editText1, TextInputEditText editText2) {

        int temp = 0;

        if (editText1.getEditableText().toString().trim().isEmpty()) {
            editText1.setError("Please fill the empty area.");
            temp++;
        }

        if (editText2.getEditableText().toString().trim().isEmpty()) {
            editText2.setError("Please fill the empty area.");
            temp++;
        }
        return temp == 0;
    }

}
