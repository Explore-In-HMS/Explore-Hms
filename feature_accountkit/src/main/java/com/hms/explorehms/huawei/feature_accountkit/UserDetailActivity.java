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
package com.hms.explorehms.huawei.feature_accountkit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.CredentialManager;
import com.hms.explorehms.Util;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_profile_image)
    CircleImageView profileImage;
    @BindView(R.id.detail_txt_username)
    TextView username;
    @BindView(R.id.detail_txt_name)
    TextView name;
    @BindView(R.id.detail_txt_idtoken)
    TextView idtoken;
    @BindView(R.id.detail_txt_email)
    TextView email;
    @BindView(R.id.detail_btn_logOut)
    Button btnLogOut;
    @BindView(R.id.detail_btn_revoke)
    Button btnRevoke;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        setupToolbar();

        Picasso.get()
                .load(CredentialManager.getProfilePic())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.login)
                .into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // onSuccessState
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(CredentialManager.getProfilePic())
                                .placeholder(R.drawable.login)
                                .error(R.drawable.profile_image)
                                .into(profileImage);
                    }
                });
        name.setText(CredentialManager.getFullname());
        username.setText(CredentialManager.getDisplaName());
        idtoken.setText(CredentialManager.getIDToken());
        email.setText(CredentialManager.getEmail());


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

    @OnClick(R.id.detail_btn_logOut)
    public void logOut() {
        Task<Void> signOutTask = CredentialManager.getHuaweiIdAuthService().signOut();

        Activity activity = this;
        signOutTask.addOnCompleteListener(task -> {
            Toast.makeText(activity, "Sign Out Successfull", Toast.LENGTH_SHORT).show();
            CredentialManager.clearAuthorization();
            activity.finish();
            Log.i("DEBUG", "signOut complete");
        });
    }

    @OnClick(R.id.detail_btn_revoke)
    public void revoke() {
        Activity activity = this;
        CredentialManager.getHuaweiIdAuthService().cancelAuthorization().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    //do some thing while cancel success
                    CredentialManager.clearAuthorization();
                    activity.finish();
                    Util.startActivity(activity, LoginActivity.class);
                    Log.i("DEBUG", "onSuccess: ");
                } else {
                    //do some thing while cancel success
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        int statusCode = ((ApiException) exception).getStatusCode();
                        Log.i("ERROR", "onFailure: " + statusCode);
                    }
                }
            }
        });
    }
}