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

package com.hms.explorehms.huawei.feature_drivekit;

import android.content.Context;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.hms.explorehms.huawei.feature_drivekit.model.DriveHelper;
import com.hms.explorehms.huawei.feature_drivekit.model.RepliesAdapter;
import com.hms.explorehms.baseapp.library.ProgressDialogScreen;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.button.MaterialButton;
import com.huawei.cloud.services.drive.model.Comment;
import com.huawei.cloud.services.drive.model.File;
import com.huawei.cloud.services.drive.model.Reply;

import java.util.List;


public class CommentDetailActivity extends AppCompatActivity {

    private static final String TAG = "CommentReplyAdapter";


    private Comment selectedComment;

    private RepliesAdapter repliesAdapter;
    private ProgressDialogScreen progressDialog;
    
    private TextView tvComment;
    private TextView tvReplyCount;
    private int replyCount;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply);
        setupToolbar();

        selectedComment = DriveHelper.getSelectedFileComment();

        initUI();
    }

    private void initUI() {
        progressDialog = new ProgressDialogScreen(CommentDetailActivity.this);

        CardView cardView = findViewById(R.id.include_comment);
        EditText etReply = findViewById(R.id.et_reply);
        Button btnSendReply = findViewById(R.id.btn_sendReply);
        TextView tvNoReply = findViewById(R.id.no_reply);

        ImageButton imgBtnEditComment = cardView.findViewById(R.id.imgbtn_edit);
        ImageButton imgBtnDeleteComment = cardView.findViewById(R.id.imgbtn_delete);
        tvComment = cardView.findViewById(R.id.tv_comment);
        TextView tvCommenter = cardView.findViewById(R.id.tv_commenter);
        TextView tvCreateDate = cardView.findViewById(R.id.tv_comment_createDate);
        tvReplyCount = cardView.findViewById(R.id.tv_reply_count);
        tvReplyCount.setVisibility(View.VISIBLE);
        MaterialButton btnReplies = cardView.findViewById(R.id.btn_replies);
        btnReplies.setVisibility(View.GONE);
        DriveHelper dHelper = new DriveHelper();
        tvComment.setText(selectedComment.getDescription());
        tvCommenter.setText(selectedComment.getCreator().getDisplayName());
        tvCreateDate.setText(dateTimeToString(selectedComment.getCreatedTime().getValue()));

        ImageView ivInfo = findViewById(R.id.ivInfo);

        ivInfo.setOnClickListener(v -> Util.openWebPage(CommentDetailActivity.this, getResources().getString(R.string.drive_link_doc)));

        imgBtnEditComment.setOnClickListener(v -> showUpdateCommentDialog());

        btnSendReply.setOnClickListener(v -> {
            String text = etReply.getText().toString();
            if (text == null || text.length() <= 0) {
                Toast.makeText(CommentDetailActivity.this, "Comment text could not be empty!", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.showProgressDialog();

                sendReply();
            }
        });

        imgBtnDeleteComment.setOnClickListener(v -> {
            progressDialog = new ProgressDialogScreen(CommentDetailActivity.this);
            progressDialog.showProgressDialog();

            Future<Boolean> result = Async.submit(() -> dHelper.deleteComment(DriveHelper.getSelectedFile().getId(), selectedComment.getId()));

            result.addSuccessCallback(result12 -> {
                if (result12) {
                    DriveHelper.releaseSelectedComment();
                    DriveHelper.setDeletedComment(selectedComment);
                    progressDialog.dismissProgressDialog();
                    Toast.makeText(CommentDetailActivity.this, "Comment Deleted", Toast.LENGTH_LONG).show();
                    CommentDetailActivity.this.finish();
                } else {
                    progressDialog.dismissProgressDialog();
                    Toast.makeText(CommentDetailActivity.this, "Comment could not be deleted!", Toast.LENGTH_SHORT).show();
                }
            });

            result.addFailureCallback(t -> {
                progressDialog.dismissProgressDialog();
                Log.d(TAG, "Comment delete error on AsyncTask", t);
                Toast.makeText(CommentDetailActivity.this, "Error occurred while deleting the comment!", Toast.LENGTH_LONG).show();
            });

        });


        Future<List<Reply>> result = Async.submit(() -> dHelper.getReplyList(DriveHelper.getSelectedFile().getId(), selectedComment.getId()));

        result.addSuccessCallback(result1 -> {
            runOnUiThread(() -> {
                replyCount = result1.size();
                tvReplyCount.setText(getResources().getText(R.string.txt_reply_count).toString() + replyCount);
                tvNoReply.setText("Replies:");
                RecyclerView rv = findViewById(R.id.rv_replies);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CommentDetailActivity.this);
                rv.setLayoutManager(layoutManager);
                repliesAdapter = new RepliesAdapter(CommentDetailActivity.this, (List<Reply>) result, dHelper);
                rv.setAdapter(repliesAdapter);
            });
            result.addFailureCallback(t -> {
                Toast.makeText(CommentDetailActivity.this, "Could not retrieve reply list for the comment!", Toast.LENGTH_SHORT).show();
                Log.i(TAG,t.toString());
            });
        });
    }

    public void sendReply() {
        EditText etReply = findViewById(R.id.et_reply);
        progressDialog.showProgressDialog();
        File selectedFile = DriveHelper.getSelectedFile();
        DriveHelper dHelper = new DriveHelper();
        TextView tvNoReply = findViewById(R.id.no_reply);

        Future<Boolean> taskReply = Async.submit(() -> dHelper.addReply(selectedFile.getId(), selectedComment.getId(), etReply.getText().toString()));

        taskReply.addSuccessCallback(result -> {
            if (result) {
                tvNoReply.setText(getString(R.string.replies));
                Future<List<Reply>> taskReplyList = Async.submit(() -> dHelper.getReplyList(selectedFile.getId(), selectedComment.getId()));

                taskReplyList.addSuccessCallback(result1 -> {
                    replyCount++;
                    if (result1.size() > 0) {
                        tvReplyCount.setText(getResources().getText(R.string.txt_reply_count).toString() + replyCount);
                        tvNoReply.setText(getString(R.string.replies));
                        repliesAdapter.updateRepliesAdapter(result1);
                        progressDialog.dismissProgressDialog();
                    } else {
                        Toast.makeText(CommentDetailActivity.this, "No replies found", Toast.LENGTH_SHORT).show();
                    }

                });

                taskReplyList.addFailureCallback(t -> {
                    progressDialog.dismissProgressDialog();
                    Toast.makeText(CommentDetailActivity.this, "Error occurred while getting replies!", Toast.LENGTH_SHORT).show();

                });
                Toast.makeText(CommentDetailActivity.this, "Reply posted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CommentDetailActivity.this, "Error! Try again!", Toast.LENGTH_SHORT).show();
            }

            progressDialog.dismissProgressDialog();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
        });

        taskReply.addFailureCallback(t -> {
            Toast.makeText(CommentDetailActivity.this, "System error! Try again later!", Toast.LENGTH_SHORT).show();

            progressDialog.dismissProgressDialog();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
        });
    }

    private void showUpdateCommentDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText etNewComment = new EditText(this);
        etNewComment.setText(selectedComment.getDescription());
        File selectedFile = DriveHelper.getSelectedFile();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dpToPx(16), 0, dpToPx(16), 0);
        etNewComment.setLayoutParams(lp);
        DriveHelper dHelper = new DriveHelper();
        alert.setTitle("Update Comment");
        alert.setCancelable(false);

        alert.setView(etNewComment);

        alert.setPositiveButton("Update", (dialog, whichButton) -> {

            if (!etNewComment.getText().toString().equals("")) {
                progressDialog = new ProgressDialogScreen(CommentDetailActivity.this);
                progressDialog.showProgressDialog();

                final String updatedComment = etNewComment.getText().toString();

                Future<Boolean> result = Async.submit(() -> dHelper.renameDriveComment(selectedFile.getId(), selectedComment.getId(), etNewComment.getText().toString()));

                result.addSuccessCallback(result1 -> {
                    if (result1) {
                        selectedComment.setDescription(etNewComment.getText().toString());
                        tvComment.setText(updatedComment);
                        selectedComment.setDescription(updatedComment);

                        DriveHelper.setRefreshCommentList(true);
                        Toast.makeText(CommentDetailActivity.this, "Comment Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CommentDetailActivity.this, "Comment could not be updated!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismissProgressDialog();
                });

                result.addFailureCallback(t -> {
                    progressDialog.dismissProgressDialog();
                    Toast.makeText(CommentDetailActivity.this, "Comment update error!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Comment update error on AsyncTask", t);
                });
            } else {
                Toast.makeText(CommentDetailActivity.this, "File name should not be empty!", Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // what ever you want to do with No option.
        });

        alert.show();
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

    String dateTimeToString(Long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
        return dateFormat.format(timestamp);
    }

    public void decrementReplyCount(){
        replyCount--;
        tvReplyCount.setText(getResources().getText(R.string.txt_reply_count).toString() + replyCount);
    }
}