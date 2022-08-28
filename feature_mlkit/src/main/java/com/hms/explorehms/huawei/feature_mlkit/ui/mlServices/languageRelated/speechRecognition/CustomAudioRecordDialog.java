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
package com.hms.explorehms.huawei.feature_mlkit.ui.mlServices.languageRelated.speechRecognition;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.hms.explorehms.huawei.feature_mlkit.R;

public class CustomAudioRecordDialog extends Dialog {


    private OnCompleteListener completeListener;
    private OnBackPressedListener backPressedListener;

    public static final int TYPE_WITH_COMPLETE_BUTTON = 1;

    public static final int TYPE_WITHOUT_COMPLETE_BUTTON = 2;

    private ImageView mVolumeLevelIcon;

    private final Context mContext;

    private final int mType;

    //endregion

    /**
     * CustomAudioRecordDialog constructor
     *
     * @param context
     * @param type
     */
    public CustomAudioRecordDialog(@NonNull Context context, int type) {
        this(context, R.style.BottomDialogStyle, type);
    }

    /**
     * CustomAudioRecordDialog constructor
     *
     * @param context
     * @param themeResId
     * @param type
     */
    private CustomAudioRecordDialog(@NonNull Context context, int themeResId, int type) {
        super(context, themeResId);
        mContext = context;
        mType = type;
        initView();
    }


    public interface OnCompleteListener {
        void onComplete();
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }


    private void initView() {
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.dialog_audio_record_custom, null);
            Button btnComplete = view.findViewById(R.id.btn_stop_record);
            if (mType == TYPE_WITHOUT_COMPLETE_BUTTON) {
                btnComplete.setVisibility(View.GONE);
            } else {
                btnComplete.setOnClickListener(view1 -> {
                    if (completeListener != null) {
                        completeListener.onComplete();
                    }
                });
            }
            window.setContentView(view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = (int) dp2px(mContext, 25);
            params.leftMargin = (int) dp2px(mContext, 20);
            params.rightMargin = (int) dp2px(mContext, 20);

            view.setLayoutParams(params);

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            setCanceledOnTouchOutside(false);

            mVolumeLevelIcon = view.findViewById(R.id.id_recorder_dialog_voice);

            this.setOnKeyListener((arg0, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && backPressedListener != null) {
                        backPressedListener.onBackPressed();
                }
                return false;
            });
        }
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.completeListener = listener;
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        this.backPressedListener = listener;
    }


    /**
     * update volume image by level
     *
     * @param level level
     */
    public void updateVolumeLevel(int level) {
        if (isShowing()) {
            String defPackage = mContext.getPackageName() + ".feature_mlkit";
            int resId = mContext.getResources().getIdentifier("volume_" + level, "mipmap", defPackage);
            mVolumeLevelIcon.setImageResource(resId);
        }
    }


    public static float dp2px(Context context, float dipValue) {
        return dipValue * context.getResources().getDisplayMetrics().density + 0.5f;
    }

}
