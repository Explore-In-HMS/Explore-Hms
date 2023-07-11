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

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentDrivingBinding;
import com.hms.explorehms.databinding.FragmentWalkingBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.hms.explorehms.mapkit.data.Constants;
import com.hms.explorehms.mapkit.data.IVolley;
import com.hms.explorehms.mapkit.data.MyVolleyRequest;
import com.hms.explorehms.mapkit.helper.PolylineHelper;
import com.hms.explorehms.mapkit.model.request.Destination;
import com.hms.explorehms.mapkit.model.request.DirectionRequest;
import com.hms.explorehms.mapkit.model.request.Origin;
import com.hms.explorehms.mapkit.model.response.DirectionResponse;
import com.hms.explorehms.mapkit.myEnums.DirectionType;
import com.hms.explorehms.mapkit.ui.adapter.DirectionsAdapter;
import com.google.gson.Gson;
import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrivingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrivingFragment extends BaseFragment implements IVolley {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LinearLayoutManager llm;
    private Context mcontext;
    private DirectionRequest dirRequest;
    private RecyclerView recylerView;
    private TextView stepsVia,stepsMainInfo;
    private FragmentWalkingBinding binding;
    private Origin origin;
    private Destination destination;
    private DirectionResponse mdirectionResponse;

    public DrivingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DrivingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DrivingFragment newInstance(String param1, String param2) {
        DrivingFragment fragment = new DrivingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mcontext=this.requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return FragmentDrivingBinding.inflate(getLayoutInflater()).getRoot();
    }

    /**
     * It initializes the UI with calling allSteps
     */
    @Override
    public void initializeUI() {
        try {
            allSteps();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * It handles Cycling steps a route
     */
    void allSteps() throws UnsupportedEncodingException, JSONException {
        origin= new
                Origin();
        destination= new
                Destination();
        dirRequest= new
                DirectionRequest();
        origin.setLat(Constants.ORIGIN_LAT);
        origin.setLng(Constants.ORIGIN_LONG);
        destination.setLat( Constants.DESTINATION_LAT);
        destination.setLng(Constants.DESTINATION_LONG);
        dirRequest.setOrigin(origin);
        dirRequest.setDestination(destination);
        Gson gson = new Gson();
        String dirReq = gson.toJson(dirRequest);
        if (hMap != null) {
            new MyVolleyRequest(getContext(), this).postRequest(
                    dirReq, DirectionType.DRIVE.getType(),
                    hMap, directionResponse -> {
                        mdirectionResponse = directionResponse;
                        showAllSteps(mdirectionResponse);
                        new PolylineHelper().drawPolyline(mdirectionResponse,hMap);
                    }
            );
        }
        addMarkerToMap(origin.getLat(), origin.getLng(), "Origin", "Start Point",null);
        addMarkerToMap(destination.getLat(), destination.getLng(), "Destination", "End Point", BitmapDescriptorFactory.fromResource(R.drawable.finish_flag_96));

        //  TODO setting camera coordinates and moving camera on Huawei Map
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                new LatLng(
                        origin.getLat(),
                        origin.getLng()
                ), 11f
        );
        hMap.moveCamera(update);
    }

    /**
     * It adds a marker to map, it takes lat, lot, title, snippet and an icon as parameter.
     */
    void addMarkerToMap(Double lat, Double lot, String title, String snippet, BitmapDescriptor descriptor) {
        hMap.addMarker(
                new MarkerOptions()
                        .title(title)
                        .snippet(snippet)
                        .position(new LatLng(lat, lot)).icon(descriptor)
        );
    }

    /**
     * It shows all steps for the route.
     */
    void showAllSteps(DirectionResponse directionResponse) {
        mdirectionResponse = directionResponse;
        String mainInfo = mdirectionResponse.getRoutes().get(0).getPaths().get(0).getDurationText() + " " + (mdirectionResponse.getRoutes().get(0).getPaths().get(0).getDistanceText());
        stepsMainInfo.setText(mainInfo);
        stepsVia.setText(mdirectionResponse.getRoutes().get(0).getPaths().get(0).getSteps().get(0).getRoadName());
        DirectionsAdapter directionAdapter = new DirectionsAdapter(
                mcontext,
                mdirectionResponse.getRoutes().get(0).getPaths().get(0).getSteps()
        );
        llm = new LinearLayoutManager(mcontext);
        recylerView.setLayoutManager(llm);
        recylerView.setHasFixedSize(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recylerView.setAdapter(directionAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recylerView=view.findViewById(R.id.directionStepsRecyclerView);
        stepsMainInfo=view.findViewById(R.id.directionStepsMainInfo);
        stepsVia=view.findViewById(R.id.directionStepsVia);
    }

    @Override
    public void onSuccess(DirectionResponse directionResponse) {
    }
}