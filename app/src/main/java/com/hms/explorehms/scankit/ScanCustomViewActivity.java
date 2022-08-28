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

package com.hms.explorehms.scankit;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.R;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.hmsscankit.RemoteView;

import java.util.Objects;

import static com.huawei.hms.ml.scan.HmsScanBase.ALL_SCAN_TYPE;

public class ScanCustomViewActivity extends AppCompatActivity {


    //declare RemoteView instance
    private RemoteView remoteView;
    //declare the key ,used to get the value returned from scankit
    public static final String SCAN_RESULT = "scanResult";

    int mScreenWidth;
    int mScreenHeight;
    //scan_view_finder width & height is  300dp
    static final int SCAN_FRAME_SIZE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scan_custom_view);

        setupToolbar();
        //1.get screen density to caculate viewfinder's rect
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;
        //2.get screen size
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);

        //3.caculate viewfinder's rect,it's in the middle of the layout
        //set scanning area(Optional, rect can be null,If not configure,default is in the center of layout)
        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = mScreenHeight / 2 - scanFrameSize / 2;
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2;


        //initialize RemoteView instance, and set calling back for scanning result
        remoteView = new RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(ALL_SCAN_TYPE).build();
        remoteView.onCreate(savedInstanceState);
        remoteView.setOnResultCallback(result -> {
            //judge the result is effective
            if (result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                Intent intent = new Intent();
                intent.putExtra(SCAN_RESULT, result[0]);
                setResult(RESULT_OK, intent);
                ScanCustomViewActivity.this.finish();
            }
        });

        //add remoteView to framelayout
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = findViewById(R.id.rim);
        frameLayout.addView(remoteView, params);

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_custom_view);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //manage remoteView lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}