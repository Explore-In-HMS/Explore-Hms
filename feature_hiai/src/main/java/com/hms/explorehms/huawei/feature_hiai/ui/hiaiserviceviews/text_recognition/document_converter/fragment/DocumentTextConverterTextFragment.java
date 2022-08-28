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
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.utils.hiai_service_utils.TextRecognitionUtils;
import com.huawei.hiai.vision.visionkit.text.Text;

public class DocumentTextConverterTextFragment extends Fragment {

    private final Text textResult;
    private final Bitmap originalImage;

    private ImageView imageView;


    public DocumentTextConverterTextFragment(Text textResult,Bitmap originalImage){
        this.textResult = textResult;
        this.originalImage = originalImage;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document_text_converter_text_hiai, container, false);
        imageView = view.findViewById(R.id.iv_document_text_converter_text);
        TextView tvText = view.findViewById(R.id.tv_document_text_converter_coordinate_text_result_text);
        TextView tvLanguage = view.findViewById(R.id.tv_document_text_converter_coordinate_text_result_language);
        TextView tvProbability = view.findViewById(R.id.tv_document_text_converter_coordinate_text_result_probability);

        drawRect();

        if(textResult.getValue() != null){
            tvText.setText(textResult.getValue());
        }else{
            tvText.setText(getString(R.string.txt_document_text_converter_no_result_hiai));
        }

        tvLanguage.setText(TextRecognitionUtils.getTextLanguages().get(textResult.getPageLanguage()));
        tvProbability.setText(String.valueOf(textResult.getProbability()));

        return view;
    }

    private void drawRect(){
        final Bitmap mutableBitmap = this.originalImage.copy(Bitmap.Config.ARGB_8888,true);

        Canvas canvas = new Canvas(mutableBitmap);

        boolean draw = false;

        if(textResult.getTextRect() != null){

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            canvas.drawRect(
                    textResult.getTextRect().left,
                    textResult.getTextRect().top,
                    textResult.getTextRect().right,
                    textResult.getTextRect().bottom,
                    paint);

            draw = true;
        }

        if(textResult.getCornerPoints() != null){
            Paint paintPoint = new Paint();
            paintPoint.setStyle(Paint.Style.STROKE);
            paintPoint.setColor(Color.GREEN);
            paintPoint.setStrokeWidth(30);

            for(Point p : textResult.getCornerPoints()){
                canvas.drawPoint(p.x,p.y,paintPoint);

                draw = true;
            }

            int x1 = textResult.getCornerPoints()[0].x;
            int y1 = textResult.getCornerPoints()[1].y;
            int x2 = textResult.getCornerPoints()[2].x;
            int y2 = textResult.getCornerPoints()[3].y;

            Rect rectArea = new Rect(x1,y1,x2,y2);

            Paint paintRect = new Paint();
            paintRect.setColor(Color.RED);
            paintRect.setStyle(Paint.Style.STROKE);
            paintRect.setStrokeWidth(10);

            canvas.drawRect(rectArea.left,rectArea.top,rectArea.right,rectArea.bottom,paintRect);

            draw = true;
        }

        if(draw){
            Glide.with(this).load(mutableBitmap).into(imageView);

            imageView.setOnClickListener(v -> Util.showDialogImagePeekView(getActivity(),getContext(),imageView));
        }
        
    }

}
