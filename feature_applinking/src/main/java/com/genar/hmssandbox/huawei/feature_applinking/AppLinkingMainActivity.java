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
package com.genar.hmssandbox.huawei.feature_applinking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.google.android.material.button.MaterialButton;
import com.huawei.agconnect.applinking.AGConnectAppLinking;
import com.huawei.agconnect.applinking.AppLinking;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;


//https://hmssandboxdev.dre.agconnect.link/FXbP

public class AppLinkingMainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = "AppLinking";

    private EditText etSocialCardTitle;
    private EditText etSocialCardDescription;
    private SwitchCompat swShortLink;
    private TextView tvAppLink;
    private ProgressBar progressBar;
    private String[] packageNames;
    private int selectedPathIndex = 0;
    private SwitchCompat iosLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_linking_main);
        Toolbar toolBar = findViewById(R.id.applinking);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, getResources().getString(R.string.url_txt_applinking));
        initUIElements();
        AGConnectAppLinking.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUIElements() {


        String[] moduleNames = getResources().getStringArray(R.array.spinner_array);
        packageNames = getResources().getStringArray(R.array.package_names);

        progressBar = findViewById(R.id.progress_bar);
        etSocialCardTitle = findViewById(R.id.edSocialCardTitle);
        etSocialCardDescription = findViewById(R.id.etSocialCardDescription);
        swShortLink = findViewById(R.id.switchShortLink);
        iosLink=findViewById(R.id.switch_IOS);
        MaterialButton btnCreateLink = findViewById(R.id.btnCreateLink);
        btnCreateLink.setOnClickListener(this);

        tvAppLink = findViewById(R.id.tvAppLink);
        tvAppLink.setOnClickListener(this);

        Spinner pathSpinner = findViewById(R.id.spinnerModuleNames);
        pathSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, moduleNames);
        pathSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        selectedPathIndex = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //do nothing

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateLink:
                createLinkClicked();
                break;
            case R.id.tvAppLink:
                appLinkClicked();
                break;
            default: //default state
                break;
        }
    }

    private void createLinkClicked() {
        progressBar.setVisibility(View.VISIBLE);
        setLink(packageNames[selectedPathIndex]);
    }

    private void appLinkClicked() {
        if (!tvAppLink.getText().toString().isEmpty()) {
            String link = tvAppLink.getText().toString();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, link);

            startActivity(Intent.createChooser(intent, "Share App Linking"));
        }
    }

    private void setLink(String path) {

        boolean shortLinkEnabled = swShortLink.isChecked();
        boolean iosLinkEnabled=iosLink.isChecked();
        if(iosLinkEnabled){
            AppLinking.Builder builder = AppLinking.newBuilder();
            builder.setIsShowPreview(true);
            builder.setUriPrefix("https://hmssandboxdev.dre.agconnect.link");
            builder.setDeepLink(Uri.parse("https://developer.huawei.com/" + path));

            // Set IOS link behavior
            AppLinking.IOSLinkInfo.Builder behaviorBuilder = AppLinking.IOSLinkInfo.newBuilder();
            builder.setIOSLinkInfo(behaviorBuilder.build());

            if (twoInputEmptyValidator(etSocialCardTitle, etSocialCardDescription)) {

                AppLinking.SocialCardInfo socialCardInfo = AppLinking.SocialCardInfo.newBuilder()
                        .setTitle(etSocialCardTitle.getEditableText().toString().trim())
                        .setImageUrl("https://developer.huawei.com/Enexport/sites/default/images/logo.png")
                        .setDescription(etSocialCardDescription.getEditableText().toString().trim())
                        .build();
                builder.setSocialCardInfo(socialCardInfo);

                if (shortLinkEnabled) {
                    builder.buildShortAppLinking().addOnSuccessListener(shortAppLinking -> tvAppLink.setText(shortAppLinking.getShortUrl().toString())).addOnFailureListener(e -> Log.e(TAG, "Message: " + e.getMessage() + e.getCause()));
                } else {
                    AppLinking appLink = builder.buildAppLinking();
                    tvAppLink.setText(appLink.getUri().toString());

                }
            }
            progressBar.setVisibility(View.INVISIBLE);

        }
        else {
            AppLinking.Builder builder = AppLinking.newBuilder();
            builder.setUriPrefix("https://hmssandboxdev.dre.agconnect.link");
            builder.setDeepLink(Uri.parse("https://developer.huawei.com/" + path));

            // Set Android link behavior
            AppLinking.AndroidLinkInfo.Builder behaviorBuilder = AppLinking.AndroidLinkInfo.newBuilder();
            // Set Min Version if user app's version less than version number or users direct to AppGallery
            behaviorBuilder.setOpenType(AppLinking.AndroidLinkInfo.AndroidOpenType.AppGallery);
            builder.setAndroidLinkInfo(behaviorBuilder.build());

            if (twoInputEmptyValidator(etSocialCardTitle, etSocialCardDescription)) {

                AppLinking.SocialCardInfo socialCardInfo = AppLinking.SocialCardInfo.newBuilder()
                        .setTitle(etSocialCardTitle.getEditableText().toString().trim())
                        .setImageUrl("https://developer.huawei.com/Enexport/sites/default/images/logo.png")
                        .setDescription(etSocialCardDescription.getEditableText().toString().trim())
                        .build();
                builder.setSocialCardInfo(socialCardInfo);

                if (shortLinkEnabled) {
                    builder.buildShortAppLinking().addOnSuccessListener(shortAppLinking -> tvAppLink.setText(shortAppLinking.getShortUrl().toString())).addOnFailureListener(e -> Log.e(TAG, "Message: " + e.getMessage() + e.getCause()));
                } else {
                    AppLinking appLink = builder.buildAppLinking();
                    tvAppLink.setText(appLink.getUri().toString());

                }
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

    private Boolean twoInputEmptyValidator(EditText editText1, EditText editText2) {

        int temp = 0;

        if (editText1.getEditableText().toString().trim().isEmpty()) {
            editText1.setError("Please fill the empty area.");
            temp++;
        }

        if (editText2.getEditableText().toString().trim().isEmpty()) {
            editText2.setError("Please fill the empty area.");
            temp++;
        }

        return temp == 0;
    }
}