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

package com.hms.explorehms.huawei.feature_awarenesskit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.ActivityAwarenessKitReworkInfoBinding;
import com.hms.explorehms.locationkit.LocationKitActivity;
import com.hms.explorehms.locationkit.Utils;


public class AwarenessKitReworkInfoActivity extends AppCompatActivity {

    private ActivityAwarenessKitReworkInfoBinding binding;
    private static final String TAG = AwarenessKitReworkInfoActivity.class.getSimpleName();
    private static final int permissionRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAwarenessKitReworkInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        statusCheck();
        initUI();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarAwarenessKitInfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, (getResources().getString(R.string.awarenesskit_more_information_link_documentation_link)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        binding.tvAdvantagesInfoAwarenesskit.setText(getResources().getString(R.string.advantages_awarenesskit));
        binding.tvCaptureBarrierInfoAwarenesskit.setText(getResources().getString(R.string.capture_barrier_info_awarenesskit));

        binding.btnLetsStartAwarenesskit.setOnClickListener(view ->
                startActivity
                        (new Intent(
                                AwarenessKitReworkInfoActivity.this,
                                AwarenessKitReworkMainActivity.class)
                        )
        );
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showNeedPermissionWarning();

        }
    }

    public void showNeedPermissionWarning() {
        Utils.showDialogGpsWarning(this,
                "Location is NULL!\nNEED GPS Settings Check",
                getString(com.hms.explorehms.R.string.permissionSettings),
                com.hms.explorehms.R.drawable.icon_settings_loc,
                "You can not use Location Features without GPS!",
                getString(com.hms.explorehms.R.string.yesGo), getString(com.hms.explorehms.R.string.cancel));
    }
}