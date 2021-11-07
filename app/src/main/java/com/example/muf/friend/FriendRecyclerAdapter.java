package com.example.muf.friend;

import android.media.Image;
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
import com.squareup.picasso.Picasso;

import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {
    private ArrayList<UserModel> friendList;

    @NonNull
    @Override
    public FriendRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_friend, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(friendList.get(position));
    }

    public void setFriendList(ArrayList<UserModel> list){
        this.friendList = list;
        notifyDataSetChanged();;
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name;
        TextView profileMusic;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            profile = (ImageView) itemView.findViewById(R.id.profile);
            name = (TextView) itemView.findViewById(R.id.name);
            profileMusic = (TextView) itemView.findViewById(R.id.message);
        }

        void onBind(UserModel item){
            name.setText(item.getNickName());
            if(item.getProfileImageUrl() != null){
                Picasso.get().load(item.getProfileImageUrl()).into(profile);
            }
            if(item.getProfileMusicUrl()!=null) {
                SpotifyApi spotifyApi = new SpotifyApi();
                spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);
                Track track = spotifyApi.getService().getTrack(item.getProfileMusicUrl());
                profileMusic.setText(track.artists.get(0).name + " - " + track.name);
            }

        }
    }
}
