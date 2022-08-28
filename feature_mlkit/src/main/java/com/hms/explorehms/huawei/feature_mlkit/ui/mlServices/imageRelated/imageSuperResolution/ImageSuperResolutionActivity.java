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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.imageSuperResolution;

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
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzer;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerFactory;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionAnalyzerSetting;
import com.huawei.hms.mlsdk.imagesuperresolution.MLImageSuperResolutionResult;

import java.util.ArrayList;
import java.util.List;

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
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class ImageSuperResolutionActivity extends AppCompatActivity {

    //region variablesAndObjects
    private static final String TAG = ImageSuperResolutionActivity.class.getSimpleName();

    private Unbinder unbinder;

    private MLImageSuperResolutionAnalyzer analyzer;

    private static final int IMAGE_MAX_SIZE = 1024;

    private static final int AD_JUST_OPTION_1_X = 0;
    private static final int AD_JUST_OPTION_3_X = 1;
    private static final int AD_JUST_OPTION_ORG = 2;

    private int selectedAdjustOption = AD_JUST_OPTION_1_X;

    private Uri takedImageUri;
    private Bitmap sourceImageBitmap;
    private Bitmap destinationImageBitmap;

    private final List<ImageView> adjustSelectImageList = new ArrayList<>();

    @Nullable
    @BindView(R.id.iv_isr_source_image)
    ImageView imageViewIsrSourceImage;

    @Nullable
    @BindView(R.id.tv_image_size_info)
    TextView tvImageSizeInfo;

    @Nullable
    @BindView(R.id.iv_isr_destination_image)
    ImageViewTouch imageViewIsrDestinationImage;

    @Nullable
    @BindView(R.id.ll_adjust_setting)
    ConstraintLayout layoutAdjustOptions;

    @Nullable
    @BindView(R.id.ll_1x_adjust)
    LinearLayout layoutAdjust1X;

    @Nullable
    @BindView(R.id.ll_3x_adjust)
    LinearLayout layoutAdjust3X;

    @Nullable
    @BindView(R.id.iv_adjust_1x)
    ImageView ivAdjust1x;

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
    @BindView(R.id.iv_isr_select_image)
    ImageView imageViewSelectImage;

    @Nullable
    @BindView(R.id.iv_isr_adjust)
    ImageView imageViewSetAdjust;

    @Nullable
    @BindView(R.id.iv_isr_help)
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
        setContentView(R.layout.activity_image_super_resolution);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        createImageSuperResolutionAnalyzer();

        adjustSelectImageList.add(ivAdjust1x);
        adjustSelectImageList.add(ivAdjust3x);
        adjustSelectImageList.add(ivAdjustOrg);

        //ActivityCompat.requestPermissions(this, permReqForStorageForSelectImage, permCodeStorageForSelectImage);
        adjustDefaultImageAndAnalyse();
    }


    @OnClick({R.id.ll_1x_adjust, R.id.ll_3x_adjust, R.id.ll_original_adjust, R.id.iv_isr_select_image, R.id.iv_isr_adjust, R.id.iv_isr_help})
    public void onItemClick(View v) {

        switch (v.getId()) {
            case R.id.ll_1x_adjust:
                changeSelectedAdjustOption(AD_JUST_OPTION_1_X);
                break;
            case R.id.ll_3x_adjust:
                changeSelectedAdjustOption(AD_JUST_OPTION_3_X);
                break;
            case R.id.ll_original_adjust:
                changeSelectedAdjustOption(AD_JUST_OPTION_ORG);
                break;
            case R.id.iv_isr_select_image:
                showProgress();
                ActivityCompat.requestPermissions(this, permReqForStorageForSelectImage, PERM_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case R.id.iv_isr_adjust:
                showOrHideAdjustOptionsLayout();
                break;
            case R.id.iv_isr_help:
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
        Util.setToolbar(this, toolbar , getResources().getString(R.string.link_irs_isr));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public MLImageSuperResolutionAnalyzer createImageSuperResolutionAnalyzer() {
        if (selectedAdjustOption == AD_JUST_OPTION_1_X) {
            // Method 1: Use default parameter settings, that is, the 1x super-resolution capability.
            analyzer = MLImageSuperResolutionAnalyzerFactory.getInstance().getImageSuperResolutionAnalyzer();
        } else {
            // Method 2: Use custom parameter settings. Currently, only 1x super-resolution is supported. More capabilities will be supported in the future.
            MLImageSuperResolutionAnalyzerSetting setting = new MLImageSuperResolutionAnalyzerSetting.Factory()
                    .setScale(MLImageSuperResolutionAnalyzerSetting.ISR_SCALE_3X)
                    .create();
            analyzer = MLImageSuperResolutionAnalyzerFactory.getInstance().getImageSuperResolutionAnalyzer(setting);
        }
        return analyzer;
    }

    private void changeSelectedAdjustOption(int index) {
        if (takedImageUri == null) {
            Utils.showToastMessage(getApplicationContext(), "Please Select An Image Before Chang AdjustOption");
        } else {
            boolean selectedAdjustSwitch = false;
            if (selectedAdjustOption == index) {
                selectedAdjustSwitch = false;
            } else {
                selectedAdjustSwitch = true;
            }
            selectedAdjustOption = index;
            setSelectedAdjustViewPoint(selectedAdjustOption);
            analyseImageSuperResolution(false, selectedAdjustSwitch);
        }
    }

    private void setSelectedAdjustViewPoint(int position) {
        for (int i = 0; i < adjustSelectImageList.size(); i++) {
            if (i == position) {
                adjustSelectImageList.get(i).setBackgroundResource(R.drawable.icon_bg_circle_red);
            } else {
                adjustSelectImageList.get(i).setBackgroundResource(R.drawable.icon_bg_circle_white);
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
        imageViewIsrSourceImage.setImageResource(R.drawable.icon_isr_coyote);
        sourceImageBitmap =  ((BitmapDrawable) imageViewIsrSourceImage.getDrawable()).getBitmap();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(ImageSuperResolutionActivity.this, sourceImageBitmap);
        analyseImageSuperResolution(true, false);
    }

    /**
     * Super-Resolution with Asynchronous method
     * <p>
     * The quality of low-quality images on the network can be improved with this service,
     * and larger and clearer images can be obtained too when users are reading news.
     *
     * @param isReload : true = getting new image. false = different adjust will be used for previously set image
     * @param isSwitch : true = selectedAdjustOption is change 1x to 3x or 3x to 1x . false = will be used for previously adjust type
     *                 The analyzer only supports a single instance.
     *                 If you want to switch to a different scale, you need to release the model and recreate it.
     */
    private void analyseImageSuperResolution(boolean isReload, boolean isSwitch) {

        if (isReload) {
            sourceImageBitmap = BitmapUtils.getBitmapFromUriWithSize(this, takedImageUri, IMAGE_MAX_SIZE, IMAGE_MAX_SIZE);
            setImageViewWithBitmap(imageViewIsrSourceImage, sourceImageBitmap);
            Log.d(TAG, "ImageSuperResolution get new source imageBitmap and setViews");
        }

        if (selectedAdjustOption == AD_JUST_OPTION_ORG) {
            Log.d(TAG, "ImageSuperResolution just change image with adjustOption");
            setImageViewWithBitmap(imageViewIsrDestinationImage, sourceImageBitmap);
            setImageSizeInfo(sourceImageBitmap.getWidth(), sourceImageBitmap.getHeight());
            displaySuccessAnalyseResults("ImageSuperResolution image adjust option changed with original");
            return;
        }

        if (isSwitch) {
            // The analyzer only supports a single instance.
            // If you want to switch to a different scale, you need to release the model and recreate it.
            analyzer.stop();
            analyzer = createImageSuperResolutionAnalyzer();
        }

        // Create an MLFrame by using the bitmap.
        MLFrame frame = MLFrame.fromBitmap(sourceImageBitmap);
        // another method
        //MLFrame frame = new MLFrame.Creator().setBitmap(sourceImageBitmap).create();

        Task<MLImageSuperResolutionResult> task = analyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(result -> {
            Log.d(TAG, "ImageSuperResolution onSuccess : getting resultBitmap and setViews.");

            destinationImageBitmap = result.getBitmap();

            setImageViewWithBitmap(imageViewIsrDestinationImage, destinationImageBitmap);
            setImageSizeInfo(destinationImageBitmap.getWidth(), destinationImageBitmap.getHeight());

            displaySuccessAnalyseResults("ImageSuperResolution onSuccess.");

        }).addOnFailureListener(e -> {
            Log.e(TAG, "ImageSuperResolution onFailure : " + e.getMessage(), e);
            Utils.showToastMessage(this, "ImageSuperResolution Exception : " + e.getMessage());
            displayFailureAnalyseResults("ImageSuperResolution onFailure : " + e.getMessage());
        });

    }

    private void setImageViewWithBitmap(final ImageView imageView, final Bitmap bitmap) {
        ImageSuperResolutionActivity.this.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
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
                        "You can not use Image Super Resolution without Storage Permission!",
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
                        analyseImageSuperResolution(true, false);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "onActivityResult activityIntentCodeStorage Exception for data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "Exception for data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
        }
    }


    private void showHelpInformationTipsDialog() {
        DialogUtils.showInformationTipsDialog(
                ImageSuperResolutionActivity.this,
                getResources().getString(R.string.img_sp_res_s),
                getResources().getString(R.string.isr_tips_content),
                R.drawable.icons_information,
                getResources().getString(R.string.dialog_text_show_source_page),
                getResources().getString(R.string.dialog_text_cancel),
                (positiveListener) -> Utils.openWebPage(this, getResources().getString(R.string.link_irs_isr)));
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
        Log.i(TAG, "Image Super Resolution Success Results : " + text);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);
        Log.e(TAG, "Image Super Resolution was Failed Results : " + msg);
        Utils.showToastMessage(getApplicationContext(), "Image Super Resolution was Failed : \n" + msg);
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