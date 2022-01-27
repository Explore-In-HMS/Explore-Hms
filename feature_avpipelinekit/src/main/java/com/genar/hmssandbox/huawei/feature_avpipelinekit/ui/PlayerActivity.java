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

package com.genar.hmssandbox.huawei.feature_avpipelinekit.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.genar.hmssandbox.huawei.feature_avpipelinekit.R;
import com.huawei.hms.avpipeline.api.MediaParcel;
import com.huawei.hms.avpipeline.sdk.AVPLoader;
import com.huawei.hms.avpipeline.sdk.MediaPlayer;

import java.util.concurrent.CountDownLatch;

public abstract class PlayerActivity extends AppCompatActivity {
    private static final String TAG = "AVP-PlayerActivity";
    private static final int MSG_INIT_FWK = 1;
    private static final int MSG_CREATE = 2;
    private static final int MSG_PREPARE_DONE = 3;
    private static final int MSG_RELEASE = 4;
    private static final int MSG_START_DONE = 5;
    private static final int MSG_STOP_DONE = 6;
    private static final int MSG_SET_DURATION = 7;
    private static final int MSG_GET_CURRENT_POS = 8;
    private static final int MSG_UPDATE_PROGRESS_POS = 9;
    private static final int MSG_SEEK = 10;


    private static final int MIN_CLICK_TIME_INTERVAL = 3000;
    private static long mLastClickTime = 0;
    protected Switch mSwitch;
    protected MediaPlayer mPlayer;
    private SurfaceView mSurfaceVideo;
    private SurfaceHolder mVideoHolder;
    private TextView mTextCurMsec;
    private TextView soundText;
    private TextView mTextTotalMsec;
    private String mFilePath = null;
    private boolean mIsPlaying = false;
    private long mDuration = -1;
    private SeekBar mProgressBar;
    private SeekBar mVolumeSeekBar;
    private AudioManager mAudioManager;
    private Handler mMainHandler;
    private CountDownLatch mCountDownLatch;

    private Handler mPlayerHandler = null;
    private HandlerThread mPlayerThread = null;

    void makeToastAndRecordLog(int priority, String msg) {
        Log.println(priority, TAG, msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayerThread = new HandlerThread(TAG);
        mPlayerThread.start();
        if (mPlayerThread.getLooper() != null) {
            mPlayerHandler = new Handler(mPlayerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_SEEK: {
                            seek((long) msg.obj);
                            break;
                        }
                        case MSG_GET_CURRENT_POS: {
                            getCurrentPos();
                            break;
                        }
                        case MSG_INIT_FWK: {
                            initFwk();
                            break;
                        }
                        case MSG_CREATE: {
                            mCountDownLatch = new CountDownLatch(1);
                            startPlayMedia();
                            break;
                        }
                        case MSG_START_DONE: {
                            onStartDone();
                            break;
                        }
                        case MSG_PREPARE_DONE: {
                            onPrepareDone();
                            break;
                        }
                        case MSG_RELEASE: {
                            stopPlayMedia();
                            mCountDownLatch.countDown();
                            break;
                        }
                    }
                    super.handleMessage(msg);
                }
            };


            initSeekBar();
            soundText.setVisibility(View.GONE);
            initAllView();
            mPlayerHandler.sendEmptyMessage(MSG_INIT_FWK);
        }
    }

    private void getCurrentPos() {
        long currMsec = mPlayer.getCurrentPosition();
        if (currMsec == -1) {
            Log.e(TAG, "get current position failed, try again");
            mPlayerHandler.sendEmptyMessageDelayed(MSG_GET_CURRENT_POS, 300);
            return;
        }
        if (currMsec < mDuration) {
            Message msgTime = mPlayerHandler.obtainMessage();
            msgTime.obj = currMsec;
            msgTime.what = MSG_UPDATE_PROGRESS_POS;
            mMainHandler.sendMessage(msgTime);
        }
        mPlayerHandler.sendEmptyMessageDelayed(MSG_GET_CURRENT_POS, 300);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void initAllView() {
        mSurfaceVideo = findViewById(R.id.surfaceViewup);
        mVideoHolder = mSurfaceVideo.getHolder();
        mVideoHolder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                if (holder != mVideoHolder) {
                    Log.i(TAG, "holder unmatch, create");
                    return;
                }
                Log.i(TAG, "holder match, create");

                mPlayerHandler.sendEmptyMessage(MSG_CREATE);
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (holder != mVideoHolder) {
                    Log.i(TAG, "holder unmatch, change");
                    return;
                }
                Log.i(TAG, "holder match, change");
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                if (holder != mVideoHolder) {
                    Log.i(TAG, "holder unmatch, destroy");
                    return;
                }
                Log.i(TAG, "holder match, destroy ... ");
                mPlayerHandler.sendEmptyMessage(MSG_RELEASE);
                try {
                    mCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "holder match, destroy done ");

            }
        });

        final ImageButton btn = findViewById(R.id.startStopButton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "click button");
                if (mPlayer == null) {
                    return;
                }
                if (mIsPlaying) {
                    mIsPlaying = false;
                    mPlayer.pause();
                    btn.setBackgroundResource(R.drawable.pause);
                    mPlayer.setVolume(0.6f, 0.6f);
                } else {
                    mIsPlaying = true;
                    mPlayer.start();
                    btn.setBackgroundResource(R.drawable.play);
                }
            }
        });

        final ImageButton mutBtn = findViewById(R.id.muteButton);
        mutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPlayer == null) {
                    return;
                }
                MediaPlayer.VolumeInfo volumeInfo = mPlayer.getVolume();
                boolean isMute = mPlayer.getMute();
                Log.i(TAG, "now is mute?: " + isMute);
                if (isMute) {
                    mutBtn.setBackgroundResource(R.drawable.volume);
                    mPlayer.setVolume(volumeInfo.left, volumeInfo.right);
                    isMute = false;
                    mPlayer.setMute(isMute);
                } else {
                    mutBtn.setBackgroundResource(R.drawable.mute);
                    isMute = true;
                    mPlayer.setMute(isMute);
                }
            }
        });

        Button selectBtn = findViewById(R.id.selectFileBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "user is choosing file");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivityForResult(Intent.createChooser(intent, "choose file"), 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(PlayerActivity.this, "install file manager first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwitch = findViewById(R.id.switchSr);
    }

    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1 || resultCode != RESULT_OK) {
            makeToastAndRecordLog(Log.ERROR, "startActivityForResult failed");
            return;
        }
        Uri fileuri = data.getData();
        if (!DocumentsContract.isDocumentUri(this, fileuri)) {
            makeToastAndRecordLog(Log.ERROR, "this uri is not Document Uri");
            return;
        }
        String uriAuthority = fileuri.getAuthority();
        if (!uriAuthority.equals("com.android.externalstorage.documents")) {
            makeToastAndRecordLog(Log.ERROR, "this uri is:" + uriAuthority + ", but we need external storage document");
            return;
        }
        String docId = DocumentsContract.getDocumentId(fileuri);
        String[] split = docId.split(":");
        if (!split[0].equals("primary")) {
            makeToastAndRecordLog(Log.ERROR, "this document id is:" + docId + ", but we need primary:*");
            return;
        }
        mFilePath = Environment.getExternalStorageDirectory() + "/" + split[1];
        makeToastAndRecordLog(Log.INFO, mFilePath);
    }

    private void initSeekBar() {
        mProgressBar = findViewById(R.id.seekBar);
        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "bar progress=" + seekBar.getProgress()); // get progress percent
                long seekToMsec = (long) (seekBar.getProgress() / 100.0 * mDuration);
                Message msg = mPlayerHandler.obtainMessage();
                msg.obj = seekToMsec;
                msg.what = MSG_SEEK;
                mPlayerHandler.sendMessage(msg);
            }
        });
        mTextCurMsec = findViewById(R.id.textViewNow);
        mTextTotalMsec = findViewById(R.id.textViewTotal);
        soundText=findViewById(R.id.tv_info_sound);

        mVolumeSeekBar = findViewById(R.id.volumeSeekBar);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumeSeekBar.setProgress(currentVolume);
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && (mPlayer != null)) {
                    MediaPlayer.VolumeInfo volumeInfo = mPlayer.getVolume();
                    volumeInfo.left = (float) (progress * 0.1);
                    volumeInfo.right = (float) (progress * 0.1);
                    mPlayer.setVolume(volumeInfo.left, volumeInfo.right);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initFwk() {
        if (AVPLoader.isInit()) {
            Log.d(TAG, "avp framework already inited");
            return;
        }
        boolean ret = AVPLoader.initFwk(getApplicationContext());
        if (ret) {
            makeToastAndRecordLog(Log.INFO, "avp framework load succ");
        } else {
            makeToastAndRecordLog(Log.ERROR, "avp framework load failed");
        }
    }

    protected int getPlayerType() {
        return MediaPlayer.PLAYER_TYPE_AV;
    }

    protected void setGraph() {
    }

    protected void setListener() {
    }

    private void seek(long seekPosMs) {
        if (mDuration > 0 && mPlayer != null) {
            Log.d(TAG, "seekToMsec=" + seekPosMs);
            mPlayer.seek(seekPosMs);
        }
    }

    private void startPlayMedia() {
        if (mFilePath == null) {
            return;
        }
        Log.i(TAG, "start to play media file " + mFilePath);

        mPlayer = MediaPlayer.create(getPlayerType());
        if (mPlayer == null) {
            return;
        }
        setGraph();
        if (getPlayerType() == MediaPlayer.PLAYER_TYPE_AV) {
            int ret = mPlayer.setVideoDisplay(mVideoHolder.getSurface());
            if (ret != 0) {
                makeToastAndRecordLog(Log.ERROR, "setVideoDisplay failed, ret=" + ret);
                return;
            }
        }
        int ret = mPlayer.setDataSource(mFilePath);
        if (ret != 0) {
            makeToastAndRecordLog(Log.ERROR, "setDataSource failed, ret=" + ret);
            return;
        }

        mPlayer.setOnStartCompletedListener(new MediaPlayer.OnStartCompletedListener() {
            @Override
            public void onStartCompleted(MediaPlayer mp, int param1, int param2, MediaParcel parcel) {
                if (param1 != 0) {
                    Log.e(TAG, "start failed, return " + param1);
                    mPlayerHandler.sendEmptyMessage(MSG_RELEASE);
                } else {
                    mPlayerHandler.sendEmptyMessage(MSG_START_DONE);
                }
            }
        });

        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp, int param1, int param2, MediaParcel parcel) {
                if (param1 != 0) {
                    Log.e(TAG, "prepare failed, return " + param1);
                    mPlayerHandler.sendEmptyMessage(MSG_RELEASE);
                } else {
                    mPlayerHandler.sendEmptyMessage(MSG_PREPARE_DONE);
                }
            }
        });

        mPlayer.setOnPlayCompletedListener(new MediaPlayer.OnPlayCompletedListener() {
            @Override
            public void onPlayCompleted(MediaPlayer mp, int param1, int param2, MediaParcel parcel) {
                Message msgTime = mMainHandler.obtainMessage();
                msgTime.obj = mDuration;
                msgTime.what = MSG_UPDATE_PROGRESS_POS;
                mMainHandler.sendMessage(msgTime);
                Log.i(TAG, "sendMessage duration");
                mPlayerHandler.sendEmptyMessage(MSG_RELEASE);
            }
        });

        setListener();
        mPlayer.prepare();
    }

    private void onPrepareDone() {
        Log.i(TAG, "onPrepareDone");
        if (mPlayer == null) {
            return;
        }
        mPlayer.start();
    }

    private void onStartDone() {
        Log.i(TAG, "onStartDone");
        mIsPlaying = true;
        mDuration = mPlayer.getDuration();
        Log.d(TAG, "duration=" + mDuration);

        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_PROGRESS_POS: {
                        long currMsec = (long) msg.obj;
                        Log.i(TAG, "currMsec: " + currMsec);
                        mProgressBar.setProgress((int) (currMsec / (double) mDuration * 100));
                        mTextCurMsec.setText(msecToString(currMsec));
                    }
                    case MSG_SET_DURATION: {
                        mTextTotalMsec.setText(msecToString(mDuration));
                        break;
                    }
                }
                super.handleMessage(msg);
            }
        };

        mPlayerHandler.sendEmptyMessage(MSG_GET_CURRENT_POS);
        mMainHandler.sendEmptyMessage(MSG_SET_DURATION);
    }

    private void stopPlayMedia() {
        if (mFilePath == null) {
            return;
        }
        Log.i(TAG, "stopPlayMedia doing");
        mIsPlaying = false;
        if (mPlayer == null) {
            return;
        }
        mPlayerHandler.removeMessages(MSG_GET_CURRENT_POS);
        mPlayer.stop();
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        Log.i(TAG, "stopPlayMedia done");
    }

    @SuppressLint("DefaultLocale")
    private String msecToString(long msec) {
        long timeInSec = msec / 1000;
        return String.format("%02d:%02d", timeInSec / 60, timeInSec % 60);
    }

    protected boolean isFastClick() {
        long curTime = System.currentTimeMillis();
        if ((curTime - mLastClickTime) < MIN_CLICK_TIME_INTERVAL) {
            return true;
        }
        mLastClickTime = curTime;
        return false;
    }
}
