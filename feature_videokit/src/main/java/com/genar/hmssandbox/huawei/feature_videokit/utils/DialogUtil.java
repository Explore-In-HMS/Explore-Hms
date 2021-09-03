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
package com.genar.hmssandbox.huawei.feature_videokit.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.genar.hmssandbox.huawei.SandboxApplication;
import com.genar.hmssandbox.huawei.feature_videokit.R;
import com.genar.hmssandbox.huawei.feature_videokit.file_readers.FileReadUtil;
import com.huawei.hms.videokit.player.CacheInfo;
import com.huawei.hms.videokit.player.InitBitrateParam;
import com.huawei.hms.videokit.player.Preloader;
import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;

public class DialogUtil {

    static Preloader preloader = null;
    static int isCreatedInteger = -1;
    static InitBitrateParam initBitrateParam;
    boolean isMuted = false;
    boolean isCycled = true;
    boolean isAudioOnly = false;
    boolean isBuffering = true;
    boolean isAutoSwitch = true;
    boolean isVOD = true;
    boolean isAdaptive = true;
    int checkedItem = 2;
    String playbackSpeed = "1.0x";

    public DialogUtil() {

    }

    /**
     * @param context the context in which you want to show the dialog
     * @param icon the icon inside the dialog if want to change it. The default is the app_icon and this field is nullable
     * @param title Title of the dialog
     * @param radioButtonList the list of buttons that can be chosen
     * @param typeOfDialog the type of the dialog that needs to be shown. Hardcoded cases should be coded
     * @param player the WisePlayer instance where you can control directly the video player stream
     */

    public static Preloader getPreloader() {
        return preloader;
    }

    public static void addSingleCacheDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_single_cache_dialog, null);
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                .setTitle(R.string.add_preload_task)
                .setView(view)
                .create();
        dialog.show();

        //You can change .setText(...) methods to whatever you wish
        final EditText cacheUrl = view.findViewById(R.id.et_cache_url);
        cacheUrl.setText("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        final EditText cachePlayParam = view.findViewById(R.id.et_cache_playparam);
        final EditText cacheAppId = view.findViewById(R.id.et_cache_appid);
        final EditText cacheWidth = view.findViewById(R.id.et_cache_width);
        final EditText cacheHeight = view.findViewById(R.id.et_cache_height);
        final EditText cacheSize = view.findViewById(R.id.et_cache_size);
        cacheSize.setText("209715200");
        final EditText cachePriority = view.findViewById(R.id.et_cache_priority);
        cachePriority.setText("1");
        Button okBt = view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(v -> {
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
            if (!TextUtils.isEmpty(cacheInfo.getUrl())) {
                if (getPreloader() != null) {
                    isCreatedInteger = getPreloader().addCache(cacheInfo, priority);
                    if (isCreatedInteger >= 0)
                        showToast(context, "Preload initiated! Code is " + isCreatedInteger);
                    dialog.dismiss();
                } else {
                    showToast(context, context.getString(R.string.video_add_single_cache_fail));
                    dialog.dismiss();
                }
            } else {
                showToast(context, context.getString(R.string.video_preload_url));
            }

        });
        cancelBt.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Get the volume Settings dialog
     *
     * @param context Context
     * @param player  WisePlayer instance
     */
    public static void showSetVolumeDialog(Context context, WisePlayer player) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_volume_dialog, null);
        final android.app.AlertDialog dialog =
                new android.app.AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.video_set_volume))
                        .setView(view)
                        .create();
        dialog.show();
        final EditText volumeValueEditText = view.findViewById(R.id.set_volume_editText);
        Button okButton = view.findViewById(R.id.set_volume_button_ok);
        Button cancelButton = view.findViewById(R.id.set_volume_button_cancel);
        okButton.setOnClickListener(v -> {
            float tmp = 1f;
            if (player != null) {
                String inputText = "";
                if (volumeValueEditText.getText() != null) {
                    inputText = volumeValueEditText.getText().toString();
                }
                try {
                    tmp = Float.parseFloat(inputText);
                    if (tmp > 1f) {
                        showToast(context, "Number is larger than 1.0, please enter again.");
                    } else {
                        player.setVolume(tmp);
                        showToast(context, "The volume is set to " + tmp);
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(context, "There is another error we do not know :(");
                }
            }
        });
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    public static void showBitrateRangeDialog(Context context, WisePlayer player) {
        View view = LayoutInflater.from(context).inflate(R.layout.bitrate_range_dialog, null);
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                .setTitle(R.string.video_bitrate_range_setting_title)
                .setView(view)
                .create();
        dialog.show();
        final EditText bitrateMinSetting = view.findViewById(R.id.bitrate_min_setting);
        final EditText bitrateMaxSetting = view.findViewById(R.id.bitrate_max_setting);
        Button okBt = view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(v -> {
            if (bitrateMinSetting.getText() != null && bitrateMaxSetting.getText() != null
                    && !bitrateMinSetting.getText().toString().equals("") && !bitrateMaxSetting.getText().toString().equals("")) {
                player.setBitrateRange(Integer.parseInt(bitrateMinSetting.getText().toString()), Integer.parseInt(bitrateMaxSetting.getText().toString()));
                dialog.dismiss();
            } else {
                showToast(context, "Please enter valid values for both");
            }
        });
        cancelBt.setOnClickListener(v -> dialog.dismiss());
    }

    private static void showToast(Context context, String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

    public static void setInitBitrate(Context context, WisePlayer player) {
        View view = LayoutInflater.from(context).inflate(R.layout.init_bitrate, null);
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                .setTitle(R.string.video_init_bitrate_setting)
                .setView(view)
                .create();
        dialog.show();

        final CheckBox checkUpTo = view.findViewById(R.id.check_up_to);

        //You can change this values to hints by uncommenting relevant lines if you want to guide the user.
        final EditText initBitrateEditText = view.findViewById(R.id.initBitrateEditText);
        initBitrateEditText.setText("10000");
        final EditText widthEditText = view.findViewById(R.id.widthEditText);
        widthEditText.setText("1280");
        final EditText heightEditText = view.findViewById(R.id.heightEditText);
        heightEditText.setText("720");

        Button okBt = view.findViewById(R.id.ok_bt);
        Button cancelBt = view.findViewById(R.id.cancel_bt);

        okBt.setOnClickListener(v -> {
            initBitrateParam = new InitBitrateParam();
            initBitrateParam.setBitrate(Integer.parseInt(initBitrateEditText.getText().toString()));
            initBitrateParam.setHeight(Integer.parseInt(heightEditText.getText().toString()));
            initBitrateParam.setWidth(Integer.parseInt(widthEditText.getText().toString()));
            initBitrateParam.setType(checkUpTo.isChecked() ? 0 : 1);
            player.setInitBitrate(initBitrateParam);

            dialog.dismiss();
        });

        cancelBt.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * @param context         = the context in which you want to show the dialog
     * @param icon            = the icon inside the dialog if want to change it. The default is the app_icon and this field is nullable
     * @param title           = Title of the dialog
     * @param radioButtonList = the list of radio buttons that can be chosen inside AlertDialog
     * @param typeOfDialog    = the type of the dialog that needs to be shown. Hardcoded cases should be coded
     * @param player          = the WisePlayer instance where you can control directly the video player stream
     */

    public void defaultAlertDialog(Context context, @Nullable Drawable icon, String title, String[] radioButtonList,
                                   String typeOfDialog, WisePlayer player) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(title);

        int which = 0;

        if (typeOfDialog.equals("mute_unmute") && !isMuted)
            which = 1;
        if (typeOfDialog.equals("repeat_mode") && !isCycled)
            which = 1;
        if (typeOfDialog.equals("playback_mode") && isAudioOnly)
            which = 1;
        if (typeOfDialog.equals("download_control") && !isBuffering)
            which = 1;
        if (typeOfDialog.equals("bandwidth_adaptation") && !isAutoSwitch)
            which = 1;
        if (typeOfDialog.equals("video_type") && !isVOD)
            which = 1;
        if (typeOfDialog.equals("bitrate_type") && !isAdaptive)
            which = 1;

        alertDialog.setSingleChoiceItems(radioButtonList, which, (dialog, which1) -> {
            switch (typeOfDialog) {
                case "mute_unmute":
                    if (which1 == 0) {
                        player.setMute(true);
                        isMuted = true;
                        showToast(context, "The video is muted.");
                    } else {
                        player.setMute(false);
                        isMuted = false;
                        showToast(context, "The video is unmuted.");
                    }
                    break;
                case "download_control":
                    if (which1 == 0) {
                        player.setBufferingStatus(true);
                        isBuffering = true;
                        showToast(context, "The buffer will continue.");
                    } else {
                        player.setBufferingStatus(false);
                        isBuffering = false;
                        showToast(context, "The buffer is stopped.");
                    }
                    break;
                case "playback_mode":
                    if (which1 == 0) {
                        player.setPlayMode(PlayerConstants.PlayMode.PLAY_MODE_NORMAL);
                        isAudioOnly = false;
                        showToast(context, "The audio and the video will play together.");
                    } else {
                        player.setPlayMode(PlayerConstants.PlayMode.PLAY_MODE_AUDIO_ONLY);
                        isAudioOnly = true;
                        showToast(context, "Only the audio will play.");
                    }
                    break;
                case "repeat_mode":
                    if (which1 == 0) {
                        player.setCycleMode(PlayerConstants.CycleMode.MODE_CYCLE);
                        isCycled = true;
                        showToast(context, "Same video will repeat.");
                    } else {
                        player.setCycleMode(PlayerConstants.CycleMode.MODE_NORMAL);
                        isCycled = false;
                        showToast(context, "Normal video mode is set.");
                    }
                    break;
                case "bandwidth_adaptation":
                    if (which1 == 0) {
                        player.setBandwidthSwitchMode(PlayerConstants.BandwidthSwitchMode.AUTO_SWITCH_MODE);
                        isAutoSwitch = true;
                        showToast(context, "Adaptive bitrate streaming will be used.");
                    } else {
                        player.setBandwidthSwitchMode(PlayerConstants.BandwidthSwitchMode.MANUAL_SWITCH_MODE);
                        isAutoSwitch = false;
                        showToast(context, "Current bitrate will be used.");
                    }
                    break;
                case "video_type":
                    if (which1 == 0) {
                        player.setVideoType(0); //VOD
                        isVOD = true;
                        showToast(context, "Type is set to VOD.");
                    } else {
                        player.setVideoType(1); //Live streaming
                        isVOD = false;
                        showToast(context, "Type is set to Live Streaming.");
                    }
                    break;
                case "bitrate_type":
                    if (which1 == 0) {
                        isAdaptive = true;
                        //the default is already being adaptive.
                    } else {
                        DialogUtil.setInitBitrate(context, player);
                        isAdaptive = false;
                    }
                    break;
                default:
                    Log.i("DEFAULTDIALOG", "Default dialog error occurred.");
            }
            dialog.dismiss();
        });

        if (icon != null) {
            alertDialog.setIcon(icon);
        } else {
            alertDialog.setIcon(R.drawable.ic_launcher);
        }
        alertDialog.show();
    }

    public boolean vodReturner() {
        return isVOD;
    }

    public void alertDialogForPlaybackSpeed(Context context, WisePlayer wisePlayer, TextView playSpeedTextView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.choose_pback_speed);
        String[] speedList = {"0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x"};

        alertDialog.setSingleChoiceItems(speedList, checkedItem, (dialog, which) -> {
            playbackSpeed = speedList[which];
            showToast(context, "Playback speed is set to " + playbackSpeed);
            checkedItem = which;
            setPlaySpeed(wisePlayer, playbackSpeed);
            playSpeedTextView.setText(playbackSpeed);
            dialog.dismiss();
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public void addPreloadTaskDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bitrate_range_dialog, null);
        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(context)
                .setTitle(R.string.video_init_preloader)
                .setView(view)
                .create();
        dialog.show();
        final TextView minTextView = view.findViewById(R.id.bitrate_min_tv);
        final TextView maxTextView = view.findViewById(R.id.bitrate_max_tv);
        final EditText bitrateMinSetting = view.findViewById(R.id.bitrate_min_setting);
        final EditText bitrateMaxSetting = view.findViewById(R.id.bitrate_max_setting);
        bitrateMinSetting.setInputType(InputType.TYPE_CLASS_TEXT);
        minTextView.setText(R.string.video_preloader_path);
        maxTextView.setText(R.string.video_preloader_total_size);
        String pathDirectory = context.getFilesDir().getPath() + "/preloader"; //the path is inside the app directory
        bitrateMinSetting.setText(pathDirectory);
        bitrateMaxSetting.setText("209715200"); //no need to add to strings.xml
        Button okBt = view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(v -> {
            preloader = SandboxApplication.getWisePlayerFactory().createPreloader();
            if (preloader != null && !TextUtils.isEmpty(bitrateMinSetting.getText())
                    && !TextUtils.isEmpty(bitrateMaxSetting.getText())) {
                String path = bitrateMinSetting.getText().toString();
                if (FileReadUtil.createFile(path)) {
                    int size = Integer.parseInt(bitrateMaxSetting.getText().toString());
                    int result = preloader.initCache(path, size);
                    if (result == 0) {
                        showToast(context, "Preloader is successfully initialized");
                        addSingleCacheDialog(context);
                    } else //-1 means unsuccessful
                        showToast(context, "Preloader cannot be initialized");
                } else {
                    showToast(context, "Path cannot be created for preloader.");
                }
            } else {
                showToast(context, "Please enter full information.");
            }
            dialog.dismiss();
        });
        cancelBt.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Set the speed
     *
     * @param speedValue The speed of the string
     */
    public void setPlaySpeed(WisePlayer wisePlayer, String speedValue) {
        switch (speedValue) {
            case "1.25x":
                wisePlayer.setPlaySpeed(1.25f);
                break;
            case "1.5x":
                wisePlayer.setPlaySpeed(1.5f);
                break;
            case "1.75x":
                wisePlayer.setPlaySpeed(1.75f);
                break;
            case "2.0x":
                wisePlayer.setPlaySpeed(2.0f);
                break;
            case "0.5x":
                wisePlayer.setPlaySpeed(0.5f);
                break;
            case "0.75x":
                wisePlayer.setPlaySpeed(0.75f);
                break;
            default:
                wisePlayer.setPlaySpeed(1.0f);
                break;
        }
    }
}





