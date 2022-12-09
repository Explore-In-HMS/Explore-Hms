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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentSiteKitAutocompleteBinding;
import com.hms.explorehms.sitekit.common.ProgressDialogScreenSitekit;
import com.hms.explorehms.sitekit.fragments_sitekit.adapters.AutocompleteAdapter;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.QueryAutocompleteRequest;
import com.huawei.hms.site.api.model.QueryAutocompleteResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;


public class SiteKitAutocompleteFragment extends Fragment {


    private final String TAG = SiteKitAutocompleteFragment.class.getSimpleName();

    private FragmentSiteKitAutocompleteBinding binding;

    private SearchService searchService;

    private QueryAutocompleteRequest queryAutocompleteRequest;
    private ProgressDialogScreenSitekit progressDialogScreenSitekit;

    private AutocompleteAdapter autocompleteAdapter;


    /**
     * ViewBinding process is done here.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSiteKitAutocompleteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    /**
     * Site kit uses cloud operation for their services. Therefore we need to set API_KEY first for use APIs.
     * <p>
     * After API_KEY set, we initialize request.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialogScreenSitekit = new ProgressDialogScreenSitekit(requireContext());
        try {
            String API_KEY = AGConnectServicesConfig.fromContext(requireContext()).getString("client/api_key");
            searchService = SearchServiceFactory.create(view.getContext(), URLEncoder.encode(API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
        }

        /**
         *
         * Setting request's properties
         *
         */
        queryAutocompleteRequest = new QueryAutocompleteRequest();
        queryAutocompleteRequest.setLocation((Coordinate) getArguments().getParcelable("location"));
        //queryAutocompleteRequest.setLanguage("en");
        queryAutocompleteRequest.setRadius(2000);
        //queryAutocompleteRequest.setPoliticalView(""); The value is a two-letter country/region code complying with ISO 3166-1 alpha-2.

        initUI();
    }


    /**
     * We are setting recyclerview adapter and editText's listeners.
     */
    private void initUI() {
        autocompleteAdapter = new AutocompleteAdapter(new ArrayList<>(), requireContext());
        binding.recyclerViewAutocomplete.setAdapter(autocompleteAdapter);
        binding.edtAutocompleteInput.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!textView.getText().toString().isEmpty()) {
                    makeSearch(textView.getText().toString());
                }
                return true;
            }
            return false;
        });


        binding.edtAutocompleteInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // beforeTextChanged
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // onTextChanged
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    autocompleteAdapter.clearData();

            }
        });

    }


    /**
     * We are making autocomplete process with user input string.
     *
     * @param query
     */
    private void makeSearch(String query) {
        queryAutocompleteRequest.setQuery(query);
        progressDialogScreenSitekit.showProgressDialog();
        SearchResultListener<QueryAutocompleteResponse> resultListener =
                new SearchResultListener<QueryAutocompleteResponse>() {
                    // Return search results upon a successful search.
                    @Override
                    public void onSearchResult(QueryAutocompleteResponse results) {
                        progressDialogScreenSitekit.dismissProgressDialog();
                        if (results.getSites() != null && results.getSites().length != 0) {
                            ArrayList<Site> list = new ArrayList<>();
                            Collections.addAll(list, results.getSites());
                            autocompleteAdapter.updateData(list);
                        }
                    }

                    // Return the result code and description upon a search exception.
                    @Override
                    public void onSearchError(SearchStatus status) {
                        progressDialogScreenSitekit.dismissProgressDialog();
                        Log.i(TAG, "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
                    }
                };
        searchService.queryAutocomplete(queryAutocompleteRequest, resultListener);
    }
}