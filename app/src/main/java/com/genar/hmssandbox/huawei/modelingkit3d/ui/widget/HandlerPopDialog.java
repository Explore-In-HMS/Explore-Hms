/**
 * Copyright 2021. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.genar.hmssandbox.huawei.modelingkit3d.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.modelingkit3d.model.ConstantBean;
import com.genar.hmssandbox.huawei.modelingkit3d.ui.adapter.RecycleHistoryAdapter;
import com.genar.hmssandbox.huawei.modelingresource.db.TaskInfoAppDb;
import com.genar.hmssandbox.huawei.modelingresource.db.TaskInfoAppDbUtils;
import com.genar.hmssandbox.huawei.modelingresource.util.Utils;
import com.huawei.hms.objreconstructsdk.Modeling3dReconstructConstants;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructTaskUtils;

import java.io.File;
import java.util.ArrayList;


public class HandlerPopDialog {
    private Context mContext;
    private TaskInfoAppDb appDb;
    RecycleHistoryAdapter.DataViewHolder holder;
    View contentView;
    RecycleHistoryAdapter adapter;
    PopupWindow popupWindow;
    TextView tvDownload;
    ArrayList<TaskInfoAppDb> dataList;
    public Modeling3dReconstructTaskUtils magic3DReconstructTaskUtils;
    int status ;

    public HandlerPopDialog(Context mContext, RecycleHistoryAdapter adapter, TaskInfoAppDb appDb, RecycleHistoryAdapter.DataViewHolder holder, ArrayList<TaskInfoAppDb> dataList ,int status) {
        this.mContext = mContext;
        this.appDb = appDb;
        this.holder = holder;
        this.adapter = adapter;
        this.dataList = dataList;
        this.status = status ;
        contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_dialog_layout, null);
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        int[] windowPos = calculatePopWindowPos(holder.ivPoint, contentView);
        popupWindow.showAtLocation(holder.ivPoint, Gravity.START | Gravity.TOP, windowPos[0] - Utils.dip2px(mContext, 25), windowPos[1] + Utils.dip2px(mContext, 25));
        initView(contentView);
    }

    private void initView(View contentView) {
        tvDownload = contentView.findViewById(R.id.tv_download);
        if (appDb.getStatus() == ConstantBean.MODELS_RECONSTRUCT_COMPLETED_STATUS) {
            tvDownload.setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.tv_restrict_status).setVisibility(View.VISIBLE);
            if (status==Modeling3dReconstructConstants.RestrictStatus.UNRESTRICT){
                ((TextView)contentView.findViewById(R.id.tv_restrict_status)).setText(R.string.restricted_text);
            }else if (status==Modeling3dReconstructConstants.RestrictStatus.RESTRICT){
                ((TextView)contentView.findViewById(R.id.tv_restrict_status)).setText(R.string.unrestricted_text);
            }
        } else {
            tvDownload.setVisibility(View.GONE);
        }
        if (appDb.getStatus() == ConstantBean.MODELS_RECONSTRUCT_START_STATUS) {
            contentView.findViewById(R.id.tv_delete).setVisibility(View.GONE);
        } else {
            contentView.findViewById(R.id.tv_delete).setVisibility(View.VISIBLE);
        }

        try {
            String savePath = TaskInfoAppDbUtils.getTasksByTaskId(appDb.getTaskId()).getFileSavePath();
            if (TextUtils.isEmpty(savePath)) {
                contentView.findViewById(R.id.tv_open_file).setVisibility(View.GONE);
            }else {
                contentView.findViewById(R.id.tv_open_file).setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            contentView.findViewById(R.id.tv_open_file).setVisibility(View.GONE);
        }

        tvDownload.setOnClickListener(v -> {
            String savePath = TaskInfoAppDbUtils.getTasksByTaskId(appDb.getTaskId()).getFileSavePath();
            if (TextUtils.isEmpty(savePath)) {
                adapter.setOnDownLoadClick(appDb, holder);
            } else {
                File file = new File(savePath);
                if (!file.exists() || file.listFiles() == null || file.listFiles().length != 3) {
                    Utils.deleteDirectory(savePath);
                    adapter.setOnDownLoadClick(appDb, holder);
                } else {
                    Toast.makeText(mContext, "The model file already exists.", Toast.LENGTH_LONG).show();
                }
            }
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.tv_delete).setOnClickListener(v -> {

            DeleteDialog deleteDialog = new DeleteDialog(mContext);
            deleteDialog.show();
            deleteDialog.getTvSure().setOnClickListener(view -> {
                deleteDialog.dismiss();
                TaskInfoAppDbUtils.deleteByUploadFilePath(appDb.getFileUploadPath());
                dataList.remove(holder.getAdapterPosition());
                adapter.notifyDataSetChanged();
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                magic3DReconstructTaskUtils = Modeling3dReconstructTaskUtils.getInstance(SandboxApplication.app);
                new Thread(() -> magic3DReconstructTaskUtils.deleteTask(appDb.getTaskId())).start();
            });

            deleteDialog.getTvCancel().setOnClickListener(view -> {
                deleteDialog.dismiss();
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            });
        });

        contentView.findViewById(R.id.tv_open_file).setOnClickListener(view -> {
            String savePath = TaskInfoAppDbUtils.getTasksByTaskId(appDb.getTaskId()).getFileSavePath() ;
            Toast.makeText(mContext,savePath,Toast.LENGTH_LONG).show();
        });

        contentView.findViewById(R.id.tv_restrict_status).setOnClickListener(v -> {
            magic3DReconstructTaskUtils = Modeling3dReconstructTaskUtils.getInstance(SandboxApplication.app);
            new Thread(()->{
                if (status==Modeling3dReconstructConstants.RestrictStatus.UNRESTRICT){
                    magic3DReconstructTaskUtils.setTaskRestrictStatus(appDb.getTaskId(),Modeling3dReconstructConstants.RestrictStatus.RESTRICT);
                }else {
                    magic3DReconstructTaskUtils.setTaskRestrictStatus(appDb.getTaskId(),Modeling3dReconstructConstants.RestrictStatus.UNRESTRICT);
                }
            }).start();
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        });
    }

    private static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int[] windowPos = new int[2];
        final int[] anchorLoc = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
