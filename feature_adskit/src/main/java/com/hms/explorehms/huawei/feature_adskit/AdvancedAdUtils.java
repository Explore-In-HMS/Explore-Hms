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
package com.hms.explorehms.huawei.feature_adskit;

import android.util.Log;

import com.huawei.hms.ads.AdParam;

public class AdvancedAdUtils {

    private static final String TAG = AdvancedAdUtils.class.getSimpleName();

    /**
     * Set Customize ad RequestOptions parameters
     *
     * @param nonPersonalizedAd         =  0, 1 : ALL
     * @param tagForChildProtection     = -1, 0, 1.  UNSPECIFIED, FALSE, TRUE -> Process ad requests according to the COPPA.
     * @param tagForUnderAgeOfPromise   = -1, 0, 1.  UNSPECIFIED, FALSE, TRUE -> -> Process ad requests according to the GDPR.
     * @param adContentClassification   = W, PI, J, A, "" : widespread audiences,  audiences with parental guidance, junior and older audiences, only for adults, UNKOWN
     * @return AdParam
     *
     * more details : https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/requestoptions-0000001050066843-V5
     * more details : https://developer.huawei.com/consumer/en/doc/HMSCore-Guides-V5/publisher-service-advanced-settings-0000001050064972-V5
     */
    public static AdParam editAndGetAdParam(int nonPersonalizedAd, int tagForChildProtection,
                                            int tagForUnderAgeOfPromise, String adContentClassification) {

        return getAddParamBuilderBase(nonPersonalizedAd, tagForChildProtection, tagForUnderAgeOfPromise, adContentClassification).build();
    }

    private static AdParam.Builder getAddParamBuilderBase(int nonPersonalizedAd, int tagForChildProtection,
                                                   int tagForUnderAgeOfPromise, String adContentClassification){
        AdParam.Builder builder = new AdParam.Builder();

        try {
            builder.setTagForChildProtection(tagForChildProtection);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setTagForChildProtection exception : " + e.getMessage(), e);
        }
        try {
            builder.setNonPersonalizedAd(nonPersonalizedAd);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setNonPersonalizedAd exception : " + e.getMessage(), e);
        }
        try {
            builder.setTagForUnderAgeOfPromise(tagForUnderAgeOfPromise);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setTagForUnderAgeOfPromise exception : " + e.getMessage(), e);
        }
        try {
            builder.setAdContentClassification(adContentClassification);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setAdContentClassification exception : " + e.getMessage(), e);
        }

        return builder;
    }
    /**
     * ! All These customise parameters not supported some region adn some state. getting error !
     *
     * @param targetingContentUrl       = ?
     * @param gender                    = 0 : unKnow, 1 : male, 2 : female
     * @param requestOrigin             = ?
     * @param belongCountryCode         = ? such as 'TR'
     * @param tagForChildProtection     = -1, 0, 1.  UNSPECIFIED, FALSE, TRUE -> Process ad requests according to the COPPA.
     * @param nonPersonalizedAd         = 0, 1 : ALL
     * @param appLang                   = such as 'tr-TR'
     * @param appCountry                = such as 'Turkey'
     * @param tagForUnderAgeOfPromise   = -1, 0, 1.  UNSPECIFIED, FALSE, TRUE -> -> Process ad requests according to the GDPR.
     * @param adContentClassification   = W, PI, J, A : widespread audiences,  audiences with parental guidance, junior and older audiences, only for adults,
     * @return AdParam
     *
     * more details : https://developer.huawei.com/consumer/en/doc/development/HMSCore-References-V5/requestoptions-0000001050066843-V5
     * more details : https://developer.huawei.com/consumer/en/doc/HMSCore-Guides-V5/publisher-service-advanced-settings-0000001050064972-V5
     */
    public AdParam editAndGetAdParamWithAll(String targetingContentUrl, int gender, String requestOrigin,
                                            String belongCountryCode, int tagForChildProtection, int nonPersonalizedAd,
                                            String appLang, String appCountry, int tagForUnderAgeOfPromise,
                                            String adContentClassification) {

        AdParam.Builder builder = getAddParamBuilderBase(nonPersonalizedAd, tagForChildProtection, tagForUnderAgeOfPromise, adContentClassification);

        try {
            builder.setTargetingContentUrl(targetingContentUrl);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setTargetingContentUrl exception : " + e.getMessage(), e);
        }
        try {
            builder.setGender(gender);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setGender exception : " + e.getMessage(), e);
        }
        try {
            builder.setRequestOrigin(requestOrigin);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setRequestOrigin exception : " + e.getMessage(), e);
        }
        try {
            builder.setBelongCountryCode(belongCountryCode);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setBelongCountryCode exception : " + e.getMessage(), e);
        }

        try {
            builder.setAppLang(appLang);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setAppLang exception : " + e.getMessage(), e);
        }
        try {
            builder.setAppCountry(appCountry);
        } catch (Exception e) {
            Log.e(TAG, "editAndGetAdParam.setAppCountry exception : " + e.getMessage(), e);
        }
        return builder.build();
    }

    public static String getDetailsFromErrorCode(int errorCode) {
        switch (errorCode) {
            case AdParam.ErrorCode.INNER:
                return "ERROR: 0 : INNER ";
            case AdParam.ErrorCode.INVALID_REQUEST:
                return "ERROR: 1 : INVALID_REQUEST";
            case AdParam.ErrorCode.NETWORK_ERROR:
                return "ERROR: 2 : NETWORK_ERROR";
            case AdParam.ErrorCode.NO_AD:
                return "ERROR: 3 : NO_AD";
            case AdParam.ErrorCode.AD_LOADING:
                return "ERROR: 4 : AD_LOADING";
            case AdParam.ErrorCode.LOW_API:
                return "ERROR: 5 : LOW_API";
            case AdParam.ErrorCode.BANNER_AD_EXPIRE:
                return "ERROR: 6 : BANNER_AD_EXPIRE";
            case AdParam.ErrorCode.BANNER_AD_CANCEL:
                return "ERROR: 7 : BANNER_AD_CANCEL";
            case AdParam.ErrorCode.HMS_NOT_SUPPORT_SET_APP:
                return "ERROR: 8 : HMS_NOT_SUPPORT_SET_APP";
            default:
                return "";
        }
    }
}
