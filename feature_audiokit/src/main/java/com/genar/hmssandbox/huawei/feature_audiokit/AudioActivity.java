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
package com.genar.hmssandbox.huawei.feature_audiokit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.feature_audiokit.databinding.ActivityAudioBinding;
import com.huawei.hms.api.bean.HwAudioPlayItem;
import com.huawei.hms.audiokit.player.callback.HwAudioConfigCallBack;
import com.huawei.hms.audiokit.player.manager.HwAudioConfigManager;
import com.huawei.hms.audiokit.player.manager.HwAudioManager;
import com.huawei.hms.audiokit.player.manager.HwAudioManagerFactory;
import com.huawei.hms.audiokit.player.manager.HwAudioPlayerConfig;
import com.huawei.hms.audiokit.player.manager.HwAudioPlayerManager;
import com.huawei.hms.audiokit.player.manager.HwAudioQueueManager;
import com.huawei.hms.audiokit.player.manager.HwAudioStatusListener;
import com.huawei.hms.common.util.Logger;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class AudioActivity extends AppCompatActivity implements CurrentPlaylistAdapter.OnItemClickListener {

    private static final String TAG = "AudioActivity";
    private static final String CHANNEL_ID = "4";
    private static final String MEDIACENTER_CANCEL_NOTIFICATION = "com.huawei.hms.mediacenter.cancel_notification";
    private final List<HwAudioStatusListener> mTempListeners = new CopyOnWriteArrayList<>();
    private final BroadcastReceiver mClickEventReceiver = new ClickEventReceiver();
    ActivityAudioBinding binding;
    NotificationCompat.Builder builder;
    boolean isReallyPlaying = true;
    String isLocal;
    String nameOfPlaylist;
    List<HwAudioPlayItem> tmpHwPlayItemList = new ArrayList<>();
    PlaylistCreator playlistCreator = new PlaylistCreator(this);
    private HwAudioManager mHwAudioManager;
    private HwAudioPlayerManager mHwAudioPlayerManager;
    HwAudioStatusListener mPlayListener = new HwAudioStatusListener() {
        @Override
        public void onSongChange(HwAudioPlayItem hwAudioPlayItem) {
            setSongDetails(hwAudioPlayItem);
            if (mHwAudioPlayerManager.getOffsetTime() != -1 && mHwAudioPlayerManager.getDuration() != -1)
                updateSeekBar(mHwAudioPlayerManager.getOffsetTime(), mHwAudioPlayerManager.getDuration());
        }

        @Override
        public void onQueueChanged(List<HwAudioPlayItem> list) {
            if (mHwAudioPlayerManager != null && list.size() != 0 && !isReallyPlaying) {
                mHwAudioPlayerManager.play();
                isReallyPlaying = true;
                binding.playButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_pause));
            }
        }

        @Override
        public void onBufferProgress(int percent) {
            /*
                do nothing
             */
        }

        @Override
        public void onPlayProgress(final long currentPosition, long duration) {
            updateSeekBar(currentPosition, duration);
        }

        @Override
        public void onPlayCompleted(boolean isStopped) {
            if (mHwAudioPlayerManager != null && isStopped) {
                mHwAudioPlayerManager.playNext();
            }
            isReallyPlaying = !isStopped;
        }

        @Override
        public void onPlayError(int errorCode, boolean isUserForcePlay) {
            if (errorCode == 2010003) {
                Toast.makeText(AudioActivity.this, "Network error! Please make sure you are connected",
                        Toast.LENGTH_LONG).show();
            } else if (errorCode == 2010004)
                Toast.makeText(AudioActivity.this, "Some unknown error occurred.", Toast.LENGTH_LONG).show();
            Log.e("onError msg:", "Code is " + errorCode);
        }

        @Override
        public void onPlayStateChange(boolean isPlaying, boolean isBuffering) {
            if (isPlaying || isBuffering) {
                binding.playButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_pause));
                isReallyPlaying = true;
            } else {
                binding.playButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_play_arrow));
                isReallyPlaying = false;
                if (builder != null)
                    builder.setOngoing(false); //probably not working as intended
            }

        }
    };
    private HwAudioConfigManager mHwAudioConfigManager;
    private HwAudioQueueManager mHwAudioQueueManager;
    private List<HwAudioPlayItem> playList;
    private List<HwAudioPlayItem> wholeLocalPlayList;
    private List<HwAudioPlayItem> wholeOnlinePlayList;
    private List<Integer> indexList = new ArrayList<>();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Custom service to stop playback when the activity is destroyed.
        //This service will destroy itself along with the music in the background.
        Intent intent = new Intent(this, AudioKillService.class);
        startService(intent);

        binding = ActivityAudioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupToolbar();

        nameOfPlaylist = (getIntent().getExtras() != null) ? getIntent().getExtras().getString("nameOfPlaylist") : "not found";
        indexList = (getIntent().getExtras() != null) ? getIntent().getExtras().getIntegerArrayList("playlist") : null;
        isLocal = (getIntent().getExtras() != null) ? getIntent().getExtras().getString("playlistType") : "not found";

        if (isLocal != null && isLocal.equals("online")) {
            wholeOnlinePlayList = playlistCreator.getOnlinePlaylist();
            getRelevantPlaylist(wholeOnlinePlayList);
        } else {
            wholeLocalPlayList = playlistCreator.getLocalPlayList();
            getRelevantPlaylist(wholeLocalPlayList);
        }

        if (nameOfPlaylist != null && !nameOfPlaylist.equals("")) {
            binding.playlistNameTextView.setText(nameOfPlaylist);
        }

        binding.albumPictureImageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground_audio));

        initializeManagerAndGetPlayList(this);

        binding.volumeSeekBar.setMax(100); //sound has 100 levels
        binding.volumeSeekBar.setProgress(100);
        binding.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.volumeSeekBar.setProgress(progress);
                if (mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.setVolume(progress);
                }
                String progressString = progress + "";
                binding.volumeTextView.setText(progressString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                /*
                    do nothing
                 */
            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    binding.volumeImageView.setImageDrawable(getDrawable(R.drawable.ic_volume_off));
                } else {
                    binding.volumeImageView.setImageDrawable(getDrawable(R.drawable.ic_volume_up));
                }
            }
        });

        IntentFilter cancelIntent = new IntentFilter();
        cancelIntent.addAction(MEDIACENTER_CANCEL_NOTIFICATION);
        registerReceiver(mClickEventReceiver, cancelIntent);

        handleOnClicks();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void handleOnClicks() {
        final Drawable drawablePlay = getDrawable(R.drawable.ic_play_arrow);
        final Drawable drawablePause = getDrawable(R.drawable.ic_pause);

        binding.playButtonImageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if (binding.playButtonImageView.getDrawable().getConstantState().equals(drawablePlay.getConstantState()) && mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.play();
                    binding.playButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_pause));
                    isReallyPlaying = true;
                } else if (binding.playButtonImageView.getDrawable().getConstantState().equals(drawablePause.getConstantState()) && mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.pause();
                    binding.playButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_play_arrow));
                    isReallyPlaying = false;
                }
            }
        });

        binding.nextSongImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.playNext();
                    isReallyPlaying = true;
                }
            }
        });

        binding.speedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.setPlaySpeed(2f);
                }
            }
        });

        binding.previousSongImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.playPre();
                    isReallyPlaying = true;
                }
            }
        });

        binding.albumPictureImageView.setOnTouchListener(new OnSwipeTouchListener(AudioActivity.this) {
            @Override
            public void onSwipeLeft() {
                if (mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.playPre();
                    isReallyPlaying = true;
                }
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {
                if (mHwAudioPlayerManager != null) {
                    mHwAudioPlayerManager.playNext();
                    isReallyPlaying = true;
                }
                super.onSwipeRight();
            }
        });

        binding.containerLayout.setVisibility(View.GONE);

        binding.playlistImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.containerLayout.getVisibility() == View.GONE) {
                    if (mHwAudioPlayerManager != null)
                        binding.containerLayout.setVisibility(View.VISIBLE);
                    else {
                        Toast.makeText(AudioActivity.this, "Please wait until the playback is ready", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    binding.containerLayout.setVisibility(View.GONE);
                }
            }
        });

        /*
        Playback mode.
            0: sequential playback
            1: shuffling songs
            2: repeating a playlist
            3: repeating a song
        */

        final Drawable shuffleDrawable = getDrawable(R.drawable.ic_shuffle);
        final Drawable orderDrawable = getDrawable(R.drawable.ic_normal_playlist_mode);
        final Drawable loopItself = getDrawable(R.drawable.ic_repeat_one);
        final Drawable loopPlaylist = getDrawable(R.drawable.ic_repeat);

        binding.shuffleButtonImageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if (mHwAudioPlayerManager != null) {
                    if (binding.shuffleButtonImageView.getDrawable().getConstantState().equals(shuffleDrawable.getConstantState())) {
                        mHwAudioPlayerManager.setPlayMode(0);
                        binding.shuffleButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_normal_playlist_mode));
                        Toast.makeText(AudioActivity.this, "Normal order", Toast.LENGTH_SHORT).show();
                    } else if (binding.shuffleButtonImageView.getDrawable().getConstantState().equals(orderDrawable.getConstantState())) {
                        mHwAudioPlayerManager.setPlayMode(1);
                        binding.shuffleButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_normal_playlist_mode));
                        Toast.makeText(AudioActivity.this, "Shuffle songs", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.loopButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHwAudioPlayerManager != null) {
                    if (binding.loopButtonImageView.getDrawable().getConstantState().equals(loopItself.getConstantState())) {
                        mHwAudioPlayerManager.setPlayMode(2);
                        binding.loopButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_repeat));
                        Toast.makeText(AudioActivity.this, "Loop playlist", Toast.LENGTH_SHORT).show();
                    } else if (binding.loopButtonImageView.getDrawable().getConstantState().equals(loopPlaylist.getConstantState())) {
                        mHwAudioPlayerManager.setPlayMode(3);
                        binding.loopButtonImageView.setImageDrawable(getDrawable(R.drawable.ic_repeat_one));
                        Toast.makeText(AudioActivity.this, "Loop the song", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void getRelevantPlaylist(List<HwAudioPlayItem> relevantList) {
        if (indexList != null) {
            for (int i = 0; i < indexList.size(); i++) {
                tmpHwPlayItemList.add(relevantList.get(indexList.get(i)));
            }
            playList = tmpHwPlayItemList;
        } else {
            Toast.makeText(this, "Your playlist is empty!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSeekBar(final long currentPosition, long duration) {
        //seekbar
        binding.musicSeekBar.setMax((int) (duration / 1000));

        if (mHwAudioPlayerManager != null) {
            int mCurrentPosition = (int) (currentPosition / 1000);
            binding.musicSeekBar.setProgress(mCurrentPosition);

            setProgressText(mCurrentPosition);
        }

        binding.musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*
                     do nothing
                */
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                /*
                     do nothing
                */
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mHwAudioPlayerManager != null && fromUser) {
                    mHwAudioPlayerManager.seekTo(progress * 1000);
                }
                if (!isReallyPlaying) {
                    setProgressText(progress);
                }
            }
        });
    }

    public void setProgressText(int progress) {
        String progressText = String.format(Locale.US, "%02d:%02d", computeTimeToMinute(progress)
                , computeTimeToSeconds(progress));
        binding.progressTextView.setText(progressText);
    }

    private Long computeTimeToMinute(int progress) {
        return TimeUnit.MILLISECONDS.toMinutes((long) progress * 1000);
    }

    private Long computeTimeToSeconds(int progress) {
        return TimeUnit.MILLISECONDS.toSeconds((long) progress * 1000) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) progress * 1000));
    }

    public void setSongDetails(HwAudioPlayItem currentItem) {
        if (currentItem != null) {
            if (isLocal.equals("local"))
                getBitmapOfCover(currentItem);
            else {
                binding.albumPictureImageView.setImageDrawable(getDrawable(R.drawable.ic_launcher_foreground_audio));
            }

            if (currentItem.getAudioTitle() != null) {
                if (!currentItem.getAudioTitle().equals(""))
                    binding.songNameTextView.setText(currentItem.getAudioTitle());
                else
                    binding.songNameTextView.setText(R.string.choose_song);
            } else {
                binding.songNameTextView.setText(R.string.choose_song);
            }

            if (currentItem.getSinger() != null) {
                if (!currentItem.getSinger().equals(""))
                    binding.artistNameTextView.setText(currentItem.getSinger());
                else
                    binding.artistNameTextView.setText(R.string.from_playlist);
            } else
                binding.artistNameTextView.setText(R.string.from_playlist);


            binding.albumNameTextView.setText(R.string.album_unknown); //there is no field assinged to this in HwAudioPlayItem
            binding.progressTextView.setText(R.string.zero_and_zero);

            long durationTotal = currentItem.getDuration();
            String totalDurationText = String.format(Locale.US, "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(durationTotal),
                    TimeUnit.MILLISECONDS.toSeconds(durationTotal) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationTotal))
            );
            binding.totalDurationTextView.setText(totalDurationText);
        } else {
            binding.songNameTextView.setText(R.string.choose_song);
            binding.artistNameTextView.setText(R.string.from_playlist);
            binding.albumNameTextView.setText(R.string.right_top_of_screen);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void initializeManagerAndGetPlayList(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                HwAudioPlayerConfig hwAudioPlayerConfig = new HwAudioPlayerConfig(context);
                HwAudioManagerFactory.createHwAudioManager(hwAudioPlayerConfig, new HwAudioConfigCallBack() {
                    @Override
                    public void onSuccess(HwAudioManager hwAudioManager) {
                        try {
                            mHwAudioManager = hwAudioManager;
                            mHwAudioPlayerManager = mHwAudioManager.getPlayerManager();
                            mHwAudioConfigManager = mHwAudioManager.getConfigManager();
                            mHwAudioQueueManager = mHwAudioManager.getQueueManager();

                            mHwAudioQueueManager.addPlayItemList(playList, 0);

                            if (playList != null) {
                                doListenersAndNotifications();
                                mHwAudioPlayerManager.setPlaySpeed(1f);
                            } else {
                                Log.e("TAG", "playlist null");
                            }
                        } catch (Exception e) {
                            Log.e("TAG", "player init fail", e);
                        }
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.e("TAG", "init err:" + errorCode);
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                addListenerToManager(mPlayListener);

                CurrentPlaylistAdapter currentPlaylistAdapter = new CurrentPlaylistAdapter(AudioActivity.this, mHwAudioQueueManager, playList, false);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AudioActivity.this);
                binding.playlistRecyclerView.setLayoutManager(layoutManager);
                currentPlaylistAdapter.setOnItemClickListener(AudioActivity.this);
                binding.playlistRecyclerView.setAdapter(currentPlaylistAdapter);

                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    private Bitmap getAlbumImage(String path) {
        try {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            byte[] data = mmr.getEmbeddedPicture();
            if (data != null)
                return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
            return null;
        }
        return null;
    }

    public Bitmap getBitmapOfCover(HwAudioPlayItem currItem) {
        if (currItem != null) {
            String currentSongPath = currItem.getFilePath();
            if (currentSongPath != null && isLocal.equals("local")) {
                final Bitmap tmpMap = getAlbumImage(currentSongPath);
                if (tmpMap != null) {
                    runOnUiThread(() -> binding.albumPictureImageView.setImageBitmap(tmpMap));

                    return tmpMap;
                }
            }
        }
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void doListenersAndNotifications() {
        new Handler(Looper.getMainLooper()).post(() -> {
            for (HwAudioStatusListener listener : mTempListeners) {
                try {
                    mHwAudioManager.addPlayerStatusListener(listener);
                } catch (RemoteException e) {
                    Log.e("TAG", "TAG", e);
                }
            }

            createNotificationChannel();

            mHwAudioConfigManager.setSaveQueue(true);
            mHwAudioConfigManager.setNotificationFactory(notificationConfig -> {
                builder = new NotificationCompat.Builder(getApplication(), CHANNEL_ID);
                RemoteViews remoteViews = new RemoteViews(getApplication().getPackageName(), R.layout.notification_player);
                builder.setContent(remoteViews);
                builder.setSmallIcon(R.drawable.icon_app);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setCustomBigContentView(remoteViews);
                NotificationUtils.addChannel(getApplication(), NotificationUtils.NOTIFY_CHANNEL_ID_PLAY, builder);
                boolean isQueueEmpty = mHwAudioManager.getQueueManager().isQueueEmpty();
                Bitmap bitmap;
                bitmap = notificationConfig.getBitmap();
                setBitmap(remoteViews, bitmap);
                boolean isPlaying = mHwAudioManager.getPlayerManager().isPlaying() && !isQueueEmpty;
                remoteViews.setImageViewResource(R.id.image_toggle, isPlaying ? R.drawable.ic_notification_stop : R.drawable.ic_notification_play);
                HwAudioPlayItem playItem = mHwAudioManager.getQueueManager().getCurrentPlayItem();
                remoteViews.setTextViewText(R.id.text_song, playItem.getAudioTitle());
                remoteViews.setTextViewText(R.id.text_artist, playItem.getSinger());
                remoteViews.setImageViewResource(R.id.image_last, R.drawable.ic_notification_before);
                remoteViews.setImageViewResource(R.id.image_next, R.drawable.ic_notification_next);
                remoteViews.setOnClickPendingIntent(R.id.image_last, notificationConfig.getPrePendingIntent());
                remoteViews.setOnClickPendingIntent(R.id.image_toggle, notificationConfig.getPlayPendingIntent());
                remoteViews.setOnClickPendingIntent(R.id.image_next, notificationConfig.getNextPendingIntent());
                remoteViews.setOnClickPendingIntent(R.id.image_close, getCancelPendingIntent());
                remoteViews.setOnClickPendingIntent(R.id.layout_content, getMainIntent());

                return builder.build();
            });
        });
    }

    private void setBitmap(RemoteViews remoteViews, Bitmap bitmap) {
        HwAudioPlayItem tmpItem = mHwAudioQueueManager.getCurrentPlayItem();
        Bitmap imageCoverOfMusic = getBitmapOfCover(tmpItem);
        if (imageCoverOfMusic != null) {
            remoteViews.setImageViewBitmap(R.id.image_cover, imageCoverOfMusic);
        } else {
            if (bitmap != null) {
                remoteViews.setImageViewBitmap(R.id.image_cover, bitmap);
            } else {
                remoteViews.setImageViewResource(R.id.image_cover, R.drawable.icon_notifaction_default);
            }
        }
    }

    private PendingIntent getCancelPendingIntent() {
        Log.i("TAG", "getCancelPendingIntent");
        Intent closeIntent = new Intent(MEDIACENTER_CANCEL_NOTIFICATION);
        closeIntent.setPackage(getApplication().getPackageName());
        return PendingIntent.getBroadcast(getApplicationContext(), 2, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getMainIntent() {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setClass(getApplication().getBaseContext(), AudioMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return PendingIntent.getActivity(getApplication(), 0, intent, 0);
    }

    //This method makes sure that the instance of @HwAudioStatusListener is added to the manager (@HwAudioManager),
    //so that it can control the playback.
    //There is also a temporary list of listeners to attach it later, in case the manager has not been started yet, i.e. is null.
    public void addListenerToManager(HwAudioStatusListener listener) {
        if (mHwAudioManager != null) {
            try {
                mHwAudioManager.addPlayerStatusListener(listener);
            } catch (RemoteException e) {
                Log.e("TAG", "TAG", e);
            }
        } else {
            mTempListeners.add(listener);
        }
    }

    @Override
    public void onItemClick(List<HwAudioPlayItem> myPlayList, int position) {
        if (mHwAudioPlayerManager != null) {
            mHwAudioPlayerManager.playList(myPlayList, position, 0);
            //doListenersAndNotifications(); //to make sure that notifications are updated
            // when changing between local and online playlist
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.containerLayout.getVisibility() == View.VISIBLE) {
            binding.containerLayout.setVisibility(View.GONE);
        } else {
            ArrayList<Integer> tmpIndexList = new ArrayList<>();

            if (isLocal != null) {
                if (isLocal.equals("online")) {
                    if (playList != null && tmpHwPlayItemList != null) {
                        for (int i = 0; i < playList.size(); i++) { //remaining audio files
                            tmpIndexList.add(wholeOnlinePlayList.indexOf(playList.get(i)));
                        }
                    }
                } else {
                    if (playList != null && tmpHwPlayItemList != null) {
                        for (int i = 0; i < playList.size(); i++) { //remaining audio files
                            tmpIndexList.add(wholeLocalPlayList.indexOf(playList.get(i)));
                        }
                    }
                }
            }

            if (mHwAudioPlayerManager != null)
                mHwAudioPlayerManager.stop();
            Intent returnIntent = new Intent();
            returnIntent.putIntegerArrayListExtra("sizeOfPlaylist", tmpIndexList);
            setResult(Activity.RESULT_OK, returnIntent);
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase);
    }

    // Receives intents when closing from notification bar
    private class ClickEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MEDIACENTER_CANCEL_NOTIFICATION.equals(action)) {
                Log.i("TAG", "onReceive----->cancelNotification");
                mHwAudioPlayerManager.stop();
            }
        }
    }
}