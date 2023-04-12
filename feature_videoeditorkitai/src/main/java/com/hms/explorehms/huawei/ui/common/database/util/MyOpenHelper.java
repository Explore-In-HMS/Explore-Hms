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

package com.hms.explorehms.huawei.ui.common.database.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;
import com.hms.explorehms.huawei.ui.common.database.bean.CloudMaterialsBeanDao;
import com.hms.explorehms.huawei.ui.common.database.dao.DaoMaster;

import org.greenrobot.greendao.database.Database;

@KeepOriginal
public class MyOpenHelper extends DaoMaster.OpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        DBMigrationHelper.getInstance().migrate(db, CloudMaterialsBeanDao.class);
    }
}
