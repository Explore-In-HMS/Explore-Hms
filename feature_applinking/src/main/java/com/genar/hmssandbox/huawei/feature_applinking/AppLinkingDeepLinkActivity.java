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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.applinking.AGConnectAppLinking;
import com.huawei.agconnect.applinking.ResolvedLinkData;

public class AppLinkingDeepLinkActivity extends AppCompatActivity {

    private static final String TAG = "AppLinkingDeepLinkActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_linking_deep_link);


        AGConnectAppLinking.getInstance().getAppLinking(this).addOnSuccessListener(resolvedLinkData -> {

            Uri deepLink = resolvedLinkData.getDeepLink();
            ResolvedLinkData.LinkType linkType=resolvedLinkData.getLinkType();
            String installSource=resolvedLinkData.getInstallSource();
            String className=deepLink.getPath().replace('/', ' ').trim();

            try {
                startActivity(new Intent(this, Class.forName(className)));
                toast(getResources().getString(R.string.deeplink_success_message)+"link Type:"+linkType.name()+"Source:"+installSource);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                toast(getResources().getString(R.string.deeplink_failed_message));
            }

        }).addOnFailureListener(e -> Log.e(TAG, "Message: " + e.getMessage()));

    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}