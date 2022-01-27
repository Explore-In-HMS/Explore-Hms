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

package com.genar.hmssandbox.huawei.feature_drivekit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_drivekit.model.DriveFileUtil;
import com.genar.hmssandbox.huawei.feature_drivekit.model.DriveHelper;
import com.genar.hmssandbox.huawei.feature_drivekit.model.FilesAdapter;
import com.genar.hmssandbox.huawei.baseapp.library.ProgressDialogScreen;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.button.MaterialButton;
import com.huawei.cloud.base.util.StringUtils;
import com.huawei.cloud.services.drive.Drive;
import com.huawei.cloud.services.drive.model.File;
import com.huawei.cloud.services.drive.model.FileList;

import java.io.InputStream;
import java.util.List;

public class FilesActivity extends AppCompatActivity {


    private static final int PICK_FILE_REQUEST = 1001;
    private static final String TAG = "FilesActivity";
    FilesAdapter kitAdapter;
    List<File> driveFiles;
    RecyclerView rv;
    EditText etSearchQuery;
    private DriveHelper mDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        mDrive = new DriveHelper();
        setupToolbar();

        ImageButton btnSearch = findViewById(R.id.btn_file_search);
        MaterialButton btnUpload = findViewById(R.id.btn_uploadfile);
        ImageView ivInfo = findViewById(R.id.ivInfo);
        etSearchQuery = findViewById(R.id.et_filesearch);

        ivInfo.setOnClickListener(v -> Util.openWebPage(FilesActivity.this, getResources().getString(R.string.drive_link_doc)));

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent();
            //sets the select file to all types of files
            intent.setType("*/*");
            //allows to select data and return it
            intent.setAction(Intent.ACTION_GET_CONTENT);
            //starts new activity to select file and return data
            startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
        });

        btnSearch.setOnClickListener(v -> searchDriveFiles());

        etSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                searchDriveFiles();
                return true;
            }
            return false;
        });

        queryFiles(null);
    }


    void searchDriveFiles() {
        String searchQuery = etSearchQuery.getText().toString();
        if (!searchQuery.equals(""))
            queryFiles(searchQuery);
        else
            queryFiles("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void queryFiles(final String queryText) {
        new Thread(() -> {
            try {
                String containers = "";
                String queryFile = "";
                if (queryText != null && !queryText.equals(""))
                    queryFile = "fileName contains '" + queryText + "' and mimeType != 'application/vnd.huawei-apps.folder'";
                else
                    queryFile = "mimeType != 'application/vnd.huawei-apps.folder'";

                Drive drive = DriveHelper.buildDrive(getApplicationContext());
                Drive.Files.List request = drive.files().list();
                FileList files;
                while (true) {
                    files = request
                            .setQueryParam(queryFile)
                            .setPageSize(100)
                            .setOrderBy("fileName")
                            .setFields("*")
                            .setContainers(containers)
                            .execute();
                    if (files == null || files.getFiles().size() > 0) {
                        break;
                    }
                    if (!StringUtils.isNullOrEmpty(files.getNextCursor())) {
                        request.setCursor(files.getNextCursor());
                    } else {
                        break;
                    }
                }
                if (files != null && files.getFiles().size() > 0) {
                    driveFiles = files.getFiles();
                } else {
                    showTips("Files not found");
                    driveFiles.clear();
                }
                FilesActivity.this.runOnUiThread(this::updateRecyclerView);
            } catch (Exception ex) {
                Log.d(TAG, "query error " + ex.toString());
                showTips("query error " + ex.toString());
            }
        }).start();
    }

    private void updateRecyclerView() {
        if (rv != null) {
            kitAdapter.updateFileAdapter(driveFiles);
        } else if (driveFiles != null && driveFiles.size() > 0) {
            rv = findViewById(R.id.rv_files);
            int columnCount = 2;
            rv.addItemDecoration(new SpacesItemDecoration(8, columnCount));

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
            rv.setLayoutManager(layoutManager);
            kitAdapter = new FilesAdapter(this, driveFiles);
            rv.setAdapter(kitAdapter);
        }
    }

    private void showTips(final String text) {
        runOnUiThread(() -> {
            final Toast toast = Toast.makeText(FilesActivity.this, text, Toast.LENGTH_LONG);
            toast.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DriveHelper.getDeletedFile() != null && driveFiles != null && driveFiles.size() > 0) {
            driveFiles.remove(DriveHelper.getDeletedFile());
            kitAdapter.updateFileAdapter(driveFiles);
            DriveHelper.setDeletedFile(null);
        }

        if (DriveHelper.isRefreshFileList()) {
            kitAdapter.refreshAdapter();
            DriveHelper.setRefreshFileList(false);
        }

        if (DriveHelper.isUpdateFileListFromServer()) {
            queryFiles(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_FILE_REQUEST && data != null) {
            final ProgressDialogScreen prg = new ProgressDialogScreen(FilesActivity.this);
            final Uri selectedFileUri = data.getData();
            prg.showProgressDialog();

            Future<Boolean> result = Async.submit(() -> {
                InputStream io = getContentResolver().openInputStream(selectedFileUri);
                return (boolean) mDrive.createFile(io, null, DriveFileUtil.getFileRealNameFromUri(this, selectedFileUri));
            });

            result.addSuccessCallback(result1 -> {
                if (result1) {
                    Toast.makeText(FilesActivity.this, "Upload Successful:"+result1.toString(), Toast.LENGTH_LONG).show();
                    queryFiles(null);
                } else {
                    Toast.makeText(FilesActivity.this, "Upload Error!", Toast.LENGTH_LONG).show();
                }
                prg.dismissProgressDialog();
            });

            result.addFailureCallback(t -> {
                prg.dismissProgressDialog();
                Toast.makeText(FilesActivity.this, "Unexpected Error", Toast.LENGTH_LONG).show();
            });
        }


    }


    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;
        private final int columnCount;

        public SpacesItemDecoration(int space, int columnCount) {
            this.space = space;
            this.columnCount = columnCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) < columnCount) {
                outRect.top = space;
            }
        }
    }

}
