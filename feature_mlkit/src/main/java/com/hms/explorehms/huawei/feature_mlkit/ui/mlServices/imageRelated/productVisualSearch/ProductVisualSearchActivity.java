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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.productVisualSearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_mlkit.R;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.CameraImageGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.GraphicOverlay;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.graphicView.ProductGraphic;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.transactor.ProductTransactor;
import com.hms.explorehms.huawei.feature_mlkit.ui.cameraOperations.utils.BitmapUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.DialogUtils;
import com.hms.explorehms.huawei.feature_mlkit.utils.Utils;
import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCapture;
import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCaptureConfig;
import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCaptureFactory;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.productvisionsearch.MLProductVisionSearch;
import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProduct;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProductVisualSearchActivity extends AppCompatActivity {


    private static final int BTN_PRODUCT_SEARCH_WITH_IMAGE = R.id.btn_productSearchWithImage;
    private static final int BTN_PRODUCT_SEARCH_STORAGE = R.id.btn_productSearchStorage;
    private static final int BTN_PRODUCT_SEARCH_WITH_TAKE_PICTURE = R.id.btn_productSearchWithTakeAPicture;
    private static final int BTN_PRODUCT_SEARCH_WITH_CAMERA_STREAM = R.id.btn_productSearchWithCameraStream;
    private static final int IV_INFO = R.id.ivInfo;

    private static final int LIVE_OVERLAY = R.id.live_overlay;
    private static final int IV_PRODUCT_SEARCH = R.id.iv_productSearch;
    private static final int RESULT_LOGS = R.id.resultLogs;
    private static final int PROGRESS_BAR = R.id.progressBar;

    private static final String DIALOG_STORAGE_IMAGE_MESSAGE = "Would You Like To Go To Permission Settings To Allow?";
    private static final String YES_GO_DIALOG_MESSAGE = "YES GO";
    private static final String CANCEL_DIALOG_MESSAGE = "CANCEL";

    //region variablesAndObjects
    private static final String TAG = ProductVisualSearchActivity.class.getSimpleName();

    private Unbinder unbinder;

    private static final int PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE = 1;
    String[] permissionRequestStorageForSelectImage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE = 11;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO = 2;
    String[] permissionRequestCameraAndStorageForTakePhoto = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int activityIntentCodeCameraAndStorageForTakePhoto = 22;

    private static final int PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM = 3;
    String[] permissionRequestCameraAndStorageForStream = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private ProductTransactor productTransactor;

    private Uri takedImageUri;

    @Nullable
    @BindView(value = LIVE_OVERLAY)
    GraphicOverlay graphicOverlay;


    @Nullable
    @BindView(value = IV_PRODUCT_SEARCH)
    ImageView imageViewProductSearch;

    @Nullable
    @BindView(value = RESULT_LOGS)
    TextView resultLogs;

    @Nullable
    @BindView(value = PROGRESS_BAR)
    ProgressBar progressBar;

    //endregion variablesAndObjects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_visual_search);
        setupToolbar();

        unbinder = ButterKnife.bind(this);

        createMLProductVisionSearchTransactors();

        // this is important for get image of imageView by Bitmap and use with AnalyzeWithImage
        imageViewProductSearch.setDrawingCacheEnabled(true);

    }


    @OnClick({R.id.btn_productSearchWithImage, R.id.btn_productSearchStorage,
            R.id.btn_productSearchWithTakeAPicture, R.id.btn_productSearchWithCameraStream})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case BTN_PRODUCT_SEARCH_WITH_IMAGE:
                clearLogs();
                showProgress();
                Bitmap bitmap = imageViewProductSearch.getDrawingCache();
                analyseVisualSearchProductWithImage(bitmap, false);
                break;
            case BTN_PRODUCT_SEARCH_STORAGE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestStorageForSelectImage, PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE);
                break;
            case BTN_PRODUCT_SEARCH_WITH_TAKE_PICTURE:
                clearLogs();
                showProgress();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForTakePhoto, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO);
                break;
            case BTN_PRODUCT_SEARCH_WITH_CAMERA_STREAM:
                clearLogs();
                ActivityCompat.requestPermissions(this, permissionRequestCameraAndStorageForStream, PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM);
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_irs_pvs));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createMLProductVisionSearchTransactors() {
        productTransactor = new ProductTransactor(this);
    }


    /**
     * Product visual search with Asynchronous method
     * <p>
     * The product visual search service calls the on-cloud API on the device
     * to detect, recognize, and search for products.
     * During commissioning and usage, ensure that the device can access the Internet.
     * With deep learning as the core technology,
     * ML Kit uses the image recognition and search functions to implement personalized product search services.
     * You need to build a product image library for product search.
     * To experience the product visual search service
     * (or to obtain the APIs for online product adding, deletion, modification, and query), contact mlkit@huawei.com.
     *
     * @param bitmap
     * @param isFromGallery : true = image getting from Gallery. set imageView with this bitmap. false = image using from imageView
     */
    private void analyseVisualSearchProductWithImage(Bitmap bitmap, boolean isFromGallery) {

        graphicOverlay.clear();

        if (isFromGallery) {
            imageViewProductSearch.setImageBitmap(bitmap);
        } else {
            imageViewProductSearch.setImageResource(R.drawable.test_image_product_visual_search);
        }

        // ! important !
        // for change scale takedImageBitmap by imageViewObject size
        Bitmap takedImageBitmap = imageViewProductSearch.getDrawingCache();
        takedImageUri = BitmapUtils.getImageUriFromBitmap(ProductVisualSearchActivity.this, bitmap);

        MLFrame frame = new MLFrame.Creator().setBitmap(takedImageBitmap).create();

        productTransactor.detectInImage(frame)
                .addOnSuccessListener(productVisionSearchResults -> {
                    if (productVisionSearchResults == null) {
                        Log.e(TAG, "analyseVisualSearchProductWithImage : landmarkTransactor results is NULL !");
                        return;
                    }

                    if (productVisionSearchResults.size() > 0) {

                        graphicOverlay.clear();

                        CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, takedImageBitmap);
                        graphicOverlay.addGraphic(imageGraphic);

                        List<String> productList = new ArrayList<>();

                        for (MLProductVisionSearch productVisionSearch : productVisionSearchResults) {

                            ProductGraphic productGraphic = new ProductGraphic(graphicOverlay, productVisionSearch.getProductList());
                            graphicOverlay.addGraphic(productGraphic);
                            graphicOverlay.postInvalidate();

                            Log.e(TAG, "analyseVisualSearchProductWithImage : productVisionSearch.getProductList() : " + productVisionSearch.getProductList().size());

                            for (MLVisionSearchProduct visionSearchProduct : productVisionSearch.getProductList()) {

                                String productId = "id : " + visionSearchProduct.getProductId();
                                String productPsb = "Possb : " + visionSearchProduct.getPossibility();
                                String productCc = "CustCont : " + visionSearchProduct.getCustomContent();
                                String productUrl = "Url : " + visionSearchProduct.getProductUrl();
                                String productImgUrl = "imgUrl : " + visionSearchProduct.getImageList().get(0).getImageId();

                                productList.add(productId + " - " + " - " + productPsb + " " + productUrl);
                                Log.e(TAG, "analyseVisualSearchProductWithImage : productVisionSearch.getProductList() : " + productId + " - " + productCc + " - " + productPsb + " - " + productUrl + " - " + productImgUrl);
                            }

                        }

                        imageViewProductSearch.setImageBitmap(BitmapUtils.loadBitmapFromView(graphicOverlay, 1080, 1200));

                        displaySuccessAnalyseResults(productList);

                    } else {
                        Log.e(TAG, "analyseVisualSearchProductWithImage : productVisionSearchResults addOnSuccessListener : 0 ");
                        Utils.showToastMessage(getApplicationContext(), "No ProductVisualSearch data, Recognized ProductVisualSearchResults size is zero!");
                        displayFailureAnalyseResults("No ProductVisualSearch data, Recognized ProductVisualSearchResults size is zero!");
                    }

                    hideProgress();

                })
                .addOnFailureListener(e -> {
                    String errorMessage = e.getMessage();
                    try {
                        int errorCode = ((MLException) e).getErrCode();
                        String errorMsg = e.getMessage();
                        errorMessage = "ERROR: " + errorCode + " : " + errorMsg;
                    } catch (Exception ex) {
                        Log.e(TAG, "analyseVisualSearchProductWithImage.ProductVisualSearchTransactor.detectInImage.onFailure (MLException) errCode : " + ex.getMessage(), ex);
                    }
                    Log.e(TAG, "analyseVisualSearchProductWithImage.ProductVisualSearchTransactor.detectInImage.onFailure exc : " + errorMessage, e);

                    displayFailureAnalyseResults(errorMessage);

                    hideProgress();
                });

        stopVisualSearchProductTransactor();

    }


    private void createMLProductVisionSearchCaptureAndStart() {
        /*
         * Set the maximum number of detection results. The default value is 20. The value ranges from 1 to 100.
         * Set the fragment (inherited from AbstractProductFragment) for custom product display.
         * Set the product set ID, which can be generated and obtained in AppGallery Connect.
         * //.setProductSetId("xxxxx")
         * Set the Site region. Currently, the following site regions are supported:
         * CHINA, EUROPE, AFILA, RUSSIA, GERMAN and SIANGAPORE.
         * (The site region must be the same as the access site selected in AppGallery Connect.)
         *
         */
        MLProductVisionSearchCaptureConfig config = new MLProductVisionSearchCaptureConfig.Factory()
                .setLargestNumOfReturns(16)
                .setProductFragment(new ProductVisualSearchFragment())
                .setRegion(MLProductVisionSearchCaptureConfig.REGION_DR_CHINA)
                .create();
        MLProductVisionSearchCapture capture = MLProductVisionSearchCaptureFactory.getInstance().create(config);
        capture.startCapture(ProductVisualSearchActivity.this);
    }


    private void displaySuccessAnalyseResults(List<String> resultList) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 200);
        String textMessage = "Product Visual Search Success Results : with " + resultList.size() + " product. :\n" + resultList.toString();
        Log.i(TAG, textMessage);
        resultLogs.setText(textMessage);
    }

    private void displayFailureAnalyseResults(String msg) {
        hideProgress();
        Utils.createVibration(getApplicationContext(), 400);

        String textFromResult = "Product Visual Search was Failed Results : \n" + msg;

        Log.e(TAG, textFromResult);

        resultLogs.setText(textFromResult);

        Utils.showToastMessage(getApplicationContext(), textFromResult);
    }


    private void clearLogs() {
        resultLogs.setText(R.string.product_search_result_descriptions_will_be_here);
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVisualSearchProductTransactor();
        unbinder.unbind();
    }

    public void stopVisualSearchProductTransactor() {
        if (productTransactor != null) {
            productTransactor.stop();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult : requestCode : " + requestCode);

        if (permissions == null || grantResults == null) {
            return;
        }

        if (requestCode == PERMISSION_CODE_STORAGE_FOR_SELECT_IMAGE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted storage permission ForDevice -> Start Intent ACTION_PICK");
                // Call the system album. maybe type change will necessary
                this.startActivityForResult(Utils.createIntentForPickImageFromStorage(), ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE);
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : StoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED STORAGE PERMISSION",
                        DIALOG_STORAGE_IMAGE_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Product Visual Search without Storage Permission!",
                        YES_GO_DIALOG_MESSAGE, CANCEL_DIALOG_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_TAKE_PHOTO) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission -> Start Intent TakePicture");

                // set taken photo name with timestamp and location
                @SuppressLint("SimpleDateFormat")
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String photoFileName = timeStamp + "_newProductSearchPicture";

                String storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/ML Product Visual Search";
                File imageFile = new File(storageDirectory, photoFileName + ".jpg");
                Uri photoUri = Uri.fromFile(imageFile);
                Log.i(TAG, "onRequestPermissionsResult storageDirectory : " + storageDirectory);
                Log.i(TAG, "onRequestPermissionsResult photoUri.getPath : " + photoUri.getPath());


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, photoFileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "ML Product Visual Search Photo From Camera");

                    takedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Log.i(TAG, "onRequestPermissionsResult takedImageUri --------> : " + takedImageUri);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takedImageUri);
                    this.startActivityForResult(takePictureIntent, activityIntentCodeCameraAndStorageForTakePhoto);
                } else {
                    Log.i(TAG, "takePictureIntent.resolveActivity( this.getPackageManager()) is NULL");
                }
            } else {
                hideProgress();
                Log.w(TAG, "onRequestPermissionsResult : CameraPermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        DIALOG_STORAGE_IMAGE_MESSAGE,
                        R.drawable.icons_switch_camera_black,
                        "You can not use Product Visual Search with Take a Picture without Camera And Storage Permission!",
                        YES_GO_DIALOG_MESSAGE, CANCEL_DIALOG_MESSAGE);
            }
        }

        if (requestCode == PERMISSION_CODE_CAMERA_AND_STORAGE_FOR_STREAM) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult : granted camera and storage permission ForDevice -> Start ProductVisionSearchCapture");

                if (Utils.haveNetworkConnection(this)) {
                    createMLProductVisionSearchCaptureAndStart();
                } else {
                    DialogUtils.showDialogPermissionWarning(
                            this,
                            "NEED NETWORK PERMISSION",
                            "Would You Like To Go To Settings To Open Network?",
                            R.drawable.icon_settings,
                            "You can not Product Vision Search without Internet Connection!",
                            YES_GO_DIALOG_MESSAGE, CANCEL_DIALOG_MESSAGE);
                }

            } else {
                Log.w(TAG, "onRequestPermissionsResult : CameraAndStoragePermission was NOT GRANTED");
                DialogUtils.showDialogPermissionWarning(this,
                        "NEED CAMERA AND STORAGE PERMISSION",
                        DIALOG_STORAGE_IMAGE_MESSAGE,
                        R.drawable.icon_folder,
                        "You can not use Product Visual Search with Camera Stream without Camera And Storage Permission!",
                        YES_GO_DIALOG_MESSAGE, CANCEL_DIALOG_MESSAGE);
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

            if (requestCode == ACTIVITY_INTENT_CODE_STORAGE_FOR_SELECT_IMAGE) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    analyseVisualSearchProductWithImage(bitmap, true);
                } catch (IOException e) {
                    Log.i(TAG, "onActivityResult activityIntentCodeStorageForSelectImage IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }
            }
            if (requestCode == activityIntentCodeCameraAndStorageForTakePhoto) {
                Bitmap bitmap;
                try {
                    Log.i(TAG, "onActivityResult takedImageUri --------> " + takedImageUri);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), takedImageUri);
                    analyseVisualSearchProductWithImage(bitmap, true);
                } catch (Exception e) {
                    Log.e(TAG, "onActivityResult activityIntentCodeCameraAndStorageForTakePhoto IOException for getBitmap with data.getData : " + e.getMessage());
                    Utils.showToastMessage(getApplicationContext(), "IOException for getBitmap with data.getData : " + e.getMessage());
                    hideProgress();
                }

            }
        }
    }


}