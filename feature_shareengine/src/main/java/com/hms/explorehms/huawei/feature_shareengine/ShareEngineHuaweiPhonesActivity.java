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

package com.hms.explorehms.huawei.feature_shareengine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.hms.explorehms.Util;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ShareEngineHuaweiPhonesActivity extends AppCompatActivity {


    private static final String TAG = "ShareEngineHuaweiPhones";
    private static final int PERMISSION_CODE = 101;
    private static final int FILE_SELECT_CODE = 102;


    MaterialButton btnShareText;
    MaterialButton btnShareFile;
    EditText edtShareTextInput;
    SwitchCompat swtFileFiles;


    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_engine_huawei_phones);
        setupToolbar();

        btnShareText = findViewById(R.id.btnShareEngineShareText);
        btnShareFile = findViewById(R.id.btnShareEngineShareFile);
        edtShareTextInput = findViewById(R.id.edit_text);
        swtFileFiles = findViewById(R.id.switchFileFilesShareEngine);


        String externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ActivityCompat.checkSelfPermission(this, externalStoragePermission) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{externalStoragePermission}, PERMISSION_CODE);

        if (Boolean.TRUE.equals(initShareProperties())) {
            //button listener for text sharing.
            btnShareText.setOnClickListener(view -> {
                if (edtShareTextInput.getEditableText().toString().isEmpty())
                    edtShareTextInput.setError("Field cannot be empty.");
                else {
                    String textInput = edtShareTextInput.getEditableText().toString();
                    shareTextContent(textInput);
                }
            });
            //button listener for file/files sharing
            btnShareFile.setOnClickListener(view -> showFileChooser());
        }

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.title_between_huawei);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getString(R.string.url_shareengine));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Permission control for external storage.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE && grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * We are creating intent instance for start sharing. Text and file sharing uses same
     * configuration so of that we created one intent instance.
     *
     * @return
     */
    private Boolean initShareProperties() {
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*");
        shareIntent.setPackage("com.huawei.android.instantshare");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager manager = getApplicationContext().getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(shareIntent, 0);

        if (infos.size() == 0) {
            Log.d(TAG, "share via intent not supported");
            return false;
        } else
            return true;
    }

    /**
     * We are sending text file via on intent without any package for Huawei Phones.
     *
     * @param input
     */
    private void shareTextContent(String input) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, input);
        getApplicationContext().startActivity(shareIntent);
    }

    /**
     * We are creating creating intent for file selection.
     * If user wants to share multiple file we add different extra into our intent.
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (swtFileFiles.isChecked()) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * In here, we are getting our files from phone storage and we are sending an array our share
     * function. If user wants to share file or multiple files we arrange the array in here.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            // Get the Uri of the selected file
            ArrayList<Uri> list = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                int currentItem = 0;
                while (currentItem < count) {
                    Uri uri = data.getClipData().getItemAt(currentItem).getUri();
                    list.add(uri);
                    currentItem = currentItem + 1;
                }
            } else {
                Uri uri = data.getData();
                list.add(uri);
            }
            shareFileFiles(list);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This functions gets an array. This array might contains a file or multiple files.
     * If array only contains a file we are start sharing via on intent that we created before.
     * If array contains more than one file we are creating new intent object for sharing.
     *
     * @param files
     */
    private void shareFileFiles(ArrayList<Uri> files) {

        if (files.isEmpty())
            return;

        if (!swtFileFiles.isChecked()) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, files.get(0));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getApplicationContext().startActivity(shareIntent);

        } else {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("*/*");
            intent.setPackage("com.huawei.android.instantshare");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            PackageManager mPackageManager = getApplicationContext().getPackageManager();
            List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentActivities(intent, 0);
            if (resolveInfoList.size() > 0) {
                getApplicationContext().startActivity(intent);
            }

        }
    }

}