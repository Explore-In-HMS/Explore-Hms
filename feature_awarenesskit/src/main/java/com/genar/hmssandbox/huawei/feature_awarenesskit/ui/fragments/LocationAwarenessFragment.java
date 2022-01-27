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

package com.genar.hmssandbox.huawei.feature_awarenesskit.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.genar.hmssandbox.huawei.feature_awarenesskit.R;
import com.genar.hmssandbox.huawei.feature_awarenesskit.databinding.FragmentLocationAwarenessBinding;
import com.genar.hmssandbox.huawei.feature_awarenesskit.receivers.BarrierReceiver;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.barrier.LocationBarrier;


public class LocationAwarenessFragment extends Fragment {


    private final static String locationAwarenessTag = "LocationAwareness";
    private final static String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
    private final static String accessBackgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION;

    private FragmentLocationAwarenessBinding binding;


    private AwarenessBarrier enterBarrier;
    private AwarenessBarrier exitBarrier;
    private AwarenessBarrier stayBarrier;

    private BarrierUpdateRequest.Builder builder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationAwarenessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 101);
        builder = new BarrierUpdateRequest.Builder();
        binding.tvLocationAwarenessInfo.setText(getActivity().getResources().getString(R.string.location_awareness_info));

        getActivity().setTitle("Location Awareness");


        if (ActivityCompat.checkSelfPermission(requireActivity(), accessFineLocation) == PackageManager.PERMISSION_DENIED &&
                ActivityCompat.checkSelfPermission(requireActivity(), accessBackgroundLocation) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{accessFineLocation, accessBackgroundLocation}, 101);
        } else {
            getCurrentLocation();
        }

        initUI();
    }

    private void initUI() {
        binding.btnSetEnterBarrierLocationawareness.setOnClickListener(view -> {
            setBarriers(enterBarrier, "enter barrier");
            binding.btnSetEnterBarrierLocationawareness.setClickable(false);
            binding.btnSetEnterBarrierLocationawareness.setAlpha(0.8f);
        });
        binding.btnSetExitBarrierLocationawareness.setOnClickListener(view -> {
            setBarriers(exitBarrier, "exit barrier");
            binding.btnSetExitBarrierLocationawareness.setClickable(false);
            binding.btnSetExitBarrierLocationawareness.setAlpha(0.8f);
        });
        binding.btnSetStayBarrierLocationawareness.setOnClickListener(view -> {
            setBarriers(stayBarrier, "stay barrier");
            binding.btnSetStayBarrierLocationawareness.setClickable(false);
            binding.btnSetStayBarrierLocationawareness.setAlpha(0.8f);
        });
        binding.btnDeleteBarriersLocationawareness.setOnClickListener(view -> {
            deleteBarriers();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_DENIED) && (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                Toast.makeText(requireActivity(), "Permission denied.", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            } else {
                getCurrentLocation();
            }
        }
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    private void getCurrentLocation() {
        Awareness.getCaptureClient(requireActivity()).getLocation()
                .addOnSuccessListener(locationResponse -> {
                    Location location = locationResponse.getLocation();

                    Log.i(locationAwarenessTag, "Longitude:" + location.getLongitude()
                            + ",Latitude:" + location.getLatitude());

                    Location currLocation = new Location("");
                    currLocation.setLatitude(location.getLatitude());
                    currLocation.setLongitude(location.getLongitude());

                    binding.tvCurrentLocationLocationawareness.setText(currLocation.getLatitude() + "," + currLocation.getLongitude());

                    initBarriers(location);

                })
                .addOnFailureListener(e -> Log.e(locationAwarenessTag, "get location failed", e));
    }

    @Override
    public void onDestroyView() {
        deleteBarriers();
        super.onDestroyView();
    }

    /**
     * delete all set barriers
     */
    private void deleteBarriers() {
        BarrierUpdateRequest deleteRequest = builder.deleteAll().build();

        Awareness.getBarrierClient(requireActivity()).updateBarriers(deleteRequest)
                .addOnSuccessListener(aVoid ->
                        {
                            binding.btnSetEnterBarrierLocationawareness.setClickable(true);
                            binding.btnSetEnterBarrierLocationawareness.setAlpha(1f);

                            binding.btnSetExitBarrierLocationawareness.setClickable(true);
                            binding.btnSetExitBarrierLocationawareness.setAlpha(1f);

                            binding.btnSetStayBarrierLocationawareness.setClickable(true);
                            binding.btnSetStayBarrierLocationawareness.setAlpha(1f);
                        }
                ).addOnFailureListener(e -> {
            binding.btnSetEnterBarrierLocationawareness.setClickable(true);
            binding.btnSetEnterBarrierLocationawareness.setAlpha(1f);

            binding.btnSetExitBarrierLocationawareness.setClickable(true);
            binding.btnSetExitBarrierLocationawareness.setAlpha(1f);

            binding.btnSetStayBarrierLocationawareness.setClickable(true);
            binding.btnSetStayBarrierLocationawareness.setAlpha(1f);

            Log.e(locationAwarenessTag, "delete barriers failed", e);
        });
    }

    @SuppressLint("MissingPermission")
    private void initBarriers(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double radius = 200;

        enterBarrier = LocationBarrier.enter(latitude, longitude, radius);
        exitBarrier = LocationBarrier.exit(latitude, longitude, radius);
        stayBarrier = LocationBarrier.stay(latitude, longitude, radius, 2000);

    }

    private void setBarriers(AwarenessBarrier barrier, String message) {
        final String BARRIER_RECEIVER_ACTION = requireActivity().getApplication().getPackageName() + "LOCATION_BARRIER_RECEIVER_ACTION";
        Intent intent = new Intent(BARRIER_RECEIVER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BarrierReceiver barrierReceiver = new BarrierReceiver();
        requireActivity().registerReceiver(barrierReceiver, new IntentFilter(BARRIER_RECEIVER_ACTION));

        String locationBarrierLabel = "Location Awareness:+" + message;

        BarrierUpdateRequest request = builder.addBarrier(locationBarrierLabel, barrier, pendingIntent).build();
        Awareness.getBarrierClient(requireContext()).updateBarriers(request)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(requireContext(), "add barrier success", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "add barrier failed" + e, Toast.LENGTH_SHORT).show();
                    Log.e(locationAwarenessTag, "add barrier failed", e);
                });

    }
}