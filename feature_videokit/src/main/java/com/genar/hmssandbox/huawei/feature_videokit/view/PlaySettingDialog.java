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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;

import com.genar.hmssandbox.huawei.feature_videokit.contract.OnPlaySettingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Play the Settings Dialog
 */
public class PlaySettingDialog implements OnClickListener {
    // Dialog builder
    private AlertDialog.Builder builder;

    // Listener
    private OnPlaySettingListener onPlaySettingListener;

    // Show data list
    private List<Pair<String, String>> showTextList = new ArrayList<Pair<String, String>>();

    // Setting Type
    private int playSettingType;

    // The default selection
    private int selectIndex;

    /**
     * Constructor
     *
     * @param context Context
     */
    public PlaySettingDialog(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    /**
     * Set dialog title
     *
     * @param title Title
     * @return dialog
     */
    public PlaySettingDialog setTitle(CharSequence title) {
        builder.setTitle(title);
        return this;
    }

    /**
     * Set dialog data
     *
     * @param strList data list
     * @return dialog
     */
    public PlaySettingDialog setList(List<String> strList) {
        showTextList = new ArrayList<Pair<String, String>>();
        if (strList != null) {
            for (String temp : strList) {
                showTextList.add(new Pair<String, String>(temp, temp));
            }
        }
        return this;
    }

    /**
     * Set dialog default selection
     *
     * @param value The select value
     * @return Dialog
     */
    public PlaySettingDialog setSelectValue(String value) {
        for (int iLoop = 0; iLoop < showTextList.size(); iLoop++) {
            if (showTextList.get(iLoop).first.equals(value)) {
                selectIndex = iLoop;
                break;
            }
        }
        return this;
    }

    /**
     * Set dialog default selection
     *
     * @param index The select index
     * @return Dialog
     */
    public PlaySettingDialog setSelectIndex(int index) {
        selectIndex = index;
        return this;
    }

    /**
     * Settings dialog parameter
     *
     * @param playSettingListener Listener
     * @param playSettingType Player set type
     * @return Dialog
     */
    public PlaySettingDialog initDialog(OnPlaySettingListener playSettingListener, int playSettingType) {
        this.onPlaySettingListener = playSettingListener;
        this.playSettingType = playSettingType;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (onPlaySettingListener != null) {
            onPlaySettingListener.onSettingItemClick(showTextList.get(which).first, playSettingType);
        }
        dialog.dismiss();
    }

    /**
     * Cancel button listener
     *
     * @param text Cancel button value
     * @param listener Listener
     * @return Dialog
     */
    public PlaySettingDialog setNegativeButton(String text, OnClickListener listener) {
        builder.setNegativeButton(text, listener);
        return this;
    }

    /**
     * Show dialog
     *
     * @return Dialog
     */
    public PlaySettingDialog show() {
        String[] items = new String[showTextList.size()];

        for (int iLoop = 0; iLoop < items.length; iLoop++) {
            items[iLoop] = showTextList.get(iLoop).second;
        }
        builder.setSingleChoiceItems(items, selectIndex, this);
        AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        dialog.show();
        return this;
    }
}
