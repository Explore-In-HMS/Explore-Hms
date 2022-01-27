/**
 * Copyright 2021. Explore in HMS. All rights reserved.
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
package com.genar.hmssandbox.huawei.feature_videokit.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class SelectDialog implements DialogInterface.OnClickListener {
    AlertDialog.Builder builder;

    Handler handler;

    int messageId;

    List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

    int seletedIndex;

    public SelectDialog(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    public SelectDialog setTitle(CharSequence title) {
        builder.setTitle(title);
        return this;
    }

    public SelectDialog setList(List<String> strList) {
        list = new ArrayList<Pair<String, String>>();
        if (strList != null) {
            for (String temp : strList) {
                list.add(new Pair<String, String>(temp, temp));
            }
        }
        return this;
    }

    public SelectDialog setIntArray(int[] array) {
        list = new ArrayList<Pair<String, String>>();
        for (int iLoop = 0; iLoop < array.length; iLoop++) {
            String value = String.valueOf(array[iLoop]);
            list.add(new Pair<String, String>(value, value));
        }

        return this;
    }

    public SelectDialog addSelectItem(String item, String hint) {
        list.add(new Pair<String, String>(item, hint));
        return this;
    }

    public SelectDialog addSelectItem(String item) {
        list.add(new Pair<String, String>(item, item));
        return this;
    }

    public SelectDialog setSelectValue(String value) {
        for (int iLoop = 0; iLoop < list.size(); iLoop++) {
            if (list.get(iLoop).first.equals(value)) {
                seletedIndex = iLoop;
                break;
            }
        }
        return this;
    }

    public SelectDialog setSelectIndex(int index) {
        seletedIndex = index;
        return this;
    }

    public SelectDialog setHandler(Handler handler, int messageId) {
        this.handler = handler;
        this.messageId = messageId;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Message message = handler.obtainMessage(messageId, which, 0, list.get(which).first);
        handler.sendMessage(message);
        dialog.dismiss();
    }

    public SelectDialog setNegativeButton(String text, OnClickListener listener) {
        builder.setNegativeButton(text, listener);
        return this;
    }

    public SelectDialog show() {
        String[] items = new String[list.size()];

        for (int iLoop = 0; iLoop < items.length; iLoop++) {
            items[iLoop] = list.get(iLoop).second;
        }
        builder.setSingleChoiceItems(items, seletedIndex, this);
        AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.7f;
        window.setAttributes(lp);
        dialog.show();
        return this;
    }
}
