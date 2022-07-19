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

package com.genar.hmssandbox.huawei.feature_analyticskit;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.type.HAEventType;
import com.huawei.hms.analytics.type.HAParamType;

import java.util.ArrayList;

public class EcommerceExampleActivity extends AppCompatActivity {

    HiAnalyticsInstance mHiAnalyticsInstance;

    EditText productId, productName, category, quantity, price, currencyName;
    MaterialButton btnPostInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecommerce_activity);
        Toolbar toolBar = findViewById(R.id.toolbar_analytics);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Sets context and data processing location. DE -> Germany
        mHiAnalyticsInstance = HiAnalytics.getInstance(this, "DE");

        //Sets the app installation source
        mHiAnalyticsInstance.setChannel("AppGallery");

        //Sets whether to collect system attributes. Only userAgent attribute is supported now.
        mHiAnalyticsInstance.setPropertyCollection("userAgent",true);
        initialize();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initialize() {
        productId = findViewById(R.id.et_productId);
        productName = findViewById(R.id.et_productName);
        category = findViewById(R.id.et_category);
        quantity = findViewById(R.id.et_quantity);
        price = findViewById(R.id.et_price);
        currencyName = findViewById(R.id.et_currencyName);
        btnPostInfo = findViewById(R.id.btn_postInformation);
    }

    private void postEventBundleList(String productId,
                                     String productName,
                                     String category,
                                     Long quantity,
                                     Double price,
                                     String currencyName
    ) {
        Bundle bundle_pre1 = new Bundle();
        bundle_pre1.putString(HAParamType.PRODUCTID, productId);
        bundle_pre1.putString(HAParamType.PRODUCTNAME, productName);
        bundle_pre1.putString(HAParamType.CATEGORY, category);
        bundle_pre1.putLong(HAParamType.QUANTITY, quantity);
        bundle_pre1.putDouble(HAParamType.PRICE, price);
        bundle_pre1.putString(HAParamType.CURRNAME, currencyName);

        ArrayList<Bundle> listBundle = new ArrayList<>();
        listBundle.add(bundle_pre1);

        Bundle commonBundle = new Bundle();
        commonBundle.putString(HAParamType.SEARCHKEYWORDS, "phone");
        commonBundle.putParcelableArrayList("items", listBundle);
        mHiAnalyticsInstance.onEvent(HAEventType.ADDPRODUCT2WISHLIST, commonBundle);
        Toast.makeText(this, "Your product  posted to the AppGallery Connect with wish List successfully", Toast.LENGTH_LONG).show();
    }

    public void postInfo(View view) {
        if (productId.getText().toString().equals("")
                || productName.getText().toString().equals("")
                || category.getText().toString().equals("")
                || quantity.getText().toString().equals("")
                || price.getText().toString().equals("")
                || currencyName.getText().toString().equals("")) {
            Toast.makeText(this, "You have to fill all the information to post the AppGallery Connect", Toast.LENGTH_LONG).show();
        } else {
            postEventBundleList(productId.getText().toString(), productName.getText().toString(),
                    category.getText().toString(), Long.valueOf(quantity.getText().toString()),
                    Double.valueOf(price.getText().toString()), currencyName.getText().toString());
            clear();
        }

    }

    public void clear() {
        productId.setText("");
        productName.setText("");
        category.setText("");
        quantity.setText("");
        price.setText("");
        currencyName.setText("");
        productId.requestFocus();
    }
}