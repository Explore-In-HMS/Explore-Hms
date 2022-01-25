/**
 * Copyright 2020. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.genar.hmssandbox.huawei.feature_videokit.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.feature_videokit.R;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnDialogInputValueListener;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnPlayWindowListener;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnWisePlayerListener;
import com.genar.hmssandbox.huawei.feature_videokit.control.PlayControl;
import com.genar.hmssandbox.huawei.feature_videokit.entity.BitrateInfo;
import com.genar.hmssandbox.huawei.feature_videokit.entity.PlayEntity;
import com.genar.hmssandbox.huawei.feature_videokit.utils.Constants;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DataFormatUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DeviceUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DialogUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.LogUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.PlayControlUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.SelectDialog;
import com.genar.hmssandbox.huawei.feature_videokit.utils.StringUtil;
import com.genar.hmssandbox.huawei.feature_videokit.view.PlayView;
import com.huawei.hms.videokit.player.AudioTrackInfo;
import com.huawei.hms.videokit.player.SubtitleTrackInfo;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.hms.videokit.player.common.PlayerConstants.BandwidthSwitchMode;
import com.huawei.hms.videokit.player.common.PlayerConstants.PlayMode;
import com.huawei.hms.videokit.player.common.PlayerConstants.ResumeType;
import com.huawei.hms.videokit.player.internal.SubtitleInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Play Activity
 */
public class PlayActivity extends AppCompatActivity implements OnPlayWindowListener, OnWisePlayerListener {
    private static final String TAG = "PlayActivity";

    // Seek spacing
    private static final int SEEK_TIME =  3 * 1000;

    // Play view
    private PlayView playView;

    // Play control
    private PlayControl playControl;

    // Whether the user is touch seekbar
    private boolean isUserTrackingTouch = false;

    // Whether at the front desk
    private boolean isResume = false;

    // Whether the suspend state
    private boolean isSuspend = false;

    // Vertical screen properties
    private int systemUiVisibility = 0;

    // Whether to suspend the buffer
    private int streamRequestMode = 0;

    // Smooth/manual switch bitrate
    private boolean isAutoSwitchBitrate;

    // Play complete
    private boolean isPlayComplete = false;

    // Play the View is created
    private boolean hasSurfaceCreated = false;

    // WisePlayer instance
    private WisePlayer wisePlayer = null;

    // List of subtitle track info
    private SubtitleTrackInfo[] infoList = null;

    private boolean isPortraitOrientation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        playControl = new PlayControl(this, this);
        // Some of the properties of preserving vertical screen
        systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();

        // If failed to initialize, exit the current interface directly
        if (playControl.initPlayFail()) {
            Toast.makeText(this, getResources().getString(R.string.init_play_fail), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            playView.setRecycleData(playControl.getPlayList());
            ready();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume isSuspend:" + isSuspend);
        isResume = true;
        if (hasSurfaceCreated) {
            playControl.setBookmark(Constants.DEFAULT_BOOKMARK_VALUE);
            playControl.playResume(ResumeType.KEEP);
            if (!updateViewHandler.hasMessages(Constants.PLAYING_WHAT)) {
                updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        isResume = false;
        playView.onPause();
        playControl.onPause();
        if (updateViewHandler != null) {
            updateViewHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * init the layout
     */
    private void initView() {
        playView = new PlayView(this, this, this);
        setContentView(playView.getContentView());
    }

    /**
     * Start playback activity
     *
     * @param context Context
     * @param playEntity Play the video data entity
     */
    public static void startPlayActivity(Context context, PlayEntity playEntity) {
        Intent intent = new Intent();
        intent.setClass(context, PlayActivity.class);
        intent.putExtra(Constants.VIDEO_PLAY_DATA, playEntity);
        context.startActivity(intent);
    }

    /**
     * Prepare playing
     */
    private void ready() {
        playControl.setCurrentPlayData(getIntentExtra());
        playView.showBufferingView();
        playControl.ready();
    }

    /**
     * get the video data
     *
     * @return the video data
     */
    private Serializable getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getSerializableExtra(Constants.VIDEO_PLAY_DATA);
        } else {
            return null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.d(TAG, "surface created");
        hasSurfaceCreated = true;
        playControl.setSurfaceView(playView.getSurfaceView());
        if (isSuspend) {
            isSuspend = false;
            playControl.playResume(ResumeType.KEEP);
            if (updateViewHandler != null && !updateViewHandler.hasMessages(Constants.PLAYING_WHAT)) {
                updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurfaceCreated = false;
        LogUtil.d(TAG, "surfaceDestroyed");
        isSuspend = true;
        playControl.suspend();
    }

    @Override
    public void onLoadingUpdate(WisePlayer wisePlayer, final int percent) {
        LogUtil.d(TAG, "update buffering percent :" + percent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (percent < 100) {
                    playView.updateBufferingView(percent);
                } else {
                    playView.dismissBufferingView();
                }
            }
        });
    }

    @Override
    public void onStartPlaying(WisePlayer wisePlayer) {
        LogUtil.d(TAG, "onStartPlaying");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isPlayComplete = false;
                playView.dismissBufferingView();
            }
        });
    }

    @Override
    public boolean onError(WisePlayer wisePlayer, int what, int extra) {
        LogUtil.d(TAG, "onError what:" + what + " extra:" + extra);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playView.dismissBufferingView();
                Toast.makeText(PlayActivity.this, "error:" + what + " extra:" + extra, Toast.LENGTH_SHORT).show();
            }
        });
        if (updateViewHandler != null) {
            updateViewHandler.sendEmptyMessageDelayed(Constants.PLAY_ERROR_FINISH, Constants.DELAY_MILLIS_3000);
        }
        return false;
    }

    @Override
    public void onSubtitleUpdate(WisePlayer wisePlayer, Parcelable[] subtitles) {
        LogUtil.d(TAG, "onSubtitleUpdate length:" + subtitles.length);
        if (subtitles.length > 0) {
            SubtitleInfo[] subtmp = new SubtitleInfo[subtitles.length];
            for (int i = 0; i < subtitles.length; i++) {
                if (subtitles[i] instanceof SubtitleInfo) {
                    subtmp[i] = (SubtitleInfo) subtitles[i];
                    if (subtmp[i] != null) {
                        LogUtil.d(TAG, "onSubtitleUpdate subtitle:" + subtmp[i].getSubtitle() + ",startTS:" +
                                subtmp[i].getStartTS() + ",endTS:" + subtmp[i].getEndTS());
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isPlayComplete) {
            playControl.savePlayProgress();
            isPlayComplete = false;
        }
        playControl.stop();
        playControl.release();
        // Mute only on the current video effect
        PlayControlUtil.setIsMute(false);
        if (updateViewHandler != null) {
            updateViewHandler.removeCallbacksAndMessages(null);
            updateViewHandler = null;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserTrackingTouch = true;
        LogUtil.d(TAG, "onStartTrackingTouch isUserTrackingTouch:" + isUserTrackingTouch);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.d(TAG, "onStopTrackingTouch Progress:" + seekBar.getProgress());
        isPlayComplete = false;
        playControl.updateCurProgress(seekBar.getProgress());
        playView.showBufferingView();
        playView.updatePlayProgressView(seekBar.getProgress(), playControl.getBufferTime(),
                playControl.getBufferingSpeed(), playControl.getCurrentBitrate());
        isUserTrackingTouch = false;
        if (updateViewHandler != null) {
            updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (playView.isBackButtonFocus()) {
            playView.setFocusViewPosition(PlayView.FOCUS_BACK_VIEW);
        } else if (playView.isSettingFocus()) {
            playView.setFocusViewPosition(PlayView.FOCUS_SETTING_VIEW);
        } else if (playView.isFullScreenFocus()) {
            playView.setFocusViewPosition(PlayView.FOCUS_FULLSCREEN_VIEW);
        } else if (playView.isSpeedFocus()) {
            playView.setFocusViewPosition(PlayView.FOCUS_SPEED_VIEW);
        } else if (playView.isRefreshFocus()) {
            playView.setFocusViewPosition(PlayView.FOCUS_REFRESH_VIEW);
        } else if (playView.isPlayFocus()) {
            playView.setFocusViewPosition(PlayView.FOCUS_PLAY_VIEW);
        } else if (playView.isSeekBarFocus()) {
            playView.clearBackgroundColor();
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    int tempPos = playControl.getCurrentTime() + SEEK_TIME;
                    playControl.updateCurProgress(tempPos);
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    int tempPoss = playControl.getCurrentTime() - SEEK_TIME;
                    if (tempPoss < 0) {
                        tempPoss = 0;
                    }
                    playControl.updateCurProgress(tempPoss);
                    return true;
            }
        } else {
            playView.clearBackgroundColor();
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Update the player view
     */
    private Handler updateViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null) {
                LogUtil.i(TAG, "current mss is empty.");
                return;
            }
            switch (msg.what) {
                case Constants.PLAYING_WHAT:
                    if (!isUserTrackingTouch) {
                        playView.updatePlayProgressView(playControl.getCurrentTime(), playControl.getBufferTime(),
                                playControl.getBufferingSpeed(), playControl.getCurrentBitrate());
                        sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
                    }
                    break;
                case Constants.UPDATE_PLAY_STATE:
                    playView.updatePlayCompleteView();
                    removeCallbacksAndMessages(null);
                    break;
                case Constants.PLAY_ERROR_FINISH:
                    finish();
                    break;
                case Constants.UPDATE_SWITCH_BITRATE_SUCCESS:
                    playView.hiddenSwitchingBitrateTextView();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Set the play mode
     *
     * @param playMode The play mode
     */
    private void setPlayMode(int playMode) {
        playControl.setPlayMode(playMode, true);
    }

    /**
     * Set cycle mode
     *
     * @param isCycleMode Is cycle mode
     */
    public void setCycleMode(boolean isCycleMode) {
        playControl.setCycleMode(isCycleMode);
    }

    /**
     * Select the play speed
     */
    private void setPlaySpeed() {
        try {
            String[] showTextArray = getResources().getStringArray(R.array.play_speed_text);
            String speedValue = DataFormatUtil.getPlaySpeedString(playControl.getPlaySpeed());
            LogUtil.d("current play speed : float is " + playControl.getPlaySpeed() + ", and String is" + speedValue);
            playView.showSettingDialogValue(Constants.PLAYER_SWITCH_PLAY_SPEED, Arrays.asList(showTextArray),
                    speedValue);
        } catch (NotFoundException e) {
            LogUtil.w(TAG, "get string array error :" + e.getMessage());
        }
    }

    /**
     * Set the play speed
     *
     * @param speedValue the play speed
     */
    private void onSwitchPlaySpeed(String speedValue) {
        playControl.setPlaySpeed(speedValue);
        LogUtil.d(TAG,
                "current set speed value:" + speedValue + ", and get player speed value:" + playControl.getPlaySpeed());
        playView.setSpeedButtonText(speedValue);
    }

    /**
     * Close logo
     */
    private void closeLogo() {
        playControl.closeLogo();
    }

    /**
     * Whether to stop the downloading
     *
     * @param selectValue Select text value
     */
    private void onSwitchRequestMode(String selectValue) {
        if (selectValue.equals(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_keep_download))) {
            streamRequestMode = 0;
        } else if (selectValue.equals(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_stop_download))) {
            streamRequestMode = 1;
        }
        LogUtil.i(TAG, "mStreamRequestMode:" + streamRequestMode);

        playControl.setBufferingStatus(streamRequestMode == 0 ? true : false, true);
    }

    /**
     * Set the bitrate
     *
     * @param itemSelect select text value
     */
    private void onSwitchBitrate(String itemSelect) {
        if (itemSelect.equals(StringUtil.getStringFromResId(PlayActivity.this, R.string.automatic_adaptation))) {
            if (isAutoSwitchBitrate) {
                playControl.switchBitrateSmooth(0);
            } else {
                playControl.switchBitrateDesignated(0);
            }
            playView.showSwitchingBitrateTextView(itemSelect);
        } else if (playControl.getSwitchBitrateList() != null) {
            int index = playControl.getSwitchBitrateList().indexOf(itemSelect);
            if (index != -1 && index <= playControl.getBitrateRangeList().size()) {
                BitrateInfo bitrateInfo = playControl.getBitrateRangeList().get(index);
                if (isAutoSwitchBitrate) {
                    playControl.setBitrateRange(bitrateInfo.getMinBitrate(), bitrateInfo.getMaxBitrate());
                    playControl.switchBitrateSmooth(bitrateInfo.getCurrentBitrate());
                } else {
                    playControl.switchBitrateDesignated(bitrateInfo.getCurrentBitrate());
                }
            } else {
                LogUtil.i(TAG, "get bitrate error");
                return;
            }
            playView.showSwitchingBitrateTextView(itemSelect);
        }
    }

    /**
     * Modify the state of play
     */
    private void changePlayState() {
        if (playControl.isPlaying()) {
            updateViewHandler.removeCallbacksAndMessages(null);
            playView.setPlayView();
        } else {
            isPlayComplete = false;
            updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            playView.setPauseView();
        }
        playControl.setPlayData(playControl.isPlaying());
    }

    /**
     * Select the way to switch bitrate
     */
    private void switchBitrateAuto() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.video_bitrate_auto));
        showTextList.add(getResources().getString(R.string.video_bitrate_designated));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_AUTO_DESIGNATED, showTextList, Constants.DIALOG_INDEX_ONE);
    }

    /**
     * Set the bandwidth switch
     */
    private void switchBandwidthMode() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.close_adaptive_bandwidth));
        showTextList.add(getResources().getString(R.string.open_adaptive_bandwidth));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_BANDWIDTH_MODE, showTextList, Constants.DIALOG_INDEX_ONE);
    }

    /**
     * Switch Subtitle
     */
    private void switchSubtitle() {
        if (this.wisePlayer != null) {
            infoList = this.wisePlayer.getSubtitleTracks();
            if (infoList == null) {
                LogUtil.d(TAG, "switchSubtitle infoList == null");
                return;
            }
            if (infoList.length > 0) {
                List<String> showTextList = new ArrayList<>();
                LogUtil.d(TAG, "switchSubtitle infoList.length:" + infoList.length);
                for (int i = 0; i < infoList.length; i++) {
                    String desc = infoList[i].getDesc();
                    int id = infoList[i].getId();
                    LogUtil.d(TAG, "switchSubtitle desc:" + desc + ", id:" + id);
                    showTextList.add(desc);
                }
                int selectedIndex = -1;
                SubtitleTrackInfo info = wisePlayer.getSelectedSubtitleTrack();
                if (info != null) {
                    selectedIndex = info.getId();
                    LogUtil.d(TAG, "switchSubtitle selectedIndex:" + selectedIndex);
                }
                if (selectedIndex == -1 || selectedIndex >= infoList.length) {
                    selectedIndex = infoList.length;
                }
                showTextList.add("close");
                playView.showSettingDialog(Constants.PLAYER_SWITCH_SUBTITLE, showTextList, selectedIndex);
            }
        }
    }

    /**
     * Set the play mode
     */
    private void switchPlayMode() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.play_video));
        showTextList.add(getResources().getString(R.string.play_audio));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_PLAY_MODE, showTextList, playControl.getPlayMode());
    }

    /**
     * If set up a video loop
     */
    private void switchPlayLoop() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.video_loop_play));
        showTextList.add(getResources().getString(R.string.video_not_loop_play));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_LOOP_PLAY_MODE, showTextList,
                playControl.isCycleMode() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
    }

    /**
     * Select whether the mute
     */
    private void switchVideoMute() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.video_mute));
        showTextList.add(getResources().getString(R.string.video_not_mute));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_VIDEO_MUTE_MODE, showTextList,
                PlayControlUtil.isMute() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
    }

    /**
     * Show the volume dialog
     */
    private void setVideoVolume() {
        DialogUtil.showSetVolumeDialog(this, new OnDialogInputValueListener() {
            @Override
            public void dialogInputListener(String inputText) {
                playControl.setVolume(StringUtil.valueOf(inputText));
            }
        });
    }

    /**
     * Switching bitrate
     *
     * @param isAuto Whether it is smooth
     */
    private void switchBitrateMenu(boolean isAuto) {
        List<String> bitrateList = playControl.getBitrateStringList();
        if (bitrateList == null || bitrateList.size() == 0) {
            Toast.makeText(this, getString(R.string.switch_bitrate_enable), Toast.LENGTH_SHORT).show();
            return;
        }
        isAutoSwitchBitrate = isAuto;
        int selectedValueIndex;
        int currentBitrate = playControl.getCurrentBitrate();
        LogUtil.i(TAG, "currentBitrate:" + currentBitrate);
        if (currentBitrate == 0) {
            selectedValueIndex = 0;
        } else {
            selectedValueIndex = playControl.getCurrentBitrateIndex();
        }
        playView.showSettingDialog(Constants.PLAYER_SWITCH_BITRATE, bitrateList, selectedValueIndex);
    }

    /**
     * Whether to stop the download dialog
     */
    private void stopRequestStream() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_keep_download));
        showTextList.add(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_stop_download));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_STOP_REQUEST_STREAM, showTextList, streamRequestMode);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                changePlayState();
                break;
            case R.id.back_tv:
                backPress();
                break;
            case R.id.fullscreen_btn:
                setFullScreen();
                break;
            case R.id.play_speed_btn:
                setPlaySpeed();
                break;
            case R.id.setting_tv:
                onSettingDialog();
                break;
            case R.id.switch_bitrate_tv:
                switchBitrateAuto();
                break;
            case R.id.play_refresh:
                playControl.refresh();
                break;
            default:
                break;
        }
    }

    /**
     * Play the Settings dialog
     */
    private void onSettingDialog() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_bandwidth_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_bitrate_title));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_stop_downloading));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_play_speed));
        if (!playControl.isHttpVideo()) {
            showTextList.add(StringUtil.getStringFromResId(this, R.string.close_logo));
        }
        showTextList.add(StringUtil.getStringFromResId(this, R.string.play_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_loop_play));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_mute_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_volume));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_switch_subtitle));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.get_audio_track_info));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.switch_audio_track));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.set_wake_mode));
        playView.showSettingDialog(Constants.MSG_SETTING, showTextList, 0);
    }

    /**
     * Set up the full screen
     */
    private void setFullScreen() {
        if (!DeviceUtil.isFullScreen(this)) {
            playView.setFullScreenView(playControl.getCurrentPlayName());
            if (DeviceUtil.isPortrait(this)) {
                isPortraitOrientation = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            // Set up the full screen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            playControl.setSurfaceChange();
            playView.showSwitchBitrateTextView();
        }
    }

    @Override
    public void onBackPressed() {
        // Click the back button to return to the landscape conditions vertical screen
        if (!DeviceUtil.isPortrait(getApplicationContext())) {
            backPress();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Click the back button in the upper left corner
     */
    private void backPress() {
        if (DeviceUtil.isFullScreen(this)) {
            if (isPortraitOrientation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            playView.setPortraitView();
            // Remove the full screen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            playControl.setSurfaceChange();
        } else {
            finish();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtil.d(TAG, "onSurfaceTextureAvailable");
        hasSurfaceCreated = true;
        playControl.setTextureView(playView.getTextureView());
        if (isSuspend) {
            isSuspend = false;
            playControl.playResume(ResumeType.KEEP);
            if (!updateViewHandler.hasMessages(Constants.PLAYING_WHAT)) {
                updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtil.d(TAG, "onSurfaceTextureDestroyed");
        hasSurfaceCreated = false;
        isSuspend = true;
        playControl.suspend();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onItemClick(int pos) {
        LogUtil.d(TAG, "select new video url");
        if (!isPlayComplete) {
            playControl.savePlayProgress();
            isPlayComplete = false;
        }
        if (updateViewHandler != null) {
            updateViewHandler.removeCallbacksAndMessages(null);
        }

        PlayEntity playEntity = playControl.getPlayFromPosition(pos);
        if (playEntity != null) {
            playControl.reset();
            playView.reset();
            LogUtil.d(TAG, "reset success");
            if (playControl.initPlayFail()) {
                Toast.makeText(this, getResources().getString(R.string.init_play_fail), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                playControl.setCurrentPlayData(playEntity);
                restartPlayer();
            }
        }
    }

    @Override
    public void onSettingItemClick(String itemSelect, int settingType) {
        switch (settingType) {
            case Constants.MSG_SETTING:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_bitrate_title))) {
                    switchBitrateAuto();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_stop_downloading))) {
                    stopRequestStream();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_play_speed))) {
                    setPlaySpeed();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_bandwidth_mode))) {
                    switchBandwidthMode();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_switch_subtitle))) {
                    switchSubtitle();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.close_logo))) {
                    closeLogo();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.play_mode))) {
                    switchPlayMode();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_loop_play))) {
                    switchPlayLoop();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_mute_setting))) {
                    switchVideoMute();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_volume))) {
                    setVideoVolume();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.switch_audio_track))) {
                    getSwitchAudioTrack();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.get_audio_track_info))) {
                    getAudioTracks();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.set_wake_mode))) {
                    setWakeMode();
                } else {
                    LogUtil.i(TAG, "current settings type is " + itemSelect);
                }
                break;
            case Constants.PLAYER_SWITCH_AUTO_DESIGNATED:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_bitrate_auto))) {
                    switchBitrateMenu(true);
                } else {
                    switchBitrateMenu(false);
                }
                break;
            case Constants.PLAYER_SWITCH_STOP_REQUEST_STREAM:
                onSwitchRequestMode(itemSelect);
                break;
            case Constants.PLAYER_SWITCH_PLAY_SPEED:
                onSwitchPlaySpeed(itemSelect);
                break;
            case Constants.PLAYER_SWITCH_BANDWIDTH_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.open_adaptive_bandwidth))) {
                    playControl.setBandwidthSwitchMode(BandwidthSwitchMode.AUTO_SWITCH_MODE, true);
                } else {
                    playControl.setBandwidthSwitchMode(BandwidthSwitchMode.MANUAL_SWITCH_MODE, true);
                }
                break;
            case Constants.PLAYER_SWITCH_SUBTITLE:
                if (infoList != null) {
                    if (itemSelect.equals("close")) {
                        playControl.closeSubtitle();
                    } else {
                        for (int i = 0; i < infoList.length; i++) {
                            String desc = infoList[i].getDesc();
                            int id = infoList[i].getId();
                            if (desc.equals(itemSelect)) {
                                playControl.switchSubtitle(id);
                            }
                        }
                    }
                }
                break;
            case Constants.PLAYER_SWITCH_PLAY_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.play_audio))) {
                    setPlayMode(PlayMode.PLAY_MODE_AUDIO_ONLY);
                } else {
                    setPlayMode(PlayMode.PLAY_MODE_NORMAL);
                }
                break;
            case Constants.PLAYER_SWITCH_LOOP_PLAY_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_loop_play))) {
                    setCycleMode(true);
                } else {
                    setCycleMode(false);
                }
                break;
            case Constants.PLAYER_SWITCH_VIDEO_MUTE_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_mute))) {
                    playControl.setMute(true);
                } else {
                    playControl.setMute(false);
                }
                break;
            case Constants.PLAYER_SWITCH_BITRATE:
                onSwitchBitrate(itemSelect);
                break;
            case Constants.PLAYER_GET_AUDIO_TRACKS:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.current_audio_track))) {
                    LogUtil.d(TAG, "getSelectedAudioTrack is :" + itemSelect);
                    playControl.getSelectedAudioTrack();
                } else {
                    LogUtil.d(TAG, "getAudioTracks is :" + itemSelect);
                    playControl.getAudioTracks();
                }
                break;
            case Constants.PLAYER_SWITCH_AUDIO_TRACK:
                playControl.switchAudioTrack(itemSelect);
                break;
            case Constants.PLAYER_SET_WAKE_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.set_wake_mode))) {
                    playControl.setWakeMode(true);
                } else {
                    playControl.setWakeMode(false);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Reset player play parameters
     */
    private void restartPlayer() {
        playControl.setMute(false);
        playControl.ready();
        if (hasSurfaceCreated) {
            if (PlayControlUtil.isSurfaceView()) {
                playControl.setSurfaceView(playView.getSurfaceView());
            } else {
                playControl.setTextureView(playView.getTextureView());
            }
            isSuspend = false;
            playControl.playResume(ResumeType.KEEP);
            if (!updateViewHandler.hasMessages(Constants.PLAYING_WHAT)) {
                updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            }
        }
    }

    @Override
    public boolean onEvent(WisePlayer wisePlayer, int what, int extra, Object o) {
        LogUtil.d(TAG, "onEvent = " + what + " extra = " + extra);
        if (what == PlayerConstants.EventCode.BITRATE_SWITCH_COMPLETE) {
            playView.updateSwitchingBitrateTextView(getString(R.string.resolution_switched_auto));
            playView.setSwitchBitrateTv(playControl.getCurrentVideoHeight());
            updateViewHandler.sendEmptyMessageDelayed(Constants.UPDATE_SWITCH_BITRATE_SUCCESS,
                    Constants.DELAY_MILLIS_1000);
        }
        return true;
    }

    @Override
    public void onPlayEnd(WisePlayer wisePlayer) {
        LogUtil.d(TAG, "onPlayEnd " + wisePlayer.getCurrentTime());
        playControl.clearPlayProgress();
        isPlayComplete = true;
        if (updateViewHandler != null){
            updateViewHandler.sendEmptyMessageDelayed(Constants.UPDATE_PLAY_STATE, Constants.DELAY_MILLIS_1000);
        }
    }

    @Override
    public void onReady(final WisePlayer wisePlayer) {
        this.wisePlayer = wisePlayer;
        LogUtil.d(TAG, "onReady");
        playControl.start();
        // Make sure the main thread to update
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playView.updatePlayView(wisePlayer);
                if (isResume) {
                    playView.setPauseView();
                }
                playView.setContentView(wisePlayer, playControl.getCurrentPlayName());
                updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
            }
        });
    }

    @Override
    public void onResolutionUpdated(WisePlayer wisePlayer, int w, int h) {
        LogUtil.d(TAG, "current video width:" + w + " height:" + h);
        playView.setContentView(wisePlayer, playControl.getCurrentPlayName());
    }

    @Override
    public void onSeekEnd(WisePlayer wisePlayer) {

    }

    public void getAudioTracks() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.current_audio_track));
        showTextList.add(getResources().getString(R.string.audio_tracks));
        playView.showGettingDialog(Constants.PLAYER_GET_AUDIO_TRACKS, showTextList, Constants.DIALOG_INDEX_ONE);
    }

    private void setWakeMode() {
        List<String> list = new ArrayList<>();
        list.clear();
        list.add(getResources().getString(R.string.set_wake_mode));
        list.add(getResources().getString(R.string.close_wake_mode));
        playView.showSettingDialog(Constants.PLAYER_SET_WAKE_MODE, list,
                PlayControlUtil.isWakeOn() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
    }

    public void getSwitchAudioTrack() {
        Object obj = null;
        AudioTrackInfo[] audioTrack = null;
        if ((obj = playControl.getAudioTracks()) != null) {
            audioTrack = (AudioTrackInfo[]) obj;
        }

        if (audioTrack == null) {
            LogUtil.w(TAG, "switchAudio no audio track");

            SelectDialog dialog = new SelectDialog(this);
            dialog.setTitle("switchAudio  no audiotrack");
            dialog.setHandler(updateViewHandler, Constants.PLAYER_SWITCH_AUDIO_TRACK);
            dialog.setNegativeButton("Cancle",null);
            dialog.show();
            return;
        }
        List<String> showTextList = new ArrayList<>();
        for (int i = 0; i < audioTrack.length; ++i) {
            showTextList.add(audioTrack[i].getDesc());
        }
        LogUtil.d(TAG, "getSwitchAudioTrack current audiotrack:" + playControl.getAudioLangIndex());
        playView.showSettingDialog(Constants.PLAYER_SWITCH_AUDIO_TRACK, showTextList, playControl.getAudioLangIndex());
    }
}
