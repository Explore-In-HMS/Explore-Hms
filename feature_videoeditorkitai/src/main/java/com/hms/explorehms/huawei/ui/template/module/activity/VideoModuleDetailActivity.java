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

package com.hms.explorehms.huawei.ui.template.module.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.hms.explorehms.huawei.ui.common.utils.GsonUtils;
import com.hms.explorehms.huawei.ui.common.view.navigator.FixFragmentNavigator;
import com.hms.explorehms.huawei.ui.template.bean.Constant;
import com.hms.explorehms.huawei.ui.template.bean.MaterialData;
import com.hms.explorehms.huawei.ui.template.bean.TemplateProjectBean;
import com.hms.explorehms.huawei.ui.template.viewmodel.ModuleEditViewModel;
import com.hms.explorehms.huawei.feature_videoeditorkitai.R;
import com.huawei.secure.android.common.intent.SafeIntent;

import java.util.List;

public class VideoModuleDetailActivity extends BaseActivity {
    private String mName;

    private String mDescription;

    private ModuleEditViewModel mModuleEditViewModel;

    private TemplateProjectBean templateProjectBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_detail);
        SafeIntent intent = new SafeIntent(getIntent());
        String templatedetail = intent.getStringExtra(Constant.TEMPLATE_KEY_DETAIL);
        templateProjectBean = GsonUtils.fromJson(templatedetail, TemplateProjectBean.class);
        initView(intent, templatedetail);
        initObject(intent);
    }

    private void initView(SafeIntent safeIntent, String templatedetail) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_module_detail);
        if (fragment != null) {
            NavController navController = NavHostFragment.findNavController(fragment);
            NavigatorProvider provider = navController.getNavigatorProvider();
            FixFragmentNavigator fragmentNavigator =
                new FixFragmentNavigator(this, fragment.getChildFragmentManager(), fragment.getId());
            provider.addNavigator(fragmentNavigator);

            Bundle bundle = safeIntent.getExtras();
            if (bundle != null) {
                bundle.putString(Constant.TEMPLATE_KEY_DETAIL, templatedetail);
                navController.setGraph(R.navigation.nav_graph_video_module_detail_t, bundle);
            }
        }
    }

    private void initObject(SafeIntent intent) {
        mModuleEditViewModel = new ViewModelProvider(this, mFactory).get(ModuleEditViewModel.class);
        mName = intent.getStringExtra(TemplateDetailActivity.NAME);
        mDescription = intent.getStringExtra(TemplateDetailActivity.DESCRIPTION);

        List<MaterialData> mMaterialDataList = intent.getParcelableArrayListExtra(Constant.EXTRA_MODULE_SELECT_RESULT);
        if (mMaterialDataList != null) {
            mModuleEditViewModel.initData(mMaterialDataList);

            if (templateProjectBean != null) {
                mModuleEditViewModel.updateDataProject(templateProjectBean);
            }
        }
    }

    public String getName() {
        return mName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mModuleEditViewModel.getEditor() != null) {
            mModuleEditViewModel.getEditor().stopEditor();
            mModuleEditViewModel.setEditor(null);
        }
    }
}