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

package com.genar.hmssandbox.huawei.feature_gameservice.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.genar.hmssandbox.huawei.CredentialManager;
import com.genar.hmssandbox.huawei.feature_gameservice.R;
import com.genar.hmssandbox.huawei.feature_gameservice.databinding.FragmentGameServicesLoginBinding;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.AntiAddictionCallback;
import com.huawei.hms.jos.AppParams;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.JosStatusCodes;
import com.huawei.hms.jos.games.GameScopes;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.player.Player;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.huawei.hms.utils.ResourceLoaderUtil;

import java.util.ArrayList;
import java.util.List;

public class GameServicesLoginFragment extends Fragment {


    private static final String TAG = "GameServicesLoginFragment";
    private static final int ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID = 101;

    private FragmentGameServicesLoginBinding binding;

    private NavController navController;
    private GameServicesLoginFragmentDirections.ActionGotoGameServicesHomeFragment action;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGameServicesLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Game Services");
        navController = Navigation.findNavController(view);

        initUI();
    }

    private void initUI() {

        binding.tvIntrouctionInfoGameservices.setText(
                getResources().getString(R.string.introduction_text_gameservices)
        );
        binding.tvWhatCanYouDoInfoGameservices.setText(
                getResources().getString(R.string.whatcanyoudo_text_gameservices)
        );

        binding.btnLoginGameservices.setOnClickListener(view -> signIn());
    }

    private void signIn() {
        List<Scope> scopes = new ArrayList<>();
        scopes.add(GameScopes.DRIVE_APP_DATA);
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME)
                .setAccessToken()
                .setIdToken()
                .setScopeList(scopes)
                .createParams();
        HuaweiIdAuthService authService = HuaweiIdAuthManager.getService(requireActivity(), authParams);
        startActivityForResult(authService.getSignInIntent(), ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID) {
            if (resultCode == 0) {
                Toast.makeText(requireContext(), "loginWithHuaweiId Cancelled and No Data!", Toast.LENGTH_SHORT).show();
            } else {
                Task<AuthHuaweiId> task = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
                if (task.isSuccessful()) {
                    AuthHuaweiId huaweiAccount = task.getResult();
                    CredentialManager.setCredentials(huaweiAccount);
                    getGamePlayer(huaweiAccount);
                } else {
                    Toast.makeText(requireContext(), "getCurrentUser onFailure : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void getGamePlayer(AuthHuaweiId huaweiAccount) {

        AccountAuthParams params = AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM_GAME;
        JosAppsClient appsClient = JosApps.getJosAppsClient(requireActivity());
        Task<Void> initTask;
        ResourceLoaderUtil.setmContext(requireContext());  // Set the game addiction prevention message context, which is mandatory.
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
                // After the game is initialized successfully, call the floating window display API once.
                //showFloatWindowNewWay();
                // The sign-in function can be implemented only after initialization is successful.
                // signIn();
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
                                Log.d("GameServicesLoginFragmentFailure","has reject the protocol");
                                // Exit the game.
                            }
                            // Process other result codes.
                        }
                    }
                });

        PlayersClient client = Games.getPlayersClient(requireActivity());

        Task<Player> task = client.getGamePlayer();

        task.addOnSuccessListener(player -> {
            if (player.getAccessToken() != null) {
                action = GameServicesLoginFragmentDirections.actionGotoGameServicesHomeFragment(huaweiAccount);
                navController.navigate(action);
            }
        }).addOnFailureListener(e -> Log.e(TAG, "getGamePlayer: " + e.getMessage()));
    }

}