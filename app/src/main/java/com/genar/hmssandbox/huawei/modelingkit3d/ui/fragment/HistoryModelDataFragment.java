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
package com.genar.hmssandbox.huawei.modelingkit3d.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.cameratakelib.utils.LogUtil;
import com.genar.hmssandbox.huawei.modelingkit3d.model.ConstantBean;
import com.genar.hmssandbox.huawei.modelingkit3d.ui.adapter.RecycleHistoryAdapter;
import com.genar.hmssandbox.huawei.modelingkit3d.ui.widget.ProgressCustomDialog;
import com.genar.hmssandbox.huawei.modelingresource.db.TaskInfoAppDb;
import com.genar.hmssandbox.huawei.modelingresource.db.TaskInfoAppDbUtils;
import com.genar.hmssandbox.huawei.modelingresource.util.Constants;
import com.huawei.hms.objreconstructsdk.Modeling3dReconstructConstants;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructDownloadConfig;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructDownloadListener;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructDownloadResult;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructEngine;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructInitResult;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructQueryResult;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructSetting;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructTaskUtils;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructUploadListener;
import com.huawei.hms.objreconstructsdk.cloud.Modeling3dReconstructUploadResult;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HistoryModelDataFragment extends Fragment implements RecycleHistoryAdapter.OnItemClickListener , RecycleHistoryAdapter.OnItemClickDownloadListener, ProgressCustomDialog.OnItemCancelClickListener {
    private Unbinder unbinder;
    private Modeling3dReconstructEngine magic3dReconstructEngine;
    private Modeling3dReconstructDownloadConfig downloadConfig;
    public Modeling3dReconstructTaskUtils magic3DReconstructTaskUtils;
    public ArrayList<TaskInfoAppDb> dataBeans = new ArrayList<>();
    private RecycleHistoryAdapter adapter;
    private Context mContext;

    String TAG = "HistoryDataFragment";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    ProgressCustomDialog dialog;
    private Timer timer = new Timer();

    public HistoryModelDataFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEngine();
        magic3DReconstructTaskUtils = Modeling3dReconstructTaskUtils.getInstance(SandboxApplication.app);
    }

    public void initEngine() {
        magic3dReconstructEngine = Modeling3dReconstructEngine.getInstance(SandboxApplication.app);
        downloadConfig = new Modeling3dReconstructDownloadConfig.Factory()
                //0->Normal mode 1->PBR mode
                .setTextureMode(Modeling3dReconstructConstants.TextureMode.NORMAL)
                .create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_data_fragment_layout, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleHistoryAdapter(dataBeans, getContext());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemClickDownloadListener(this);
        recyclerView.setAdapter(adapter);

        dataBeans = TaskInfoAppDbUtils.getAllTasks();
        adapter.setDataList(dataBeans);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadPage();
            }
        }, 1000, 15000);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        timer = null;
    }

    @Override
    public void onClick(View parent, int position) {
        initEngine();
        magic3dReconstructEngine.setReconstructUploadListener(uploadListener);
        dialog = new ProgressCustomDialog(mContext, ConstantBean.PROGRESS_CUSTOM_DIALOG_TYPE_ONE, getString(R.string.doing_post_text));
        dialog.show();
        dialog.setListener(HistoryModelDataFragment.this, dataBeans.get(position));
        dialog.setCanceledOnTouchOutside(false);
        TaskInfoAppDb news = dataBeans.get(position);
        if (!TextUtils.isEmpty(news.getTaskId())) {
            magic3dReconstructEngine.uploadFile(news.getTaskId(), news.getFileUploadPath());
        } else {
            int type ;
                    if(news.getModelType().equals(mContext.getString(R.string.slam))){
                        type = Constants.SLAM_MODEL ;
                    }else if (news.getModelType().equals(mContext.getString(R.string.rgb))){
                        type = Constants.RGB_MODEL;
                    }else {
                        type=Integer.parseInt(news.getModelType());
                    }
            Modeling3dReconstructSetting setting = new Modeling3dReconstructSetting.Factory()
                    .setReconstructMode(type)
                    .create();
            new Thread(() -> {
                Modeling3dReconstructInitResult result = magic3dReconstructEngine.initTask(setting);
                String taskId = result.getTaskId();
                if (taskId == null || taskId.equals("")) {
                    new Handler(mContext.getMainLooper()).post(() -> {
                        dialog.dismiss();
                        Toast.makeText(mContext, "upload failed", Toast.LENGTH_LONG).show();
                    });
                } else {
                    news.setTaskId(taskId);
                    // Update the latest data containing taskID
                    TaskInfoAppDbUtils.updateTaskIdAndStatusByPath(news.getFileUploadPath(), taskId, ConstantBean.MODELS_INIT_STATUS);
                    magic3dReconstructEngine.uploadFile(taskId, news.getFileUploadPath());
                }
            }).start();
        }
    }

    @Override
    public void onClickDownLoad(TaskInfoAppDb appDb, RecycleHistoryAdapter.DataViewHolder holder) {
        initEngine();
        dialog = new ProgressCustomDialog(getContext(), ConstantBean.PROGRESS_CUSTOM_DIALOG_TYPE_ONE, getString(R.string.downloading_dialog_text));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setListener(HistoryModelDataFragment.this, appDb);

        //Obtains the model file format.
        downloadConfig.getModelFormat();

        //Obtains the texture map mode
        downloadConfig.getTextureMode();

        magic3dReconstructEngine.setReconstructDownloadListener(magic3dReconstructDownloadListener);
        magic3dReconstructEngine.downloadModel(appDb.getTaskId(), appDb.getFileSavePath());
        magic3dReconstructEngine.downloadModelWithConfig(appDb.getTaskId(),appDb.getFileSavePath(), downloadConfig);
    }


    private final Modeling3dReconstructUploadListener uploadListener = new Modeling3dReconstructUploadListener() {
        @Override
        public void onUploadProgress(String taskId, double progress, Object ext) {
            dialog.setCurrentProgress(progress);
        }

        @Override
        public void onResult(String taskId, Modeling3dReconstructUploadResult result, Object ext) {
            if (result.isComplete()) {
                LogUtil.i(TAG + result.isComplete());
                TaskInfoAppDbUtils.updateStatusByTaskId(taskId, ConstantBean.MODELS_UPLOAD_COMPLETED_STATUS);
                updateTaskStatus(adapter, taskId, ConstantBean.MODELS_UPLOAD_COMPLETED_STATUS);
                dialog.dismiss();
            }
        }

        @Override
        public void onError(String taskId, int errorCode, String message) {
            LogUtil.i("UPLOAD FAILED-->" + errorCode + "<----->" + message);
            ((Activity) mContext).runOnUiThread(() -> {
                dialog.dismiss();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            });
        }
    };

    private Modeling3dReconstructDownloadListener magic3dReconstructDownloadListener = new Modeling3dReconstructDownloadListener() {
        @Override
        public void onDownloadProgress(String taskId, double progress, Object ext) {
            dialog.setCurrentProgress(progress);
        }

        @Override
        public void onResult(String taskId, Modeling3dReconstructDownloadResult result, Object ext) {
            ((Activity) mContext).runOnUiThread(() -> {
                Toast.makeText(getContext(), "Download completed", Toast.LENGTH_SHORT).show();
                TaskInfoAppDbUtils.updateDownloadByTaskId(taskId, 1);
                dialog.dismiss();
            });
        }

        @Override
        public void onError(String taskId, int errorCode, String message) {
            LogUtil.e(taskId + " <---> " + errorCode + message);
            ((Activity) mContext).runOnUiThread(() -> {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        }
    };

    private void updateTaskStatus(RecycleHistoryAdapter adapter, String taskId, int comStatus) {
        ArrayList<TaskInfoAppDb> appDbs = dataBeans;
        int count = appDbs.size();
        for (int i = 0; i < count; i++) {
            if (appDbs.get(i).getTaskId() != null && appDbs.get(i).getTaskId().equals(taskId)) {
                appDbs.get(i).setStatus(comStatus);
                break;
            }
        }
        ((Activity) mContext).runOnUiThread(() -> adapter.notifyDataSetChanged());
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (magic3dReconstructEngine != null) {
            magic3dReconstructEngine.close();
        }
    }

    public void loadPage() {
        for (int i = 0; i < dataBeans.size(); i++) {
            TaskInfoAppDb task = dataBeans.get(i);
            if (task.getStatus() < ConstantBean.MODELS_RECONSTRUCT_COMPLETED_STATUS) {
                if (task.getTaskId() != null) {
                    new Thread("queryThread") {
                        @Override
                        public void run() {
                            Modeling3dReconstructQueryResult queryResult = magic3DReconstructTaskUtils.queryTask(task.getTaskId());
                            if (queryResult.getRetCode() == 0) {
                                int ret = queryResult.getStatus();
                                TaskInfoAppDbUtils.updateStatusByTaskId(queryResult.getTaskId(), ret);
                                Lock lock = new ReentrantLock();
                                try {
                                    lock.lock();
                                    task.setStatus(ret);
                                    ((Activity) mContext).runOnUiThread(() -> adapter.notifyDataSetChanged());
                                } finally {
                                    lock.unlock();
                                }
                            }
                        }
                    }.start();
                }
            }
            else {
                //Reconstruction task failure reason
//                String failureMessage = queryResult.getReconstructFailMessage();
//                Toast.makeText(getContext(),failureMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * cancel upload
     *
     * @param appDb Task
     */
    @Override
    public void onCancel(TaskInfoAppDb appDb) {
        initEngine();
        if (appDb.getStatus() == ConstantBean.MODELS_RECONSTRUCT_COMPLETED_STATUS) {
            new Thread("cancelThread") {
                @Override
                public void run() {
                    int result = magic3dReconstructEngine.cancelDownload(appDb.getTaskId());
                    if (result == 0) {
                        ((Activity) mContext).runOnUiThread(() -> Toast.makeText(getActivity(), "Cancel download successfully", Toast.LENGTH_SHORT).show());

                    }
                }
            }.start();
        } else {
            new Thread(() -> {
                int result = magic3dReconstructEngine.cancelUpload(appDb.getTaskId());
                if (result == 0) {
                    TaskInfoAppDbUtils.updateStatusByTaskId(appDb.getTaskId(), ConstantBean.MODELS_INIT_STATUS);
                    ((Activity) mContext).runOnUiThread(() -> {
                        // Update task status
                        updateTaskStatus(adapter, appDb.getTaskId(), ConstantBean.MODELS_INIT_STATUS);
                        Toast.makeText(getActivity(), "Cancel successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }
}
