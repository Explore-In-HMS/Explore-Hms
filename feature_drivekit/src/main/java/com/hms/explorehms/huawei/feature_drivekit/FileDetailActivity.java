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

package com.hms.explorehms.huawei.feature_drivekit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_drivekit.model.CommentsAdapter;
import com.hms.explorehms.huawei.feature_drivekit.model.DriveHelper;
import com.hms.explorehms.baseapp.library.ProgressDialogScreen;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.huawei.cloud.services.drive.model.Comment;
import com.huawei.cloud.services.drive.model.File;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FileDetailActivity extends AppCompatActivity {

    private static final String TAG = "FileDetailActivity";
    private File selectedFile;

    private List<Comment> driveComments;

    private CommentsAdapter commentsAdapter;
    private DriveHelper dHelper;

    private TextView tvFilename;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);
        setupToolbar();

        selectedFile = DriveHelper.getSelectedFile();
        dHelper = new DriveHelper();

        initUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        ProgressDialogScreen progressDialog= new ProgressDialogScreen(FileDetailActivity.this);
        Button btnRenameFile = findViewById(R.id.btn_renameFile);
        CardView cardView = findViewById(R.id.include_filedetails);
        TextView tvNoComment = findViewById(R.id.no_comment);

        ImageView ivImage = cardView.findViewById(R.id.iv_file_image);
        tvFilename = cardView.findViewById(R.id.tv_filename);
        TextView tvCreationDate = cardView.findViewById(R.id.tv_creationdate);
        TextView tvFileSize = cardView.findViewById(R.id.tv_filesize);
        Button btnDownload = cardView.findViewById(R.id.btn_download);
        Button btnDelete = cardView.findViewById(R.id.btn_delete);
        Button btnCopyFile = cardView.findViewById(R.id.btn_copyFile);
        ImageView ivHistoryButton = cardView.findViewById(R.id.iv_historybutton);

        ivImage.setImageResource(R.drawable.icon_drive_image);
        tvFilename.setText(selectedFile.getFileName());
        tvCreationDate.setText(dateTimeToString(selectedFile.getCreatedTime().getValue()));
        double fileSize = (double) selectedFile.getSize() / (double) 1024;
        tvFileSize.setText(String.format("%s KB", roundTwoDecimals(fileSize)));
        EditText etComment= findViewById(R.id.et_comment);
        Button btnSendComment = findViewById(R.id.btn_sendcomment);

        Picasso.get()
                .load(selectedFile.getIconDownloadLink())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.icon_drive_file)
                .into(ivImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //onSuccess
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

        ImageView ivInfo = findViewById(R.id.ivInfo);

        ivInfo.setOnClickListener(v -> Util.openWebPage(FileDetailActivity.this, getResources().getString(R.string.drive_link_doc)));

        ivHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(FileDetailActivity.this, HistoryVersionActivity.class);
            FileDetailActivity.this.startActivity(intent);
        });

        btnSendComment.setOnClickListener(v -> {
            String text = etComment.getText().toString();
            if (text.length() <= 0) {
                Toast.makeText(FileDetailActivity.this, "Comment text could not be empty!", Toast.LENGTH_SHORT).show();
            } else {
                runOnUiThread(() -> {
                    progressDialog.showProgressDialog();
                    try {

                        dHelper.addComment(selectedFile.getId(), etComment.getText().toString());
                        driveComments = dHelper.getCommentList(selectedFile.getId());
                        if (driveComments.size() > 0) {
                            tvNoComment.setText("Comments:");
                        }
                        commentsAdapter.updateDataSet(driveComments);
                        etComment.setText("");

                        progressDialog.dismissProgressDialog();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                    } catch (Exception e) {
                        Log.e("Error", "Exception: " + e.getMessage());
                        progressDialog.dismissProgressDialog();
                    }
                });
            }
        });

        btnDelete.setOnClickListener(v -> {
            progressDialog.showProgressDialog();
            Future<String> result = Async.submit(() -> {
                dHelper.deleteFile(selectedFile.getId());
                return "A";
            });

            result.addSuccessCallback(result1 -> {
                progressDialog.dismissProgressDialog();
                DriveHelper.releaseSelectedFile();
                DriveHelper.setDeletedFile(selectedFile);
                Toast.makeText(FileDetailActivity.this, "File '" + selectedFile.getFileName() + "' is deleted!", Toast.LENGTH_LONG).show();
                FileDetailActivity.this.finish();
            });

            result.addFailureCallback(t -> {
                progressDialog.dismissProgressDialog();
                Toast.makeText(FileDetailActivity.this, "File could not been deleted! Try again.", Toast.LENGTH_SHORT).show();
            });
        });

        btnDownload.setOnClickListener(v -> {
            progressDialog.showProgressDialog();
            Future<String> result = Async.submit(() -> {
                dHelper.downloadFile(selectedFile);
                return "A";
            });

            result.addSuccessCallback(result1 -> {
                progressDialog.dismissProgressDialog();
                Toast.makeText(FileDetailActivity.this, "File '" + selectedFile.getFileName() + "' is downloaded to folder : \n //Huawei//ExploreHMS//DownLoad//", Toast.LENGTH_LONG).show();
            });

            result.addFailureCallback(t -> {
                progressDialog.dismissProgressDialog();
                Toast.makeText(FileDetailActivity.this, "Error occured while downloading the file! Try again.", Toast.LENGTH_LONG).show();
            });
        });

        btnCopyFile.setOnClickListener(v -> {
            Future<Boolean> result = Async.submit(() -> dHelper.copyFile(selectedFile));

            result.addSuccessCallback(result1 -> {
                if (result1) {
                    Toast.makeText(FileDetailActivity.this, "File copied!", Toast.LENGTH_LONG).show();
                    DriveHelper.setUpdateFileListFromServer(true);
                } else {
                    Toast.makeText(FileDetailActivity.this, "Error occurred while copying the file", Toast.LENGTH_LONG).show();
                }
            });

            result.addFailureCallback(t -> {
                progressDialog.dismissProgressDialog();
                Toast.makeText(FileDetailActivity.this, "Network Error! Try again later!", Toast.LENGTH_LONG).show();
            });
        });

        btnRenameFile.setOnClickListener(v -> showRenameDialog());

        try {
            driveComments = dHelper.getCommentList(selectedFile.getId());
            RecyclerView rv = findViewById(R.id.rv_comments);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            rv.setLayoutManager(layoutManager);
            commentsAdapter = new CommentsAdapter(this, driveComments, dHelper);
            rv.setAdapter(commentsAdapter);

            if (driveComments.size() > 0) {
                tvNoComment.setText(R.string.comments);
            } else {
                tvNoComment.setText(R.string.no_comments);
            }
        } catch (ExecutionException e) {
            Toast.makeText(this, R.string.could_not_retrieve_comment_list_for_the_file, Toast.LENGTH_SHORT).show();
            Log.i(TAG,e.toString());
        }catch (InterruptedException e) {
            Toast.makeText(this, R.string.could_not_retrieve_comment_list_of_the_file, Toast.LENGTH_SHORT).show();
            Log.i(TAG,e.toString());
            Thread.currentThread().interrupt();
        }
    }

    private void showRenameDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText etNewFileName = new EditText(this);
        etNewFileName.setText(selectedFile.getFileName().substring(0, selectedFile.getFileName().lastIndexOf(".")));
        ProgressDialogScreen progressDialog= new ProgressDialogScreen(FileDetailActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpToPx(16), 0, dpToPx(16), 0);
        etNewFileName.setLayoutParams(lp);

        alert.setTitle("Rename File");
        alert.setCancelable(false);

        alert.setView(etNewFileName);

        alert.setPositiveButton("Rename", (dialog, whichButton) -> {

            if (!etNewFileName.getText().toString().equals("")) {
                final String newFileName = etNewFileName.getText().toString() + getFileExtension(selectedFile.getFileName());
                progressDialog.showProgressDialog();
                Future<Void> result = Async.submit(() -> {
                    dHelper.renameDriveFile(selectedFile, newFileName);
                    return null;
                });

                result.addSuccessCallback(result1 -> {
                    progressDialog.dismissProgressDialog();
                    tvFilename.setText(newFileName);
                    selectedFile.setFileName(newFileName);
                    DriveHelper.setRefreshFileList(true);
                    Toast.makeText(FileDetailActivity.this, "File name changed to '" + newFileName + "'.", Toast.LENGTH_LONG).show();
                });

                result.addFailureCallback(t -> {
                    progressDialog.dismissProgressDialog();
                    Toast.makeText(FileDetailActivity.this, "Error occured while file name change", Toast.LENGTH_LONG).show();
                });
            } else {
                Toast.makeText(FileDetailActivity.this, "File name should not be empty!", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // what ever you want to do with No option.
        });

        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DriveHelper.getDeletedComment() != null && driveComments != null && driveComments.size() > 0) {
            driveComments.remove(DriveHelper.getDeletedComment());
            commentsAdapter.updateCommentAdaptor(driveComments);
            DriveHelper.setDeletedComment(null);
        }

        if (DriveHelper.isRefreshCommentList()) {
            commentsAdapter.refreshAdapter();
            DriveHelper.setRefreshCommentList(false);
        }
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.parseDouble(twoDForm.format(d));
    }

    String dateTimeToString(Long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
        return dateFormat.format(timestamp);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}