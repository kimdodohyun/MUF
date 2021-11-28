package com.example.muf.friend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.R;
import com.example.muf.Streaming.StreamingRecyclerAdapter;
import com.example.muf.homeActivity;
import com.example.muf.model.UserModel;
import com.squareup.picasso.Picasso;

import java.security.Provider;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class RecommenFriendAdapter extends RecyclerView.Adapter<RecommenFriendAdapter.recommendViewHolder> {
    private ArrayList<UserModel> arrayList;
    private Context context;

    public RecommenFriendAdapter(ArrayList<UserModel> ArrayList, Context Context){
        this.arrayList = ArrayList;
        this.context = Context;;
    }
    @NonNull
    @Override
    public recommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recommend_friend, parent, false);
        return new recommendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull recommendViewHolder holder, int position) {
        Log.d("test123", "onBindViewHolder: " + arrayList.get(position).getUid());
        holder.tv_nickName.setText(arrayList.get(position).getNickName());
        String musicUri = arrayList.get(position).getProfileMusicUri();
        if(musicUri != null){
            String parse[] = musicUri.split(":");
            SpotifyApi spotifyApi;
            spotifyApi = new SpotifyApi();
            spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);
            Track track = spotifyApi.getService().getTrack(parse[2]);
            String img_url = track.album.images.get(0).url;
            Glide.with(holder.itemView)
                    .load(img_url)
                    .into(holder.iv_profileMusicImg);

        }
    }

    public void updateList(ArrayList<UserModel> newlist){
        arrayList.clear();
        arrayList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return arrayList.size(); }

    public class recommendViewHolder extends RecyclerView.ViewHolder {
        TextView tv_nickName;
        ImageView iv_profileMusicImg;

        public recommendViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_nickName = (TextView) itemView.findViewById(R.id.userNickNameInHomeFrag);
            iv_profileMusicImg = (ImageView) itemView.findViewById(R.id.userProfileMusicImgInHomeFrag);
        }
    }
}
