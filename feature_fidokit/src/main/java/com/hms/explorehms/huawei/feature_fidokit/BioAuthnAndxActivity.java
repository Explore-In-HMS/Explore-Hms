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
package com.hms.explorehms.huawei.feature_fidokit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.hms.explorehms.Util;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnCallback;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnManager;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnPrompt;
import com.huawei.hms.support.api.fido.bioauthn.BioAuthnResult;
import com.huawei.hms.support.api.fido.bioauthn.CryptoObject;
import com.huawei.hms.support.api.fido.bioauthn.FaceManager;


public class BioAuthnAndxActivity extends AppCompatActivity {
    private static final String TAG = "FidoBioAuthn";
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_authn_andx);
        Toolbar toolBar = findViewById(R.id.toolbar_fido_bioauthn);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, getString(R.string.url_txt_fido));
        resultTextView = findViewById(R.id.resultTextView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Shows the fingerprint prompt without CryptoObject and allows the user to use the device PIN and password for
     * authentication.
     *
     * @param view View
     */
    public void btnFingerAuthenticateWithoutCryptoObjectClicked(View view) {
        // Whether fingerprint authentication is supported.
        BioAuthnManager bioAuthnManager = new BioAuthnManager(this);
        int errorCode = bioAuthnManager.canAuth();
        if (errorCode != 0) {
            // Fingerprint authentication is not supported.
            resultTextView.setText("");
            showResult(getString(R.string.cannot_auth) + errorCode);
            logger(getString(R.string.cannot_auth) + errorCode);
            return;
        }
        // Callback.
        BioAuthnCallback callback = new BioAuthnCallback() {
            @Override
            public void onAuthError(int errMsgId, @NonNull CharSequence errString) {
                // Authentication error.
                showResult(getString(R.string.auth_error) + errMsgId + getString(R.string.error_message) + errString);
                logger(getString(R.string.auth_error) + errMsgId + getString(R.string.error_message) + errString);
            }

            @Override
            public void onAuthSucceeded(BioAuthnResult result) {
                // Authentication success.
                showResult(getString(R.string.auth_succeed));
                logger(getString(R.string.auth_succeed));
            }

            @Override
            public void onAuthFailed() {
                // Authentication failure.
                showResult(getString(R.string.auth_failed));
                logger(getString(R.string.auth_failed));
            }
        };
        BioAuthnPrompt bioAuthnPrompt = new BioAuthnPrompt(this, ContextCompat.getMainExecutor(this), callback);
        // Construct the prompt information.
        BioAuthnPrompt.PromptInfo.Builder builder =
                new BioAuthnPrompt.PromptInfo.Builder().setTitle("This is the title.")
                        .setSubtitle("This is the subtitle")
                        .setDescription("This is the description");
        // Prompt the user to use the fingerprint for authentication, and also provide options for the user to use the PIN, lock screen pattern, or lock screen password for authentication.
        // If the parameter is set to true here, setNegativeButtonText(CharSequence) is not supported.
        builder.setDeviceCredentialAllowed(true);
        // Set the cancellation button title. If a title is set, setDeviceCredentialAllowed(true) is not supported.
        BioAuthnPrompt.PromptInfo info = builder.build();
        bioAuthnPrompt.auth(info);
    }

    /**
     * Sends a 3D facial authentication request to the user device.
     *
     * @param view View
     */
    public void btnFaceAuthenticateWithoutCryptoObjectClicked(View view) {
        // Callback.
        BioAuthnCallback callback = new BioAuthnCallback() {
            @Override
            public void onAuthError(int errMsgId, @NonNull CharSequence errString) {
                // Authentication error.
                showResultCameraPermission(getString(R.string.auth_error) + errMsgId + getString(R.string.error_message) + errString
                        + (errMsgId == 1012 ? " The camera permission may not be enabled." : ""));
                logger(getString(R.string.auth_error) + errMsgId + getString(R.string.error_message) + errString
                        + (errMsgId == 1012 ? " The camera permission may not be enabled." : ""));
            }

            @Override
            public void onAuthHelp(int helpMsgId, @NonNull CharSequence helpString) {
                // Help.
                //resultTextView.append("Authentication help. helpMsgId=" + helpMsgId + ",helpString=" + helpString + "\n");
            }

            @Override
            public void onAuthSucceeded(BioAuthnResult result) {
                // Authentication success.
                showResult(getString(R.string.auth_succeed));
                logger(getString(R.string.auth_succeed));
            }

            @Override
            public void onAuthFailed() {
                // Authentication failure.
                showResult(getString(R.string.auth_failed));
                logger(getString(R.string.auth_failed));
            }
        };
        // Cancellation signal.
        CancellationSignal cancellationSignal = new CancellationSignal();
        FaceManager faceManager = new FaceManager(this);
        // Checks whether 3D facial authentication can be used.
        int errorCode = faceManager.canAuth();
        if (errorCode != 0) {
            // Authentication is not supported.
            resultTextView.setText("");
            showBiometricMsg();
            //showResult(getString(R.string.cannot_auth) + errorCode);
            logger(getString(R.string.cannot_auth) + errorCode);
            return;
        }
        // Flags.
        int flags = 0;
        // Authentication message handler.
        Handler handler = null;
        // It is recommended that CryptoObject be set to null. In this version, KeyStore is not associated with facial authentication.
        // The value false must be passed to KeyGenParameterSpec.Builder.setUserAuthenticationRequired().
        CryptoObject crypto = null;
        resultTextView.setText("Start face authentication.\nAuthenticating......\n");
        faceManager.auth(crypto, cancellationSignal, flags, callback, handler);
    }

    private void showResult(final String msg) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BioAuthnAndxActivity.this);
            builder.setTitle("Authentication Result");
            builder.setMessage(msg);
            builder.setPositiveButton("OK", null);
            builder.show();
            resultTextView.append(msg + "\n");
        });
    }

    private void logger(String string) {
        Log.i(TAG, string);
        resultTextView.append(string + System.lineSeparator());
        int offset = resultTextView.getLineCount() * resultTextView.getLineHeight();
        if (offset > resultTextView.getHeight()) {
            resultTextView.scrollTo(0, offset - resultTextView.getHeight());
        }
    }

    private void showBiometricMsg() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(BioAuthnAndxActivity.this);
        builder.setTitle("Authentication failed.");
        builder.setMessage("Please define for finger for your device");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );
        builder.show();
    }

    private void showResultCameraPermission(final String msg) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BioAuthnAndxActivity.this);
            builder.setTitle("Authentication Result");
            builder.setMessage(msg);
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }
            );
            builder.show();
            resultTextView.append(msg + "\n");
        });
    }
}


