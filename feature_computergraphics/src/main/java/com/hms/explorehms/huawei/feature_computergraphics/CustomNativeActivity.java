package com.hms.explorehms.huawei.feature_computergraphics;

import android.app.NativeActivity;
import android.os.Bundle;

public class CustomNativeActivity extends NativeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onBackPressed();
    }
}
