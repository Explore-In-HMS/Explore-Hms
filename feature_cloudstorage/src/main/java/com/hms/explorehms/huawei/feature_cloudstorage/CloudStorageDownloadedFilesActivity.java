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

package com.hms.explorehms.huawei.feature_cloudstorage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hms.explorehms.huawei.feature_cloudstorage.databinding.ActivityCloudStorageDownloadedFilesBinding;
import com.hms.explorehms.huawei.feature_cloudstorage.fragments.adapter.DownloadedFilesAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudStorageDownloadedFilesActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    private ActivityCloudStorageDownloadedFilesBinding binding;
    private static final String PATH = "/Download/CloudStorage";
    private static final String APP_PROVIDER = "com.hms.explorehms.huawei.provider";
    private File[] mFiles;
    private String folderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCloudStorageDownloadedFilesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getStringExtra("folderName") != null) {
            folderName = getIntent().getStringExtra("folderName");
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,
                    CloudStorageMainActivity.class));
            finish();
        }

        binding.gvPhoto.setOnItemClickListener(this);
        binding.tvStorageInfoCloudstorage.setText(getResources().getString(R.string.downloaded_files_path_cloudstorage));
        setupToolbar();
        initData();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarDownloadedFilesCloudstorage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initData() {
        String folderPath = Environment.getExternalStorageDirectory().getPath() + PATH + getString(R.string.special_character) + folderName;
        mFiles = getImages(folderPath);
        if (mFiles == null || mFiles.length == 0) {
            Toast.makeText(this, "File is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        List<File> list = Arrays.asList(mFiles);
        List<File> mFileList = new ArrayList<>(list);
        DownloadedFilesAdapter adapter = new DownloadedFilesAdapter(mFileList, this);
        binding.gvPhoto.setAdapter(adapter);
    }

    private File[] getImages(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            return folder.listFiles(new MyFileFilter());
        }

        return new File[0];
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String filePath = mFiles[position].getPath();
        Uri uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        String filesCloudStorage = "DownloadedFilesCloudStorageDownloadedFilesCloudStorage";
        if (Build.VERSION.SDK_INT >= 24) {
            Log.i(filesCloudStorage, file.length() + "");
            uri = FileProvider.getUriForFile(this, APP_PROVIDER, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        String endsWith = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        switch (endsWith) {
            case "jpg":
            case "png":
            case "gif":
            case "jpeg":
            case "bmp":
                intent.setDataAndType(uri, "images/*");
                break;
            case "pdf":
                intent.setDataAndType(uri, "application/pdf");
                break;
            case "xls":
            case "xlsx":
                intent.setDataAndType(uri, "application/vnd.ms-excel");
                break;
            case "mp4":
                intent.setDataAndType(uri, "video/mp4");
                break;
            case "doc":
            case "docx":
            case "txt":
                intent.setDataAndType(uri, "text/*");
                break;
            default:
                Log.d(filesCloudStorage, "SwitchDefault");
        }
        try {
            PackageManager packageManager = this.getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, " No Intent available to handle action", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(filesCloudStorage, "startActivity error: " + e.getMessage());
        }
    }

    static class MyFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String name = file.getName();
            return name.endsWith("jpg") || name.endsWith("png") || name.endsWith("gif")
                    || name.endsWith("jpeg") || name.endsWith("bmp") || name.endsWith("pdf") ||
                    name.endsWith("mp4") || name.endsWith("doc")
                    || name.endsWith("txt") || name.endsWith("docx") || name.endsWith("xlsx") || name.endsWith("xls");
        }
    }
}