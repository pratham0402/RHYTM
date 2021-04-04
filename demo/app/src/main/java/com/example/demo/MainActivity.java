package com.example.demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int REQUEST_CODE = 1;
    static ArrayList<SongInfo> songInfos; // all songs
    static boolean shuffleBool = false, repeatBool = false;
    static ArrayList<SongInfo> albums = new ArrayList<>(); // album songs
    private String my_sort_pref = "SortOrder";
    private String my_theme = "Theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    // check if premission is granted to access internal storage
    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else{
            songInfos = getAllAudio(this);
            initViewPager();
        }
    }

    // request for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                songInfos = getAllAudio(this);
                initViewPager();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }

    }

    // initailizing all instances
    private void initViewPager(){
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Album");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    // fragment view
    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    // getting all songs present in fragment
    public ArrayList<SongInfo> getAllAudio(Context context)
    {
        SharedPreferences preferences = getSharedPreferences(my_sort_pref, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");
        String order = null;
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<SongInfo> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder)
        {
            case "sortByName":
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "sortBySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;

            case "sortByDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;

        }
        String[] projection = {MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, order);
        if(cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(2);
                String artist = cursor.getString(0);
                String path = cursor.getString(3);
                String title = cursor.getString(1);
                String duration = cursor.getString(4);
                String id = cursor.getString(5);

                SongInfo songInfo = new SongInfo(path, title, album, artist, duration, id);
                Log.e("path : "+path, "album : "+album);
                tempAudioList.add(songInfo);
                if (!duplicate.contains(album))
                {
                    albums.add(songInfo);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioList;
    }

    // search option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu); //get search menu layout
        MenuItem item = menu.findItem(R.id.search_opt);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    // search function
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<SongInfo> all_song = new ArrayList<>();
        for(SongInfo song : songInfos)
        {
            if (song.getTitle().toLowerCase().contains(input))
            {
                all_song.add(song);
            }
        }
        SongsFragment.songAdapter.updateList(all_song);
        return true;
    }

    // -----------------


    // sort selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(my_sort_pref, MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
            case R.id.sort_by_name:
                editor.putString("sorting", "sortByName");
                editor.apply();
                this.recreate();
                break;

            case R.id.sort_by_size:
                editor.putString("sorting", "sortBySize");
                editor.apply();
                this.recreate();
                break;

            case R.id.sort_by_date:
                editor.putString("sorting", "sortByDate");
                editor.apply();
                this.recreate();
                break;

            case R.id.online:
                startActivity(new Intent(MainActivity.this, login.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
