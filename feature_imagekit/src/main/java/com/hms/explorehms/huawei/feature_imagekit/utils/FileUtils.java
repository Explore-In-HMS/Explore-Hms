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

package com.hms.explorehms.huawei.feature_imagekit.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static final String TAG = "IMAGEKIT";

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean createResourceDirs(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdir();
        }
        return false;
    }

    public static boolean copyAssetsFilesToDirs(Context context, String foldersName, String path) {
        try {
            String[] files = context.getAssets().list(foldersName);
            for (String file : files) {

                if (!copyAssetsFileToDirs(context, foldersName + File.separator + file, path + File.separator + file)) {
                    Log.e(TAG, "Copy resource file fail, please check permission");
                    return false;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        }
        return true;
    }

    public static boolean copyAssetsFileToDirs(Context context, String fileName, String path) {
        File file = new File(path);
        try (InputStream inputStream = context.getAssets().open(fileName);
             FileOutputStream outputStream = new FileOutputStream(file)) {

            byte[] temp = new byte[4096];
            int n;
            while (-1 != (n = inputStream.read(temp))) {
                outputStream.write(temp, 0, n);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        } finally {
            Log.i(TAG, "copyAssetsFileToDirs: ");
        }
        return true;
    }


    public static boolean copyAssetsFileToDirsRecursive(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyAssetsFileToDirsRecursive(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                try (InputStream is = context.getAssets().open(oldPath);
                     FileOutputStream fos = new FileOutputStream(new File(newPath))) {

                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "copyAssetsFileToDirsRecursive: " + e.getMessage());
                }

            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

}
