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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.imageRelated.productVisualSearch;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hms.explorehms.huawei.feature_mlkit.R;
import com.huawei.hms.mlsdk.productvisionsearch.MLVisionSearchProduct;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BottomSheetAdapter extends BaseAdapter {
    private List<MLVisionSearchProduct> mlProducts;
    private final Context context;

    public BottomSheetAdapter(List<MLVisionSearchProduct> mlProducts, Context context) {
        this.mlProducts = mlProducts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mlProducts == null ? 0 : mlProducts.size();
    }

    @Override
    public Object getItem(int position) {
        return mlProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_product, null);
        }
        // The getImageId method obtains the link to an image in the base library provided by ML Kit for display only.
        // When integrating the product visual search capability, you need to build your own product image library,
        // and use the product ID returned by the getProductId() API to obtain product images in the library.
        ProgressBar prb = convertView.findViewById(R.id.progressBar);
        ImageView img = convertView.findViewById(R.id.img);
        TextView txt = convertView.findViewById(R.id.tv);

        txt.setText(mlProducts.get(position).getProductId());

        String imageUrl = mlProducts.get(position).getImageList().get(0).getImageId();
        Log.d("BottomSheetAdapter", "imageUrl : -" + imageUrl + "-");

        Picasso.get().load(imageUrl).into(img, new Callback() {
            @Override
            public void onSuccess() {
                prb.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.e("BottomSheetAdapter", "Picasso.onError : " + e.getMessage(), e);
            }
        });
        return convertView;
    }
}