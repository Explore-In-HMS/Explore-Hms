/**
 * Copyright 2020. Explore in HMS. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.genar.hmssandbox.huawei.feature_videokit.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.feature_videokit.R;
import com.genar.hmssandbox.huawei.feature_videokit.adapter.SelectPlayDataAdapter;
import com.genar.hmssandbox.huawei.feature_videokit.contract.OnHomePageListener;
import com.genar.hmssandbox.huawei.feature_videokit.entity.PlayEntity;
import com.genar.hmssandbox.huawei.feature_videokit.utils.DialogUtil;

import java.util.List;

/**
 * Home page view
 */
public class HomePageView {
    // Home page parent view
    private View contentView;

    // Context
    private Context context;

    // Play recyclerView
    private RecyclerView playRecyclerView;

    // Input play url
    private EditText addressEt;

    // Play button
    private Button playBt;

    // Load view
    private ProgressBar playLoading;

    // Play adapter
    private SelectPlayDataAdapter selectPlayDataAdapter;

    // Listener
    private OnHomePageListener onHomePageListener;

    /**
     * Constructor
     *
     * @param context Context
     * @param onHomePageListener Listener
     */
    public HomePageView(Context context, OnHomePageListener onHomePageListener) {
        this.context = context;
        this.onHomePageListener = onHomePageListener;
        initView();
    }

    /**
     * Get parent view
     *
     * @return Parent view
     */
    public View getContentView() {
        return contentView;
    }

    /**
     * Init view
     */
    private void initView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
        playRecyclerView = (RecyclerView) contentView.findViewById(R.id.player_recycler_view);
        playLoading = (ProgressBar) contentView.findViewById(R.id.play_loading);
        addressEt = (EditText) contentView.findViewById(R.id.input_path_ed);
        playBt = (Button) contentView.findViewById(R.id.main_play_btn);
        playBt.setOnClickListener(onHomePageListener);
        //menuBt = (ImageView) contentView.findViewById(R.id.play_list_menu);
       //menuBt.setOnClickListener(onHomePageListener);
        selectPlayDataAdapter = new SelectPlayDataAdapter(context, onHomePageListener);
        playRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        playRecyclerView.setAdapter(selectPlayDataAdapter);
        playRecyclerView.setVisibility(View.GONE);
        playLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Set the current data list
     *
     * @param playList Data list
     */
    public void updateRecyclerView(List<PlayEntity> playList) {
        selectPlayDataAdapter.setSelectPlayList(playList);
        playLoading.setVisibility(View.GONE);
        playRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Get input text
     *
     * @return Text value
     */
    public String getInputUrl() {
        if (addressEt.getText() == null) {
            return "";
        } else {
            return addressEt.getText().toString();
        }
    }

    /**
     * Show settings dialog
     *
     * @param playSettingType Play setting type
     * @param settingList Play setting text list
     * @param defaultSelect Default selection
     */
    public void showVideoTypeDialog(int playSettingType, List<String> settingList, int defaultSelect) {
        DialogUtil.showVideoTypeDialog(context, playSettingType, settingList, defaultSelect, onHomePageListener);
    }

    /**
     * Whether the menu button has focus
     *
     * @return boolean Whether the menu button has focus
     */


    /**
     * Set background color
     */


    /**
     * Clear background color
     */

}
