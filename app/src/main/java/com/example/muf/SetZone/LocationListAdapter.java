package com.example.muf.SetZone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.muf.R;

import java.util.ArrayList;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.LocationListViewHolder> implements LocationOnItemClickEventListener {
    private ArrayList<LocationList> arrayList;
    private Context context;
    LocationOnItemClickEventListener listener;

    public LocationListAdapter(ArrayList<LocationList> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public LocationListAdapter.LocationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_location, parent, false);
        return new LocationListViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationListViewHolder holder, int position) {
        holder.tv_name.setText(arrayList.get(position).getZonename().get("kname"));
        Log.d("test", "onBindViewHolder: "+arrayList.get(position).getZonename().get("kname"));
    }

    @Override
    public int getItemCount() { return (arrayList != null ? arrayList.size() : 0); }

    public void setOnLocationItemClickListener(LocationOnItemClickEventListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(LocationListViewHolder holder, View view, int pos) {
        if(listener != null){
            listener.onItemClick(holder,view, pos);
        }
    }


    public static class LocationListViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;

        public LocationListViewHolder(View view, final LocationOnItemClickEventListener listener) {
            super(view);
            this.tv_name = view.findViewById(R.id.location_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(LocationListViewHolder.this, view, pos);
                    }
                }
            });
        }

    }
    public LocationList getItem(int pos){ return arrayList.get(pos);
    }
}
