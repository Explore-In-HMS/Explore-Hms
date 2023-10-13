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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.safetydetect.UrlCheckResponse;
import com.huawei.hms.support.api.entity.safetydetect.UrlCheckThreat;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectClient;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

import java.util.List;

public class URLCheckFragment extends Fragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String TAG = com.hms.explorehms.huawei.feature_safetydetect.URLCheckFragment.class.getSimpleName();

    private  String APP_ID; //appId for HMS ExploreHMS

    private SafetyDetectClient client;

    private Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = SafetyDetect.getClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_url_check, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        client.initUrlCheck();
    }

    @Override
    public void onPause() {
        super.onPause();
        client.shutdownUrlCheck();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.fg_call_url_btn).setOnClickListener(this);
        APP_ID= AGConnectServicesConfig.fromContext(this.requireContext()).getString("client/app_id");
        spinner = getActivity().findViewById(R.id.fg_url_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.url_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView fgInfoUrlCheck = getActivity().findViewById(R.id.fg_info_url_check);
        fgInfoUrlCheck.setText(R.string.info_url_check);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fg_call_url_btn) {
            callUrlCheckApi();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        final TextView testRes = getActivity().findViewById(R.id.resultTextView);
        testRes.setText(R.string.result_will_be_displayed_here);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i(TAG,"onNothingSelected");
    }

    private void callUrlCheckApi() {
        Log.i(TAG, "Start call URL check api");

        String realUrl = spinner.getSelectedItem().toString().trim();
        final TextView testRes = getActivity().findViewById(R.id.resultTextView);
        client.urlCheck(realUrl, APP_ID,
                // Specify url threat type
                UrlCheckThreat.MALWARE,
                UrlCheckThreat.PHISHING)
                .addOnSuccessListener(new OnSuccessListener<UrlCheckResponse>() {
                    /**
                     * Called after successfully communicating with the SafetyDetect API.
                     * The #onSuccess callback receives an
                     * {@link UrlCheckResponse} that contains a
                     * list of UrlCheckThreat that contains the threat type of the Url.
                     */
                    @Override
                    public void onSuccess(UrlCheckResponse urlCheckResponse) {
                        // Indicates communication with the service was successful.
                        // Identify any detected threats.
                        // Call getUrlCheckResponse method of UrlCheckResponse then you can get List<UrlCheckThreat> .
                        // If List<UrlCheckThreat> is empty , that means no threats found , else that means threats found.
                        List<UrlCheckThreat> list = urlCheckResponse.getUrlCheckResponse();
                        if (list.isEmpty()) {
                            // No threats found.
                            testRes.setText(getString(R.string.no_threats_found));
                        } else {
                            // Threats found!
                            testRes.setText(getString(R.string.threats_found));
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * Called when an error occurred when communicating with the SafetyDetect API.
                     */
                    @Override
                    public void onFailure(Exception e) {
                        // There was an error communicating with the service.
                        String errorMsg;
                        if (e instanceof ApiException) {
                            // An error with the Huawei Mobile Service API contains some
                            ApiException apiException = (ApiException) e;
                            errorMsg = "Error: " +
                                    SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " +
                                    e.getMessage();
                            testRes.setText(errorMsg);

                            //Error code: 19803
                            int userDetectPermission = SafetyDetectStatusCodes.USER_DETECT_PERMISSION;
                            if (((ApiException) e).getStatusCode() == userDetectPermission){
                                Toast.makeText(getActivity(), "Failed to display a popup on a non-Huawei phone.", Toast.LENGTH_SHORT).show();
                                testRes.setText(R.string.error_code_19803);
                            }
                            // You can use the apiException.getStatusCode() method to get the status code.
                            // Note: If the status code is SafetyDetectStatusCodes.CHECK_WITHOUT_INIT, you need to call initUrlCheck().
                        } else {
                            // Unknown type of error has occurred.
                            errorMsg = e.getMessage();
                        }
                        Log.d(TAG, errorMsg);
                        testRes.setText(errorMsg);
                    }
                });
    }
}