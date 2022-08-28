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

package com.hms.explorehms.huawei.feature_panorama;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.panorama.Panorama;
import com.huawei.hms.panorama.PanoramaInterface;
import com.huawei.hms.panorama.ResultCode;

import java.io.IOException;

public class LocalDisplayActivity extends Activity implements View.OnTouchListener {

    private static final String LOG_TAG = "LocalDisplayActivity";

    private RelativeLayout mLayout;

    private View mPanoramaView;

    private TextView mChangeModeButton,mChangeRenderModeButton;

    private MediaPlayer mPlayer;
    //Create a local object.
    private PanoramaInterface.PanoramaLocalInterface mLocalInstance;

    private int controlModeCount = 0;
    private int controlRenderModeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doFullScreen();

        setContentView(R.layout.activity_local_display);
        mLayout = findViewById(R.id.relativeLayout);

        mChangeModeButton = findViewById(R.id.changeModeButton);
        mChangeRenderModeButton = findViewById(R.id.changeRenderModeButton);

        //Initialize the local object.
        mLocalInstance = Panorama.getInstance().getLocalInstance(this);
        if (mLocalInstance == null) {
            logAndToast("mLocalInstance is null");
            return;
        }

        int ret = mLocalInstance.init();
        if (ret != ResultCode.SUCCEED) {
            logAndToast("mLocalInstance init failed " + ret);
            return;
        }

        initControlMode();
        initRenderMode();

        Intent intent = getIntent();
        int id = intent.getIntExtra("ViewId", -1);
        doDisplayById(id);

    }

    //You can call the setControlMode API to change the control mode as needed
    private void initControlMode() {
        mChangeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controlModeCount % 3 == 0) {
                    mLocalInstance.setControlMode(PanoramaInterface.CONTROL_TYPE_TOUCH);
                    mChangeModeButton.setText(R.string.button_touch);
                } else if (controlModeCount % 3 == 1) {
                    mLocalInstance.setControlMode(PanoramaInterface.CONTROL_TYPE_POSE);
                    mChangeModeButton.setText(R.string.button_pose);
                } else {
                    mLocalInstance.setControlMode(PanoramaInterface.CONTROL_TYPE_MIX);
                    mChangeModeButton.setText(R.string.button_mix);
                }
                controlModeCount++;
            }
        });
        mChangeModeButton.performClick();
    }

    private void initRenderMode() {
        mChangeRenderModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controlRenderModeCount % 3 == 0) {
                    mLocalInstance.setValue(PanoramaInterface.KEY_RENDER_MODE,PanoramaInterface.VALUE_RENDER_MODE_NORMAL);
                    mChangeRenderModeButton.setText(R.string.render_mode_normal);
                } else if (controlRenderModeCount % 3 == 1) {
                    mLocalInstance.setValue(PanoramaInterface.KEY_RENDER_MODE,PanoramaInterface.VALUE_RENDER_MODE_POLAR);
                    mChangeRenderModeButton.setText(R.string.render_mode_polar);
                } else {
                    mLocalInstance.setValue(PanoramaInterface.KEY_RENDER_MODE,PanoramaInterface.VALUE_RENDER_MODE_ASTEROID);
                    mChangeRenderModeButton.setText(R.string.render_mode_asteroid);
                }
                controlRenderModeCount++;
            }
        });
        mChangeRenderModeButton.performClick();
    }

    @SuppressLint("NonConstantResourceId")
    private void doDisplayById(int id) {
        switch (id) {
            case R.id.buttonInAppSpherical:
                doDisplaySpherical();
                break;
            case R.id.buttonInAppVideo:
                doDisplayVideo();
                break;
            default:
                Log.w(LOG_TAG, "Invalid id " + id);
                break;
        }
        if (mChangeModeButton != null) {
            mChangeModeButton.bringToFront();
        }

        if (mChangeRenderModeButton != null) {
            mChangeRenderModeButton.bringToFront();
        }
    }

    /*
    * Obtain the view object.
       View getView()
     **/
    private void addViewToLayout() {
        mPanoramaView = mLocalInstance.getView();
        if (mPanoramaView == null) {
            logAndToast("getView failed");
            return;
        }

        //Update the touch event in real time
        mPanoramaView.setOnTouchListener(this);
        mLayout.addView(mPanoramaView);
    }

    //Set a panoramic image and specify the type
    //Displaying a panoramic image
    private void doDisplaySpherical() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pano);
        Log.i(LOG_TAG, "test uri " + uri.toString());
        int ret = mLocalInstance.setImage(uri, PanoramaInterface.IMAGE_TYPE_SPHERICAL);
        if (ret != ResultCode.SUCCEED) {
            logAndToast("doDisplaySpherical setImage failed " + ret);
            return;
        }

        addViewToLayout();
    }

    //Displaying a panoramic video
    private void doDisplayVideo() {
        Surface videoSurface = mLocalInstance.getSurface(PanoramaInterface.IMAGE_TYPE_SPHERICAL);
        if (videoSurface == null) {
            logAndToast("videoSurface is null");
            return;
        }

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        initMediaPlayer(videoUri, videoSurface);

        addViewToLayout();
    }

    //After obtaining the surface, set it in MediaPlayer.
    private void initMediaPlayer(Uri videoUri, Surface surface) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getApplicationContext(), videoUri);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
        }
        mPlayer.setLooping(true);
        mPlayer.setSurface(surface);

        try {
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Media Player prepare exception");
        }
        mPlayer.start();

        float ratio = mPlayer.getVideoWidth() / (float) mPlayer.getVideoHeight();
        Log.i(LOG_TAG, "ratio = " + ratio);
        mLocalInstance.setValue(PanoramaInterface.KEY_VIDEO_RATIO, String.valueOf(ratio));
    }

    private void doFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final Window win = getWindow();
        win.getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void logAndToast(String message) {
        Log.e(LOG_TAG, message);
        Toast.makeText(LocalDisplayActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mPanoramaView != null && mPanoramaView.equals(view) && mLocalInstance != null) {
            mLocalInstance.updateTouchEvent(motionEvent);
            return true;

        }
        return false;
    }

    /*
    * Deinitialize the local object.
       void deInit()
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }

        if (mLocalInstance != null) {
            mLocalInstance.deInit();
        }
    }


}