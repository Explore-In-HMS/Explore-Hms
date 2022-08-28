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

package com.hms.explorehms.huawei.feature_cameraengine.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.hms.explorehms.huawei.feature_cameraengine.R;
import com.hms.explorehms.huawei.feature_cameraengine.ui.common.ZoomableImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;

public class ViewUtils {

    public static Dialog dialogImagePeekView;
    public static Dialog dialogVideoView;

    public static void showDialogImagePeekView(Activity activity, Context context, File file, String mode) {

        if(file.exists()){
            dialogImagePeekView = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialogImagePeekView.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, com.hms.explorehms.R.color.transparent)));
            dialogImagePeekView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogImagePeekView.setCanceledOnTouchOutside(true);
            dialogImagePeekView.setCancelable(true);
            View dialogLayout = View.inflate(new ContextThemeWrapper(context, com.hms.explorehms.R.style.AppTheme), R.layout.dialog_image_peek_view_camera_engine,null);
            ZoomableImageView imageView = dialogLayout.findViewById(R.id.imageZoomable); // change
            MaterialButton button = dialogLayout.findViewById(R.id.btn_back_peek_view_ce);
            MaterialTextView tvMode = dialogLayout.findViewById(R.id.tv_image_mode_camera_engine);

            button.setOnClickListener(v -> dialogImagePeekView.dismiss());

            tvMode.setText(mode);

            Glide.with(context).load(file).into(imageView);

            dialogImagePeekView.setContentView(dialogLayout);
            dialogImagePeekView.show();
        }

    }

    public static void showDialogVideoView(Activity activity,Context context, File file,String mode){
        if(file.exists()){
            dialogVideoView = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialogVideoView.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, com.hms.explorehms.R.color.transparent)));
            dialogVideoView.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogVideoView.setCanceledOnTouchOutside(true);
            dialogVideoView.setCancelable(true);

            View dialogLayout = View.inflate(new ContextThemeWrapper(context, com.hms.explorehms.R.style.AppTheme), R.layout.dialog_video_view_camera_engine,null);
            VideoView videoView = dialogLayout.findViewById(R.id.video_view_camera_engine);
            MaterialButton button = dialogLayout.findViewById(R.id.btn_back_peek_view_ce);
            MaterialTextView tvMode = dialogLayout.findViewById(R.id.tv_image_mode_camera_engine);
            MaterialTextView tvTime = dialogLayout.findViewById(R.id.tv_record_time_camera_engine);

            button.setOnClickListener(v -> dialogVideoView.dismiss());

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoView.start();
                }
            });

            tvMode.setText(mode);

            videoView.setVideoPath(file.getAbsolutePath());
            videoView.start();

            dialogVideoView.setContentView(dialogLayout);
            dialogVideoView.show();
        }
    }

    public static void setCaptureOnGalleryButton(File file, ImageView imageView, Activity activity){
        activity.runOnUiThread(() -> {
            imageView.getLayoutParams().height = 200;
            imageView.getLayoutParams().width = 200;
            imageView.requestLayout();
            Glide.with(activity).load(file).into(imageView);
        });
    }

    private static Handler handlerCenterView;
    private static Runnable runnableCenterView;

    public static void showSettingOnCenter(MaterialTextView textView, String text){
        if(handlerCenterView != null){
            handlerCenterView.removeCallbacks(runnableCenterView);
        }

        final boolean[] showed = {false};
        handlerCenterView = new Handler(Looper.getMainLooper());
        runnableCenterView = () -> {
            if (!showed[0]){
                textView.setText(text);
                textView.animate().alpha(1.0f).setDuration(400);
                handlerCenterView.postDelayed(runnableCenterView,400);
                showed[0] = true;
            } else {
                textView.animate().alpha(0.0f).setDuration(600);
            }
        };

        handlerCenterView.post(runnableCenterView);
    }

    public static void showDoubleTap(Context context,ImageView ivDoubleTap){
        Glide.with(context).load(ContextCompat.getDrawable(context,R.drawable.icon_double_tap_camera_engine)).into(ivDoubleTap);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ivDoubleTap.setVisibility(View.GONE);
            }
        },1750);
    }

    public static void lightEffect(ImageView ivFlash, boolean state){
        int duration = 150;

        TranslateAnimation animationDown = new TranslateAnimation(0.0f,0.0f,0.0f,50f);
        animationDown.setDuration(duration);
        TranslateAnimation animationUp = new TranslateAnimation(0.0f,0.0f,50.0f,0.0f);
        animationUp.setDuration(duration);

        animationDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ivFlash.startAnimation(animationUp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(state)
                    ivFlash.setBackgroundResource(R.drawable.icon_light_on_camera_engine);
                else
                    ivFlash.setBackgroundResource(R.drawable.icon_light_off_camera_engine);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivFlash.startAnimation(animationDown);
    }
}
