package com.example.demo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;

import static com.example.demo.AlbumDetailsAdapter.albumSongs;
import static com.example.demo.ApplicationClass.ACTION_NEXT;
import static com.example.demo.ApplicationClass.ACTION_PLAY;
import static com.example.demo.ApplicationClass.ACTION_PREVIOUS;
import static com.example.demo.ApplicationClass.CHANNEL_ID_2;
import static com.example.demo.MainActivity.repeatBool;
import static com.example.demo.MainActivity.shuffleBool;
import static com.example.demo.MainActivity.songInfos;
import static com.example.demo.SongAdapter.mSongs;

public class Player extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    TextView artist, song, cur_dur, final_dur;
    ImageView cover_art, shuffle_btn, next_btn, prev_btn, repeat_btn, back_btn;
    FloatingActionButton play_btn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<SongInfo> listSongs = new ArrayList<>();
    static Uri uri;
    //static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        initView();
        getIntendMethod();
        /*song.setText(listSongs.get(position).getTitle());
        artist.setText(listSongs.get(position).getArtist());
        musicService.onCompleted();*/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(musicService != null && fromUser){
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Player.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (musicService != null){
                    int mCurrentPosition = musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    cur_dur.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBool)
                {
                    shuffleBool = false;
                    shuffle_btn.setImageResource(R.drawable.ic_shuffle);
                }
                else{
                    shuffleBool = true;
                    shuffle_btn.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBool)
                {
                    repeatBool = false;
                    repeat_btn.setImageResource(R.drawable.ic_repeat);
                }
                else{
                    repeatBool = true;
                    repeat_btn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadbtn();
        nextThreadbtn();
        prevThreadbtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    // get random position
    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    // WHEN PREVIOUS BUTTON IS PRESSED -------------------------------------------------------------
    private void prevThreadbtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevbtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevbtnClicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song.setText(listSongs.get(position).getTitle());
            artist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            Player.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle_outline_black_24dp);
            play_btn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            musicService.start();
        }
        else
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song.setText(listSongs.get(position).getTitle());
            artist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            Player.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!= null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle_outline_black_24dp);
            play_btn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        }
    }

    // AFTER NEXT BUTTON IS PRESSED ----------------------------------------------------------------
    private void nextThreadbtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextbtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextbtnClicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position + 1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
           musicService.createMediaPlayer(position);
            metaData(uri);
            song.setText(listSongs.get(position).getTitle());
            artist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            Player.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService!= null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle_outline_black_24dp);
            play_btn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            musicService.start();
        }
        else
        {
            musicService.stop();
            musicService.release();
            if (shuffleBool && !repeatBool)
            {
                position = getRandom(listSongs.size()-1);
            }
            else if (!shuffleBool && !repeatBool)
            {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song.setText(listSongs.get(position).getTitle());
            artist.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            Player.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause_circle_outline_black_24dp);
            play_btn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
        }
    }

    // AFTER PLAY BUTTON IS PRESSED ----------------------------------------------------------------
    private void playThreadbtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                play_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playbtnClicked();
                    }
                });
            }
        };
        playThread.start();

    }

    public void playbtnClicked() {
        if(musicService.isPlaying())
        {
            play_btn.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
            musicService.showNotification(R.drawable.ic_play_circle_outline_black_24dp);
            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            Player.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
        else
        {
            play_btn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            musicService.showNotification(R.drawable.ic_pause_circle_outline_black_24dp);
            musicService.start();
            seekBar.setMax(musicService.getDuration());
            Player.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null){
                        int mCurrentPosition = musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    // ---------------------------------------------------------------------------------------------
    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String second = String.valueOf(mCurrentPosition % 60);
        String minute = String.valueOf(mCurrentPosition / 60);
        totalout = minute + ":" + second;
        totalNew = minute + ":" + "0" + second;
        if (second.length() == 1){
            return totalNew;
        }
        else {
            return totalout;
        }
    }

    private void getIntendMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");

        if(sender != null && sender.equals("albumDetails"))
        {
            listSongs = albumSongs;
        }
        else {
            listSongs = mSongs;
        }
        if(listSongs != null){
            play_btn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        /*if(musicService != null){
            musicService.stop();
            musicService.release();
            musicService.createMediaPlayer(positon);
            musicService.start();
        }
        else {
            musicService.createMediaPlayer(positon);
            musicService.start();
        }*/
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);
        /*seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);*/
    }

    private void initView() {
        artist = (TextView) findViewById(R.id.song_artist);
        song = (TextView) findViewById(R.id.song_name);
        cur_dur = (TextView) findViewById(R.id.curr_dur);
        final_dur = (TextView) findViewById(R.id.final_dur);
        shuffle_btn = (ImageView) findViewById(R.id.btn_shuffle);
        next_btn = (ImageView) findViewById(R.id.btn_next);
        prev_btn = (ImageView) findViewById(R.id.btn_prev);
        repeat_btn = (ImageView) findViewById(R.id.btn_repeat);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        cover_art = (ImageView) findViewById(R.id.cover_art);
        play_btn = (FloatingActionButton) findViewById(R.id.play_pause_btn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int duration = Integer.parseInt(songInfos.get(position).getDuration()) / 1000;
        final_dur.setText(formattedTime(duration));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art != null){
            Glide.with(this).asBitmap().load(art).into(cover_art);

        }
        else {
            Glide.with(this).asBitmap().load(R.drawable.comm).into(cover_art);
        }
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation aniOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation aniIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        //Toast.makeText(this, "Connected "+musicService, Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song.setText(listSongs.get(position).getTitle());
        artist.setText(listSongs.get(position).getArtist());
        musicService.onCompleted();
        musicService.showNotification(R.drawable.ic_pause_circle_outline_black_24dp);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }


    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
