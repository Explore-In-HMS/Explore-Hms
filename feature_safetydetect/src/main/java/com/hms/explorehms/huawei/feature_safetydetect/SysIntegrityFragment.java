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
package com.hms.explorehms.huawei.feature_safetydetect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.safetydetect.SysIntegrityResp;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * An example of how to use SysIntegrity Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 *
 * @since 4.0.0.300
 */
public class SysIntegrityFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = com.hms.explorehms.huawei.feature_safetydetect.SysIntegrityFragment.class.getSimpleName();

    private  String APP_ID; //appId for HMS ExploreHMS

    private MaterialButton mButton1;

    private TextView basicIntegrityTextView;

    private TextView adviceTextView;

    private TextView infoTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sys_integrity, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mButton1 = getActivity().findViewById(R.id.fg_button_sys_integrity_go);
        APP_ID=AGConnectServicesConfig.fromContext(this.requireContext()).getString("client/app_id");
        mButton1.setOnClickListener(this);
        basicIntegrityTextView = getActivity().findViewById(R.id.fg_payloadBasicIntegrity);
        adviceTextView = getActivity().findViewById(R.id.fg_payloadAdvice);
        infoTextView = getActivity().findViewById(R.id.fg_sys_integrity_info);
        infoTextView.setText(R.string.info_sys_integrity);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fg_button_sys_integrity_go) {
            processView();
            invokeSysIntegrity();
        }
    }

    private void invokeSysIntegrity() {
        byte[] nonce = new byte[24];
        try {
            SecureRandom random;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                random = SecureRandom.getInstanceStrong();
            } else {
                random = SecureRandom.getInstance("SHA1PRNG");
            }
            random.nextBytes(nonce);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
        }

        SafetyDetect.getClient(getActivity())
                .sysIntegrity(nonce, APP_ID)
                .addOnSuccessListener(new OnSuccessListener<SysIntegrityResp>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(SysIntegrityResp response) {
                        // Indicates communication with the service was successful.
                        // Use response.getResult() to get the result data.
                        String jwsStr = response.getResult();
                        // Process the result data here
                        String[] jwsSplit = jwsStr.split("\\.");
                        String jwsPayloadStr = jwsSplit[1];
                        try {
                            String payloadDetail = new String(Base64.decode(jwsPayloadStr.getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE), StandardCharsets.UTF_8);
                            final JSONObject jsonObject = new JSONObject(payloadDetail);
                            final boolean basicIntegrity = jsonObject.getBoolean("basicIntegrity");


                            String isBasicIntegrity = String.valueOf(basicIntegrity);
                            String basicIntegrityResult = "Basic Integrity: " + isBasicIntegrity;
                            basicIntegrityTextView.setText(basicIntegrityResult);
                            String allDetail="";
                            JSONArray detailList;
                            if(!basicIntegrity) {
                                detailList = jsonObject.getJSONArray("detail");

                                if (detailList.length() > 0) {
                                    for (int i = 0; i < detailList.length(); ++i) {
                                        String detail = detailList.getString(i);
                                        allDetail = detail + " ";
                                    }
                                    basicIntegrityTextView.setText(basicIntegrityResult + " " + allDetail);
                                }
                            }
                            mButton1.setBackgroundColor(basicIntegrity ? R.color.background_green : R.color.background_red);
                            mButton1.setText(R.string.rerun);
                            basicIntegrityTextView.setText(basicIntegrityResult);
                            if (!basicIntegrity) {
                                String advice = "Advice: " + jsonObject.getString("advice");
                                adviceTextView.setText(advice);
                            }
                        } catch (Exception e) {
                            String errorMsg = e.getMessage();
                            Log.e(TAG, errorMsg != null ? errorMsg : "unknown error");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onFailure(Exception e) {
                        // There was an error communicating with the service.
                        String errorMsg;
                        if (e instanceof ApiException) {
                            // An error with the HMS API contains some additional details.
                            ApiException apiException = (ApiException) e;
                            errorMsg = SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) +
                                    ": " + apiException.getMessage();

                            //Error code: 19803
                            int userDetectPermission = SafetyDetectStatusCodes.USER_DETECT_PERMISSION;
                            if (((ApiException) e).getStatusCode() == userDetectPermission){
                                Toast.makeText(getActivity(), "Failed to display a popup on a non-Huawei phone.", Toast.LENGTH_SHORT).show();
                                basicIntegrityTextView.setText(R.string.error_code_19803);
                            }
                            // You can use the apiException.getStatusCode() method to get the status code.

                        } else {
                            // Unknown type of error has occurred.
                            errorMsg = e.getMessage();
                        }
                        Log.e(TAG, errorMsg);
                        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        mButton1.setBackgroundColor(R.color.background_yellow);
                        mButton1.setText(R.string.rerun);
                    }
                });
    }

    private void processView() {
        basicIntegrityTextView.setText("");
        adviceTextView.setText("");
        mButton1.setText(R.string.processing);
    }
}
