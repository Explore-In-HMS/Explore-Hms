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
package com.hms.explorehms.huawei.feature_fidokit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.Util;
import com.google.android.material.button.MaterialButton;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

public class FidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fido);

        setupToolbar();

        MaterialButton fido2Button = findViewById(R.id.fido2_button);
        MaterialButton bioAuthnButton = findViewById(R.id.bio_authn_button);

        fido2Button.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Fido2ClientActivity.class);
            startActivity(intent);
        });
        bioAuthnButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), BioAuthnAndxActivity.class);
            startActivity(intent);
        });

    }

    private void setupToolbar() {
        Toolbar toolBar = findViewById(R.id.toolbar_fido);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, getString(R.string.url_txt_fido));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}