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

package com.hms.explorehms.huawei.feature_videokit.activity;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.ExploreHMSApplication;
import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_videokit.R;
import com.hms.explorehms.huawei.feature_videokit.contract.OnDialogConfirmListener;
import com.hms.explorehms.huawei.feature_videokit.contract.OnHomePageListener;
import com.hms.explorehms.huawei.feature_videokit.control.HomePageControl;
import com.hms.explorehms.huawei.feature_videokit.entity.PlayEntity;
import com.hms.explorehms.huawei.feature_videokit.utils.Constants;
import com.hms.explorehms.huawei.feature_videokit.utils.DialogUtil;
import com.hms.explorehms.huawei.feature_videokit.utils.LogUtil;
import com.hms.explorehms.huawei.feature_videokit.utils.PermissionUtils;
import com.hms.explorehms.huawei.feature_videokit.utils.PlayControlUtil;
import com.hms.explorehms.huawei.feature_videokit.utils.StringUtil;
import com.hms.explorehms.huawei.feature_videokit.view.HomePageView;
import com.huawei.hms.videokit.player.CreateComponentException;
import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.LogConfigInfo;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptionsExt;
import com.huawei.hms.videokit.player.bean.recommend.RecommendOptions;
import com.huawei.hms.videokit.player.bean.recommend.RecommendVideo;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.huawei.hms.videokit.player.common.PlayerConstants.BandwidthSwitchMode;
import com.huawei.hms.videokit.player.common.PlayerConstants.PlayMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Home page activity
 */
public class HomePageActivity extends AppCompatActivity implements OnHomePageListener {
    private static final String TAG = "HomePageActivity";

    private static final int MSG_REQUEST_WRITE_SDCARD = 1;

    // Home page view
    private HomePageView homePageView;

    // Home page control
    private HomePageControl homePageControl;

    // Params context
    private String paramsContext = "gEKQBehAEs5xcnc81KAY8MS7L8fNop7IMq0LaXmTRjUSZVoG9UrBfFDvt76D";

    // Abstract
    private static WisePlayer.IRecommendVideoCallback recommendVideoCallback =
            new WisePlayer.IRecommendVideoCallback() {
                @Override
                public void onSuccess(List<RecommendVideo> list) {
                    LogUtil.i("query recommend video success.");
                }

                @Override
                public void onFailed(int what, int extra, Object obj) {
                    LogUtil.i("query recommend video fail, and error code is " + what);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePageView = new HomePageView(this, this);
        homePageControl = new HomePageControl(this);
        setContentView(homePageView.getContentView());
        PermissionUtils.requestPermissionsIfNeed(this, new String[] {permission.WRITE_EXTERNAL_STORAGE},
                MSG_REQUEST_WRITE_SDCARD);
        initPlayer();
    }

    /**
     * Access to data, update the list
     */
    private void updateView() {
        homePageControl.loadPlayList();
        homePageView.updateRecyclerView(homePageControl.getPlayList());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            LogUtil.i("current request permissions grant result is empty!");
            return;
        }
        switch (requestCode) {
            case MSG_REQUEST_WRITE_SDCARD:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, this.getString(R.string.video_init_preload), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                LogUtil.d("Apply access failure");
                break;
        }
    }

    @Override
    public void onItemClick(int pos) {
        if (ExploreHMSApplication.getWisePlayerFactory() == null) {
            Toast.makeText(this, getString(R.string.wait_init_play), Toast.LENGTH_SHORT).show();
            return;
        }
        PlayEntity playEntity = homePageControl.getPlayFromPosition(pos);
        if (playEntity != null) {
            PlayActivity.startPlayActivity(this, playEntity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_play_btn:
                String inputUrl = homePageView.getInputUrl();
                if (TextUtils.isEmpty(inputUrl)) {
                    Toast.makeText(this, getResources().getString(R.string.input_path), Toast.LENGTH_SHORT).show();
                } else {
                    PlayActivity.startPlayActivity(this, homePageControl.getInputPlay(inputUrl));
                }
                break;
            case R.id.play_list_menu:
                onSettingDialog();
                break;
            default:
                break;
        }
    }

    /**
     * menu dialog
     */
    private void onSettingDialog() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_type));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_view_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_mute_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.play_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.automatic_adaptation));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_init_bitrate_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_bitrate_range_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_init_preloader));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_add_single_cache));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_pause_cache));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_resume_cache));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_remove_cache));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_remove_tasks));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.get_recommend_info));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.update_server_country));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_alg_para));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.subtitle_preset_language_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.audio_set_prefer_audio));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.set_socks_proxy));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.download_link_num));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.set_wake_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.subtitle_render));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.init_player));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.release_player));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.seek_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.resume_start_frame_mode));
        homePageView.showVideoTypeDialog(Constants.SET_HOME_SETTING, showTextList, 0);
    }

    private void doSetting(String itemSelect) {
        if (TextUtils.isEmpty(itemSelect)) {
            LogUtil.w(TAG, "setting type is null");
            return;
        }
        List<String> list = new ArrayList<>();
        if (TextUtils.equals(itemSelect, getString(R.string.video_type))) {
            list.clear();
            list.add(getResources().getString(R.string.video_on_demand));
            list.add(getResources().getString(R.string.video_live));
            homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_MODE, list, PlayControlUtil.getVideoType());
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_view_setting))) {
            list.clear();
            list.add(getResources().getString(R.string.video_surfaceview_setting));
            list.add(getResources().getString(R.string.video_textureview_setting));
            homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_VIEW, list,
                    PlayControlUtil.isSurfaceView() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_mute_setting))) {
            list.clear();
            list.add(getResources().getString(R.string.video_mute));
            list.add(getResources().getString(R.string.video_not_mute));
            homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_MUTE, list,
                    PlayControlUtil.isMute() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
        } else if (TextUtils.equals(itemSelect, getString(R.string.play_mode))) {
            list.clear();
            list.add(getResources().getString(R.string.play_video));
            list.add(getResources().getString(R.string.play_audio));
            homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_PLAY, list, PlayControlUtil.getPlayMode());
        } else if (TextUtils.equals(itemSelect, getString(R.string.automatic_adaptation))) {
            list.clear();
            list.add(getResources().getString(R.string.open_adaptive_bandwidth));
            list.add(getResources().getString(R.string.close_adaptive_bandwidth));
            homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_BANDWIDTH, list,
                    PlayControlUtil.getBandwidthSwitchMode());
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_init_bitrate_setting))) {
            list.clear();
            list.add(getResources().getString(R.string.video_init_bitrate_use));
            list.add(getResources().getString(R.string.video_init_bitrate_not_use));
            homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_INIT_BANDWIDTH, list,
                    PlayControlUtil.isInitBitrateEnable() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_bitrate_range_setting))) {
            DialogUtil.showBitrateRangeDialog(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_init_preloader))) {
            DialogUtil.initPreloaderDialog(this, new OnDialogConfirmListener() {
                @Override
                public void onConfirm() {
                    DialogUtil.addSingleCacheDialog(HomePageActivity.this);
                }
            });
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_add_single_cache))) {
            DialogUtil.addSingleCacheDialog(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_pause_cache))) {
            homePageControl.pauseAllTasks();
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_resume_cache))) {
            homePageControl.resumeAllTasks();
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_remove_cache))) {
            homePageControl.removeAllCache();
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_remove_tasks))) {
            homePageControl.removeAllTasks();
        } else if (TextUtils.equals(itemSelect, getString(R.string.update_server_country))) {
            DialogUtil.updateServerCountryDialog(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.get_recommend_info))) {
            RecommendOptions recommendOptions = new RecommendOptions();
            recommendOptions.setLanguage("zh_CN");
            try {
                ExploreHMSApplication.getWisePlayerFactory()
                        .createWisePlayer()
                        .getRecommendVideoList("8859289", recommendOptions, "CgB6e3x9cDTitEyidsqxd/Q6cmh/" + paramsContext,
                                recommendVideoCallback);
            } catch (Exception e) {
                LogUtil.w(TAG, "Obtain recommendations error:" + e.getMessage());
            }
        } else if (TextUtils.equals(itemSelect, getString(R.string.video_set_alg_para))) {
            DialogUtil.setInitBufferTimeStrategy(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.subtitle_preset_language_setting))) {
            DialogUtil.showSubtitlePresetLanguageDialog(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.audio_set_prefer_audio))) {
            DialogUtil.showPreferAudioLangDialog(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.set_socks_proxy))) {
            DialogUtil.showProxyInfoDialog(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.download_link_num))) {
            list.clear();
            list.add(getResources().getString(R.string.download_link_single));
            list.add(getResources().getString(R.string.download_link_multi));
            homePageView.showVideoTypeDialog(Constants.DOWNLOAD_LINK_NUM, list,
                    PlayControlUtil.isDownloadLinkSingle() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
        } else if (TextUtils.equals(itemSelect, getString(R.string.set_wake_mode))) {
            list.clear();
            list.add(getResources().getString(R.string.set_wake_mode));
            list.add(getResources().getString(R.string.close_wake_mode));
            homePageView.showVideoTypeDialog(Constants.SET_WAKE_MODE, list,
                    PlayControlUtil.isWakeOn() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
        } else if (TextUtils.equals(itemSelect, getString(R.string.subtitle_render))) {
            list.clear();
            list.add(getResources().getString(R.string.subtitle_render_player));
            list.add(getResources().getString(R.string.subtitle_render_demo));
            homePageView.showVideoTypeDialog(Constants.SET_SUBTITLE_RENDER_MODE, list,
                    PlayControlUtil.isSubtitleRenderByDemo() ? Constants.DIALOG_INDEX_TWO : Constants.DIALOG_INDEX_ONE);
        } else if (TextUtils.equals(itemSelect, getString(R.string.init_player))) {
            initPlayer();
        } else if (TextUtils.equals(itemSelect, getString(R.string.release_player))) {
            ExploreHMSApplication.release(this);
        } else if (TextUtils.equals(itemSelect, getString(R.string.seek_mode))) {
            list.clear();
            list.add(getResources().getString(R.string.seek_previous_sync));
            list.add(getResources().getString(R.string.seek_closest));
            homePageView.showVideoTypeDialog(Constants.SET_SEEK_MODE, list, PlayControlUtil.getSeekMode());
        } else if (TextUtils.equals(itemSelect, getString(R.string.resume_start_frame_mode))) {
            list.clear();
            list.add(getResources().getString(R.string.seek_previous_sync));
            list.add(getResources().getString(R.string.seek_closest));
            homePageView.showVideoTypeDialog(Constants.SET_RESUME_START_FRAME_MODE, list, PlayControlUtil.getResumeStartFrameMode());
        } else {
            LogUtil.i(TAG, "unavailable type");
        }
    }

    @Override
    public void onSettingItemClick(String itemSelect, int settingType) {
        switch (settingType) {
            case Constants.PLAYER_SWITCH_VIDEO_MODE:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.video_on_demand))) {
                    homePageControl.setVideoType(Constants.VIDEO_TYPE_ON_DEMAND);
                } else {
                    homePageControl.setVideoType(Constants.VIDEO_TYPE_LIVE);
                }
                break;
            case Constants.PLAYER_SWITCH_VIDEO_VIEW:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.video_surfaceview_setting))) {
                    homePageControl.setSurfaceViewView(true);
                } else {
                    homePageControl.setSurfaceViewView(false);
                }
                break;
            case Constants.PLAYER_SWITCH_VIDEO_MUTE:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.video_mute))) {
                    homePageControl.setMute(true);
                } else {
                    homePageControl.setMute(false);
                }
                break;
            case Constants.PLAYER_SWITCH_VIDEO_PLAY:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.play_audio))) {
                    homePageControl.setPlayMode(PlayMode.PLAY_MODE_AUDIO_ONLY);
                } else {
                    homePageControl.setPlayMode(PlayMode.PLAY_MODE_NORMAL);
                }
                break;
            case Constants.PLAYER_SWITCH_BANDWIDTH:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.close_adaptive_bandwidth))) {
                    homePageControl.setBandwidthSwitchMode(BandwidthSwitchMode.MANUAL_SWITCH_MODE);
                } else {
                    homePageControl.setBandwidthSwitchMode(BandwidthSwitchMode.AUTO_SWITCH_MODE);
                }
                break;
            case Constants.PLAYER_SWITCH_INIT_BANDWIDTH:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.video_init_bitrate_use))) {
                    homePageControl.setInitBitrateEnable(true);
                    DialogUtil.setInitBitrate(this);
                } else {
                    homePageControl.setInitBitrateEnable(false);
                }
                break;
            case Constants.PLAYER_SWITCH_CLOSE_LOGO:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.video_open_logo_setting))) {
                    homePageControl.setCloseLogo(false);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(getResources().getString(R.string.video_close_logo_one));
                    list.add(getResources().getString(R.string.video_close_logo_all));
                    homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_CLOSE_LOGO_EFFECT, list,
                            PlayControlUtil.isTakeEffectOfAll() ? Constants.DIALOG_INDEX_TWO : Constants.DIALOG_INDEX_ONE);
                }
                break;
            case Constants.PLAYER_SWITCH_CLOSE_LOGO_EFFECT:
                homePageControl.setCloseLogo(true);
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.video_close_logo_all))) {
                    homePageControl.setBandwidthSwitchMode(true);
                } else {
                    homePageControl.setBandwidthSwitchMode(false);
                }
                break;
            case Constants.DOWNLOAD_LINK_NUM:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.download_link_single))) {
                    homePageControl.setDownloadLink(true);
                } else {
                    homePageControl.setDownloadLink(false);
                }
                break;
            case Constants.SET_WAKE_MODE:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.set_wake_mode))) {
                    homePageControl.setWakeMode(true);
                } else {
                    homePageControl.setWakeMode(false);
                }
                break;
            case Constants.SET_SUBTITLE_RENDER_MODE:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.subtitle_render_player))) {
                    homePageControl.setSubtitleRenderByDemo(false);
                } else {
                    homePageControl.setSubtitleRenderByDemo(true);
                }
                break;
            case Constants.SET_HOME_SETTING:
                doSetting(itemSelect);
                break;
            case Constants.SET_SEEK_MODE:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.seek_previous_sync))) {
                    LogUtil.i(TAG, "set preview picture listener");
                    PlayControlUtil.setSeekMode(PlayerConstants.SeekMode.PREVIOUS_SYNC);
                } else {
                    LogUtil.i(TAG, "cancel preview picture listener");
                    PlayControlUtil.setSeekMode(PlayerConstants.SeekMode.CLOSEST);
                }
                break;
            case Constants.SET_RESUME_START_FRAME_MODE:
                if (TextUtils.equals(itemSelect, getResources().getString(R.string.seek_previous_sync))) {
                    LogUtil.i(TAG, "set preview picture listener");
                    PlayControlUtil.setResumeStartFrameMode(PlayerConstants.SeekMode.PREVIOUS_SYNC);
                } else {
                    LogUtil.i(TAG, "cancel preview picture listener");
                    PlayControlUtil.setResumeStartFrameMode(PlayerConstants.SeekMode.CLOSEST);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (homePageView.menuHasFocus()) {
            homePageView.setMenuBackgroundColor();
        } else {
            homePageView.clearMenuBackgroundColor();
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Init the player
     */
    private void initPlayer() {
        // Initializing the player is best placed in a child thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
                WisePlayerFactoryOptionsExt.Builder factoryOptions =
                        new WisePlayerFactoryOptionsExt.Builder().setDeviceId("xxx").setEnableIPV6(true);
                LogConfigInfo logCfgInfo =
                        new LogConfigInfo(Constants.LEVEL_DEBUG, "", Constants.LOG_FILE_NUM, Constants.LOG_FILE_SIZE);
                factoryOptions.setLogConfigInfo(logCfgInfo);
                WisePlayerFactory.initFactory(HomePageActivity.this, factoryOptions.build(), initFactoryCallback);
            }
        }).start();
    }

    /**
     * Player initialization callback
     */
    private static InitFactoryCallback initFactoryCallback = new InitFactoryCallback() {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            LogUtil.i(TAG, "init player factory success");
            ExploreHMSApplication.setWisePlayerFactory(wisePlayerFactory);
        }

        @Override
        public void onFailure(int errorCode, String reason) {
            LogUtil.w(TAG, "init player factory fail reason :" + reason + ", errorCode is " + errorCode);
        }
    };
}