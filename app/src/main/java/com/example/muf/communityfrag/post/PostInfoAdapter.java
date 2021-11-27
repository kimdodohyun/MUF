package com.example.muf.communityfrag.post;

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
    private ArrayList<PostFireBase> arrayList;
    private Context context;
    public interface OnItemClickEventListener{ void onItemClick(View view, int pos);}
    public interface OnImageClickEventListener{void onItemClick(int pos);}
    private OnItemClickEventListener itemClickEventListener;
    private OnImageClickEventListener imageClickEventListener;

    public PostInfoAdapter(ArrayList<PostFireBase> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public PostInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        PostInfoViewHolder holder = new PostInfoViewHolder(view,itemClickEventListener, imageClickEventListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostInfoViewHolder holder, int position) {
        if(arrayList.get(position).getProfileimg() != null) {
            Glide.with(holder.itemView)
                    .load(arrayList.get(position).getProfileimg()) //ProfileImage
                    .into(holder.iv_profile);
        }
        holder.tv_usernickname.setText(arrayList.get(position).getUsername()); //UserName
        holder.tv_albumtitle.setText(arrayList.get(position).getAlbumtitle()); //AlbumTitle
        holder.tv_artist.setText(arrayList.get(position).getArtis()); //Artist
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getAlbumimg())   //AlbumImage
                .into(holder.album_imag);
        holder.tv_inputtext.setText(arrayList.get(position).getInputtext()); //UserName
    }

    public void setOnItemClickListener(OnItemClickEventListener listener){
        itemClickEventListener = listener;
    }
    public void setOnImageClickListener(OnImageClickEventListener ImageListener){
        imageClickEventListener = ImageListener;
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class PostInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_usernickname;
        TextView tv_albumtitle;
        TextView tv_artist;
        ImageView album_imag;
        TextView tv_inputtext;

        public PostInfoViewHolder(@NonNull View itemView
                , final OnItemClickEventListener listener
                , final OnImageClickEventListener iv_listener) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.publisher_profile_picture);
            this.tv_usernickname = itemView.findViewById(R.id.publisher_nickname);
            this.tv_albumtitle = itemView.findViewById(R.id.albumtitle);
            this.tv_artist = itemView.findViewById(R.id.artistname);
            this.album_imag = itemView.findViewById(R.id.album_image);
            this.tv_inputtext = itemView.findViewById(R.id.inputtext);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        listener.onItemClick(view, pos);
                    }
                }
            });
            iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        iv_listener.onItemClick(pos);
                    }
                }
            });
        }
    }
}
