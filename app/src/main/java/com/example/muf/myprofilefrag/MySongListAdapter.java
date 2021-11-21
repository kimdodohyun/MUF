package com.example.muf.myprofilefrag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.R;
import com.example.muf.model.Music;

import java.util.ArrayList;

public class MySongListAdapter extends RecyclerView.Adapter<MySongListAdapter.MyPostInfoViewHolder>{
    private ArrayList<Music> arrayList;
    private Context context;

    public MySongListAdapter(ArrayList<Music> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_music, parent, false);
        MyPostInfoViewHolder holder = new MyPostInfoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostInfoViewHolder holder, int position) {
        if(arrayList.get(position).getImg_uri() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getImg_uri()) //앨범 이미지 그리기
                    .into(holder.iv_albumImage);
        }
        holder.tv_musicTitle.setText(arrayList.get(position).getTitle()); //노래 제목
        holder.tv_artistName.setText(arrayList.get(position).getArtist_name()); //artist name
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class MyPostInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_albumImage;
        TextView tv_musicTitle;
        TextView tv_artistName;

        public MyPostInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_albumImage = itemView.findViewById(R.id.entity_image);
            this.tv_musicTitle = itemView.findViewById(R.id.entity_title);
            this.tv_artistName = itemView.findViewById(R.id.entity_subtitle);
        }
    }
}
