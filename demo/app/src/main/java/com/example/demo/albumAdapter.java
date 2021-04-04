package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

public class albumAdapter extends RecyclerView.Adapter<albumAdapter.MyHolder> {


    private Context mContext;
    private ArrayList<SongInfo> albumSongs;
    View view;

    public albumAdapter(Context mContext, ArrayList<SongInfo> albumSongs) {
        this.mContext = mContext;
        this.albumSongs = albumSongs;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        holder.album_text.setText(albumSongs.get(position).getAlbum());
        byte[] image = getAlbumArt(albumSongs.get(position).getPath());
        // set cover art
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_img);
        }
        else {
            Glide.with(mContext).load(R.drawable.comm).into(holder.album_img);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AlbumDetails.class);
                intent.putExtra("AlbumName", albumSongs.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumSongs.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_img;
        TextView album_text;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_img = itemView.findViewById(R.id.album_img);
            album_text = itemView.findViewById(R.id.album_text);
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (Build.VERSION.SDK_INT >= 14){
            try {
                retriever.setDataSource(uri, new HashMap<String, String>());
            } catch (RuntimeException ex) {
                // something went wrong with the file, ignore it and continue
            }
        }
        else {
            retriever.setDataSource(uri);
        }
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
