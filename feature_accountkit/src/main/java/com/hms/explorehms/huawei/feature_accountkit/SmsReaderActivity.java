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
package com.hms.explorehms.huawei.feature_accountkit;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.api.CommonStatusCodes;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.sms.ReadSmsManager;
import com.huawei.hms.support.sms.common.ReadSmsConstant;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This activity allows SMS management for sign in via SMS message.
 */
public class SmsReaderActivity extends AppCompatActivity {
    @BindView(R.id.et_number)
    EditText number;
    @BindView(R.id.et_smscode)
    EditText smscode;
    @BindView(R.id.btn_getcode)
    Button btngetcode;
    @BindView(R.id.btn_login)
    Button login;
    private static final String TAG = "SmsReaderActivity";
    String message;

    /**
     * The method initializes the sets up necessary for variables.
     * It also asks for permission.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_reader);
        ButterKnife.bind(this);
        setupToolbar();
        String packageName = getApplicationContext().getPackageName();
        MessageDigest messageDigest = getMessageDigest();
        String signature = getSignature(this, packageName);
        String hashCode = getHashCode(packageName, messageDigest, signature);
        message = "<#> verification code is 101010" + hashCode;
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        11);
            }
        }
    }

    @OnClick(R.id.btn_getcode)
    /**
     * Allows to sign in process using SMS authentication.
     */
    public void signinsms() {
        MySMSBroadcastReceiver mySMSBroadcastReceiver = new MySMSBroadcastReceiver();
        IntentFilter filter = new IntentFilter(ReadSmsConstant.READ_SMS_BROADCAST_ACTION);
        this.registerReceiver(mySMSBroadcastReceiver, filter);
        Task<Void> task = ReadSmsManager.start(SmsReaderActivity.this);
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    sendSms(message, number.getText().toString());
                }
            }
        });
    }

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Called when the user presses the "back" button in the toolbar.
     * It handles the behavior for navigation.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * This function asks for permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 11: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    return;
                }
            }
        }
    }

    /**
     * It returns a message digest using SHA-256 algorithm.
     */
    private MessageDigest getMessageDigest() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No Such Algorithm.", e);
        }
        return messageDigest;
    }

    /**
     * It returns a signature if not null.
     */
    private String getSignature(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Signature[] signatureArrs;
        try {
            signatureArrs = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name inexistent.");
            return "";
        }
        if (null == signatureArrs || 0 == signatureArrs.length) {
            Log.e(TAG, "signature is null.");
            return "";
        }
        return signatureArrs[0].toCharsString();
    }

    /**
     * Generates a hash code for the specified digital signature using Base64 encoding.
     */
    private String getHashCode(String packageName, MessageDigest messageDigest, String signature) {
        String appInfo = packageName + " " + signature;
        messageDigest.update(appInfo.getBytes(StandardCharsets.UTF_8));
        byte[] hashSignature = messageDigest.digest();
        hashSignature = Arrays.copyOfRange(hashSignature, 0, 9);
        String base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING | Base64.NO_WRAP);
        base64Hash = base64Hash.substring(0, 11);
        return base64Hash;
    }

    /**
     * This broadcast receiver class is responsible for handling incoming SMSs.
     */
    public class MySMSBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && ReadSmsConstant.READ_SMS_BROADCAST_ACTION.equals(intent.getAction())) {
                Status status = bundle.getParcelable(ReadSmsConstant.EXTRA_STATUS);
                if (status.getStatusCode() == CommonStatusCodes.TIMEOUT) {
                    return;
                } else if (status.getStatusCode() == CommonStatusCodes.SUCCESS) {
                    if (bundle.containsKey(ReadSmsConstant.EXTRA_SMS_MESSAGE)) {

                        smscode.setText("101010");
                    }
                }
            }
        }
    }

    /**
     * It sends SMS message to the phone number with a string message.
     */
    public void sendSms(String messages, String number) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, messages, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();


    }

    @OnClick(R.id.btn_login)
    /**
     * It handles login process by checking sms code.
     */
    public void login() {
        if (smscode.getText().toString().equals("101010")) {
            Toast.makeText(getApplicationContext(), "Success",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed",
                    Toast.LENGTH_LONG).show();
        }
        finish();
    }
}