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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.productVisualSearch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.huawei.hms.mlplugin.productvisionsearch.MLProductVisionSearchCapture;
import com.huawei.hms.mlsdk.productvisionsearch.MLProductVisionSearch;
import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProduct;

import java.util.ArrayList;
import java.util.List;

public class ProductVisualSearchFragment extends MLProductVisionSearchCapture.AbstractProductFragment<MLProductVisionSearch> {

    private static final String TAG = ProductVisualSearchFragment.class.getSimpleName();

    private View root;

    private final List<MLVisionSearchProduct> mlProducts = new ArrayList<>();

    private List<MLProductVisionSearch> productData;

    private BottomSheetAdapter adapter;

    private TextView prompt;


    @Override
    public void onResume() {
        super.onResume();
        product(productData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_product_visual_search, container, false);
        initView();
        return root;
    }

    private void initView() {
        GridView gridView = root.findViewById(R.id.gv);
        prompt = root.findViewById(R.id.prompt);
        gridView.setNumColumns(2);
        adapter = new BottomSheetAdapter(mlProducts, getContext());
        root.findViewById(R.id.img_close).setOnClickListener(v -> getActivity().finish());
        gridView.setAdapter(adapter);
    }

    @Override
    public List<MLProductVisionSearch> getProductList(List<MLProductVisionSearch> list) {
        return list;
    }

    @Override
    public void onResult(List<MLProductVisionSearch> productList) {
        productData = productList;
        product(productList);
    }

    private void product(List<MLProductVisionSearch> productList) {
        if (null == productList) {
            return;
        }
        mlProducts.clear();

        if (productList.size() == 0) {
            prompt.setText(getString(R.string.productEmptyMsg));
            return;
        }
        for (MLProductVisionSearch search : productList) {
            mlProducts.addAll(search.getProductList());
            Log.d(TAG, "MLProductVisionSearch.onResult : mlProducts : " + search.toString());
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onError(Exception e) {
        Log.e(TAG, "MLProductVisionSearch.onError : " + e.getMessage(), e);
        return false;
    }

}