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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.huawei.feature_awarenesskit.databinding.FragmentBeaconAwarenessBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.status.BeaconStatus;

import java.util.Arrays;
import java.util.List;


public class BeaconAwarenessFragment extends Fragment {

    private final static String beaconAwarenessTag = "BeaconAwareness";
    private final static String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final static String bluetoothPermission = Manifest.permission.BLUETOOTH;

    private FragmentBeaconAwarenessBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBeaconAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Beacon Awareness");


        getActivity().setTitle("Beacon Awareess");
        checkPermission();
        initUI();

    }

    private void initUI() {
        binding.progressBeaconawareness.setVisibility(View.INVISIBLE);
        binding.btnFilterBeaconsBeaconawareness.setOnClickListener(view -> {
            if (twoInputEmptyValidator(binding.edtBeaconNameBeaconawareness, binding.edtBeaconTypeBeaconawareness)) {
                BeaconStatus.Filter filter = BeaconStatus.Filter.match(
                        binding.edtBeaconNameBeaconawareness.getEditableText().toString(),
                        binding.edtBeaconTypeBeaconawareness.getEditableText().toString()
                );
                binding.progressBeaconawareness.setVisibility(View.VISIBLE);
                binding.tvBeaconListBeaconawareness.setText("");
                filterBeacons(filter);
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void filterBeacons(BeaconStatus.Filter filter) {

        Awareness.getCaptureClient(requireActivity()).getBeaconStatus(filter)
                .addOnSuccessListener(beaconStatusResponse -> {
                    binding.progressBeaconawareness.setVisibility(View.GONE);
                    List<BeaconStatus.BeaconData> beaconDataList = beaconStatusResponse.
                            getBeaconStatus().getBeaconData();
                    if (beaconDataList != null && beaconDataList.size() != 0) {
                        int i = 1;
                        StringBuilder builder = new StringBuilder();
                        for (BeaconStatus.BeaconData beaconData : beaconDataList) {
                            builder.append("Beacon Data ").append(i);
                            builder.append(" namespace:").append(beaconData.getNamespace());
                            builder.append(",type:").append(beaconData.getType());
                            builder.append(",content:").append(Arrays.toString(beaconData.getContent()));
                            builder.append("; ");
                            i++;
                        }
                        Log.i(beaconAwarenessTag, builder.toString());

                        binding.tvBeaconListBeaconawareness.setText(builder.toString());

                    } else {
                        binding.progressBeaconawareness.setVisibility(View.GONE);
                        binding.tvBeaconListBeaconawareness.setText("No beacons match filter nearby");
                        Log.i(beaconAwarenessTag, "no beacons match filter nearby");
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBeaconawareness.setVisibility(View.GONE);
                    Log.e(beaconAwarenessTag, "get beacon status failed", e);
                });

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), bluetoothPermission) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), accessFineLocation) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{bluetoothPermission, accessFineLocation}, 101);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 101 && grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED) &&
                (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
            Toast.makeText(requireContext(), permissions.length + " Permission denied." , Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        }

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
            editText1.setError("Please fill the empty area.");
            temp++;
        }

        if (editText2.getEditableText().toString().trim().isEmpty()) {
            editText2.setError("Please fill the empty area.");
            temp++;
        }

        return temp == 0;
    }
}