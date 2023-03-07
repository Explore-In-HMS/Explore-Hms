package com.hms.explorehms.huawei.feature_authservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hms.explorehms.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;

public class GoogleLoginActivity extends AppCompatActivity {
    protected AGConnectAuth auth;
    private static final int SIGN_CODE = 9901;
    private GoogleSignInClient client;
    private ConstraintLayout loginBtn;
    private ConstraintLayout logoutBtn;

    private TextView tvProfileDetails;

    private static final String TAG = GoogleLoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        tvProfileDetails = findViewById(R.id.tvProfileDetails);

        GoogleSignInOptions options =
                new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.server_client_id))
                        .requestProfile()
                        .build();
        client = GoogleSignIn.getClient(this, options);

        loginBtn = findViewById(R.id.clLogin);
        logoutBtn = findViewById(R.id.clLogout);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void login() {
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_CODE);
    }

    public void logout() {
        client.signOut();
        if (tvProfileDetails == null) {
            return;
        }
        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();
                tvProfileDetails.setText(getString(R.string.txt_message_for_instance_user_to_log_out));
                Log.d(TAG, getString(R.string.log_out_instance_user));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.log_out_instance_user));
            } catch (Exception e) {
                Log.e(TAG, "AGConnectAuth.getInstance signOut Exception : " + e.getMessage(), e);
                tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signOut Exception :%n%s", e.getMessage()));
            }
        } else {
            Log.w("TAG", getString(R.string.log_out_user));
            tvProfileDetails.setText(getString(R.string.txt_message_for_no_logged_user));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_CODE) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    //String idToken = googleSignInAccount.getIdToken();
                    showResultDetail(googleSignInAccount);
                    loginSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }

    protected void loginSuccess() {
        Utils.showToastMessage(this, getString(R.string.success_login_with_google));
        Log.d(TAG, getString(R.string.success_login_with_google));
    }

    public void showResultDetail(GoogleSignInAccount googleSignInAccount) {
        if (tvProfileDetails == null) {
            return;
        }
        String signMsg = "Google " + " onSuccess : \n" +
                "profile DisplayName  : " + googleSignInAccount.getGivenName() + "\n" +
                "profile Id Token     : " + googleSignInAccount.getIdToken();

        Log.i(TAG, signMsg);
        tvProfileDetails.setText(signMsg);
    }
}