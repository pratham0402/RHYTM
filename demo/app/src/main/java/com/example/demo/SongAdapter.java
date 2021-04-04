package com.example.demo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.INotificationSideChannel;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

//import static com.example.demo.SongsFragment.jcAudios_off;
//import static com.example.demo.SongsFragment.jcPlayerView_off;
import static com.example.demo.online_songs.jcPlayerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    private Context mContext;
    static ArrayList<SongInfo> mSongs;
    SongInfo temp;

    SongAdapter(Context mContext, ArrayList<SongInfo> mSongs){
        this.mContext = mContext;
        this.mSongs = mSongs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false); // attaching with music_item layout
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(mSongs.get(position).getTitle());
        byte[] image = getAlbumArt(mSongs.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(holder.imageView);
//            InputStream is = new ByteArrayInputStream(image);
//            Bitmap bm = BitmapFactory.decodeStream(is);
//            holder.imageView.setImageBitmap(bm);
        }
        else {
            Glide.with(mContext).load(R.drawable.comm).into(holder.imageView);
        }
        //Glide.with(mContext).load(R.drawable.comm).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Player.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
                //jcPlayerView.kill();
//                jcPlayerView_off.playAudio(jcAudios_off.get(position));
//                jcPlayerView_off.setVisibility(View.VISIBLE);
//                jcPlayerView_off.createNotification();
            }
        });
        holder.menu_opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.delete:
                                deleteItem(position, v);
                                break;

                            case R.id.share:
                                File file = new File(mSongs.get(position).getPath());
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("application/vnd.android.package-archive");
                                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                mContext.startActivity(Intent.createChooser(intent,"Share Via"));
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView, menu_opt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.song_text);
            imageView = itemView.findViewById(R.id.song_img);
            menu_opt = itemView.findViewById(R.id.menu_opt);
        }
    }

    // deleting the song
    private void deleteItem(final int position, View view){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mSongs.get(position).getId()));
        //path, title, album, artist, duration, id
        File file = new File(mSongs.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted)
        {
            mContext.getContentResolver().delete(contentUri, null,null);
            mSongs.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mSongs.size());
            Snackbar.make(view, "Song deleted", Snackbar.LENGTH_LONG).show();
        }
        else {
            Snackbar.make(view, "Song can't be deleted", Snackbar.LENGTH_LONG).show();
        }
    }

    // share

    // getting album art
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

    void updateList(ArrayList<SongInfo> searchList)
    {
        mSongs = new ArrayList<>();
        mSongs.addAll(searchList);
        notifyDataSetChanged();
    }

}
