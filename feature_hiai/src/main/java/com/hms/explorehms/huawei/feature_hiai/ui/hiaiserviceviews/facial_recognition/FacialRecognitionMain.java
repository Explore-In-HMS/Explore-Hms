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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition;

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
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.face_attributes.FaceAttributesActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.face_detection.FaceDetectionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.face_orientation_recognition.FaceOrientationRecognitionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.face_parsing.FaceParsingActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.facial_comparison.FacialComparisionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.facial_recognition.facial_feature_detection.FacialFeatureDetectionActivity;

public class FacialRecognitionMain extends Fragment {

    private View view;

    private CardView cvFacialComparision;
    private CardView cvFaceDetection;
    private CardView cvFaceParsing;
    private CardView cvFaceAttributes;
    private CardView cvFaceOrientationRecognation;
    private CardView cvFacialFeatureDetection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_facial_recognition_hiai, container, false);

        initUI();
        initListener();

        return view;
    }

    private void initUI(){
        cvFacialComparision = view.findViewById(R.id.cv_facial_comparison);
        cvFaceDetection = view.findViewById(R.id.cv_face_detection);
        cvFaceParsing = view.findViewById(R.id.cv_face_parsing);
        cvFaceAttributes = view.findViewById(R.id.cv_face_attributes);
        cvFaceOrientationRecognation = view.findViewById(R.id.cv_face_orientation_recog);
        cvFacialFeatureDetection = view.findViewById(R.id.cv_facial_feature_detection);
    }

    private void initListener(){

        cvFacialComparision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FacialComparisionActivity.class);
                startActivity(intent);
            }
        });

        cvFaceDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceDetectionActivity.class);
                startActivity(intent);
            }
        });

        cvFaceParsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceParsingActivity.class);
                startActivity(intent);
            }
        });

        cvFaceAttributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceAttributesActivity.class);
                startActivity(intent);
            }
        });

        cvFaceOrientationRecognation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceOrientationRecognitionActivity.class);
                startActivity(intent);
            }
        });

        cvFacialFeatureDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FacialFeatureDetectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
