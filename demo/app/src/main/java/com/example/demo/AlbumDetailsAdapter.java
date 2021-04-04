package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {


    private Context mContext;
    static ArrayList<SongInfo> albumSongs;
    View view;

    public AlbumDetailsAdapter(Context mContext, ArrayList<SongInfo> albumSongs) {
        this.mContext = mContext;
        this.albumSongs = albumSongs;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.album_song_text.setText(albumSongs.get(position).getTitle());
        byte[] image = getAlbumArt(albumSongs.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_song_img);
        }
        else {
            Glide.with(mContext).load(R.drawable.comm).into(holder.album_song_img);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Player.class);
                intent.putExtra("sender", "albumDetails");
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumSongs.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_song_img;
        TextView album_song_text;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_song_img = itemView.findViewById(R.id.song_img);
            album_song_text = itemView.findViewById(R.id.song_text);
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
