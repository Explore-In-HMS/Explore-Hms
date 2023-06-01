
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

package com.hms.explorehms.huawei.ui.mediaeditor.texts.fragment;

import android.content.Context;
import android.view.View;

import com.hms.explorehms.huawei.ui.common.BaseFragment;
import com.huawei.hms.videoeditor.sdk.bean.HVEWordStyle;
import com.hms.explorehms.huawei.ui.mediaeditor.texts.viewmodel.TextEditViewModel;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class KeyBoardFragment extends BaseFragment {
    private TextEditViewModel textEditViewModel;

    private Context fragmentContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentContext = getContext();
        if (fragmentContext != null && (fragmentContext instanceof ViewModelStoreOwner)) {
            textEditViewModel =
                new ViewModelProvider((ViewModelStoreOwner) fragmentContext).get(TextEditViewModel.class);
            HVEWordStyle wordStyle = textEditViewModel.getLastWordStyle();
            if (wordStyle == null) {
                textEditViewModel.setDefWordStyle(0);
            }
        }
    }

    @Override
    protected void initViewModelObserve() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.panel_keyboard_edit;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initObject() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setViewLayoutEvent() {
        return 0;
    }
}
