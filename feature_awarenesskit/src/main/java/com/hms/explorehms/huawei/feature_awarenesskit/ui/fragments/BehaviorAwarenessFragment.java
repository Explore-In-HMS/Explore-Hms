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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentBehaviorAwarenessBinding;
import com.hms.explorehms.huawei.feature_awarenesskit.receivers.BarrierReceiver;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.barrier.BehaviorBarrier;
import com.huawei.hms.kit.awareness.status.BehaviorStatus;
import com.huawei.hms.kit.awareness.status.DetectedBehavior;

public class BehaviorAwarenessFragment extends Fragment {


    private final static String behaviorAwarenessTag = "BehaviorAwareness";

    private FragmentBehaviorAwarenessBinding binding;

    private BarrierUpdateRequest.Builder builder;
    private Handler handler;
    private Runnable run;


    private int behaviorId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBehaviorAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Behavior Awareness");

        binding.tvBehaviorAwarenessInfo.setText(getActivity().getResources().getString(R.string.behavior_awareness_info));

        builder = new BarrierUpdateRequest.Builder();
        handler = new Handler();

        getActivity().setTitle("Behavior Awareness");

        checkPermission();

        initUI();
    }

    @SuppressLint("MissingPermission")
    private void initUI() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, requireActivity().getResources().getStringArray(R.array.behaviorInfo));
        binding.spinnerMethodsBehaviorawareness.setAdapter(adapter);


        binding.btnSetBarrierBehaviorawareness.setOnClickListener(view -> {
            int methodInd = binding.spinnerMethodsBehaviorawareness.getSelectedItemPosition();

            if (methodInd == 5) {
                AwarenessBarrier barrier = BehaviorBarrier.keeping(7);
                String barrierLabel = getResources().getStringArray(R.array.behaviorInfo)[5];
                initBarrier(barrier, barrierLabel);
                //7
            } else if (methodInd == 6) {
                //8
                AwarenessBarrier barrier = BehaviorBarrier.keeping(8);
                String barrierLabel = getResources().getStringArray(R.array.behaviorInfo)[6];
                initBarrier(barrier, barrierLabel);
            } else if (methodInd == 4) {
                Toast.makeText(requireContext(), "Cannot set barrier unknown", Toast.LENGTH_SHORT).show();
            } else {
                AwarenessBarrier barrier = BehaviorBarrier.keeping(methodInd);
                String barrierLabel = getResources().getStringArray(R.array.behaviorInfo)[methodInd];
                initBarrier(barrier, barrierLabel);
            }

        });

        binding.spinnerMethodsBehaviorawareness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deleteBarriers();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(behaviorAwarenessTag, "Nothing selected");
            }
        });

    }

    @Override
    public void onDestroyView() {
        deleteBarriers();
        super.onDestroyView();
    }

    /**
     * function that we are tracing current behavior
     */
    @SuppressLint("MissingPermission")
    private void getBehavior() {
        run = new Runnable() {
            @Override
            public void run() {
                Awareness.getCaptureClient(requireActivity()).getBehavior()
                        .addOnSuccessListener(behaviorResponse -> {
                            BehaviorStatus behaviorStatus = behaviorResponse.getBehaviorStatus();
                            DetectedBehavior mostLikelyBehavior = behaviorStatus.getMostLikelyBehavior();

                            int temp = mostLikelyBehavior.getType();

                            if (behaviorId != temp) {
                                behaviorId = temp;

                                if (behaviorId == 7)
                                    binding.tvDetectedBehaviorBehaviorawareness.setText(getResources().getStringArray(R.array.behaviorInfo)[5]);
                                else if (behaviorId == 8)
                                    binding.tvDetectedBehaviorBehaviorawareness.setText(getResources().getStringArray(R.array.behaviorInfo)[6]);
                                else
                                    binding.tvDetectedBehaviorBehaviorawareness.setText(getResources().getStringArray(R.array.behaviorInfo)[behaviorId]);
                            }

                            Log.i(behaviorAwarenessTag, "behavior");

                        }).addOnFailureListener(e -> Log.e(behaviorAwarenessTag, "Error: " + e.getMessage()));

                handler.postDelayed(this, 1000);

            }
        };

    }

    /**
     * setting barrier and barrier label
     *
     * @param barrier
     * @param message
     */
    private void initBarrier(AwarenessBarrier barrier, String message) {

        final String BARRIER_RECEIVER_ACTION = requireActivity().getApplication().getPackageName() + "BEHAVIOR_BARRIER_RECEIVER_ACTION";
        Intent intent = new Intent(BARRIER_RECEIVER_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BarrierReceiver barrierReceiver = new BarrierReceiver();
        requireActivity().registerReceiver(barrierReceiver, new IntentFilter(BARRIER_RECEIVER_ACTION));

        String behaviorBarrierLabel = "Behavior Awareness:+" + message;
        BarrierUpdateRequest request = builder.addBarrier(behaviorBarrierLabel, barrier, pendingIntent).build();
        Awareness.getBarrierClient(requireActivity()).updateBarriers(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "add barrier success", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> {
                    Toast.makeText(requireContext(), "add barrier failed", Toast.LENGTH_SHORT).show();
                    Log.e(behaviorAwarenessTag, "Error " + error.getMessage());
                });
    }

    /**
     * delete all set barriers
     */
    private void deleteBarriers() {
        BarrierUpdateRequest deleteRequest = builder.deleteAll().build();

        Awareness.getBarrierClient(requireActivity()).updateBarriers(deleteRequest)
                .addOnSuccessListener(aVoid ->
                        Log.i(behaviorAwarenessTag, "deleteBarriers: success")
                ).addOnFailureListener(e ->
                        Log.e(behaviorAwarenessTag, "delete barriers failed", e));
    }


    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(run);
        Log.i(behaviorAwarenessTag, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(run, 1000);
        Log.i(behaviorAwarenessTag, "onResume: ");
    }

    /**
     * permission control
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), "com.huawei.hms.permission.ACTIVITY_RECOGNITION") ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = new String[]{"com.huawei.hms.permission.ACTIVITY_RECOGNITION"};
                requestPermissions(permission, 101);
            } else {
                //permission granted
                getBehavior();

            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = new String[]{Manifest.permission.ACTIVITY_RECOGNITION};
                requestPermissions(permission, 101);
            } else {
                //permission granted
                getBehavior();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            } else {
                checkPermission();
            }
        }
    }

}