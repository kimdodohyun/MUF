package com.example.muf.Streaming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.R;

import java.util.ArrayList;


public class StreamingRecyclerAdapter extends RecyclerView.Adapter<StreamingRecyclerAdapter.ViewHolder> implements OnItemClickEventListener {
    private ArrayList<Stream> streamList;
    private Context context;
    OnItemClickEventListener listener;

    public StreamingRecyclerAdapter(ArrayList<Stream> streamList, Context context){
        this.streamList = streamList;
        this.context = context;
    }

    @NonNull
    @Override
    public StreamingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_streaming, parent, false);

        return new ViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamingRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(streamList.get(position));
    }

    @Override
    public int getItemCount() {
        return streamList.size();
    }

    public void setOnItemClickListener(OnItemClickEventListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(StreamingRecyclerAdapter.ViewHolder holder, View view, int pos) {
        if(listener != null){
            listener.onItemClick(holder,view,pos);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView stream_img;
        TextView stream_artist;
        TextView stream_track;

        public ViewHolder(@NonNull View itemView, final OnItemClickEventListener listener) {
            super(itemView);

            stream_img = (ImageView) itemView.findViewById(R.id.img_streaming_album);
            stream_artist = (TextView) itemView.findViewById(R.id.tv_streaming_artist);
            stream_track = (TextView) itemView.findViewById(R.id.tv_streaming_track);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, pos);
                    }
                }
            });
        }

        void onBind(Stream item){
            stream_artist.setText(item.getArtistName());
            stream_track.setText(item.getTrackName());
            Glide.with(itemView.getContext()).load(item.getTrackImgUri()).into(stream_img);
        }
    }

    public Stream getItem(int pos){ return streamList.get(pos); }
}
