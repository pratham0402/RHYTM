package com.example.demo;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.util.ArrayList;

import static com.example.demo.MainActivity.songInfos;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    static SongAdapter songAdapter;
//    public static JcPlayerView jcPlayerView_off;
//    public static ArrayList<JcAudio> jcAudios_off = new ArrayList<>();

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

//        jcPlayerView_off = view.findViewById(R.id.jcplayer_off);
//        for (int i=0; i<songInfos.size(); i++){
//            jcAudios_off.add(JcAudio.createFromURL(songInfos.get(i).getTitle(), songInfos.get(i).getPath()));
//        }
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        if(!(songInfos.size() < 1)){
            songAdapter = new SongAdapter(getContext(), songInfos);
            recyclerView.setAdapter(songAdapter);
//            jcPlayerView_off.initPlaylist(jcAudios_off, null);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }

        return view;
    }

}
