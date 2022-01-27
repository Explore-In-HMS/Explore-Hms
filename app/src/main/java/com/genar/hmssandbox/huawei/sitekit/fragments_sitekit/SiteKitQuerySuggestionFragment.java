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

package com.genar.hmssandbox.huawei.sitekit.fragments_sitekit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.genar.hmssandbox.huawei.R;
import com.genar.hmssandbox.huawei.databinding.FragmentSiteKitQuerySuggestionBinding;
import com.genar.hmssandbox.huawei.sitekit.common.SiteKitResultItemClickListener;
import com.genar.hmssandbox.huawei.sitekit.fragments_sitekit.adapters.QuerySuggestionAdapter;
import com.genar.hmssandbox.huawei.sitekit.model.AddressInfo;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.QuerySuggestionRequest;
import com.huawei.hms.site.api.model.QuerySuggestionResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SiteKitQuerySuggestionFragment extends Fragment implements SiteKitResultItemClickListener {

    private static final String TAG = SiteKitQuerySuggestionFragment.class.getSimpleName();
    private FragmentSiteKitQuerySuggestionBinding binding;

    private SearchService searchService;

    private QuerySuggestionRequest request;
    ArrayList<AddressInfo> addresses;
    QuerySuggestionAdapter recyclerViewAdapter;

    private NavController navController;

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


        binding = FragmentSiteKitQuerySuggestionBinding.inflate(inflater, container, false);
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
            String API_KEY = getResources().getString(R.string.site_kit_API_KEY);
            ;
            searchService = SearchServiceFactory.create(view.getContext(), URLEncoder.encode(API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
        }

        addresses = new ArrayList<>();
        recyclerViewAdapter = new QuerySuggestionAdapter(new ArrayList<>(), this);
        binding.searchSuggestionRecyclerView.setAdapter(recyclerViewAdapter);

        /**
         * Creating QuerySuggestionRequest. You can customize with using various properties.
         */
        request = new QuerySuggestionRequest();
        request.setCountries(Arrays.asList("en", "fr", "cn", "de", "ko"));
        Boolean isStrictBound=binding.cbxStrictBound.isChecked();
        request.setStrictBounds(isStrictBound);
        request.setLocation((Coordinate) getArguments().getParcelable("location"));
        //request.setBounds(Coordinate()); coordinate bounds to which search results need to be biased.
        //request.setRadius(5000); search radius, in meters. The value ranges from 1 to 50000. The default value is 50000.
        //request.setPoiTypes(List< LocationType >); list of POI types. The value range is a subset of LocationType.
        //request.setCountryCode(""); code of the country where places are searched, which complies with the ISO 3166-1 alpha-2 standard.
        //request.setLanguage("en");  language in which search results are displayed. If this parameter is not passed, English will be used. If English is unavailable, the local language will be used.
        //request.setPoliticalView(""); political view. The value is a two-letter country/region code complying with ISO 3166-1 alpha-2.

        initUI();
    }

    private void initUI() {

        /**
         *
         * Listener instance to get QuerySuggestionResponse.
         * If search process successfully done we are creating a list and updating recyclerView data.
         *
         */
        SearchResultListener<QuerySuggestionResponse> suggestionResultListener = new SearchResultListener<QuerySuggestionResponse>() {
            @Override
            public void onSearchResult(QuerySuggestionResponse querySuggestionResponse) {
                List<Site> sites = querySuggestionResponse.getSites();
                if (sites == null || sites.isEmpty()) {
                    return;
                }
                ArrayList<AddressInfo> list = new ArrayList<>();
                for (Site site : sites) {
                    AddressInfo temp = new AddressInfo(site.getName(), site.getSiteId(), site.getLocation(), site.getAddress());
                    list.add(temp);
                }
                binding.progressBarSiteKitQuerySuggestion.setVisibility(View.GONE);
                recyclerViewAdapter.updateData(list);
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                binding.progressBarSiteKitQuerySuggestion.setVisibility(View.GONE);
                Log.e(TAG, "Message: " + searchStatus.getErrorMessage());
            }
        };

        try {
            binding.edtQuerySuggestionInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // onTextChanged
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // beforeTextChanged
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = editable.toString();
                    if (text.length() >= 2) {
                        binding.progressBarSiteKitQuerySuggestion.setVisibility(View.VISIBLE);
                        request.setQuery(text);
                        makeSearch(suggestionResultListener);
                    }
                }
            });
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "textWatcherException: " + e.getMessage());

        }


    }

    /**
     * Query search method.
     * <p>
     * Service method gets two instance, QuerySuggestionRequest, SearchResultListener
     *
     * @param suggestionResultListener
     */
    private void makeSearch(SearchResultListener<QuerySuggestionResponse> suggestionResultListener) {
        searchService.querySuggestion(request, suggestionResultListener);
    }

    /**
     * This method is triggered after any recyclerView item has been clicked.
     * It gets the data that clicked and this data pass the DetailFragment with using Navigation Component.
     *
     * @param data
     */
    @Override
    public void onItemClick(AddressInfo data) {
        SiteKitQuerySuggestionFragmentDirections.ActionGotoAddressDetailFragment action = SiteKitQuerySuggestionFragmentDirections.actionGotoAddressDetailFragment(data.getSiteId());
        navController.navigate(action);
    }
}