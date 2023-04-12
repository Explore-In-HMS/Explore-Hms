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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition;

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
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter.DocumentTextConverterRecognitionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.general_text_recognition.GeneralTextRecognitionActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.table_recognition.TableRecognitionActivity;

public class TextRecognitionMain extends Fragment {

    private View view;

    private CardView cvGeneralTextRecognition;
    private CardView cvTableRecognition;
    private CardView cvDocumentConverter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_text_recognition_hiai, container, false);

        initUI();
        initListener();

        return view;
    }

    private void initUI() {

         cvGeneralTextRecognition = view.findViewById(R.id.cv_general_text_recognition);
         cvTableRecognition = view.findViewById(R.id.cv_table_recognition);
         cvDocumentConverter = view.findViewById(R.id.cv_document_converter);
    }

    private void initListener(){
        cvGeneralTextRecognition.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GeneralTextRecognitionActivity.class);
            startActivity(intent);
        });

        cvTableRecognition.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TableRecognitionActivity.class);
            startActivity(intent);
        });

        cvDocumentConverter.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DocumentTextConverterRecognitionActivity.class);
            startActivity(intent);
        });

    }
}
