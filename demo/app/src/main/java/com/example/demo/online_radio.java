package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.io.IOException;

public class online_radio extends AppCompatActivity {


    int[] radioImageFirst = {R.drawable.air};
    int[] radioImageSec = { R.drawable.comm, R.drawable.city, R.drawable.mirchi, R.drawable.redfm};
   // static Button play;
    static ImageView play_btn;
    static TextView currradio;
    static MediaPlayer mediaPlayer = new MediaPlayer();
    static boolean prepared=false, started=false;

    public static String[] names = {"UNKNOWN", "RADIO CITY", "RADIO MIRCHI", "RED FM"};
    public static String[] stream = {"http://stream.radioreklama.bg:80/radio1rock128", "https://prclive4.listenon.in/Hindi", "https://streams.radio.co/s8d06d0298/listen", "http://104.238.99.1:8000/red967.mp3"};
    String[] s = {"http://stream.radioreklama.bg:80/radio1rock128"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_radio);

        //play = findViewById(R.id.radio_play_btn);
        currradio = findViewById(R.id.curr_fm);
        play_btn = findViewById(R.id.radio_play_btn);
        //play.setEnabled(false);

//
//        radio_adapter radioAdapter = new radio_adapter(radioImageFirst, s);
//        MultiSnapRecyclerView firstRV = findViewById(R.id.first_recycler_view);
//        firstRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        firstRV.setAdapter(radioAdapter);


        radio_adapter secradioAdapter = new radio_adapter(radioImageSec);
        MultiSnapRecyclerView secRV = findViewById(R.id.sec_recycler_view);
        secRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        secRV.setAdapter(secradioAdapter);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    //play.setText("PLAY");
                    play_btn.setImageResource(R.drawable.ic_play_white);

                }
                else {
                    if (mediaPlayer == null){
                        currradio.setText("NO radio selected");
                    }
                    else {
                        mediaPlayer.start();
                        //play.setText("STOP");
                        play_btn.setImageResource(R.drawable.ic_pause_white);
                    }
                }
            }
        });


    }
}
