package com.hms.explorehms;

import androidx.fragment.app.Fragment;

import com.kcode.lib.dialog.UpdateActivity;

public class CustomsUpdateActivity extends UpdateActivity {
    @Override
    protected Fragment getUpdateDialogFragment() {
        return CustomsUpdateFragment.newInstance(mModel,"当前已经是最新版本");
    }
}
