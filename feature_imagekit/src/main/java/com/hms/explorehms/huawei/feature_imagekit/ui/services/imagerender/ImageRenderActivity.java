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

package com.hms.explorehms.huawei.feature_imagekit.ui.services.imagerender;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_imagekit.R;
import com.hms.explorehms.huawei.feature_imagekit.utils.ApplicationUtils;
import com.hms.explorehms.huawei.feature_imagekit.utils.FileUtils;
import com.hms.explorehms.huawei.feature_imagekit.utils.ImageRenderUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hms.image.render.IStreamCallBack;
import com.huawei.hms.image.render.ImageRender;
import com.huawei.hms.image.render.ImageRenderImpl;
import com.huawei.hms.image.render.RenderView;
import com.huawei.hms.image.render.ResultCode;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ImageRenderActivity extends AppCompatActivity {

    private static final String TAG = "IMAGEKIT";
    private static final String SOURCE_PATH = "sources";

    /**
     * Source path of target animations
     */
    private String sourcePath;

    /**
     * ImageVisionImp object. Image Render Service function are called from this object
     */
    private ImageRenderImpl imageRenderAPI;

    /**
     * UI Elements
     */
    private FrameLayout contentView;

    private MaterialButton btnStartAnimation;
    private MaterialButton btnStopAnimation;
    private MaterialButton btnPauseAnimation;
    private MaterialButton btnResumeAnimation;

    private ImageView btnStartRecording;

    private Spinner spinnerRenderAnimation;

    private MaterialTextView tvAnimationInfo;
    private MaterialTextView tvRecordProgress;

    private boolean recording = false;

    private AlertDialog savingDialog;

    private String mp4Path;
    private String gifPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_render_service);

        sourcePath = getFilesDir().getPath() + File.separator + SOURCE_PATH;

        initUI();
        initListener();
        initService();
        setupToolbar();
        initData();
    }

    /**
     * Initialize UI Elements
     */
    private void initUI() {
        contentView = findViewById(R.id.fl_image_render);
        btnStartAnimation = findViewById(R.id.btn_start_render_animation);
        btnStopAnimation = findViewById(R.id.btn_stop_render_animation);
        btnPauseAnimation = findViewById(R.id.btn_pause_render_animation);
        btnResumeAnimation = findViewById(R.id.btn_resume_render_animation);
        spinnerRenderAnimation = findViewById(R.id.spin_render_animation);
        tvAnimationInfo = findViewById(R.id.tv_animation_info_image_kit);
        btnStartRecording = findViewById(R.id.iv_image_render_record_animation);
        tvRecordProgress = findViewById(R.id.tv_image_render_record_status_progress);

        setSpinnerAdapter();
    }

    /**
     * Initialize Toolbar
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_render_service_image_kit);
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

        spinnerRenderAnimation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeAnimation(spinnerRenderAnimation.getAdapter().getItem(position).toString().replace(" ", ""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(TAG, "onNothingSelected");
            }
        });

        btnStartAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAnimation();
            }
        });

        btnPauseAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAnimation();
            }
        });

        btnResumeAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeAnimation();
            }
        });

        btnStopAnimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAnimation();
            }
        });

        btnStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            spinnerRenderAnimation.setVisibility(View.GONE);
                            btnStartRecording.setVisibility(View.GONE);
                            startRecord();
                            recording = true;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageRenderActivity.this);
                builder.setMessage(getString(R.string.msg_start_record_warning_render_image_kit))
                        .setPositiveButton(getString(R.string.txt_yes_image_kit), dialogClickListener)
                        .setNegativeButton(R.string.txt_no_image_kit, dialogClickListener)
                        .show();
            }
        });
    }

    /**
     * Set Spinner With Animations Name
     */
    private void setSpinnerAdapter() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.Animations));

        spinnerRenderAnimation.setAdapter(adapter);
    }


    /**
     * Initialize Service
     *
     * @see "https://bit.ly/3nhy61B"
     */
    private void initService() {

        // Obtain an ImageRender instance.
        ImageRender.getInstance(this, new ImageRender.RenderCallBack() {
            @Override
            public void onSuccess(ImageRenderImpl imageRender) {
                Log.i(TAG, "serviceInitialization - Image Render Service");
                Toast.makeText(getApplicationContext(), getString(R.string.msg_initialization_success_common_image_kit), Toast.LENGTH_SHORT).show();
                imageRenderAPI = imageRender;
            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getApplicationContext(), getString(R.string.msg_callback_failed_common_image_kit) + i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * To Create Path for Animations
     */
    private void initData() {
        // Absolute path of the resource files.
        if (!FileUtils.createResourceDirs(sourcePath)) {
            Log.e(TAG, "Create dirs fail, please check permission");
        }
    }

    /**
     * To play basic animations, such as rotation, translation, and scaling, are not played by default
     */
    private void playAnimation() {

        if (null != imageRenderAPI) {
            int playResult = imageRenderAPI.playAnimation();
            if (playResult == ResultCode.SUCCEED) {
                Log.i(TAG, "Start animation success");

                btnStartAnimation.setEnabled(false);
                btnPauseAnimation.setEnabled(true);
                btnResumeAnimation.setEnabled(false);
                btnStopAnimation.setEnabled(true);

            } else {
                Log.i(TAG, "Start animation failure");
            }
        } else {
            Log.w(TAG, "Start animation fail, please init first.");
        }
    }

    /**
     * Function for the API pauses all basic animations and starts playing paused animations
     */
    private void pauseAnimation() {
        if (null != imageRenderAPI) {
            int pauseResult = imageRenderAPI.pauseAnimation(true); // If isEnable is true, all basic animations are paused, and paused animations start to play
            if (pauseResult == ResultCode.SUCCEED) {
                Log.i(TAG, "Pause animation success");

                btnStartAnimation.setEnabled(false);
                btnPauseAnimation.setEnabled(false);
                btnResumeAnimation.setEnabled(true);
                btnStopAnimation.setEnabled(true);
            } else {
                Log.i(TAG, "Pause animation failure");
            }
        } else {
            Log.w(TAG, "Pause animation fail, please init first.");
        }
    }

    /**
     * To resume paused animations
     */
    private void resumeAnimation() {
        if (null != imageRenderAPI) {
            int resumeResult = imageRenderAPI.resumeAnimation();
            if (resumeResult == ResultCode.SUCCEED) {
                Log.i(TAG, "Resume animation success");

                btnStartAnimation.setEnabled(false);
                btnPauseAnimation.setEnabled(true);
                btnResumeAnimation.setEnabled(false);
                btnStopAnimation.setEnabled(true);

            } else {
                Log.i(TAG, "Resume animation failure");
            }
        } else {
            Log.w(TAG, "Resume animation fail, please init first.");
        }
    }

    /**
     * To stop animations
     */
    private void stopAnimation() {
        if (null != imageRenderAPI) {
            int stopResult = imageRenderAPI.stopAnimation();
            if (stopResult == ResultCode.SUCCEED) {
                Log.i(TAG, "Stop animation success");

                btnStartAnimation.setEnabled(true);
                btnPauseAnimation.setEnabled(false);
                btnResumeAnimation.setEnabled(false);
                btnStopAnimation.setEnabled(false);
            } else {
                Log.i(TAG, "Stop animation failure");
            }
        } else {
            Log.w(TAG, "Stop animation fail, please init first.");
        }
    }

    /**
     * Call startRecord to start animation recording.
     * Only the .mp4 and .gif formats are supported.
     * The input JSON parameters include the recording type, compression rate, and frame rate.
     * Image Kit returns the operation result through IStreamCallBack
     */
    private void startRecord() {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonVideo = new JSONObject();
            JSONObject jsonGif = new JSONObject();

            /**
             * 1 : video
             * 2 : gif
             * 3 : video and gif
             */
            jsonObject.put("recordType", 3);

            /**
             * Video compression rate
             * Range is [0.5-1]
             * @implNote mandatory for video recording
             */
            jsonVideo.put("videoScale", 0.5);
            /**
             * Video frame rate
             * Range is [5-20]
             * @implNote mandatory for video recording
             */
            jsonVideo.put("videoFps", 16);

            /**
             * GIF compression rate
             * Range is [0.1-0.5]
             * @implNote mandatory for GIF image recording
             */
            jsonGif.put("gifScale", 0.5);
            /**
             * GIF frame rate
             * Range is [10-20]
             * @implNote mandatory for GIF image recording
             */
            jsonGif.put("gifFps", 16);

            jsonObject.put("video", jsonVideo);
            jsonObject.put("gif", jsonGif);

            int resultCode = imageRenderAPI.startRecord(jsonObject, new IStreamCallBack() {
                @Override
                public void onRecordSuccess(HashMap<String, Object> hashMap) {
                    runOnUiThread(() -> {
                        if (recording) {
                            Toast.makeText(ImageRenderActivity.this, getString(R.string.msg_record_success_render_image_kit), Toast.LENGTH_SHORT).show();
                            saveRecordResult(hashMap);
                        }

                        tvRecordProgress.setText(getString(R.string.txt_not_recording_image_render_image_kit));
                        spinnerRenderAnimation.setVisibility(View.VISIBLE);
                        btnStartRecording.setVisibility(View.VISIBLE);

                        recording = false;

                        savingDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(ImageRenderActivity.this);
                        builder.setMessage("Video Path : " + mp4Path + "\n\nGif Path : " + gifPath)
                                .setPositiveButton(getString(R.string.txt_ok_image_kit), null)
                                .show();
                    });
                }

                @Override
                public void onRecordFailure(HashMap<String, Object> hashMap) {
                    int errorCode = (int) hashMap.get("errorCode");
                    String errorMessage = (String) hashMap.get("errorMessage");
                    Log.i(TAG, "Back result" + errorCode + ";Back msg" + errorMessage);


                    tvRecordProgress.setText(getString(R.string.txt_not_recording_image_render_image_kit));

                    spinnerRenderAnimation.setVisibility(View.VISIBLE);
                    btnStartRecording.setVisibility(View.VISIBLE);

                    recording = false;

                    savingDialog.dismiss();
                }

                @Override
                public void onProgress(int i) {
                    runOnUiThread(() -> {
                        tvRecordProgress.setText(String.format(getString(R.string.txt_recording_image_render_image_kit), i));
                        if (i == 100)
                            tvRecordProgress.setText(getString(R.string.txt_not_record_process_finished_image_kit));
                    });

                    Log.i(TAG, String.format("Progress : %s", i));
                }
            });


            if (resultCode == ResultCode.SUCCEED) {

                Log.i(TAG, "Start record success");

                //Stop recording after 10 second, max limit is 15 sec
                new Handler(Looper.getMainLooper()).postDelayed(this::stopRecord, 10000);

            } else {
                Toast.makeText(getApplicationContext(), "Start record failure, please try again later", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Start record failure:" + resultCode);
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Call stopRecord to stop animation recording.
     * If this API is not called when the recording lasts for 15s, the recording automatically stops
     */
    private void stopRecord() {
        if (null != imageRenderAPI) {

            setProgressDialog();

            int stopRecordResult = imageRenderAPI.stopRecord();
            if (stopRecordResult == ResultCode.SUCCEED) {
                spinnerRenderAnimation.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), getString(R.string.msg_record_stopped_render_image_kit), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Stop record success");

            } else {
                Log.i(TAG, "Stop record failure");
            }
        } else {
            Log.w(TAG, "Stop record fail, please init first.");
        }
    }

    /**
     * Function for Image Render service parses the image and script in sourcePath and returns the rendered views to the app
     *
     * @param animationName target animation name
     */
    private void changeAnimation(String animationName) {

        if (!FileUtils.copyAssetsFilesToDirs(this, animationName, sourcePath)) {
            Log.e(TAG, "copy files failure, please check permissions");
            return;
        }

        if (imageRenderAPI == null) {
            Log.e(TAG, "initRemote fail, please check kit version");
            return;
        }

        if (contentView.getChildCount() > 0) {
            contentView.removeAllViews();
            try {
                imageRenderAPI.removeRenderView();
            } catch (NullPointerException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        if (imageRenderAPI != null) {
            int initResult = imageRenderAPI.doInit(sourcePath, ApplicationUtils.createAuthJson(this));

            if (initResult == 0) {
                // Obtain the rendered view.
                RenderView renderView = imageRenderAPI.getRenderView();

                if (renderView.getResultCode() == ResultCode.SUCCEED) {
                    View view = renderView.getView();
                    if (null != view) {
                        // Add the rendered view to the layout.
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.topToBottom = R.id.seperator_filter_service_image_kit_info;
                        view.setLayoutParams(params);

                        contentView.addView(view);

                        btnPauseAnimation.setEnabled(false);
                        stopAnimation();
                    } else {
                        Log.w(TAG, "GetRenderView fail, view is null");
                    }
                }
            }

            String animationInfo = ImageRenderUtils.getAnimationInfo(animationName, getApplicationContext());
            tvAnimationInfo.setText(animationInfo);

            if (!animationName.contains("Animation")) {
                btnStartAnimation.setVisibility(View.GONE);
                btnPauseAnimation.setVisibility(View.GONE);
                btnResumeAnimation.setVisibility(View.GONE);
                btnStopAnimation.setVisibility(View.GONE);
            } else {
                btnStartAnimation.setEnabled(true);
                btnPauseAnimation.setEnabled(false);
                btnResumeAnimation.setEnabled(false);
                btnStopAnimation.setEnabled(false);

                btnStartAnimation.setVisibility(View.VISIBLE);
                btnPauseAnimation.setVisibility(View.VISIBLE);
                btnResumeAnimation.setVisibility(View.VISIBLE);
                btnStopAnimation.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Initialize Service
     * To save animation record result that obtains from ImageRenderAPI's IStreamCallBack()
     *
     * @param hashMap Record result of IStreamCallBack
     */
    private void saveRecordResult(HashMap<String, Object> hashMap) {
        String fileName = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + File.separator
                + "VideoAndPic";
        File fileDir = new File(fileName);
        if (!fileDir.exists()) {
            if (!fileDir.mkdir()) {
                return;
            }
        }
        mp4Path = fileName + File.separator + System.currentTimeMillis() + ".mp4";
        gifPath = fileName + File.separator + System.currentTimeMillis() + ".gif";
        String recordType = (String) hashMap.get("recordType");
        byte[] videoBytes = (byte[]) hashMap.get("videoBytes");
        byte[] gifBytes = (byte[]) hashMap.get("gifBytes");
        try {
            if (recordType != null && recordType.equals("1")) {
                if (videoBytes != null) {
                    saveFile(videoBytes, mp4Path);
                }
            } else if (recordType != null && recordType.equals("2")) {
                if (gifBytes != null) {
                    saveFile(gifBytes, gifPath);
                }
            } else if (recordType != null && recordType.equals("3")) {
                if (videoBytes != null) {
                    saveFile(videoBytes, mp4Path);
                }

                if (gifBytes != null) {
                    saveFile(gifBytes, gifPath);
                }
            }
        } catch (IOException e) {
            Log.w(TAG, e.toString());
        }
    }

    /**
     * Initialize Service
     * Save animation to external file path
     *
     * @param bytes bytes of animation record
     * @param path  target path where the animation's record will be saved
     */
    private void saveFile(byte[] bytes, String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(path));
        try {
            fos.write(bytes, 0, bytes.length);
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * Bind rendered views on onResume cycle
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (null != imageRenderAPI) {
            /**
             * To bindview use this
             */
//            imageRenderAPI.bindRenderView(sourcePath, ApplicationUtils.createAuthJson(), new IBindCallBack() {
//                @Override
//                public void onBind(RenderView renderView, int i) {
//                    if (renderView != null) {
//                        if (renderView.getResultCode() == ResultCode.SUCCEED) {
//                            final View view = renderView.getView();
//                            if (null != view) {
//                                /**
//                                 * To bindview use this
//                                 */
//                                //contentView.addView(view);
//                                hashCodeRenderView = String.valueOf(view.hashCode());
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onParseEnd() {
//
//                }
//            });
        }
    }

    public void setProgressDialog() {

        if (savingDialog == null) {
            int llPadding = 30;
            RelativeLayout rl = new RelativeLayout(ImageRenderActivity.this);
            rl.setPadding(llPadding, llPadding, llPadding, llPadding);
            rl.setGravity(Gravity.CENTER);

            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rlParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            rl.setLayoutParams(rlParams);


            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setIndeterminate(true);
            progressBar.setPadding(0, 0, llPadding, 0);
            progressBar.setLayoutParams(rlParams);

            rl.addView(progressBar);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setView(rl);
            builder.setCancelable(false);

            savingDialog = builder.create();
        }

        savingDialog.show();
        Window window = savingDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(savingDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            savingDialog.getWindow().setAttributes(layoutParams);
        }
    }

    /**
     * If a rendered view is no longer needed, call the removeRenderView() API on onPause
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (null != imageRenderAPI) {
            /**
             * To bindview use this
             */
//            int result = imageRenderAPI.unBindRenderView(hashCodeRenderView);
        }
    }

    @Override
    public void onBackPressed() {
        if (recording) {
            Toast.makeText(getApplicationContext(), "Please wait until record completed", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }


}
