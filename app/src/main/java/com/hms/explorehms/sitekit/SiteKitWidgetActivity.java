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

package com.hms.explorehms.sitekit;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.ActivitySiteKitWidgetBinding;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.widget.SearchFragment;
import com.huawei.hms.site.widget.SiteSelectionListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SiteKitWidgetActivity extends AppCompatActivity {

    private final String TAG = SiteKitWidgetActivity.class.getSimpleName();

    private ActivitySiteKitWidgetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteKitWidgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String API_KEY = AGConnectServicesConfig.fromContext(this).getString("client/api_key");

        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.widget_sitekit);

        setupToolbar();
        try {
            searchFragment.setApiKey(URLEncoder.encode(API_KEY, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode apikey error");
        }

        searchFragment.setOnSiteSelectedListener(new SiteSelectionListener() {
            @Override
            public void onSiteSelected(Site site) {
                initUI(site);
            }

            @Override
            public void onError(SearchStatus searchStatus) {
                Log.e(TAG, "Site kit widget error message/code: " + searchStatus.getErrorMessage() + " " + searchStatus.getErrorCode());
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_widget);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initUI(Site site) {
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

    }
}