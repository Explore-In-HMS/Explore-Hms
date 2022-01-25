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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.genar.hmssandbox.huawei.feature_audiokit.databinding.ActivityLocalAudioBinding;
import com.huawei.hms.api.bean.HwAudioPlayItem;

import java.util.ArrayList;
import java.util.List;

public class LocalAudioActivity extends AppCompatActivity implements CreatedPlaylistAdapter.OnItemClickListener {

    ActivityLocalAudioBinding binding;
    String isLocal;
    List<HwAudioPlayItem> playList;
    private static  final String TYPE_OF_PLAYLIST = "playlistType";
    int isFirstTime;
    List<Integer> myCreatedIndexList = new ArrayList<>();
    List<LocalAudioItem> localAudioList = new ArrayList<>();
    CreatedPlaylistAdapter createdPlaylistAdapter;
    String tmpName = "";
    CurrentPlaylistAdapter currentPlaylistAdapter;
    List<Integer> indexlist;
    int prePos = 0;
    PlaylistCreator playlistCreator = new PlaylistCreator(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocalAudioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupToolbar();

        isLocal = (getIntent().getExtras() != null) ? getIntent().getExtras().getString(TYPE_OF_PLAYLIST) : "not found";

        if(isLocal != null && isLocal.equals("online")){
            playList = playlistCreator.getOnlinePlaylist();
        }
        else{
            playList = playlistCreator.getLocalPlayList();
        }

        if(playList.size() == 0){
            Toast.makeText(this, "You have no audio file in your device. Try again later.", Toast.LENGTH_SHORT).show();
        }
        else{
            currentPlaylistAdapter = new CurrentPlaylistAdapter(this, playList, true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LocalAudioActivity.this);
            binding.songRecyclerView.setLayoutManager(layoutManager);
            RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(LocalAudioActivity.this);
            binding.playlistsRecyclerView.setLayoutManager(layoutManager2);
            binding.songRecyclerView.setAdapter(currentPlaylistAdapter);

            isFirstTime = 0;

            binding.createPlaylistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myCreatedIndexList = currentPlaylistAdapter.getMyCreatedIndexList();
                    List<Integer> myTmpList = new ArrayList<>(myCreatedIndexList); //IF YOU REMOVE THIS
                    // AND ALL LOCALAUDIOITEM INSTANCES WILL HAVE THE SAME INDEX LIST

                    if(!myTmpList.isEmpty()){
                        if(isFirstTime == 0){
                            LocalAudioItem myLocalAudioItem = new LocalAudioItem("Playlist " + ++isFirstTime, myTmpList);
                            localAudioList.add(myLocalAudioItem);
                            createdPlaylistAdapter = new CreatedPlaylistAdapter(localAudioList, myCreatedIndexList);
                            createdPlaylistAdapter.setOnItemClickListener(LocalAudioActivity.this);
                            binding.playlistsRecyclerView.setAdapter(createdPlaylistAdapter);
                        }
                        else{
                            LocalAudioItem myLocalAudioItem = new LocalAudioItem("Playlist " + ++isFirstTime, myTmpList);
                            localAudioList.add(myLocalAudioItem);
                            createdPlaylistAdapter.notifyItemInserted(localAudioList.size()-1);
                        }
                    }
                    else{
                        Toast.makeText(LocalAudioActivity.this, "Please add some audio files to your playlist", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public void onItemClick(int position) {
        prePos = position; //this is to remember which of the playlists the user has clicked (used below)
        Intent i = new Intent(LocalAudioActivity.this, AudioActivity.class);
        if(isLocal.equals("local"))
            i.putExtra(TYPE_OF_PLAYLIST, "local");
        else
            i.putExtra(TYPE_OF_PLAYLIST, "online");
        i.putIntegerArrayListExtra("playlist", (ArrayList<Integer>) localAudioList.get(position).getIndexList());
        i.putExtra("nameOfPlaylist", localAudioList.get(position).getNameOfPlaylist());
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //this is where the subsequent activity sends back information about playlists
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                indexlist = data.getIntegerArrayListExtra("sizeOfPlaylist"); //get the modified playlist
                if (indexlist != null) {
                    if(!indexlist.isEmpty()){
                        //if some songs remain, get the corresponding @LocalAudioItem and update the indexlist of it
                        LocalAudioItem newTmpItem = localAudioList.get(prePos);
                        newTmpItem.setIndexList(indexlist);
                        createdPlaylistAdapter.notifyItemChanged(prePos);
                    }
                    else{
                        //if the user removed all the playlists, remove the list from here too
                        localAudioList.remove(prePos);
                        createdPlaylistAdapter.notifyItemRemoved(prePos);
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(this, "There is an error in updating the playlists", Toast.LENGTH_SHORT).show();
            }
        }
    }
}