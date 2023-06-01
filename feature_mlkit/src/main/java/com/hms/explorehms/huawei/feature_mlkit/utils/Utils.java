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
package com.hms.explorehms.huawei.feature_mlkit.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.mlsdk.common.MLApplication;

import java.security.SecureRandom;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {


    private static  String API_KEY = "";

    // Setting ApiKey For SDK Data Security and use Remote Analyzer:
    //
    // When using ML Kit services on the cloud, you need to set the api_key and ensure that it is secure.
    //
    // Do not hardcode the api_key in the code or store it in the app configuration file.
    // You are advised to store the api_key on the cloud and obtain it when running the app.
    //
    // You can use the following API to initialize the api_key when the app is started.
    // The api_key does not need to be set again once being initialized.
    public static void setApiKeyForRemoteMLApplication(Context context){
        API_KEY= AGConnectServicesConfig.fromContext(context).getString("client/api_key");
        MLApplication.getInstance().setApiKey(API_KEY);
    }


    public static void showToastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    public static boolean haveNetworkConnection(Context context){
        boolean haveConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE );
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

        for( NetworkInfo ni : networkInfos ){

            if( ni != null && ni.isConnected() ){
                haveConnected = true;
            }
        }

        return haveConnected ;
    }

    @SuppressLint("MissingPermission")
    private boolean isConnectedWifi(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    /**
     *
     * @param millisecond : long type vibrate time
     */
    @SuppressLint("MissingPermission")
    public static void createVibration(Context context, long millisecond) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(millisecond);
    }

    // ------------------------------------------------------------------------------------------ //


    public static Intent createIntentForPickImageFromStorage( ){
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,     "image/*");
        return pickIntent;
    }

    public static void startActivity(Activity activity, Class<?> c){
        activity.startActivity(new Intent(activity,c));
    }

    /**
     * @param a    : activity
     * @param c    : class
     * @param desc : description
     * @param link : url
     */
    public static void startActivityWebView(Activity a, Class<?> c, String desc, String link) {
        Intent i = new Intent(a, c);
        i.putExtra("desc", desc);
        i.putExtra("link", link);
        a.startActivity(i);
    }

    public static void openWebPage(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    public static void makeFullScreenActivity(Activity activityRef) {
        activityRef.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_IMMERSIVE
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static void hideNavigationBarActivity(Activity activityRef) {
        View view = activityRef.getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        activityRef.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    public static void setItemColorUnavailable(ImageView v) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setImageAlpha(128); // 128 = 0.5
    }

    public static int getRandomNumber(int max, int min) {
        return new SecureRandom().nextInt((max - min) + 1) + min;
    }

    public static int getColorOptionsRandomly() {
        int whichColor = getRandomNumber(13,1);
        int color ;
        switch (whichColor) {
            case 1:
                color = Color.parseColor("#2ecc71");
                break;
            case 2:
                color = Color.parseColor("#f1c40f");
                break;
            case 3:
                color = Color.parseColor("#D81B60");
                break;
            case 4:
                color = Color.parseColor("#1DE9B6");
                break;
            case 5:
                color = Color.parseColor("#FF1744");
                break;
            case 6:
                color = Color.parseColor("#e74c3c");
                break;
            case 7:
                color = Color.parseColor("#FFEA00");
                break;
            case 8:
                color = Color.parseColor("#D500F9");
                break;
            case 9:
                color = Color.parseColor("#536DFE");
                break;
            case 10:
                color = Color.parseColor("#00B0FF");
                break;
            case 11:
                color = Color.parseColor("#76FF03");
                break;
            case 12:
                color = Color.parseColor("#000000");
                break;
            default:
                color = Color.parseColor("#ffffff");
                break;
        }
        return color;
    }

}
