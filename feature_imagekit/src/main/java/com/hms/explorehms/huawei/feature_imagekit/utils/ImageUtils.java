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

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;

import java.io.FileDescriptor;
import java.io.IOException;

public class ImageUtils {

    private static final String TAG = "IMAGEKIT";


    public static Bitmap uriToBitmap(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();

            return image;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    public static Bitmap cutSmartLayoutImage(Bitmap bitmap) {
        Bitmap cutBitmap;
        if ((float) bitmap.getHeight() / (float) bitmap.getWidth() == 16f / 9f) {
            return bitmap;
        }
        if (bitmap.getWidth() / 9 < bitmap.getHeight() / 16) {
            cutBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 9 * 9, bitmap.getWidth() / 9 * 16);
        } else {
            cutBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getHeight() / 16 * 9, bitmap.getHeight() / 16 * 16);
        }
        return cutBitmap;
    }

    public static Uri getUriToDrawable(@NonNull Context context, @AnyRes int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
    }
}
