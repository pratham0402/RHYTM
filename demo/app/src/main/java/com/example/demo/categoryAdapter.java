package com.example.demo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.MyViewHolder>{

    private Context mContext;
    private List<categoryInfo> categoryInfos;

    public categoryAdapter(Context mContext, List<categoryInfo> categoryInfos) {
        this.mContext = mContext;
        this.categoryInfos = categoryInfos;
    }

    @NonNull
    @Override
    public categoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull categoryAdapter.MyViewHolder holder, int position) {
        final categoryInfo ci = categoryInfos.get(position);
        holder.ctitle.setText(ci.getcNAME());
        Glide.with(mContext).load(ci.getcURL()).into(holder.cImage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, online_songs.class);
                intent.putExtra("songCategory", ci.getSongCategory());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ctitle;
        ImageView cImage;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ctitle = itemView.findViewById(R.id.cate_img);
            cImage = itemView.findViewById(R.id.cate_title);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
