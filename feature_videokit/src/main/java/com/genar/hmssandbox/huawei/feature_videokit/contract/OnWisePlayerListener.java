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

package com.genar.hmssandbox.huawei.feature_videokit.contract;

import android.widget.SeekBar.OnSeekBarChangeListener;

import com.huawei.hms.videokit.player.WisePlayer;

/**
 * Player SDK support to listener events
 */
public interface OnWisePlayerListener extends WisePlayer.ErrorListener, WisePlayer.ReadyListener,
    WisePlayer.EventListener, WisePlayer.PlayEndListener, WisePlayer.ResolutionUpdatedListener,
    WisePlayer.SeekEndListener, WisePlayer.LoadingListener, OnSeekBarChangeListener,
    WisePlayer.SubtitleUpdateListener {
}
