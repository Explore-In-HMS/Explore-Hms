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

package com.hms.explorehms.locationkit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

import com.hms.explorehms.R;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void startActivity(Activity activity, Class c) {
        activity.startActivity(new Intent(activity, c));
    }

    /*
     * startActivityWithFlags
     *
     * @param activity
     * @param c        : class
     * @param flags
     */
    public static void startActivity(Activity activity, Class c, List<Integer> flags) {
        Intent intent = new Intent();
        intent.setClass(activity.getApplicationContext(), c);
        for (Integer flag : flags) {
            intent.setFlags(flag);
        }
        activity.startActivity(intent);
    }


    public static void openWebPage(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    /*
     * @param context
     * @param dialogTitle
     * @param dialogMessage
     * @param iconId        : such as R.drawable.icon_settings
     * @param cancelMessage
     * @param positiveText
     * @param negativeText
     */
    public static void showDialogPermissionWarning(Context context, String dialogTitle,
                                                   String dialogMessage, int iconId, String cancelMessage,
                                                   String positiveText, String negativeText) {
        showAlertDialog(context, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            // proceed with logic by disabling the related features or quit the app.
                            showToastMessage(context, cancelMessage);
                            break;
                        default:
                            Log.i(TAG, String.valueOf(R.string.default_text));
                    }
                });
    }

    public static boolean isDeveloperOptionOpen(Context context) {
        boolean isOpen = false;
        int result = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        Log.d(TAG, "isDeveloperOptionOpen : getContentResolver.DEVELOPMENT_SETTINGS_ENABLED : " + result);
        if (result == 1) {
            isOpen = true;
        }
        return isOpen;
    }

    /*
     * @param context
     * @param dialogTitle
     * @param dialogMessage
     * @param iconId        : such as R.drawable.icon_settings
     * @param cancelMessage
     * @param positiveText
     * @param negativeText
     */
    public static void showDialogDeveloperOptionsWarning(Context context, String dialogTitle,
                                                         String dialogMessage, int iconId, String cancelMessage,
                                                         String positiveText, String negativeText) {
        showAlertDialog(context, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            // proceed with logic by disabling the related features or quit the app.
                            showToastMessage(context, cancelMessage);
                            break;
                        default:
                            Log.i(TAG, String.valueOf(R.string.default_text));
                    }
                });
    }


    /*
     * @param context
     * @param dialogTitle
     * @param dialogMessage
     * @param iconId        : such as R.drawable.icon_settings
     * @param cancelMessage
     * @param positiveText
     * @param negativeText
     */
    public static void showDialogGpsWarning(Context context, String dialogTitle,
                                            String dialogMessage, int iconId, String cancelMessage,
                                            String positiveText, String negativeText) {
        showAlertDialog(context, dialogTitle, dialogMessage, iconId, positiveText, negativeText,
                (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            showToastMessage(context, cancelMessage);
                            break;
                        default:
                            Log.i(TAG, String.valueOf(R.string.default_text));
                    }
                });
    }


    public static void showAlertDialog(Context context, String title, String message, int iconId,
                                       String positiveText, String negativeText,
                                       DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(iconId)
                .setPositiveButton(positiveText, okListener)
                .setNeutralButton(negativeText, okListener)
                .create()
                .show();
    }



    /**
     * @return systemTime : yyyy-MM-dd HH:mm:ss
     */
    public static String getTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return DATETIME.format(new Date());
    }


    public static String getJsonFromFile(Context context, String fileName, boolean isBr) {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try {
            AssetManager assetManager = context.getAssets();
            bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName), StandardCharsets.UTF_8));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
                if (isBr) {
                    stringBuilder.append("\n");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "getJsonFromFile : Exception : " + e.getMessage(), e);
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "getJsonFromFile : finally Exception : " + e.getMessage(), e);
            }
        }
        return stringBuilder.toString();
    }

    public static boolean getBinaryFlag(int sourceType) {
        boolean flag = false;
        if (sourceType <= 0) {
            return false;
        }
        String binary = Integer.toBinaryString(sourceType);
        if (binary.length() >= 4) {
            String isbinary = binary.substring(binary.length() - 4).charAt(0) + "";
            flag = isbinary.equals("1");
        }
        return flag;
    }

    /*
     * @param activity
     * @param permissionRequestCode
     * @return : boolean
     */
    public static boolean checkActivityRecognitionPermission(Activity activity, int permissionRequestCode) {
        boolean isPermitGrant = false;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (activity.checkSelfPermission("com.huawei.hms.permission.ACTIVITY_RECOGNITION") != PackageManager.PERMISSION_DENIED) {
                String[] permissions = {"com.huawei.hms.permission.ACTIVITY_RECOGNITION"};
                activity.requestPermissions(permissions, permissionRequestCode);
            } else {
                Log.i(TAG, "getGrantPermission sdk <= 28 P and permission has granted.");
                isPermitGrant = true;
            }
        } else {
            if (activity.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                String[] permission = new String[]{Manifest.permission.ACTIVITY_RECOGNITION};
                activity.requestPermissions(permission, permissionRequestCode);
            } else {
                Log.i(TAG, "getGrantPermission sdk > 28 P and permission has granted.");
                isPermitGrant = true;
            }
        }
        return isPermitGrant;
    }


    static int permissionRequestCode1 = 1;
    static String[] permissionRequest1 = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    static int permissionRequestCode2 = 2;
    static String[] permissionRequest2 = {android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            "android.permission.ACCESS_BACKGROUND_LOCATION"};

    /*
     * @param activity
     * @param permissionRequestList
     * @param permissionRequestCode
     * @return : boolean
     */
    public static boolean isGrantLocationPermissions(Activity activity, String[] permissionRequestList, int permissionRequestCode) {
        boolean isPermitGrant = false;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "getGrantPermission sdk < 28 Q");
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(permissionRequestList, permissionRequestCode);
            } else {
                Log.i(TAG, "getGrantPermission sdk < 28 Q and permission has granted.");
            }
            isPermitGrant = true;
        } else {
            Log.i(TAG, "getGrantPermission sdk > 28 Q");
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(permissionRequestList, permissionRequestCode);
            } else {
                Log.i(TAG, "getGrantPermission sdk > 28 Q and permission has granted.");
                isPermitGrant = true;
            }
        }
        return isPermitGrant;
    }

}
