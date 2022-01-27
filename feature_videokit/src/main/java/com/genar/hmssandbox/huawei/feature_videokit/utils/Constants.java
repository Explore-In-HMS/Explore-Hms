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

/**
 * Constant class
 */
public class Constants {
    /**
     * The current data
     */
    public static final String VIDEO_PLAY_DATA = "video_play_data";

    /**
     * The delay time
     */
    public static final long DELAY_MILLIS_500 = 500;

    public static final long DELAY_MILLIS_3000 = 3000;

    public static final long DELAY_MILLIS_1000 = 1000;

    /**
     * Under the vertical screen SurfaceView height
     */
    public static final float HEIGHT_DP = 300;

    /**
     * present Current Position
     */
    public static final int PLAYING_WHAT = 1;

    /**
     * After the completion of the play status update button
     */
    public static final int UPDATE_PLAY_STATE = 4;

    /**
     * Receive the onError news out of the current page
     */
    public static final int PLAY_ERROR_FINISH = 5;

    /**
     * Update switch bitrate success
     */
    public static final int UPDATE_SWITCH_BITRATE_SUCCESS = 6;

    /**
     * Switch bitrate
     */
    public static final int PLAYER_SWITCH_STOP_REQUEST_STREAM = 7;

    /**
     * Setting
     */
    public static final int MSG_SETTING = 8;

    /**
     * Switch speed
     */
    public static final int PLAYER_SWITCH_PLAY_SPEED = 9;

    /**
     * Switch bitrate
     */
    public static final int PLAYER_SWITCH_BITRATE = 10;

    /**
     * Smooth/Designated cutting rate
     */
    public static final int PLAYER_SWITCH_AUTO_DESIGNATED = 11;

    /**
     * Set the bandwidth adaptive switch
     */
    public static final int PLAYER_SWITCH_BANDWIDTH_MODE = 12;

    /**
     * Switch the audio and video
     */
    public static final int PLAYER_SWITCH_PLAY_MODE = 13;

    /**
     * Set the looping
     */
    public static final int PLAYER_SWITCH_LOOP_PLAY_MODE = 14;

    /**
     * Set the mute
     */
    public static final int PLAYER_SWITCH_VIDEO_MUTE_MODE = 15;

    /**
     * Switch subtitle
     */
    public static final int PLAYER_SWITCH_SUBTITLE = 16;

    /**
     * Get Audio track
     */
    public static final int PLAYER_GET_AUDIO_TRACKS = 17;

    /**
     * Switch Audio track
     */
    public static final int PLAYER_SWITCH_AUDIO_TRACK = 18;

    /**
     * Set keep wake up
     */
    public static final int PLAYER_SET_WAKE_MODE = 19;

    /**
     * Set the video type
     */
    public static final int PLAYER_SWITCH_VIDEO_MODE = 0;

    /**
     * Set the play View
     */
    public static final int PLAYER_SWITCH_VIDEO_VIEW = 1;

    /**
     * Set the mute
     */
    public static final int PLAYER_SWITCH_VIDEO_MUTE = 3;

    /**
     * Set the play mode
     */
    public static final int PLAYER_SWITCH_VIDEO_PLAY = 4;

    /**
     * Set the bandwidth adaptive
     */
    public static final int PLAYER_SWITCH_BANDWIDTH = 5;

    /**
     * Set the bitrate
     */
    public static final int PLAYER_SWITCH_INIT_BANDWIDTH = 6;

    /**
     * Set off the logo
     */
    public static final int PLAYER_SWITCH_CLOSE_LOGO = 7;

    /**
     * Close the logo under the influence of all the sources or sources
     */
    public static final int PLAYER_SWITCH_CLOSE_LOGO_EFFECT = 8;

    /**
     * Set Prefer Audio Track
     */
    public static final int PLAYER_SET_PREFER_LANG = 10;

    /**
     * Home page setting menu
     */
    public static final int SET_HOME_SETTING = 15;

    /**
     * The first options dialog
     */
    public static final int DIALOG_INDEX_ONE = 0;

    /**
     * The second options dialog
     */
    public static final int DIALOG_INDEX_TWO = 1;

    /**
     * Set play type live
     */
    public static final int VIDEO_TYPE_LIVE = 1;

    /**
     * Set play type demand
     */
    public static final int VIDEO_TYPE_ON_DEMAND = 0;

    public static final int DISPLAY_HEIGHT_SMOOTH = 270;

    public static final int DISPLAY_HEIGHT_SD = 480;

    public static final int DISPLAY_HEIGHT_HD = 720;

    public static final int DISPLAY_HEIGHT_BLUE_RAY = 1080;

    /**
     * BITRATE WITHIN RANGE
     */
    public static final int BITRATE_WITHIN_RANGE = 100;

    /**
     * Set download thread
     */
    public static final int DOWNLOAD_LINK_NUM = 11;

    /**
     * Set keep wake up
     */
    public static final int SET_WAKE_MODE = 12;

    /**
     * Set subtitle render mode
     */
    public static final int SET_SUBTITLE_RENDER_MODE = 13;

    /**
     * Log size
     */
    public static final int LOG_FILE_SIZE = 1024;

    /**
     * Default bookmark value
     */
    public static final int DEFAULT_BOOKMARK_VALUE = -1;

    /**
     * Log number
     */
    public static final int LOG_FILE_NUM = 20;

    /**
     * Debug
     */
    public static final int LEVEL_DEBUG = 0;

    /**
     * INFO
     */
    public static final int LEVEL_INFO = 1;

    /**
     * WARN
     */
    public static final int LEVEL_WARN = 2;

    /**
     * ERROR
     */
    public static final int LEVEL_ERROR = 3;

    /**
     * CLOSE
     */
    public static final int LEVEL_CLOSE = 10;

    /**
     * Url type
     */
    public static class UrlType {
        /**
         * A single play address
         */
        public static final int URL = 0;

        /**
         * Multiple play address
         */
        public static final int URL_MULTIPLE = 1;

        /**
         * Huawei managed video address
         */
        public static final int URL_JSON = 2;

        /**
         * Huawei managed video address(Set video format)
         */
        public static final int URL_JSON_FORMAT = 3;
    }
}