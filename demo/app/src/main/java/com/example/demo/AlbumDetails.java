package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.util.ArrayList;

import static com.example.demo.MainActivity.songInfos;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<SongInfo> albSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
//    public static JcPlayerView jcPlayerView_off_alb;
//    public static ArrayList<JcAudio> jcAudios_off_alb = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_album_details);

        recyclerView = (RecyclerView) findViewById(R.id.alb_rv);
        albumPhoto = (ImageView) findViewById(R.id.albumIcon);
//        jcPlayerView_off_alb = findViewById(R.id.jcplayer_off_alb);
        albumName = getIntent().getStringExtra("AlbumName");
        int j = 0;
        for (int i=0; i<songInfos.size(); i++)
        {
            if (albumName.equals(songInfos.get(i).getAlbum()))
            {
                albSongs.add(j, songInfos.get(i));
                j++;
            }
        }
        byte[] image = getAlbumArt(albSongs.get(0).getPath());
        if(image != null){
            Glide.with(this).asBitmap().load(image).into(albumPhoto);
        }
        else {
            Glide.with(this).load(R.drawable.comm).into(albumPhoto);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(albSongs.size() < 1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
