package com.hms.explorehms.mapkit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hms.explorehms.R;
import com.hms.explorehms.databinding.FragmentHeatMapBinding;
import com.hms.explorehms.mapkit.base.BaseFragment;
import com.huawei.hms.maps.model.HeatMap;
import com.huawei.hms.maps.model.HeatMapOptions;

import java.util.HashMap;
import java.util.Map;

public class HeatMapFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return FragmentHeatMapBinding.inflate(getLayoutInflater()).getRoot();
    }

    @Override
    public void initializeUI() {
        HeatMapOptions heatMapOptions = new HeatMapOptions();
        heatMapOptions.intensity(2f);
// Set the heatmap dataset.
        heatMapOptions.dataSet(R.raw.earthquakes);
// Add the heatmap to a map.
        HeatMap heatMap = hMap.addHeatMap("id", heatMapOptions);
        Map<Float, Integer> map = new HashMap<>();
        map.put(1.0f, 158777);
        heatMap.setColor(map);
// Modify the heatmap intensity.
        heatMap.setIntensity(10f);
// Modify the heatmap transparency.
        heatMap.setOpacity(0.5f);
// Modify the heatmap radius.
        heatMap.setRadius(10f);

    }
}