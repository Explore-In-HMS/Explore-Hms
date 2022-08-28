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

package com.hms.explorehms.huawei.feature_appgallerykit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.hms.explorehms.Util;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.jos.AppUpdateClient;
import com.huawei.hms.jos.JosApps;
import com.huawei.updatesdk.service.appmgr.bean.ApkUpgradeInfo;
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack;
import com.huawei.updatesdk.service.otaupdate.UpdateKey;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_features_appgallerykit);
        setupToolbar();
        checkUpdate();
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.pager);

        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);


        TabLayout tabLayout = findViewById(R.id.tabDots);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //no naming for the tabs
                    }
                }
        ).attach();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.link_app_gallery_kit_documentation_introduction));
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    private void checkUpdate() {
        AppUpdateClient client = JosApps.getAppUpdateClient(this);
        client.checkAppUpdate(this, new UpdateCallBack(this));
    }


    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            //0 index based.
            if (position == 0)
                return new GeneralFeaturesFragment();
            else if (position == 1)
                return new AppGalleryKitAppFragment();
            else if (position == 2)
                return new AppGalleryKitGameFragment();
            else if (position == 3)
                return new InitializeAppFragment();
            else if (position == 4)
                return new UpdateAppFragment();
            else
                return new ScreenSlidePageFragment(); //placeholder, it should never enter here*/
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    private static class UpdateCallBack implements CheckUpdateCallBack {
        private WeakReference<MainActivity> weakMainActivity;

        private UpdateCallBack(MainActivity apiActivity) {
            this.weakMainActivity = new WeakReference<>(apiActivity);
        }


        public void onUpdateInfo(Intent intent) {
            if (intent != null) {
                MainActivity apiActivity = null;
                if (weakMainActivity != null && weakMainActivity.get() != null) {
                    apiActivity = weakMainActivity.get();
                }
                // Obtain the update status code. Default_value indicates the default return code when status cannot be obtained, which is determined by the app.
                int status = intent.getIntExtra(UpdateKey.STATUS, 100);
                // Error code. You are advised to record it.
                int rtnCode = intent.getIntExtra(UpdateKey.FAIL_CODE, 200);
                // Failure information. You are advised to record it.
                String rtnMessage = intent.getStringExtra(UpdateKey.FAIL_REASON);
                Serializable info = intent.getSerializableExtra(UpdateKey.INFO);
                // Check whether the app has an update by checking whether info obtained is of the ApkUpgradeInfo type.
                // Call the showUpdateDialog API to display the update pop-up. The demo has an independent button for displaying the pop-up. Therefore, this API is not called here. For details, please refer to the checkUpdatePop() method.
                if (info instanceof ApkUpgradeInfo && apiActivity != null) {

                    AppUpdateClient client = JosApps.getAppUpdateClient(apiActivity);
                    //Force Update option is selected as false.
                    client.showUpdateDialog(apiActivity, (ApkUpgradeInfo) info, false);
                    Log.i("AppGalleryKit", "checkUpdatePop success");
                }
                if (apiActivity != null) {
                    //status --> 3: constant value NO_UPGRADE_INFO, indicating that no update is available.
                    Log.i("AppGalleryKit", "onUpdateInfo status: " + status + ", rtnCode: " + rtnCode + ", rtnMessage: " + rtnMessage);
                }
            }
        }

        @Override
        public void onMarketInstallInfo(Intent intent) {
            //onMarketInstallInfo
        }

        @Override
        public void onMarketStoreError(int i) {
            //onMarketStoreError
        }

        @Override
        public void onUpdateStoreError(int i) {
            //onUpdateStoreError
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}