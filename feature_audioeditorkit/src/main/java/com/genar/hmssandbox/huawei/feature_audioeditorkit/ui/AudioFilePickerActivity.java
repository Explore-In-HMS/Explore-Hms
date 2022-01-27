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

package com.genar.hmssandbox.huawei.feature_audioeditorkit.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.feature_audioeditorkit.util.FileUtils;
import com.huawei.hms.audioeditor.sdk.HAEConstant;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @since 2021-05-10
 */
public class AudioFilePickerActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performFileSearch();
    }


    private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    List<Uri> uris = new ArrayList<>();

                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        uris.add(uri);
                    } else {

                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                uris.add(clipData.getItemAt(i).getUri());
                            }
                        }
                    }
                    handleSelectedAudios(uris);
                }
                finish();
                break;
            }
            default:
                break;
        }
    }

    private void handleSelectedAudios(List<Uri> uriList) {
        if (uriList == null || uriList.size() == 0) {
            return;
        }
        ArrayList<String> audioList = new ArrayList<>();
        for (Uri uri : uriList) {
            String filePath = FileUtils.getRealPath(this, uri);
            audioList.add(filePath);
        }
        Intent intent = new Intent();
        intent.putExtra(HAEConstant.AUDIO_PATH_LIST, audioList);
        this.setResult(HAEConstant.RESULT_CODE, intent);
        finish();
    }
}
