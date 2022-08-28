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

package com.hms.explorehms.huawei.feature_gameservice.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServicePlayerStatisticsBinding;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.playerstats.GamePlayerStatistics;
import com.huawei.hms.jos.games.playerstats.GamePlayerStatisticsClient;


public class GameServicePlayerStatisticsFragment extends BaseFragmentGameServices<FragmentGameServicePlayerStatisticsBinding> {


    private static final String TAG = "GameServicePlayerStatistics";

    @Override
    FragmentGameServicePlayerStatisticsBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServicePlayerStatisticsBinding.inflate(inflater, container, false);
    }

    @Override
    void initializeUI() {
        setTitle("Player Statistics");

        GamePlayerStatisticsClient playerStatisticsClient = Games.getGamePlayerStatsClient(requireActivity());

        Task<GamePlayerStatistics> task = playerStatisticsClient.getGamePlayerStatistics(true);

        task.addOnSuccessListener(gamePlayerStatistics -> {

            if (gamePlayerStatistics == null) {
                Toast.makeText(requireContext(), "PlayerStats is null, innerError", Toast.LENGTH_SHORT).show();
                return;
            }

            view.tvAverageSessionInfoGameservices.setText(String.valueOf(gamePlayerStatistics.getAverageOnLineMinutes()));
            view.tvDaySinceLastInfoGameservices.setText(String.valueOf(gamePlayerStatistics.getDaysFromLastGame()));
            view.tvNumberOfPurchasesInfoGameservices.setText(String.valueOf(gamePlayerStatistics.getPaymentTimes()));
            view.tvNumberOfSessionInfoGameservices.setText(String.valueOf(gamePlayerStatistics.getOnlineTimes()));
            view.tvTotalPurchasesInfoGameservices.setText(String.valueOf(gamePlayerStatistics.getTotalPurchasesAmountRange()));
            view.tvDescribeContentsInfoGameservices.setText(String.valueOf(gamePlayerStatistics.describeContents()));


        }).addOnFailureListener(e -> {
            if (e instanceof ApiException) {
                Log.e(TAG, "getPlayerStatisticsError: " + e.getMessage());
            }
        });

    }
}