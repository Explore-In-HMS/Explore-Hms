/*
 * Copyright (c) Explore in HMS. 2012-2021. All rights reserved.
 */

package com.genar.hmssandbox.huawei.feature_modem5g_kit.hms5gkit.activitys.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;


public class BaseActivity extends AppCompatActivity implements BaseViewInterface {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected Bundle mSavedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityStack.getInstance().addActivity(this);

        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }

        mInflater = getLayoutInflater();
        mContext = this;
        mSavedInstanceState = savedInstanceState;

        init(savedInstanceState);
        initView();
        initData();
        regReceiver();
        initNav();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {

    }

    @Override
    public void regReceiver() {

    }

    @Override
    public void unRegReceiver() {

    }

    @Override
    public void initNav() {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Open an activity by default, do not close the current activity
     *
     * @param clz clz
     */
    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity, Bundle ex) {
        Intent intent = new Intent(this, clz);
        if (ex != null) {
            intent.putExtras(ex);
        }
        startActivity(intent);
        if (isCloseCurrentActivity) {
            ActivityStack.getInstance().finishActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getInstance().finishActivity(this);
        unRegReceiver();
        super.onDestroy();
    }

    protected int getLayoutId() {
        return 0;
    }
}
