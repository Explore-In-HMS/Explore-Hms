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
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.face3d.Live3DActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceDetection.FaceDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.faceVerification.FaceVerificationActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.gesture.GestureMainActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.handKeyPointDetection.HandKeyPointDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.livenessDetection.LivenessDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.faceAndBodyRelated.skeletonDetection.SkeletonDetectionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FaceAndBodyRelatedServicesFragment extends Fragment {

    private Unbinder unbinder;

    public FaceAndBodyRelatedServicesFragment() {
        // Required empty public constructor
    }

    public static FaceAndBodyRelatedServicesFragment newInstance() {
        return new FaceAndBodyRelatedServicesFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_face_and_body_related_services, container, false);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.cv_face_body_related_fd, R.id.cv_face_body_related_skltd, R.id.cv_face_body_related_lvd, R.id.cv_face_body_related_hkd,R.id.cv_face_body_related_3d,R.id.cv_face_body_related_fv,R.id.cv_face_body_related_gesture})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.cv_face_body_related_fd:
                Utils.startActivity(requireActivity(), FaceDetectionActivity.class);
                break;
            case R.id.cv_face_body_related_skltd:
                Utils.startActivity(requireActivity(), SkeletonDetectionActivity.class);
                break;
            case R.id.cv_face_body_related_lvd:
                Utils.startActivity(requireActivity(), LivenessDetectionActivity.class);
                break;
            case R.id.cv_face_body_related_hkd:
                Utils.startActivity(requireActivity(), HandKeyPointDetectionActivity.class);
                break;
            case R.id.cv_face_body_related_3d:
                Utils.startActivity(requireActivity(), Live3DActivity.class);
                break;
            case R.id.cv_face_body_related_fv:
                Utils.startActivity(requireActivity(), FaceVerificationActivity.class);
                break;
            case R.id.cv_face_body_related_gesture:
                Utils.startActivity(requireActivity(), GestureMainActivity.class);
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