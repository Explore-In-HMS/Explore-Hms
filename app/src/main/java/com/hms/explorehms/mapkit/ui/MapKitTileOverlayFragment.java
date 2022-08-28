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

package com.hms.explorehms.mapkit.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.databinding.FragmentMapKitTileOverlayBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.huawei.hms.maps.model.Tile;
import com.huawei.hms.maps.model.TileOverlayOptions;
import com.huawei.hms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;

public class MapKitTileOverlayFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return FragmentMapKitTileOverlayBinding.inflate(getLayoutInflater()).getRoot();
    }

    @Override
    public void initializeUI() {
        setTile();
    }

    private void setTile() {
        // Set the tile size to 256 x 256.
        int mTileSize = 256;
        final int mScale = 1;
        final int mDimension = mScale * mTileSize;
        TileProvider mTileProvider;
        // Create a TileProvider object. The following assumes that the tile is locally generated.
        mTileProvider = (x, y, zoom) -> {
            Matrix matrix = new Matrix();
            float scale = (float) Math.pow(2, zoom) * mScale;
            matrix.postScale(scale, scale);
            matrix.postTranslate(-x * (float)mDimension, -y * (float)mDimension);

            // Generate a bitmap image.
            final Bitmap bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.RGB_565);
            bitmap.eraseColor(Color.parseColor("#024CFF"));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return new Tile(mDimension, mDimension, stream.toByteArray());
        };
        TileOverlayOptions options = new TileOverlayOptions()
                .tileProvider(mTileProvider)
                .transparency(0.5f)
                .fadeIn(true);
        hMap.addTileOverlay(options);
    }
}