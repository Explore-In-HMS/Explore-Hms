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
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServiceAddictionPreventionBinding;

/**
 * This shows Addiction Prevention
 */
public class GameServiceAddictionPreventionFragment extends BaseFragmentGameServices<FragmentGameServiceAddictionPreventionBinding> {

    /**
     * Sets the binding for the layout.
     */
    @Override
    FragmentGameServiceAddictionPreventionBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServiceAddictionPreventionBinding.inflate(inflater, container, false);
    }

    /**
     * Initializes the title
     */
    @Override
    void initializeUI() {
        setTitle("Addiction Prevention");
    }
}