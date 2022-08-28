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
package com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.video_technology.video_summarization;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_hiai.R;
import com.hms.explorehms.huawei.feature_hiai.adapter.list.VideoSummarizationResultVideoRecylerViewAdapter;
import com.hms.explorehms.huawei.feature_hiai.constant.ServiceGroupConstants;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceActivity;
import com.hms.explorehms.huawei.feature_hiai.ui.hiaiserviceviews.base.BaseServiceInterface;
import com.hms.explorehms.huawei.feature_hiai.utils.DialogUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.image.detector.AestheticsScoreDetector;
import com.huawei.hiai.vision.visionkit.common.Video;
import com.huawei.hiai.vision.visionkit.image.detector.AEModelConfiguration;
import com.huawei.hiai.vision.visionkit.image.detector.AEVideoResult;
import com.huawei.hiai.vision.visionkit.image.detector.ImageDetectType;
import com.huawei.hiai.vision.visionkit.image.detector.aestheticsmodel.AestheticVideoSummerization;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class VideoSummarizationActivity extends BaseServiceActivity implements BaseServiceInterface {

    private static final int GALLERY_REQUEST = 99;
    private static final int CAMERA_REQUEST = 100;

    private Uri videoUri;

    private MaterialButton btnGallery;
    private MaterialButton btnCamera;
    private MaterialButton btnRunService;
    private MaterialButton btnWatchLiveCover;

    private RecyclerView rvSummarizedVideos;

    private MaterialTextView tvVideoPath;
    private MaterialTextView tvSummarizedVideoCount;
    private MaterialTextView tvVideoLiveCoverStartEnd;
    private MaterialTextView tvVideoStaticCoverTime;

    private ProgressBar pbRunService;

    private final StringBuilder tvVideos = new StringBuilder();
    private final ArrayList<Uri> videos = new ArrayList<>();

    private VideoView videoViewLiveCover;
    private VideoView videoViewStaticCover;

    private ConstraintLayout clVideoViewLiveCover;
    private ConstraintLayout clViewStaticCover;


    public VideoSummarizationActivity() {
        super(ServiceGroupConstants.VIDEO);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_summarization_hiai);
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
        btnGallery = findViewById(R.id.btn_video_summarization_gallery);
        btnCamera = findViewById(R.id.btn_video_summarization_camera);
        tvVideoPath = findViewById(R.id.tv_video_summarization_video_paths);
        btnRunService = findViewById(R.id.btn_video_summarization_run);
        rvSummarizedVideos = findViewById(R.id.rv_video_summarization_videos_hiai);
        tvSummarizedVideoCount = findViewById(R.id.tv_video_summarization_count);
        pbRunService = findViewById(R.id.pb_video_summarization_async);
        videoViewLiveCover = findViewById(R.id.vw_video_summarization_live_cover_video);
        btnWatchLiveCover = findViewById(R.id.btn_video_summarization_run_live_cover_video);
        videoViewStaticCover = findViewById(R.id.vw_video_summarization_static_cover_video);
        clVideoViewLiveCover = findViewById(R.id.cl_video_summarization_live_cover_video);
        clViewStaticCover = findViewById(R.id.cl_video_summarization_static_cover_video);
        tvVideoLiveCoverStartEnd = findViewById(R.id.tv_video_summarization_live_cover_start_end_video);
        tvVideoStaticCoverTime = findViewById(R.id.tv_video_summarization_static_cover_video);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tb_video_summarization_hiai);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.txt_video_summarization_doc_link_hiai));
    }

    @Override
    public void initListeners() {

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

        btnRunService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getResult();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_error_occurred_check_rest_hiai) + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        rvSummarizedVideos.setNestedScrollingEnabled(false);


    }

    @Override
    public void initService() {
        VisionBase.init(VideoSummarizationActivity.this, new ConnectionCallback() {
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Video");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");

        videoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void getResult() {

        if (videos != null && videos.size() > 0) {

            pbRunService.setVisibility(View.VISIBLE);
            btnGallery.setEnabled(false);
            btnCamera.setEnabled(false);
            btnRunService.setEnabled(false);

            AestheticsScoreDetector aestheticsScoreDetector = new AestheticsScoreDetector(VideoSummarizationActivity.this);

            AEModelConfiguration aeModelConfiguration = new AEModelConfiguration();

            aeModelConfiguration.getSummerizationConf().setSummerizationMaxLen(10); //10s
            aeModelConfiguration.getSummerizationConf().setSummerizationMinLen(2); //2s

            aeModelConfiguration.getLiveCoverConf().setLiveCoverMaxLen(10);
            aeModelConfiguration.getDetectVideoConf().setDetectVideoStatReptInterval(10);

            aestheticsScoreDetector.setAeModelConfiguration(aeModelConfiguration);

            String[] sVideos = new String[videos.size()];
            int c = 0;

            for (Uri _video : videos) {
                Video video = new Video();

                grantUriPermission("com.huawei.hiai", _video, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                video.setPath(_video.toString());

                sVideos[c] = video.getPath();

                c++;
            }

            AEVideoResult aeVideoResult = new AEVideoResult();
            AEVideoResult aesVideoResultTemp = new AEVideoResult();

            new Thread(() -> {
                int resultCodeSummarization = aestheticsScoreDetector.getCover(sVideos, aesVideoResultTemp, ImageDetectType.TYPE_IMAGE_GET_VIDEO_SUMMERIZATION, null);

                aeVideoResult.setVideoSummerization(aesVideoResultTemp.getVideoSummerization());

                int resultCodeLiveCover = aestheticsScoreDetector.getCover(sVideos, aesVideoResultTemp, ImageDetectType.TYPE_IMAGE_GET_VIDEO_LIVE_COVER, null);

                aeVideoResult.setVideoLiveCover(aesVideoResultTemp.getVideoLiveCover());

                int resultCodeStaticCover = aestheticsScoreDetector.getCover(sVideos, aesVideoResultTemp, ImageDetectType.TYPE_IMAGE_GET_VIDEO_STATIC_COVER, null);

                aeVideoResult.setVideoStaticCover(aesVideoResultTemp.getVideoStaticCover());


                if (resultCodeSummarization == 0 && resultCodeLiveCover == 0 && resultCodeStaticCover == 0) {

                    runOnUiThread(() -> {
                        tvSummarizedVideoCount.setText(String.format(getString(R.string.txt_video_summarization_summary_video_parts_count_hiai), aeVideoResult.getVideoSummerization().size()));

                        setSummarizationResult(aeVideoResult.getVideoSummerization());
                        setLiveCoverResult(aeVideoResult.getVideoLiveCover());
                        setStaticCoverResult(aeVideoResult.getVideoStaticCover());


                        pbRunService.setVisibility(View.GONE);
                        btnGallery.setEnabled(true);
                        btnCamera.setEnabled(true);
                        btnRunService.setEnabled(true);
                    });
                }
            }).start();

        } else {
            showVideoAlert();
        }
    }

    private void setSummarizationResult(List<AestheticVideoSummerization> summarizationResult) {
        VideoSummarizationResultVideoRecylerViewAdapter adapter = new VideoSummarizationResultVideoRecylerViewAdapter(getApplicationContext(), summarizationResult);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvSummarizedVideos.setLayoutManager(mLayoutManager);
        rvSummarizedVideos.setAdapter(adapter);
    }

    public void setLiveCoverResult(Pair<Long, Long> liveCoverResult) {

        clVideoViewLiveCover.setVisibility(View.VISIBLE);
        btnWatchLiveCover.setVisibility(View.VISIBLE);

        tvVideoLiveCoverStartEnd.setVisibility(View.VISIBLE);
        tvVideoLiveCoverStartEnd.setText(String.format(getString(R.string.txt_video_summarization_summary_video_start_end_time_hiai), liveCoverResult.first / 1000, liveCoverResult.second / 1000));

        videoViewLiveCover.setVideoPath(videos.get(0).toString());
        videoViewLiveCover.seekTo(Math.toIntExact(liveCoverResult.first));

        btnWatchLiveCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoViewLiveCover.start();

                videoViewLiveCover.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int currentPos = videoViewLiveCover.getCurrentPosition();

                        if (liveCoverResult.second <= currentPos) {
                            videoViewLiveCover.stopPlayback();
                        } else {
                            videoViewLiveCover.postDelayed(this, 500);
                        }
                    }
                }, 500);
            }
        });

    }

    public void setStaticCoverResult(Long resultTime) {

        clViewStaticCover.setVisibility(View.VISIBLE);
        tvVideoStaticCoverTime.setVisibility(View.VISIBLE);

        tvVideoStaticCoverTime.setText(String.format(getString(R.string.txt_video_summarization_summary_static_time_hiai), resultTime / 1000));
        videoViewStaticCover.setVideoPath(videos.get(0).toString());
        videoViewStaticCover.seekTo(Math.toIntExact(resultTime));
        videoViewStaticCover.stopPlayback();

    }

    private boolean checkMimeType() {
        try {
            ContentResolver cR = getApplicationContext().getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getExtensionFromMimeType(cR.getType(videoUri));

            if (type != null && type.equals("mp4")) {
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        DialogUtils.createInfoDialog(VideoSummarizationActivity.this,
                "Format Invalid",
                "Video format must be mp4!");

        videoUri = null;

        return false;
    }

    private void addVideo() {
        tvVideos.append("\t");
        tvVideos.append(videoUri.getPath());
        tvVideos.append("\n");

        videos.add(videoUri);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && videoUri != null) {
            addVideo();
            tvVideoPath.setText(tvVideos.toString());
        } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            videoUri = data.getData();

            if (videoUri != null) {
                if (checkMimeType()) {
                    addVideo();
                }

                tvVideoPath.setText(tvVideos.toString());
            }

        } else
            showVideoAlert();
    }
}
