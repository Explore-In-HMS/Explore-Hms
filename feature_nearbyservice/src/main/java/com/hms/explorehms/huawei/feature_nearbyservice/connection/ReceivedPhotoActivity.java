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

package com.hms.explorehms.huawei.feature_nearbyservice.connection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.hms.explorehms.huawei.feature_nearbyservice.R;
import com.hms.explorehms.huawei.feature_nearbyservice.connection.utils.ToastUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReceivedPhotoActivity extends Activity implements AdapterView.OnItemClickListener,
        View.OnClickListener, ChatActivity.ReceivedFileListener {

    private static final String APP_PROVIDER = "com.hms.explorehms.huawei.provider";

    private static final String TAG = "ReceivedPhotoActivity";
    /**
     * 显示图片的GridView
     */
    private GridView gvPhoto;
    /**
     * 文件夹下所有图片
     */
    private File[] mImages;

    private List<File> mImageList = new ArrayList<>();
    /**
     * 显示图片的适配器
     */
    private PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_photo);
        initView();
        initData();
    }

    private void initData() {
        String folderPath = Environment.getExternalStorageDirectory().getPath() + "/Download/Nearby";
        mImages = getImages(folderPath);
        if (mImages == null || mImages.length == 0) {
            ToastUtil.showShortToast(getApplicationContext(), "Get file is null");
            return;
        }
        List<File> list = Arrays.asList(mImages);
        mImageList.addAll(list);
        adapter = new PhotoAdapter(mImageList, this);
        gvPhoto.setAdapter(adapter);
    }

    private void initView() {
        ChatActivity mService = ChatActivity.getService();
        gvPhoto = findViewById(R.id.gv_photo);
        gvPhoto.setOnItemClickListener(this);
        findViewById(R.id.btn_sync).setOnClickListener(this);
        mService.setReceivedFileListener(this);
    }

    private File[] getImages(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            return folder.listFiles(new MyFileFilter());
        }

        return new File[0];
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String filePath = mImages[position].getPath();
        Uri uri;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (Build.VERSION.SDK_INT >= 24) {
            Log.i(TAG, file.length() + "");
            uri = FileProvider.getUriForFile(this, APP_PROVIDER, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        String endsWith = file.getName().substring(file.getName().lastIndexOf(".")+1);
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
            case "mp4":
                intent.setDataAndType(uri, "video/mp4");
                break;
            case "doc":
            case "docx":
            case "txt":
                intent.setDataAndType(uri, "text/*");
                break;
            default: //default case
                break;
        }
        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No activity found handle this intent", Toast.LENGTH_SHORT).show();
        } else {
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onClick(View v) {
        //同步照片
        if (v.getId() == R.id.btn_sync) {
            Log.d(TAG, "click btn_sync");
        }
    }

    @Override
    public void receivedFile(File file) {
        boolean isExist = false;
        Log.e(TAG, "file.path==========" + file.getPath());
        for (File file1 : mImageList) {
            Log.e(TAG, "file1.path==========" + file1.getPath());
            if (file.getPath().equalsIgnoreCase(file1.getPath())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            Log.e(TAG, "file.path1111111111111111==========" + file.getPath());
            mImageList.add(file);
            adapter.updateData(mImageList);
        }
    }

    static class MyFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            String name = file.getName();
            return name.endsWith("jpg") || name.endsWith("png") || name.endsWith("gif")
                    || name.endsWith("jpeg") || name.endsWith("bmp") || name.endsWith("pdf") || name.endsWith("mp4") || name.endsWith("doc")
                    || name.endsWith("txt") || name.endsWith("docx");
        }
    }
}