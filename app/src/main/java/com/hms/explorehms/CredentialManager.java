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
package com.hms.explorehms;

import android.net.Uri;

import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class CredentialManager {

    public CredentialManager(){
        // Empty constructor
    }

    private static AccountAuthService  mHuaweiIdAuthService;

    private static AuthHuaweiId huaweiId;

    public static boolean isSignedOut = true;

    public static AccountAuthService getHuaweiIdAuthService() {
        return mHuaweiIdAuthService;
    }

    public static void setHuaweiIdAuthService(AccountAuthService mHuaweiIdAuthService) {
        CredentialManager.mHuaweiIdAuthService = mHuaweiIdAuthService;
    }
    public static String getIDToken() {
        return huaweiId.getIdToken();
    }

    public static String getAccessToken(){
        return huaweiId.getAccessToken();
    }

    public static String getUserName() {
        return huaweiId.getDisplayName();
    }

    public static String getDisplaName() {
        return huaweiId.getDisplayName();
    }

    public static String getFullname() {
        return huaweiId.getGivenName() + " " + huaweiId.getFamilyName();
    }

    public static String getEmail(){
        return huaweiId.getEmail();
    }

    public static Uri getProfilePic() {
        return huaweiId.getAvatarUri();
    }

    public static void setCredentials(AuthHuaweiId authHuaweiId){
        huaweiId = authHuaweiId;
        isSignedOut = false;
    }

    public static void clearAuthorization(){
        huaweiId = null;
        isSignedOut = true;
    }
}
