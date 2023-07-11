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

package com.hms.explorehms.huawei.feature_authservice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hms.explorehms.Util;
import com.hms.explorehms.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.EmailAuthProvider;
import com.huawei.agconnect.auth.EmailUser;
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

/**
 * This shows how we sign-in with email by Auth Service.
 */
public class EmailLoginActivity extends AppCompatActivity {

    //region variablesAndObjects

    private static final String TAG = EmailLoginActivity.class.getSimpleName();
    private static final String VALID_EMAIL_ADDRESS = "Please enter the emailAddress with valid!";
    private static final String ON_FAILURE_MESSAGE = " : onFailure : ";
    private static final String ON_SUCCESS_MESSAGE = " : onSuccess...";
    private static final String AG_CONNECT_AUTH_GET_INSTANCE_SIGN_IN_EXCEPTION = "\nAGConnectAuth.getInstance signIn Exception : ";

    private Unbinder unbinder;

    private static final int ET_E_MAIL = R.id.etEmail;
    private static final int ET_PASSWORD = R.id.etPassword;
    private static final int ET_VERIFY_CODE = R.id.etVerifyCode;
    private static final int BTN_SEND_CODE = R.id.btnSendCode;
    private static final int CL_REGISTER_AND_LOG_IN_ELEMENTS = R.id.clRegisterAndLoginElements;
    private static final int CL_RESET_ELEMENTS = R.id.clResetElements;
    private static final int CL_LOG_OUT = R.id.clLogout;
    private static final int ET_VERIFY_CODE_FOR_MAIL = R.id.etVerifyCodeForMail;
    private static final int ET_VERIFY_CODE_FOR_PASSWORD_MAIL = R.id.etVerifyCodeForPasswordMail;
    private static final int TV_PROFILE_DETAILS = R.id.tvProfileDetails;

    @Nullable
    @BindView(ET_E_MAIL)
    EditText etEmail;

    @Nullable
    @BindView(ET_PASSWORD)
    EditText etPassword;

    @Nullable
    @BindView(ET_VERIFY_CODE)
    EditText etVerifyCode;

    @Nullable
    @BindView(BTN_SEND_CODE)
    Button btnSendCode;

    @Nullable
    @BindView(CL_REGISTER_AND_LOG_IN_ELEMENTS)
    ConstraintLayout clRegisterAndLoginElements;

    @Nullable
    @BindView(CL_RESET_ELEMENTS)
    ConstraintLayout clResetElements;

    @Nullable
    @BindView(CL_LOG_OUT)
    ConstraintLayout clLogout;

    @Nullable
    @BindView(ET_VERIFY_CODE_FOR_MAIL)
    EditText etVerifyCodeForMail;

    @Nullable
    @BindView(ET_VERIFY_CODE_FOR_PASSWORD_MAIL)
    EditText etVerifyCodeForPasswordMail;

    @Nullable
    @BindView(TV_PROFILE_DETAILS)
    TextView tvProfileDetails;

    //endregion views


    /**
     * The method initializes the sets up necessary for variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        unbinder = ButterKnife.bind(this);
        setupToolbar();
        hideResetValueLayout();

        Utils.initializeAGConnectInstance(getApplicationContext());
    }


    @OnClick({R.id.btnSendCode, R.id.clRegister, R.id.clLogin1, R.id.clLogin,
            R.id.tvForgotMyPassword, R.id.tvShowLoginButtons,
            R.id.btnSendCodeForMail, R.id.btnChangeMail,
            R.id.btnSendCodeForPasswordMail, R.id.btnChangePasswordMail,
            R.id.clLogout})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendCode:
                sendVerifyCodeToEmail();
                break;
            case R.id.clRegister:
                registerWithEmail();
                break;
            case R.id.clLogin1:
                loginWithEmailAndVerificationCode();
                break;
            case R.id.clLogin:
                loginWithEmailAndPassword();
                break;
            case R.id.tvForgotMyPassword:
                showResetValueLayout();
                clLogout.setVisibility(View.GONE);
                break;
            case R.id.tvShowLoginButtons:
                hideResetValueLayout();
                break;
            case R.id.btnSendCodeForMail:
                sendVerifyCodeToChangeMailAddress();
                break;
            case R.id.btnChangeMail:
                updateEmailAddress();
                break;
            case R.id.btnSendCodeForPasswordMail:
                sendVerifyCodeToChangePassword();
                break;
            case R.id.btnChangePasswordMail:
                updatePassword();
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

    /**
     * Sets up the toolbar for the activity
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_mail));
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
     * It shows reset value layout
     */
    public void showResetValueLayout() {
        if (clLogout == null || clResetElements == null || clRegisterAndLoginElements == null) {
            return;
        }
        clLogout.setVisibility(View.VISIBLE);
        clResetElements.setVisibility(View.VISIBLE);
        clRegisterAndLoginElements.setVisibility(View.GONE);
    }

    /**
     * It hides reset value layout
     */
    public void hideResetValueLayout() {
        if (clLogout == null || clResetElements == null || clRegisterAndLoginElements == null) {
            return;
        }
        clLogout.setVisibility(View.GONE);
        clResetElements.setVisibility(View.GONE);
        clRegisterAndLoginElements.setVisibility(View.VISIBLE);
    }

    /**
     * It sends verify code to email
     */
    private void sendVerifyCodeToEmail() {
        if (etEmail == null) {
            return;
        }

        String emailAddress = etEmail.getText().toString().trim();
        if (emailAddress.isEmpty() || !emailAddress.contains("@")) {
            Utils.showToastMessage(getApplicationContext(), VALID_EMAIL_ADDRESS);
        } else {
            VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                    .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                    .sendInterval(30)   //shortest send interval ，30-120s
                    .locale(Locale.getDefault())    // Locale.CHINA()
                    .build();
            Task<VerifyCodeResult> task = AGConnectAuth.getInstance().requestVerifyCode(emailAddress, settings);
            task.addOnSuccessListener(TaskExecutors.uiThread(), new OnSuccessListener<VerifyCodeResult>() {
                @Override
                public void onSuccess(VerifyCodeResult verifyCodeResult) {
                    String msg = getResources().getString(R.string.verify_code_send_mail) + ON_FAILURE_MESSAGE;
                    showSuccessMessageDetail(msg, msg + " : \nverifyCodeResult ValidityPeriod : " + verifyCodeResult.getValidityPeriod());
                }
            }).addOnFailureListener(TaskExecutors.uiThread(), new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    showErrorMessageDetail(getResources().getString(R.string.verify_code_send_mail) + ON_FAILURE_MESSAGE, e);
                }
            });

        }
    }

    //  After the registration is successful, the user signs in automatically.
    private void registerWithEmail() {
        if (etEmail == null || etPassword == null || etVerifyCode == null) {
            return;
        }

        String emailAddress = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCode.getText().toString().trim();

        if (emailAddress.isEmpty() || !emailAddress.contains("@") || password.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the emailAddress, password and verifyCode with valid!");
        } else {
            EmailUser emailUser = new EmailUser.Builder()
                    .setEmail(emailAddress)
                    .setVerifyCode(verifyCode)
                    .setPassword(password)
                    .build();
            try {
                AGConnectAuth.getInstance().createUser(emailUser)
                        .addOnSuccessListener(signInResult -> {
                            String msg = getResources().getString(R.string.register_user_and_signed_in_with_mail) + " : onSuccess : \nuserId : " + signInResult.getUser().getUid() + " displayName : " + signInResult.getUser().getDisplayName() + " phone : " + signInResult.getUser().getPhone();
                            showSuccessMessageDetail(getResources().getString(R.string.register_user_and_signed_in_with_mail) + ON_SUCCESS_MESSAGE, msg);
                            showResultDetail("CreateUser", signInResult.getUser());
                            // !!
                            // You can login with this verification code
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.register_user_and_signed_in_with_mail) + ON_FAILURE_MESSAGE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.register_user_and_signed_in_with_mail) + AG_CONNECT_AUTH_GET_INSTANCE_SIGN_IN_EXCEPTION, e);
            }

        }

    }

    /**
     * It allows to login with Email and Verification code
     */
    private void loginWithEmailAndVerificationCode() {
        if (etEmail == null || etPassword == null || etVerifyCode == null) {
            return;
        }

        String emailAddress = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCode.getText().toString().trim();
        if (emailAddress.isEmpty() || !emailAddress.contains("@") || password.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the emailAddress, password and verifyCode with valid!");
        } else {
            AGConnectAuthCredential credential = EmailAuthProvider.credentialWithVerifyCode(emailAddress, password, verifyCode);
            try {
                AGConnectAuth.getInstance().signIn(credential)
                        .addOnSuccessListener(signInResult -> {
                            Log.d(TAG, getResources().getString(R.string.login_with_mail) + " : onSuccess : signInResult : " + signInResult.toString());
                            showResultDetail("SignIn With EmailAndCode", signInResult.getUser());
                            String msg = "onSuccess : userId : " + signInResult.getUser().getUid();
                            Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_mail) + " : " + msg);
                            showResetValueLayout();
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.login_with_mail) + " : AGConnectAuth.signIn : onFailure : ", e)
                        );

            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.login_with_mail) + " : AGConnectAuth.signIn : Exception : ", e);
            }
        }

    }

    /**
     * It allows to login with Email and Password
     */
    private void loginWithEmailAndPassword() {
        if (etEmail == null || etPassword == null) {
            return;
        }

        String emailAddress = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (emailAddress.isEmpty() || !emailAddress.contains("@") || password.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the emailAddress and password with valid!");
        } else {
            AGConnectAuthCredential credential = EmailAuthProvider.credentialWithPassword(emailAddress, password);
            try {
                AGConnectAuth.getInstance().signIn(credential)
                        .addOnSuccessListener(signInResult -> {
                            Log.d(TAG, getResources().getString(R.string.login_with_mail) + " : onSuccess : signInResult : " + signInResult.toString());
                            showResultDetail("SignIn with EmailAndPassword", signInResult.getUser());
                            String msg = "onSuccess : userId : " + signInResult.getUser().getUid();
                            Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_mail) + " : " + msg);
                            showResetValueLayout();
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.login_with_mail) + " : AGConnectAuth.signIn : onFailure : ", e)
                        );

            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.login_with_mail) + " : AGConnectAuth.signIn : Exception : ", e);
            }
        }

    }

    /**
     * It shows success message detail
     */
    public void showSuccessMessageDetail(String warning, String message) {

        if (!warning.isEmpty()) Utils.showToastMessage(getApplicationContext(), warning);

        Log.d(TAG, message);
        tvProfileDetails.setText(message);
    }

    /**
     * It shows error message detail
     */
    public void showErrorMessageDetail(String message, Exception e) {
        if (tvProfileDetails == null) {
            return;
        }

        Log.e(TAG, message + "\n" + e.getMessage(), e);
        tvProfileDetails.setText(String.format("%s%n%s", message, e.getMessage()));
        Utils.showToastMessage(getApplicationContext(), message + "\n" + e.getMessage());
    }

    /**
     * It displays the details of sign-in user's information, including a message.
     */
    public void showResultDetail(String msg, AGConnectUser signInResult) {
        if (tvProfileDetails == null) {
            return;
        }
        String signMsg = msg + " onSuccess : \n\n" +
                "user uid         : " + signInResult.getUid() + "\n" +
                "user email       : " + signInResult.getEmail() + "\n" +
                "user providerId  : " + signInResult.getProviderId() + "\n";
        Log.i(TAG, signMsg);
        tvProfileDetails.setText(signMsg);
    }


    // Other Feature Methods

    /**
     * It sends verify code to change user mail address
     */
    public void sendVerifyCodeToChangeMailAddress() {
        String emailAddress = etEmail.getText().toString().trim();
        if (emailAddress.isEmpty() || !emailAddress.contains("@")) {
            Utils.showToastMessage(getApplicationContext(), VALID_EMAIL_ADDRESS);
        } else {

            VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                    .action(VerifyCodeSettings.ACTION_REGISTER_LOGIN)
                    .sendInterval(30)   //shortest send interval ，30-120s
                    .locale(Locale.CHINA)    // Locale.getDefault()
                    .build();
            EmailAuthProvider.requestVerifyCode(emailAddress, settings)
                    .addOnSuccessListener(TaskExecutors.uiThread(), verifyCodeResult -> {
                        String msg = getResources().getString(R.string.verify_code_send_update_email) + " : onSuccess";
                        showSuccessMessageDetail(msg, msg + " : \nverifyCodeResult ValidityPeriod: " + verifyCodeResult.getValidityPeriod());
                        // You can updateEmailAddress with this verification code and new EmailAddress now.
                    })
                    .addOnFailureListener(TaskExecutors.uiThread(), e ->
                            showErrorMessageDetail(getResources().getString(R.string.verify_code_send_update_email) + ON_FAILURE_MESSAGE, e)
                    );

        }

    }

    /**
     * It allows to update email address
     */
    public void updateEmailAddress() {
        String emailAddress = etEmail.getText().toString().trim();
        String verifyCode = etVerifyCodeForMail.getText().toString().trim();
        if (emailAddress.isEmpty() || !emailAddress.contains("@") || verifyCode.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the emailAddress and verifyCodeForMail with valid!");
        } else {
            try {
                AGConnectAuth.getInstance().getCurrentUser().updateEmail(emailAddress, verifyCode)
                        .addOnSuccessListener(aVoid -> {
                            String msg = getResources().getString(R.string.email_address_update) + ON_SUCCESS_MESSAGE;
                            showSuccessMessageDetail("", msg);
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.email_address_update) + ON_FAILURE_MESSAGE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.email_address_update) + AG_CONNECT_AUTH_GET_INSTANCE_SIGN_IN_EXCEPTION, e);
            }
        }
    }

    /**
     * It sends verify code to change password
     */
    public void sendVerifyCodeToChangePassword() {
        String emailAddress = etEmail.getText().toString().trim();
        if (emailAddress.isEmpty() || !emailAddress.contains("@")) {
            Utils.showToastMessage(getApplicationContext(), VALID_EMAIL_ADDRESS);
        } else {

            VerifyCodeSettings settings = new VerifyCodeSettings.Builder()
                    .action(VerifyCodeSettings.ACTION_RESET_PASSWORD)
                    .sendInterval(30)   //shortest send interval ，30-120s
                    .locale(Locale.CHINA)    // Locale.getDefault()
                    .build();
            EmailAuthProvider.requestVerifyCode(emailAddress, settings)
                    .addOnSuccessListener(TaskExecutors.uiThread(), verifyCodeResult -> {
                        String msg = getResources().getString(R.string.verify_code_send_update_password) + " : onSuccess ";
                        showSuccessMessageDetail(msg, msg + " : \nverifyCodeResult ValidityPeriod : " + verifyCodeResult.getValidityPeriod());
                        // You can updatePassword with this verification code
                        // OR
                        // You can resetPassword with this verification code
                    })
                    .addOnFailureListener(TaskExecutors.uiThread(), e ->
                            showErrorMessageDetail(getResources().getString(R.string.verify_code_send_update_password) + ON_FAILURE_MESSAGE, e)
                    );

        }

    }

    /**
     * It allows to update password
     */
    public void updatePassword() {
        String newPassword = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCodeForPasswordMail.getText().toString().trim();
        if (verifyCode.isEmpty() || newPassword.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the newPassword and verifyCodeForPassword with valid!");
        } else {
            try {
                AGConnectAuth.getInstance().getCurrentUser().updatePassword(newPassword, verifyCode, AGConnectAuthCredential.Email_Provider)
                        .addOnSuccessListener(aVoid -> {
                            String msg = getResources().getString(R.string.update_password) + ON_SUCCESS_MESSAGE;
                            showSuccessMessageDetail("", msg);
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.update_password) + ON_FAILURE_MESSAGE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.update_password) + AG_CONNECT_AUTH_GET_INSTANCE_SIGN_IN_EXCEPTION, e);
            }
        }
    }

    /**
     * It resets the password
     */
    public void resetPassword() {
        if (etEmail == null || etPassword == null || etVerifyCodeForPasswordMail == null) {
            return;
        }
        String emailAddress = etEmail.getText().toString().trim();
        String newPassword = etPassword.getText().toString().trim();
        String verifyCode = etVerifyCodeForPasswordMail.getText().toString().trim();
        if (verifyCode.isEmpty() || newPassword.isEmpty()) {
            Utils.showToastMessage(getApplicationContext(), "Please enter the emailAddress, newPassword, and verifyCodeForPassword with valid!");
        } else {
            try {
                AGConnectAuth.getInstance().resetPassword(emailAddress, newPassword, verifyCode)
                        .addOnSuccessListener(aVoid -> {
                            String msg = getResources().getString(R.string.reset_password) + ON_SUCCESS_MESSAGE;
                            showSuccessMessageDetail("", msg);
                        })
                        .addOnFailureListener(e ->
                                showErrorMessageDetail(getResources().getString(R.string.reset_password) + ON_FAILURE_MESSAGE, e)
                        );
            } catch (Exception e) {
                showErrorMessageDetail(getResources().getString(R.string.reset_password) + AG_CONNECT_AUTH_GET_INSTANCE_SIGN_IN_EXCEPTION, e);
            }
        }
    }

    /**
     * It allows to user log out.
     */
    private void logOut() {
        if (tvProfileDetails == null) {
            return;
        }
        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();
                tvProfileDetails.setText(getString(R.string.txt_message_for_instance_user_to_log_out));
                Log.d(TAG, "InstanceUser logged out!");
                Utils.showToastMessage(getApplicationContext(), "InstanceUser logged out.");
            } catch (Exception e) {
                Log.e(TAG, "AGConnectAuth.getInstance signOut Exception : " + e.getMessage(), e);
                tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signOut Exception :%n%s", e.getMessage()));
            }
        } else {
            Log.w(TAG, getString(R.string.txt_message_for_no_logged_user));
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