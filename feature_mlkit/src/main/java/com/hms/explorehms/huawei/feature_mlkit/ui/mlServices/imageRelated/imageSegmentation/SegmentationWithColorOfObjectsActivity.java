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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSegmentation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ImageTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.StillImageSegmentationTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SegmentationWithColorOfObjectsActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = SegmentationWithColorOfObjectsActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final String[] SGM_CTG_EN = {"Background", "People", "Sky", "Plant", "Food", "Animal", "Architecture", "Flower", "Water", "Beach", "Hill"};

    private Uri takedImageUri;
    private Bitmap takedImageBitmap;

    private int imageMaxWidth;
    private int imageMaxHeight;

    private ImageTransactor imageTransactor;
    private StillImageSegmentationTransactor tempTransactor;

    // indicator default color
    private int colorValue = Color.MAGENTA;

    private boolean isLandScape;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;

    @Nullable
    @BindView(R.id.iv_imageSegmentationColor)
    ImageView imageViewSegmentationColor;

    @Nullable
    @BindView(R.id.layoutObjectCategories)
    LinearLayout objectCategoriesLayout;

    @Nullable
    @BindView(R.id.seekBarColorSelector)
    SeekbarColorSelector seekBarColorSelector;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_segmentation_with_color_of_objects);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        isLandScape = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        initObjectCategoriesLayoutAndTheirMLTransactors();

        selectAndPickImage();
    }


    @OnClick({R.id.iv_selectImageToSgm, R.id.ivInfo})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.iv_selectImageToSgm:
                selectAndPickImage();
                break;
            case R.id.ivInfo:
                Utils.openWebPage(this, getResources().getString(R.string.link_irs_is));
                break;
            default:
                Log.i(TAG,getString(R.string.default_text));
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void selectAndPickImage() {
        ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
    }

    private void initObjectCategoriesLayoutAndTheirMLTransactors() {
        String[] categories = SGM_CTG_EN;

        MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory().setAnalyzerType(MLImageSegmentationSetting.IMAGE_SEG).create();

        for (int i = 0; i < categories.length; i++) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_segmentation_object, objectCategoriesLayout, false);
            TextView textView = view.findViewById(R.id.tvSegmentationObjectItem);
            textView.setText(categories[i]);
            objectCategoriesLayout.addView(view);
            final int index = i;
            textView.setOnClickListener(v -> {
                if (takedImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must first select an image!");
                } else {
                    tempTransactor = new StillImageSegmentationTransactor(setting, takedImageBitmap, imageViewSegmentationColor, index);
                    tempTransactor.setColor(colorValue);
                    imageTransactor = tempTransactor;
                    runOnUiThread(() -> imageTransactor.process(takedImageBitmap, graphicOverlay));
                }
            });
        }


        // Color picker settings.
        seekBarColorSelector.initData();
        seekBarColorSelector.setColors(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.WHITE);
        seekBarColorSelector.setOnColorSelectorChangeListener(new SeekbarColorSelector.OnColorSelectorChangeListener() {
            @Override
            public void onColorChanged(SeekbarColorSelector picker, int color) {
                colorValue = color;
                Log.d(TAG, "seekBarColorSelector : onColorChanged : " + colorValue);
            }

            @Override
            public void onStartColorSelect(SeekbarColorSelector picker) {
                //This method will be triggered when color is select section is started.
            }

            @Override
            public void onStopColorSelect(SeekbarColorSelector picker) {
                if (takedImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must first select an image!");
                } else {
                    if (tempTransactor == null) {
                        Utils.showToastMessage(getApplicationContext(), "You must first select a segmentation Object!");
                    } else {
                        tempTransactor.setColor(colorValue);
                        imageTransactor = tempTransactor;
                        runOnUiThread(() -> imageTransactor.process(takedImageBitmap, graphicOverlay));
                    }
                }
            }
        });

        seekBarColorSelector.post(() -> Log.d(TAG, "seekBarColorSelector : post Runnable... "));

    }

    private void loadImageFromUriAndUpdateView(Uri uri) {
        takedImageUri = uri;
        // to get bitmap with uri from intent data
        // Bitmap --> bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
        takedImageBitmap = BitmapUtils.getBitmapFromUriWithSize(SegmentationWithColorOfObjectsActivity.this, takedImageUri, getMaxWidthOfImage(), getMaxHeightOfImage());
        imageViewSegmentationColor.setImageBitmap(takedImageBitmap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE);
            } else {
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use Image Segmentation Object Color without Storage Permission!",
                        "YES GO", "CANCEL");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (resultCode == 0) {
            Log.w(TAG, "onActivityResult : onActivityResult No files selected!");
            Utils.showToastMessage(getApplicationContext(), "onActivityResult No files selected!");
        } else {
            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE) {
                try {
                    if (data == null) {
                        Utils.showToastMessage(getApplicationContext(), "Selected Image Data And Uri is NULL!");
                    } else {
                        loadImageFromUriAndUpdateView(data.getData());
                    }
                } catch (Exception e) {
                    Log.i(TAG, "onActivityResult activityIntentCodeStorage Exception for data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "Exception for data.getData : " + e.getMessage());
                }
            }
        }
    }


    private void doCleanOperators() {
        if (takedImageBitmap != null) {
            BitmapUtils.recycleBitmap(takedImageBitmap);
            takedImageUri = null;
        }
        if (imageTransactor != null) {
            imageTransactor.stop();
            imageTransactor = null;
        }
        if (graphicOverlay != null) {
            graphicOverlay.clear();
            graphicOverlay = null;
        }
    }


    private int getMaxWidthOfImage() {
        if (imageMaxWidth == 0) {
            if (isLandScape) {
                imageMaxWidth = ((View) imageViewSegmentationColor.getParent()).getHeight();
            } else {
                imageMaxWidth = ((View) imageViewSegmentationColor.getParent()).getWidth();
            }
        }
        return imageMaxWidth;
    }

    private int getMaxHeightOfImage() {
        if (imageMaxHeight == 0) {
            if (isLandScape) {
                imageMaxHeight = ((View) imageViewSegmentationColor.getParent()).getWidth();
            } else {
                imageMaxHeight = ((View) imageViewSegmentationColor.getParent()).getHeight();
            }
        }
        return imageMaxHeight;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        doCleanOperators();
    }


}