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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSegmentation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.StillImageSegmentationTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SegmentationWithCutOutOfObjectsActivity extends AppCompatActivity implements StillImageSegmentationTransactor.ImageSegmentationResultCallBack {

    //region variablesAndObjects
    private static final String TAG = SegmentationWithCutOutOfObjectsActivity.class.getSimpleName();

    private Uri takedImageUri;
    private Bitmap takedImageBitmap;

    private Uri takedBgImageUri;
    private Bitmap takedBgImageBitmap;

    // Portrait foreground image.
    private Bitmap foregroundImageBitmap;

    private Bitmap processedImageBitmap;

    private static final Integer MAX_WIDTH_OF_IMAGE = 1080;
    private static final Integer MAX_HEIGHT_OF_IMAGE = 1440;

    private MLImageSegmentationAnalyzer analyzer;
    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.live_overlay)
    GraphicOverlay graphicOverlay;

    @Nullable
    @BindView(R.id.iv_imageSegmentationCut)
    ImageView imageViewSegmentationCut;


    @Nullable
    @BindView(R.id.layoutSelectImg)
    ConstraintLayout layoutSelectImg;

    @Nullable
    @BindView(R.id.layoutCutImg)
    ConstraintLayout layoutCutImg;

    @Nullable
    @BindView(R.id.layoutReplaceImgBg)
    ConstraintLayout layoutReplaceImgBg;

    @Nullable
    @BindView(R.id.layoutSaveImg)
    ConstraintLayout layoutSaveImg;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    private static final int PERMISSION_CODE_STORAGE_FOR_REPLACE_BG_IMAGE = 2;
    private static final int PERMISSION_CODE_STORAGE_FOR_SAVE_IMAGE = 3;
    String[] permissionRequestStorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_REPLACE_BG_IMAGE = 22;


    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmentation_with_cutout_of_objects);

        unbinder =  ButterKnife.bind(this);

        setupToolbar();

        createImageSegmentationSettingAndAnalyzer();
    }

    @OnClick({R.id.layoutSelectImg, R.id.layoutCutImg, R.id.layoutReplaceImgBg, R.id.layoutSaveImg, R.id.tv_clearViews, R.id.ivInfo})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.layoutSelectImg:
                ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.layoutCutImg:
                if (analyzer == null) {
                    createImageSegmentationSettingAndAnalyzer();
                }
                createImageCutOutSegmentationTransactor();
                break;
            case R.id.layoutReplaceImgBg:
                if (takedImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must first select an image!");
                } else if (foregroundImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must cut selected image and get foreground image before!");
                } else {
                    ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE_FOR_REPLACE_BG_IMAGE);
                }
                break;
            case R.id.layoutSaveImg:
                if (processedImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must process an image to save!");
                } else {
                    ActivityCompat.requestPermissions(this, permissionRequestStorage, PERMISSION_CODE_STORAGE_FOR_SAVE_IMAGE);
                }
                break;
            case R.id.tv_clearViews:
                if (takedImageBitmap == null || takedBgImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must process an image before clear views!");
                } else {
                    clearViews();
                }
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

    private void clearViews() {
        takedImageUri = null;
        takedBgImageUri = null;
        takedImageBitmap = null;
        takedBgImageBitmap = null;
        foregroundImageBitmap = null;
        processedImageBitmap = null;
        if (graphicOverlay != null) {
            graphicOverlay.clear();
        }
        imageViewSegmentationCut.setBackground(null);
        imageViewSegmentationCut.setImageResource(R.drawable.icon_segmentation);
    }

    private void loadOriginImageFromUriAndUpdateView(Uri uri) {
        Log.i(TAG, "loadOriginImageFromUriAndUpdateView : imageViewSegmentationCut. : " + (imageViewSegmentationCut).getWidth() + "-" + (imageViewSegmentationCut).getHeight());
        Log.i(TAG, "loadOriginImageFromUriAndUpdateView : imageViewSegmentationCut.getParent : " + ((View) imageViewSegmentationCut.getParent()).getWidth() + "-" + ((View) imageViewSegmentationCut.getParent()).getHeight());
        // Clear the overlay first.
        graphicOverlay.clear();
        takedImageUri = uri;
        takedImageBitmap = BitmapUtils.getBitmapFromUriWithSize(SegmentationWithCutOutOfObjectsActivity.this, takedImageUri, MAX_WIDTH_OF_IMAGE, MAX_HEIGHT_OF_IMAGE);
        imageViewSegmentationCut.setImageBitmap(takedImageBitmap);
    }


    private void loadBackgroundImageFromUriAndUpdateView(Uri uri) {
        Log.i(TAG, "loadBackgroundImageFromUriAndUpdateView : takedBgImageUri : " + takedBgImageUri);
        takedBgImageUri = uri;
        //Important method --> refreshOriginImageAndUpdateView();
        takedBgImageBitmap = BitmapUtils.getBitmapFromUriWithSize(SegmentationWithCutOutOfObjectsActivity.this, takedBgImageUri, MAX_WIDTH_OF_IMAGE, MAX_HEIGHT_OF_IMAGE);
        changeBackground(takedBgImageBitmap);
    }

    // check it and remove
    private void refreshOriginImageAndUpdateView() {
        if (takedImageUri == null) {
            Utils.showToastMessage(getApplicationContext(), "Selected Origin Image Data And Uri is NULL!");
        } else {
            // Clear the overlay first.
            graphicOverlay.clear();
            takedImageBitmap = BitmapUtils.getBitmapFromUriWithSize(SegmentationWithCutOutOfObjectsActivity.this, takedImageUri, MAX_WIDTH_OF_IMAGE, MAX_HEIGHT_OF_IMAGE);
            imageViewSegmentationCut.setImageBitmap(takedImageBitmap);
        }
    }

    private void changeBackground(Bitmap backgroundBitmap) {
        if (foregroundImageBitmap == null && backgroundBitmap == null) {
            Log.d(TAG, "changeBackground foregroundImageBitmap or backgroundBitmap is NULL!");
            Utils.showToastMessage(getApplicationContext(), "You must process an image to Change Background!");
        } else {
            BitmapDrawable drawable = new BitmapDrawable(backgroundBitmap);
            imageViewSegmentationCut.setDrawingCacheEnabled(true);
            imageViewSegmentationCut.setBackground(drawable);
            imageViewSegmentationCut.setImageBitmap(foregroundImageBitmap);

            processedImageBitmap = Bitmap.createBitmap(imageViewSegmentationCut.getDrawingCache());
            imageViewSegmentationCut.setDrawingCacheEnabled(false);
        }
        Log.i(TAG, "changeBackground : imageViewSegmentationCut. : " + (imageViewSegmentationCut).getWidth() + "-" + (imageViewSegmentationCut).getHeight());
        Log.i(TAG, "changeBackground : imageViewSegmentationCut.getParent : " + ((View) imageViewSegmentationCut.getParent()).getWidth() + "-" + ((View) imageViewSegmentationCut.getParent()).getHeight());
    }


    @Override
    public void callResultBitmap(Bitmap bitmap) {
        Log.d(TAG, "callResultBitmap processedImageBitmap = bitmap from ImageSegmentationResultCallBack");
        processedImageBitmap = bitmap;
    }


    private void createImageSegmentationSettingAndAnalyzer() {
        MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory().setAnalyzerType(MLImageSegmentationSetting.BODY_SEG).create();
        analyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting);
    }

    /**
     * create Image CutOut Segmentation Transactor Analyzer
     * get foregroundImageBitmap and processedImageBitmap with asyncAnalyseFrame
     */
    private void createImageCutOutSegmentationTransactor() {
        if (takedImageBitmap == null) {
            Utils.showToastMessage(getApplicationContext(), "You must select an image to Cut!");
        } else {
            MLFrame mlFrame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();
            analyzer.asyncAnalyseFrame(mlFrame)
                    .addOnSuccessListener(mlImageSegmentationResults -> {
                        // Transacting logic for segment success.
                        if (mlImageSegmentationResults != null) {
                            Log.d(TAG, "createImageTransactorAndCutOut : Image Segmentation CutOut OnSuccess");
                            Utils.showToastMessage(getApplicationContext(), "Image Segmentation CutOut OnSuccess");

                            foregroundImageBitmap = mlImageSegmentationResults.getForeground();
                            imageViewSegmentationCut.setImageBitmap(foregroundImageBitmap);
                            processedImageBitmap = ((BitmapDrawable) imageViewSegmentationCut.getDrawable()).getBitmap();
                        } else {
                            Log.e(TAG, "createImageTransactorAndCutOut : Image Segmentation Results is NULL!");
                            Utils.showToastMessage(getApplicationContext(), "Image Segmentation Results is NULL!");
                        }
                    }).addOnFailureListener(e -> {
                Log.e(TAG, "createImageTransactorAndCutOut : onFailure : " + e.getMessage(), e);
                Utils.showToastMessage(getApplicationContext(), "Image Segmentation Results onFailure : " + e.getMessage());
            });
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE);
            } else {
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        getString(R.string.image_segmentation_storage_permission),
                        getString(R.string.yes_go), getString(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_REPLACE_BG_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_REPLACE_BG_IMAGE);
            } else {
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        getString(R.string.background_image_segmentation_storage_permission),
                        getString(R.string.yes_go), getString(R.string.cancel));
            }
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_SAVE_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (processedImageBitmap == null) {
                    Utils.showToastMessage(getApplicationContext(), "You must process an image to save!");
                } else {
                    BitmapUtils.saveToAlbum(processedImageBitmap, getApplicationContext());
                    Utils.showToastMessage(getApplicationContext(), "Processed image saving to gallery.");
                }
            } else {
                DialogUtils.showDialogPermissionWarning(this,
                        getString(R.string.need_storage_permission),
                        getString(R.string.permission_settings_allow),
                        R.drawable.icon_folder,
                        getString(R.string.save_image_segmentation_storage_permission),
                        getString(R.string.yes_go), getString(R.string.cancel));
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
                        loadOriginImageFromUriAndUpdateView(data.getData());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onActivityResult activityIntentCodeStorage Exception for data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "Exception for data.getData : " + e.getMessage());
                }
            }

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_REPLACE_BG_IMAGE) {
                try {
                    if (data == null) {
                        Utils.showToastMessage(getApplicationContext(), "Selected BG Image Data And Uri is NULL!");
                    } else {
                        loadBackgroundImageFromUriAndUpdateView(data.getData());
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onActivityResult activityIntentCodeStorage Exception for data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "Exception for data.getData : " + e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (analyzer != null) {
            try {
                analyzer.stop();
            } catch (IOException e) {
                Log.e(TAG, "Stop analyzer failed : " + e.getMessage(), e);
            }
        }
        takedImageUri = null;
        takedBgImageUri = null;
        BitmapUtils.recycleBitmap(takedImageBitmap, takedBgImageBitmap, foregroundImageBitmap, processedImageBitmap);
        if (graphicOverlay != null) {
            graphicOverlay.clear();
            graphicOverlay = null;
        }
    }

}