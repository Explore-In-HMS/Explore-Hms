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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseModel;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseObjectList;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseObjectListBox;
import com.hms.explorehms.huawei.feature_imagekit.model.ThemeTaggingResponseTag;
import com.hms.explorehms.huawei.feature_imagekit.model.TokenResponseModel;
import com.hms.explorehms.huawei.feature_imagekit.ui.adapter.ThemeTaggingResultObjectListAdapter;
import com.hms.explorehms.huawei.feature_imagekit.ui.adapter.ThemeTaggingResultTagsListAdapter;
import com.hms.explorehms.huawei.feature_imagekit.utils.ApplicationUtils;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageUtils;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.huawei.hms.image.vision.ImageVision;
import com.huawei.hms.image.vision.ImageVisionImpl;
import com.huawei.hms.image.vision.bean.ImageVisionResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ThemeTaggingActivity extends AppCompatActivity {

    private static final String TAG = "IMAGEKIT";

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    /**
     * ImageVisionImp object. All Image Vision Service functions are called from this object
     */
    private ImageVisionImpl imageVisionAPI = null;

    /**
     * UI Elements
     */
    private LinearLayout llThemeTagging;
    private LinearLayout llThemeTaggingResultObjectList;
    private LinearLayout llThemeTaggingResultTags;

    private ListView lvThemeTaggingResult;
    private ListView lvThemeTaggingResultTag;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;

    private ImageView ivImage;

    private MaterialTextView tvResult;
    private MaterialTextView tvOperationResult;

    private ProgressBar progressBar;

    private MaterialButton btnRunService;
    private MaterialButton btnGetToken;

    private String requestToken;
    /**
     * Target image URI
     */
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_vision_theme_tagging);

        initUI();
        initListener();
        initService();
        setupToolbar();
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {
        btnGallery = findViewById(R.id.btn_theme_tagging_image_kit_gallery);
        btnCamera = findViewById(R.id.btn_theme_tagging_image_kit_camera);
        ivImage = findViewById(R.id.iv_image_theme_tagging);
        tvResult = findViewById(R.id.tv_imagekit_theme_tagging_result);
        tvOperationResult = findViewById(R.id.tv_imagekit_theme_tagging_operation_result);
        btnRunService = findViewById(R.id.btn_image_vision_theme_tagging_runservice);
        btnGetToken = findViewById(R.id.btn_image_vision_theme_tagging_gettoken);
        progressBar = findViewById(R.id.pb_imagekit_themetagging);
        lvThemeTaggingResult = findViewById(R.id.lv_imagekit_theme_tagging);
        lvThemeTaggingResultTag = findViewById(R.id.lv_imagekit_theme_tagging_tag);
        llThemeTagging = findViewById(R.id.ll_imagekit_theme_tagging_result_text);
        llThemeTaggingResultObjectList = findViewById(R.id.ll_imagekit_theme_tagging_result_object_list);
        llThemeTaggingResultTags = findViewById(R.id.ll_imagekit_theme_tagging_result_tags);
    }

    /**
     * Initialize Toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_theme_tagging_image_kit);
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
    private void initListener() {

        btnGallery.setOnClickListener(v -> openGallery());

        btnCamera.setOnClickListener(v -> openCamera());

        btnRunService.setOnClickListener(v -> {

            if (imageUri != null) {
                requestThemeTaggingService();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_photo_or_gallery_warning_theme_image_kit), Toast.LENGTH_SHORT).show();
            }

        });

        btnGetToken.setOnClickListener(v -> {
            getToken();
        });
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

                Log.i(TAG, "serviceInitialization - Theme Tagging Service");

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
     * To create required JSON parameter call this function
     */
    private JSONObject createRequestJSON() {

        JSONObject requestObject = new JSONObject();
        try {

            JSONObject taskJson = new JSONObject();

            taskJson.put("language", "en");
            taskJson.put("needObjectList", false);

            requestObject.put("taskJson", taskJson);
            requestObject.put("requestId", "1");

            /**
             * @implNote ApplicationUtils class contains functions that are used in common Image Kit Service functions
             */
            JSONObject authJson = ApplicationUtils.createAuthJson(this);

            if (requestToken != null && !requestToken.equals("")) {
                authJson.put("token", requestToken);
            } else {
                return null;
            }

            requestObject.put("authJson", authJson);

        } catch (JSONException ex) {
            Log.e(TAG, ex.toString());
        }

        Log.i(TAG, String.valueOf(requestObject));

        return requestObject;
    }

    private void getToken()  {
        Future<TokenResponseModel> token = Async.submit(ApplicationUtils::getToken);

        token.addSuccessCallback(result -> {
            if (result != null && result.accessToken != null && !result.accessToken.equals("")) {
                requestToken = result.accessToken;
                btnGetToken.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteImageKit));
                btnGetToken.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                vibrate();
                btnGetToken.setEnabled(false);
                btnGetToken.setText(getString(R.string.txt_token_obtained_image_kit));
            } else {
                Toast.makeText(getApplicationContext(), "Get Token Failed, Please Try Again", Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Theme Tagging Service request, necessary parameters should take from user
     * To obtain json from Smart Layout Service call analyzeImageLayout(JSON,Bitmap)
     */
    private void requestThemeTaggingService() {

        if (requestToken == null || requestToken.equals("")) {
            Toast.makeText(getApplicationContext(), "Get Token First", Toast.LENGTH_SHORT).show();
            return;
        }

        tvResult.setText("");
        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonObject = createRequestJSON();
        Bitmap bitmap = ImageUtils.uriToBitmap(getApplicationContext(), imageUri);

        new Thread(() -> {

            ImageVisionResult result = imageVisionAPI.analyzeImageThemeTag(jsonObject, bitmap);

            Log.i(TAG, new Gson().toJson(result));

            if (result != null && result.getResponse() != null && result.getResultCode() == 0) {

                ThemeTaggingResponseModel parsedResult = parseResponse(result);

                runOnUiThread(() -> {

                    if (parsedResult != null) { // if parse is successful
                        llThemeTagging.setVisibility(View.GONE);
                        setResultInfo(parsedResult);
                        setObjectListAdapter(parsedResult); //objectList
                        setTagsAdapter(parsedResult);
                    } else { // if parse failed, just show result as text
                        llThemeTagging.setVisibility(View.VISIBLE);

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(result.getResponse());

                        json = json.replace("nameValuePairs", ">");

                        tvResult.setText(json);
                    }

                    progressBar.setVisibility(View.GONE);
                });
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }).start();
    }

    /**
     * Show result info to user
     */
    private void setResultInfo(ThemeTaggingResponseModel result) {

        tvOperationResult.setVisibility(View.VISIBLE);

        tvOperationResult.setText(result.resultCode == 0 ? getString(R.string.txt_success_theme_image_kit) : String.valueOf(result.resultCode) + getString(R.string.txt_try_again_theme_image_kit));
    }

    /**
     * Show Result Tag To User Dynamically
     */
    private void setTagsAdapter(ThemeTaggingResponseModel result) {

        if (result.tags != null) {
            llThemeTaggingResultTags.setVisibility(View.VISIBLE);

            ArrayList<ThemeTaggingResponseTag> list = new ArrayList<>(result.tags);

            ThemeTaggingResultTagsListAdapter adapter = new ThemeTaggingResultTagsListAdapter(getApplicationContext(), list);
            lvThemeTaggingResultTag.setAdapter(adapter);
            getListViewSize(lvThemeTaggingResultTag);
            lvThemeTaggingResultTag.invalidate();
        } else {
            llThemeTaggingResultTags.setVisibility(View.GONE);
        }
    }

    /**
     * Show Result Object To User Dynamically
     */
    private void setObjectListAdapter(ThemeTaggingResponseModel result) {

        if (result.objectList != null) {
            llThemeTaggingResultObjectList.setVisibility(View.VISIBLE);

            ArrayList<ThemeTaggingResponseObjectList> list = new ArrayList<>(result.objectList);
            ThemeTaggingResultObjectListAdapter adapter = new ThemeTaggingResultObjectListAdapter(getApplicationContext(), list, new ThemeTaggingResultObjectListAdapter.ThemeTaggingResultObjectAdapterListener() {
                @Override
                public void onShow(ThemeTaggingResponseObjectListBox box) {
                    if (box != null) {
                        drawBoxOnImageView(box);
                    }
                }
            });
            lvThemeTaggingResult.setAdapter(adapter);
            getListViewSize(lvThemeTaggingResult);
            lvThemeTaggingResult.invalidate();
        } else {
            llThemeTaggingResultObjectList.setVisibility(View.GONE);
        }
    }

    /**
     * Show Objects Area On ImageView
     */
    private void drawBoxOnImageView(ThemeTaggingResponseObjectListBox box) {
        Bitmap bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();

        final Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(mutableBitmap);

        Rect rectangle = getRect(mutableBitmap, box);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        c.drawRect(rectangle.left, rectangle.top, rectangle.right, rectangle.bottom, paint);

        ivImage.post(() -> ivImage.setImageBitmap(mutableBitmap));
    }

    /**
     * Get rect object from response to specify found objects on image
     *
     * @param bitmap Bitmap of image
     * @param box    ThemeTaggingResponseObject response box object
     * @return
     */
    private Rect getRect(Bitmap bitmap, ThemeTaggingResponseObjectListBox box) {
        double width = box.width;
        double height = box.height;
        double centerX = box.centerX;
        double centerY = box.centerY;

        int left = (int) (centerX * bitmap.getWidth() - 0.5 * width * bitmap.getWidth());
        int top = (int) (centerY * bitmap.getHeight() - 0.5 * height * bitmap.getHeight());
        int right = (int) (centerX * bitmap.getWidth() + 0.5 * width * bitmap.getWidth());
        int bottom = (int) (centerY * bitmap.getHeight() + 0.5 * height * bitmap.getHeight());
        return new Rect(left, top, right, bottom);
    }

    /**
     * Parse Json Response That Obtained From Theme Tagging Activity
     */
    private ThemeTaggingResponseModel parseResponse(ImageVisionResult result) {

        ThemeTaggingResponseModel model = null;

        try {
            Gson builder = new GsonBuilder().create();
            JSONObject jsonObject = result.getResponse();
            model = builder.fromJson(jsonObject.toString(), ThemeTaggingResponseModel.class);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }

        return model;
    }

    /**
     * To Configure UI
     */
    public void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

    @SuppressLint("MissingPermission")
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect effect = VibrationEffect.createOneShot(500, 2);
        v.vibrate(effect);
    }

    /**
     * Obtain data from Camera
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
