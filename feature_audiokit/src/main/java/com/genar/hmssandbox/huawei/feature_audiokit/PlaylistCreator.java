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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hms.api.bean.HwAudioPlayItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlaylistCreator {

    Context context;

    public PlaylistCreator(Context context){
        this.context = context;
    }

    public List<HwAudioPlayItem> getLocalPlayList() {
        List<HwAudioPlayItem> playItemList = new ArrayList<>();
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver == null) {
                return playItemList;
            }

            cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.Media.DISPLAY_NAME + " ASC");

            if (cursor != null) {
                if(!cursor.moveToNext()){
                    //there is no music, do sth
                    return playItemList; //return empty list for now
                }
                else{
                    //retrieving audio files that match our above mentioned criteria
                    //if the cursor.moveToNext() is not null, there is at least one audio file in the storage
                    //so we can use do while to not miss any of it
                    do{
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                        if (new File(path).exists()) {
                            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                            if (isMusic != 0) {
                                HwAudioPlayItem songItem = new HwAudioPlayItem();
                                songItem.setAudioTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                                songItem.setAudioId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)) + "");
                                songItem.setFilePath(path);
                                songItem.setOnline(0); //0 means local
                                songItem.setIsOnline(0); //0 means local
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    songItem.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                                }
                                else
                                    songItem.setDuration(0);
                                songItem.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                                playItemList.add(songItem);
                            }
                        }
                    }while(cursor.moveToNext());
                }
            }
            else{
                Toast.makeText(context, "We have a serious cursor problem here!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TAG,", "EXCEPTION", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return playItemList;
    }

    public List<HwAudioPlayItem> getOnlinePlaylist() {
        List<HwAudioPlayItem> playItemList = new ArrayList<>();
        HwAudioPlayItem audioPlayItem1 = new HwAudioPlayItem();
        audioPlayItem1.setAudioId("1000");
        audioPlayItem1.setSinger("Sample MP3 - Need lower bandwidth");
        audioPlayItem1.setOnlinePath("https://lfmusicservice.hwcloudtest.cn:18084/HMS/audio/Taoge-dayu.mp3");
        audioPlayItem1.setOnline(1);
        audioPlayItem1.setAudioTitle("Kalimba - MP3");
        playItemList.add(audioPlayItem1);

        HwAudioPlayItem audioPlayItem2 = new HwAudioPlayItem();
        audioPlayItem2.setAudioId("1001");
        audioPlayItem2.setSinger("Sample FLAC - Need higher bandwidth");
        audioPlayItem2.setOnlinePath("https://lfmusicservice.hwcloudtest.cn:18084/HMS/audio/Taoge-chengshilvren.mp3");
        audioPlayItem2.setOnline(1);
        audioPlayItem2.setAudioTitle("Kalimba - FLAC");
        playItemList.add(audioPlayItem2);

        return playItemList;
    }
}
