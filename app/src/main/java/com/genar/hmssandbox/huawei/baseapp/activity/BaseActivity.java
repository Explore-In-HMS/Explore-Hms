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

package com.genar.hmssandbox.huawei.baseapp.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.Util;

public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(inflateLayout());
        initToolbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    public abstract int getLayoutRes();
    protected abstract String getToolbarTitle();

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected abstract String getDocumentationLink();

    protected int getToolbarLayout(){
        return R.layout.toolbar;
    }

    private View inflateLayout() {
        View contentView = getLayoutInflater().inflate(getLayoutRes(), null, false);

        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        attachToolbarToLayout(getLayoutInflater(), (ViewGroup) contentView);
        ((ViewGroup) contentView).getChildAt(1).setPadding(0,actionBarHeight,0,0);

        return contentView;
    }

    private void attachToolbarToLayout(LayoutInflater inflater, ViewGroup parent){
        mToolbar = (Toolbar) inflater.inflate(getToolbarLayout(),parent,false);

        parent.addView(mToolbar,0);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(10f);
        }

        getSupportActionBar().setTitle(getToolbarTitle());

        ImageView ivInfo = findViewById(R.id.ivInfo);

        ivInfo.setOnClickListener(v -> Util.openWebPage(this, getDocumentationLink()));
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }
}