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

package com.hms.explorehms.huawei.ui.common.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

public class Utils {
    public static SpannableStringBuilder setNumColor(String string, int color) {
        SpannableStringBuilder style = new SpannableStringBuilder(string);
        for (int i = 0; i < string.length(); i++) {
            char a = string.charAt(i);
            if (a >= '0' && a <= '9') {
                style.setSpan(new ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }

    public static void setTextAttrs(SpannableString spannableString, String number, int resId) {
        if (spannableString == null || StringUtil.isEmpty(number)) {
            return;
        }
        int color = ResUtils.getColor(resId);
        int start = spannableString.toString().indexOf(number);
        int end = start + number.length();
        setStringSpan(spannableString, new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void setStringSpan(SpannableString spannableString, Object what, int start, int end, int flag) {
        if (null == spannableString || null == what) {
            return;
        }

        if (start < 0 || end < 0 || end < start || start > spannableString.length() || end > spannableString.length()) {
            return;
        }

        spannableString.setSpan(what, start, end, flag);
    }
}
