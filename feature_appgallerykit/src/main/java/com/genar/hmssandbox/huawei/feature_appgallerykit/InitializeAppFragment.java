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

package com.genar.hmssandbox.huawei.feature_appgallerykit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.AntiAddictionCallback;
import com.huawei.hms.jos.AppParams;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.JosStatusCodes;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.utils.ResourceLoaderUtil;


public class InitializeAppFragment extends Fragment {


    public InitializeAppFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view4 = inflater.inflate(R.layout.fragment_initialize_app, container, false);
        TextView descAppGalleryKitInitializeApp = view4.findViewById(R.id.descAppGalleryKitInitializeApp);
        descAppGalleryKitInitializeApp.setText(R.string.desc_app_gallery_kit_initialize_app);
        init();
        return view4;
    }

    private void init() {
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
        Log.i("InitializeAppFragment", "init success");
    }
}