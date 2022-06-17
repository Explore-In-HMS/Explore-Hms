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
package com.genar.hmssandbox.huawei.feature_clouddb.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.feature_clouddb.R;
import com.genar.hmssandbox.huawei.feature_clouddb.dao.CloudDBZoneWrapper;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

public class MainActivityCloud extends AppCompatActivity {

    private Button btnWithLogin;
    private Button btnWithoutLogin;
    private Intent activityIntent;
    private static final int HUAWEI_ID_SIGN_IN =123;
    private static final String LOGIN_MESSAGE ="STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cloud);

        //CloudDB's initialize methods called , This method is called inside manifest file.
        AGConnectCloudDB.initialize(getApplicationContext());

        CloudDBZoneWrapper.initCloudDBZone();

        setComponents();//
        setClickEvent();//set click events

    }

    private void setComponents(){

        btnWithLogin= findViewById(R.id.btnContinuesWithLogin);
        btnWithoutLogin= findViewById(R.id.btnContinueWithoutLogin);
        setupToolbar();
        activityIntent = new Intent(getApplicationContext(),MainFragmentActivity.class);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setClickEvent(){

        btnWithLogin.setOnClickListener(view -> {
            Log.i(LOGIN_MESSAGE,"User wanted to Log in for Cloud DB");
            if(AGConnectAuth.getInstance().getCurrentUser() == null){
                loginRequest();
            }else{
                goToMainActivity();
            }
        });

        btnWithoutLogin.setOnClickListener(view -> {
            if(AGConnectAuth.getInstance().getCurrentUser() != null){
                //Users need to login to make some operations so We restrict them to login
                AGConnectAuth.getInstance().signOut();
            }
            Log.i(LOGIN_MESSAGE,"User didn't want to Log in for Cloud DB");
            goToMainActivity();
        });
    }

    private void loginRequest(){
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(this,authParams);

        startActivityForResult(service.getSignInIntent(), HUAWEI_ID_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      if(requestCode == HUAWEI_ID_SIGN_IN){
          Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
          if(authHuaweiIdTask.isSuccessful() && authHuaweiIdTask.isComplete()){

           AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
           String accessToken = huaweiAccount.getAccessToken();
           AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
           AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
               @Override
               public void onSuccess(SignInResult signInResult) {
                   goToMainActivity();
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(Exception e) {
                   Log.e(LOGIN_MESSAGE,e.toString());
               }
           });

       }
      }
    }
    private void goToMainActivity(){
        startActivity(activityIntent);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }
}