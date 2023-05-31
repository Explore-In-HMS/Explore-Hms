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
import android.content.Intent;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_drivekit.CommentDetailActivity;
import com.hms.explorehms.huawei.feature_drivekit.R;
import com.hms.explorehms.baseapp.library.ProgressDialogScreen;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.button.MaterialButton;
import com.huawei.cloud.services.drive.model.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private static final String TAG = "CommentsAdapter";

    Context context;
    List<Comment> commentList;
    DriveHelper dHelper;

    public CommentsAdapter(Context context, List<Comment> commentList, DriveHelper dHelper) {
        this.context = context;
        this.commentList = commentList;
        this.dHelper = dHelper;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rv_comment, parent, false);

        return new CommentsAdapter.CommentViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateDataSet(List<Comment> comments) {
        commentList = comments;
        notifyDataSetChanged();
    }

    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    public void updateCommentAdaptor(List<Comment> list) {
        commentList = list;
        notifyDataSetChanged();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageButton imgBtnEditComment;
        ImageButton imgBtnDeleteComment;
        TextView tvComment;
        TextView tvCreateDate;
        TextView tvCommenter;
        TextView tvReplyCount;
        MaterialButton btnReplies;

        Comment comment;

        CommentViewHolder(View itemView) {
            super(itemView);
            imgBtnEditComment = itemView.findViewById(R.id.imgbtn_edit);
            imgBtnDeleteComment = itemView.findViewById(R.id.imgbtn_delete);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvCommenter = itemView.findViewById(R.id.tv_commenter);
            tvCreateDate = itemView.findViewById(R.id.tv_comment_createDate);
            tvReplyCount = itemView.findViewById(R.id.tv_reply_count);
            tvReplyCount.setVisibility(View.GONE);
            btnReplies = itemView.findViewById(R.id.btn_replies);
            btnReplies.setVisibility(View.VISIBLE);
        }

        void bind(final Comment comment) {
            this.comment = comment;

            tvComment.setText(comment.getDescription());
            tvCommenter.setText(comment.getCreator().getDisplayName());
            tvCreateDate.setText(dateTimeToString(comment.getCreatedTime().getValue()));

            btnReplies.setOnClickListener(v -> {
                DriveHelper.setSelectedFileComment(comment);
                Intent intent = new Intent(context, CommentDetailActivity.class);
                context.startActivity(intent);
            });

            imgBtnEditComment.setOnClickListener(v -> showUpdateCommentDialog());

            imgBtnDeleteComment.setOnClickListener(v -> {
                final ProgressDialogScreen progressDialog = new ProgressDialogScreen(context);
                progressDialog.showProgressDialog();

                Future<Boolean> result = Async.submit(() -> dHelper.deleteComment(DriveHelper.getSelectedFile().getId(), comment.getId()));

                result.addSuccessCallback(result1 -> {
                    if (result1) {
                        commentList.remove(comment);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Comment Deleted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Comment could not be deleted!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismissProgressDialog();
                });

                result.addFailureCallback(t -> {
                    progressDialog.dismissProgressDialog();
                    Log.d(TAG, "Comment delete error on AsyncTask", t);
                    Toast.makeText(context, "Error occurred while deleting the comment!", Toast.LENGTH_LONG).show();
                });

            });
        }

        private void showUpdateCommentDialog() {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            final EditText etNewComment = new EditText(context);
            etNewComment.setText(comment.getDescription());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dpToPx(16), 0, dpToPx(16), 0);
            etNewComment.setLayoutParams(lp);

            alert.setTitle("Update Comment");
            alert.setCancelable(false);

            alert.setView(etNewComment);

            alert.setPositiveButton("Update", (dialog, whichButton) -> {

                if (!etNewComment.getText().toString().equals("")) {
                    final ProgressDialogScreen progressDialog = new ProgressDialogScreen(context);
                    progressDialog.showProgressDialog();

                    Future<Boolean> result = Async.submit(() -> dHelper.renameDriveComment(DriveHelper.getSelectedFile().getId(), comment.getId(), etNewComment.getText().toString()));

                    result.addSuccessCallback(result1 -> {
                        if (result1) {
                            comment.setDescription(etNewComment.getText().toString());
                            notifyDataSetChanged();
                            Toast.makeText(context, "Comment Updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Comment could not be updated!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismissProgressDialog();
                    });

                    result.addFailureCallback(t -> {
                        progressDialog.dismissProgressDialog();
                        Toast.makeText(context, "Comment update error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Comment update error on AsyncTask", t);
                    });
                } else {
                    Toast.makeText(context, "Comment should not be empty!", Toast.LENGTH_LONG).show();
                }
            });

            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
                // what ever you want to do with No option.
            });

            alert.show();
        }

        String dateTimeToString(Long timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
            return dateFormat.format(timestamp);
        }

        private int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }
    }
}
