package com.example.demo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.demo.online_radio.currradio;
import static com.example.demo.online_radio.mediaPlayer;
import static com.example.demo.online_radio.names;
//import static com.example.demo.online_radio.play;
import static com.example.demo.online_radio.play_btn;
import static com.example.demo.online_radio.prepared;
import static com.example.demo.online_radio.started;
import static com.example.demo.online_radio.stream;

public class radio_adapter extends RecyclerView.Adapter<radio_adapter.ViewHolder> {

    int[] images;

    public radio_adapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        int image = images[position];
        holder.radioImg.setImageResource(image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){

                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(stream[position]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    currradio.setText(names[position]);

                }
                else{
                    try {
                        mediaPlayer.setDataSource(stream[position]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //play.setText("STOP");
                    play_btn.setImageResource(R.drawable.ic_pause_white);
                    mediaPlayer.start();
                    currradio.setText(names[position]);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView radioImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioImg = itemView.findViewById(R.id.radio_image_view);
        }
    }



}
