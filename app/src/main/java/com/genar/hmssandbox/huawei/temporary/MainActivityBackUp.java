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

package com.genar.hmssandbox.huawei.temporary;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.baseapp.adapter.KitAdapter;
import com.genar.hmssandbox.huawei.baseapp.adapter.KitCategoryAdapter;
import com.genar.hmssandbox.huawei.baseapp.model.KitCategoryModel;
import com.genar.hmssandbox.huawei.baseapp.model.KitModel;
import com.genar.hmssandbox.huawei.baseapp.library.SpacesItemDecoration;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.klinker.android.peekview.PeekViewActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivityBackUp extends PeekViewActivity {

    KitCategoryAdapter kitCategoryAdapter;
    ArrayList<KitModel> kitList;
    ArrayList<KitCategoryModel> categoryList;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AGConnectCrash.getInstance().enableCrashCollection(true);

//        Util.initDynamicAbility(this);

        kitList = new ArrayList<>();
        /*kitList.add(
                new KitModel("Auth Service", "com.genar.hmssandbox.huawei.feature_authservice.MainActivityAuth", true, "feature_authservice", R.drawable.icon_authservice, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Location Kit", "com.genar.hmssandbox.huawei.locationkit.LocationKitActivity", false, null, R.drawable.icon_locationkit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Scan Kit", "com.genar.hmssandbox.huawei.scankit.ScanKitActivity", false, null, R.drawable.icon_scankit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Push Kit", "com.genar.hmssandbox.huawei.pushkit.MainActivityPushKit", false, null, R.drawable.icon_pushkit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Site Kit", "com.genar.hmssandbox.huawei.sitekit.MainActivitySiteKit", false, null, R.drawable.icon_sitekit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Map Kit", "com.genar.hmssandbox.huawei.mapkit.MainActivityMapKit", false, null, R.drawable.icon_mapkit, KitModel.HmsCategory.AppServices));

        kitList.add(
                new KitModel("Drive Kit", "com.genar.hmssandbox.huawei.feature_drivekit.DriveKitLoginActivity", true, "feature_drivekit", R.drawable.icon_drivekit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Video Kit", "com.genar.hmssandbox.huawei.feature_videokit.PlayActivity", true, "feature_videokit", R.drawable.icon_videokit, KitModel.HmsCategory.Media));
        kitList.add(
                new KitModel("ML Kit", "com.genar.hmssandbox.huawei.feature_mlkit.MLMainActivity", true, "feature_mlkit", R.drawable.icon_mlkit, KitModel.HmsCategory.AI));
        kitList.add(
                new KitModel("Dynamic Tag Manager", "com.genar.hmssandbox.huawei.feature_dynamictagmanager.DynamicActivity", true, "feature_dynamictagmanager", R.drawable.icon_dynamictagmanager, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Nearby Service", "com.genar.hmssandbox.huawei.feature_nearbyservice.NearbyServiceActivity", true, "feature_nearbyservice", R.drawable.icon_nearbyservices, KitModel.HmsCategory.System));
        kitList.add(
                new KitModel("Panorama Kit", "com.genar.hmssandbox.huawei.feature_panorama.PanoramaActivity", true, "feature_panorama", R.drawable.icon_panoramakit, KitModel.HmsCategory.Media));
        kitList.add(
                new KitModel("Image Kit", "com.genar.hmssandbox.huawei.feature_imagekit.MainActivityImageKit", true, "feature_imagekit", R.drawable.icon_imagekit, KitModel.HmsCategory.Media));
        kitList.add(
                new KitModel("Scene Kit", "com.genar.hmssandbox.huawei.feature_scenekit.MainActivity", true, "feature_scenekit", R.drawable.icon_scenekit, KitModel.HmsCategory.Graphics));
        kitList.add(
                new KitModel("Analytics Kit", "com.genar.hmssandbox.huawei.feature_analyticskit.AnalyticsKitActivity", true, "feature_analyticskit", R.drawable.icon_analyticskit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Health Kit", "com.genar.hmssandbox.huawei.feature_healthkit.auth.HealthKitAuthClientActivity", true, "feature_healthkit", R.drawable.icon_health_kit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Ads Kit", "com.akgul.hmssandbox.huawei.MainActivity", true, "feature_adskit", R.drawable.icon_adskit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Awareness Kit", "com.genar.hmssandbox.huawei.feature_awarenesskit.ui.AwarenessKitReworkInfoActivity", true, "feature_awarenesskit", R.drawable.icon_awereness_kit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Identity Kit", "com.genar.hmssandbox.huawei.feature_identitykit.IdentityActivity", true, "feature_identitykit", R.drawable.icon_identity_kit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Fido Kit", "com.genar.hmssandbox.huawei.feature_fidokit.FidoActivity", true, "feature_fidokit", R.drawable.icon_fido, KitModel.HmsCategory.Security));
        kitList.add(
                new KitModel("App Linking", "com.genar.hmssandbox.huawei.feature_applinking.AppLinkingMainActivity", true, "feature_applinking", R.drawable.icon_applinking, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Account Kit", "com.genar.hmssandbox.huawei.feature_accountkit.LoginActivity", true, "feature_accountkit", R.drawable.icon_accounts, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Share Engine", "com.genar.hmssandbox.huawei.feature_shareengine.ShareEngineMainActivity", true, "feature_shareengine", R.drawable.icon_sharekit, KitModel.HmsCategory.SmartDevice));
        kitList.add(
                new KitModel("Audio Kit", "com.genar.hmssandbox.huawei.feature_audiokit.AudioMainActivity", true, "feature_audiokit", R.drawable.icon_audiokit, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Search Kit", "com.genar.hmssandbox.huawei.feature_searchkit.SearchKitMainActivity", true, "feature_searchkit", R.drawable.icon_searchkit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Safety Detect", "com.genar.hmssandbox.huawei.feature_safetydetect.MainSafetyActivity", true, "feature_safetydetect", R.drawable.icon_safetydetect, KitModel.HmsCategory.Security));
        kitList.add(
                new KitModel("Quick App", "com.genar.hmssandbox.huawei.feature_quickapp.OtherFeaturesSliderActivity", true, "feature_quickapp", R.drawable.icon_quickapp, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Wireless Kit", "com.genar.hmssandbox.huawei.feature_wirelesskit.MainWirelessActivity", true, "feature_wirelesskit", R.drawable.icon_wirelesskit, KitModel.HmsCategory.System));
        kitList.add(
                new KitModel("Accelerate Kit", "com.genar.hmssandbox.huawei.feature_acceleratekit.AccelerateKitMainActivity", true, "feature_acceleratekit", R.drawable.icon_acceleratekit, KitModel.HmsCategory.Graphics));
        kitList.add(
                new KitModel("Cast Engine", "com.genar.hmssandbox.huawei.feature_castengine.CastEngineMainActivity", true, "feature_castengine", R.drawable.icon_castengine, KitModel.HmsCategory.SmartDevice));
        kitList.add(
                new KitModel("Cloud Functions", "com.genar.hmssandbox.huawei.feature_cloudfunctions.CloudFunctionsMainActivity", true, "feature_cloudfunctions", R.drawable.icon_agc_cloudfunctions, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Hi AI", "com.genar.hmssandbox.huawei.feature_hiai.MainActivityHiAi", true, "feature_hiai", R.drawable.icon_hiai, KitModel.HmsCategory.AI));
        kitList.add(
                new KitModel("Cloud DB", "com.genar.hmssandbox.huawei.feature_clouddb.view.MainActivityCloud", true, "feature_clouddb", R.drawable.cloud_feature_icon, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Wallet Kit", "com.genar.hmssandbox.huawei.feature_walletkit.GeneralFeaturesActivity", true, "feature_walletkit", R.drawable.icon_walletkit, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("CaaS Kit", "com.genar.hmssandbox.huawei.feature_caaskit.GeneralFeaturesActivity", true, "feature_caaskit", R.drawable.icon_caaskitlite, KitModel.HmsCategory.SmartDevice));
        kitList.add(
                new KitModel("App Signing", "com.genar.hmssandbox.huawei.feature_appsigning.GeneralFeaturesActivity", true, "feature_appsigning", R.drawable.icon_appsigning, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Computer Graphics Kit", "com.genar.hmssandbox.huawei.feature_computergraphics.ComputerGraphicsMainActivity", true, "feature_computergraphics", R.drawable.icon_computergraphics, KitModel.HmsCategory.Graphics));
        kitList.add(
                new KitModel("Open Testing", "com.genar.hmssandbox.huawei.feature_opentesting.GeneralFeaturesActivity", true, "feature_opentesting", R.drawable.opentesting_icon, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Crash Service", "com.genar.hmssandbox.huawei.feature_crashservice.CrashServiceMainActivity", true, "feature_crashservice", R.drawable.crash_service, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Cloud Testing", "com.genar.hmssandbox.huawei.feature_cloudtesting.GeneralFeaturesActivity", true, "feature_cloudtesting", R.drawable.icon_cloud_testing, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("HQUIC Kit", "com.genar.hmssandbox.huawei.feature_hquic.HQUICActivity", true, "feature_hquic", R.drawable.icon_hquic, KitModel.HmsCategory.System));
        kitList.add(
                new KitModel("Cloud Debugging", "com.genar.hmssandbox.huawei.feature_clouddebugging.GeneralFeaturesActivity", true, "feature_clouddebugging", R.drawable.huawei_icon_clouddebugging, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Remote Configuration", "com.genar.hmssandbox.huawei.feature_remoteconfig.RemoteConfigMainActivity", true, "feature_remoteconfig", R.drawable.remote_configuration, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("A/B Test", "com.genar.hmssandbox.huawei.feature_abtest.ABTestActivity", true, "feature_abtest", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("WisePlay DRM", "com.genar.hmssandbox.huawei.wiseplaydrm.WisePlayDRMMainActivity", true, "feature_wiseplaydrm", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.Media));
        kitList.add(
                new KitModel("Operation Analysis Kit", "com.genar.hmssandbox.huawei.feature_operationanalysis.OperationMainActivity", true, "feature_operationanalysis", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Distribution Analysis", "com.genar.hmssandbox.huawei.feature_distributionanalysis.DistributionMainActivity", true, "feature_distributionanalysis", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("OneHop Engine", "com.genar.hmssandbox.huawei.feature_onehopengine.GeneralFeaturesActivity", true, "feature_onehopengine", R.drawable.onehop_engine_icon, KitModel.HmsCategory.SmartDevice));
        kitList.add(
                new KitModel("Cloud Storage", "com.genar.hmssandbox.huawei.feature_cloudstorage.CloudStorageMainActivity", true, "feature_cloudstorage", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Cloud Hosting", "com.genar.hmssandbox.huawei.feature_cloudhosting.CloudHostingMainActivity", true, "feature_cloudhosting", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("App Messaging", "com.genar.hmssandbox.huawei.feature_appmessaging.AppMessagingMainActivity", true, "feature_appmessaging", R.drawable.app_messaging_icon, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("App Bundle", "com.genar.hmssandbox.huawei.feature_appbundle.AppBundleMainActivity", true, "feature_appbundle", R.drawable.app_bundle_icon, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Preorders", "com.genar.hmssandbox.huawei.feature_preorders.PreordersActivity", false, "feature_hquic", R.drawable.icon_hquic, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Early Access", "com.genar.hmssandbox.huawei.feature_earlyaccess.EarlyAccessActivity", false, "feature_earlyaccess", R.drawable.icon_hquic, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("App Release", "com.genar.hmssandbox.huawei.feature_apprelease.AppReleaseActivity", false, "feature_apprelease", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("In-App Purchases", "com.genar.hmssandbox.huawei.feature_inapppurchases.GeneralFeaturesActivity", true, "feature_inapppurchases", R.drawable.inapppurchasesicon, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("App Performance Management", "com.genar.hmssandbox.huawei.feature_apm.AppPerformanceMainActivity", true, "feature_apm", R.drawable.apm_icon, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Comments", "com.genar.hmssandbox.huawei.feature_comments.CommentsActivity", true, "feature_comments", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Gift Management", "com.genar.hmssandbox.huawei.feature_giftmanagement.GiftManagementActivity", true, "feature_giftmanagement", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Game Service", "com.genar.hmssandbox.huawei.feature_gameservice.GameServicesMainActivity", true, "feature_gameservice", R.drawable.icon_gameservices, KitModel.HmsCategory.AppServices));
        kitList.add(
                new KitModel("Integration Check", "com.genar.hmssandbox.huawei.feature_integrationcheck.MainIntegrationCheckActivity", true, "feature_integrationcheck", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Paid Apps", "com.genar.hmssandbox.huawei.feature_paidapps.GeneralFeaturesActivity", true, "feature_paidapps", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Product Management", "com.genar.hmssandbox.huawei.feature_productmanagement.ProductManagementActivity", true, "feature_productmanagement", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Phased Release", "com.genar.hmssandbox.huawei.feature_phasedrelease.PhasedReleaseActivity", true, "feature_phasedrelease", R.drawable.ic_launcher_foreground, KitModel.HmsCategory.AppGalleryConnect));
        kitList.add(
                new KitModel("Audio Engine", "com.genar.hmssandbox.huawei.feature_audioengine.MainAudioEngineActivity", true, "feature_audioengine", R.drawable.icon_audiokit, KitModel.HmsCategory.Media));

        categoryList = new ArrayList<>();
        categoryList.add(
                new KitCategoryModel("App Services", getKitsByCategory(KitModel.HmsCategory.AppServices)));
        categoryList.add(
                new KitCategoryModel("Graphics", getKitsByCategory(KitModel.HmsCategory.Graphics)));
        categoryList.add(
                new KitCategoryModel("Media", getKitsByCategory(KitModel.HmsCategory.Media)));
        categoryList.add(
                new KitCategoryModel("AI", getKitsByCategory(KitModel.HmsCategory.AI)));
        categoryList.add(
                new KitCategoryModel("Smart Device", getKitsByCategory(KitModel.HmsCategory.SmartDevice)));
        categoryList.add(
                new KitCategoryModel("Security", getKitsByCategory(KitModel.HmsCategory.Security)));
        categoryList.add(
                new KitCategoryModel("System", getKitsByCategory(KitModel.HmsCategory.System)));
        categoryList.add(
                new KitCategoryModel("App Gallery Connect", getKitsByCategory(KitModel.HmsCategory.AppGalleryConnect)));*/


        /*RecyclerView rv_kitCategory = findViewById(R.id.rv_kitCategory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_kitCategory.setLayoutManager(layoutManager);
        kitCategoryAdapter = new KitCategoryAdapter(this, categoryList);
        rv_kitCategory.setAdapter(kitCategoryAdapter);*/
    }

    private void setKitRecylerView(RecyclerView rv, List<KitModel> list) {
        int columnCount = 3;
        rv.addItemDecoration(new SpacesItemDecoration(16, columnCount));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        rv.setLayoutManager(layoutManager);
        KitAdapter adapter = new KitAdapter(this, list);
        rv.setAdapter(adapter);
    }

    private List<KitModel> getKitsByCategory(KitModel.HmsCategory category) {
        List<KitModel> result = new ArrayList<>();
        for (KitModel kit : kitList) {
            if (kit.getCategory() == category) {
                result.add(kit);
            }
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem btnsearch = menu.findItem(R.id.btn_searchKits);
        SearchView sv = (SearchView) btnsearch.getActionView();
        int id = sv.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setTextColor(Color.WHITE);
//        sv.setMaxWidth(Integer.MAX_VALUE);
        /*sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchStr = newText.toLowerCase();
                ArrayList<KitModel> resultList = new ArrayList<>();

                for (KitModel kitModel : kitList) {
                    if (kitModel.getKitName().toLowerCase().contains(searchStr)) {
                        resultList.add(kitModel);
                    }
                }

//                kitCategoryAdapter.updateList(resultList);
                return true;
            }
        });*/
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_searchKits) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void expand(final View v) {
        v.measure(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ConstraintLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

//    private void CreateKitCategoryView(){
//        ConstraintLayout cv = new ConstraintLayout();
//        cv.layout
//    }
}
