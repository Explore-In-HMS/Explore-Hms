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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.audioTranscription.audioOperations;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static boolean deleteQuietly(final File file) {
        if (file == null) {
            return false;
        }
        Log.i(TAG, "deleteQuietly : deleteQuietly : " + file.getPath());
        try {
            if (file.isDirectory()) {
                cleanDirectory(file);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteQuietly : Exception : " + e.getMessage());
        }

        try {
            return file.delete();
        } catch (final Exception ignored) {
            return false;
        }
    }

    public static void cleanDirectory(final File directory) throws IOException {

        Log.i(TAG, "cleanDirectory : deleteQuietly : " + directory.getPath());

        final File[] files = verifiedListFiles(directory);

        IOException exception = null;
        for (final File file : files) {
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                Log.e(TAG, "cleanDirectory : Exception : " + ioe.getMessage(), ioe);
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

    private static File[] verifiedListFiles(final File directory) throws IOException {
        Log.i(TAG, "verifiedListFiles : directory : " + directory.getPath());
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new IOException("Failed to list contents of " + directory);
        }
        return files;
    }

    public static void forceDelete(final File file) throws IOException {
        Log.i(TAG, "forceDelete : file : " + file.getPath());
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            final boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                final String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void deleteDirectory(final File directory) throws IOException {
        Log.i(TAG, "deleteDirectory : directory : " + directory.getPath());
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);

        if (!directory.delete()) {
            final String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }


}
