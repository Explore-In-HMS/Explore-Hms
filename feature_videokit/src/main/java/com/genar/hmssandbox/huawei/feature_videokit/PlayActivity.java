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
package com.genar.hmssandbox.huawei.feature_videokit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_videokit.file_readers.FileReadUtil;
import com.genar.hmssandbox.huawei.feature_videokit.file_readers.VideoItemClass;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DeviceUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DialogUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.TimeUtil;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements Callback, PlayAdapter.OnItemClickListener {

    private boolean hasVideoEverStarted = false;
    private final Handler mHandler = new Handler();
    WisePlayer player;
    SurfaceView surfaceView;
    List<VideoItemClass> myVideoList = new ArrayList<>();
    RecyclerView videoListRecyclerView;
    TextView progressTextView;
    TextView totalTextView;
    ImageView playImageView;
    TextView playSpeedTextView;
    Button fullscreenButton;
    SeekBar seekBar;
    TextView videoTitleTextView;
    VideoItemClass currentPlayItem;
    boolean isReallyPlaying = false;
    DialogUtil dialogUtil = new DialogUtil();
    ConstraintLayout playDetailsConstraintLayout;
    ConstraintLayout outerConstraintLayout;
    ConstraintLayout videoTitleEtcConstraintLayout;
    FrameLayout frameLayout;
    ImageView backArrowImageView;
    boolean isPortrait;
    RelativeLayout videoBufferLayout;
    TextView percentTextView;
    SurfaceHolder surfaceHolder;
    ConstraintSet beforeClickingSurfaceViewSet = new ConstraintSet();
    int onClickedPos;
    WisePlayer.ReadyListener myReadyListener = new WisePlayer.ReadyListener() {
        @Override
        public void onReady(WisePlayer wisePlayer) {
            startPlaying();

            runOnUiThread(() -> updatePlayView(player));
        }
    };
    WisePlayer.LoadingListener myLoadingListener = new WisePlayer.LoadingListener() {
        @Override
        public void onLoadingUpdate(WisePlayer wisePlayer, int percent) {
            runOnUiThread(() -> {
                if (percent < 100) {
                    updateBufferingView(percent);
                } else {
                    videoBufferLayout.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onStartPlaying(WisePlayer wisePlayer) {
            Log.i(this.getClass().getSimpleName(), "onStartPlaying");
        }
    };
    WisePlayer.EventListener myEventListener = (wisePlayer, what, extra, o) -> {
        Log.i("onEvent", "what parameter is this: " + what);
        if (what == 215) {
            showToast("Some of the current video will be played from cache!");
            return true;
        }
        return false;
    };
    WisePlayer.SeekEndListener mySeekEndListener = wisePlayer -> {
        //...
    };
    WisePlayer.ResolutionUpdatedListener myResolutionUpdatedListener = (wisePlayer, i, i1) -> {
        //...
    };
    // Vertical screen properties
    private int systemUiVisibility = 0;
    WisePlayer.PlayEndListener myPlayEndListener = new WisePlayer.PlayEndListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onPlayEnd(WisePlayer wisePlayer) {
            isReallyPlaying = false;
            progressTextView.setText(TimeUtil.formatLongToTimeStr(0));
            seekBar.setProgress(0);
            playImageView.setImageDrawable(getDrawable(R.drawable.ic_play));
            setPortraitView();
        }
    };
    WisePlayer.ErrorListener myErrorListener = (wisePlayer, what, extra) -> {
        videoBufferLayout.setVisibility(View.GONE);

        //code 1099 : unknown error.
        //a self fix for an unknown error.
        if (what == 1099) {
            player.stop();
            player.release();
            initPlayer();
            onItemClick(myVideoList, onClickedPos);
        } else {
            showToast("Error code is " + what + ". Sub-code is " + extra + " .");
        }
        return false;
    };

    public static void setMargins(View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }

    public void startPlaying() {
        if (player != null) {
            player.start();
            isReallyPlaying = true;
        }
    }

    public void pausePlaying() {
        if (player != null) {
            player.pause();
            isReallyPlaying = false;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_videokit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void updatePlayView(WisePlayer wisePlayer) {
        if (wisePlayer != null) {
            int totalTime = wisePlayer.getDuration();
            seekBar.setMax(totalTime);
            totalTextView.setText(TimeUtil.formatLongToTimeStr(totalTime));
            progressTextView.setText(TimeUtil.formatLongToTimeStr(0));
            seekBar.setProgress(0);
            videoTitleTextView.setText(currentPlayItem.getTitle());
            playImageView.setImageDrawable(getDrawable(R.drawable.ic_full_screen_suspend_normal));
        }
    }

    public void runOnUIThreadForMe() {
        PlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player != null && isReallyPlaying) {
                    updatePlayProgressView(player.getCurrentTime(), player.getBufferTime());
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    public void removeMyCallbacks() {
        mHandler.removeCallbacks(null);
    }

    public void initViews() {
        surfaceView = findViewById(R.id.surfaceView);
        videoListRecyclerView = findViewById(R.id.videoListRecyclerView);
        progressTextView = findViewById(R.id.progressTextView);
        totalTextView = findViewById(R.id.totalTextView);
        playImageView = findViewById(R.id.playImageView);
        playSpeedTextView = findViewById(R.id.playSpeedTextView);
        fullscreenButton = findViewById(R.id.fullscreenButton);
        seekBar = findViewById(R.id.seekBar);
        videoTitleTextView = findViewById(R.id.videoTitleTextView);
        playDetailsConstraintLayout = findViewById(R.id.playDetailsConstraintLayout);
        frameLayout = findViewById(R.id.frameLayout);
        outerConstraintLayout = findViewById(R.id.outerConstraintLayout);
        videoTitleEtcConstraintLayout = findViewById(R.id.videoTitleEtcConstraintLayout);
        backArrowImageView = findViewById(R.id.backArrowImageView);
        isPortrait = DeviceUtil.isPortrait(getApplicationContext());
        systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        videoBufferLayout = findViewById(R.id.buffer_rl);
        videoBufferLayout.setVisibility(View.GONE);
        percentTextView = findViewById(R.id.play_process_buffer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        setupToolbar();

        initViews();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!isPortrait)
            setPortraitView();

        initPlayer();

        runOnUIThreadForMe();

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        try {
            myVideoList = FileReadUtil.videoListRetriever(this);
        } catch (JSONException e) {
            showToast("JSON FILE CANNOT BE READ!");
            e.printStackTrace();
        }

        if (myVideoList != null && isPortrait) {
            PlayAdapter playAdapter = new PlayAdapter(myVideoList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            videoListRecyclerView.setLayoutManager(layoutManager);
            playAdapter.setOnItemClickListener(this);
            videoListRecyclerView.setAdapter(playAdapter);
        } else {
            showToast("Video List is null.");
        }

        playImageView.setOnClickListener(view -> {
            if (player != null && hasVideoEverStarted) {
                playButtonChangerAndStartPauseVideo();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(this.getClass().getSimpleName(), "onProgressChanged");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(this.getClass().getSimpleName(), "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null && hasVideoEverStarted && dialogUtil.vodReturner()) {
                    showBufferingView();
                    player.seek(seekBar.getProgress());
                    updatePlayProgressView(seekBar.getProgress(), player.getBufferTime());
                }
            }
        });

        playSpeedTextView.setOnClickListener(view -> {
            if (!totalTextView.getText().equals(getString(R.string.initialStringForPlaySpeed)))
                dialogUtil.alertDialogForPlaybackSpeed(PlayActivity.this, player, playSpeedTextView);
        });

        backArrowImageView.setOnClickListener(view -> setPortraitView());

        fullscreenButton.setOnClickListener(view -> {
            if (isPortrait && hasVideoEverStarted) {
                String tmpTitle = getString(R.string.no_title_found);
                if (currentPlayItem != null && currentPlayItem.getTitle() != null)
                    tmpTitle = currentPlayItem.getTitle();
                setFullScreenView(tmpTitle);
                player.setSurfaceChange();
            } else {
                setPortraitView();
            }
        });

        surfaceView.setOnClickListener(view -> {
            if (!isPortrait) {
                if (videoTitleEtcConstraintLayout.getVisibility() == View.VISIBLE) {
                    makeLayoutsGone();
                } else {
                    makeLayoutsVisible();
                }
            }
        });
    }

    public void initPlayer() {
        if (SandboxApplication.getWisePlayerFactory() != null) {
            player = SandboxApplication.getWisePlayerFactory().createWisePlayer();

            //*setting some unchanging parameters beforehand
            player.setReadyListener(myReadyListener);
            player.setPlayEndListener(myPlayEndListener);
            player.setLoadingListener(myLoadingListener);
            player.setErrorListener(myErrorListener);
            player.setEventListener(myEventListener);
            player.setSeekEndListener(mySeekEndListener);
            player.setResolutionUpdatedListener(myResolutionUpdatedListener);

            player.setVideoType(PlayerConstants.PlayMode.PLAY_MODE_NORMAL); //or 0
            player.setBookmark(10000);
            player.setCycleMode(PlayerConstants.CycleMode.MODE_CYCLE); //or 1
            player.setPlayMode(0); //Playback mode. 0: audio+video, 1: audio-only (may not work with video urls)
            //*ready and start will be made upon onClick.
        }
    }

    public void setFullScreenView(String name) {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        videoListRecyclerView.setVisibility(View.GONE);
        backArrowImageView.setVisibility(View.VISIBLE);

        videoTitleTextView.setText(name);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        isPortrait = false;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View view = this.getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(outerConstraintLayout);
        //video title layout is on top and below there is video view.
        constraintSet.connect(R.id.frameLayout, ConstraintSet.TOP, R.id.videoTitleEtcConstraintLayout, ConstraintSet.BOTTOM, 0);
        constraintSet.connect(R.id.frameLayout, ConstraintSet.BOTTOM, R.id.playDetailsConstraintLayout, ConstraintSet.TOP, 0);
        constraintSet.connect(R.id.outerConstraintLayout, ConstraintSet.BOTTOM, R.id.playDetailsConstraintLayout, ConstraintSet.BOTTOM, 100);
        constraintSet.applyTo(outerConstraintLayout);
    }

    public void setPortraitView() {
        if (videoTitleEtcConstraintLayout.getVisibility() == View.GONE) {
            makeLayoutsVisible();
        }
        backArrowImageView.setVisibility(View.GONE);

        if (getSupportActionBar() != null)
            getSupportActionBar().show();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        isPortrait = true;
        videoListRecyclerView.setVisibility(View.VISIBLE);
        // Remove the full screen
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
        if (player != null)
            player.setSurfaceChange();
    }

    public void makeLayoutsVisible() {
        beforeClickingSurfaceViewSet.applyTo(outerConstraintLayout);
    }

    public void makeLayoutsGone() {
        beforeClickingSurfaceViewSet.clone(outerConstraintLayout);
        videoTitleEtcConstraintLayout.setVisibility(View.GONE);
        playDetailsConstraintLayout.setVisibility(View.GONE);
        setMargins(frameLayout, 0, 0, 0, 0);
    }

    public void updateBufferingView(int percent) {
        if (videoBufferLayout.getVisibility() == View.GONE) {
            videoBufferLayout.setVisibility(View.VISIBLE);
        }
        String percentageText = percent + "%";
        percentTextView.setText(percentageText);
    }

    public void showBufferingView() {
        videoBufferLayout.setVisibility(View.VISIBLE);
        percentTextView.setText("0%");
    }

    public void updatePlayProgressView(int progress, int bufferPosition) {
        seekBar.setProgress(progress);
        seekBar.setSecondaryProgress(bufferPosition);
        seekBar.incrementSecondaryProgressBy(bufferPosition - progress);
        progressTextView.setText(TimeUtil.formatLongToTimeStr(progress));
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.setView(surfaceView);
            // To resume WisePlayer when you bring your app to the foreground, call the resume API. You can determine whether the playback automatically starts after your app is brought to the foreground by passing a parameter.
            player.resume(PlayerConstants.ResumeType.KEEP);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (player != null) {
            player.setSurfaceChange();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.suspend();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        while (player == null) {
            initPlayer();
            if (player != null)
                break;
        }

        if (hasVideoEverStarted) {
            switch (item.getItemId()) {
                case R.id.mute_unmute:
                    dialogUtil.defaultAlertDialog(this, null, "Mute or Unmute the Video",
                            new String[]{"Mute", "Unmute"}, "mute_unmute", player);
                    return true;
                case R.id.download_control:
                    dialogUtil.defaultAlertDialog(this, null, "Download Control",
                            new String[]{"Resume download", "Stop download"}, "download_control", player);
                    return true;
                case R.id.playback_mode:
                    dialogUtil.defaultAlertDialog(this, null, "Playback Mode",
                            new String[]{"Video + Audio", "Audio Only"}, "playback_mode", player);
                    return true;
                case R.id.repeat_mode:
                    dialogUtil.defaultAlertDialog(this, null, "Repeat Mode",
                            new String[]{"Enable repeat mode", "Disable Repeat Mode"}, "repeat_mode", player);
                    return true;
                case R.id.volume:
                    DialogUtil.showSetVolumeDialog(this, player);
                    return true;
                case R.id.bandwidth_adaptation:
                    dialogUtil.defaultAlertDialog(this, null, "Bandwidth Adaptation",
                            new String[]{"Enable bandwidth adaptation", "Disable bandwidth adaptation"}, "bandwidth_adaptation", player);
                    return true;
                case R.id.play_video_by_url:
                    showPlayURLDialog(this, player);
                    return true;
                case R.id.video_type:
                    dialogUtil.defaultAlertDialog(this, null, "Video Type",
                            new String[]{"VOD (Video on Demand)", "Live Streaming"}, "video_type", player);
                    return true;
                case R.id.add_preload_task:
                    if (checkReadPermissionBoolean()) {
                        dialogUtil.addPreloadTaskDialog(this);
                    } else {
                        requestPermission();
                    }
                    return true;
                case R.id.pause_preload_task:
                    if (DialogUtil.getPreloader() != null) {
                        DialogUtil.getPreloader().pauseAllTasks();
                        showToast("All tasks are paused");
                    } else
                        showToast(this.getString(R.string.video_add_single_cache_fail));
                    return true;
                case R.id.resume_preload_task:
                    if (DialogUtil.getPreloader() != null) {
                        DialogUtil.getPreloader().resumeAllTasks();
                        showToast("All tasks are resumed");
                    } else
                        showToast(this.getString(R.string.video_add_single_cache_fail));
                    return true;
                case R.id.delete_all_preload_data:
                    if (DialogUtil.getPreloader() != null) {
                        DialogUtil.getPreloader().removeAllCache();
                        showToast("All cached data are deleted");
                    } else
                        showToast(this.getString(R.string.video_add_single_cache_fail));
                    return true;
                case R.id.delete_all_preload_tasks:
                    if (DialogUtil.getPreloader() != null) {
                        DialogUtil.getPreloader().removeAllTasks();
                        showToast("All preload tasks are deleted");
                    } else
                        showToast(this.getString(R.string.video_add_single_cache_fail));
                    return true;
                case R.id.set_bitrate_range:
                    DialogUtil.showBitrateRangeDialog(this, player);
                    return true;
                case R.id.playback_start_bitrate:
                    dialogUtil.defaultAlertDialog(this, null, "Playback bitrate selection",
                            new String[]{"Use adaptive playback start bitrate", "Use specified playback start bitrate"}, "bitrate_type", player);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            if (item.getItemId() != android.R.id.home)
                showToast("Start a video first");
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        if (!checkReadPermissionBoolean())
            requestPermission();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (isPortrait && player != null) {
            player.stop();
            player.release();
            player = null;
            hasVideoEverStarted = false;
            isReallyPlaying = false;
            super.onBackPressed();
        } else {
            setPortraitView();
        }
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            hasVideoEverStarted = false;
            isReallyPlaying = false;
            removeMyCallbacks();
        }

        super.onDestroy();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void playButtonChangerAndStartPauseVideo() {
        if (!isReallyPlaying) {
            startPlaying();
            playImageView.setImageDrawable(getDrawable(R.drawable.ic_full_screen_suspend_normal));
        } else {
            pausePlaying();
            playImageView.setImageDrawable(getDrawable(R.drawable.ic_play));
        }
    }

    @Override
    public void onItemClick(List<VideoItemClass> myVideoList, int position) {
        onClickedPos = position;

        currentPlayItem = myVideoList.get(position);

        while (player == null) {
            initPlayer();
            if (player != null)
                break;
        }

        hasVideoEverStarted = true;

        if (player.isPlaying()) {
            player.stop();
            isReallyPlaying = false;
        }

        player.reset();

        player.setPlaySpeed(1.0f);
        playSpeedTextView.setText(R.string.onePointZeroText);

        String url = currentPlayItem.getSources();
        player.setPlayUrl(url);

        player.setView(surfaceView);

        if (!dialogUtil.vodReturner()) {
            player.setVideoType(1);
        }

        showBufferingView();
        player.ready();
    }

    public boolean checkReadPermissionBoolean() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Checking whether user granted the permission or not.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //granted
            showToast("Permission granted! You can use preloader now.");
        } else {
            showToast("Please go back and come again to allow permission to use preloader.");
        }
    }

    private void showToast(String toastText) {
        Toast.makeText(PlayActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }

    //It is put here and not in DialogUtil due to variables that are needed.
    public void showPlayURLDialog(Context context, WisePlayer player) {
        View view = LayoutInflater.from(context).inflate(R.layout.play_url_pasting_dialog, null);
        final android.app.AlertDialog dialog =
                new android.app.AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.paste_url_to_play))
                        .setView(view)
                        .create();
        dialog.show();
        final EditText playURLEditText = view.findViewById(R.id.playURLEditText);
        Button playURLButton = view.findViewById(R.id.playURLButton);
        playURLButton.setOnClickListener(v -> {
            if (player != null && playURLEditText.getText() != null) {
                String inputText = playURLEditText.getText().toString();

                currentPlayItem = new VideoItemClass();
                currentPlayItem.setSources(inputText);
                currentPlayItem.setTitle(getString(R.string.internetURLVideo));

                if (!inputText.equals("") && URLUtil.isValidUrl(inputText)) {

                    player.stop();
                    player.reset();

                    player.setPlaySpeed(1.0f);
                    playSpeedTextView.setText(R.string.onePointZeroText);

                    player.setPlayUrl(inputText);

                    player.setView(surfaceView);

                    showBufferingView();

                    if (!dialogUtil.vodReturner()) {
                        player.setVideoType(1);
                    }

                    hasVideoEverStarted = true;
                    player.ready();

                    dialog.dismiss();

                } else {
                    showToast("URL is empty or URL is not valid.");
                }

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}
