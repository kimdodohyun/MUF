package com.example.muf.post;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.R;

import java.util.ArrayList;

public class PostInfoAdapter extends RecyclerView.Adapter<PostInfoAdapter.PostInfoViewHolder> {
    private ArrayList<Contents> arrayList;
    private Context context;

    public PostInfoAdapter(ArrayList<Contents> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        PostInfoViewHolder holder = new PostInfoViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostInfoViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile_pic())
                .into(holder.iv_profile);
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getAlbum_image())
                .into(holder.album_imag);
        holder.tv_usernickname.setText(arrayList.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class PostInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        ImageView album_imag;
        TextView tv_usernickname;

        public PostInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.publisher_profile_picture);
            this.album_imag = itemView.findViewById(R.id.album_image);
            this.tv_usernickname = itemView.findViewById(R.id.publisher_nickname);

        }
    }
}
