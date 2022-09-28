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

package com.hms.explorehms.pushkit.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.hms.explorehms.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class TopicDialogPushKit extends Dialog {

    private View dialogView;

    private final TopicDialogListener listener;
    private final Context context;

    private TextInputEditText edTopic;

    private Button btnCancel;
    private Button btnConfirm;

    @SuppressLint("InflateParams")
    public TopicDialogPushKit(Context context, boolean isAdd, TopicDialogListener listener) {
        super(context);

        this.listener = listener;
        this.context = context;

        initView(isAdd);
        initListener();

        setCanceledOnTouchOutside(false);
        setContentView(dialogView);
    }

    @SuppressLint("InflateParams")
    private void initView(boolean isAdd) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        dialogView = layoutInflater.inflate(R.layout.dialog_add_topic_pushkit,null, false);
        btnConfirm = dialogView.findViewById(R.id.btn_push_topic_confirm);
        btnCancel = dialogView.findViewById(R.id.btn_push_topic_cancel);
        edTopic = dialogView.findViewById(R.id.ed_push_topic);
        TextInputLayout edLayout = dialogView.findViewById(R.id.et_Layout);

        edLayout.setHint(isAdd ? "Subscribe topic name" : "Unsubscribe topic name");
    }

    private void initListener(){
        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmClick(Objects.requireNonNull(edTopic.getText()).toString());
                dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                dismiss();
            }
        });

        edTopic.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                InputMethodManager imm =
                        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
    }


    public interface TopicDialogListener {
        void onConfirmClick(String topic);
    }
}