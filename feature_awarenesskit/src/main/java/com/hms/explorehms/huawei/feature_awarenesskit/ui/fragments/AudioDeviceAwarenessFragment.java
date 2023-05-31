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

package com.hms.explorehms.huawei.feature_awarenesskit.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentAudioDeviceAwarenessBinding;
import com.hms.explorehms.huawei.feature_awarenesskit.receivers.BarrierReceiver;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.barrier.BluetoothBarrier;
import com.huawei.hms.kit.awareness.barrier.HeadsetBarrier;
import com.huawei.hms.kit.awareness.status.BluetoothStatus;
import com.huawei.hms.kit.awareness.status.HeadsetStatus;


public class AudioDeviceAwarenessFragment extends Fragment {


    private final static String audioDeviceAwarenessTag = "AudioDeviceAwareness";

    private FragmentAudioDeviceAwarenessBinding binding;


    private BarrierUpdateRequest.Builder builder;
    private Handler handler;
    private Runnable runHeadset;
    private Runnable runBluetoothDevice;


    private String tempHeadsetStatus = "";
    private String tempBluetoothDeviceStatus = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAudioDeviceAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Audio Device Status Awareness");

        handler = new Handler(Looper.getMainLooper());
        builder = new BarrierUpdateRequest.Builder();

        getActivity().setTitle("Audio Device Awareness");

        checkPermission();

        initUI();
    }


    @SuppressLint("MissingPermission")
    private void initUI() {
        getCurrentHeadsetStatus();
        getCurrentBluetoothCarStereoStatus();


        binding.btnSetHeadsetConnectingBarrier.setOnClickListener(view -> {
            if (binding.edtHeadsetConnecting.getEditableText().toString().isEmpty()) {
                binding.edtHeadsetConnecting.setError("Field cannot empty.");
            } else {
                AwarenessBarrier headsetConnectingBarrier = HeadsetBarrier.connecting();
                initBarrier(
                        headsetConnectingBarrier,
                        binding.edtHeadsetConnecting.getEditableText().toString(),
                        "Headset Awareness:"
                );
                binding.btnSetHeadsetConnectingBarrier.setClickable(false);
                binding.btnSetHeadsetConnectingBarrier.setAlpha(0.8f);
            }
        });

        binding.btnSetBluetoothDeviceConnectingBarrier.setOnClickListener(view -> {
            if (binding.edtBluetoothDeviceConnecting.getEditableText().toString().isEmpty()) {
                binding.edtBluetoothDeviceConnecting.setError("Field cannot empty.");
            } else {
                AwarenessBarrier carStereoConnectingBarrier = BluetoothBarrier.connecting(0);
                initBarrier(
                        carStereoConnectingBarrier,
                        binding.edtBluetoothDeviceConnecting.getEditableText().toString(),
                        "Bluetooth Device Awareness:"
                );
                binding.btnSetBluetoothDeviceConnectingBarrier.setClickable(false);
                binding.btnSetBluetoothDeviceConnectingBarrier.setAlpha(0.8f);
            }

        });

        binding.btnDeleteBarriersAudiodeviceawareness.setOnClickListener(view -> deleteBarriers());
    }

    private void initBarrier(AwarenessBarrier barrier, String message, String method) {
        final String BARRIER_RECEIVER_ACTION = requireActivity().getApplication().getPackageName() + "HEADSET_BARRIER_RECEIVER_ACTION";
        Intent intent = new Intent(BARRIER_RECEIVER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BarrierReceiver barrierReceiver = new BarrierReceiver();
        requireActivity().registerReceiver(barrierReceiver, new IntentFilter(BARRIER_RECEIVER_ACTION));

        String headsetBarrierLabel = method + "+" + message;
        BarrierUpdateRequest request = builder.addBarrier(headsetBarrierLabel, barrier, pendingIntent).build();
        Awareness.getBarrierClient(requireActivity()).updateBarriers(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "add barrier success", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(audioDeviceAwarenessTag, "add barrier failed", e));

    }

    /**
     * function that we are tracing current headset status
     */
    private void getCurrentHeadsetStatus() {

        runHeadset = new Runnable() {
            @Override
            public void run() {
                Awareness.getCaptureClient(requireActivity())
                        .getHeadsetStatus()
                        .addOnSuccessListener(headsetStatusResponse -> {

                            HeadsetStatus headsetStatus = headsetStatusResponse.getHeadsetStatus();
                            int status = headsetStatus.getStatus();
                            String stateStr = "Headsets are " +
                                    (status == HeadsetStatus.CONNECTED ? "connected" : "disconnected");

                            if (!tempHeadsetStatus.equals(stateStr)) {
                                tempHeadsetStatus = stateStr;
                                binding.tvCurrentHeadsetStatusText.setText(tempHeadsetStatus);
                                Log.i(audioDeviceAwarenessTag, stateStr);
                            }

                            Log.i(audioDeviceAwarenessTag, "headset");

                        })
                        .addOnFailureListener(e -> Log.e(audioDeviceAwarenessTag, "get Headsets Capture failed", e));
                handler.postDelayed(this, 2000);
            }
        };
    }

    /**
     * delete all set barriers
     */
    private void deleteBarriers() {
        BarrierUpdateRequest deleteRequest = builder.deleteAll().build();
        Awareness.getBarrierClient(requireActivity()).updateBarriers(deleteRequest)
                .addOnSuccessListener(aVoid -> {
                            binding.btnSetHeadsetConnectingBarrier.setClickable(true);
                            binding.btnSetHeadsetConnectingBarrier.setAlpha(1f);

                            binding.btnSetBluetoothDeviceConnectingBarrier.setClickable(true);
                            binding.btnSetBluetoothDeviceConnectingBarrier.setAlpha(1f);
                        }
                ).addOnFailureListener(e -> {
            binding.btnSetHeadsetConnectingBarrier.setClickable(true);
            binding.btnSetHeadsetConnectingBarrier.setAlpha(1f);

            binding.btnSetBluetoothDeviceConnectingBarrier.setClickable(true);
            binding.btnSetBluetoothDeviceConnectingBarrier.setAlpha(1f);
            Log.e(audioDeviceAwarenessTag, "delete barriers failed", e);
        });

    }

    @Override
    public void onDestroyView() {
        deleteBarriers();
        super.onDestroyView();
    }

    /**
     * function that we are tracing current bluetooth car stereo status
     */
    @SuppressLint("MissingPermission")
    private void getCurrentBluetoothCarStereoStatus() {

        runBluetoothDevice = new Runnable() {
            @Override
            public void run() {
                int deviceType = 0; // Value 0 indicates a Bluetooth car stereo.
                Awareness.getCaptureClient(requireActivity()).getBluetoothStatus(deviceType)

                        .addOnSuccessListener(bluetoothStatusResponse -> {
                            BluetoothStatus bluetoothStatus = bluetoothStatusResponse.getBluetoothStatus();
                            int status = bluetoothStatus.getStatus();
                            String stateStr = "The Bluetooth car stereo is " +
                                    (status == BluetoothStatus.CONNECTED ? "connected" : "disconnected");
                            if (!tempBluetoothDeviceStatus.equals(stateStr)) {
                                tempBluetoothDeviceStatus = stateStr;
                                binding.tvBluetoothDeviceStatusText.setText(tempBluetoothDeviceStatus);
                                Log.i(audioDeviceAwarenessTag, stateStr);
                            }

                            Log.i(audioDeviceAwarenessTag, "bluetooth");
                        })
                        .addOnFailureListener(e -> Log.e(audioDeviceAwarenessTag, "get bluetooth status failed", e));
                handler.postDelayed(this, 2000);
            }
        };

    }

    /**
     * permission control
     */
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH) ==
                PackageManager.PERMISSION_DENIED) {
            String[] permission = new String[]{"com.huawei.hms.permission.ACTIVITY_RECOGNITION"};
            requestPermissions(permission, 101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101 && grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runHeadset);
        handler.removeCallbacks(runBluetoothDevice);
        Log.i(audioDeviceAwarenessTag, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runHeadset, 2000);
        handler.postDelayed(runBluetoothDevice, 2000);
        Log.i(audioDeviceAwarenessTag, "onResume: ");
    }
}