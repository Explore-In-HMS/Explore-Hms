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
package com.genar.hmssandbox.huawei.feature_identitykit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.google.android.material.button.MaterialButton;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.identity.Address;
import com.huawei.hms.identity.entity.GetUserAddressResult;
import com.huawei.hms.identity.entity.UserAddress;
import com.huawei.hms.identity.entity.UserAddressRequest;
import com.huawei.hms.support.api.client.Status;

public class IdentityActivity extends AppCompatActivity {

    private static final String TAG = "IdentityKit";

    private static final int GET_ADDRESS = 1000;

    // TextView for displaying operation information on the UI
    private TextView addresslogInfo;

    // TextView for displaying user name, city, area, address, phone information on the UI
    private TextView userAddressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);
        Toolbar toolBar = findViewById(R.id.toolbar_identity);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, getString(R.string.url_txt_identitykit));
        userAddressTextView = findViewById(R.id.user_address);
        addresslogInfo = findViewById(R.id.identity_address_log_info);
        addresslogInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        // Button for displaying operation information on the UI
        MaterialButton queryUserAddress = findViewById(R.id.query_user_address);
        queryUserAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserAddress();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //1. Construct a UserAddressRequest object in new mode. Then, call the getUserAddress API.
    private void getUserAddress() {
        UserAddressRequest req = new UserAddressRequest();
        Task<GetUserAddressResult> task = Address.getAddressClient(this).getUserAddress(req);
        task.addOnSuccessListener(new OnSuccessListener<GetUserAddressResult>() {
            @Override
            public void onSuccess(GetUserAddressResult result) {
                Log.i(TAG, " onSuccess result code:" + result.getReturnCode());
                logger(TAG + " onSuccess result code:" + result.getReturnCode());
                try {
                    startActivityForResult(result);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, " on Failed result code:" + e.getMessage());
                logger(TAG + " on Failed result code:" + e.getMessage());
            }
        });
    }

    //2. Display the address selection page by calling the startResolutionForResult method of Status.
    private void startActivityForResult(GetUserAddressResult result) throws IntentSender.SendIntentException {
        Status status = result.getStatus();
        if (result.getReturnCode() == 0 && status.hasResolution()) {
            Log.i(TAG, "the result had resolution.");
            status.startResolutionForResult(this, 1000);
        } else {
            Log.i(TAG, "the result hasn't resolution.");
        }
    }

    //3. After the user selects an address, call the parseIntent method of UserAddress in
    // onActivityResult of the page to obtain the address from the returned result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == GET_ADDRESS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    UserAddress userAddress = UserAddress.parseIntent(data);
                    if (userAddress != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Adress: ");
                        sb.append("name:").append(userAddress.getName()).append(", ");
                        sb.append("city:").append(userAddress.getAdministrativeArea()).append(", ");
                        sb.append("area:").append(userAddress.getLocality()).append(",");
                        sb.append("address:").append(userAddress.getAddressLine1()).append(userAddress.getAddressLine2()).append(", ");
                        sb.append("phone:").append(userAddress.getPhoneNumber());
                        sb.append("CountryIso Code: ").append(userAddress.getCountryISOCode());
                        sb.append("Country Code: ").append(userAddress.getCountryCode());
                        Log.i(TAG, "user address is " + sb.toString());
                        userAddressTextView.setText(sb.toString());
                    } else {
                        userAddressTextView.setText(getString(R.string.error));
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    Log.i(TAG, "result is wrong, result code is " + resultCode);
                    break;
            }
        }
    }

    private void logger(String string) {
        Log.i(TAG, string);
        addresslogInfo.append("Log: " + string + System.lineSeparator());
        int offset = addresslogInfo.getLineCount() * addresslogInfo.getLineHeight();
        if (offset > addresslogInfo.getHeight()) {
            addresslogInfo.scrollTo(0, offset - addresslogInfo.getHeight());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}