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

package com.hms.explorehms.huawei.feature_gameservice.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_gameservice.R;
import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServicesHomeBinding;

/**
 * This shows the features that the Game Service has, such as playing games, getting player stats, or Game Turbo Engine.
 */
public class GameServicesHomeFragment extends BaseFragmentGameServices<FragmentGameServicesHomeBinding> {

    /**
     * It handles binding
     */
    @Override
    FragmentGameServicesHomeBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServicesHomeBinding.inflate(inflater, container, false);
    }

    /**
     * Initializes the UI to show game services.
     */
    @Override
    void initializeUI() {
        setTitle("Game Services");

        view.btnPlayerStatisticsGameservices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoGameServicesPlayerStatisticsFragment));

        view.btnEventsGameservices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoGameServicesEventsFragment));

        view.btnGameTurboEngineGameservices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoGameServicesGameTurboEngineFragment));

        view.btnFloatingWindowGameservices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoGameServicesFloatingWindowFragment));

        view.btnAddictionPreventionGameservices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoGameServicesAddictionPreventionFragment));
        view.btnPlayGameservices.setOnClickListener(view1 -> {
            GameServicesHomeFragmentDirections.ActionGotoGameServicesGameFragment action =
                    GameServicesHomeFragmentDirections.actionGotoGameServicesGameFragment();
            action.setMilliLeft(0L);
            action.setScore(0);
            navController.navigate(action);
        });

        view.btnLoadGameservices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_gotoGameServicesLoadFragment));
    }

}