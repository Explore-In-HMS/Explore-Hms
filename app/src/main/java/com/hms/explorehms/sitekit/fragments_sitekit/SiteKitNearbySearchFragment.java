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

package com.hms.explorehms.sitekit.fragments_sitekit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentSiteKitNearbySearchBinding;
import com.hms.explorehms.sitekit.common.SiteKitResultItemClickListener;
import com.hms.explorehms.sitekit.fragments_sitekit.adapters.SiteKitGeneralAdapter;
import com.hms.explorehms.sitekit.model.AddressInfo;
import com.hms.explorehms.sitekit.model.POIType;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SiteKitNearbySearchFragment extends Fragment implements SiteKitResultItemClickListener {

    private final String TAG = SiteKitNearbySearchFragment.class.getSimpleName();

    private FragmentSiteKitNearbySearchBinding binding;
    private SearchService searchService;

    private NearbySearchRequest request;
    ArrayList<AddressInfo> addresses;
    SiteKitGeneralAdapter recyclerViewAdapter;

    private NavController navController;


    /**
     * ViewBinding process is done here.
     *
     * @param inflater
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSiteKitNearbySearchBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    /**
     * Site kit uses cloud operation for their services. Therefore we need to set API_KEY first for use APIs.
     * <p>
     * After API_KEY set, we initialize RecyclerView and ui elements.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        try {
            String API_KEY = AGConnectServicesConfig.fromContext(requireContext()).getString("client/api_key");
            searchService = SearchServiceFactory.create(view.getContext(), URLEncoder.encode(API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
        }


        addresses = new ArrayList<>();
        recyclerViewAdapter = new SiteKitGeneralAdapter(new ArrayList<>(), this::onItemClick);
        binding.nearbySearchRecyclerView.setAdapter(recyclerViewAdapter);


        initUI(view);
    }

    private void initUI(View view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.radius_array_sitekit));
        binding.spinnerRadius.setAdapter(adapter);

        String[] poiTypeArr = {
                getString(POIType.ALL.getStringVal()),
                getString(POIType.CAFE.getStringVal()),
                getString(POIType.RESTAURANT.getStringVal()),
                getString(POIType.SHOPPING_MALL.getStringVal()),
                getString(POIType.ADDRESS.getStringVal()),
                getString(POIType.DRUG_STORE.getStringVal()),
                getString(POIType.HOSPITAL.getStringVal()),
                getString(POIType.BANK.getStringVal())
        };

        ArrayAdapter<String> poiAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, poiTypeArr);
        binding.spinnerPOIType.setAdapter(poiAdapter);


        /**
         *
         * Listener instance to get NearbySearchRequests.
         * If search process successfully done we are creating a list and updating recyclerView data.
         *
         */
        SearchResultListener<NearbySearchResponse> nearbyResultListener = new SearchResultListener<NearbySearchResponse>() {
            @Override
            public void onSearchResult(NearbySearchResponse nearbySearchResponse) {
                List<Site> sites = nearbySearchResponse.getSites();
                if (nearbySearchResponse.getTotalCount() <= 0 || sites == null || sites.isEmpty()) {
                    return;
                }
                ArrayList<AddressInfo> list = new ArrayList<>();
                for (Site site : sites) {
                    AddressInfo temp = new AddressInfo(site.getName(), site.getSiteId(), site.getLocation(), site.getAddress());
                    list.add(temp);
                }
                Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show();
                recyclerViewAdapter.updateData(list);
                binding.progressBarSiteKitNearbySearch.setVisibility(View.GONE);
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                binding.progressBarSiteKitNearbySearch.setVisibility(View.GONE);
                Log.e(TAG, "Message: " + searchStatus.getErrorMessage());
            }
        };

        binding.btnMakeNearbySearch.setOnClickListener(view1 -> {
            binding.progressBarSiteKitNearbySearch.setVisibility(View.VISIBLE);
            /**
             *
             * For making Nearby Search Request we need to create NearbySearchRequest instance.
             * You can customize your request with following properties.
             *
             * In this module, we gave location, user input's, radius and POI type (CAFE, HOSPITAL etc.) to the request.
             * Radius -> It is a integer value. With radius value, service searches places in these value.
             *
             */
            request = new NearbySearchRequest();
            Boolean isStrictBounded= binding.cbxStrictBound.isChecked();
            request.setLocation((Coordinate) getArguments().getParcelable("location"));
            request.setQuery(binding.edtNearbyInput.getEditableText().toString());
            request.setRadius(Integer.parseInt(binding.spinnerRadius.getSelectedItem().toString()));
            request.setStrictBounds(isStrictBounded);
            request.setPoiType(POIType.values()[binding.spinnerPOIType.getSelectedItemPosition()].getLocationType());
            //request.setLanguage("en");
            //request.setHwPoiType(HwLocationType.ADDRESS); Huawei POI type of returned places. This parameter is recommended.
            //request.setPageIndex(1); current page number. The value ranges from 1 to 60. The default value is 1.
            //request.setPageSize(20); number of records on each page. The value ranges from 1 to 20. The default value is 20.
            //request.setPoliticalView("");The value is a two-letter country/region code complying with ISO 3166-1 alpha-2


            /**
             * Service call after the request instance has been created.
             */
            searchService.nearbySearch(request, nearbyResultListener);

        });
    }


    /**
     * This method is triggered after any recyclerView item has been clicked.
     * It gets the data that clicked and this data pass the DetailFragment with using Navigation Component.
     *
     * @param data
     */
    @Override
    public void onItemClick(AddressInfo data) {
        SiteKitNearbySearchFragmentDirections.ActionGotoAddressDetailFragment action = SiteKitNearbySearchFragmentDirections.actionGotoAddressDetailFragment(data.getSiteId());
        navController.navigate(action);
    }
}