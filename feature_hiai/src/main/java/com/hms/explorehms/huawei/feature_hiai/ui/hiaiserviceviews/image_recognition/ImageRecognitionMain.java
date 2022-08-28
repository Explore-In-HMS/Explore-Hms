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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.aestheticScore.AestheticsScoreActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.documentSkewCorrection.DocumentSkewCorrectionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.imageCategoryLabeling.ImageCategoryLabelingActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.imageSuperResolution.ImageSuperResolutionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.portraitSegmentation.PortraitSegmentationActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.sceneDetection.SceneDetectionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.semanticSegmentation.SemanticSegmentationActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.image_recognition.textImageSuperResolution.TextImageSuperResolutionActivity;

public class ImageRecognitionMain extends Fragment {

    private View view;

    private CardView cvAestheticScore;
    private CardView cvImageCategoryLabeling;
    private CardView cvImageSuperResolution;
    private CardView cvSceneDetection;
    private CardView cvDocumentSkewCorrection;
    private CardView cvTextImageSuperResolution;
    private CardView cvPortraitSegmentation;
    private CardView cvSemanticSegmentation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_image_recognition_hiai, container, false);

        initUI();
        initListener();

        return view;
    }

    private void initUI(){

        cvAestheticScore = view.findViewById(R.id.cv_aesthetics_score);
        cvImageCategoryLabeling = view.findViewById(R.id.cv_image_category_labeling);
        cvImageSuperResolution = view.findViewById(R.id.cv_image_super_resolution);
        cvSceneDetection = view.findViewById(R.id.cv_scene_detection);
        cvDocumentSkewCorrection = view.findViewById(R.id.cv_document_skew_correction);
        cvTextImageSuperResolution = view.findViewById(R.id.cv_text_image_super_resolution);
        cvPortraitSegmentation = view.findViewById(R.id.cv_portrait_segmentation);
        cvSemanticSegmentation = view.findViewById(R.id.cv_semantic_segmentation);
    }

    private void initListener(){
        cvAestheticScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AestheticsScoreActivity.class);
                startActivity(intent);
            }
        });

        cvImageCategoryLabeling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageCategoryLabelingActivity.class);
                startActivity(intent);
            }
        });

        cvImageSuperResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageSuperResolutionActivity.class);
                startActivity(intent);
            }
        });


        cvSceneDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SceneDetectionActivity.class);
                startActivity(intent);
            }
        });

        cvDocumentSkewCorrection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DocumentSkewCorrectionActivity.class);
                startActivity(intent);
            }
        });

        cvTextImageSuperResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TextImageSuperResolutionActivity.class);
                startActivity(intent);
            }
        });

        cvPortraitSegmentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PortraitSegmentationActivity.class);
                startActivity(intent);
            }
        });

        cvSemanticSegmentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SemanticSegmentationActivity.class);
                startActivity(intent);
            }
        });
    }
}
