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

package com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.utils.FileUtils;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageVisionUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.huawei.hms.image.vision.sticker.StickerLayout;
import com.huawei.hms.image.vision.sticker.item.TextEditInfo;
import com.squareup.picasso.Picasso;

public class StickerActivity extends AppCompatActivity {

    private static final String TAG = "IMAGEKIT";

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    /**
     * Target image URI
     */
    private Uri imageUri;

    /**
     * Sticker Service Element That Process Sticker's Add, Remove, Edit Functions
     */
    private TextEditInfo textEditInfo;
    private StickerLayout stickerLayout;

    /**
     * Sticker's root path
     */
    private String rootPath;

    /**
     * UI Elements
     */
    private ScrollView scrollView;
    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private ImageView ivImage;
    private TextView tvStickerInfoText;
    private Spinner spinnerSticker;
    private Button btnAddSticker;
    private Button btnAddEditableSticker;
    private TextInputLayout textInputLayout;
    private EditText etEditableStickerText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_vision_sticker);

        rootPath = getBaseContext().getFilesDir().getPath() + "/vgmap/";

        initUI();
        initListener();
        setupToolbar();
        initData();
        initAdapter();
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {

        /**
         * Call findViewById to obtain the StickerLayout object.
         */
        scrollView = findViewById(R.id.sv_sticker_service_image_kit);
        stickerLayout = findViewById(R.id.sticker_layout_imagekit_sticker);
        btnGallery = findViewById(R.id.btn_sticker_service_image_kit_gallery);
        btnCamera = findViewById(R.id.btn_sticker_service_image_kit_camera);
        ivImage = findViewById(R.id.iv_image_sticker);
        tvStickerInfoText = findViewById(R.id.tv_image_vision_sticker_info_text);
        spinnerSticker = findViewById(R.id.spinner_imagekit_sticker);
        btnAddSticker = findViewById(R.id.btn_imagekit_addsticker_sticker);
        btnAddEditableSticker = findViewById(R.id.btn_imagekit_addsticker_editable_sticker);
        textInputLayout = findViewById(R.id.et_imagekit_editable_sticker_text_base);
        etEditableStickerText = findViewById(R.id.et_imagekit_editable_sticker_text);
    }

    /**
     * Initialize Toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_sticker_service_image_kit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.txt_image_doc_link_image_kit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Initialize Listeners of UI Elements
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        stickerLayout.setStickerLayoutListener(new StickerLayout.StickerLayoutListener() {
            @Override
            public void onStickerLayoutClick() {
                Log.i(TAG, "onStickerLayoutClick");
            }

            @Override
            public void onStickerTouch(int i) {
                Log.i(TAG, "onStickerTouch");
            }

            @Override
            public void onTextEdit(TextEditInfo textEditInfo) {
                StickerActivity.this.textEditInfo = textEditInfo;
                tvStickerInfoText.setVisibility(View.GONE);
                textInputLayout.setVisibility(View.VISIBLE);
                etEditableStickerText.setVisibility(View.VISIBLE);
                etEditableStickerText.setText(textEditInfo.getText());
            }

            @Override
            public void needDisallowInterceptTouchEvent(boolean b) {
                Log.i(TAG, "needDisallowInterceptTouchEvent");
            }
        });

        stickerLayout.setOnTouchListener((v, event) -> {
            stickerLayout.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        etEditableStickerText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textEditInfo.setText(s.toString());
                stickerLayout.updateStickerText(textEditInfo);
                stickerLayout.postInvalidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddSticker.setOnClickListener(v -> addSticker(rootPath + "sticker1", spinnerSticker.getSelectedItem().toString()));

        btnAddEditableSticker.setOnClickListener(v ->
                addSticker(rootPath + "textArt1", "") //there is only one textart so static
        );

        scrollView.setOnTouchListener((v, event) -> {
            stickerLayout.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
    }

    /**
     * To Copy Sticker Data From Assets to Device Directory
     */
    private void initData() {
        FileUtils.copyAssetsFileToDirsRecursive(getBaseContext(), "vgmap", rootPath);
    }

    /**
     * Initialize Sticker Adapter
     *
     * @implNote ImageVisionUtils class contains functions that are used in common Image Kit Image Vision Service functions
     */
    private void initAdapter() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, ImageVisionUtils.getStickerAdapter());
        spinnerSticker.setAdapter(adapter);
    }

    /**
     * Call the addSticker API with resource file path and file name to add stickers and text arts
     */
    private void addSticker(String rootPath, String fileName) {

        try {
            int resultCode = stickerLayout.addSticker(rootPath, fileName);
            Log.i(TAG, String.valueOf(resultCode));
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    /**
     * Open Gallery Function, necessary Android permissions should have taken before this process
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    /**
     * Open Camera Function, necessary Android permissions should have taken before this process
     */
    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    /**
     * Obtain data from Camera or Gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (imageUri != null) {

                Picasso.get()
                        .load(imageUri)
                        .fit()
                        .centerCrop()
                        .into(ivImage);
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();

            if (imageUri != null) {

                Picasso.get()
                        .load(imageUri)
                        .fit()
                        .centerCrop()
                        .into(ivImage);
            }
        }
    }

}
