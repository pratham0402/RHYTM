package com.example.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class songsCategoryImg extends AppCompatActivity {


    private RecyclerView recyclerView;
    private categoryAdapter categoryAdapter;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;
    StorageReference mStorage;
    private List<categoryInfo> categoryInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_category_img);

        recyclerView = findViewById(R.id.online_cate_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        progressDialog = new ProgressDialog(this);
        categoryInfoList = new ArrayList<>();
        progressDialog.setMessage("please wait .... ");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();

                for (DataSnapshot DSS : snapshot.getChildren()){

                    categoryInfo ci = DSS.getValue(categoryInfo.class);
                    categoryInfoList.add(ci);
                }
                categoryAdapter = new categoryAdapter(getApplicationContext(), categoryInfoList);
                recyclerView.setAdapter(categoryAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upl_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.uplOpt:
                startActivity(new Intent(this, upload_songs.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
