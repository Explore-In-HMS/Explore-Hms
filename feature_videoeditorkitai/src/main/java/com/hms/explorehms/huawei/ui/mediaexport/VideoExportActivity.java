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

package com.hms.explorehms.huawei.ui.mediaexport;

import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.EDITOR_UUID;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.EXPORT_TYPE_TAG;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.NORMAL_EXPORT_TYPE;
import static com.hms.explorehms.huawei.ui.mediaexport.model.ExportConstants.SOURCE;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.hms.explorehms.huawei.ui.common.BaseActivity;
import com.hms.explorehms.huawei.ui.common.listener.OnClickRepeatedListener;
import com.hms.explorehms.huawei.ui.common.view.navigator.FixFragmentNavigator;
import com.hms.explorehms.huawei.ui.mediaexport.fragment.ExportFragment;
import com.hms.explorehms.huawei.ui.mediaexport.viewmodel.ExportViewModel;
import com.hms.explorehms.huawei.ui.mediaexport.viewmodel.SettingViewModel;
import com.hms.explorehms.huawei.utils.SmartLog;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;
import com.huawei.secure.android.common.intent.SafeIntent;

/**
 * 导出 Activity
 *
 * @since 2022.04.26
 */
public class VideoExportActivity extends BaseActivity {
    private static final String TAG = "ExportActivity";

    private SettingViewModel settingViewModel;

    private ExportViewModel exportViewModel;

    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusNavigateBg();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        initView();
        initObject();
        initNavigate();
        initData();
        initEvent();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
    }

    private void initObject() {
        settingViewModel = new ViewModelProvider(this, factory).get(SettingViewModel.class);
        exportViewModel = new ViewModelProvider(this, factory).get(ExportViewModel.class);
    }

    private void initNavigate() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_export);
        if (fragment != null) {
            NavController navController = NavHostFragment.findNavController(fragment);
            NavigatorProvider provider = navController.getNavigatorProvider();
            FixFragmentNavigator fragmentNavigator =
                new FixFragmentNavigator(this, fragment.getChildFragmentManager(), fragment.getId());
            provider.addNavigator(fragmentNavigator);
            navController.setGraph(R.navigation.nav_graph_export);
        }
    }

    private void initData() {
        SafeIntent intent = new SafeIntent(getIntent());
        int exportType = intent.getIntExtra(EXPORT_TYPE_TAG, NORMAL_EXPORT_TYPE);
        SmartLog.d(TAG, "export type " + exportType);
        cacheCommonData(intent);
    }

    private void initEvent() {
        mIvBack.setOnClickListener(new OnClickRepeatedListener(v -> onBackPressed()));
    }

    @Override
    public void onBackPressed() {
        if (exportViewModel.isExporting()) {
            showExportStopDialog();
        } else {
            finish();
        }
    }

    private void setStatusNavigateBg() {
        //statusBarColor = R.color.export_bg;
        //navigationBarColor = R.color.export_bg;
    }

    private void cacheCommonData(SafeIntent intent) {
        settingViewModel.setExportType(intent.getIntExtra(EXPORT_TYPE_TAG, NORMAL_EXPORT_TYPE));
        settingViewModel.setEditUuid(intent.getStringExtra(EDITOR_UUID));
        settingViewModel.setSource(intent.getStringExtra(SOURCE));
    }

    private void showExportStopDialog() {
        Fragment navigationFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
        FragmentManager fragmentManager = null;
        if (navigationFragment != null) {
            fragmentManager = navigationFragment.getChildFragmentManager();
        }
        Fragment fragment = null;
        if (fragmentManager != null) {
            fragment = fragmentManager.getPrimaryNavigationFragment();
        }

        if (fragment instanceof ExportFragment) {
            ((ExportFragment) fragment).showExportStopDialog();
        }
    }
}