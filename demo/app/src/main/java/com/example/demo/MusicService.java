package com.example.demo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.demo.ApplicationClass.ACTION_NEXT;
import static com.example.demo.ApplicationClass.ACTION_PLAY;
import static com.example.demo.ApplicationClass.ACTION_PREVIOUS;
import static com.example.demo.ApplicationClass.CHANNEL_ID_2;
import static com.example.demo.Player.listSongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<SongInfo> songInfos = new ArrayList<>();
    Uri uri ;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("ActionName");
        int myPosition = intent.getIntExtra("servicePosition", -1);
        if (myPosition != -1){
            playMedia(myPosition);
        }
        if (actionName != null){
            switch (actionName)
            {
                case "playPause":
                    Toast.makeText(this, "playPause", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null)
                    {
                        Log.e("Inside", "Action");
                        actionPlaying.playbtnClicked();
                    }
                    break;

                case "previous":
                    Toast.makeText(this, "PREVIOUS", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null)
                    {
                        Log.e("Inside", "Action");
                        actionPlaying.prevbtnClicked();
                    }
                    break;

                case "next":
                    Toast.makeText(this, "NEXT", Toast.LENGTH_SHORT).show();
                    if (actionPlaying != null)
                    {
                        Log.e("Inside", "Action");
                        actionPlaying.nextbtnClicked();
                    }
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int Startposition)
    {
        songInfos = listSongs;
        position = Startposition;
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (songInfos != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else
        {

            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start(){
        mediaPlayer.start();
    }

    boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    void stop(){
        mediaPlayer.stop();
    }

    void release(){
        mediaPlayer.release();
    }

    int getDuration(){
        return mediaPlayer.getDuration();
    }

    void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    void createMediaPlayer(int positionInner){
        position = positionInner;
        uri = Uri.parse(songInfos.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    void pause(){
        mediaPlayer.pause();
    }

    void onCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.nextbtnClicked();
            if(mp != null){
                createMediaPlayer(position);
                mp.start();
                onCompleted();
            }
        }

    }

    void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }

    void showNotification(int playPauseBtn){
        Intent intent = new Intent(this, Player.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        picture = getAlbumArt(songInfos.get(position).getPath());
        Bitmap thumb;
        if (picture != null)
        {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else
        {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.comm);
        }

        //creating notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(thumb)
                .setContentTitle(songInfos.get(position).getTitle())
                .setContentText(songInfos.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_skip_next_black_24dp, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

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
