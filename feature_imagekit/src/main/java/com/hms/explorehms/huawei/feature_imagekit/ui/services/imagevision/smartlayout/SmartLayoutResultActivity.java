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

package com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.smartlayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.application.AppSession;
import com.huawei.hms.image.vision.bean.ImageLayoutInfo;

import org.json.JSONException;

public class SmartLayoutResultActivity extends AppCompatActivity {

    private static final String TAG = "IMAGEKIT";
    private final ImageLayoutInfo imageLayoutInfo;
    private final Bitmap bitmap;
    private final Context context;
    private RelativeLayout relativeLayout;
    private ImageView imageView;

    public SmartLayoutResultActivity() {
        this.context = AppSession.getInstance().getImageLayoutResultContext();
        this.imageLayoutInfo = AppSession.getInstance().getImageLayoutInfo();
        this.bitmap = AppSession.getInstance().getImageLayoutBitmap();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_vision_smart_layout_result);

        initUI();
        setBackground();
        setView();
    }

    private void initUI() {
        relativeLayout = findViewById(R.id.rl_image_smart_layout_result_imagekit);
        imageView = findViewById(R.id.iv_image_vision_smart_layout_result_imagekit);
    }

    private void setBackground() {
        imageView.setBackground(new BitmapDrawable(context.getResources(), this.bitmap));
    }

    private void setView() {
        try {

            if (imageLayoutInfo.getViewGroup() != null) {
                if (imageLayoutInfo.getMaskView() != null) {
                    relativeLayout.addView(imageLayoutInfo.getMaskView());
                }
                imageLayoutInfo.getViewGroup().setX(imageLayoutInfo.getResponse().getInt("locationX"));
                imageLayoutInfo.getViewGroup().setY(imageLayoutInfo.getResponse().getInt("locationX"));
                relativeLayout.addView(imageLayoutInfo.getViewGroup());
                imageLayoutInfo.getViewGroup()
                        .measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                imageLayoutInfo.getViewGroup()
                        .layout(0, 0, imageLayoutInfo.getViewGroup().getMeasuredWidth(),
                                imageLayoutInfo.getViewGroup().getMeasuredHeight());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }
}
