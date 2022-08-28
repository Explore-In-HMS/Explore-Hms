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

package com.hms.explorehms.huawei.feature_awarenesskit.ui.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentPhoneStatusAwarenessBinding;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.status.ApplicationStatus;
import com.huawei.hms.kit.awareness.status.DarkModeStatus;
import com.huawei.hms.kit.awareness.status.ScreenStatus;
import com.huawei.hms.kit.awareness.status.WifiStatus;


public class PhoneStatusAwarenessFragment extends Fragment {


    private final String phoneStatusAwarenessTag = "PhoneStatusAwareness";

    private FragmentPhoneStatusAwarenessBinding binding;

    private int tempScreenStatus = 0;
    private int tempWifiStatus = 0;

    private Handler handler;
    private Runnable runScreenStatus;
    private Runnable runWifiStatus;
    private Runnable runDarkModeStatus;
    private Runnable runAppStatusAwareness;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPhoneStatusAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Phone Status Awareness");

        handler = new Handler();

        startGetStatusProcess();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runScreenStatus, 3000);
        handler.postDelayed(runWifiStatus, 3000);
        handler.postDelayed(runDarkModeStatus, 3000);
        handler.postDelayed(runAppStatusAwareness, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runScreenStatus);
        handler.removeCallbacks(runWifiStatus);
        handler.removeCallbacks(runDarkModeStatus);
        handler.removeCallbacks(runAppStatusAwareness);
    }

    private void startGetStatusProcess() {
        startDarkModeAwareness();
        startWifiStatusAwareness();
        startScreenStatusAwareness();
        startAppStatusAwareness();
    }

    private void startScreenStatusAwareness() {
        runScreenStatus = new Runnable() {
            @Override
            public void run() {
                Awareness.getCaptureClient(requireActivity()).getScreenStatus()
                        .addOnSuccessListener(screenStatusResponse -> {
                            int state = screenStatusResponse.getScreenStatus().getStatus();

                            if (tempScreenStatus != state) {
                                switch (state) {
                                    case ScreenStatus.UNKNOWN:
                                        Log.i(phoneStatusAwarenessTag, "it is unknown");
                                        tempScreenStatus = ScreenStatus.UNKNOWN;
                                        binding.tvScreenStatusTextPhonestatusawareness.setText("unknown");
                                        break;
                                    case ScreenStatus.SCREEN_OFF:
                                        Log.i(phoneStatusAwarenessTag, "it is screen off");
                                        tempScreenStatus = ScreenStatus.SCREEN_OFF;
                                        binding.tvScreenStatusTextPhonestatusawareness.setText("screen off");
                                        break;
                                    case ScreenStatus.SCREEN_ON:
                                        Log.i(phoneStatusAwarenessTag, "it is screen on");
                                        tempScreenStatus = ScreenStatus.SCREEN_ON;
                                        binding.tvScreenStatusTextPhonestatusawareness.setText("screen on");
                                        break;
                                    case ScreenStatus.UNLOCK:
                                        Log.i(phoneStatusAwarenessTag, "it is unlock");
                                        tempScreenStatus = ScreenStatus.UNLOCK;
                                        binding.tvScreenStatusTextPhonestatusawareness.setText("unlock");
                                        break;
                                    default:
                                        Log.i(phoneStatusAwarenessTag, "it is sth wrong");
                                        break;
                                }
                            }

                        })
                        .addOnFailureListener(e -> {
                            Log.e(phoneStatusAwarenessTag, "get screen state failed" + e.getMessage());
                            e.printStackTrace();
                        });

                handler.postDelayed(this, 3000);
            }
        };
    }

    private void startWifiStatusAwareness() {

        runWifiStatus = new Runnable() {
            @Override
            public void run() {

                Awareness.getCaptureClient(requireActivity()).getWifiStatus()
                        .addOnSuccessListener(wifiStatusResponse -> {
                            WifiStatus status = wifiStatusResponse.getWifiStatus();
                            String stateMsg = "UNKNOWN";
                            String bssid = null;
                            String ssid = null;

                            if (tempWifiStatus != status.getStatus()) {

                                switch (status.getStatus()) {
                                    case WifiStatus.CONNECTED:
                                        stateMsg = "connected";
                                        bssid = status.getBssid();
                                        ssid = status.getSsid();
                                        tempWifiStatus = WifiStatus.CONNECTED;
                                        binding.tvWifiStatusTextPhonestatusawareness.setText(stateMsg);
                                        break;
                                    case WifiStatus.ENABLED:
                                        stateMsg = "enabled";
                                        tempWifiStatus = WifiStatus.ENABLED;
                                        binding.tvWifiStatusTextPhonestatusawareness.setText(stateMsg);
                                        break;
                                    case WifiStatus.DISABLED:
                                        stateMsg = "disabled";
                                        tempWifiStatus = WifiStatus.ENABLED;
                                        binding.tvWifiStatusTextPhonestatusawareness.setText(stateMsg);
                                        break;
                                    default:
                                        break;
                                }
                                Log.i(phoneStatusAwarenessTag, "stateMsg: " + stateMsg + ", bssid: " + bssid + ", ssid: " + ssid);

                            }


                        })
                        .addOnFailureListener(e -> {
                            Log.e(phoneStatusAwarenessTag, "get wifi status failed", e);
                        });
                handler.postDelayed(this, 3000);
            }
        };

    }

    private void startDarkModeAwareness() {

        runDarkModeStatus = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                Awareness.getCaptureClient(requireActivity()).getDarkModeStatus()
                        .addOnSuccessListener(darkModeStatusResponse -> {
                            DarkModeStatus darkModeStatus = darkModeStatusResponse.getDarkModeStatus();
                            if (darkModeStatus.isDarkModeOn()) {
                                Log.i(phoneStatusAwarenessTag, "dark mode is on");
                                binding.tvDarkModeTextPhonestatusawareness.setText("dark mode is on");
                            } else {
                                Log.i(phoneStatusAwarenessTag, "dark mode is off");
                                binding.tvDarkModeTextPhonestatusawareness.setText("dark mode is off");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(phoneStatusAwarenessTag, "get darkMode status failed", e);
                        });
                handler.postDelayed(this, 3000);
            }

        };
    }

    private void startAppStatusAwareness() {

        String pkgName = requireActivity().getPackageName();
        runAppStatusAwareness = new Runnable() {
            @Override
            public void run() {
                Awareness.getCaptureClient(requireActivity())
                        .getApplicationStatus(pkgName)
                        .addOnSuccessListener(
                                applicationStatusResponse -> {
                                    int state = applicationStatusResponse.getApplicationStatus().getStatus();
                                    switch (state) {
                                        case ApplicationStatus.UNKNOWN:
                                            binding.tvAppStatusTextPhonestatusawareness.setText("app is unknown");
                                            Log.i(phoneStatusAwarenessTag, "app is unknown");
                                            break;
                                        case ApplicationStatus.SILENT:
                                            binding.tvAppStatusTextPhonestatusawareness.setText("app is silent");
                                            Log.i(phoneStatusAwarenessTag, "app is silent");
                                            break;
                                        case ApplicationStatus.RUNNING:
                                            binding.tvAppStatusTextPhonestatusawareness.setText("app is running");
                                            Log.i(phoneStatusAwarenessTag, "app is running");
                                            break;
                                        default:
                                            binding.tvAppStatusTextPhonestatusawareness.setText("sth wrong");
                                            Log.i(phoneStatusAwarenessTag, "app is sth wrong");
                                            break;
                                    }
                                })
                        .addOnFailureListener(
                                e -> {
                                    Log.d(phoneStatusAwarenessTag, "get application status failed");
                                });
                handler.postDelayed(this, 3000);
            }
        };


    }
}