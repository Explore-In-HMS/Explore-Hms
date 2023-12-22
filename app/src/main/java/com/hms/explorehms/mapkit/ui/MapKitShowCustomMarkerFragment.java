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

package com.hms.explorehms.mapkit.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentMapKitShowCustomMarkerBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.hms.explorehms.mapkit.data.MarkerData;
import com.hms.explorehms.mapkit.ui.adapter.InfoWindowAdapter;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;


public class MapKitShowCustomMarkerFragment extends BaseFragment {

    private int count = 1;
    private FragmentMapKitShowCustomMarkerBinding binding;
    private static final String TAG = "MAP_KIT";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapKitShowCustomMarkerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initializeUI() {
        hMap.setInfoWindowAdapter(new InfoWindowAdapter(getContext()));
        hMap.setOnMapClickListener(this::clickedOnMap);
    }

    private void addMarker(MarkerData markerData) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(markerData.getLat(), markerData.getLng()))
                .clusterable(true)
                .clickable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));

        Marker marker = hMap.addMarker(options);
        marker.setTag(markerData);

        Animation translateAnimation = new TranslateAnimation(0,2,0,2);
        translateAnimation.setDuration(1000L);
        translateAnimation.setRepeatCount(10);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Translate Animation Start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "Translate Animation End");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "Translate Animation Repeated");
            }
        });

        Animation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setRepeatCount(5);
        alphaAnimation.setDuration(1000L);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Alpha Animation Start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "Alpha Animation End");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "Alpha Animation Repeated");
            }
        });

        Animation scaleAnimation = new ScaleAnimation(0, 2, 0, 2);
        scaleAnimation.setRepeatCount(10);
        scaleAnimation.setDuration(1000L);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Scale Animation Start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "Scale Animation Start");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "Scale Animation Repeated");
            }
        });


        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);

        marker.startAnimation();

        if(marker.isClickable() || options.isClickable()){
            Log.i(TAG,"Custom markers are clickable");
        }else{
            marker.setClickable(true);
        }
        hMap.setMarkersClustering(true);
    }

    private void clickedOnMap(LatLng latLng) {
        MarkerData markerData = new MarkerData();
        markerData.setLat(latLng.latitude);
        markerData.setLng(latLng.longitude);
        markerData.setTitle(String.format("Marker - %d", count));
        markerData.setSnippet(latLng.latitude + " - " + latLng.longitude);
        this.count++;
        addMarker(markerData);
    }
}