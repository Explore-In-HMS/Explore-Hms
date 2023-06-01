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
package com.hms.explorehms.scankit.action;

import android.content.Intent;
import android.net.Uri;

import com.huawei.hms.ml.scan.HmsScan;

public class EmailAction {
    public static final String TAG = "EmailAction";

    private EmailAction() {
        throw new IllegalStateException("Utility class");
    }


    public static Intent getEmailInfo(HmsScan.EmailContent emailContent) {
        Uri uri  = Uri.parse("mailto:" + emailContent.addressInfo);
        String[] tos = {emailContent.addressInfo};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_SUBJECT,  emailContent.subjectInfo);
        intent.putExtra(Intent.EXTRA_TEXT,  emailContent.bodyInfo);
        return intent;
    }
}
