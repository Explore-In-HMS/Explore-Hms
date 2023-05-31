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
import com.huawei.cloud.services.drive.model.Reply;

import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyViewHolder> {

    private static final String TAG = "RepliesAdapter";

    Context context;
    List<Reply> replyList;
    DriveHelper dHelper;

    public RepliesAdapter(Context context, List<Reply> replyList, DriveHelper dHelper) {
        this.context = context;
        this.replyList = replyList;
        this.dHelper = dHelper;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_comment, parent, false);

        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Reply reply = replyList.get(position);

        holder.bind(reply);
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public void updateRepliesAdapter(List<Reply> replies) {
        replyList = replies;
        notifyDataSetChanged();
    }


    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageButton imgBtnEditReply;
        ImageButton imgBtnDeleteReply;
        TextView tvReply;
        TextView tvCreateDate;
        TextView tvCreator;
        TextView tvReplyCount;
        MaterialButton btnReplies;

        Reply reply;

        ReplyViewHolder(View itemView) {
            super(itemView);
            imgBtnEditReply = itemView.findViewById(R.id.imgbtn_edit);
            imgBtnDeleteReply = itemView.findViewById(R.id.imgbtn_delete);
            tvReply = itemView.findViewById(R.id.tv_comment);
            tvCreator = itemView.findViewById(R.id.tv_commenter);
            tvCreateDate = itemView.findViewById(R.id.tv_comment_createDate);
            tvReplyCount = itemView.findViewById(R.id.tv_reply_count);
            tvReplyCount.setVisibility(View.GONE);
            btnReplies = itemView.findViewById(R.id.btn_replies);
            btnReplies.setVisibility(View.GONE);
        }

        private String dateTimeToString(Long timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
            return dateFormat.format(timestamp);
        }

        private int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }

        private void bind(final Reply reply) {
            this.reply = reply;

            tvReply.setText(reply.getDescription());
            tvCreator.setText(reply.getCreator().getDisplayName());
            tvCreateDate.setText(dateTimeToString(reply.getCreatedTime().getValue()));

            imgBtnEditReply.setOnClickListener(v -> showUpdateReplyDialog());

            imgBtnDeleteReply.setOnClickListener(v -> {
                final ProgressDialogScreen progressDialog = new ProgressDialogScreen(context);
                progressDialog.showProgressDialog();

                Future<Boolean> result = Async.submit(() -> dHelper.deleteReplies(DriveHelper.getSelectedFile().getId(), DriveHelper.getSelectedFileComment().getId(), reply.getId()));

                result.addSuccessCallback(result1 -> {
                    if (result1) {
                        replyList.remove(reply);
                        notifyDataSetChanged();
                        ((CommentDetailActivity)context).decrementReplyCount();
                        progressDialog.dismissProgressDialog();
                        Toast.makeText(context, "Reply Deleted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Reply could not be deleted!", Toast.LENGTH_SHORT).show();
                    }
                });

                result.addFailureCallback(t -> {
                    progressDialog.dismissProgressDialog();
                    Log.d(TAG, "Reply delete error on AsyncTask", t);
                    Toast.makeText(context, "Error occurred while deleting the reply!", Toast.LENGTH_LONG).show();
                });

            });
        }

        private void showUpdateReplyDialog() {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            final EditText etNewReply = new EditText(context);
            etNewReply.setText(reply.getDescription());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dpToPx(16), 0, dpToPx(16), 0);
            etNewReply.setLayoutParams(lp);

            alert.setTitle("Update Reply");
            alert.setCancelable(false);

            alert.setView(etNewReply);

            alert.setPositiveButton("Update", (dialog, whichButton) -> {

                if (!etNewReply.getText().toString().equals("")) {
                    final ProgressDialogScreen progressDialog = new ProgressDialogScreen(context);
                    progressDialog.showProgressDialog();

                    Future<Boolean> result = Async.submit(() -> dHelper.editReplyDescription(DriveHelper.getSelectedFile().getId(), DriveHelper.getSelectedFileComment().getId(), reply.getId(), etNewReply.getText().toString()));

                    result.addSuccessCallback(result1 -> {
                        if (result1) {
                            reply.setDescription(etNewReply.getText().toString());
                            notifyDataSetChanged();
                            Toast.makeText(context, "Reply Updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Reply could not be updated!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismissProgressDialog();
                    });

                    result.addFailureCallback(t -> {
                        progressDialog.dismissProgressDialog();
                        Toast.makeText(context, "Reply update error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Reply update error on AsyncTask", t);
                    });
                } else {
                    Toast.makeText(context, "Reply should not be empty!", Toast.LENGTH_LONG).show();
                }
            });

            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
                // what ever you want to do with No option.
            });

            alert.show();
        }
    }
}
