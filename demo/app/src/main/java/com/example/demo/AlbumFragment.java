package com.example.demo;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.demo.MainActivity.albums;
import static com.example.demo.MainActivity.songInfos;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    albumAdapter albAdpt;

    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.fa_rv);
        recyclerView.setHasFixedSize(true);
        if(!(albums.size() < 1)){
            albAdpt = new albumAdapter(getContext(), albums);
            recyclerView.setAdapter(albAdpt);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }

}
