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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServiceGameTurboEngineBinding;
import com.huawei.game.gamekit.GameManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This shows Game Turbo Engine, it allows a game app to closely work with the operating system for improved performance
 */
public class GameServiceGameTurboEngineFragment extends BaseFragmentGameServices<FragmentGameServiceGameTurboEngineBinding> {


    private static final String TAG = "GameServiceGameTurboEngine";

    private GameManager gameManager;

    /**
     * Sets the binding for the layout.
     */
    @Override
    FragmentGameServiceGameTurboEngineBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServiceGameTurboEngineBinding.inflate(inflater, container, false);
    }

    /**
     * Initializes the UI to show statistics
     * It creates a JSONObject, then put data in object
     * It also initializes gameManager by GameManager
     * then it sends the object to gameManager
     */
    @Override
    void initializeUI() {
        setTitle("Game Turbo Engine");
        gameManager = GameManager.getGameManager();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MessageType", 3);
            jsonObject.put("SceneID", 1);
            jsonObject.put("Description", "Game Start");
            jsonObject.put("ImportantLevel", 2);
            jsonObject.put("Status", 1);
            jsonObject.put("RecommendFps", 60);
            jsonObject.put("KeyThread", "net|6001");
            String message = jsonObject.toString();
            gameManager.updateGameAppInfo(message);
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception GTE : " + e.getMessage());
        }


        boolean isSuccess = gameManager.registerGame("com.hms.explorehms.huawei.feature_gameservicesrework", null);

        view.tvPhoneInfoGTEComputergraphics.setText(
                getPhoneInfo()
        );
        view.tvGameRegisteredGTEGameservices.setText(
                String.valueOf(isSuccess)
        );

    }

    /**
     * Returns the phone number if it can be retrieved, or a string that cannot be retrieved.
     */
    private String getPhoneInfo() {
        String phoneInfo = gameManager.getPhoneInfo();
        if (phoneInfo == null || phoneInfo.isEmpty()) {
            return "Phone Info cannot be taken";
        } else {
            return phoneInfo;
        }
    }
}