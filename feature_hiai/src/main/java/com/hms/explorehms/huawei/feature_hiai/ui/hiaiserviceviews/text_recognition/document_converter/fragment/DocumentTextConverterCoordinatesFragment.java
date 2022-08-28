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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hiai.vision.visionkit.image.detector.DocCoordinates;

public class DocumentTextConverterCoordinatesFragment extends Fragment {

    private final DocCoordinates coordinates;
    private final Bitmap originalImage;
    private ImageView ivImage;

    public DocumentTextConverterCoordinatesFragment(DocCoordinates coordinates, Bitmap bitmap){
        this.coordinates = coordinates;
        this.originalImage = bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document_text_converter_coordinates_hiai, container, false);
        ivImage = view.findViewById(R.id.iv_document_text_converter_coordinates);
        MaterialTextView tvCoordinates = view.findViewById(R.id.tv_document_text_converter_coordinate_result_text);
        MaterialTextView tvWarn = view.findViewById(R.id.tv_document_text_converter_coordinate_warn);
        MaterialTextView tvImageLabel = view.findViewById(R.id.tv_document_text_converter_coordinates_image_image_kit);

        if(this.coordinates != null){
            drawRect();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String coordinateJson = gson.toJson(this.coordinates);

            tvCoordinates.setText(coordinateJson);
        }else{
            ivImage.setVisibility(View.GONE);
            tvImageLabel.setVisibility(View.GONE);
            tvWarn.setVisibility(View.VISIBLE);
            tvWarn.setText(getString(R.string.txt_document_text_converter_no_coordinates_hiai));
        }

        return view;
    }

    private void drawRect(){
        final Bitmap mutableBitmap = this.originalImage.copy(Bitmap.Config.ARGB_8888,true);

        Canvas canvas = new Canvas(mutableBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);

        canvas.drawRect(
                this.coordinates.getTopLeftCoordinate().x,
                this.coordinates.getTopLeftCoordinate().y,
                this.coordinates.getBottomRightCoordinate().x,
                this.coordinates.getBottomRightCoordinate().y,
                paint);

        Glide.with(this).load(mutableBitmap).into(ivImage);

        ivImage.setOnClickListener(v -> Util.showDialogImagePeekView(getActivity(),getContext(),ivImage));
    }
}
