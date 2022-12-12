/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hms.explorehms.huawei.feature_clouddb.utils;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.Navigation;

import com.hms.explorehms.huawei.feature_clouddb.model.BookComment;

public class FragmentOperation {

    private FragmentOperation(){}

    public static void changeFragment(View fragmentView, int actionId){

        Navigation.findNavController(fragmentView).navigate(actionId);

    }

    public static void changeFragment(View fragmentView, int actionId, BookComment selectedComment){
        Bundle test = new Bundle();
        test.putSerializable("selectedComment",selectedComment);
        Navigation.findNavController(fragmentView).navigate(actionId,test);
    }
}
