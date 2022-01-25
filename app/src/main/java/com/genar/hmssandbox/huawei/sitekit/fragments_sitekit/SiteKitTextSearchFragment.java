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
import com.genar.hmssandbox.huawei.databinding.FragmentSiteKitTextSearchBinding;
import com.genar.hmssandbox.huawei.sitekit.common.SiteKitResultItemClickListener;
import com.genar.hmssandbox.huawei.sitekit.fragments_sitekit.adapters.SiteKitGeneralAdapter;
import com.genar.hmssandbox.huawei.sitekit.model.AddressInfo;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SiteKitTextSearchFragment extends Fragment implements SiteKitResultItemClickListener {

    private final String TAG = SiteKitTextSearchFragment.class.getSimpleName();

    private FragmentSiteKitTextSearchBinding binding;
    private SearchService searchService;

    private TextSearchRequest request;
    ArrayList<AddressInfo> addresses;
    SiteKitGeneralAdapter recyclerViewAdapter;

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


        binding = FragmentSiteKitTextSearchBinding.inflate(inflater, container, false);
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
            searchService = SearchServiceFactory.create(view.getContext(), URLEncoder.encode(API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
        }

        addresses = new ArrayList<>();
        recyclerViewAdapter = new SiteKitGeneralAdapter(new ArrayList<>(), this);
        binding.textSearchRecyclerView.setAdapter(recyclerViewAdapter);

        /**
         * Creating TextSearchRequest. You can customize with using various properties.
         */
        request = new TextSearchRequest();
        request.setHwPoiType(HwLocationType.AMUSEMENT_PARK);
        request.setCountries(Arrays.asList("en", "fr", "cn", "de", "ko"));
        request.setLocation(getArguments().getParcelable("location"));
        //request.setLanguage("en");
        //request.setHwPoiType(HwLocationType.ADDRESS); Huawei POI type of returned places. This parameter is recommended.
        //request.setPageIndex(1); current page number. The value ranges from 1 to 60. The default value is 1.
        //request.setPageSize(20); number of records on each page. The value ranges from 1 to 20. The default value is 20.
        //request.setPoliticalView("");The value is a two-letter country/region code complying with ISO 3166-1 alpha-2
        //request.setCountryCode("tr"); code of the country where places are searched.

        initUI();
    }

    private void initUI() {


        /**
         *
         * Listener instance to get TextSearchResponse.
         * If search process successfully done we are creating a list and updating recyclerView data.
         *
         */
        SearchResultListener<TextSearchResponse> textResultListener = new SearchResultListener<TextSearchResponse>() {
            @Override
            public void onSearchResult(TextSearchResponse textSearchResponse) {
                List<Site> sites = textSearchResponse.getSites();
                if (textSearchResponse.getTotalCount() <= 0 || sites == null || sites.isEmpty()) {
                    return;
                }
                ArrayList<AddressInfo> list = new ArrayList<>();
                for (Site site : sites) {
                    AddressInfo temp = new AddressInfo(site.getName(), site.getSiteId(), site.getLocation(), site.getAddress());
                    list.add(temp);
                }
                recyclerViewAdapter.updateData(list);
            }

            @Override
            public void onSearchError(SearchStatus searchStatus) {
                Log.e(TAG, "Message: " + searchStatus.getErrorMessage());
            }
        };


        binding.edtTextSearchInput.addTextChangedListener(new TextWatcher() {
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
                    request.setQuery(text);
                    makeSearch(textResultListener);
                }
            }
        });

    }


    /**
     * Text search method.
     * <p>
     * Service method gets two instance, TextSearchRequest, SearchResultListener
     *
     * @param
     */
    private void makeSearch(SearchResultListener<TextSearchResponse> textResultListener) {
        searchService.textSearch(request, textResultListener);
    }


    /**
     * This method is triggered after any recyclerView item has been clicked.
     * It gets the data that clicked and this data pass the DetailFragment with using Navigation Component.
     *
     * @param data
     */
    @Override
    public void onItemClick(AddressInfo data) {
        SiteKitTextSearchFragmentDirections.ActionGotoAddressDetailFragment action = SiteKitTextSearchFragmentDirections.actionGotoAddressDetailFragment(data.getSiteId());
        navController.navigate(action);
    }

}