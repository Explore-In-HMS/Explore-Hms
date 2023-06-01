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

package com.hms.explorehms.huawei.feature_cloudstorage.fragments;

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

import com.hms.explorehms.huawei.feature_cloudstorage.R;
import com.hms.explorehms.huawei.feature_cloudstorage.dao.ProgressDialogCloudStorage;
import com.hms.explorehms.huawei.feature_cloudstorage.databinding.FragmentCloudStorageLoginBinding;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.auth.AGCAuthException;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;


public class CloudStorageLoginFragment extends Fragment {

    private static final String TAG = "CloudStorageLogin";
    private static final int ACTIVITY_REQUEST_LOGIN_WITH_HUAWEI_ID = 101;
    private FragmentCloudStorageLoginBinding binding;
    private NavController navController;

    private ProgressDialogCloudStorage progressDialogCloudStorage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCloudStorageLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Cloud Storage");

        binding.tvAdvantagesTextCloudstorage.setText(
                getActivity().getResources().getString(R.string.advantages_cloudstorage)
        );

        AGConnectInstance.initialize(requireContext());

        progressDialogCloudStorage = new ProgressDialogCloudStorage(requireContext());

        navController = Navigation.findNavController(view);

        initUI();
    }

    private void initUI() {
        binding.btnLoginCloudstorage.setOnClickListener(view -> loginWithHuaweiId());
    }

    private void loginWithHuaweiId() {
        progressDialogCloudStorage.showProgressDialog();
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
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
                    transmitHuaweiAccountAccessTokenIntoAGC(huaweiAccount.getAccessToken());
                } else {
                    Toast.makeText(requireContext(), "getCurrentUser onFailure : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void transmitHuaweiAccountAccessTokenIntoAGC(String accessToken) {
        try {
            AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
            AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener(signInResult -> {
                        Toast.makeText(requireContext(), "Welcome " + signInResult.getUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                        String msg = "onSuccess : userId : " + signInResult.getUser().getUid() + " displayName : " + signInResult.getUser().getDisplayName() + " isAnonymous : " + signInResult.getUser().isAnonymous();
                        progressDialogCloudStorage.dismissProgressDialog();
                        navController.navigate(R.id.action_gotoHomeFragment_cloudstorage);
                    }).addOnFailureListener(e -> {
                if (((AGCAuthException) e).getCode() == 5) {
                    Log.e(TAG, "transmitHuaweiAccountAccessTokenIntoAGC: " + e.getMessage());
                    try {
                        AGConnectAuth.getInstance().signOut();
                        loginWithHuaweiId();
                    } catch (Exception ex) {
                        Toast.makeText(requireContext(), "AGConnectAuth.getInstance signOut Exception :\n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } catch (Exception e) {
            progressDialogCloudStorage.dismissProgressDialog();
            Log.e(TAG, "AGConnectAuth.getInstance signIn Exception : " + e.getMessage(), e);

        }
    }


}