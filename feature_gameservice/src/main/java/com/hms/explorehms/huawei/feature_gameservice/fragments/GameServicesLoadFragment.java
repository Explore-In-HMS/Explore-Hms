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

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.hms.explorehms.huawei.feature_gameservice.R;
import com.hms.explorehms.huawei.feature_gameservice.dao.ILoadItemClickListener;
import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServicesLoadBinding;
import com.hms.explorehms.huawei.feature_gameservice.fragments.adapters.LoadAdapterGameService;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.jos.games.ArchivesClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.archive.Archive;
import com.huawei.hms.jos.games.archive.ArchiveSummary;
import com.huawei.hms.jos.games.archive.OperationResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GameServicesLoadFragment extends BaseFragmentGameServices<FragmentGameServicesLoadBinding> implements ILoadItemClickListener {


    private static final String TAG = "GameServicesLoadFragment";

    private ItemTouchHelper itemTouchHelper;

    private List<ArchiveSummary> data;
    private LoadAdapterGameService adapterGameService;


    @Override
    FragmentGameServicesLoadBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServicesLoadBinding.inflate(inflater, container, false);
    }

    @Override
    void initializeUI() {

        initTouchHelper();
        data = new ArrayList<>();
        adapterGameService = new LoadAdapterGameService(new ArrayList<>(), this);
        view.rvLoadGameservices.setAdapter(adapterGameService);

        itemTouchHelper.attachToRecyclerView(view.rvLoadGameservices);

        obtainArchiveData();
    }

    private void obtainArchiveData() {
        ArchivesClient archivesClient = Games.getArchiveClient(requireActivity());
        Task<List<ArchiveSummary>> task = archivesClient.getArchiveSummaryList(true);
        task.addOnSuccessListener(archiveSummaries -> {
            if (archiveSummaries != null) {
                data = archiveSummaries;
                adapterGameService.updateData(archiveSummaries);
            }
            view.progressLoadScreenGameservices.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            view.progressLoadScreenGameservices.setVisibility(View.GONE);
            Log.e(TAG, "onFailure: " + e.getMessage());
        });
    }

    @Override
    public void onLoadItemClick(ArchiveSummary archiveSummary) {
        view.progressLoadScreenGameservices.setVisibility(View.VISIBLE);
        ArchivesClient client = Games.getArchiveClient((Activity) requireContext());
        Task<OperationResult> task = client.loadArchiveDetails(archiveSummary);
        task.addOnSuccessListener(operationResult -> {
            Archive archive = operationResult.getArchive();
            if (archive != null && archive.getSummary() != null) {
                try {
                    byte[] archiveData = archive.getDetails().get();
                    String[] gameInfo = new String(archiveData).split(",");
                    GameServicesLoadFragmentDirections.ActionGotoGameFragmentFromLoadFragment action =
                            GameServicesLoadFragmentDirections.actionGotoGameFragmentFromLoadFragment();
                    action.setMilliLeft(Long.parseLong(gameInfo[0]));
                    action.setScore(Integer.parseInt(gameInfo[1]));
                    view.progressLoadScreenGameservices.setVisibility(View.GONE);
                    navController.navigate(action);

                } catch (IOException e) {
                    view.progressLoadScreenGameservices.setVisibility(View.GONE);
                    Log.e(TAG, "IOexception: " + e.getMessage());
                }
            }

        }).addOnFailureListener(e -> {
            view.progressLoadScreenGameservices.setVisibility(View.GONE);
            Log.e(TAG, "onLoadItemClick: " + e.getMessage());
        });
    }

    private void initTouchHelper() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteSave(data.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Drawable icon = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_delete_gameservice
                );
                ColorDrawable background = new ColorDrawable(Color.RED);

                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;
                if (dX < 0) { // Swiping to the left
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        itemTouchHelper = new ItemTouchHelper(callback);
    }

    private void deleteSave(ArchiveSummary summary, int position) {
        view.progressLoadScreenGameservices.setVisibility(View.VISIBLE);
        Task<String> removeArchiveTask = Games.getArchiveClient((Activity) requireContext()).removeArchive(summary);
        removeArchiveTask.addOnSuccessListener(s -> {
            data.remove(position);
            adapterGameService.updateData(data);
            view.progressLoadScreenGameservices.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            view.progressLoadScreenGameservices.setVisibility(View.GONE);
            adapterGameService.updateData(data);
            Log.e(TAG, "deleteSave: " + e.getMessage());
        });
    }

}