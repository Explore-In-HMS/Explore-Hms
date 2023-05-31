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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huawei.hms.videoeditor.sdk.util.KeepOriginal;
import com.huawei.hms.videoeditor.sdk.util.SmartLog;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 输入输出对象关闭工具类<BR>
 *
 * @author s00197405
 * @version [V1.0.0.300, 2018/1/5]
 * @since V1.0.0.300
 */
@KeepOriginal
public final class CloseUtils {
    /**
     * 日志标记
     */
    private static final String TAG = "CloseUtils";

    /**
     * 工具类私有化构造方法
     */
    private CloseUtils() {

    }

    /**
     * 输入流关闭方对象
     *
     * @param cursor 待关闭对对象
     */
    public static void close(final Cursor cursor) {
        if (null == cursor) {
            SmartLog.e(TAG, "cursor is empty");
            return;
        }

        try {
            // 关闭对象
            cursor.close();
        } catch (Exception e) {
            SmartLog.e(TAG, "close cursor has exception:", e);
        }
    }

    /**
     * 数据库关闭方对象
     *
     * @param db 待关闭对对象
     */
    public static void close(final SQLiteDatabase db) {
        if (null == db) {
            return;
        }

        try {
            // 关闭对象
            db.close();
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        }
    }

    /**
     * 输入流关闭方对象
     *
     * @param closed 待关闭对对象
     */
    public static void close(final Closeable closed) {
        if (null == closed) {
            return;
        }

        try {
            // 关闭对象
            closed.close();
        } catch (IOException e) {
            SmartLog.e(TAG, e.getMessage());
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        }
    }

    /**
     * http连接关闭方法
     *
     * @param conn HttpURLConnection连接
     */
    public static void close(final HttpURLConnection conn) {
        if (null == conn) {
            return;
        }

        try {
            // 关闭http连接
            conn.disconnect();
        } catch (Exception e) {
            SmartLog.e(TAG, e.getMessage());
        }
    }
}
