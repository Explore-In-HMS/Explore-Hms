package com.hms.explorehms.baseapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hms.explorehms.R;
import com.hms.explorehms.modelingkit3d.ui.activity.MainActivity;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void loginWithId(View view) {
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setAccessToken()
                .createParams();

        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(LoginActivity.this, authParams);
        startActivityForResult(service.getSignInIntent(), 1003);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1003) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);

            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();

                String accessToken = huaweiAccount.getAccessToken();
                AGConnectAuth.getInstance().signIn(HwIdAuthProvider.credentialWithToken(accessToken))
                        .addOnSuccessListener(signInResult -> {
                            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(LoginActivity.this, "Auth Service Error", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(LoginActivity.this, "Huawei ID signIn failed: " + authHuaweiIdTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}