
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

package com.hms.explorehms.huawei.ui.mediaeditor.menu;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditItemViewModel extends ViewModel {

    private MutableLiveData<EditMenuBean> itemsFirstSelected = new MutableLiveData<>();

    private MutableLiveData<EditMenuBean> itemsSecondSelected = new MutableLiveData<>();

    private MutableLiveData<Boolean> isShowSecondItem = new MutableLiveData<>();

    public EditItemViewModel() {

    }

    public MutableLiveData<EditMenuBean> getItemsFirstSelected() {
        return itemsFirstSelected;
    }

    public MutableLiveData<EditMenuBean> getItemsSecondSelected() {
        return itemsSecondSelected;
    }

    public void setIsShowSecondItem(boolean isShowSecondItem) {
        this.isShowSecondItem.postValue(isShowSecondItem);
    }

    public MutableLiveData<Boolean> getIsShowSecondItem() {
        return isShowSecondItem;
    }
}
