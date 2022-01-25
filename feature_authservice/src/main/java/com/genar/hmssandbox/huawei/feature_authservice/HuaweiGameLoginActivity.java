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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.genar.hmssandbox.huawei.Util;
import com.genar.hmssandbox.huawei.feature_authservice.util.Utils;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HWGameAuthProvider;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.AntiAddictionCallback;
import com.huawei.hms.jos.AppParams;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.JosStatusCodes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.huawei.hms.utils.ResourceLoaderUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HuaweiGameLoginActivity extends AppCompatActivity {

    //region variablesAndObjects

    private static final String TAG = HuaweiGameLoginActivity.class.getSimpleName();
    private static final String LOGIN_WITH_HUAWEI_GAME_ON_FAILURE = "loginWithHuaweiGame onFailure : ";
    private static final String LOGIN_HUAWEI_ON_FAILURE = " : onFailure : ";

    private static final int ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_GAME = 102;

    private Unbinder unbinder;

    private static final int TV_PROFILE_DETAILS = R.id.tvProfileDetails;
    @Nullable
    @BindView(TV_PROFILE_DETAILS)
    TextView tvProfileDetails;

    //endregion views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huawei_game_login);

        unbinder = ButterKnife.bind(this);
        setupToolbar();

        Utils.initializeAGConnectInstance(getApplicationContext());
    }

    private static final int CL_LOGIN = R.id.clLogin;
    private static final int CL_LOG_OUT = R.id.clLogout;

    @OnClick({R.id.clLogin, R.id.clLogout,})
    public void onItemClick(View v) {
        switch (v.getId()) {
            case CL_LOGIN:
                if (!Utils.isLoggedInAgcUser()) {
                    loginWithHuaweiGame();
                }
                break;
            case CL_LOG_OUT:
                logOut();
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
        Util.setToolbar(this, toolbar, getResources().getString(R.string.url_auth_service_huawei_game));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loginWithHuaweiGame() {
        if (tvProfileDetails == null) {
            return;
        }
        try {
            HuaweiMobileServicesUtil.setApplication(getApplication());

            AccountAuthParams params = AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME;
            JosAppsClient appsClient = JosApps.getJosAppsClient(this);
            Task<Void> initTask;
            ResourceLoaderUtil.setmContext(this);  // Set the game addiction prevention message context, which is mandatory.
            initTask = appsClient.init(
                    new AppParams(params, new AntiAddictionCallback() {
                        @Override
                        public void onExit() {
                            // The callback is returned in either of the following cases:
                            // 1. If a minor who has passed identity verification signs in to your game beyond the allowed time period, Game Service will display a message, indicating that the player is not allowed to enter the game, and the player taps OK.
                            // 2. A minor who has passed identity verification signs in to your game within the allowed time period. If the player is still playing the game at 21:00, Game Service notifies the player that the allowed time period ends, and the player taps OK.
                            // Implement the game addiction prevention function including saving the game progress and calling the account sign-out API or exiting the game process using System.exit(0).
                        }
                    }));
            initTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            if (e instanceof ApiException) {
                                ApiException apiException = (ApiException) e;
                                int statusCode = apiException.getStatusCode();
                                // Result code 7401 indicates that the user does not agree to Huawei's joint operations privacy agreement.
                                if (statusCode == JosStatusCodes.JOS_PRIVACY_PROTOCOL_REJECTED) {
                                    Log.d("failure","has reject the protocol");
                                    // Exit the game.
                                }
                                // Process other result codes.
                            }
                        }
                    });
            HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME).createParams();
            HuaweiIdAuthService authService = HuaweiIdAuthManager.getService(this, authParams);
            startActivityForResult(authService.getSignInIntent(), ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_GAME);
        } catch (Exception e) {
            String textResult = "HuaweiMobileServicesUtil HuaweiIdAuthService.getSignInIntent Exception : " + e.getMessage();
            Log.e(TAG, textResult, e);
            tvProfileDetails.setText(textResult);
            Utils.showToastMessage(getApplicationContext(), textResult);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_GAME) {
            if (resultCode == 0) {
                Log.w(TAG, "loginWithHuaweiGame : onActivityResult No any data detected");
                Utils.showToastMessage(getApplicationContext(), "loginWithHuaweiGame Cancelled and No Data!");
            } else {
                Task<AuthHuaweiId> task = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
                task.addOnSuccessListener(authHuaweiId -> {
                    Log.i(TAG, "loginWithHuaweiGame parseAuthResultFromIntent onSuccess authHuaweiId = " + authHuaweiId.getDisplayName() + " = " + authHuaweiId.toString());
                    getCurrentPlayerInfo(authHuaweiId);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, LOGIN_WITH_HUAWEI_GAME_ON_FAILURE + e.getMessage(), e);
                    Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huawei_game_service) + LOGIN_HUAWEI_ON_FAILURE + e.getMessage());
                });
            }
        }
    }


    private void getCurrentPlayerInfo(AuthHuaweiId authHuaweiId) {
        PlayersClient playersClient = Games.getPlayersClient(this, authHuaweiId);
        Task<Player> playerTask = playersClient.getCurrentPlayer();
        playerTask.addOnSuccessListener(player -> {
            showProfileDetail(player);
            transmitHuaweiGamePlayerIntoAGC(player);
        }).addOnFailureListener(e -> {
            String excMsg = e.getMessage();
            if (e instanceof ApiException) {
                Log.e(TAG, "loginWithHuaweiGame onFailure : status: " + ((ApiException) e).getStatusCode(), e);
                excMsg = excMsg + " / " + ((ApiException) e).getStatusCode();
            }
            Log.e(TAG, LOGIN_WITH_HUAWEI_GAME_ON_FAILURE + excMsg, e);
            Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huawei_game_service) + LOGIN_HUAWEI_ON_FAILURE + excMsg);

            if (excMsg != null) {
                if (excMsg.contains("203817988") || excMsg.contains("third provider is disabled")) {
                    Utils.showToastMessage(getApplicationContext(), "You must install GameCenter app before!");
                }
            }
        });
    }

    private void transmitHuaweiGamePlayerIntoAGC(final Player player) {

        if (tvProfileDetails == null) {
            return;
        }

        try {
            // add control updates for; if gameCenter account does not have profile image,
            // or not installed, or Service is disable states
            Uri imageUri;
            if (player.hasHiResImage()) {
                imageUri = player.getHiResImageUri();
            } else if (player.hasIconImage()) {
                imageUri = player.getIconImageUri();
            } else {
                imageUri = null;
            }

            HWGameAuthProvider.Builder credentialBuilder = new HWGameAuthProvider.Builder()
                    .setPlayerSign(player.getPlayerSign())
                    .setPlayerId(player.getPlayerId())
                    .setDisplayName(player.getDisplayName())
                    .setPlayerLevel(player.getLevel())
                    .setSignTs(player.getSignTs());
            if (imageUri != null) {
                credentialBuilder.setImageUrl(imageUri.toString());
            }

            AGConnectAuthCredential credential = credentialBuilder.build();
            AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener(signInResult -> {
                        String msg = "onSuccess : userId : " + signInResult.getUser().getUid() + " playerId " + player.getPlayerId() + " - playerName : " + player.getDisplayName() + " - level : " + player.getLevel();
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huawei_game_service) + " : " + msg);
                        showResultDetail(tvProfileDetails.getText() + "\n\n" + getResources().getString(R.string.login_with_huawei_game_service), signInResult.getUser());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, LOGIN_WITH_HUAWEI_GAME_ON_FAILURE + e.getMessage(), e);
                        tvProfileDetails.setText(String.format("%s\n\nAGConnectAuth.getInstance signIn onFailure : \n\n%s", tvProfileDetails.getText(), e.getMessage()));
                        Utils.showToastMessage(getApplicationContext(), getResources().getString(R.string.login_with_huaweiid) + LOGIN_HUAWEI_ON_FAILURE + e.getMessage());
                        // getting error : code: 203817988 message: third provider is disabled
                    });

        } catch (Exception e) {
            String messageResult = "AGConnectAuth.getInstance signIn Exception : " + e.getMessage();
            Log.e(TAG, messageResult, e);
            tvProfileDetails.setText(messageResult);
            Utils.showToastMessage(getApplicationContext(), "AGConnectAuth.getInstance signIn Exception :\n" + e.getMessage());
        }
    }

    public void showProfileDetail(Player player) {
        if (tvProfileDetails == null) {
            return;
        }
        String msg = "\nCurrentPlayer ProfileDetail : \n\n" +
                "PlayerId    : " + player.getPlayerId() + "\n" +
                "DisplayName : " + player.getDisplayName() + "\n" +
                "Level       : " + player.getLevel() + "\n" +
                "SignTs      : " + player.getSignTs() + "\n";
        Log.i(TAG, msg);
        tvProfileDetails.setText(msg);
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

    private void logOut() {
        if (tvProfileDetails == null) {
            return;
        }
        if (Utils.isLoggedInAgcUser()) {
            try {
                AGConnectAuth.getInstance().signOut();

                tvProfileDetails.setText(getString(R.string.txt_message_for_instance_user_to_log_out));
                Log.d(TAG, getString(R.string.txt_message_for_instance_user_to_log_out));
                Utils.showToastMessage(getApplicationContext(), getString(R.string.txt_message_for_instance_user_to_log_out));

            } catch (Exception e) {
                Log.e(TAG, "AGConnectAuth.getInstance signOut Exception : " + e.getMessage(), e);
                tvProfileDetails.setText(String.format("AGConnectAuth.getInstance signOut Exception :%n%s", e.getMessage()));
            }
        } else {
            Log.e(TAG, "logOut : No logged in user");
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