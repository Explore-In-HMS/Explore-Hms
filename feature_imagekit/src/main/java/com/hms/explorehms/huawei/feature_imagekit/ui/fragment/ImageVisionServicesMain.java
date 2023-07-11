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

package com.hms.explorehms.huawei.feature_imagekit.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.FilterActivity;
import com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.ImageCroppingActivity;
import com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.StickerActivity;
import com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.ThemeTaggingActivity;
import com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.smartlayout.SmartLayoutActivity;
import com.google.android.material.button.MaterialButton;

public class ImageVisionServicesMain extends Fragment {

    private static final int PERMISSION_REQUEST = 99;

    private View view;

    private MaterialButton cvFilter;
    private MaterialButton cvSmartLayout;
    private MaterialButton cvThemeTagging;
    private MaterialButton cvSticker;
    private MaterialButton cvImageCropping;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_image_vision_imagekit, container, false);

        initUI();
        initListener();

        return view;
    }

    private void initUI() {
        cvFilter = view.findViewById(R.id.tv_image_vision_filter_imagekit);
        cvSmartLayout = view.findViewById(R.id.tv_image_vision_smart_layout_imagekit);
        cvThemeTagging = view.findViewById(R.id.tv_image_vision_theme_tagging_imagekit);
        cvSticker = view.findViewById(R.id.tv_image_vision_sticker_service_imagekit);
        cvImageCropping = view.findViewById(R.id.tv_image_vision_image_cropping_imagekit);
    }

    private void initListener() {

        cvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermissionsGranted(FilterActivity.class);
            }
        });

        cvSmartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermissionsGranted(SmartLayoutActivity.class);
            }
        });

        cvThemeTagging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermissionsGranted(ThemeTaggingActivity.class);
            }
        });

        cvSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermissionsGranted(StickerActivity.class);
            }
        });

        cvImageCropping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPermissionsGranted(ImageCroppingActivity.class);
            }
        });
    }

    private void isPermissionsGranted(Class<?> cls) {
        if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            String[] permission = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            requestPermissions(permission, PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(getActivity(), cls);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST && grantResults.length != 0 &&
                (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(getContext(), "Please Allow Permissions to Use Image Kit Services", Toast.LENGTH_LONG).show();
        }

    }

}
