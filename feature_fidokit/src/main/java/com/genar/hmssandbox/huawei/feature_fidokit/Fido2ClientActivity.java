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
package com.genar.hmssandbox.huawei.feature_fidokit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.huawei.hms.support.api.fido.fido2.Algorithm;
import com.huawei.hms.support.api.fido.fido2.Attachment;
import com.huawei.hms.support.api.fido.fido2.AttestationConveyancePreference;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorMetadata;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorSelectionCriteria;
import com.huawei.hms.support.api.fido.fido2.Fido2;
import com.huawei.hms.support.api.fido.fido2.Fido2AuthenticationRequest;
import com.huawei.hms.support.api.fido.fido2.Fido2AuthenticationResponse;
import com.huawei.hms.support.api.fido.fido2.Fido2Client;
import com.huawei.hms.support.api.fido.fido2.Fido2Extension;
import com.huawei.hms.support.api.fido.fido2.Fido2Intent;
import com.huawei.hms.support.api.fido.fido2.Fido2IntentCallback;
import com.huawei.hms.support.api.fido.fido2.Fido2RegistrationRequest;
import com.huawei.hms.support.api.fido.fido2.Fido2RegistrationResponse;
import com.huawei.hms.support.api.fido.fido2.Fido2Response;
import com.huawei.hms.support.api.fido.fido2.NativeFido2AuthenticationOptions;
import com.huawei.hms.support.api.fido.fido2.NativeFido2RegistrationOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialCreationOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialDescriptor;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialParameters;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRequestOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRpEntity;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialType;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialUserEntity;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Fido2ClientActivity extends AppCompatActivity {
    private static final String TAG = "Fido2Client";
    // Specify a value based on the actual service requirements.
    private static final String RP_ID = "com.huawei.hms.fido2.test";
    private TextView resultView;
    private Fido2Client fido2Client;
    private byte[] regCredentialId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fido2_client);
        Toolbar toolBar = findViewById(R.id.toolbar_fido2);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolBar, getString(R.string.url_txt_fido));
        resultView = findViewById(R.id.resultTextView);
        fido2Client = Fido2.getFido2Client(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Obtain the challenge value and related policy from the FIDO server, and initiate a Fido2RegistrationRequest request.
    private Fido2RegistrationRequest assembleFido2RegistrationRequest() {
        byte[] challengeBytes = getChallege();
        // Initiate the Fido2RegistrationRequest request. The first parameter of AuthenticatorSelectionCriteria
        // specifies whether to use a platform authenticator or a roaming authenticator. If no authenticator needs to be specified, pass null to this parameter.


        PublicKeyCredentialCreationOptions.Builder builder = new PublicKeyCredentialCreationOptions.Builder();
        builder.setRp(new PublicKeyCredentialRpEntity(RP_ID, RP_ID, null))
                .setChallenge(challengeBytes)
                .setAttestation(AttestationConveyancePreference.DIRECT)
                .setAuthenticatorSelection(new AuthenticatorSelectionCriteria(Attachment.PLATFORM, null, null))
                .setPubKeyCredParams(new ArrayList<>(
                        Arrays.asList(
                                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, Algorithm.ES256),
                                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, Algorithm.RS256))))
                .setTimeoutSeconds(60L);
        // Specify a value based on the actual service requirements.
        String user = "fidoCp";
        builder.setUser(new PublicKeyCredentialUserEntity(user, user.getBytes(StandardCharsets.UTF_8)));
        if (regCredentialId != null) {
            builder.setExcludeList(new ArrayList<>(
                    Collections.singletonList(
                            new PublicKeyCredentialDescriptor(PublicKeyCredentialType.PUBLIC_KEY, regCredentialId))));
        }
        HashMap<String, Object> extensions = new HashMap<>();
        builder.setExtensions(extensions);
        // Specify a platform authenticator and related extension items. You can specify a platform authenticator or not as needed.
        useSelectedPlatformAuthenticator(extensions);
        return new Fido2RegistrationRequest(builder.build(), null);
    }

    /**
     * initiate registration
     *
     * @param view indicating a UI object
     */
    public void btnRegistrationClicked(View view) {
        if (!fido2Client.isSupported()) {
            showMsg(getString(R.string.fido2_is_not_supported));
            logger(getString(R.string.fido2_is_not_supported));
            return;
        }
        Fido2RegistrationRequest request = assembleFido2RegistrationRequest();
        // Call Fido2Client.getRegistrationIntent to obtain a Fido2Intent instance and start the FIDO client registration process.
        fido2Client.getRegistrationIntent(request, NativeFido2RegistrationOptions.DEFAULT_OPTIONS,
                new Fido2IntentCallback() {
                    @Override
                    public void onSuccess(Fido2Intent fido2Intent) {
                        // Start the FIDO client registration process through Fido2Client.REGISTRATION_REQUEST.
                        fido2Intent.launchFido2Activity(Fido2ClientActivity.this, Fido2Client.REGISTRATION_REQUEST);
                        logger(getString(R.string.registration_success));
                    }

                    @Override
                    public void onFailure(int errorCode, CharSequence errString) {
                        showError("Registration failure." + errorCode + "=" + errString);
                        logger("Registration failure." + errorCode + "=" + errString);
                    }
                });
    }

    // Obtain the challenge value and related policy from the FIDO server, and initiate a Fido2AuthenticationRequest request.
    private Fido2AuthenticationRequest assembleFido2AuthenticationRequest() {
        byte[] challengeBytes = getChallege();
        // Initiate the Fido2RegistrationRequest request.
        List<PublicKeyCredentialDescriptor> allowList = new ArrayList<>();
        allowList.add(new PublicKeyCredentialDescriptor(PublicKeyCredentialType.PUBLIC_KEY, regCredentialId));
        PublicKeyCredentialRequestOptions.Builder builder = new PublicKeyCredentialRequestOptions.Builder();
        builder.setRpId(RP_ID).setChallenge(challengeBytes).setAllowList(allowList).setTimeoutSeconds(60L);
        HashMap<String, Object> extensions = new HashMap<>();
        builder.setExtensions(extensions);
        // Specify a platform authenticator and related extension items. You can specify a platform authenticator or not as needed.
        useSelectedPlatformAuthenticator(extensions);
        return new Fido2AuthenticationRequest(builder.build(), null);
    }

    /**
     * authentication to the FIDO server for verification
     *
     * @param view indicating a UI object
     */
    public void btnAuthenticationClicked(View view) {
        if (regCredentialId == null) {
            showMsg("Please register first.");
            logger("Please register first.");
            return;
        }
        if (!fido2Client.isSupported()) {
            showMsg(getString(R.string.fido2_is_not_supported));
            logger(getString(R.string.fido2_is_not_supported));
            return;
        }
        Fido2AuthenticationRequest request = assembleFido2AuthenticationRequest();
        // Call Fido2Client.getAuthenticationIntent to obtain a Fido2Intent instance and start the FIDO client authentication process.
        fido2Client.getAuthenticationIntent(request, NativeFido2AuthenticationOptions.DEFAULT_OPTIONS,
                new Fido2IntentCallback() {
                    @Override
                    public void onSuccess(Fido2Intent fido2Intent) {
                        // Start the FIDO client authentication process through Fido2Client.AUTHENTICATION_REQUEST.
                        fido2Intent.launchFido2Activity(Fido2ClientActivity.this, Fido2Client.AUTHENTICATION_REQUEST);
                    }

                    @Override
                    public void onFailure(int errorCode, CharSequence errString) {
                        showError(getString(R.string.auth_failure) + errorCode + "=" + errString);
                        logger(getString(R.string.auth_failure) + errorCode + "=" + errString);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            showMsg("Unknown error.");
            logger("Unknown error.");
            return;
        }
        // Receive registration response
        if (requestCode == Fido2Client.REGISTRATION_REQUEST) {
            Fido2RegistrationResponse fido2RegistrationResponse = fido2Client.getFido2RegistrationResponse(data);
            if (fido2RegistrationResponse.isSuccess()) {
                resultView.append("\n\nregistration\n");
                resultView.append(fido2RegistrationResponse.getAuthenticatorAttestationResponse().toJson());
                resultView.append("\n");
                // save the credentialId
                regCredentialId = fido2RegistrationResponse.getAuthenticatorAttestationResponse().getCredentialId();
                showMsg(getString(R.string.registration_success));
                logger(getString(R.string.registration_success));
            } else {
                showError("Registration failed.", fido2RegistrationResponse);
                logger("Registration failed." + fido2RegistrationResponse);
            }
            // Receive authentication response
        } else if (requestCode == Fido2Client.AUTHENTICATION_REQUEST) {
            Fido2AuthenticationResponse fido2AuthenticationResponse =
                    fido2Client.getFido2AuthenticationResponse(data);
            if (fido2AuthenticationResponse.isSuccess()) {
                resultView.append("\n\nAuthentication\n");
                resultView.append(fido2AuthenticationResponse.getAuthenticatorAssertionResponse().toJson());
                resultView.append("\n");
                showMsg("Authentication success.");
                logger("Authentication success.");
            } else {
                showError(getString(R.string.auth_failure), fido2AuthenticationResponse);
            }
        }
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Fido2ClientActivity.this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showError(String message, Fido2Response fido2Response) {
        String errMsgBuilder = message +
                fido2Response.getFido2Status() +
                "=" +
                fido2Response.getFido2StatusMessage() +
                String.format(Locale.getDefault(), "(Ctap error: 0x%x=%s)", fido2Response.getCtapStatus(),
                        fido2Response.getCtapStatusMessage());
        showError(errMsgBuilder);
    }

    private void showMsg(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Fido2ClientActivity.this);
        builder.setTitle("Information");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    /**
     * Clear data
     *
     * @param view view
     */
    public void btnClearLogClicked(View view) {
        resultView.setText("");
        regCredentialId = null;
    }

    private byte[] getChallege() {
        return SecureRandom.getSeed(16);
    }

    // Specify a platform authenticator and related extension items.
    private void useSelectedPlatformAuthenticator(HashMap<String, Object> extensions) {
        if (!fido2Client.hasPlatformAuthenticators()) {
            return;
        }
        List<String> selectedAuthenticatorList = new ArrayList<>();
        for (AuthenticatorMetadata meta : fido2Client.getPlatformAuthenticators()) {
            if (!meta.isAvailable()) {
                continue;
            }
            // Fingerprint authenticator
            if (meta.isSupportedUvm(AuthenticatorMetadata.UVM_FINGERPRINT)) {
                selectedAuthenticatorList.add(meta.getAaguid());

                if (meta.getExtensions().contains(Fido2Extension.W3C_WEBAUTHN_UVI.getIdentifier())) {
                    // Indicates whether to verify the fingerprint ID. If the value is true, the same finger must be used for both registration and verification.
                    extensions.put(Fido2Extension.W3C_WEBAUTHN_UVI.getIdentifier(), Boolean.TRUE);
                }

                if (meta.getExtensions().contains(Fido2Extension.HMS_R_PA_CIBBE_01.getIdentifier())) {
                    // Indicates whether the authentication credential expires when the biometric feature changes. If the value is true or empty, the key will expire when the fingerprint is enrolled. This is valid only for registration.
                    extensions.put(Fido2Extension.HMS_R_PA_CIBBE_01.getIdentifier(), Boolean.TRUE);
                }
            }
            // Lock screen face authenticator
            else {
                meta.isSupportedUvm(AuthenticatorMetadata.UVM_FACEPRINT);
            }
        }
        extensions.put(Fido2Extension.HMS_RA_C_PACL_01.getIdentifier(), selectedAuthenticatorList);

    }

    private void logger(String string) {
        Log.i(TAG, string);
        resultView.append(string + System.lineSeparator());
        int offset = resultView.getLineCount() * resultView.getLineHeight();
        if (offset > resultView.getHeight()) {
            resultView.scrollTo(0, offset - resultView.getHeight());
        }
    }
}