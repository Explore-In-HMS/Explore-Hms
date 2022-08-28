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
package com.hms.explorehms.huawei.feature_audiokit;

import java.util.List;

public class LocalAudioItem {
    String nameOfPlaylist;
    List<Integer> indexList;

    public LocalAudioItem(String nameOfPlaylist, List<Integer> indexList) {
        this.nameOfPlaylist = nameOfPlaylist;
        this.indexList = indexList;
    }

    public String getNameOfPlaylist() {
        return nameOfPlaylist;
    }

    public void setNameOfPlaylist(String nameOfPlaylist) {
        this.nameOfPlaylist = nameOfPlaylist;
    }

    public List<Integer> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Integer> indexList) {
        this.indexList = indexList;
    }
}
