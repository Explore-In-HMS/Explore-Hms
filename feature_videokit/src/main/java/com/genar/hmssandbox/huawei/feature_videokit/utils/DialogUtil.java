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

package com.genar.hmssandbox.huawei.feature_videokit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.feature_videokit.R;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnDialogConfirmListener;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnDialogInputValueListener;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnHomePageListener;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnPlaySettingListener;
import com.genar.hmssandbox.huawei.feature_videokit.view.PlaySettingDialog;
import com.huawei.hms.videokit.player.CacheInfo;
import com.huawei.hms.videokit.player.InitBufferTimeStrategy;
import com.huawei.hms.videokit.player.Preloader;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.bean.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Dialog tools
 */
public class DialogUtil {
    private static final String TAG = "DialogUtil";
    /**
     * Set Bitrate dialog
     *
     * @param context Context
     */
    public static void setInitBitrate(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.init_bitrate, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(StringUtil.getStringFromResId(context, R.string.video_init_bitrate_setting))
            .setView(view)
            .create();
        dialog.show();

        final CheckBox checkUpTo = (CheckBox) view.findViewById(R.id.check_up_to);
        if (PlayControlUtil.getInitType() == 0) {
            checkUpTo.setChecked(true);
        } else {
            checkUpTo.setChecked(false);
        }
        final EditText initBitrateEt = (EditText) view.findViewById(R.id.init_bitrate_et);
        initBitrateEt.setText(String.valueOf(PlayControlUtil.getInitBitrate()));
        final EditText widthEt = (EditText) view.findViewById(R.id.init_width_et);
        widthEt.setText(String.valueOf(PlayControlUtil.getInitWidth()));
        final EditText heightEt = (EditText) view.findViewById(R.id.init_height_et);
        heightEt.setText(String.valueOf(PlayControlUtil.getInitHeight()));

        Button okBt = (Button) view.findViewById(R.id.ok_bt);
        Button cancelBt = (Button) view.findViewById(R.id.cancel_bt);

        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayControlUtil.setInitType(checkUpTo.isChecked() ? 0 : 1);
                PlayControlUtil.setInitBitrate(Integer.parseInt(initBitrateEt.getText().toString()));
                PlayControlUtil.setInitHeight(Integer.parseInt(heightEt.getText().toString()));
                PlayControlUtil.setInitWidth(Integer.parseInt(widthEt.getText().toString()));
                dialog.dismiss();
            }
        });

        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Home page setup dialog
     *
     * @param context Context
     * @param playSettingType Set the play type
     * @param settingList Set the list of options
     * @param defaultSelect The default Settings string
     * @param onHomePageListener Click listener
     */
    public static void showVideoTypeDialog(Context context, int playSettingType, List<String> settingList,
        int defaultSelect, OnHomePageListener onHomePageListener) {
        PlaySettingDialog playSettingDialog = new PlaySettingDialog(context);
        playSettingDialog.initDialog(onHomePageListener, playSettingType);
        playSettingDialog.setList(settingList);
        playSettingDialog.setSelectIndex(defaultSelect);
        playSettingDialog.setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        playSettingDialog.show();
    }

    /**
     * Play activity Settings dialog
     *
     * @param context Context
     * @param settingType Set the play type
     * @param showTextList Set the list of options
     * @param selectIndex The default Settings index
     * @param onPlaySettingListener Click listener
     */
    public static void onSettingDialogSelectIndex(Context context, int settingType, List<String> showTextList,
        int selectIndex, OnPlaySettingListener onPlaySettingListener) {
        PlaySettingDialog dialog = new PlaySettingDialog(context).setList(showTextList);
        dialog.setTitle(StringUtil.getStringFromResId(context, R.string.settings));
        dialog.setSelectIndex(selectIndex);
        dialog.setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        dialog.initDialog(onPlaySettingListener, settingType);
        dialog.show();
    }

    /**
     *  Play activity Gettings dialog
     *
     * @param context Context
     * @param gettingType Get the play type
     * @param showTextList Get the list of options
     * @param selectIndex The default Gettings index
     * @param onPlaySettingListener Click listener
     */
    public static void onGettingDialogSelectIndex(Context context, int gettingType, List<String> showTextList,
        int selectIndex, OnPlaySettingListener onPlaySettingListener) {
        PlaySettingDialog dialog = new PlaySettingDialog(context).setList(showTextList);
        dialog.setTitle(StringUtil.getStringFromResId(context, R.string.gettings));
        dialog.setSelectIndex(selectIndex);
        dialog.setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        dialog.initDialog(onPlaySettingListener, gettingType);
        dialog.show();
    }

    /**
     * Play activity Settings dialog
     *
     * @param context Context
     * @param settingType Set the play type
     * @param showTextList Set the list of options
     * @param selectValue The default Settings string
     * @param onPlaySettingListener Click listener
     */
    public static void onSettingDialogSelectValue(Context context, int settingType, List<String> showTextList,
        String selectValue, OnPlaySettingListener onPlaySettingListener) {
        PlaySettingDialog dialog = new PlaySettingDialog(context).setList(showTextList);
        dialog.setTitle(StringUtil.getStringFromResId(context, R.string.settings));
        dialog.setSelectValue(selectValue)
            .setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        dialog.initDialog(onPlaySettingListener, settingType);
        dialog.show();
    }

    /**
     * Get the volume Settings dialog
     *
     * @param context Context
     * @param onDialogInputValueListener Click listener
     */
    public static void showSetVolumeDialog(Context context,
        final OnDialogInputValueListener onDialogInputValueListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_volume_dialog, null);
        final AlertDialog dialog =
            new AlertDialog.Builder(context).setTitle(StringUtil.getStringFromResId(context, R.string.video_set_volume))
                .setView(view)
                .create();
        dialog.show();
        final EditText volumeValueEt = (EditText) view.findViewById(R.id.set_volume_et);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogInputValueListener != null) {
                    String inputText = "";
                    if (volumeValueEt.getText() != null) {
                        inputText = volumeValueEt.getText().toString();
                    }
                    onDialogInputValueListener.dialogInputListener(inputText);
                    dialog.dismiss();
                }
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Set the bitrate range dialog
     *
     * @param context Context
     */
    public static void showBitrateRangeDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bitrate_range_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(StringUtil.getStringFromResId(context, R.string.video_bitrate_range_setting_title))
            .setView(view)
            .create();
        dialog.show();
        final EditText bitrateMinSetting = (EditText) view.findViewById(R.id.bitrate_min_setting);
        final EditText bitrateMaxSetting = (EditText) view.findViewById(R.id.bitrate_max_setting);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // No bitrate setting, no limit by default
                if (bitrateMinSetting.getText() != null && !TextUtils.isEmpty(bitrateMinSetting.getText().toString())) {
                    PlayControlUtil.setMinBitrate(Integer.parseInt(bitrateMinSetting.getText().toString()));
                }
                if (bitrateMaxSetting.getText() != null && !TextUtils.isEmpty(bitrateMaxSetting.getText().toString())) {
                    PlayControlUtil.setMaxBitrate(Integer.parseInt(bitrateMaxSetting.getText().toString()));
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Set subtitle preset language dialog
     *
     * @param context Context
     */
    public static void showSubtitlePresetLanguageDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.subtitle_preset_language_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.subtitle_preset_language_setting_title))
                .setView(view)
                .create();
        dialog.show();
        final EditText subtitleLanguageSetting = (EditText) view.findViewById(R.id.subtitle_language_setting);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subtitleLanguageSetting.getText() != null) {
                    PlayControlUtil.setSubtitlePresetLanguage(subtitleLanguageSetting.getText().toString());
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * init preloader
     *
     * @param context Context
     * @param onDialogConfirmListener Dialog confirm listener
     */
    public static void initPreloaderDialog(final Context context,
        final OnDialogConfirmListener onDialogConfirmListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.bitrate_range_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(StringUtil.getStringFromResId(context, R.string.video_init_preloader))
            .setView(view)
            .create();
        dialog.show();
        final TextView minTextView = view.findViewById(R.id.bitrate_min_tv);
        final TextView maxTextView = view.findViewById(R.id.bitrate_max_tv);
        final EditText bitrateMinSetting = (EditText) view.findViewById(R.id.bitrate_min_setting);
        final EditText bitrateMaxSetting = (EditText) view.findViewById(R.id.bitrate_max_setting);
        bitrateMinSetting.setInputType(InputType.TYPE_CLASS_TEXT);
        minTextView.setText(R.string.video_preloader_path);
        maxTextView.setText(R.string.video_preloader_total_size);
        bitrateMinSetting.setText("/sdcard/preloader");
        bitrateMaxSetting.setText("209715200");
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Preloader preloader = SandboxApplication.getWisePlayerFactory().createPreloader();
                if (preloader != null && !TextUtils.isEmpty(bitrateMinSetting.getText())
                    && !TextUtils.isEmpty(bitrateMaxSetting.getText())) {
                    PlayControlUtil.setPreloader(preloader);
                    String path = bitrateMinSetting.getText().toString();
                    if (FileUtil.createFile(path)) {
                        int size = Integer.parseInt(bitrateMaxSetting.getText().toString());
                        int result = preloader.initCache(path, size);
                        PlayControlUtil.setInitResult(result);
                    } else {
                        Toast.makeText(context, context.getString(R.string.create_file_fail), Toast.LENGTH_SHORT)
                            .show();
                    }
                }
                onDialogConfirmListener.onConfirm();
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Add single cache
     *
     * @param context Context
     */
    public static void addSingleCacheDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_single_cache_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
            .setTitle(StringUtil.getStringFromResId(context, R.string.video_add_single_cache))
            .setView(view)
            .create();
        dialog.show();
        final EditText cacheUrl = (EditText) view.findViewById(R.id.et_cache_url);
        final EditText cachePlayParam = (EditText) view.findViewById(R.id.et_cache_playparam);
        final EditText cacheAppId = (EditText) view.findViewById(R.id.et_cache_appid);
        final EditText cacheWidth = (EditText) view.findViewById(R.id.et_cache_width);
        final EditText cacheHeight = (EditText) view.findViewById(R.id.et_cache_height);
        final EditText cacheSize = (EditText) view.findViewById(R.id.et_cache_size);
        final EditText cachePriority = (EditText) view.findViewById(R.id.et_cache_priority);
        final EditText cacheVideoFormat = (EditText) view.findViewById(R.id.et_cache_format);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheInfo cacheInfo = new CacheInfo();
                int priority = 0;
                if (!TextUtils.isEmpty(cacheUrl.getText())) {
                    cacheInfo.setUrl(cacheUrl.getText().toString());
                }
                if (!TextUtils.isEmpty(cachePlayParam.getText())) {
                    cacheInfo.setPlayParam(cachePlayParam.getText().toString());
                }
                if (!TextUtils.isEmpty(cacheAppId.getText())) {
                    cacheInfo.setAppId(cacheAppId.getText().toString());
                }
                if (!TextUtils.isEmpty(cacheWidth.getText())) {
                    cacheInfo.setWidth(Integer.parseInt(cacheWidth.getText().toString()));
                }
                if (!TextUtils.isEmpty(cacheHeight.getText())) {
                    cacheInfo.setHeight(Integer.parseInt(cacheHeight.getText().toString()));
                }
                if (!TextUtils.isEmpty(cacheSize.getText())) {
                    cacheInfo.setCacheSize(Integer.parseInt(cacheSize.getText().toString()));
                }
                if (!TextUtils.isEmpty(cachePriority.getText())) {
                    priority = Integer.parseInt(cachePriority.getText().toString());
                }
                if (!TextUtils.isEmpty(cacheVideoFormat.getText())) {
                    cacheInfo.setVideoFormat(Integer.parseInt(cacheVideoFormat.getText().toString()));
                }
                if (!TextUtils.isEmpty(cacheInfo.getUrl())) {
                    if (PlayControlUtil.getPreloader() != null) {
                        PlayControlUtil.getPreloader().addCache(cacheInfo, priority);
                    } else {
                        Toast
                            .makeText(context, context.getString(R.string.video_add_single_cache_fail),
                                Toast.LENGTH_SHORT)
                            .show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.video_preload_url), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * set InitBufferTimeStrategy dialog
     *
     * @param context current context
     */
    public static void setInitBufferTimeStrategy(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_alg_para, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.video_set_alg_para))
                .setView(view)
                .create();
        dialog.show();
        AtomicInteger maxBufferTime = new AtomicInteger();
        StringBuilder stringBuilder = new StringBuilder();
        // direct seeding
        if (PlayControlUtil.getVideoType() == 1) {
            maxBufferTime.set(4000);
        } else {
            maxBufferTime.set(30000);
        }
        List<InitBufferTimeStrategy.DownloadMultipleZone> downloadMultipleZones = new ArrayList<>();
        AtomicReference<InitBufferTimeStrategy.Builder> initBufferTimeStrategy = new AtomicReference<>();
        final EditText algTopBitrate = (EditText) view.findViewById(R.id.alg_bitrate_top_min);
        final EditText algTopDelay = (EditText) view.findViewById(R.id.alg_top_delay);
        final TextView displayValue = (TextView) view.findViewById(R.id.alg_total);
        view.findViewById(R.id.alg_top_add).setOnClickListener(v -> {
            String getTopBitrate = algTopBitrate.getText().toString().trim();
            String getTopDelay = algTopDelay.getText().toString().trim();
            if (TextUtils.isEmpty(getTopBitrate) || TextUtils.isEmpty(getTopDelay)) {
                return;
            }

            int minBitrate = Integer.parseInt(getTopBitrate);
            int topDelay = Integer.parseInt(getTopDelay);

            stringBuilder.append(minBitrate);
            stringBuilder.append(":");
            stringBuilder.append(Integer.MAX_VALUE);
            stringBuilder.append("-");
            stringBuilder.append(topDelay);
            stringBuilder.append(",");
            downloadMultipleZones.add(new InitBufferTimeStrategy.DownloadMultipleZone(minBitrate, Integer.MAX_VALUE, topDelay));
            displayValue.setText(stringBuilder.toString());
        });
        final EditText algDelay = (EditText) view.findViewById(R.id.alg_delay);
        final EditText algBitrateMax = (EditText) view.findViewById(R.id.alg_bitrate_max);
        final EditText algBitrateMin = (EditText) view.findViewById(R.id.alg_bitrate_min);

        view.findViewById(R.id.alg_add).setOnClickListener(v -> {
            String delayValue = algDelay.getText().toString().trim();
            String defaultMax = algBitrateMax.getText().toString().trim();
            String defaultMin = algBitrateMin.getText().toString().trim();
            if (TextUtils.isEmpty(delayValue) || TextUtils.isEmpty(defaultMax) || TextUtils.isEmpty(defaultMin)) {
                return;
            }

            int bitDelayValue = Integer.parseInt(delayValue);
            int birateMax = Integer.parseInt(defaultMax);
            int birateMin = Integer.parseInt(defaultMin);

            stringBuilder.append(birateMin);
            stringBuilder.append(":");
            stringBuilder.append(birateMax);
            stringBuilder.append("-");
            stringBuilder.append(bitDelayValue);
            stringBuilder.append(",");
            try {
                downloadMultipleZones.add(new InitBufferTimeStrategy.DownloadMultipleZone(birateMin, birateMax, bitDelayValue));
            } catch (IllegalArgumentException e) {
                Toast.makeText(context, "IllegalArgumentException", Toast.LENGTH_LONG).show();
            }
            algDelay.setText("");
            algBitrateMax.setText("");
            algBitrateMin.setText("");
            displayValue.setText(stringBuilder.toString());
        });

        final EditText algWindowsValue = (EditText) view.findViewById(R.id.alg_windows_value);
        view.findViewById(R.id.modify_default_value).setOnClickListener(v -> {
            String defaultWindowsValue = algWindowsValue.getText().toString().trim();
            if (TextUtils.isEmpty(defaultWindowsValue)) {
                return;
            }
            int windows = Integer.parseInt(defaultWindowsValue);
            maxBufferTime.set(windows);
        });

        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(v -> {
            try {
                if (downloadMultipleZones.size() == 0) {
                    Toast.makeText(context, context.getString(R.string.video_display_msg), Toast.LENGTH_LONG).show();
                    return;
                }
                initBufferTimeStrategy.set(new InitBufferTimeStrategy.Builder(maxBufferTime.get()));
                for (InitBufferTimeStrategy.DownloadMultipleZone zone : downloadMultipleZones) {
                    initBufferTimeStrategy.get().append(zone.getMin(), zone.getMax(), zone.getBufferTime());
                }
                PlayControlUtil.setInitBufferTimeStrategy(initBufferTimeStrategy.get().build());
            } catch (InitBufferTimeStrategy.DownloadMultipleZoneException | IllegalArgumentException e) {
                Toast.makeText(context, "Data Error", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });
        cancelBt.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Update Server country dialog
     *
     * @param context 上下文
     */
    public static void updateServerCountryDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.update_country_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.update_server_country_title))
                .setView(view)
                .create();
        dialog.show();
        final EditText bitrateMaxSetting = (EditText) view.findViewById(R.id.server_country_setting);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitrateMaxSetting.getText() == null || TextUtils.isEmpty(bitrateMaxSetting.getText().toString())) {
                    Toast.makeText(context, context.getString(R.string.update_server_country_toast_empty), Toast.LENGTH_SHORT)
                            .show();
                } else if (bitrateMaxSetting.getText().length() != 2) {
                    Toast.makeText(context, context.getString(R.string.update_server_country_toast_length), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    WisePlayerFactory.updateServeCountry(bitrateMaxSetting.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Set audio preset language dialog
     *
     * @param context Context
     */
    public static void showPreferAudioLangDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_prefer_audio, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.audio_set_prefer_audio))
                .setView(view)
                .create();
        dialog.show();
        final EditText preferlangSetting = (EditText) view.findViewById(R.id.prefer_audio_setting);
        preferlangSetting.setText(PlayControlUtil.getPreferAudio());
        Button okBt = (Button) view.findViewById(R.id.set_prefer_audio_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_prefer_audio_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferlangSetting.getText() != null) {
                    PlayControlUtil.setPreferAudio(preferlangSetting.getText().toString());
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void showProxyInfoDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_proxy_info, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.socks_proxy_setting))
                .setView(view)
                .create();
        dialog.show();
        final EditText proxyIPSetting = (EditText) view.findViewById(R.id.socks_proxy_ip);
        final EditText proxyPortSetting = (EditText) view.findViewById(R.id.socks_proxy_port);
        final EditText proxyUserSetting = (EditText) view.findViewById(R.id.socks_proxy_user);
        final EditText proxyPasswordSetting = (EditText) view.findViewById(R.id.socks_proxy_password);

        Button okBt = (Button) view.findViewById(R.id.set_proxy_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_proxy_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String proxyHost = proxyIPSetting.getText().toString();
                String proxyPort = proxyPortSetting.getText().toString();
                String proxyUser = proxyUserSetting.getText().toString();
                String proxyPassword = proxyPasswordSetting.getText().toString();
                if (!proxyHost.isEmpty() && !proxyPort.isEmpty() && !proxyUser.isEmpty() && !proxyPassword.isEmpty()) {
                    LogUtil.d(TAG, "showProxyInfoDialog value:" + proxyHost + ":" + proxyPort + ":" + proxyUser);
                    Proxy socksProxy = new Proxy();
                    socksProxy.setType(Proxy.Type.SOCKS);
                    socksProxy.setHost(proxyHost);
                    socksProxy.setPort(proxyPort);
                    socksProxy.setUser(proxyUser);
                    socksProxy.setPasswd(proxyPassword);
                    PlayControlUtil.setProxyInfo(socksProxy);
                } else {
                    PlayControlUtil.setProxyInfo(null);
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
