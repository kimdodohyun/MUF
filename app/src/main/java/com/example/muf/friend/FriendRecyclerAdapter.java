package com.example.muf.friend;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.R;
import com.example.muf.homeActivity;
import com.example.muf.model.UserModel;
import com.example.muf.post.PostInfoAdapter;
import com.squareup.picasso.Picasso;

import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> implements OnItemClickEventListener {
    private ArrayList<UserModel> friendList;
    private Context context;
    OnItemClickEventListener listener;

    public FriendRecyclerAdapter(ArrayList<UserModel> friendList, Context context){
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friend, parent,false);

        return new ViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(friendList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void setOnItemClickListener(OnItemClickEventListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int pos) {
        if(listener != null){
            listener.onItemClick(holder,view, pos);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name;
        TextView profileMusic;

        public ViewHolder(@NonNull View itemView, final OnItemClickEventListener listener){
            super(itemView);

            profile = (ImageView) itemView.findViewById(R.id.profile);
            name = (TextView) itemView.findViewById(R.id.name);
            profileMusic = (TextView) itemView.findViewById(R.id.message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, pos);
                    }
                }
            });
        }

        void onBind(UserModel item){
            name.setText(item.getNickName());
            if(item.getProfileImageUrl() != null){
                Picasso.get().load(item.getProfileImageUrl()).into(profile);
            }
            if(item.getProfileMusicUrl()!=null) {
                SpotifyApi spotifyApi = new SpotifyApi();
                spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);
                String[]  parse = item.getProfileMusicUrl().split(":");
                Track track = spotifyApi.getService().getTrack(parse[2]);
                profileMusic.setText(track.artists.get(0).name + " - " + track.name);
                profileMusic.setSelected(true);
            }

        }
    }

    public UserModel getItem(int pos){
        return friendList.get(pos);
    }
}
