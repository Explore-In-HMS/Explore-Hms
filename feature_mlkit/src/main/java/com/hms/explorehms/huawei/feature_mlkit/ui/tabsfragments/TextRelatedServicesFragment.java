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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.bankCardRecognition.BankCardRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.docRecognition.DocumentRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.generalCardRecognition.GeneralCardRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.idCardRecognition.IDCardActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.textEmbedding.TextEmbeddingActivity;
import com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.textRelated.textRecognition.TextRecognitionActivity;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TextRelatedServicesFragment extends Fragment {

    //region variables
    private static final String TAG = TextRelatedServicesFragment.class.getSimpleName();

    private Unbinder unbinder;

    //endregion

    public TextRelatedServicesFragment() {
        // Required empty public constructor
    }

    public static TextRelatedServicesFragment newInstance() {
        return new TextRelatedServicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_related_services, container, false);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick({R.id.cv_text_related_ter, R.id.cv_text_related_dor, R.id.cv_text_related_bcr,
            R.id.cv_text_related_gcr, R.id.cv_text_related_text_embed,R.id.cv_text_related_icr})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_text_related_ter:
                Log.d(TAG, "onClick : go To TextRecognitionActivity");
                Utils.startActivity(requireActivity(), TextRecognitionActivity.class);
                break;
            case R.id.cv_text_related_dor:
                Log.d(TAG, "onClick : go To DocumentRecognitionActivity");
                Utils.startActivity(requireActivity(), DocumentRecognitionActivity.class);
                break;
            case R.id.cv_text_related_bcr:
                Utils.startActivity(requireActivity(), BankCardRecognitionActivity.class);
                break;
            case R.id.cv_text_related_gcr:
                Utils.startActivity(requireActivity(), GeneralCardRecognitionActivity.class);
                break;
            case R.id.cv_text_related_text_embed:
                Utils.startActivity(requireActivity(), TextEmbeddingActivity.class);
                break;
            case R.id.cv_text_related_icr:
                Utils.startActivity(requireActivity(), IDCardActivity.class);
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