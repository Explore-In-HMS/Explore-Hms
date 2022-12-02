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

package com.hms.explorehms.sitekit.fragments_sitekit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentSiteKitPlaceDetailSearchBinding;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.DetailSearchRequest;
import com.huawei.hms.site.api.model.DetailSearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class SiteKitPlaceDetailSearchFragment extends Fragment {

    private final String TAG = SiteKitPlaceDetailSearchFragment.class.getSimpleName();

    private FragmentSiteKitPlaceDetailSearchBinding binding;

    private SearchService searchService;

    private DetailSearchRequest detailSearchRequest;

    /**
     * ViewBinding process is done here.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSiteKitPlaceDetailSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            String API_KEY = AGConnectServicesConfig.fromContext(requireContext()).getString("client/api_key");
            searchService = SearchServiceFactory.create(view.getContext(), URLEncoder.encode(API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
        }

        detailSearchRequest = new DetailSearchRequest();

        assert getArguments() != null;
        if (getArguments().getString("siteId") != null) {
            makeSearch(getArguments().getString("siteId"));
        }
    }


    /**
     * Setting request properties and make Place Detail Search with SiteID.
     *
     * @param siteiId
     */
    private void makeSearch(String siteiId) {
        detailSearchRequest.setSiteId(siteiId);
        detailSearchRequest.setLanguage("en");
        //detailSearchRequest.setPoliticalView("");  The value is a two-letter country/region code complying with ISO 3166-1 alpha-2.

        SearchResultListener<DetailSearchResponse> resultListener = new SearchResultListener<DetailSearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(DetailSearchResponse result) {
                Site site;
                if (result == null || (site = result.getSite()) == null) {
                    return;
                }
                initUI(site);
            }

            @Override
            public void onSearchError(SearchStatus status) {
                Log.i(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        // Call the place detail search API.
        searchService.detailSearch(detailSearchRequest, resultListener);

    }

    /**
     * Setting variables to the UI.
     */
    private void initUI(Site site) {

        try {
            binding.tvDetailName.setText(getResources().getString(R.string.address_item_name, site.getName()));
            binding.tvDetailCountry.setText(getResources().getString(R.string.address_item_country, site.getAddress().getCountry()));
            binding.tvDetailAdminArea.setText(getResources().getString(R.string.address_item_admin_area, site.getAddress().getAdminArea()));
            binding.tvDetailLocality.setText(getResources().getString(R.string.address_item_locality, site.getAddress().getLocality()));
            binding.tvDetailSubAdminArea.setText(getResources().getString(R.string.address_item__sub_admin_area, site.getAddress().getSubAdminArea()));
            binding.tvDetailThoroughfare.setText(getResources().getString(R.string.address_item__thoroughfare, site.getAddress().getThoroughfare()));
            binding.tvDetailStreetNumber.setText(getResources().getString(R.string.address_item__street_number, site.getAddress().getStreetNumber()));
            binding.tvDetailPostalCode.setText(getResources().getString(R.string.address_item__postal_code, site.getAddress().getPostalCode()));
            binding.tvDetailLat.setText(getResources().getString(R.string.address_item_latitude, site.getLocation().getLat()));
            binding.tvDetailLn.setText(getResources().getString(R.string.address_item_longitude, site.getLocation().getLng()));

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }
}