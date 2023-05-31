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

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.hms.explorehms.huawei.feature_awarenesskit.R;
import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentLightAwarenessBinding;
import com.hms.explorehms.huawei.feature_awarenesskit.receivers.BarrierReceiver;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AmbientLightBarrier;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.status.AmbientLightStatus;

public class LightAwarenessFragment extends Fragment {


    private final static String ambientLightAwarenessTag = "AmbientLightAwareness";

    private FragmentLightAwarenessBinding binding;

    private BarrierUpdateRequest.Builder builder;

    private Handler handler;
    private Runnable run;
    private float tempLightStatus = 0.0F;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLightAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Ambient Light Awareness");

        binding.tvLightAwarenessInfo.setText(getResources().getString(R.string.light_awareness_info));

        getActivity().setTitle("Ambient Light Awareness");
        handler = new Handler();
        builder = new BarrierUpdateRequest.Builder();

        initUI();
    }

    private void initUI() {
        getLightIntensity();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.lightAwarenessMethods));
        binding.spinnerMethodsLightawareness.setAdapter(adapter);

        binding.spinnerMethodsLightawareness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 2) {
                    deleteBarriers();
                    setUIForRangeCondition();
                } else {
                    deleteBarriers();
                    setUIForAboveBelowCondition();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(ambientLightAwarenessTag, "Nothing selected");
            }
        });

        binding.btnSetBarrierLightawareness.setOnClickListener(view -> {
            switch (binding.spinnerMethodsLightawareness.getSelectedItemPosition()) {
                case 0:
                    if (twoInputEmptyValidator(binding.edtTriggerValueLightawareness, binding.edtBarrierLabelLightawareness)) {
                        AwarenessBarrier lightAboveBarrier = AmbientLightBarrier.above(
                                Float.parseFloat(binding.edtTriggerValueLightawareness.getEditableText().toString())
                        );

                        initBarrier(
                                lightAboveBarrier,
                                binding.edtBarrierLabelLightawareness.getEditableText().toString()
                        );

                    }
                    break;
                case 1:
                    if (twoInputEmptyValidator(binding.edtTriggerValueLightawareness, binding.edtBarrierLabelLightawareness)) {
                        AwarenessBarrier lightBelowBarrier = AmbientLightBarrier.below(
                                Float.parseFloat(binding.edtTriggerValueLightawareness.getEditableText().toString())
                        );

                        initBarrier(
                                lightBelowBarrier,
                                binding.edtBarrierLabelLightawareness.getEditableText().toString()
                        );
                    }
                    break;
                case 2:

                    if (threeInputEmptyValidator(binding.edtRangeStartLightawareness, binding.edtRangeEndLightawareness, binding.edtBarrierLabelLightawareness)) {

                        AwarenessBarrier lightRangeBarrier = AmbientLightBarrier.range(
                                Float.parseFloat(binding.edtRangeStartLightawareness.getEditableText().toString()),
                                Float.parseFloat(binding.edtRangeEndLightawareness.getEditableText().toString())
                        );

                        initBarrier(
                                lightRangeBarrier,
                                binding.edtBarrierLabelLightawareness.getEditableText().toString()
                        );
                    }
                    break;
                default:
                    break;
            }

        });
    }

    @Override
    public void onDestroyView() {
        deleteBarriers();
        super.onDestroyView();
    }

    /**
     * Setting barrier and barrier label
     *
     * @param barrier
     * @param message
     */
    private void initBarrier(AwarenessBarrier barrier, String message) {

        final String BARRIER_RECEIVER_ACTION = requireActivity().getPackageName() + "LIGHT_BARRIER_RECEIVER_ACTION";
        Intent intent = new Intent(BARRIER_RECEIVER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BarrierReceiver barrierReceiver = new BarrierReceiver();
        requireActivity().registerReceiver(barrierReceiver, new IntentFilter(BARRIER_RECEIVER_ACTION));

        String lightBarrierLabel = "Ambient Light Awareness:+" + message;
        BarrierUpdateRequest request = builder.addBarrier(lightBarrierLabel, barrier, pendingIntent).build();
        Awareness.getBarrierClient(requireActivity()).updateBarriers(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "add barrier success", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(ambientLightAwarenessTag, "add barrier failed", e));

    }

    /**
     * delete all set barriers
     */
    private void deleteBarriers() {
        BarrierUpdateRequest deleteRequest = builder.deleteAll().build();
        Awareness.getBarrierClient(requireActivity()).updateBarriers(deleteRequest)
                .addOnSuccessListener(aVoid -> {

                        }
                ).addOnFailureListener(e -> Log.e(ambientLightAwarenessTag, "delete barriers failed", e));
    }


    /**
     * Runnable function for tracing ambient light intensity.
     */
    private void getLightIntensity() {

        run = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                Awareness.getCaptureClient(requireActivity()).getLightIntensity()
                        .addOnSuccessListener(ambientLightResponse -> {
                            AmbientLightStatus ambientLightStatus = ambientLightResponse.getAmbientLightStatus();
                            float temp = ambientLightStatus.getLightIntensity();

                            if (tempLightStatus != temp) {
                                tempLightStatus = temp;
                                binding.tvCurrentLightText.setText(tempLightStatus + " lux");

                            }
                            Log.i(ambientLightAwarenessTag, "ambient light");

                        }).addOnFailureListener(e ->
                        Log.e(ambientLightAwarenessTag, "Error: " + e.getMessage()));
                handler.postDelayed(this, 2000);
            }
        };
    }

    private void setUIForRangeCondition() {
        binding.layEdt1Lightawareness.setVisibility(View.INVISIBLE);

        binding.layEdt3Lightawareness.setVisibility(View.VISIBLE);
        binding.layEdt4Lightawareness.setVisibility(View.VISIBLE);
    }

    private void setUIForAboveBelowCondition() {
        binding.layEdt3Lightawareness.setVisibility(View.INVISIBLE);
        binding.layEdt4Lightawareness.setVisibility(View.INVISIBLE);

        binding.layEdt1Lightawareness.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(run);
        Log.i(ambientLightAwarenessTag, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(run, 2000);
        Log.i(ambientLightAwarenessTag, "onResume: ");
    }

    /**
     * empty control for two editText
     *
     * @param editText1
     * @param editText2
     * @return
     */
    private Boolean twoInputEmptyValidator(TextInputEditText editText1, TextInputEditText editText2) {

        int temp = 0;

        if (editText1.getEditableText().toString().trim().isEmpty()) {
            editText1.setError(getString(R.string.fill_the_empty_area));
            temp++;
        }

        if (editText2.getEditableText().toString().trim().isEmpty()) {
            editText2.setError(getString(R.string.fill_the_empty_area));
            temp++;
        }

        return temp == 0;
    }

    /**
     * Empty text control function for three input.
     *
     * @param editText1
     * @param editText2
     * @param editText3
     * @return
     */

    private Boolean threeInputEmptyValidator(TextInputEditText editText1, TextInputEditText editText2, TextInputEditText editText3) {

        int temp = 0;

        if (editText1.getEditableText().toString().trim().isEmpty()) {
            editText1.setError(getString(R.string.fill_the_empty_area));
            temp++;
        }

        if (editText2.getEditableText().toString().trim().isEmpty()) {
            editText2.setError(getString(R.string.fill_the_empty_area));
            temp++;
        }

        if (editText3.getEditableText().toString().trim().isEmpty()) {
            editText3.setError(getString(R.string.fill_the_empty_area));
            temp++;
        }

        return temp == 0;
    }

}