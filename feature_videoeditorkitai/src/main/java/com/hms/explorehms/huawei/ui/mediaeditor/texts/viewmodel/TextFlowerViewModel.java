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

package com.hms.explorehms.huawei.ui.mediaeditor.texts.viewmodel;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.hms.explorehms.huawei.ui.common.bean.CloudMaterialBean;
import com.hms.explorehms.huawei.ui.common.bean.Constant;
import com.hms.explorehms.huawei.ui.common.bean.MaterialsDownloadInfo;
import com.huawei.hms.videoeditor.materials.HVEChildColumnRequest;
import com.huawei.hms.videoeditor.materials.HVEChildColumnResponse;
import com.huawei.hms.videoeditor.materials.HVEColumnInfo;
import com.huawei.hms.videoeditor.materials.HVEDownloadMaterialListener;
import com.huawei.hms.videoeditor.materials.HVEDownloadMaterialRequest;
import com.huawei.hms.videoeditor.materials.HVELocalMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialConstant;
import com.huawei.hms.videoeditor.materials.HVEMaterialInfo;
import com.huawei.hms.videoeditor.materials.HVEMaterialsManager;
import com.huawei.hms.videoeditor.materials.HVEMaterialsResponseCallback;
import com.huawei.hms.videoeditor.materials.HVETopColumnInfo;
import com.huawei.hms.videoeditor.materials.HVETopColumnRequest;
import com.huawei.hms.videoeditor.materials.HVETopColumnResponse;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;
import com.hms.explorehms.huawei.ui.common.utils.StringUtil;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TextFlowerViewModel extends AndroidViewModel {

    private static final String TAG = "TextFlowerViewModel";

    private final MutableLiveData<String> errorString = new MutableLiveData<>();

    private final MutableLiveData<String> emptyString = new MutableLiveData<>();

    private final MutableLiveData<List<CloudMaterialBean>> mFlowerMaterials = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mFlowerDownloadSuccess = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadFail = new MutableLiveData<>();

    private final MutableLiveData<MaterialsDownloadInfo> mDownloadProgress = new MutableLiveData<>();

    private final MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> clear = new MutableLiveData<>();

    private MutableLiveData<String> fontColumn = new MutableLiveData<>();

    public TextFlowerViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadMaterials(Integer page) {
        HVEMaterialsResponseCallback contentFlowerListener =
            new HVEMaterialsResponseCallback<HVEChildColumnResponse>() {
                @Override
                public void onFinish(HVEChildColumnResponse response) {
                    initMaterialsCutContentResp(response, R.string.result_empty);
                }

                @Override
                public void onUpdate(HVEChildColumnResponse response) {
                    initMaterialsCutContentResp(response, R.string.result_empty);
                }

                @Override
                public void onError(Exception e) {
                    errorString.postValue(getApplication().getString(R.string.result_illegal));
                    String b = e.getMessage();
                    SmartLog.e(TAG, b);
                }
            };

        HVEMaterialsResponseCallback columnFlowerListener = new HVEMaterialsResponseCallback<HVETopColumnResponse>() {
            @Override
            public void onFinish(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response, page, contentFlowerListener);
            }

            @Override
            public void onUpdate(HVETopColumnResponse response) {
                initMaterialsCutColumnResp(response, page, contentFlowerListener);
            }

            @Override
            public void onError(Exception e) {
                errorString.postValue(getApplication().getString(R.string.result_illegal));
                String b = e.getMessage();
                SmartLog.e(TAG, b);
            }
        };

        List<String> fatherColumnFont = new ArrayList<>();
        fatherColumnFont.add(HVEMaterialConstant.TEXT_FLOWER_FATHER_COLUMN);
        HVETopColumnRequest request = new HVETopColumnRequest(fatherColumnFont);

        HVEMaterialsManager.getTopColumnById(request, columnFlowerListener);
    }

    private void initMaterialsCutContentResp(HVEChildColumnResponse response, int p) {
        List<HVEMaterialInfo> bubblesContents = response.getMaterialInfoList();
        if (!bubblesContents.isEmpty()) {
            boundaryPageData.postValue(response.isHasMoreItem());
            queryDownloadStatus(bubblesContents);
        } else {
            errorString.postValue(getApplication().getString(p));
        }
    }

    private void initMaterialsCutColumnResp(HVETopColumnResponse response, Integer page,
        HVEMaterialsResponseCallback<HVEChildColumnResponse> contentListener) {
        List<HVETopColumnInfo> columns = response.getColumnInfos();
        if (columns.isEmpty()) {
            return;
        }

        HVETopColumnInfo topColumnInfo = columns.get(0);
        if (topColumnInfo == null || topColumnInfo.getChildInfoList().isEmpty()) {
            errorString.postValue(getApplication().getString(R.string.result_empty));
            return;
        }

        List<HVEColumnInfo> fontColumns = topColumnInfo.getChildInfoList();

        if (fontColumns.size() > 0) {
            SmartLog.i(TAG, "return text font content category");
            HVEChildColumnRequest request =
                new HVEChildColumnRequest(fontColumns.get(0).getColumnId(), page * Constant.PAGE_SIZE, Constant.PAGE_SIZE, false);

            fontColumn.postValue(fontColumns.get(0).getColumnName());

            HVEMaterialsManager.getChildColumnById(request, contentListener);
        }
    }

    private void queryDownloadStatus(List<HVEMaterialInfo> materialInfos) {
        List<CloudMaterialBean> list = new ArrayList<>();
        for (int i = 0; i < materialInfos.size(); i++) {
            CloudMaterialBean materialInfo = new CloudMaterialBean();
            HVEMaterialInfo hveMaterialInfo = materialInfos.get(i);
            HVELocalMaterialInfo localMaterialInfo =
                HVEMaterialsManager.queryLocalMaterialById(hveMaterialInfo.getMaterialId());
            if (!StringUtil.isEmpty(localMaterialInfo.getMaterialPath())) {
                materialInfo.setLocalPath(localMaterialInfo.getMaterialPath());
            }

            materialInfo.setPreviewUrl(hveMaterialInfo.getPreviewUrl());
            materialInfo.setId(hveMaterialInfo.getMaterialId());
            materialInfo.setName(hveMaterialInfo.getMaterialName());

            list.add(materialInfo);
        }
        mFlowerMaterials.postValue(list);
    }

    public void downloadColumn(int previousPosition, int position, int dataPosition, CloudMaterialBean cutContent) {
        MaterialsDownloadInfo downloadCanvasInfo = new MaterialsDownloadInfo();
        downloadCanvasInfo.setPreviousPosition(previousPosition);
        downloadCanvasInfo.setDataPosition(dataPosition);
        downloadCanvasInfo.setPosition(position);
        downloadCanvasInfo.setContentId(cutContent.getId());
        downloadCanvasInfo.setMaterialBean(cutContent);

        HVEDownloadMaterialRequest request = new HVEDownloadMaterialRequest(cutContent.getId());
        HVEMaterialsManager.downloadMaterialById(request, new HVEDownloadMaterialListener() {
            @Override
            public void onSuccess(String file) {
                downloadCanvasInfo.setMaterialLocalPath(file);
                mFlowerDownloadSuccess.postValue(downloadCanvasInfo);
            }

            @Override
            public void onProgress(int progress) {
                downloadCanvasInfo.setProgress(progress);
                mDownloadProgress.postValue(downloadCanvasInfo);
            }

            @Override
            public void onFailed(Exception exception) {
                SmartLog.e(TAG, exception.getMessage());
                mDownloadFail.postValue(downloadCanvasInfo);
            }

            @Override
            public void onAlreadyDownload(String file) {
                downloadCanvasInfo.setMaterialLocalPath(file);
                mFlowerDownloadSuccess.postValue(downloadCanvasInfo);
                SmartLog.i(TAG, "onDownloadExists");
            }
        });
    }

    public MutableLiveData<List<CloudMaterialBean>> getPageData() {
        return mFlowerMaterials;
    }

    public MutableLiveData<String> getErrorString() {
        return errorString;
    }

    public MutableLiveData<String> getEmptyString() {
        return emptyString;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadSuccess() {
        return mFlowerDownloadSuccess;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadFail() {
        return mDownloadFail;
    }

    public MutableLiveData<MaterialsDownloadInfo> getDownloadProgress() {
        return mDownloadProgress;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    public MutableLiveData<String> getFontColumn() {
        return fontColumn;
    }

    public MutableLiveData<Boolean> getClear() {
        return clear;
    }

    public void clear(){
        clear.postValue(true);
    }
}
