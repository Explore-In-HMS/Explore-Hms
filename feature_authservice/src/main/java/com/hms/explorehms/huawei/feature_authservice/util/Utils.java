/*
 *
 *   Copyright 2020. Explore in HMS. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.hms.explorehms.huawei.feature_authservice.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    private Utils(){

    }

    public static void initializeAGConnectInstance(Context context) {
        if (AGConnectInstance.getInstance() == null) {
            AGConnectInstance.initialize(context);
        }
    }

    public static boolean isLoggedInAgcUser() {
        boolean result = false;
        if (AGConnectAuth.getInstance() != null) {
            if (AGConnectAuth.getInstance().getCurrentUser() != null) {
                AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
                result = true;
                Log.i(TAG, "isLoggedInAgcUser  : userId : " + user.getUid() + " displayName : " + user.getDisplayName() + " isAnonymous : " + user.isAnonymous() + " is already logged in.");
            }else{
                Log.i(TAG, "isLoggedInAgcUser  : AGConnectAuth.getInstance().getCurrentUser() is NULL ");
            }
        } else {
            Log.i(TAG, "isLoggedInAgcUser  : AGConnectAuth.getInstance() is NULL!");
        }
        return result;
    }


    public static void showToastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void openWebPage(Activity activity, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
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

    public static void startActivity(Activity activity, Class<?> c){
        activity.startActivity(new Intent(activity,c));
    }

}
