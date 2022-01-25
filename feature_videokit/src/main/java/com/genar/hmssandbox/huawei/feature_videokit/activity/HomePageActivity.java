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

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_videokit.R;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnDialogConfirmListener;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnHomePageListener;
import com.genar.hmssandbox.huawei.feature_videokit.control.HomePageControl;
import com.genar.hmssandbox.huawei.feature_videokit.entity.PlayEntity;
import com.genar.hmssandbox.huawei.feature_videokit.utils.Constants;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DialogUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.LogUtil;
import com.genar.hmssandbox.huawei.feature_videokit.utils.PermissionUtils;
import com.genar.hmssandbox.huawei.feature_videokit.utils.PlayControlUtil;
import com.genar.hmssandbox.huawei.feature_videokit.view.HomePageView;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.bean.recommend.RecommendOptions;
import com.huawei.hms.videokit.player.bean.recommend.RecommendVideo;
import com.huawei.hms.videokit.player.common.PlayerConstants.BandwidthSwitchMode;
import com.huawei.hms.videokit.player.common.PlayerConstants.PlayMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Home page activity
 */
public class HomePageActivity extends AppCompatActivity implements OnHomePageListener {
    private static final int MSG_REQUEST_WRITE_SDCARD = 1;

    // Home page view
    private HomePageView homePageView;

    // Home page control
    private HomePageControl homePageControl;

    //parms context
    private String PARMS_CONTEXT = "gEKQBehAEs5xcnc81KAY8MS7L8fNop7IMq0LaXmTRjUSZVoG9UrBfFDvt76D";

    //abstract
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
        setupToolbar();
        PermissionUtils.requestPermissionsIfNeed(this, new String[]{permission.WRITE_EXTERNAL_STORAGE},
                MSG_REQUEST_WRITE_SDCARD);
    }

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.tb_video);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, myToolbar, getResources().getString(com.genar.hmssandbox.huawei.R.string.video_kit_link_documentation_link));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<String> list = new ArrayList<>();
        switch (item.getItemId()) {
            /*case android.R.id.home:
               onBackPressed();
                break;*/

            case R.id.video_type_setting:
                list.clear();
                list.add(getResources().getString(R.string.video_on_demand));
                list.add(getResources().getString(R.string.video_live));
                homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_MODE, list,
                        PlayControlUtil.getVideoType());
                break;
            case R.id.video_view_setting:
                list.clear();
                list.add(getResources().getString(R.string.video_surfaceview_setting));
                list.add(getResources().getString(R.string.video_textureview_setting));
                homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_VIEW, list,
                        PlayControlUtil.isSurfaceView() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
                break;
            case R.id.video_mute_setting:
                list.clear();
                list.add(getResources().getString(R.string.video_mute));
                list.add(getResources().getString(R.string.video_not_mute));
                homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_MUTE, list,
                        PlayControlUtil.isMute() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
                break;
            case R.id.video_play_setting:
                list.clear();
                list.add(getResources().getString(R.string.play_video));
                list.add(getResources().getString(R.string.play_audio));
                homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_VIDEO_PLAY, list,
                        PlayControlUtil.getPlayMode());
                break;
            case R.id.video_bandwidth_setting:
                list.clear();
                list.add(getResources().getString(R.string.open_adaptive_bandwidth));
                list.add(getResources().getString(R.string.close_adaptive_bandwidth));
                homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_BANDWIDTH, list,
                        PlayControlUtil.getBandwidthSwitchMode());
                break;
            case R.id.video_init_bitrate_setting:
                list.clear();
                list.add(getResources().getString(R.string.video_init_bitrate_use));
                list.add(getResources().getString(R.string.video_init_bitrate_not_use));
                homePageView.showVideoTypeDialog(Constants.PLAYER_SWITCH_INIT_BANDWIDTH, list,
                        PlayControlUtil.isInitBitrateEnable() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
                break;
            case R.id.video_bitrate_range_setting:
                DialogUtil.showBitrateRangeDialog(this);
                break;
            case R.id.video_init_preloader:
                DialogUtil.initPreloaderDialog(this, new OnDialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        DialogUtil.addSingleCacheDialog(HomePageActivity.this);
                    }
                });
                break;
            case R.id.video_add_single_cache:
                DialogUtil.addSingleCacheDialog(HomePageActivity.this);
                break;
            case R.id.video_pause_cache:
                homePageControl.pauseAllTasks();
                break;
            case R.id.video_resume_cache:
                homePageControl.resumeAllTasks();
                break;
            case R.id.video_remove_cache:
                homePageControl.removeAllCache();
                break;
            case R.id.video_remove_tasks:
                homePageControl.removeAllTasks();
                break;
            case R.id.video_set_alg_param:
                DialogUtil.setInitBufferTimeStrategy(this);
                break;
            case R.id.video_update_country:
                DialogUtil.updateServerCountryDialog(this);
                break;
            case R.id.recommend_video_info:
                RecommendOptions recommendOptions = new RecommendOptions();
                recommendOptions.setLanguage("zh_CN");
                SandboxApplication.getWisePlayerFactory()
                        .createWisePlayer()
                        .getRecommendVideoList("8859289", recommendOptions,
                                "CgB6e3x9cDTitEyidsqxd/Q6cmh/" + PARMS_CONTEXT,
                                recommendVideoCallback);
                break;
            case R.id.subtitle_preset_language_setting:
                DialogUtil.showSubtitlePresetLanguageDialog(this);
                break;
            case R.id.audio_set_prefer_audio:
                DialogUtil.showPreferAudioLangDialog(this);
                break;
            case R.id.socks_proxy:
                DialogUtil.showProxyInfoDialog(this);
                break;
            case R.id.download_link_num_setting:
                list.clear();
                list.add(getResources().getString(R.string.download_link_single));
                list.add(getResources().getString(R.string.download_link_multi));
                homePageView.showVideoTypeDialog(Constants.DOWNLOAD_LINK_NUM, list,
                        PlayControlUtil.isDownloadLinkSingle() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
                break;
            case R.id.set_wake_mode:
                list.clear();
                list.add(getResources().getString(R.string.set_wake_mode));
                list.add(getResources().getString(R.string.close_wake_mode));
                homePageView.showVideoTypeDialog(Constants.SET_WAKE_MODE, list,
                        PlayControlUtil.isWakeOn() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
                break;
            case R.id.subtitle_render:
                list.clear();
                list.add(getResources().getString(R.string.subtitle_render_player));
                list.add(getResources().getString(R.string.subtitle_render_demo));
                homePageView.showVideoTypeDialog(Constants.SET_SUBTITLE_RENDER_MODE, list,
                        PlayControlUtil.isSubtitleRenderByDemo() ? Constants.DIALOG_INDEX_TWO : Constants.DIALOG_INDEX_ONE);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
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
            default:
                break;
        }
    }
}
