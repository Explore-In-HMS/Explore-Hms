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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.document_converter.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.google.android.material.textview.MaterialTextView;

public class DocumentTextConverterRefineFragment extends Fragment {

    private final Bitmap refineBitmap;
    private ImageView ivRefineImage;

    public DocumentTextConverterRefineFragment(Bitmap refineBitmap){
        this.refineBitmap = refineBitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document_text_converter_refine_hiai, container, false);
        ivRefineImage = view.findViewById(R.id.iv_document_text_converter_refine);
        MaterialTextView tvWarn = view.findViewById(R.id.tv_document_text_converter_refine_warn);
        MaterialTextView tvImageLabel = view.findViewById(R.id.tv_document_text_converter_coordinates_image_image_kit);

        if(refineBitmap != null){
            Glide.with(this).load(refineBitmap).into(ivRefineImage);

            ivRefineImage.setOnClickListener(v -> Util.showDialogImagePeekView(getActivity(),getContext(),ivRefineImage));

        } else{
            ivRefineImage.setVisibility(View.GONE);
            tvImageLabel.setVisibility(View.GONE);
            tvWarn.setVisibility(View.VISIBLE);
            tvWarn.setText(getString(R.string.txt_document_text_converter_no_result_hiai));
        }

        return view;
    }
}
