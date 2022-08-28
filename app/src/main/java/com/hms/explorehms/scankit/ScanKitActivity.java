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

package com.hms.explorehms.scankit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.R;
import com.hms.explorehms.Util;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.ml.scan.HmsScanBase;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public class ScanKitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ScanKitActivity";

    public static final String DECODE_MODE = "decode_mode";
    private static final int REQUEST_CODE_SCAN_MULTI = 0X011;

    String[] defaultModeRequest = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    String[] customModeRequest = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int SCAN_DEFAULT_VIEW_REQUEST = 2001;
    private static final int SCAN_CUSTOM_VIEW_REQUEST = 2002;
    private static final int SCAN_BITMAP_REQUEST = 2003;
    public static final int SCAN_MULTIPROCESSOR_SYNC = 2004;
    public static final int SCAN_MULTIPROCESSOR_ASYNC = 2005;
    private static final int SCAN_BITMAP_REQUEST_CAMERA = 2006;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scankit);

        setupToolbar();

        findViewById(R.id.btn_defautViewMode).setOnClickListener(this);
        findViewById(R.id.btn_customizeViewMode).setOnClickListener(this);
        findViewById(R.id.btn_BitmapMode).setOnClickListener(this);
        findViewById(R.id.btn_BitmapModeCamera).setOnClickListener(this);
        findViewById(R.id.btn_multiProccessorApiSynchronousMode).setOnClickListener(this);
        findViewById(R.id.btn_multiProccessorApiAsynchronousMode).setOnClickListener(this);
        findViewById(R.id.btn_generateQrCode).setOnClickListener(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_doc_scankit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String @NotNull [] permissions, int @NotNull [] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "onRequestPermissionsResult : grantResults.length < 2 ");
            showMessage();
            return;
        }

        //Default View Mode
        if (requestCode == SCAN_DEFAULT_VIEW_REQUEST) {
            Log.w(TAG, "onRequestPermissionsResult : requestCodeDefaultView : ScanUtil.startScan");
            startDefaultBarcodeView();
        }

        //Customized View Mode
        if (requestCode == SCAN_CUSTOM_VIEW_REQUEST) {
            Log.w(TAG, "onRequestPermissionsResult : requestCodeCustomizeView : Start CustomizeActivity");
            startCustomBarcodeView();
        }

        //Bitmap Mode
        if (requestCode == SCAN_BITMAP_REQUEST) {
            Log.w(TAG, "onRequestPermissionsResult : requestCodeBitmapMode : Start Intent ACTION_PICK");
            // Call the system album.
            startImagePicker(requestCode);
        }

        //Bitmap Mode Camera
        if (requestCode == SCAN_BITMAP_REQUEST_CAMERA) {
            Log.w(TAG, "onRequestPermissionsResult : requestCodeBitmapModeCamera : Start Intent ACTION_IMAGE_CAPTURE");
            // Call the system album.
            startCamera(requestCode);
        }
        //Multiprocessor Synchronous Mode
        if (requestCode == SCAN_MULTIPROCESSOR_SYNC) {
            Log.w(TAG, "onRequestPermissionsResult : requestCodeMultiProcApiSyn : Start CommonActivity");
            // Execute  startImagePicker(requestCode);
            Intent intent = new Intent(this, CommonActivity.class);
            intent.putExtra(DECODE_MODE, SCAN_MULTIPROCESSOR_SYNC);
            this.startActivityForResult(intent, REQUEST_CODE_SCAN_MULTI);
        }
        //Multiprocessor Asynchronous Mode
        if (requestCode == SCAN_MULTIPROCESSOR_ASYNC) {
            Log.w(TAG, "onRequestPermissionsResult : requestCodeMultiProcApiASyn : Start CommonActivity");
            // Execute  startImagePicker(requestCode);
            Intent intent = new Intent(this, CommonActivity.class);
            intent.putExtra(DECODE_MODE, SCAN_MULTIPROCESSOR_ASYNC);
            this.startActivityForResult(intent, REQUEST_CODE_SCAN_MULTI);
        }
    }

    /**
     * Constructs a default barcode scanner for all
     */
    private void startDefaultBarcodeView() {
        ScanUtil.startScan(this, SCAN_DEFAULT_VIEW_REQUEST, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScanBase.ALL_SCAN_TYPE).create());
    }

    private void startCustomBarcodeView() {
        this.startActivityForResult(new Intent(this, ScanCustomViewActivity.class), SCAN_CUSTOM_VIEW_REQUEST);
    }

    /**
     * Start an image picker intent to choose a barcode
     *
     * @param Scan_Request_Code Identifies which mode called the image picker
     */
    private void startImagePicker(int Scan_Request_Code) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        this.startActivityForResult(pickIntent, Scan_Request_Code);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void startCamera(int Scan_Request_Code) {
        Intent pickIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pickIntent.resolveActivity(getPackageManager()) != null) {
            this.startActivityForResult(pickIntent, Scan_Request_Code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        // Obtain the return value of HmsScan from the value returned by the onActivityResult
        // method by using ScanUtil.RESULT as the key value.

        switch (requestCode) {
            case SCAN_DEFAULT_VIEW_REQUEST:
                HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);

                if (!TextUtils.isEmpty(obj.getOriginalValue())) {
                    Toast.makeText(this, obj.getOriginalValue(), Toast.LENGTH_SHORT).show();
                    showMultiResultActivity(new HmsScan[]{obj});
                }
                break;
            case SCAN_CUSTOM_VIEW_REQUEST:
                HmsScan hmsScan = data.getParcelableExtra(ScanCustomViewActivity.SCAN_RESULT);
                if (hmsScan != null && !TextUtils.isEmpty(hmsScan.getOriginalValue())) {
                    Toast.makeText(ScanKitActivity.this, hmsScan.getOriginalValue(), Toast.LENGTH_SHORT).show();
                    showMultiResultActivity(new HmsScan[]{hmsScan});
                }
                break;
            case SCAN_BITMAP_REQUEST:
                try {
                    // Obtain the bitmap from the image picker.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                    // Call the decodeWithBitmap method to pass the bitmap.
                    HmsScan[] result1 = ScanUtil.decodeWithBitmap(ScanKitActivity.this, bitmap, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(0).setPhotoMode(true).create());
                    // Obtain the scanning result.
                    if (result1 != null && result1.length > 0) {
                        showMultiResultActivity(result1);
                    } else {
                        Toast.makeText(this, "Barcode is NULL or Empty!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }

                break;

            case SCAN_BITMAP_REQUEST_CAMERA:
                Camera camera = null;
                camera = Camera.open();

                Bundle extras = data.getExtras();
                Bitmap var_Bitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                var_Bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();


                YuvImage yuv = new YuvImage(bytes, ImageFormat.NV21, camera.getParameters().getPreviewSize().width,
                        camera.getParameters().getPreviewSize().height, null);
                yuv.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width,
                        camera.getParameters().getPreviewSize().height), 100, stream);

                Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);

                HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(0).setPhotoMode(false).create();

                HmsScan[] result2 = ScanUtil.decodeWithBitmap(ScanKitActivity.this, bitmap, options);

                if (result2 != null && result2.length > 0) {
                    showMultiResultActivity(result2);
                } else {
                    Toast.makeText(this, "Barcode is NULL or Empty!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    private void showMultiResultActivity(HmsScan[] scanResult) {

        Class targetClass = (scanResult.length <= 1) ? DisplayActivity.class : DisplayMulActivity.class;
        Intent intent = new Intent(this, targetClass);
        intent.putExtra(ScanUtil.RESULT, scanResult);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_defautViewMode:
                ActivityCompat.requestPermissions(this, defaultModeRequest, SCAN_DEFAULT_VIEW_REQUEST);
                break;
            case R.id.btn_customizeViewMode:
                ActivityCompat.requestPermissions(this, customModeRequest, SCAN_CUSTOM_VIEW_REQUEST);
                break;
            case R.id.btn_BitmapMode:
                ActivityCompat.requestPermissions(this, customModeRequest, SCAN_BITMAP_REQUEST);
                break;
            case R.id.btn_BitmapModeCamera:
                ActivityCompat.requestPermissions(this, customModeRequest, SCAN_BITMAP_REQUEST_CAMERA);
                break;
            case R.id.btn_multiProccessorApiSynchronousMode:
                ActivityCompat.requestPermissions(this, defaultModeRequest, SCAN_MULTIPROCESSOR_SYNC);
                break;
            case R.id.btn_multiProccessorApiAsynchronousMode:
                ActivityCompat.requestPermissions(this, defaultModeRequest, SCAN_MULTIPROCESSOR_ASYNC);
                break;
            case R.id.btn_generateQrCode:
                Util.startActivity(this, GenerateCodeActivity.class);
                break;
        }
    }

    /*
     * Shows a text Toast messages on the lower part of the screen
     * @param msg Text to show
     */
    private void showMessage() {
        Toast.makeText(this, "Need Camera and External Storage Permission For Scan!!", Toast.LENGTH_LONG).show();
    }
}