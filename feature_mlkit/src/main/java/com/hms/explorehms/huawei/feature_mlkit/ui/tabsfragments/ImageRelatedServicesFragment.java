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
package com.hms.explorehms.huawei.feature_mlkit.ui.tabsfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.documentSkewCorrection.DocumentSkewCorrectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.formTableRecognition.FormTableRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageClassification.ImageClassificationActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSegmentation.ImageSegmentationActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSuperResolution.ImageSuperResolutionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.landmarkRecognition.LandmarkRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.objectDetection.ObjectDetectionCameraActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.productVisualSearch.ProductVisualSearchActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.sceneDetection.SceneDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.textImageSuperResolution.TextImageSuperResolutionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImageRelatedServicesFragment extends Fragment {

    private Unbinder unbinder;

    public ImageRelatedServicesFragment() {
        // Required empty public constructor
    }

    public static ImageRelatedServicesFragment newInstance() {
        return new ImageRelatedServicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_related_services, container, false);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.cv_img_related_imgCls, R.id.cv_img_related_obj_dtc, R.id.cv_img_related_lnd_rec,
            R.id.cv_img_related_img_sgm, R.id.cv_img_related_prd_vs, R.id.cv_img_related_img_sp_res,
            R.id.cv_img_related_doc_sc, R.id.imageView_table_form_rec,
            R.id.cv_img_related_text_img_sp_res, R.id.cv_img_related_scene_dtc})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_img_related_imgCls:
                Utils.startActivity(requireActivity(), ImageClassificationActivity.class);
                break;
            case R.id.cv_img_related_obj_dtc:
                Utils.startActivity(requireActivity(), ObjectDetectionCameraActivity.class);
                break;
            case R.id.cv_img_related_lnd_rec:
                Utils.startActivity(requireActivity(), LandmarkRecognitionActivity.class);
                break;
            case R.id.cv_img_related_img_sgm:
                Utils.startActivity(requireActivity(), ImageSegmentationActivity.class);
                break;
            case R.id.cv_img_related_prd_vs:
                Utils.startActivity(requireActivity(), ProductVisualSearchActivity.class);
                break;
            case R.id.cv_img_related_img_sp_res:
                Utils.startActivity(requireActivity(), ImageSuperResolutionActivity.class);
                break;
            case R.id.cv_img_related_doc_sc:
                Utils.startActivity(requireActivity(), DocumentSkewCorrectionActivity.class);
                break;
            case R.id.imageView_table_form_rec:
                Utils.startActivity(requireActivity(), FormTableRecognitionActivity.class);
                break;
            case R.id.cv_img_related_text_img_sp_res:
                Utils.startActivity(requireActivity(), TextImageSuperResolutionActivity.class);
                break;
            case R.id.cv_img_related_scene_dtc:
                Utils.startActivity(requireActivity(), SceneDetectionActivity.class);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}