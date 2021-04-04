package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class online_home extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    TextView music_text, sub_text;
    ImageView musicBtn, radioBtn;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_home);
        firebaseAuth = FirebaseAuth.getInstance();
        music_text = findViewById(R.id.music_player_text);
        musicBtn = findViewById(R.id.player_icon);
        sub_text = findViewById(R.id.sub_text);
        radioBtn = findViewById(R.id.sub_icon);

        //checkNetworkState(this);

        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(online_home.this, songsCategoryImg.class));
                startActivity(new Intent(online_home.this, online_songs.class));
                //startActivity(new Intent(online_home.this, upload_songs.class));
            }
        });

        radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(online_home.this, online_radio.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // menu switch
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(online_home.this, login.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void checkNetworkState(Context c){
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (!(networkInfo.isConnected() && networkInfo != null)){
            Toast.makeText(this, "You are not connected to INTERNET ..... ", Toast.LENGTH_SHORT).show();
        }
    }

}
