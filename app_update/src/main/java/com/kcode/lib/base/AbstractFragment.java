package com.kcode.lib.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
public abstract class AbstractFragment extends DialogFragment {

    protected abstract int getLayout();

    protected abstract void setContent(View view, int contentId);

    protected abstract void initView(View view);

    protected abstract void bindUpdateListener(View view, int updateId);

    protected abstract void bindCancelListener(View view, int cancelId);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }
}
