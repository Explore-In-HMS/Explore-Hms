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

package com.genar.hmssandbox.huawei.mapkit.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.databinding.FragmentHomeMapKitBinding;


public class MapKitHomeFragment extends Fragment {


    private FragmentHomeMapKitBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeMapKitBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);


        binding.btnMoveAnimateCameraMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_goto_MapKitCameraFragment));

        binding.btnShowMarkerMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_goto_MapKitShowMarkerFragment));

        binding.btnShowCustomMarkerMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_goto_MapKitShowCustomMarkerFragment));

        binding.btnShowShapeMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_goto_MapKitShapeFragment));

        binding.btnShowTileOverlayMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_goto_MapKitTileFragment));

        binding.btnShowGroundOverlayMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.mapKitGroundOverlayFragment));

        binding.btnShowMapStyleMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_goto_MapKitMapStyleFragment));

        binding.btnShowRoutingDirectionMapkit.setOnClickListener(view1 ->
                navController.navigate(R.id.action_homeFragmentMapKit_to_mapKitDirectionApi));

        binding.btnHeatMap.setOnClickListener(view1 ->
                navController.navigate(R.id.action_homeFragmentMapKit_to_heatMapFragment));

        binding.btnOpenPetalMap.setOnClickListener(view1 -> {
            String uriString = "mapapp://textSearch?text=" + "The Eiffel Tower";
            Uri content_url = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
            startActivity(intent);
        });

    }

    }
