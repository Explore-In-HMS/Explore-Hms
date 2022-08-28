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

package com.hms.explorehms.huawei.feature_acceleratekit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_acceleratekit.adapter.AccelerateKitAdapter;
import com.hms.explorehms.huawei.feature_acceleratekit.dialog.ProgressDialogScreen;
import com.hms.explorehms.huawei.feature_acceleratekit.databinding.ActivityAccelerateKitMainBinding;
import com.hms.explorehms.huawei.feature_acceleratekit.model.ListItemAccelerateKit;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AccelerateKitMainActivity extends AppCompatActivity {


    private ActivityAccelerateKitMainBinding binding;
    private AccelerateKitAdapter adapter;

    private List<ListItemAccelerateKit> adapterData;
    private ProgressDialogScreen progressDialogScreen;

    static {
        //Used to import native 'native-lib' C++ library which is under the cpp folder
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccelerateKitMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();

        initUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_acceleratekit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_txt_acceleratekit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI() {
        adapterData = new ArrayList<>();
        adapter = new AccelerateKitAdapter(adapterData, this);

        binding.recyclerViewAccelerateKit.setAdapter(adapter);


        progressDialogScreen = new ProgressDialogScreen(this);

        binding.btnStartThread.setOnClickListener(view -> {
            if (!validator(binding.edtThreadCountAcceleratekit.getEditableText().toString().trim())) {
                binding.edtThreadCountAcceleratekit.setError("Enter a valid value\n Value must be between 0-20");
            } else {
                progressDialogScreen.showProgressDialog();
                calculatePI(Integer.parseInt(binding.edtThreadCountAcceleratekit.getEditableText().toString().trim()));
            }
        });
    }


    private void calculatePI(int threadCount) {
        long start = System.currentTimeMillis();
        String calculatedValue = stringFromJNI(threadCount);
        long difference = System.currentTimeMillis() - start;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(difference);


        ListItemAccelerateKit tempItem = new ListItemAccelerateKit(threadCount, String.valueOf(seconds) + " (" + difference + ")");
        binding.tvPIResultAcceleratekit.setText(this.getResources().getString(R.string.accelerate_kit_pi_result, calculatedValue));
        adapterData.add(tempItem);
        adapter.updateData(adapterData);
        progressDialogScreen.dismissProgressDialog();
    }

    //Used to check ifthread count is between 0-20
    private boolean validator(String input) {
        if (!input.isEmpty()) {
            return Integer.parseInt(input) >= 0 && Integer.parseInt(input) <= 20;
        } else {
            return false;
        }
    }

    // C++ library function decleration
    public native String stringFromJNI(int threadCount);

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}