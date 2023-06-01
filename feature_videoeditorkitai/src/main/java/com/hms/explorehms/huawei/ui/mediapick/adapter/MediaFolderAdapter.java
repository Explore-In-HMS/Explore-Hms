
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

package com.hms.explorehms.huawei.ui.mediapick.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hms.explorehms.huawei.ui.common.adapter.comment.RCommandAdapter;
import com.hms.explorehms.huawei.ui.common.adapter.comment.RViewHolder;
import com.hms.explorehms.huawei.ui.common.view.image.RoundImage;
import com.hms.explorehms.huawei.ui.mediapick.bean.MediaFolder;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;

public class MediaFolderAdapter extends RCommandAdapter<MediaFolder> {

    public MediaFolderAdapter(Context context, List<MediaFolder> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    protected void convert(RViewHolder holder, MediaFolder mediaFolder, int dataPosition, int position) {
        RoundImage roundImage = holder.getView(R.id.iv_media);
        View bottomLine = holder.getView(R.id.bottom_line);
        bottomLine.setVisibility(dataPosition == (mList.size() - 1) ? View.INVISIBLE : View.VISIBLE);
        Glide.with(mContext).load(mediaFolder.getFirstMediaPath()).into(roundImage);
        holder.setText(R.id.tv_folder_name, String.valueOf(mediaFolder.getDirName()));
    }
}
