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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.entity.safetydetect.WifiDetectResponse;
import com.huawei.hms.support.api.safetydetect.SafetyDetect;
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes;

public class WifiDetectFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = WifiDetectFragment.class.getSimpleName();

    private TextView wifiDetectStatusView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_detect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MaterialButton mButton1 = getActivity().findViewById(R.id.fg_get_wifidetect_status);
        mButton1.setOnClickListener(this);

        wifiDetectStatusView = getActivity().findViewById(R.id.fg_wifidetecttextView);
        TextView fgInfoWifiDetect = getActivity().findViewById(R.id.fg_info_wifi_detect);
        fgInfoWifiDetect.setText(R.string.info_wifi_detect);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fg_get_wifidetect_status) {
            getWifiDetectStatus();
        }
    }

    /**
     * getWifiDetectStatus()  Get Wifi Status.
     */

    private void getWifiDetectStatus() {
        SafetyDetect.getClient(getActivity())
                .getWifiDetectStatus()
                .addOnSuccessListener(new OnSuccessListener<WifiDetectResponse>() {
                    @Override
                    public void onSuccess(WifiDetectResponse wifiDetectResponse) {
                        int wifiDetectStatus = wifiDetectResponse.getWifiDetectStatus();
                        String wifiDetectView = "WifiDetect status: " + wifiDetectStatus;
                        wifiDetectStatusView.setText(wifiDetectView);
                        Toast.makeText(getActivity(), "WifiDetect Status is refreshed.", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // There was an error communicating with the service.
                        String errorMsg;
                        if (e instanceof ApiException) {
                            // An error with the HMS API contains some additional details.
                            ApiException apiException = (ApiException) e;
                            errorMsg = SafetyDetectStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": "
                                    + apiException.getMessage();
                            // You can use the apiException.getStatusCode() method to get the status code.
                        } else {
                            // Unknown type of error has occurred.
                            errorMsg = e.getMessage();
                        }
                        Toast.makeText(getActivity(), "WifiDetect Status is refreshed.", Toast.LENGTH_SHORT).show();
                        String msg = "Get wifiDetect status failed! Message: " + errorMsg;
                        Log.e(TAG, msg);
                        wifiDetectStatusView.setText(msg);
                    }
                });
    }

}
