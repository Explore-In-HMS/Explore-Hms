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

package com.genar.hmssandbox.huawei.feature_authservice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.PhoneAuthProvider;
import com.huawei.agconnect.auth.PhoneUser;
import com.huawei.agconnect.auth.VerifyCodeResult;
import com.huawei.agconnect.auth.VerifyCodeSettings;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hmf.tasks.TaskExecutors;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PhoneLoginActivity extends AppCompatActivity {


    //region variablesAndObjects

    private static final String TAG = PhoneLoginActivity.class.getSimpleName();
    private static final String PHONE_LOGIN_ON_FAILURE = " : onFailure : ";
    private static final String PHONE_LOGIN_DISPLAY_NAME = " displayName : ";
    private static final String PHONE_STRING = " phone : ";
    private static final String PHONE_LOGIN_ON_SUCCESS = " : onSuccess...";
    private static final String AGC_CONNECT_SIGN_IN_EXCEPTION = "\nAGConnectAuth.getInstance signIn Exception : ";

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.etCountryCode)
    EditText etCountryCode;

    @Nullable
    @BindView(R.id.etPhone)
    EditText etPhone;

    @Nullable
    @BindView(R.id.etPassword)
    EditText etPassword;

    @Nullable
    @BindView(R.id.etVerifyCode)
    EditText etVerifyCode;

    @Nullable
    @BindView(R.id.clRegisterAndLoginElements)
    ConstraintLayout clRegisterAndLoginElements;

    @Nullable
    @BindView(R.id.clResetElements)
    ConstraintLayout clResetElements;

    @Nullable
    @BindView(R.id.clLogout)
    ConstraintLayout clLogout;

    @Nullable
    @BindView(R.id.etVerifyCodeForPhone)
    EditText etVerifyCodeForPhone;

    @Nullable
    @BindView(R.id.etVerifyCodeForPasswordPhone)
    EditText etVerifyCodeForPasswordPhone;

    @Nullable
    @BindView(R.id.tvProfileDetails)
    TextView tvProfileDetails;

    //endregion views


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        unbinder = ButterKnife.bind(this);
        setupToolbar();
        hideResetValueLayout();

        Utils.initializeAGConnectInstance(getApplicationContext());
    }


    @OnClick({R.id.btnSendCode, R.id.clRegister, R.id.clLogin1, R.id.clLogin,
            R.id.tvForgotMyPassword, R.id.tvShowLoginButtons,
            R.id.btnSendCodeForPhone, R.id.btnChangePhone,
            R.id.btnSendCodeForPasswordPhone, R.id.btnChangePasswordPhone, R.id.clLogout})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendCode:
                sendVerifyCodeToPhone();
                break;
            case R.id.clRegister:
                registerWithPhone();
                break;
            case R.id.clLogin1:
                loginWithPhoneAndVerificationCode();
                break;
            case R.id.clLogin:
                loginWithPhoneAndPassword();
                break;
            case R.id.tvForgotMyPassword:
                showResetValueLayout();
                clLogout.setVisibility(View.GONE);
                break;
            case R.id.tvShowLoginButtons:
                hideResetValueLayout();
                break;
            case R.id.btnSendCodeForPhone:
                sendVerifyCodeToChangePhoneNumber();
                break;
            case R.id.btnChangePhone:
                updatePhoneNumber();
                break;
            case R.id.btnSendCodeForPasswordPhone:
                sendVerifyCodeToUpdatePassword();
                break;
            case R.id.btnChangePasswordPhone:
                resetPassword();
                break;
            case R.id.clLogout:
                logOut();
                hideResetValueLayout();
                break;
            default:
                Log.e(TAG, "Default case");
                break;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_phone));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showResetValueLayout() {
        if (clLogout == null || clResetElements == null || clRegisterAndLoginElements == null) {
            return;
        }
        clLogout.setVisibility(View.VISIBLE);
        clResetElements.setVisibility(View.VISIBLE);
        clRegisterAndLoginElements.setVisibility(View.GONE);
    }

    public void hideResetValueLayout() {
        if (clLogout == null || clResetElements == null || clRegisterAndLoginElements == null) {
            return;
        }
        clLogout.setVisibility(View.GONE);
        clResetElements.setVisibility(View.GONE);
        clRegisterAndLoginElements.setVisibility(View.VISIBLE);
    }

    private void sendVerifyCodeToPhone() {
        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        if (countryCode.isEmpty() || phoneNumber.isEmpty() || phoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode and Phone number with valid!");
        } else {
            VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                    .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                    .sendInterval(30)   //shortest send interval ，30-120s
                    .locale(Locale.getDefault())    // Locale.CHINA()
                    .build();
            Task<VerifyCodeResult> task =  AGConnectAuth.getInstance().requestVerifyCode(countryCode, phoneNumber, settings);
            task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                @Override
                public void onSuccess(VerifyCodeResult verifyCodeResult) {
                    String msg = getResources().getString(R.string.verify_code_send) + " : onSuccess.. ";
                    showSuccessMessageDetail(msg, msg + " : \nverifyCodeResult ValidityPeriod : " + verifyCodeResult.getValidityPeriod());
                }
            }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    showErrorMessageDetail(getResources().getString(R.string.verify_code_send) + PHONE_LOGIN_ON_FAILURE, e);
                }
            });



        }
    }

    //  After the registration is successful, the user signs in automatically.
    private void registerWithPhone() {
        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();    //optional
        String verifyCode = etVerifyCode.getText().toString().trim();

        if (countryCode.isEmpty() || verifyCode.isEmpty() || password.isEmpty() || phoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode, verifyCode and phoneNumber with valid!");
        } else {
            PhoneUser phoneUser = new PhoneUser.Builder()
                    .setCountryCode(countryCode)
                    .setPhoneNumber(phoneNumber)
                    .setVerifyCode(verifyCode)
                    .setPassword(password)    //optional
                    .build();
            try {
                AGConnectAuth.getInstance().createUser(phoneUser)
                        .addOnSuccessListener(signInResult -> {
                            String msg = getResources().getString(R.string.register_user_and_signed_in_with_phone) + " : onSuccess : \nuserId : " + signInResult.getUser().getUid() + PHONE_LOGIN_DISPLAY_NAME + signInResult.getUser().getDisplayName() + PHONE_STRING + signInResult.getUser().getPhone();
                            showSuccessMessageDetail(getResources().getString(R.string.register_user_and_signed_in_with_phone) + PHONE_LOGIN_ON_SUCCESS, msg);
                            showResultDetail("createUser", signInResult.getUser());
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.register_user_and_signed_in_with_phone) + PHONE_LOGIN_ON_FAILURE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.register_user_and_signed_in_with_phone) + AGC_CONNECT_SIGN_IN_EXCEPTION, e);
            }

        }

    }

    private void loginWithPhoneAndVerificationCode() {
        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCode.getText().toString().trim();

        if (countryCode.isEmpty() || verifyCode.isEmpty() || phoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode, verifyCode and phoneNumber with valid!");
        } else {
            AGConnectAuthCredential credential = PhoneAuthProvider.credentialWithVerifyCode(countryCode, phoneNumber, password, verifyCode);
            try {
                AGConnectAuth.getInstance().signIn(credential)
                        .addOnSuccessListener(signInResult -> {
                            Log.d(TAG, getResources().getString(R.string.login_with_phone_number) + " : onSuccess : signInResult : " + signInResult.toString());
                            String msg = "onSuccess : userId : " + signInResult.getUser().getUid() + PHONE_LOGIN_DISPLAY_NAME + signInResult.getUser().getDisplayName() + PHONE_STRING + signInResult.getUser().getPhone();
                            Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_phone_number) + " : " + msg);
                            showResultDetail("signIn With Phone And Code", signInResult.getUser());
                            showResetValueLayout();
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.login_with_phone_number) + " : AGConnectAuth.signIn : onFailure : ", e)
                        );

            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.login_with_phone_number) + " : AGConnectAuth.signIn : Exception : ", e);
            }
        }

    }

    private void loginWithPhoneAndPassword() {
        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (countryCode.isEmpty() || password.isEmpty() || phoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode, password and phoneNumber with valid!");
        } else {

            AGConnectAuthCredential credential = PhoneAuthProvider.credentialWithPassword(countryCode, phoneNumber, password);

            try {
                AGConnectAuth.getInstance().signIn(credential)
                        .addOnSuccessListener(signInResult -> {
                            String msg = getResources().getString(R.string.login_with_phone_number) + " : onSuccess : \nuserId : " + signInResult.getUser().getUid() + PHONE_LOGIN_DISPLAY_NAME + signInResult.getUser().getDisplayName() + PHONE_STRING + signInResult.getUser().getPhone();
                            showSuccessMessageDetail(getResources().getString(R.string.login_with_phone_number) + PHONE_LOGIN_ON_SUCCESS, msg);
                            showResultDetail("signIn With Phone And Password", signInResult.getUser());
                            showResetValueLayout();
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.login_with_phone_number) + PHONE_LOGIN_ON_FAILURE, e)
                        );

            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.login_with_phone_number) + AGC_CONNECT_SIGN_IN_EXCEPTION, e);
            }
        }

    }


    public void showSuccessMessageDetail(String warning, String message) {
        if (tvProfileDetails == null) {
            return;
        }

        if (!warning.isEmpty()) Utils.showToastMessage(getApplicationContext(), warning);

        Log.d(TAG, message);
        tvProfileDetails.setText(message);
    }

    public void showErrorMessageDetail(String message, Exception e) {
        if (tvProfileDetails == null) {
            return;
        }

        Log.e(TAG, message + "\n" + e.getMessage(), e);
        tvProfileDetails.setText(String.format("%s%n%s", message, e.getMessage()));
        Utils.showToastMessage(getApplicationContext(), message + "\n" + e.getMessage());
    }

    public void showResultDetail(String msg, AGConnectUser signInResult) {
        if (tvProfileDetails == null) {
            return;
        }

        String signMsg = msg + " onSuccess : \n\n" +
                "user Uid         : " + signInResult.getUid() + "\n" +
                "user ProviderId  : " + signInResult.getProviderId() + "\n" +
                "user DisplayName : " + signInResult.getDisplayName() + "\n";

        Log.i(TAG, signMsg);
        tvProfileDetails.setText(signMsg);
    }

    // Other Feature Methods

    public void sendVerifyCodeToChangePhoneNumber() {
        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        if (countryCode.isEmpty() || phoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode and phoneNumber with valid!");
        } else {

            VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                    .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                    .sendInterval(30)   //shortest send interval ，30-120s
                    .locale(Locale.CHINA)    // Locale.getDefault()
                    .build();
            PhoneAuthProvider.requestVerifyCode(countryCode, phoneNumber, settings)
                    .addOnSuccessListener(TaskExecutors.uiThread(), verifyCodeResult -> {
                        String msg = getResources().getString(R.string.verify_code_send_update_phone) + " : onSuccess : verifyCodeResult ValidityPeriod : " + verifyCodeResult.getValidityPeriod();
                        showSuccessMessageDetail(msg, msg + " : \nverifyCodeResult ValidityPeriod : " + verifyCodeResult.getValidityPeriod());
                        // You can updatePhoneNumber with this verification code and new Phone Number now.
                        Log.i(TAG, "PhoneAuthProvider.requestVerifyCode : " + msg);
                    })
                    .addOnFailureListener(TaskExecutors.uiThread(), e ->
                            showErrorMessageDetail(getResources().getString(R.string.verify_code_send_update_phone) + PHONE_LOGIN_ON_FAILURE, e)
                    );
        }
    }

    public void updatePhoneNumber() {
        if (etCountryCode == null || etPhone == null || etVerifyCodeForPhone == null) {
            return;
        }

        String countryCode = etCountryCode.getText().toString().trim();
        String newPhoneNumber = etPhone.getText().toString().trim();
        String verifyCode = etVerifyCodeForPhone.getText().toString().trim();

        showToastMEssageToUserAboutUpdatePhoneNumber(countryCode, newPhoneNumber, verifyCode);
    }

    private void showToastMEssageToUserAboutUpdatePhoneNumber(String countryCode, String newPhoneNumber, String verifyCode) {
        if (countryCode.isEmpty() || verifyCode.isEmpty() || newPhoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode, newPhoneNumber and VerifyCodeForPhoneNumber with valid!");
        } else {
            try {
                AGConnectAuth.getInstance().getCurrentUser().updatePhone(countryCode, newPhoneNumber, verifyCode)
                        .addOnSuccessListener(aVoid -> {
                            String msg = getResources().getString(R.string.phone_number_update) + PHONE_LOGIN_ON_SUCCESS;
                            showSuccessMessageDetail("", msg);
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.phone_number_update) + PHONE_LOGIN_ON_FAILURE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.phone_number_update) + AGC_CONNECT_SIGN_IN_EXCEPTION, e);
            }
        }
    }


    public void sendVerifyCodeToUpdatePassword() {
        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        if (countryCode.isEmpty() || phoneNumber.length() != 10) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode and phoneNumber with valid!");
        } else {

            VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                    .action(VerifyCodeSettings.ACTION_RESET_PASSWORD)
                    .sendInterval(30)   //shortest send interval ，30-120s
                    .locale(Locale.CHINA)    // Locale.getDefault()
                    .build();
            PhoneAuthProvider.requestVerifyCode(countryCode, phoneNumber, settings)
                    .addOnSuccessListener(TaskExecutors.uiThread(), verifyCodeResult -> {
                        String msg = getResources().getString(R.string.verify_code_send_update_password) + " : onSuccess : verifyCodeResult ValidityPeriod : " + verifyCodeResult.getValidityPeriod();
                        Log.d(TAG, msg);
                        showSuccessMessageDetail("", msg);
                        // You can updatePassword with this verification code and new password now.
                        // OR
                        // You can resetPassword with this verification code
                    })
                    .addOnFailureListener(TaskExecutors.uiThread(), e ->
                            showErrorMessageDetail(getResources().getString(R.string.verify_code_send_update_password) + PHONE_LOGIN_ON_FAILURE, e)
                    );

        }

    }

    public void updatePassword() {
        String countryCode = etCountryCode.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCodeForPasswordPhone.getText().toString().trim();
        if (countryCode.isEmpty() || verifyCode.isEmpty() || newPassword.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode, newPassword and VerifyCodeForPhonePassword with valid!");
        } else {
            try {
                AGConnectAuth.getInstance().getCurrentUser().updatePassword(newPassword, verifyCode, AGConnectAuthCredential.Phone_Provider)
                        .addOnSuccessListener(aVoid -> {
                            String msg = getResources().getString(R.string.update_password) + PHONE_LOGIN_ON_SUCCESS;
                            showSuccessMessageDetail("", msg);
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.update_password) + PHONE_LOGIN_ON_FAILURE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.update_password) + AGC_CONNECT_SIGN_IN_EXCEPTION, e);
            }
        }
    }

    public void resetPassword() {
        if (etCountryCode == null || etPhone == null || etPassword == null || etVerifyCodeForPasswordPhone == null) {
            return;
        }

        String countryCode = etCountryCode.getText().toString().trim();
        String phoneNumber = etPhone.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCodeForPasswordPhone.getText().toString().trim();

        showMessageToUsersAboutRestPassword(countryCode, phoneNumber, newPassword, verifyCode);

    }

    private void showMessageToUsersAboutRestPassword(String countryCode, String phoneNumber, String newPassword, String verifyCode) {
        if (countryCode.isEmpty() || phoneNumber.isEmpty() || verifyCode.isEmpty() || newPassword.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the countryCode, phoneNumber, newPassword and VerifyCodeForPhonePassword with valid!");
        } else {
            try {
                AGConnectAuth.getInstance().resetPassword(countryCode, phoneNumber, newPassword, verifyCode)
                        .addOnSuccessListener(aVoid -> {
                            String msg = getResources().getString(R.string.reset_password) + PHONE_LOGIN_ON_SUCCESS + "With " + newPassword;
                            showSuccessMessageDetail("", msg);
                            Utils.showToastMessage(getApplicationContext(), msg);
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.reset_password) + PHONE_LOGIN_ON_FAILURE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.reset_password) + AGC_CONNECT_SIGN_IN_EXCEPTION, e);
            }
        }
    }

    private void logOut() {
        if (tvProfileDetails == null) {
            return;
        }

        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();
                tvProfileDetails.setText(getString(R.string.txt_message_for_instance_user_to_log_out));
                Log.d(TAG, getString(R.string.txt_message_for_instance_user_to_log_out));
                Utils.showToastMessage(getApplicationContext(), "InstanceUser logged out.");
            } catch (Exception e) {
                Log.e(TAG, "AGConnectAuth.getInstance signOut Exception : " + e.getMessage(), e);
                tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signOut Exception :%n%s", e.getMessage()));
            }
        } else {
            Log.w(TAG, "logOut : No logged in user");
            tvProfileDetails.setText(getString(R.string.txt_message_for_no_logged_user));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logOut();
        unbinder.unbind();
    }

}