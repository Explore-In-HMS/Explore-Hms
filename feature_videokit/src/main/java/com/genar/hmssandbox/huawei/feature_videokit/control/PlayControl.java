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

package com.genar.hmssandbox.huawei.feature_videokit.control;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.TextureView;


import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnWisePlayerListener;
import com.genar.hmssandbox.huawei.feature_videokit.entity.BitrateInfo;
import com.genar.hmssandbox.huawei.feature_videokit.entity.PlayEntity;
import com.genar.hmssandbox.huawei.feature_videokit.utils.Constants;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DataFormatUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.LogUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.PlayControlUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.StringUtil;
import com.huawei.hms.videokit.player.AudioTrackInfo;
import com.huawei.hms.videokit.player.InitBitrateParam;
import com.huawei.hms.videokit.player.StreamInfo;
import com.huawei.hms.videokit.player.VideoInfo;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.hms.videokit.player.common.PlayerConstants.CycleMode;
import com.huawei.hms.videokit.player.common.PlayerConstants.ScenarioType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Play controls class
 */
public class PlayControl {
    private static final String TAG = "PlayControl";

    // Context
    private Context context;

    // Player entity
    private WisePlayer wisePlayer;

    // The current play data
    private PlayEntity currentPlayData;

    // Player listener
    private OnWisePlayerListener onWisePlayerListener;

    // Play list data
    private List<PlayEntity> playEntityList;

    // Video play url start with Http/Https
    private boolean isHttpVideo = true;

    // Video bitrate range list
    private List<BitrateInfo> bitrateRangeList;

    // Video bitrate list
    private List<String> switchBitrateList;

    private boolean isSuspend = false;

    /**
     * Constructor
     *
     * @param context Context
     * @param onWisePlayerListener Player listener
     */
    public PlayControl(Context context, OnWisePlayerListener onWisePlayerListener) {
        this.context = context;
        this.onWisePlayerListener = onWisePlayerListener;
        init();
    }

    /**
     * Init
     */
    public void init() {
        initPlayer();
        setPlayListener();
    }

    /**
     * Set the play listener
     */
    private void setPlayListener() {
        if (wisePlayer != null) {
            wisePlayer.setErrorListener(onWisePlayerListener);
            wisePlayer.setEventListener(onWisePlayerListener);
            wisePlayer.setResolutionUpdatedListener(onWisePlayerListener);
            wisePlayer.setReadyListener(onWisePlayerListener);
            wisePlayer.setLoadingListener(onWisePlayerListener);
            wisePlayer.setPlayEndListener(onWisePlayerListener);
            wisePlayer.setSeekEndListener(onWisePlayerListener);
            if (PlayControlUtil.isSubtitleRenderByDemo()) {
                wisePlayer.setSubtitleUpdateListener(onWisePlayerListener);
            } else {
                wisePlayer.setSubtitleUpdateListener(null);
            }
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set play Listener fail.");
        }
    }

    /**
     * Init play fail
     *
     * @return Whether the failure
     */
    public boolean initPlayFail() {
        return wisePlayer == null;
    }

    /**
     * Init the player
     */
    private void initPlayer() {
        if (SandboxApplication.getWisePlayerFactory() == null) {
            return;
        }
        wisePlayer = SandboxApplication.getWisePlayerFactory().createWisePlayer();
    }

    /**
     * Get the current play data
     *
     * @param serializable The play data
     */
    public void setCurrentPlayData(Serializable serializable) {
        if (serializable != null && serializable instanceof PlayEntity) {
            currentPlayData = (PlayEntity) serializable;
        }
    }

    /**
     * Start the player, the state of ready to start
     */
    public void ready() {
        if (currentPlayData != null && wisePlayer != null) {
            LogUtil.d(TAG, "current play video url is :" + currentPlayData.getUrl());
            if (currentPlayData.getUrlType() == Constants.UrlType.URL) {
                setHttpVideo(true);
                wisePlayer.setPlayUrl(new String[] {currentPlayData.getUrl()});
            } else if (currentPlayData.getUrlType() == Constants.UrlType.URL_JSON) {
                setHttpVideo(false);
                wisePlayer.setPlayUrl(currentPlayData.getUrl(), currentPlayData.getAppId(), ScenarioType.ONLINE);
            } else if (currentPlayData.getUrlType() == Constants.UrlType.URL_MULTIPLE) {
                setHttpVideo(true);
                String[] strings = StringUtil.getStringArray(currentPlayData.getUrl(), "-SPAD-");
                wisePlayer.setPlayUrl(strings);
            } else if (currentPlayData.getUrlType() == Constants.UrlType.URL_JSON_FORMAT) {
                setHttpVideo(false);
                wisePlayer.setPlayUrl(currentPlayData.getUrl(), currentPlayData.getAppId(), ScenarioType.ONLINE, currentPlayData.getVideoFormat());
            } else {
                setHttpVideo(true);
                wisePlayer.setPlayUrl(new String[] {currentPlayData.getUrl()});
            }
            setProxyInfo();
            setDownloadLink();
            setBookmark(Constants.DEFAULT_BOOKMARK_VALUE);
            setPlayMode(PlayControlUtil.getPlayMode(), false);
            setMute(PlayControlUtil.isMute());
            setVideoType(PlayControlUtil.getVideoType(), false);
            setBandwidthSwitchMode(PlayControlUtil.getBandwidthSwitchMode(), false);
            setInitBitrateEnable();
            setInitBufferTimeStrategy();
            setBitrateRange();
            setSubtitlePresetLanguage();
            setCloseLogo();
            setPreferAudioLang();
            setWakeMode(PlayControlUtil.isWakeOn());
            setSubtitleRenderByDemo(PlayControlUtil.isSubtitleRenderByDemo());
            wisePlayer.ready();
        }
    }

    /**
     * Start playing
     */
    public void start() {
        wisePlayer.start();
    }

    /**
     * Get the current play time
     *
     * @return The current play time
     */
    public int getCurrentTime() {
        if (wisePlayer != null) {
            return wisePlayer.getCurrentTime();
        } else {
            return 0;
        }
    }

    /**
     * Get total time
     *
     * @return Total time
     */
    public int getDuration() {
        if (wisePlayer != null) {
            return wisePlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * Drag the play
     *
     * @param progress Drag the time of position, the unit is milliseconds
     */
    public void updateCurProgress(int progress) {
        if (wisePlayer != null) {
            wisePlayer.seek(progress);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, seek fail.");
        }
    }

    /**
     * Binding player SurfaceView
     *
     * @param surfaceView The player SurfaceView
     */
    public void setSurfaceView(SurfaceView surfaceView) {
        if (wisePlayer != null) {
            wisePlayer.setView(surfaceView);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set surfaceView fail.");
        }
    }

    /**
     * Binding player TextureView
     *
     * @param textureView The player TextureView
     */
    public void setTextureView(TextureView textureView) {
        if (wisePlayer != null) {
            wisePlayer.setView(textureView);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set textureView fail.");
        }
    }

    /**
     * The player suspend
     */
    public void suspend() {
        if (wisePlayer != null) {
            setBufferingStatus(false, false);
            if (!isSuspend) {
                wisePlayer.suspend();
                isSuspend = true;
            }
        } else {
            LogUtil.i(TAG, "wisePlayer is null, suspend fail.");
        }
    }

    /**
     * Release player
     */
    public void release() {
        if (wisePlayer != null) {
            wisePlayer.release();
            wisePlayer = null;
        }
        clearBitrateList();
    }

    /**
     * clear video bitrate list
     */
    private void clearBitrateList() {
        if (bitrateRangeList != null) {
            bitrateRangeList.clear();
            bitrateRangeList = null;
        }
        if (switchBitrateList != null) {
            switchBitrateList.clear();
            switchBitrateList = null;
        }
    }

    /**
     * stop play
     */
    public void stop() {
        if (wisePlayer != null) {
            wisePlayer.stop();
        } else {
            LogUtil.i(TAG, "wisePlayer is null, stop fail.");
        }
    }

    /**
     * Set the play/pause state
     *
     * @param isPlaying The player status
     */
    public void setPlayData(boolean isPlaying) {
        if (wisePlayer == null) {
            LogUtil.i(TAG, "wisePlayer is null, pause or start fail.");
            return;
        }
        if (isPlaying) {
            wisePlayer.pause();
            setBufferingStatus(false, false);
        } else {
            wisePlayer.start();
            setBufferingStatus(true, false);
        }
    }

    /**
     * Gets the current play name
     *
     * @return The current play name
     */
    public String getCurrentPlayName() {
        if (currentPlayData != null) {
            return StringUtil.getNotEmptyString(currentPlayData.getName());
        } else {
            return "";
        }
    }

    /**
     * Players resume play
     *
     * @param play Resume after the player is in a state of play or pause state 0:1: pause play - 1: keep
     */
    public void playResume(int play) {
        LogUtil.i(TAG, "play resume :" + play);
        if (wisePlayer != null && isSuspend) {
            setBufferingStatus(true, false);
            wisePlayer.resume(play);
            isSuspend = false;
        } else {
            LogUtil.i(TAG, "wisePlayer is null, resume fail.");
        }
    }

    /**
     * Set the player whether to allow for the buffer.Usage scenarios such as suspended under 4g networks, not for the
     * buffer
     *
     * @param status False: stop the background buffer load.True: allows the background buffer load (the default)
     * @param isUpdateLocal Whether to update the local configuration
     */

    public void setBufferingStatus(boolean status, boolean isUpdateLocal) {
        if (wisePlayer != null && (isUpdateLocal || PlayControlUtil.isLoadBuff())) {
            wisePlayer.setBufferingStatus(status);
            if (isUpdateLocal) {
                PlayControlUtil.setLoadBuff(status);
            }
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set buffer status fail.");
        }
    }

    /**
     * Set the speed
     *
     * @param speedValue The speed of the string
     */
    public void setPlaySpeed(String speedValue) {
        if (speedValue.equals("1.25x")) {
            wisePlayer.setPlaySpeed(1.25f);
        } else if (speedValue.equals("1.5x")) {
            wisePlayer.setPlaySpeed(1.5f);
        } else if (speedValue.equals("1.75x")) {
            wisePlayer.setPlaySpeed(1.75f);
        } else if (speedValue.equals("2.0x")) {
            wisePlayer.setPlaySpeed(2.0f);
        } else if (speedValue.equals("0.5x")) {
            wisePlayer.setPlaySpeed(0.5f);
        } else if (speedValue.equals("0.75x")) {
            wisePlayer.setPlaySpeed(0.75f);
        } else {
            wisePlayer.setPlaySpeed(1.0f);
        }
    }

    /**
     * Get the current cache progress
     *
     * @return Cache progress unit of milliseconds
     */
    public int getBufferTime() {
        if (wisePlayer != null) {
            return wisePlayer.getBufferTime();
        } else {
            return 0;
        }
    }

    /**
     * Get to download speed
     *
     * @return Download speed unit b/s
     */
    public long getBufferingSpeed() {
        if (wisePlayer != null) {
            return wisePlayer.getBufferingSpeed();
        } else {
            return 0;
        }
    }

    /**
     * Get bitrate list data (String)
     *
     * @return Bitrate list data
     */
    public List<String> getBitrateStringList() {
        if (switchBitrateList == null || switchBitrateList.size() == 0) {
            switchBitrateList = new ArrayList<>();
            bitrateRangeList = new ArrayList<>();
            if (wisePlayer != null) {
                VideoInfo videoInfo = wisePlayer.getVideoInfo();
                if (videoInfo != null && videoInfo.getStreamInfos() != null) {
                    Collections.sort(videoInfo.getStreamInfos(), new StreamInfoList());
                    for (StreamInfo streamInfo : videoInfo.getStreamInfos()) {
                        if (streamInfo != null) {
                            String bitrateValue = DataFormatUtil.getVideoQuality(context, streamInfo.getVideoHeight());
                            if (!TextUtils.isEmpty(bitrateValue) && !switchBitrateList.contains(bitrateValue)) {
                                switchBitrateList.add(bitrateValue);
                            }
                            addBitrateRangeList(streamInfo);
                        }
                    }
                }
            }
        }
        return switchBitrateList;
    }

    /**
     * Set the bitrate range
     *
     * @param minBitrate The min bitrate value
     * @param maxBitrate The max bitrate value
     */
    public void setBitrateRange(int minBitrate, int maxBitrate) {
        if (wisePlayer != null) {
            wisePlayer.setBitrateRange(minBitrate, maxBitrate);
        }
    }

    /**
     * Add bitrate range list data
     *
     * @param streamInfo Bitrate data
     */
    private void addBitrateRangeList(StreamInfo streamInfo) {
        BitrateInfo bitrateInfo = new BitrateInfo();
        bitrateInfo.setCurrentBitrate(streamInfo.getBitrate());
        bitrateInfo.setMaxBitrate(streamInfo.getBitrate());
        bitrateInfo.setVideoHeight(streamInfo.getVideoHeight());
        if (bitrateRangeList.size() == 0) {
            bitrateInfo.setMinBitrate(0);
            bitrateRangeList.add(bitrateInfo);
        } else {
            BitrateInfo lastBitrateInfo = bitrateRangeList.get(bitrateRangeList.size() - 1);
            bitrateInfo.setMinBitrate(lastBitrateInfo.getCurrentBitrate());
            if (isContainsVideoHeight(streamInfo.getVideoHeight())) {
                bitrateRangeList.set(bitrateRangeList.size() - 1, bitrateInfo);
            } else {
                bitrateRangeList.add(bitrateInfo);
            }
        }
    }

    /**
     * check Whether add video height or not
     *
     * @param videoHeight video height
     * @return Whether or not
     */
    private boolean isContainsVideoHeight(int videoHeight) {
        for (BitrateInfo bitrateInfo : bitrateRangeList) {
            if (bitrateInfo.getVideoHeight() == videoHeight) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get bitrate range list
     *
     * @return Bitrate range list
     */
    public List<BitrateInfo> getBitrateRangeList() {
        return bitrateRangeList;
    }

    /**
     * Get bitrate list
     *
     * @return Bitrate list
     */
    public List<String> getSwitchBitrateList() {
        return switchBitrateList;
    }

    /**
     * Gets the current rate in Dialog rate index in the list
     *
     * @return The location of the current rate, the default back to the first
     */
    public int getCurrentBitrateIndex() {
        StreamInfo streamInfo = wisePlayer.getCurrentStreamInfo();
        if (bitrateRangeList != null) {
            for (int i = 0; i < bitrateRangeList.size(); i++) {
                BitrateInfo bitrateInfo = bitrateRangeList.get(i);
                if (streamInfo.getBitrate() >= (bitrateInfo.getMinBitrate() + Constants.BITRATE_WITHIN_RANGE)
                        && streamInfo.getBitrate() <= (bitrateInfo.getMaxBitrate() + Constants.BITRATE_WITHIN_RANGE)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Smooth transition rate
     *
     * @param currentBitrate The current need to set the bitrate
     */
    public void switchBitrateSmooth(int currentBitrate) {
        if (wisePlayer != null) {
            LogUtil.d(TAG, "switch bitrate smooth : currentBitrate " + currentBitrate);
            wisePlayer.switchBitrateSmooth(currentBitrate);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, switch bitrate smooth fail.");
        }
    }

    /**
     * Designated transition rate
     *
     * @param currentBitrate The current need to set the bitrate
     */
    public void switchBitrateDesignated(int currentBitrate) {
        if (wisePlayer != null) {
            LogUtil.d(TAG, "switch bitrate designated : currentBitrate " + currentBitrate);
            wisePlayer.switchBitrateDesignated(currentBitrate);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, switch bitrate designated fail.");
        }
    }

    /**
     * Get the play stream bitrate
     *
     * @return The play stream bitrate
     */
    public int getCurrentBitrate() {
        if (wisePlayer != null) {
            StreamInfo streamInfo = wisePlayer.getCurrentStreamInfo();
            if (streamInfo != null) {
                return streamInfo.getBitrate();
            } else {
                LogUtil.d(TAG, "get current bitrate info is empty!");
            }
        }
        return 0;
    }

    /**
     * Get the video height
     *
     * @return The video height
     */
    public int getCurrentVideoHeight() {
        if (wisePlayer != null) {
            StreamInfo streamInfo = wisePlayer.getCurrentStreamInfo();
            if (streamInfo != null) {
                return streamInfo.getVideoHeight();
            } else {
                LogUtil.d(TAG, "get current bitrate info is empty!");
            }
        }
        return 0;
    }

    /**
     * Set the bandwidth switching mode
     *
     * @param mod The bandwidth switching mode
     * @param updateLocate Whether to update the local configuration
     */
    public void setBandwidthSwitchMode(int mod, boolean updateLocate) {
        if (wisePlayer != null) {
            wisePlayer.setBandwidthSwitchMode(mod);
        }
        if (updateLocate) {
            PlayControlUtil.setBandwidthSwitchMode(mod);
        }
    }

    /**
     * Get play speed
     *
     * @return Play speed
     */
    public float getPlaySpeed() {
        if (wisePlayer != null) {
            return wisePlayer.getPlaySpeed();
        }
        return 1f;
    }

    /**
     * Close logo
     */
    public void closeLogo() {
        if (wisePlayer != null) {
            wisePlayer.closeLogo();
        } else {
            LogUtil.i(TAG, "wisePlayer is null, close logo fail.");
        }
    }

    /**
     * Set play mode
     *
     * @param playMode Play mode
     * @param updateLocate Whether to update the local configuration
     */
    public void setPlayMode(int playMode, boolean updateLocate) {
        if (wisePlayer != null) {
            wisePlayer.setPlayMode(playMode);
        }
        if (updateLocate) {
            PlayControlUtil.setPlayMode(playMode);
        }
    }

    /**
     * Get play mode
     *
     * @return Play mode
     */
    public int getPlayMode() {
        if (wisePlayer != null) {
            return wisePlayer.getPlayMode();
        } else {
            return 1;
        }
    }

    /**
     * Set cycle mode
     *
     * @param isCycleMode Whether open loop
     */
    public void setCycleMode(boolean isCycleMode) {
        if (wisePlayer != null) {
            wisePlayer.setCycleMode(isCycleMode ? CycleMode.MODE_CYCLE : CycleMode.MODE_NORMAL);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set cycle mode fail.");
        }
    }

    /**
     * Whether cycle mode
     *
     * @return Is cycle mode
     */
    public boolean isCycleMode() {
        if (wisePlayer != null) {
            return wisePlayer.getCycleMode() == CycleMode.MODE_CYCLE;
        } else {
            return false;
        }
    }

    /**
     * Set the mute
     *
     * @param status Whether quiet
     */
    public void setMute(boolean status) {
        if (wisePlayer != null) {
            wisePlayer.setMute(status);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set mute fail.");
        }
        PlayControlUtil.setIsMute(status);
    }

    /**
     * Set the volume, the current player is interval [0, 1]
     *
     * @param volume The volume interval [0, 1]
     */
    public void setVolume(float volume) {
        if (wisePlayer != null) {
            LogUtil.d(TAG, "current set volume is " + volume);
            wisePlayer.setVolume(volume);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set volume fail.");
        }
    }

    /**
     * Set play type 0: on demand (the default) 1: live
     *
     * @param videoType Play types
     * @param updateLocate Whether to update the local configuration
     */
    public void setVideoType(int videoType, boolean updateLocate) {
        if (wisePlayer != null) {
            wisePlayer.setVideoType(videoType);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set video type fail.");
        }
        if (updateLocate) {
            PlayControlUtil.setVideoType(videoType);
        }
    }

    /**
     * Set change notification
     */
    public void setSurfaceChange() {
        if (wisePlayer != null) {
            wisePlayer.setSurfaceChange();
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set surface change fail.");
        }
    }

    /**
     * Set up the bitrate
     */
    public void setInitBitrateEnable() {
        if (PlayControlUtil.isInitBitrateEnable() && wisePlayer != null) {
            InitBitrateParam initBitrateParam = new InitBitrateParam();
            initBitrateParam.setBitrate(PlayControlUtil.getInitBitrate());
            initBitrateParam.setHeight(PlayControlUtil.getInitHeight());
            initBitrateParam.setWidth(PlayControlUtil.getInitWidth());
            initBitrateParam.setType(PlayControlUtil.getInitType());
            wisePlayer.setInitBitrate(initBitrateParam);
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set init bitrate fail.");
        }
    }

    /**
     * Set the bitrate range
     */
    public void setBitrateRange() {
        if (PlayControlUtil.isSetBitrateRangeEnable() && wisePlayer != null) {
            wisePlayer.setBitrateRange(PlayControlUtil.getMinBitrate(), PlayControlUtil.getMaxBitrate());
            PlayControlUtil.clearBitrateRange();
        } else {
            LogUtil.i(TAG, "wisePlayer is null, set bitrate range fail.");
        }
    }

    /**
     * setting init BufferTime
     */
    public void setInitBufferTimeStrategy() {
        if (wisePlayer != null) {
            LogUtil.d("begin set InitBufferTimeStrategy");
            wisePlayer.setInitBufferTimeStrategy(PlayControlUtil.getInitBufferTimeStrategy());
            PlayControlUtil.setInitBufferTimeStrategy(null);
        }
    }

    /**
     * Remove the current play bookmark
     */
    public void clearPlayProgress() {
        if (currentPlayData != null) {
            LogUtil.d("clear current progress " + currentPlayData.getUrl());
            PlayControlUtil.clearPlayData(currentPlayData.getUrl());
        }
    }

    /**
     * Save the current sources of progress
     */
    public void savePlayProgress() {
        if (currentPlayData != null && wisePlayer != null) {
            LogUtil.d("save current progress " + wisePlayer.getCurrentTime());
            PlayControlUtil.savePlayData(currentPlayData.getUrl(), wisePlayer.getCurrentTime());
        }
    }

    /**
     * Bookmark play position
     *
     * @param time The bookmark time
     */
    public void setBookmark(int time) {
        if (wisePlayer == null) {
            LogUtil.i("wisePlayer is null.");
            return;
        }
        if (time != Constants.DEFAULT_BOOKMARK_VALUE) {
            wisePlayer.setBookmark(time);
        } else if (currentPlayData != null) {
            int bookmark = PlayControlUtil.getPlayData(currentPlayData.getUrl());
            LogUtil.d("current book mark is " + bookmark);
            if (bookmark != -1) {
                wisePlayer.setBookmark(bookmark);
            }
        }
    }

    /**
     * Get XML data in the sdcard
     *
     * @return Data list
     */
    public List<PlayEntity> getPlayList() {
        if (playEntityList == null || playEntityList.size() == 0) {
            playEntityList = new ArrayList<>();
            playEntityList.addAll(DataFormatUtil.getPlayList(context));
        }
        return playEntityList;
    }

    /**
     * refresh play url
     */
    public void refresh() {
        if (wisePlayer != null && currentPlayData != null) {
            wisePlayer.refreshPlayUrl(currentPlayData.getUrl());
        }
    }

    /**
     * Get the selected data
     *
     * @param position The selected position
     * @return The play data
     */
    public PlayEntity getPlayFromPosition(int position) {
        if (playEntityList != null && playEntityList.size() > position) {
            return playEntityList.get(position);
        }
        return null;
    }

    /**
     * Player reset
     */
    public void reset() {
        if (wisePlayer != null) {
            wisePlayer.reset();
        }
        clearBitrateList();
    }

    /**
     * Close logo
     */
    public void setCloseLogo() {
        if (wisePlayer != null) {
            if (PlayControlUtil.isCloseLogo()) {
                if (!PlayControlUtil.isTakeEffectOfAll()) {
                    PlayControlUtil.setCloseLogo(false);
                }
                wisePlayer.closeLogo();
            }
        }
    }

    /**
     * Video into the background
     */
    public void onPause() {
        if (currentPlayData != null && wisePlayer != null) {
            PlayControlUtil.savePlayData(currentPlayData.getUrl(), wisePlayer.getCurrentTime());
            suspend();
        }
    }

    public boolean isHttpVideo() {
        return isHttpVideo;
    }

    private void setHttpVideo(boolean httpVideo) {
        isHttpVideo = httpVideo;
    }

    /**
     * Switch subtitle
     *
     * @param trackId 字幕 track id
     */
    public void switchSubtitle(int trackId) {
        LogUtil.d(TAG, "switchSubtitle trackId：" + trackId);
        wisePlayer.selectSubtitleTrack(trackId);
    }

    /**
     * Close subtitle
     */
    public void closeSubtitle() {
        LogUtil.d(TAG, "closeSubtitle");
        wisePlayer.deselectSubtitleTrack();
    }

    /**
     * Set subtitle preset language
     */
    public void setSubtitlePresetLanguage() {
        if (PlayControlUtil.isSubtitlePresetLanguageEnable() && wisePlayer != null) {
            wisePlayer.presetSubtitleLanguage(PlayControlUtil.getSubtitlePresetLanguage());
            PlayControlUtil.clearSubtitlePresetLanguage();
        }
    }

    /**
     * Stream data sorting class
     */
    static class StreamInfoList implements Comparator<StreamInfo>, Serializable {

        private static final long serialVersionUID = -7763024398518367069L;

        @Override
        public int compare(StreamInfo streamInfo, StreamInfo lastStreamInfo) {
            if (streamInfo.getBitrate() >= lastStreamInfo.getBitrate()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public AudioTrackInfo[] getAudioTracks() {
        AudioTrackInfo[] audioTrack = null;
        if (wisePlayer != null) {
            audioTrack = wisePlayer.getAudioTracks();
            if (audioTrack == null || audioTrack.length == 0 ) {
                LogUtil.d(TAG, "getAudioTracks get null");
                return null;
            }
            for (int i = 0; i < audioTrack.length; i++) {
                LogUtil.d(TAG, "getAudioTracks is:" + audioTrack[i].getId() + " desc:" + audioTrack[i].getDesc()
                        + " select:" + audioTrack[i].getSelected());
            }
        }
        return audioTrack;
    }

    public AudioTrackInfo getSelectedAudioTrack() {
        AudioTrackInfo audioTrack = null;
        if (wisePlayer != null) {
            Object obj = wisePlayer.getSelectedAudioTrack();
            if (obj == null) {
                LogUtil.d(TAG, "getSelectedAudioTrack get null");
                return null;
            }
            audioTrack = (AudioTrackInfo) obj;
            LogUtil.d(TAG, "getSelectedAudioTrack is:" + audioTrack.getId() + " desc:" + audioTrack.getDesc()
                    + " select:" + audioTrack.getSelected());
        }
        return audioTrack;
    }

    public void switchAudioTrack(String audioTrackname) {
        LogUtil.d(TAG, "switchAudioTrack the audio switch to is:" + audioTrackname);
        if (wisePlayer != null) {
            AudioTrackInfo[] audioTrack = null;
            audioTrack = wisePlayer.getAudioTracks();
            if (audioTrack == null) {
                return;
            }
            for (int i = 0; i < audioTrack.length; i++) {
                if (audioTrack[i].getDesc().equals(audioTrackname)) {
                    LogUtil.d(TAG, "switchAudioTrack switch to:" + audioTrack[i].getId());
                    wisePlayer.selectAudioTrack(audioTrack[i].getId());
                    break;
                }
            }
        }
    }

    public int getAudioLangIndex() {
        if (wisePlayer != null) {
            return wisePlayer.getSelectedAudioTrack().getId();
        } else {
            return 0;
        }
    }

    public void setPreferAudioLang() {
        if (wisePlayer != null) {
            LogUtil.d("start set prefer language");
            wisePlayer.presetAudioLanguage(PlayControlUtil.getPreferAudio());
        }
    }

    public void setProxyInfo() {
        if (wisePlayer != null) {
            LogUtil.d("start setProxyInfo");
            wisePlayer.setProxy(PlayControlUtil.getProxyInfo());
        }
    }

    public void setDownloadLink() {
        if (wisePlayer != null) {
            LogUtil.d("setDownloadLink, is download link single:" + PlayControlUtil.isDownloadLinkSingle());
            wisePlayer.setProperties(PlayerConstants.Properties.SINGLE_LINK_DOWNLOAD, PlayControlUtil.isDownloadLinkSingle());
        }
    }

    public void setWakeMode(boolean isWakeOn) {
        if (isWakeOn) {
            wisePlayer.setWakeMode(context, PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE);
        } else {
            wisePlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        }
    }

    public void setSubtitleRenderByDemo(boolean isRenderByDemo) {
        PlayControlUtil.setSubtitleRenderByDemo(isRenderByDemo);
        if (isRenderByDemo) {
            wisePlayer.setSubtitleUpdateListener(onWisePlayerListener);
        } else {
            wisePlayer.setSubtitleUpdateListener(null);
        }
    }

    public boolean isPlaying() {
        if (wisePlayer != null) {
            return wisePlayer.isPlaying();
        } else {
            return false;
        }
    }
}