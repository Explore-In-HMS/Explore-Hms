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

package com.genar.hmssandbox.huawei.baseapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.DrawableRes;


import com.genar.hmssandbox.huawei.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class KitModel implements Parcelable {

    public static List<KitModel> KitList = Arrays.asList(
            new KitModel("Auth Service", "com.genar.hmssandbox.huawei.feature_authservice.MainActivityAuth", true, "feature_authservice", R.drawable.icon_authservice, KitModel.HmsCategory.Build),
            new KitModel("Cloud Functions", "com.genar.hmssandbox.huawei.feature_cloudfunctions.CloudFunctionsMainActivity", true, "feature_cloudfunctions", R.drawable.icon_agc_cloudfunctions, KitModel.HmsCategory.Build),
            new KitModel("Cloud DB", "com.genar.hmssandbox.huawei.feature_clouddb.view.MainActivityCloud", true, "feature_clouddb", R.drawable.cloud_feature_icon, KitModel.HmsCategory.Build),
            new KitModel("App Signing", "com.genar.hmssandbox.huawei.feature_appsigning.GeneralFeaturesActivity", true, "feature_appsigning", R.drawable.icon_appsigning, KitModel.HmsCategory.Release, KitMode.REFERENCE),
            new KitModel("Open Testing", "com.genar.hmssandbox.huawei.feature_opentesting.GeneralFeaturesActivity", true, "feature_opentesting", R.drawable.opentesting_icon, KitModel.HmsCategory.Quality, KitMode.REFERENCE),
            new KitModel("Crash Service", "com.genar.hmssandbox.huawei.reference.crashservice.CrashServiceMainActivity", false, null, R.drawable.crash_service, KitModel.HmsCategory.Quality),
            new KitModel("Cloud Testing", "com.genar.hmssandbox.huawei.feature_cloudtesting.GeneralFeaturesActivity", true, "feature_cloudtesting", R.drawable.icon_cloud_testing, KitModel.HmsCategory.Quality, KitMode.REFERENCE),
            new KitModel("Cloud Debugging", "com.genar.hmssandbox.huawei.feature_clouddebugging.GeneralFeaturesActivity", true, "feature_clouddebugging", R.drawable.huawei_icon_clouddebugging, KitModel.HmsCategory.Quality, KitMode.REFERENCE),
            new KitModel("Remote Configuration", "com.genar.hmssandbox.huawei.feature_remoteconfig.RemoteConfigMainActivity", true, "feature_remoteconfig", R.drawable.remote_configuration, KitModel.HmsCategory.Growing),
            new KitModel("A/B Test", "com.genar.hmssandbox.huawei.feature_abtest.ABTestActivity", true, "feature_abtest", R.drawable.icon_ab_test, KitModel.HmsCategory.Growing, KitMode.REFERENCE),
            new KitModel("Operation Analysis Kit", "com.genar.hmssandbox.huawei.feature_operationanalysis.OperationMainActivity", true, "feature_operationanalysis", R.drawable.icon_operation_analysis, KitModel.HmsCategory.Analytics, KitMode.REFERENCE),
            new KitModel("Distribution Analysis", "com.genar.hmssandbox.huawei.feature_distributionanalysis.DistributionMainActivity", true, "feature_distributionanalysis", R.drawable.icon_distribution_analysis, KitModel.HmsCategory.Analytics, KitMode.REFERENCE),
            new KitModel("Cloud Storage", "com.genar.hmssandbox.huawei.feature_cloudstorage.CloudStorageMainActivity", true, "feature_cloudstorage", R.drawable.icon_cloud_storage, KitModel.HmsCategory.Build),
            new KitModel("Cloud Hosting", "com.genar.hmssandbox.huawei.reference.cloudhosting.CloudHostingMainActivity", false, null, R.drawable.icon_cloud_hosting, KitModel.HmsCategory.Build),
            new KitModel("App Messaging", "com.genar.hmssandbox.huawei.feature_appmessaging.AppMessagingMainActivity", true, "feature_appmessaging", R.drawable.app_messaging_icon, KitModel.HmsCategory.Growing),
            new KitModel("App Linking", "com.genar.hmssandbox.huawei.feature_applinking.AppLinkingMainActivity", true, "feature_applinking", R.drawable.icon_applinking, KitModel.HmsCategory.Growing),
            new KitModel("App Bundle", "com.genar.hmssandbox.huawei.reference.appbundle.AppBundleMainActivity", false, null, R.drawable.app_bundle_icon, KitModel.HmsCategory.Release, KitMode.REFERENCE),
            new KitModel("Preorders", "com.genar.hmssandbox.huawei.reference.preorders.PreordersActivity", false, null, R.drawable.ic_preorder, KitModel.HmsCategory.Release, KitMode.REFERENCE),
            new KitModel("Early Access", "com.genar.hmssandbox.huawei.reference.earlyaccess.EarlyAccessActivity", false, null, R.drawable.ic_earlyaccess, KitModel.HmsCategory.Release, KitMode.REFERENCE),
            new KitModel("App Release", "com.genar.hmssandbox.huawei.reference.apprelease.AppReleaseActivity", false, null, R.drawable.icon_apprelease, KitModel.HmsCategory.Release, KitMode.REFERENCE),
            new KitModel("App Performance Management", "com.genar.hmssandbox.huawei.reference.apm.AppPerformanceMainActivity", false, null, R.drawable.apm_icon, KitModel.HmsCategory.Quality, KitMode.REFERENCE),
            new KitModel("Comments", "com.genar.hmssandbox.huawei.reference.comments.CommentsActivity", false, null, R.drawable.icon_comment, KitModel.HmsCategory.Growing, KitMode.REFERENCE),
            new KitModel("Gift Management", "com.genar.hmssandbox.huawei.reference.giftmanagement.GiftManagementActivity", false, null, R.drawable.icon_gift_management, KitModel.HmsCategory.Growing, KitMode.REFERENCE),
            new KitModel("Integration Check", "com.genar.hmssandbox.huawei.reference.integrationcheck.MainIntegrationCheckActivity", false, null, R.drawable.icon_integration_check, KitModel.HmsCategory.Quality, KitMode.REFERENCE),
            new KitModel("Paid Apps", "com.genar.hmssandbox.huawei.reference.paidapps.GeneralFeaturesActivity", false, null, R.drawable.icon_paid_apps, KitModel.HmsCategory.Earn, KitMode.REFERENCE),
            new KitModel("Product Management", "com.genar.hmssandbox.huawei.feature_productmanagement.ProductManagementActivity", true, "feature_productmanagement", R.drawable.icon_product_management, KitModel.HmsCategory.Growing, KitMode.REFERENCE),
            new KitModel("Phased Release", "com.genar.hmssandbox.huawei.feature_phasedrelease.PhasedReleaseActivity", true, "feature_phasedrelease", R.drawable.icon_phased_release, KitModel.HmsCategory.Release, KitMode.REFERENCE),
            new KitModel("Dynamic Ability", "com.genar.hmssandbox.huawei.feature_dynamicability.DynamicAbilityMainActivity", true, "feature_dynamicability", R.drawable.dynamic_ability, KitModel.HmsCategory.Build),
            new KitModel("App Gallery Kit", "com.genar.hmssandbox.huawei.feature_appgallerykit.MainActivity", true, "feature_appgallerykit", R.drawable.icon_huawei_appgallery, KitModel.HmsCategory.Earn, KitMode.REFERENCE),

            new KitModel("Location Kit", "com.genar.hmssandbox.huawei.locationkit.LocationKitActivity", false, null, R.drawable.icon_locationkit, KitModel.HmsCategory.AppServices),
            new KitModel("Account Kit", "com.genar.hmssandbox.huawei.feature_accountkit.LoginActivity", true, "feature_accountkit", R.drawable.icon_accounts, KitModel.HmsCategory.AppServices),
            new KitModel("Scan Kit", "com.genar.hmssandbox.huawei.scankit.ScanKitActivity", false, null, R.drawable.icon_scankit, KitModel.HmsCategory.AppServices),
            new KitModel("Push Kit", "com.genar.hmssandbox.huawei.pushkit.MainActivityPushKit", false, null, R.drawable.icon_pushkit, KitModel.HmsCategory.AppServices),
            new KitModel("Site Kit", "com.genar.hmssandbox.huawei.sitekit.MainActivitySiteKit", false, null, R.drawable.icon_sitekit, KitModel.HmsCategory.AppServices),
            new KitModel("Map Kit", "com.genar.hmssandbox.huawei.mapkit.MainActivityMapKit", false, null, R.drawable.icon_mapkit, KitModel.HmsCategory.AppServices),
            new KitModel("Drive Kit", "com.genar.hmssandbox.huawei.feature_drivekit.DriveKitLoginActivity", true, "feature_drivekit", R.drawable.icon_drivekit, KitModel.HmsCategory.AppServices),
            new KitModel("Video Kit", "com.genar.hmssandbox.huawei.feature_videokit.activity.HomePageActivity", true, "feature_videokit", R.drawable.icon_videokit, KitModel.HmsCategory.Media),
            new KitModel("Camera Engine", "com.genar.hmssandbox.huawei.feature_cameraengine.MainActivityCameraEngine", true, "feature_cameraengine", R.drawable.icon_cameraengine, KitModel.HmsCategory.Media),
            new KitModel("ML Kit", "com.genar.hmssandbox.huawei.feature_mlkit.MLMainActivity", true, "feature_mlkit", R.drawable.icon_mlkit, KitModel.HmsCategory.AI),
            new KitModel("Dynamic Tag Manager", "com.genar.hmssandbox.huawei.reference.dynamictagmanager.DynamicActivity", false, null, R.drawable.icon_dynamictagmanager, KitModel.HmsCategory.AppServices),
            new KitModel("Nearby Service", "com.genar.hmssandbox.huawei.feature_nearbyservice.NearbyServiceActivity", true, "feature_nearbyservice", R.drawable.icon_nearbyservices, KitModel.HmsCategory.System),
            new KitModel("Panorama Kit", "com.genar.hmssandbox.huawei.feature_panorama.PanoramaActivity", true, "feature_panorama", R.drawable.icon_panoramakit, KitModel.HmsCategory.Media),
            new KitModel("Image Kit", "com.genar.hmssandbox.huawei.feature_imagekit.MainActivityImageKit", true, "feature_imagekit", R.drawable.icon_imagekit, KitModel.HmsCategory.Media),
            new KitModel("Scene Kit", "com.genar.hmssandbox.huawei.feature_scenekit.MainActivity", true, "feature_scenekit", R.drawable.icon_scenekit, KitModel.HmsCategory.Graphics),
            new KitModel("Analytics Kit", "com.genar.hmssandbox.huawei.feature_analyticskit.AnalyticsKitActivity", true, "feature_analyticskit", R.drawable.icon_analyticskit, KitModel.HmsCategory.AppServices),
            new KitModel("Health Kit", "com.genar.hmssandbox.huawei.feature_healthkit.auth.HealthKitAuthClientActivity", true, "feature_healthkit", R.drawable.icon_health_kit, KitModel.HmsCategory.AppServices),
            new KitModel("Ads Kit", "com.genar.hmssandbox.huawei.feature_adskit.MainActivity", true, "feature_adskit", R.drawable.icon_adskit, KitModel.HmsCategory.AppServices),
            new KitModel("Awareness Kit", "com.genar.hmssandbox.huawei.feature_awarenesskit.ui.AwarenessKitReworkInfoActivity", true, "feature_awarenesskit", R.drawable.icon_awereness_kit, KitModel.HmsCategory.AppServices),
            new KitModel("Identity Kit", "com.genar.hmssandbox.huawei.feature_identitykit.IdentityActivity", true, "feature_identitykit", R.drawable.icon_identity_kit, KitModel.HmsCategory.AppServices),
            new KitModel("Fido Kit", "com.genar.hmssandbox.huawei.feature_fidokit.FidoActivity", true, "feature_fidokit", R.drawable.icon_fido, KitModel.HmsCategory.Security),
            new KitModel("Share Engine", "com.genar.hmssandbox.huawei.feature_shareengine.ShareEngineMainActivity", true, "feature_shareengine", R.drawable.icon_sharekit, KitModel.HmsCategory.SmartDevice),
            new KitModel("Search Kit", "com.genar.hmssandbox.huawei.feature_searchkit.SearchKitMainActivity", true, "feature_searchkit", R.drawable.icon_searchkit, KitModel.HmsCategory.AppServices),
            new KitModel("Safety Detect", "com.genar.hmssandbox.huawei.feature_safetydetect.MainSafetyActivity", true, "feature_safetydetect", R.drawable.icon_safetydetect, KitModel.HmsCategory.Security),
            new KitModel("Quick App", "com.genar.hmssandbox.huawei.feature_quickapp.OtherFeaturesSliderActivity", true, "feature_quickapp", R.drawable.icon_quickapp, KitModel.HmsCategory.AppServices, KitMode.REFERENCE),
            new KitModel("Wireless Kit", "com.genar.hmssandbox.huawei.feature_wirelesskit.MainWirelessActivity", true, "feature_wirelesskit", R.drawable.icon_wirelesskit, KitModel.HmsCategory.System),
            new KitModel("Accelerate Kit", "com.genar.hmssandbox.huawei.feature_acceleratekit.AccelerateKitMainActivity", true, "feature_acceleratekit", R.drawable.icon_acceleratekit, KitModel.HmsCategory.Graphics),
            new KitModel("Cast Engine", "com.genar.hmssandbox.huawei.reference.castengine.CastEngineMainActivity", false, null, R.drawable.icon_castengine, KitModel.HmsCategory.SmartDevice, KitMode.REFERENCE),
            new KitModel("Hi AI", "com.genar.hmssandbox.huawei.feature_hiai.MainActivityHiAi", true, "feature_hiai", R.drawable.icon_hiai, KitModel.HmsCategory.AI),
            new KitModel("Wallet Kit", "com.genar.hmssandbox.huawei.feature_walletkit.GeneralFeaturesActivity", true, "feature_walletkit", R.drawable.icon_walletkit, KitModel.HmsCategory.AppServices, KitMode.REFERENCE),
            new KitModel("CaaS Engine", "com.genar.hmssandbox.huawei.feature_caaskit.GeneralFeaturesActivity", true, "feature_caaskit", R.drawable.icon_caaskitlite, KitModel.HmsCategory.SmartDevice, KitMode.REFERENCE),
            new KitModel("Computer Graphics Kit", "com.genar.hmssandbox.huawei.feature_computergraphics.ComputerGraphicsMainActivity", true, "feature_computergraphics", R.drawable.icon_computergraphics, KitModel.HmsCategory.Graphics),
            new KitModel("HQUIC Kit", "com.genar.hmssandbox.huawei.reference.hquic.HQUICActivity", false, null, R.drawable.icon_hquic, KitModel.HmsCategory.System),
            new KitModel("WisePlay DRM", "com.genar.hmssandbox.huawei.reference.wiseplaydrm.WisePlayDRMMainActivity", false, null, R.drawable.icon_drm, KitModel.HmsCategory.Media, KitMode.REFERENCE),
            new KitModel("OneHop Engine", "com.genar.hmssandbox.huawei.feature_onehopengine.GeneralFeaturesActivity", true, "feature_onehopengine", R.drawable.onehop_engine_icon, KitModel.HmsCategory.SmartDevice, KitMode.REFERENCE),
            new KitModel("In App Purchases", "com.genar.hmssandbox.huawei.feature_inapppurchases.GeneralFeaturesActivity", true, "feature_inapppurchases", R.drawable.inapppurchasesicon, KitModel.HmsCategory.AppServices, KitMode.REFERENCE),
            new KitModel("Game Service", "com.genar.hmssandbox.huawei.feature_gameservice.GameServicesMainActivity", true, "feature_gameservice", R.drawable.icon_gameservices, KitModel.HmsCategory.AppServices),
            new KitModel("Audio Engine", "com.genar.hmssandbox.huawei.feature_audioengine.MainAudioEngineActivity", true, "feature_audioengine", R.drawable.icon_audiokit, KitModel.HmsCategory.Media, KitMode.REFERENCE),
            new KitModel("Audio Kit", "com.genar.hmssandbox.huawei.feature_audiokit.AudioMainActivity", true, "feature_audiokit", R.drawable.icon_audiokit, KitModel.HmsCategory.Media),
            new KitModel("AR Engine", "com.genar.hmssandbox.huawei.feature_arengine.MainActivityAREngine", true, "feature_arengine", R.drawable.icon_arengine, KitModel.HmsCategory.Graphics),
            new KitModel("Wear Engine", "com.genar.hmssandbox.huawei.feature_wearengine.WearEngineActivity", true, "feature_wearengine", R.drawable.icon_wear_engine, KitModel.HmsCategory.SmartDevice, KitMode.REFERENCE),
            new KitModel("Local Authentication Engine", "com.genar.hmssandbox.huawei.localauthenticationengine.LocalAuthenticationMainActivity", false, null, R.drawable.icon_local_auth, HmsCategory.Security, KitMode.REFERENCE),
            new KitModel("Data Security Engine", "com.genar.hmssandbox.huawei.reference.datasecurity.GeneralFeaturesActivity", false, null, R.drawable.datasecurityicon, HmsCategory.Security, KitMode.REFERENCE),
            new KitModel("Device Virtualization Engine", "com.genar.hmssandbox.huawei.feature_devicevirtualizationengine.DeviceVirtualizationMainActivity", true, "feature_devicevirtualizationengine", R.drawable.icon_devicevirtual, HmsCategory.SmartDevice, KitMode.REFERENCE),
            new KitModel("HiAI Foundation", "com.genar.hmssandbox.huawei.feature_hiaifoundation.HiAIFoundationActivity", true, "feature_hiaifoundation", R.drawable.icon_mlkit, HmsCategory.AI),
            new KitModel("Connect API", "com.genar.hmssandbox.huawei.feature_connectapi.HomeActivity", true, "feature_connectapi", R.drawable.ic_connectapi, HmsCategory.Build, KitMode.REFERENCE),
            new KitModel("Network Kit", "com.genar.hmssandbox.huawei.feature_networkkit.MainActivity", true, "feature_networkkit", R.drawable.ic_connectapi, HmsCategory.System),
            new KitModel("HEM Kit","com.genar.hmssandbox.huawei.feature_hemkit.MainActivity",true,"feature_hemkit",R.drawable.ic_connectapi,HmsCategory.System),
            new KitModel("Audio Editor Kit", "com.genar.hmssandbox.huawei.feature_audioeditorkit.ui.MainAudioEditorActivity", true, "feature_audioeditorkit", R.drawable.icon_audio_editor_kit, KitModel.HmsCategory.Media),
            new KitModel("AV Pipeline Kit", "com.genar.hmssandbox.huawei.feature_avpipelinekit.MainActivity", true, "feature_avpipelinekit", R.drawable.icon_avpipeline_kit, KitModel.HmsCategory.Media),
            new KitModel("Video Editor Kit", "com.genar.hmssandbox.huawei.feature_videoeditorkit.ServiceIntroductionActivity", true, "feature_videoeditorkit", R.drawable.icon_video_editor_kit, KitModel.HmsCategory.Media),
            new KitModel("3D Modeling Kit", "com.genar.hmssandbox.huawei.modelingkit3d.ui.activity.MainActivity", false, null, R.drawable.icon_modeling3d_kit, KitModel.HmsCategory.Graphics),
            new KitModel("5G Modem Kit", "com.genar.hmssandbox.huawei.feature_modem5g_kit.ServiceIntroductionActivity", true,"feature_modem5g_kit" , R.drawable.icon_modem5g_kit, HmsCategory.System),
            new KitModel("Keyring Service", "com.genar.hmssandbox.huawei.keyring.KeyringServiceIntroductionActivity", false,null , R.drawable.icon_keyservice, HmsCategory.Security)
    );

    public static List<KitModel> getKitsByCategory(HmsCategory category) {
        List<KitModel> result = new ArrayList<>();
        for (KitModel kit : KitList) {
            if (kit.getCategory() == category) {
                result.add(kit);
            }
        }
        return result;
    }

    public static final Creator<KitModel> CREATOR = new Creator<KitModel>() {
        @Override
        public KitModel createFromParcel(Parcel in) {
            return new KitModel(in);
        }

        @Override
        public KitModel[] newArray(int size) {
            return new KitModel[size];
        }
    };
    public static Comparator<KitModel> KITNAME = (o1, o2) -> o1.getKitName().compareToIgnoreCase(o2.getKitName());
    public static Comparator<KitModel> IS_DYNAMIC_FEATURE = (o1, o2) -> {
        if (o1.isDynamicFeature() == o2.isDynamicFeature()) {
            return o1.getKitName().compareToIgnoreCase(o2.getKitName());
        } else {
            return Boolean.compare(o1.isDynamicFeature(), o2.isDynamicFeature());
        }
    };
    private String kitName;
    private String kitPackageName;
    private boolean isDynamicFeature;
    private String featureName;
    private @DrawableRes
    int kitIconResource;
    private HmsCategory category;
    private KitMode mode;

    public KitModel(String kitName, String kitPackageName, boolean isDynamicFeature, String featureName, int kitIconResource, HmsCategory category) {
        this.kitName = kitName;
        this.kitPackageName = kitPackageName;
        this.isDynamicFeature = isDynamicFeature;
        this.featureName = featureName;
        this.kitIconResource = kitIconResource;
        this.category = category;
    }

    public KitModel(String kitName, String kitPackageName, boolean isDynamicFeature, String featureName, int kitIconResource, HmsCategory category, KitMode mode) {
        this.kitName = kitName;
        this.kitPackageName = kitPackageName;
        this.isDynamicFeature = isDynamicFeature;
        this.featureName = featureName;
        this.kitIconResource = kitIconResource;
        this.category = category;
        this.mode = mode;
    }

    protected KitModel(Parcel in) {
        kitName = in.readString();
        kitPackageName = in.readString();
        isDynamicFeature = in.readByte() != 0;
        featureName = in.readString();
        kitIconResource = in.readInt();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kitName);
        dest.writeString(kitPackageName);
        dest.writeByte((byte) (isDynamicFeature ? 1 : 0));
        dest.writeString(featureName);
        dest.writeInt(kitIconResource);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getKitName() {
        return kitName;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public String getKitPackageName() {
        return kitPackageName;
    }

    public void setKitPackageName(String kitPackageName) {
        this.kitPackageName = kitPackageName;
    }

    public boolean isDynamicFeature() {
        return isDynamicFeature;
    }

    public void setDynamicFeature(boolean dynamicFeature) {
        isDynamicFeature = dynamicFeature;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public @DrawableRes
    int getKitIconResource() {
        return kitIconResource;
    }

    public void setKitIconResource(@DrawableRes int kitIconResource) {
        this.kitIconResource = kitIconResource;
    }

    public KitMode getMode() {
        return mode;
    }

    public void setMode(KitMode mode) {
        this.mode = mode;
    }

    public HmsCategory getCategory() {
        return category;
    }

    public void setCategory(HmsCategory category) {
        this.category = category;
    }

    public enum HmsCategory {
        AppServices,
        Graphics,
        Media,
        AI,
        SmartDevice,
        Security,
        System,

        Release,
        Build,
        Growing,
        Quality,
        Earn,
        Analytics
    }

    public enum KitMode {
        REFERENCE
    }
}
