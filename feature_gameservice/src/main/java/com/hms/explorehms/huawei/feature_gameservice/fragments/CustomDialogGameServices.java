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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.hms.explorehms.huawei.feature_gameservice.R;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ItemAchievementsGameserviceBinding;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ItemCustomviewGameservicesBinding;
import com.hms.explorehms.huawei.feature_gameservice.databinding.ItemLeaderboardsGamerserviceBinding;
import com.hms.explorehms.huawei.feature_gameservice.fragments.adapters.CommonAdapterGameService;
import com.huawei.hms.jos.games.achievement.Achievement;
import com.huawei.hms.jos.games.ranking.RankingScore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/**
 * It is an enum class
 * It holds ACHIEVEMENTS and LEADERBOARDS
 */
enum DataTypeGameService {
    ACHIEVEMENTS,
    LEADERBOARDS
}

/**
 * It handles custom dialog for game services, it takes a generic type
 */
public class CustomDialogGameServices<T> {

    private static final String SHOOTER_ID = "7618DBEFF87A659F698CBE28DF8C478492377057B97EF9DA12030F6FD6D34278";
    private static final String SURVIVOR_ID = "E3229FB852205D4ECCF43452FB61C2331D84C5C3D8A1AFEB320B352AFA9B36F4";
    private Context context;
    private Dialog dialog;
    private ItemCustomviewGameservicesBinding binding;


    /**
     * It creates dialog directly in constructor of CustomDialogGameServices
     */
    public CustomDialogGameServices(Context context, String title, DataTypeGameService dataTypeGameService, List<T> data) {
        this.context = context;


        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        dialog = new Dialog(context, R.style.CustomDialog);

        binding = ItemCustomviewGameservicesBinding.inflate(LayoutInflater.from(context));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = (int) (displayMetrics.widthPixels * 0.65);
        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.END);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(binding.getRoot());


        binding.btnCloseDialogGameservices.setOnClickListener(view -> dismissDialog());

        binding.tvTableNameGameservices.setText(
                title
        );

        if (dataTypeGameService == DataTypeGameService.ACHIEVEMENTS) {
            initRecyclerViewForAchievements(data);
        } else if (dataTypeGameService == DataTypeGameService.LEADERBOARDS) {
            initRecyclerViewForLeaderboards(data);
        }

    }

    /**
     * It inits recyclerView for Achievements
     */
    private void initRecyclerViewForAchievements(List<T> data) {

        binding.rvTableGameservices.setAdapter(
                new CommonAdapterGameService<Achievement, ItemAchievementsGameserviceBinding>((List<Achievement>) data) {

                    @Override
                    public ItemAchievementsGameserviceBinding inflateView(@NonNull ViewGroup parent, int viewType) {
                        return ItemAchievementsGameserviceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                    }

                    @Override
                    public void onBindData(Achievement data, int position) {
                        Picasso.get().load(data.getReachedThumbnailUri()).into(view.ivAchievementThumGameservice);
                        view.tvAchievementsNameGameservice.setText(
                                data.getDisplayName()
                        );
                        view.tvAchievementsInfoGameservice.setText(
                                data.getDescInfo()
                        );
                        if (data.getId().equals(SHOOTER_ID)) {
                            view.tmpGameservices.setText(
                                    R.string.give_me_time_info
                            );
                        }
                        if (data.getId().equals(SURVIVOR_ID)) {
                            view.tmpGameservices.setText(
                                    R.string.slower_info
                            );
                        }
                    }
                }
        );

    }

    /**
     * It inits recyclerView for LeaderboardS
     */
    private void initRecyclerViewForLeaderboards(List<T> data) {

        binding.rvTableGameservices.setAdapter(
                new CommonAdapterGameService<RankingScore, ItemLeaderboardsGamerserviceBinding>((List<RankingScore>) data) {

                    @Override
                    public ItemLeaderboardsGamerserviceBinding inflateView(@NonNull ViewGroup parent, int viewType) {
                        return ItemLeaderboardsGamerserviceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                    }

                    @Override
                    public void onBindData(RankingScore data, int position) {
                        view.tvUserRankGameservices.setText(
                                data.getDisplayRank()
                        );
                        view.tvUserNameGameservices.setText(
                                data.getScoreOwnerDisplayName()
                        );
                        view.tvUserPointGameservices.setText(
                                context.getResources().getString(R.string.score_gameservices_dialog, data.getRankingDisplayScore())
                        );

                    }
                }
        );

    }

    /**
     * It shows dialog
     */
    public void showDialog() {
        if (!dialog.isShowing())
            dialog.show();

    }

    /**
     * It dismiss dialog
     */
    public void dismissDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }

}
