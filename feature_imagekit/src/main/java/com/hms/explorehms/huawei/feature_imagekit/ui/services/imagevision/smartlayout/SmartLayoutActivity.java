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

package com.hms.explorehms.huawei.feature_imagekit.ui.services.imagevision.smartlayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.application.AppSession;
import com.hms.explorehms.huawei.feature_imagekit.model.TokenResponseModel;
import com.hms.explorehms.huawei.feature_imagekit.utils.ApplicationUtils;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageUtils;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.image.vision.ImageVision;
import com.huawei.hms.image.vision.ImageVisionImpl;
import com.huawei.hms.image.vision.bean.ImageLayoutInfo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;


public class SmartLayoutActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "IMAGEKIT";

    private static final int CAMERA_REQUEST = 100;

    /**
     * ImageVisionImp object. All Image Vision Service functions are called from this object
     */
    private ImageVisionImpl imageVisionAPI;

    /**
     * Target image URI
     */
    private Uri imageUri;

    /**
     * UI Elements
     */

    private ProgressBar progressBar;

    private Spinner spinnerStyeList;

    private MaterialButton btnCamera;
    private MaterialButton btnRunService;

    private ImageView ivImage;
    private ImageView ivDocLink;

    private EditText etTitle;
    private EditText etDescription;
    private EditText etCopyright;
    private EditText etAnchor;

    private CheckBox cbIsNeedMask;

    private TextView tvServiceResult;

    private String style;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_vision_smart_layout);

        initUI();
        initSpinner();
        initListener();
        setupToolbar();
        initService();


        try {
            imageUri = ImageUtils.getUriToDrawable(getApplicationContext(), R.drawable.photo_imagekit_smart_layout_ex);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {
        progressBar = findViewById(R.id.pb_imagekit_smartlayout);
        btnCamera = findViewById(R.id.btn_smart_layout_image_kit_camera);
        ivImage = findViewById(R.id.iv_image_smart_layout);
        ivDocLink = findViewById(R.id.iv_doc_link_smart_layout_image_kit);
        etTitle = findViewById(R.id.tv_image_vision_smart_layout_title);
        etDescription = findViewById(R.id.tv_image_vision_smart_layout_description);
        etCopyright = findViewById(R.id.tv_image_vision_smart_layout_copyright);
        etAnchor = findViewById(R.id.tv_image_vision_smart_layout_anchor);
        cbIsNeedMask = findViewById(R.id.cb_image_vision_smart_layout_isneedmask);
        btnRunService = findViewById(R.id.btn_smart_layout_image_kit_run);
        tvServiceResult = findViewById(R.id.tv_imagekit_smart_layout_result);
        spinnerStyeList = findViewById(R.id.spinner_image_vision_style_list_image_kit);
    }

    /**
     * Initialize Listeners of UI Elements
     */
    private void initListener() {
        btnCamera.setOnClickListener(this);
        btnRunService.setOnClickListener(this);

        spinnerStyeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                style = spinnerStyeList.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "onNothingSelected");
            }
        });

        ivDocLink.setOnClickListener(v -> ApplicationUtils.openWebPage(SmartLayoutActivity.this,
                getString(R.string.txt_image_doc_link_image_kit)));
    }

    /**
     * Initialize Toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_smart_layout_image_kit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.StyleList));

        spinnerStyeList.setAdapter(adapter);
    }

    /**
     * Initialize Service
     *
     * @see "https://bit.ly/3nlEvIW"
     */
    private void initService() {

        // Obtain an ImageVisionImpl instance.
        imageVisionAPI = ImageVision.getInstance(this);

        final JSONObject authJson = ApplicationUtils.createAuthJson(this);

        imageVisionAPI.setVisionCallBack(new ImageVision.VisionCallBack() {
            @Override
            public void onSuccess(int successCode) {

                Log.i(TAG, "serviceInitialization - Smart Layout Service");

                int initCode = imageVisionAPI.init(getApplicationContext(), authJson);

                if (initCode == 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.msg_initialization_success_common_image_kit), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_callback_failed_common_image_kit) + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Open Gallery Function, necessary Android permissions should have taken before this process
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
     * To create required JSON parameter call this function
     */
    private JSONObject createRequestJSON() {

        JSONObject requestObject = new JSONObject();
        try {
            //requestID
            SecureRandom random = new SecureRandom();

            int rnd = random.nextInt(666);
            requestObject.put("requestId", rnd);

            //taskJson
            JSONObject taskJson = new JSONObject();

            taskJson.put("title", etTitle.getText().toString());
            taskJson.put("description", etDescription.getText());
            taskJson.put("copyRight", etCopyright.getText().toString());
            taskJson.put("anchor", etAnchor.getText().toString());
            taskJson.put("isNeedMask", cbIsNeedMask.isChecked());

            JSONArray jsonArray = new JSONArray();

            if (style != null && !style.equals(""))
                jsonArray.put(style);

            if (jsonArray.length() == 0)
                jsonArray.put("info8");


            taskJson.put("styleList", jsonArray);

            requestObject.put("taskJson", taskJson);

            /**
             * @implNote ApplicationUtils class contains functions that are used in common Image Kit Service functions
             */
            JSONObject authJson = ApplicationUtils.createAuthJson(this);

            TokenResponseModel tokenResponse = ApplicationUtils.getToken();

            if (tokenResponse != null) {
                authJson.put("token", tokenResponse.accessToken);
            }

            requestObject.put("authJson", authJson);

        } catch (JSONException ex) {
            Log.e(TAG, ex.toString());
        }

        Log.i(TAG, requestObject.toString());

        return requestObject;
    }

    /**
     * Smart Layout Service request, necessary parameters should take from user
     * To obtain view from Smart Layout Service call analyzeImageLayout(JSON,Bitmap)
     */
    private void requestSmartLayoutService() {

        if (etTitle.getText().toString().equals("") || etDescription.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_mandatory_input_smart_image_kit), Toast.LENGTH_LONG).show();
            return;
        }

        tvServiceResult.setText("");
        progressBar.setVisibility(View.VISIBLE);

        Bitmap bitmapImageView = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();


        Bitmap scaledImage = Bitmap.createScaledBitmap(ImageUtils.cutSmartLayoutImage(bitmapImageView),
                1080, 1920, false);

        Future<ImageLayoutInfo> infoFuture = Async.submit(() -> imageVisionAPI.analyzeImageLayout(
                createRequestJSON(),
                bitmapImageView));

        infoFuture.addSuccessCallback(result -> {

            progressBar.setVisibility(View.GONE);

            if (result.getResultCode() == 0) {
                tvServiceResult.setText(getString(R.string.txt_success_image_kit));
            } else {
                tvServiceResult.setText(getString(R.string.msg_service_result_smart_image_kit) + result.getResultCode());
            }

            setView(result, scaledImage);
        });

        infoFuture.addFailureCallback(t -> {
            progressBar.setVisibility(View.GONE);
            Log.e(TAG, t.toString());
        });
    }

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        final ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            final Network n = cm.getActiveNetwork();

            if (n != null) {
                final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        }

        return false;
    }

    /**
     * Show obtained layout on dialog
     * Received layout should show on full screen
     */
    private void setView(ImageLayoutInfo imageLayoutInfo, Bitmap bitmap) {
        AppSession.getInstance().setImageLayoutResultContext(SmartLayoutActivity.this);
        AppSession.getInstance().setImageLayoutInfo(imageLayoutInfo);
        AppSession.getInstance().setImageLayoutBitmap(bitmap);

        Intent intent = new Intent(SmartLayoutActivity.this, SmartLayoutResultActivity.class);
        startActivity(intent);
    }


    /**
     * Obtain data from Camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && imageUri != null) {
            Picasso.get()
                    .load(imageUri)
                    .fit()
                    .centerCrop()
                    .into(ivImage);

        }
    }


    /**
     * OnClick function of related UI Elements
     */
    @Override
    public void onClick(View view) {

        if (view == btnCamera) {
            openCamera();
        } else if (view == btnRunService) {

            if (imageUri != null) {
                if (isNetworkConnected()) {
                    requestSmartLayoutService();
                } else {
                    Toast.makeText(getApplicationContext(), "Service Does Not Work Stably If There Is No Internet Connection", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_pls_take_photo_common_image_kit), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
