package com.example.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.demo.Model.uploadedSongs;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class upload_songs extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView textViewImage, on_title, on_artist, on_dur, on_album, on_data;
    ImageView on_album_art;
    ProgressBar progressBar;
    StorageReference mStorageref;
    StorageTask mUploadTask;
    DatabaseReference referenceSongs;
    String songsCategory, on_title1, on_artist1, on_dur1, on_album_art1 = "", songUrl;
    MediaMetadataRetriever metadataRetriever;
    byte[] art;
    Spinner spinner;
    Uri audioUri;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_songs);
        initView();

        metadataRetriever = new MediaMetadataRetriever();
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageref = FirebaseStorage.getInstance().getReference().child("songs");

        spinner.setOnItemSelectedListener(this);

        List <String> categories = new ArrayList<>();
        categories.add("Love Songs");
        categories.add("Sad Songs");
        categories.add("Party Songs");
        categories.add("Birthday Songs");
        categories.add("God Songs");

        ArrayAdapter <String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }

    void initView(){
        textViewImage = findViewById(R.id.songFileSel);
        progressBar = findViewById(R.id.uplPB);
        on_title = findViewById(R.id.online_title);
        on_artist = findViewById(R.id.online_artist);
        on_dur = findViewById(R.id.online_dur);
        on_album = findViewById(R.id.online_album);
        on_data = findViewById(R.id.online_data);
        spinner = findViewById(R.id.spinner);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        songsCategory = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, "Selected : "+position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void openAudioFiles(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 101);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null){
            audioUri = data.getData();
            String fileName = getFileName(audioUri);
            textViewImage.setText(fileName);
            metadataRetriever.setDataSource(this, audioUri);

            //art = metadataRetriever.getEmbeddedPicture();
            //Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            //on_album_art.setImageBitmap(bitmap);

            //if (art != null){
                //Glide.with(this).asBitmap().load(art).into(on_album_art);
                //Glide.with(mContext).asBitmap().load(image).into(holder.imageView);
            //}


            on_album.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            on_artist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            //on_dur.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            s = convertDuration(Long.parseLong(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toString()));
            on_dur.setText(s);
            on_data.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            on_title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

            on_artist1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            on_title1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            on_dur1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        }

    }

    private String getFileName(Uri uri){

        String result = null;

        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null,null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }

        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut +1);
            }
        }
        return result;
    }

    public void uploadFileToFirebase(View v){

        if (textViewImage.equals("No file selected")){
            Toast.makeText(this, "please select an image...", Toast.LENGTH_SHORT).show();
        }
        else {
            if (mUploadTask != null && mUploadTask.isInProgress()){
                Toast.makeText(this, "song upload in progress", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadFiles();
            }
        }

    }

    private void uploadFiles() {

        if (audioUri != null){
            Toast.makeText(this, "uploading.. please wait!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference1 = mStorageref.child(System.currentTimeMillis()+"."+getFileExtension(audioUri));
            mUploadTask = storageReference1.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri uri = uriTask.getResult();
                    songUrl = uri.toString();
                    uploadDetailsToFB();
                    progressBar.setProgress(0);
                    /*
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadedSongs uploadedSongs = new uploadedSongs(songsCategory, on_title1, on_artist1, on_album_art1, on_dur1, uri.toString());
                            String uploadID = referenceSongs.push().getKey();
                            referenceSongs.child(uploadID).setValue(uploadedSongs);
                            Toast.makeText(upload_songs.this, "UPLOADED!!!", Toast.LENGTH_LONG).show();

                        }
                    });*/
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(upload_songs.this, "error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "No file selected ...", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadDetailsToFB() {

        uploadedSongs upl = new uploadedSongs(songsCategory, on_title1, on_artist1, on_album_art1, on_dur1, songUrl);
        FirebaseDatabase.getInstance().getReference("songs").push().setValue(upl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(upload_songs.this, "uploaded to database as well ....", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(upload_songs.this, "FAILED to upload in database "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

    public void openAlbumUploadActivity(View v){
        Intent intent = new Intent(upload_songs.this, uploadAlbum.class);
        startActivity(intent);
    }


    public static String convertDuration(long duration){
        long min = (duration/1000)/60;
        long sec = (duration/1000)%60;
        String converted = String.format("%d:%02d",min,sec);
        return converted;
    }


//    private byte[] getAlbumArt(String uri){
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(uri);
//        byte[] art = retriever.getEmbeddedPicture();
//        retriever.release();
//        return art;
//    }
}
