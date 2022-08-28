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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServicesEventsBinding;
import com.hms.explorehms.huawei.feature_gameservice.fragments.adapters.GameServiceEventAdapter;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.jos.games.event.Event;

import java.util.ArrayList;
import java.util.List;

public class GameServicesEventsFragment extends BaseFragmentGameServices<FragmentGameServicesEventsBinding> {

    private static final String TAG = "GameServicesEventsFragment";

    @Override
    FragmentGameServicesEventsBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServicesEventsBinding.inflate(inflater, container, false);
    }

    @Override
    void initializeUI() {
        setTitle("Game Service Events");
        GameServiceEventAdapter adapter = new GameServiceEventAdapter(new ArrayList<>(), requireContext());
        view.rvEventsGameservices.setAdapter(adapter);

        Task<List<Event>> task = eventsClient.getEventList(true);

        task.addOnSuccessListener(list -> {
            if (list == null) {
                Log.e(TAG, "onSuccess: Event is null");
                return;
            }
            adapter.updateList(list);
        });


    }
}