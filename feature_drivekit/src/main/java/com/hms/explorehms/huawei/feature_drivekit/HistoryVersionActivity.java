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

package com.hms.explorehms.huawei.feature_drivekit;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_drivekit.model.DriveHelper;
import com.hms.explorehms.huawei.feature_drivekit.model.HistoryVersionAdapter;
import com.github.clemp6r.futuroid.Async;
import com.github.clemp6r.futuroid.Future;
import com.google.android.material.textview.MaterialTextView;
import com.huawei.cloud.services.drive.model.File;
import com.huawei.cloud.services.drive.model.HistoryVersion;

import java.util.List;
import java.util.Objects;

public class HistoryVersionActivity extends AppCompatActivity {

    private HistoryVersionAdapter historyVersionAdapter;
    private List<HistoryVersion> historyVersions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_version);
        setupToolbar();
        initUI();

    }

    private void initUI() {
        MaterialTextView tvNoVersionFound = findViewById(R.id.lbl_noversionfound);
        DriveHelper dHelper = new DriveHelper();
        File selectedFile = DriveHelper.getSelectedFile();

        Future<List<HistoryVersion>> result = Async.submit(() -> dHelper.getHistoryVersionList(selectedFile.getId()));

        result.addSuccessCallback(result1 -> {
            if (result1 != null && result1.size() > 0) {
                tvNoVersionFound.setVisibility(View.GONE);
                historyVersions = result1;

                RecyclerView rv = findViewById(R.id.rv_historyVersions);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoryVersionActivity.this);
                rv.setLayoutManager(layoutManager);
                historyVersionAdapter = new HistoryVersionAdapter(HistoryVersionActivity.this, historyVersions, dHelper);
                rv.setAdapter(historyVersionAdapter);
            } else {
                tvNoVersionFound.setVisibility(View.VISIBLE);
            }
        });

        result.addFailureCallback(t -> Toast.makeText(HistoryVersionActivity.this, "Network Error! Try again later!", Toast.LENGTH_SHORT).show());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}