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

package com.hms.explorehms.huawei.ui.mediaeditor.trackview.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;

import com.huawei.hms.videoeditor.sdk.asset.HVEAsset;
import com.huawei.hms.videoeditor.sdk.effect.HVEEffect;

public class MainRecyclerData {
    private WholeListData wholeListData = new WholeListData();

    public List<MainListItem> mainList = new CopyOnWriteArrayList<>();

    private int viewState = MainViewState.EDIT_VIDEO_STATE;

    public MainRecyclerData(Context context) {
    }

    public void setViewState(int state) {
        viewState = state;
    }

    public int getViewState() {
        return viewState;
    }

    public List<MainListItem> getMainList() {
        mainList.clear();
        switch (viewState) {
            case MainViewState.EDIT_VIDEO_STATE:
                mainList.add(new MainListItem(MainViewState.NORMAL_STATE));
                break;
            case MainViewState.EDIT_SPECIAL_STATE:
                mainList.add(new MainListItem(MainViewState.NORMAL_STATE));
                for (NormalTrackItem item : wholeListData.specialTrackItemList) {
                    mainList.add(new MainListItem(MainViewState.EDIT_SPECIAL_STATE, item));
                }
                break;
            case MainViewState.EDIT_FILTER_STATE:
                mainList.add(new MainListItem(MainViewState.NORMAL_STATE));
                for (NormalTrackItem item : wholeListData.filterTrackItemList) {
                    mainList.add(new MainListItem(MainViewState.EDIT_FILTER_STATE, item));
                }
                break;

            case MainViewState.EDIT_AUDIO_STATE:
                mainList.add(new MainListItem(MainViewState.EDIT_AUDIO_STATE));
                for (NormalTrackItem item : wholeListData.audioTrackItemList) {
                    mainList.add(new MainListItem(MainViewState.EDIT_AUDIO_STATE, item));
                }
                break;
            case MainViewState.EDIT_STICKER_STATE:
            case MainViewState.EDIT_TEXT_STATE:
                mainList.add(new MainListItem(MainViewState.NORMAL_STATE));
                for (NormalTrackItem item : wholeListData.textTrackItemList) {
                    mainList.add(new MainListItem(MainViewState.EDIT_TEXT_STATE, item));
                }
                break;

            case MainViewState.EDIT_PIP_STATE:
                mainList.add(new MainListItem(MainViewState.NORMAL_STATE));
                for (NormalTrackItem item : wholeListData.pipTrackItemList) {
                    mainList.add(new MainListItem(MainViewState.EDIT_PIP_STATE, item));
                }
                break;
        }
        return mainList;
    }

    public WholeListData getWholeListData() {
        return wholeListData;
    }

    public static class WholeListData {
        public List<NormalTrackItem> audioTrackItemList = new CopyOnWriteArrayList<>();

        public List<NormalTrackItem> textTrackItemList = new CopyOnWriteArrayList<>();

        public List<NormalTrackItem> specialTrackItemList = new CopyOnWriteArrayList<>();

        public List<NormalTrackItem> filterTrackItemList = new CopyOnWriteArrayList<>();

        public List<NormalTrackItem> pipTrackItemList = new CopyOnWriteArrayList<>();

        public int getDataCount() {
            int count = 0;
            for (NormalTrackItem item : audioTrackItemList) {
                if (item.hveAssetList.size() > 0) {
                    count++;
                    break;
                }
            }
            for (NormalTrackItem item : textTrackItemList) {
                boolean isGet = false;
                for (HVEAsset asset : item.hveAssetList) {
                    if (asset.getType() == HVEAsset.HVEAssetType.WORD) {
                        isGet = true;
                        break;
                    }
                }
                if (isGet) {
                    count++;
                    break;
                }
            }
            for (NormalTrackItem item : textTrackItemList) {
                boolean isGet = false;
                for (HVEAsset asset : item.hveAssetList) {
                    if (asset.getType() == HVEAsset.HVEAssetType.STICKER) {
                        isGet = true;
                        break;
                    }
                }
                if (isGet) {
                    count++;
                    break;
                }
            }
            for (NormalTrackItem item : pipTrackItemList) {
                if (item.hveAssetList.size() > 0) {
                    count++;
                    break;
                }
            }
            for (NormalTrackItem item : specialTrackItemList) {
                if (item.effects.size() > 0) {
                    count++;
                    break;
                }
            }
            for (NormalTrackItem item : filterTrackItemList) {
                if (item.effects.size() > 0) {
                    count++;
                    break;
                }
            }
            return count;
        }

        public List<NormalTrackItem> getNormalList(int itemId) {
            switch (itemId) {
                case MainViewState.EDIT_AUDIO_STATE:
                    return audioTrackItemList;
                case MainViewState.EDIT_TEXT_STATE:
                    return textTrackItemList;
                case MainViewState.EDIT_SPECIAL_STATE:
                    return specialTrackItemList;
                case MainViewState.EDIT_FILTER_STATE:
                    return filterTrackItemList;
                case MainViewState.EDIT_PIP_STATE:
                    return pipTrackItemList;
                default:
                    return new ArrayList<>();
            }
        }
    }

    public static class MainListItem {
        public NormalTrackItem trackItem;

        public MainListItem(int id) {
        }

        public MainListItem(int id, NormalTrackItem item) {
            this.trackItem = item;
        }
    }

    public static class NormalTrackItem extends Object {
        public List<HVEAsset> hveAssetList = new ArrayList<>();

        public List<HVEEffect> effects = new ArrayList<>();

        public int laneIndex;

        public NormalTrackItem(int laneIndex, List<HVEAsset> assets) {
            this.laneIndex = laneIndex;
            this.hveAssetList = assets;
        }

        public NormalTrackItem(int laneIndex, List<HVEEffect> effects, int effectType) {
            this.laneIndex = laneIndex;
            this.effects = effects;
        }
    }
}
