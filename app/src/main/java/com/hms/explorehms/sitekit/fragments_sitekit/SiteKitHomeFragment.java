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

package com.hms.explorehms.sitekit.fragments_sitekit;


import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentSiteKitHomeBinding;
import com.hms.explorehms.sitekit.SiteKitWidgetActivity;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.site.api.model.Coordinate;

import java.util.List;

public class SiteKitHomeFragment extends Fragment {


    private final String TAG = SiteKitHomeFragment.class.getSimpleName();

    // ViewBinding instance
    private FragmentSiteKitHomeBinding binding;
    private Coordinate coordinate;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private SettingsClient settingsClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    /**
     * ViewBinding process is done here.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSiteKitHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * In this module, Android's Jetpack Navigation component used for navigation between fragments.
     * Also we passed data between fragment with using Navigation component.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initLocationInstances();

        binding.buttonNearbySearch.setOnClickListener(view1 -> {
            if (locationControl()) {
                SiteKitHomeFragmentDirections.ActionGotoSiteKitNearbySearchFragment action = SiteKitHomeFragmentDirections.actionGotoSiteKitNearbySearchFragment(coordinate);
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.buttonKeywordSearch.setOnClickListener(view1 -> {
            if (locationControl()) {
                SiteKitHomeFragmentDirections.ActionGotoSiteKitTextSearchFragment action = SiteKitHomeFragmentDirections.actionGotoSiteKitTextSearchFragment(coordinate);
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.buttonSuggestion.setOnClickListener(view1 -> {
            if (locationControl()) {
                SiteKitHomeFragmentDirections.ActionGotoSiteKitQuerySuggestionFragment action = SiteKitHomeFragmentDirections.actionGotoSiteKitQuerySuggestionFragment(coordinate);
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.buttonWidget.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), SiteKitWidgetActivity.class)));

        binding.buttonAutocomplete.setOnClickListener(view1 -> Navigation.findNavController(view).navigate(R.id.action_goto_siteKitAutocompleteFragment));

    }


    /**
     * Location kit instances to get user current location.
     */
    private void initLocationInstances() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        settingsClient = LocationServices.getSettingsClient(requireActivity());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (null == mLocationCallback) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null) {
                        List<Location> locations = locationResult.getLocations();
                        if (!locations.isEmpty()) {
                            coordinate = new Coordinate(locations.get(0).getLatitude(), locations.get(0).getLongitude());
                        } else {
                            coordinate = new Coordinate(41.0008551, 29.0592601);
                        }
                    } else {
                        coordinate = new Coordinate(41.0008551, 29.0592601);
                    }
                    stopGettingLocation();
                }

            };
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentLocation();
    }

    /**
     * Some of Site kit services requires user location for receive more accurate results.
     * We used Location Kit to get user current location.
     * If something went wrong we are giving coordinate manually.
     */
    private void getCurrentLocation() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            // check devices settings before request location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        Log.i("DEBUG", "check location settings success");
                        // request location updates
                        fusedLocationProviderClient
                                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                                .addOnSuccessListener(aVoid -> {

                                })
                                .addOnFailureListener(e -> Log.e(TAG, "getCurrentLocationFailed: " + e.getMessage()));
                    })
                    .addOnFailureListener(e -> {
                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(requireActivity(), 0);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e(TAG, "PendingIntent unable to execute request.");
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "LocationFeed exception:" + e.getMessage());
        }
    }

    private boolean locationControl() {
        if (coordinate != null) {
            return true;
        } else {
            Toast.makeText(requireContext(), "Location not determined yet. Please wait.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * After getting user current location we disable location service.
     */
    private void stopGettingLocation() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                    .addOnSuccessListener(aVoid -> Log.i(TAG, "removeLocationUpdatesWithCallback onSuccess"))
                    .addOnFailureListener(e -> Log.e(TAG, "removeLocationUpdatesWithCallback onFailure:" + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "removeLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }
}