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
package com.hms.explorehms.huawei.feature_audiokit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_audiokit.databinding.ActivityAudioMainBinding;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class AudioMainActivity extends AppCompatActivity {

    private ActivityAudioMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAudioMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupToolbar();

        if(!checkReadPermissionBoolean())
            requestPermission();

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkReadPermissionBoolean()){
                    if(binding.radioGroup.getCheckedRadioButtonId()==-1)
                    {
                        showToast("Please select one playlist type");
                    }
                    else
                    {
                        // get selected radio button from radioGroup
                        int selectedId = binding.radioGroup.getCheckedRadioButtonId();
                        // find the radiobutton by returned id
                        RadioButton selectedRadioButton = findViewById(selectedId);

                        Intent i = new Intent(AudioMainActivity.this, LocalAudioActivity.class);

                        if(selectedRadioButton == binding.onlinePlaylist){
                            i.putExtra("playlistType", "online");
                            showToast("Online playlist is selected");
                            startActivity(i);
                        }
                        else{
                            i.putExtra("playlistType", "local");
                            showToast("Local playlist is selected");
                            startActivity(i);
                        }
                    }
                }
                else{
                    showToast("Please go back and come back again to give permission.");
                }
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar , getString(R.string.url_txt_audiokit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    public boolean checkReadPermissionBoolean() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Checking whether user granted the permission or not.
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //granted
            showToast("Permission granted! You can choose one playlist type now.");
        }
        else{
            showToast("Please go back and come again to give permission.");
        }
    }

    private void showToast(String toastText) {
        Toast.makeText(AudioMainActivity.this, toastText, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}