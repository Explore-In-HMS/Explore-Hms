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

public class DocumentTextConverterSuperResolutionFragment extends Fragment {

    private final Bitmap superResBitmap;

    private ImageView imageView;

    public DocumentTextConverterSuperResolutionFragment(Bitmap superResBitmap){
        this.superResBitmap = superResBitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document_text_converter_super_resolution_hiai, container, false);
        View viewSeperator = view.findViewById(R.id.view_document_text_converter_refine);
        imageView = view.findViewById(R.id.iv_document_text_converter_super_resolution);
        MaterialTextView tvWarn = view.findViewById(R.id.tv_document_text_converter_super_res_warn);
        MaterialTextView tvImageLabel = view.findViewById(R.id.tv_document_text_converter_coordinates_image_image_kit);

        if(superResBitmap != null){
            Glide.with(this).load(superResBitmap).into(imageView);

            imageView.setOnClickListener(v -> Util.showDialogImagePeekView(getActivity(),getContext(),imageView));
        }else{
            imageView.setVisibility(View.GONE);
            viewSeperator.setVisibility(View.GONE);
            tvImageLabel.setVisibility(View.GONE);
            tvWarn.setText(getString(R.string.txt_document_text_converter_no_result_hiai));
            tvWarn.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
