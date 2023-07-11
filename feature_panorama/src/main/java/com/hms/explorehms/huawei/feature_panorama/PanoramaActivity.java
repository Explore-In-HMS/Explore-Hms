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

package com.hms.explorehms.huawei.feature_panorama;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hms.explorehms.Util;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.panorama.Panorama;
import com.huawei.hms.panorama.PanoramaInterface;
import com.huawei.hms.support.api.client.ResultCallback;


public class PanoramaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = "PanoramaActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panorama);
        setupToolbar();

        Button mButtonDisplayInHms = findViewById(R.id.buttonInHms);
        mButtonDisplayInHms.setOnClickListener(this);
        Button mButtonDisplayInHmsRing = findViewById(R.id.buttonInHmsRing);
        mButtonDisplayInHmsRing.setOnClickListener(this);
        Button mButtonDisplayInAppSpherical = findViewById(R.id.buttonInAppSpherical);
        mButtonDisplayInAppSpherical.setOnClickListener(this);
        Button mButtonDisplayInAppVideo = findViewById(R.id.buttonInAppVideo);
        mButtonDisplayInAppVideo.setOnClickListener(this);

        checkPermission();
    }

    private void setupToolbar() {
        Toolbar toolBar;
        toolBar = findViewById(R.id.toolbar_panorama);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, "https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/introduction-0000001050042264");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.buttonInHms:
            case R.id.buttonInHmsRing:
                displayInHms(view.getId());
                break;
            case R.id.buttonInAppSpherical:
            case R.id.buttonInAppVideo:
                displayInApp(view.getId());
                break;
            default:
                break;
        }
    }

    private class ResultCallbackImpl implements ResultCallback<PanoramaInterface.ImageInfoResult> {
        @Override
        public void onResult(PanoramaInterface.ImageInfoResult panoramaResult) {
            if (panoramaResult == null) {
                logAndToast("panoramaResult is null");
                return;
            }

            if (panoramaResult.getStatus().isSuccess()) {
                Intent intent = panoramaResult.getImageDisplayIntent();
                if (intent != null) {
                    startActivity(intent);
                } else {
                    logAndToast("unknown error, view intent is null");
                }
            } else {
                logAndToast("error status : " + panoramaResult.getStatus());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "onActivityResult: received!");
        if (requestCode != 10001 || resultCode != Activity.RESULT_OK) {
            logAndToast("onActivityResult requestCode or resultCode invalid");
            return;
        }

        if (data == null) {
            logAndToast("onActivityResult data is null");
            return;
        }

        Panorama.getInstance().loadImageInfoWithPermission(
                this, data.getData(), PanoramaInterface.IMAGE_TYPE_SPHERICAL)
                .setResultCallback(new ResultCallbackImpl());
    }

    private void displayInHms(int id) {

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pano);
        switch (id) {
            case R.id.buttonInHms:
                Panorama.getInstance().loadImageInfo(this, uri).setResultCallback(new ResultCallbackImpl());
                break;
            case R.id.buttonInHmsRing:
                Panorama.getInstance()
                        .loadImageInfo(this, uri, PanoramaInterface.IMAGE_TYPE_RING)
                        .setResultCallback(new ResultCallbackImpl());
                break;
            default:
                logAndToast("displayInHms invalid id " + id);
                break;
        }
    }

    private void displayInApp(int id) {
        Intent intent = new Intent(PanoramaActivity.this, LocalDisplayActivity.class);
        intent.putExtra("ViewId", id);
        startActivity(intent);
    }

    private void logAndToast(String message) {
        Log.e(LOG_TAG, message);
        Toast.makeText(PanoramaActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Log.i(LOG_TAG, "permission ok");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

}