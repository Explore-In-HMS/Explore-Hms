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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.text_recognition.table_recognition;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.adapter.list.TableRecognitionElementsAdapter;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.hms.explorehms.huawei.feature_hiai.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.common.VisionImage;
import com.huawei.hiai.vision.text.TableDetector;
import com.huawei.hiai.vision.visionkit.text.config.VisionTableConfiguration;
import com.huawei.hiai.vision.visionkit.text.table.Table;
import com.huawei.hiai.vision.visionkit.text.table.TableBoxCoordinate;
import com.huawei.hiai.vision.visionkit.text.table.TableCell;
import com.huawei.hiai.vision.visionkit.text.table.TableContent;

import java.util.ArrayList;

public class TableRecognitionActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri imageUri;
    private Bitmap imageBitmap;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnGetResult;

    private ImageView ivImage;

    private RecyclerView rvTableCells;

    public TableRecognitionActivity() {
        super(ServiceGroupConstants.TEXT);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_recognition_hiai);
        baseContext = this;

        initUI();
        setupToolbar();
        initListeners();
        try {
            initService();
        } catch (Exception e) {
            Log.e(TAG, "Initialization Error : " + e.toString());
        }
    }

    @Override
    public void initUI() {
        btnGallery = findViewById(R.id.btn_table_recognition_gallery);
        btnCamera = findViewById(R.id.btn_table_recognition_camera);
        ivImage = findViewById(R.id.iv_table_recognition_image);
        btnGetResult = findViewById(R.id.btn_table_recognition_run);
        rvTableCells = findViewById(R.id.rv_table_recognition_result_cell);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_table_recognition_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_table_recognition_doc_link_hiai));
    }

    @Override
    public void initListeners() {

        btnGallery.setOnClickListener(v -> openGallery());

        btnCamera.setOnClickListener(v -> openCamera());

        btnGetResult.setOnClickListener(v -> {
            try {
                getResult();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ivImage.setOnClickListener(v -> {
            if (imageBitmap != null) {
                Util.showDialogImagePeekView(this, getApplicationContext(), ivImage);
            }
        });
    }

    @Override
    public void initService() {
        VisionBase.init(TableRecognitionActivity.this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                Log.i(TAG, "onServiceConnect");
                Toast.makeText(getApplicationContext(), "Service Connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnect() {
                Log.i(TAG, "onServiceDisconnect");
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void getResult() {

        if (imageBitmap != null) {
            TableDetector detector = new TableDetector(getApplicationContext());

            VisionTableConfiguration mTableConfig = new VisionTableConfiguration.Builder()
                    .setAppType(VisionTableConfiguration.APP_NORMAL)
                    .setProcessMode(VisionTableConfiguration.MODE_IN)
                    .build();

            detector.setVisionConfiguration(mTableConfig);

            detector.prepare();

            VisionImage image = VisionImage.fromBitmap(imageBitmap);

            Table table = new Table();

            int resultCode = detector.detect(image, table, null);

            if (resultCode == 0 && table.getTableContent() != null) {
                ArrayList<TableCell> cells = new ArrayList<>();
                for (TableContent content : table.getTableContent()) {
                    cells.addAll(content.getBody());
                }


                TableRecognitionElementsAdapter adapter = new TableRecognitionElementsAdapter(new ArrayList<>(cells));
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                rvTableCells.setLayoutManager(mLayoutManager);
                rvTableCells.setAdapter(adapter);

                drawCellPoint(cells);
            } else
                Toast.makeText(getApplicationContext(), "Detection Failed, ResultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        } else {
            showImageAlert();
        }
    }

    public void drawCellPoint(ArrayList<TableCell> cells) {

        Bitmap bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint paintPoint = new Paint();
        paintPoint.setStyle(Paint.Style.STROKE);
        paintPoint.setColor(Color.GREEN);
        paintPoint.setStrokeWidth(5);

        for (TableCell cell : cells) {
            for (TableBoxCoordinate box : cell.getBox()) {
                int left = box.getmBoxCoordinate().get(0);
                int top = box.getmBoxCoordinate().get(1);
                int right = left + box.getmBoxCoordinate().get(0) - box.getmBoxCoordinate().get(4);
                int bottom = top + box.getmBoxCoordinate().get(1) - box.getmBoxCoordinate().get(5);

                canvas.drawRect(left, top, right, bottom, paintPoint);
            }
        }

        Glide.with(this).load(mutableBitmap).into(ivImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
            Glide.with(this).load(imageUri).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    Log.i(TAG, "Image Load Successful");
                    return false;
                }
            }).into(ivImage);

        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this).load(imageUri).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e(TAG, "Image Load Failed : " + (e != null ? e.toString() : ""));
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imageBitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);
                    ivImage.setPadding(0, 0, 0, 0);
                    Log.i(TAG, "Image Load Successful");
                    return false;
                }
            }).into(ivImage);

        }
    }
}
