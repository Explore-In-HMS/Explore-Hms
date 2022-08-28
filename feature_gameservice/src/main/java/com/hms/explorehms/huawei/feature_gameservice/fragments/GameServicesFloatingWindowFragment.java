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

import com.hms.explorehms.huawei.feature_gameservice.databinding.FragmentGameServicesFloatingWindowBinding;
import com.huawei.hms.android.HwBuildEx;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.buoy.BuoyClient;

import java.lang.reflect.Field;
import java.util.Objects;


public class GameServicesFloatingWindowFragment extends BaseFragmentGameServices<FragmentGameServicesFloatingWindowBinding> {


    private static final String TAG = "GameServicesFloatingWindowFragment";

    @Override
    FragmentGameServicesFloatingWindowBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return FragmentGameServicesFloatingWindowBinding.inflate(inflater, container, false);
    }

    @Override
    void initializeUI() {
        setTitle("Floating Window");


        int emuiVersion = getEmuiVersion();

        if (emuiVersion < HwBuildEx.VersionCodes.EMUI_10_0) {
            Toast.makeText(requireContext(), "Your EMUI Version is not enough to support floating window functions", Toast.LENGTH_SHORT).show();
        }

        BuoyClient buoyClient = Games.getBuoyClient(requireActivity());


        view.btnShowFWGameservices.setOnClickListener(view1 -> buoyClient.showFloatWindow());

        view.btnHideFWGameservices.setOnClickListener(view1 -> buoyClient.hideFloatWindow());


    }

    private int getEmuiVersion() {
        Object returnObj = null;
        int emuiVersionCode = 0;
        try {
            Class<?> targetClass = Class.forName("com.huawei.android.os.BuildEx$VERSION");
            Field field = targetClass.getDeclaredField("EMUI_SDK_INT");
            returnObj = field.get(targetClass);
            if (null != returnObj) {
                emuiVersionCode = (Integer) returnObj;
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFoundException: ");
        } catch (NoSuchFieldException e) {
            Log.e(TAG, "NoSuchFieldException: ");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "IllegalAccessException: ");
        } catch (ClassCastException e) {
            Log.e(TAG, "ClassCastException: getEMUIVersionCode is not a number" + Objects.requireNonNull(returnObj).toString());
        }catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        Log.i(TAG, "emuiVersionCodeValue: " + emuiVersionCode);
        return emuiVersionCode;
    }
}