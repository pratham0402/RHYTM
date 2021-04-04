package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.online_songAdapter.onlineSongInfos;


public class online_songs extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private online_songAdapter songAdapter;
    ProgressBar progressBar;
    boolean checkIn = false;
    DatabaseReference mDatabase;
    StorageReference mStorage;
    ArrayList<online_SongInfo> onlineSongInfoList;
    ValueEventListener valueEventListener;
    public static JcPlayerView jcPlayerView;
    public static ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_songs);

        /*

        ApiInterface service = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        Call<List<online_SongInfo>> call = service.getStudio();

        call.enqueue(new Callback<List<online_SongInfo>>() {

            @Override
            public void onResponse(Call<List<online_SongInfo>> call, Response<List<online_SongInfo>> response) {
                localDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<online_SongInfo>> call, Throwable t) {
                Toast.makeText(online_songs.this, "ERROR : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void localDataList(List<online_SongInfo> online_songs) {

        recyclerView = findViewById(R.id.online_recycle);
        songAdapter = new online_songAdapter(online_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(online_songs.this));//*********
        recyclerView.setAdapter(songAdapter);

    }*/


        recyclerView = findViewById(R.id.online_recycle);
        progressBar = findViewById(R.id.songPB);
        jcPlayerView = findViewById(R.id.jcplayer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onlineSongInfoList = new ArrayList<>();
        songAdapter = new online_songAdapter(this, onlineSongInfoList); //********

        mDatabase = FirebaseDatabase.getInstance().getReference("songs");
        valueEventListener = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onlineSongInfoList.clear();

                for (DataSnapshot dss : snapshot.getChildren()){
                    online_SongInfo getSongs = dss.getValue(online_SongInfo.class);
                    getSongs.setMkey(dss.getKey());
                    onlineSongInfoList.add(getSongs);
                    if(onlineSongInfos != null){
                        onlineSongInfoList = onlineSongInfos;
                    }
                    jcAudios.add(JcAudio.createFromURL(getSongs.getSongTitle(), getSongs.getSongLink()));

                }
                recyclerView.setAdapter(songAdapter);
                jcPlayerView.initPlaylist(jcAudios, null);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upl_menu, menu);
        MenuItem item = menu.findItem(R.id.search_opt_online);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.uplOpt:
                startActivity(new Intent(this, upload_songs.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeSelectedSong(int index){
        songAdapter.notifyItemChanged(songAdapter.getSelectedPosi());
        currentIndex = index;
        songAdapter.setSelectedPosi(currentIndex);
        songAdapter.notifyItemChanged(currentIndex);
    }

    // search function
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<online_SongInfo> all_song = new ArrayList<>();
        for(online_SongInfo song : onlineSongInfoList)
        {
            if (song.getSongTitle().toLowerCase().contains(input))
            {
                all_song.add(song);
            }
        }
        songAdapter.updateList(all_song);
        return true;
    }
}
