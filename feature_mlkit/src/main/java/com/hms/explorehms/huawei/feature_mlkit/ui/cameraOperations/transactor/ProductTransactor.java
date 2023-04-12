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
package com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.cameraView.FrameMetadata;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ProductGraphic;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.productvisionsearch.MLProductVisionSearch;
import com.huawei.hms.mlsdk.productvisionsearch.cloud.MLRemoteProductVisionSearchAnalyzer;
import com.huawei.hms.mlsdk.productvisionsearch.cloud.MLRemoteProductVisionSearchAnalyzerSetting;

import java.util.List;

public class ProductTransactor extends BaseTransactor<List<MLProductVisionSearch>> {

    private static final String TAG = ProductTransactor.class.getSimpleName();

    private final MLRemoteProductVisionSearchAnalyzer detector;

    public ProductTransactor(Context context) {
        super();
        Utils.setApiKeyForRemoteMLApplication(context);
        this.detector = createCustomAnalyzer();
    }

    /**
     * You can createCustom MLRemoteProductVisionSearchAnalyzer types
     */
    public MLRemoteProductVisionSearchAnalyzer createCustomAnalyzer() {
        /* Method 1: Use default parameter settings.
            MLRemoteProductVisionSearchAnalyzer analyzer = MLAnalyzerFactory.getInstance().getRemoteProductVisionSearchAnalyzer();
         */

        // Method 2: Use customized parameter settings.
        /*
         * Set the maximum number of detection results. The default value is 20. The value ranges from 1 to 100.
         * Set the product set ID, which can be generated and obtained in AppGallery Connect.
         * //.setProductSetId("xxxxx")
         * Set the Site region. Currently, the following site regions are supported:
         * CHINA, EUROPE, AFILA, RUSSIA, GERMAN, SIANGAPORE and UNKNOWN
         * (The site region must be the same as the access site selected in AppGallery Connect.)
         *
         */
        MLRemoteProductVisionSearchAnalyzerSetting settings = new MLRemoteProductVisionSearchAnalyzerSetting.Factory()
                .setLargestNumOfReturns(12)
                .setRegion(MLRemoteProductVisionSearchAnalyzerSetting.REGION_DR_CHINA)
                /* Set the product set ID. (Contact mlkit@huawei.com to obtain the configuration guide.)
                 .setProductSetId(productSetId)
                 */
                .create();

        return MLAnalyzerFactory.getInstance().getRemoteProductVisionSearchAnalyzer(settings);
    }


    @Override
    public Task<List<MLProductVisionSearch>> detectInImage(MLFrame image) {
        return detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(Bitmap originalCameraImage, List<MLProductVisionSearch> productVisionSearchResults, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        if (productVisionSearchResults != null && !productVisionSearchResults.isEmpty()) {
            graphicOverlay.clear();
            for (MLProductVisionSearch productVisionSearch : productVisionSearchResults) {
                ProductGraphic productGraphic = new ProductGraphic(graphicOverlay, productVisionSearch.getProductList());
                graphicOverlay.addGraphic(productGraphic);
            }
            graphicOverlay.postInvalidate();
        }

    }

    @Override
    protected void onFailure(Exception e) {
        Log.e(TAG, "Landmark Recognition onFailure : " + e.getMessage(), e);
    }
}
