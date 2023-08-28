package com.kcode.lib.base;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public abstract class AbstractUpdateActivity extends AppCompatActivity {
    protected abstract Fragment getUpdateDialogFragment();

    protected abstract Fragment getDownLoadDialogFragment();
}
