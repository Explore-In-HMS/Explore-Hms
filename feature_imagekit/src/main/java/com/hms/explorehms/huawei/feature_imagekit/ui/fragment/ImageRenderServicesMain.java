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

package com.hms.explorehms.huawei.feature_imagekit.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.ui.services.imagerender.ImageRenderActivity;
import com.google.android.material.button.MaterialButton;

public class ImageRenderServicesMain extends Fragment {

    private static final int PERMISSION_REQUEST = 99;

    private View view;

    private MaterialButton cvAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_image_render_imagekit, container, false);

        initUI();
        initListener();

        return view;
    }

    private void initUI() {
        cvAnimation = view.findViewById(R.id.btn_basic_and_advanced_animation_image_kit);
    }

    private void initListener() {

        cvAnimation.setOnClickListener(v -> isPermissionsGranted());
    }

    private void isPermissionsGranted() {
        if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            String[] permission = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            requestPermissions(permission, PERMISSION_REQUEST);
        } else {
            Intent intent = new Intent(getActivity(), ImageRenderActivity.class);
            startActivity(intent);
        }
    }


}
