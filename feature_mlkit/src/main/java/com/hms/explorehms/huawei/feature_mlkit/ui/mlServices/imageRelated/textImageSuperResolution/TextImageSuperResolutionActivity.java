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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.textImageSuperResolution;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.textimagesuperresolution.MLTextImageSuperResolution;
import com.huawei.hms.mlsdk.textimagesuperresolution.MLTextImageSuperResolutionAnalyzer;
import com.huawei.hms.mlsdk.textimagesuperresolution.MLTextImageSuperResolutionAnalyzerFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class TextImageSuperResolutionActivity extends AppCompatActivity {


    //region variablesAndObjects
    private static final String TAG = TextImageSuperResolutionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private MLTextImageSuperResolutionAnalyzer analyzer;

    private static final int IMAGE_MAX_SIZE = 1024;

    private static final int AD_JUST_OPTION_3_X = 0;
    private static final int AD_JUST_OPTION_ORG = 1;

    private int selectedAdjustOption = AD_JUST_OPTION_3_X;

    private Uri takedImageUri;
    private Bitmap sourceImageBitmap;
    private Bitmap destinationImageBitmap;

    private final List<ImageView> adjustSelectImageList = new ArrayList<>();

    @Nullable
    @BindView(R.id.iv_tisr_source_image)
    ImageView imageViewIsrSourceImage;

    @Nullable
    @BindView(R.id.tv_image_size_info)
    TextView tvImageSizeInfo;

    @Nullable
    @BindView(R.id.iv_tisr_destination_image)
    ImageViewTouch imageViewIsrDestinationImage;

    @Nullable
    @BindView(R.id.ll_adjust_setting)
    LinearLayout layoutAdjustOptions;

    @Nullable
    @BindView(R.id.ll_3x_adjust)
    LinearLayout layoutAdjust3X;

    @Nullable
    @BindView(R.id.iv_adjust_3x)
    ImageView ivAdjust3x;

    @Nullable
    @BindView(R.id.ll_original_adjust)
    LinearLayout layoutAdjustOriginal;

    @Nullable
    @BindView(R.id.iv_adjust_original)
    ImageView ivAdjustOrg;

    @Nullable
    @BindView(R.id.iv_tisr_select_image)
    ImageView imageViewSelectImage;

    @Nullable
    @BindView(R.id.iv_tisr_adjust)
    ImageView imageViewSetAdjust;

    @Nullable
    @BindView(R.id.iv_tisr_help)
    ImageView imageViewShowHelpHint;

    @Nullable
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private static final int PERM_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permReqForStorageForSelectImage = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int ACT_RES_CODE_STORAGE_FOR_SELECT_IMAGE = 2;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.activity_text_image_super_resolution);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createTextImageSuperResolutionAnalyzer();

        adjustSelectImageList.add(ivAdjust3x);
        adjustSelectImageList.add(ivAdjustOrg);

        //ActivityCompat.requestPermissions(this, permReqForStorageForSelectImage, permCodeStorageForSelectImage);
        adjustDefaultImageAndAnalyse();

    }


    @OnClick({R.id.ll_3x_adjust, R.id.ll_original_adjust, R.id.iv_tisr_select_image, R.id.iv_tisr_adjust, R.id.iv_tisr_help})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.ll_3x_adjust:
                changeSelectedAdjustOption(AD_JUST_OPTION_3_X);
                break;
            case R.id.ll_original_adjust:
                changeSelectedAdjustOption(AD_JUST_OPTION_ORG);
                break;
            case R.id.iv_tisr_select_image:
                showProgress();
                ActivityCompat.requestPermissions(this, permReqForStorageForSelectImage, PERM_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.iv_tisr_adjust:
                showOrHideAdjustOptionsLayout();
                break;
            case R.id.iv_tisr_help:
                showHelpInformationTipsDialog();
                break;
            default:
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_tisr));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showHelpInformationTipsDialog() {
        DialogUtils.showInformationTipsDialog(
                TextImageSuperResolutionActivity.this,
                getResources().getString(R.string.text_img_sp_res_s),
                getResources().getString(R.string.tisr_tips_content),
                R.drawable.icons_information,
                getResources().getString(R.string.dialog_text_show_source_page),
                getResources().getString(R.string.dialog_text_cancel),
                (positiveListener) -> Utils.openWebPage(this, getResources().getString(R.string.link_irs_tisr)));
    }


    public void createTextImageSuperResolutionAnalyzer() {
        analyzer = MLTextImageSuperResolutionAnalyzerFactory.getInstance().getTextImageSuperResolutionAnalyzer();
    }

    private void changeSelectedAdjustOption(int index) {
        if (takedImageUri == null) {
            Utils.showToastMessage(getApplicationContext(), "Please Select An Image Before Chang AdjustOption");
        } else {
            selectedAdjustOption = index;
            setSelectedAdjustViewPoint(selectedAdjustOption);
            analyseTextImageSuperResolution(false);
        }
    }

    private void setSelectedAdjustViewPoint(int position) {
        for (int i = 0; i < adjustSelectImageList.size(); i++) {
            if (i == position) {
                adjustSelectImageList.get(i).setBackgroundResource(R.drawable.icon_bg_circle_blue);
            } else {
                adjustSelectImageList.get(i).setBackgroundResource(R.drawable.icon_bg_circle_gray);
            }
        }
    }

    private void showOrHideAdjustOptionsLayout() {
        if (layoutAdjustOptions.getVisibility() == View.VISIBLE) {
            layoutAdjustOptions.setVisibility(View.GONE);
        } else {
            layoutAdjustOptions.setVisibility(View.VISIBLE);
        }

    }

    private void adjustDefaultImageAndAnalyse() {
        imageViewIsrSourceImage.setImageResource(R.drawable.icon_tisr_ml);
        sourceImageBitmap = ((BitmapDrawable) imageViewIsrSourceImage.getDrawable()).getBitmap();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(TextImageSuperResolutionActivity.this, sourceImageBitmap);
        analyseTextImageSuperResolution(true);
    }

    /**
     * Super-Resolution with Asynchronous method
     * <p>
     * The text on an old paper document may be gradually blurred and difficult to identify.
     * In this case, you can take a picture of the text and use this service
     * to improve the definition of the text in the image so that the text can be recognized and stored.
     *
     * @param isReload : true = getting new image. false = different adjust will be used for previously set image
     */
    private void analyseTextImageSuperResolution(boolean isReload) {

        if (isReload) {
            sourceImageBitmap = BitmapUtils.getBitmapFromUriWithSize(this, takedImageUri, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
            setImageViewWithBitmap(imageViewIsrSourceImage, sourceImageBitmap);
            Log.d(TAG, "TextImageSuperResolution get new source imageBitmap and setViews");
        }

        if (selectedAdjustOption == AD_JUST_OPTION_ORG) {
            Log.d(TAG, "TextImageSuperResolution just change image with adjustOption");
            setImageViewWithBitmap(imageViewIsrDestinationImage, sourceImageBitmap);
            setImageSizeInfo(sourceImageBitmap.getWidth(), sourceImageBitmap.getHeight());
            displaySuccessAnalyseResults("TextImageSuperResolution image adjust option changed with original");
            return;
        }

        // Create an MLFrame by using the bitmap.
        MLFrame frame = MLFrame.fromBitmap(sourceImageBitmap);
        // another method
        //MLFrame frame = new MLFrame.Creator().setBitmap(sourceImageBitmap).create();

        Task<MLTextImageSuperResolution> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(result -> {
            Log.d(TAG, "TextImageSuperResolution onSuccess : getting resultBitmap and setViews.");

            destinationImageBitmap = result.getBitmap();

            setImageViewWithBitmap(imageViewIsrDestinationImage, destinationImageBitmap);
            setImageSizeInfo(destinationImageBitmap.getWidth(), destinationImageBitmap.getHeight());

            displaySuccessAnalyseResults("TextImageSuperResolution onSuccess.");

        }).addOnFailureListener(e -> {
            Log.e(TAG, "TextImageSuperResolution onFailure : " + e.getMessage(), e);
            Utils.showToastMessage(this, "TextImageSuperResolution Exception : " + e.getMessage());
            displayFailureAnalyseResults("TextImageSuperResolution onFailure : " + e.getMessage());
        });

    }

    private void setImageViewWithBitmap(final ImageView imageView, final Bitmap bitmap) {
        TextImageSuperResolutionActivity.this.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
    }


    private void setImageSizeInfo(final int width, final int height) {
        String resultBuilder = "width: " + width + "px      height: " + height + "px";
        runOnUiThread(() -> tvImageSizeInfo.setText(resultBuilder));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (requestCode == PERM_CODE_STORAGE_FOR_SELECT_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACT_RES_CODE_STORAGE_FOR_SELECT_IMAGE);
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        "Would You Like To Go To Permission Settings To Allow?",
                        R.drawable.icon_folder,
                        "You can not use Text Image Super Resolution without Storage Permission!",
                        "YES GO", "CANCEL");
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : requestCode : " + requestCode + " resultCode : " + resultCode);
        if (resultCode == 0) {
            Log.w(TAG, "onActivityResult : onActivityResult No any data detected");
            Utils.showToastMessage(getApplicationContext(), "onActivityResult No any data detected");
            hideProgress();
        } else {
            if (requestCode == ACT_RES_CODE_STORAGE_FOR_SELECT_IMAGE) {
                try {
                    if (data == null) {
                        Utils.showToastMessage(getApplicationContext(), "Selected Image Data And Uri is NULL!");
                    } else {
                        takedImageUri = data.getData();
                        analyseTextImageSuperResolution(true);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "onActivityResult activityIntentCodeStorage Exception for data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "Exception for data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
        }
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    private void displaySuccessAnalyseResults(String text) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        Log.i(TAG, "Text Image Super Resolution Success Results : " + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Text Image Super Resolution was Failed Results : " + msg);
        Utils.showToastMessage(getApplicationContext(), "Text Image Super Resolution was Failed : \n" + msg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (analyzer != null) {
            analyzer.stop();
        }
    }


}