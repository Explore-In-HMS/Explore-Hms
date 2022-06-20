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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.genar.hmssandbox.huawei.feature_cloudstorage.CloudStorageDownloadedFilesActivity;
import com.genar.hmssandbox.huawei.feature_cloudstorage.R;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.IFileClickListener;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.IUpdateListTrigger;
import com.genar.hmssandbox.huawei.feature_cloudstorage.dao.ProgressDialogCloudStorage;
import com.genar.hmssandbox.huawei.feature_cloudstorage.databinding.FragmentCloudStorageHomeBinding;
import com.genar.hmssandbox.huawei.feature_cloudstorage.fragments.adapter.CloudStorageListAdapter;
import com.genar.hmssandbox.huawei.feature_cloudstorage.model.CloudStorageFile;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.cloud.storage.core.AGCStorageManagement;
import com.huawei.agconnect.cloud.storage.core.ListResult;
import com.huawei.agconnect.cloud.storage.core.StorageReference;
import com.huawei.agconnect.cloud.storage.core.UploadTask;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.Tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class CloudStorageHomeFragment extends Fragment implements IFileClickListener, IUpdateListTrigger {

    private FragmentCloudStorageHomeBinding binding;
    private static final String TAG = "CloudStorageHomeFragment";
    private AGCStorageManagement storageManagement;
    private ProgressDialogCloudStorage progressDialogCloudStorage;
    private String folderName;
    private NavController navDirections;
    private List<CloudStorageFile> fileList;
    private CloudStorageListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCloudStorageHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Cloud Storage Files");
        storageManagement = AGCStorageManagement.getInstance();

        navDirections = Navigation.findNavController(view);
        progressDialogCloudStorage = new ProgressDialogCloudStorage(requireContext());

        fileList = new ArrayList<>();
        adapter = new CloudStorageListAdapter(fileList, requireContext(), this::onFileClick);
        binding.recyclerViewCloudstorage.setAdapter(adapter);

        initStorage();
        initUI();

    }

    private void initUI() {
        binding.btnAddFile.setOnClickListener(view -> {
            AddFileBottomSheetFragment addPhotoBottomDialogFragment =
                    new AddFileBottomSheetFragment(this::updateList);
            addPhotoBottomDialogFragment.show(requireActivity().getSupportFragmentManager(),
                    "add_file_fragment");
        });

        binding.btnDownloadedFilesCloudstorage.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), CloudStorageDownloadedFilesActivity.class);
            intent.putExtra("folderName", folderName);
            startActivity(intent);
        });
    }

    private void initStorage() {
        progressDialogCloudStorage.showProgressDialog();
        if (AGConnectAuth.getInstance() != null) {
            if (AGConnectAuth.getInstance().getCurrentUser() != null) {
                AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                folderName = "test-" + uid.substring(0, 6) + "/";


                StorageReference reference =
                        storageManagement.getStorageReference(folderName + "init.txt");
                UploadTask task = reference.putFile(new File("assets/init.txt"));


                task.addOnFailureListener(exception -> {
                    Log.e(TAG, "initStorage: " + exception.getMessage());
                    Toast.makeText(requireContext(), "Folder creation failed." + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(uploadResult -> {
                    Toast.makeText(requireContext(), "Folder created successfully.", Toast.LENGTH_SHORT).show();
                    listAllFiles();
                });


            } else {
                progressDialogCloudStorage.dismissProgressDialog();
                navDirections.navigate(R.id.action_gotoHomeFragment_cloudstorage);
            }
        } else {
            progressDialogCloudStorage.dismissProgressDialog();
            navDirections.navigate(R.id.action_gotoHomeFragment_cloudstorage);
        }
    }

    @SuppressLint("CheckResult")
    private void listAllFiles() {
        progressDialogCloudStorage.showProgressDialog();
        Task<ListResult> listTask = storageManagement.getStorageReference(folderName).listAll();
        Observable.create((ObservableOnSubscribe<List<StorageReference>>) emitter -> {
            ListResult listResult = Tasks.await(listTask);
            if (listResult != null && !listResult.getFileList().isEmpty()) {
                emitter.onNext(listResult.getFileList());
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    progressDialogCloudStorage.dismissProgressDialog();
                    if (!fileList.isEmpty()) {
                        fileList.clear();
                    }
                    ArrayList<CloudStorageFile> temp = new ArrayList<>();
                    for (StorageReference item : list) {
                        temp.add(new CloudStorageFile(item, false));
                    }
                    fileList.addAll(temp);
                    adapter.updateData(fileList);
                });
    }


    @SuppressLint("CheckResult")
    private void deleteAllFiles() {
        for (CloudStorageFile item : fileList) {
            StorageReference reference = storageManagement.getStorageReference(
                    folderName + item.getStorageReference().getName()
            );
            Task delete = reference.delete();
            delete.addOnSuccessListener(o -> {
            }).addOnFailureListener(e -> Log.e(TAG, "deleteAllFiles: " + e.getMessage()));
        }
    }

    @Override
    public void onDestroyView() {
        deleteAllFiles();
        super.onDestroyView();
    }

    @Override
    public void onFileClick(CloudStorageFile file) {
        TransactionsBottomSheetFragment transactionsBottomSheetFragment =
                new TransactionsBottomSheetFragment(folderName,
                        file.getStorageReference().getName(),
                        this);

        transactionsBottomSheetFragment.show(requireActivity().getSupportFragmentManager(),
                "transactions_fragments");
    }

    @Override
    public void updateList() {
        listAllFiles();
    }
}