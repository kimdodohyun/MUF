package com.example.muf.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.R;
import com.example.muf.model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendRequestRecyclerAdapter extends RecyclerView.Adapter<FriendRequestRecyclerAdapter.ViewHolder> implements OnConfirmClickListener {
    private ArrayList<UserModel> friendList;
    private Context context;
    OnConfirmClickListener confirmListener;
    OnRejectClickListener rejectListenr;

    public FriendRequestRecyclerAdapter(ArrayList<UserModel> friendList, Context context){
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_request, parent,false);

        return new ViewHolder(view, confirmListener, rejectListenr);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(friendList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void setOnCofirmClickListener(OnConfirmClickListener listener){
        this.confirmListener = listener;
    }

    public void setOnrejectClickListener(OnRejectClickListener listener){
        this.rejectListenr = listener;
    }

    @Override
    public void onItemClick(FriendRequestRecyclerAdapter.ViewHolder holder, View view, int pos) {
        if(confirmListener != null){
            confirmListener.onItemClick(holder,view, pos);
        }
        if(rejectListenr != null){
            rejectListenr.onItemClick(holder,view,pos);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name;
        Button confirm;
        Button reject;

        public ViewHolder(@NonNull View itemView, final OnConfirmClickListener confirmListener, final OnRejectClickListener rejectListener){
            super(itemView);

            profile = (ImageView) itemView.findViewById(R.id.request_profile);
            name = (TextView) itemView.findViewById(R.id.request_name);
            confirm = (Button) itemView.findViewById(R.id.request_confirm);
            reject = (Button) itemView.findViewById(R.id.reuqest_reject);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(confirmListener != null){
                        confirmListener.onItemClick(FriendRequestRecyclerAdapter.ViewHolder.this, view, pos);
                    }
                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(rejectListener != null){
                        rejectListener.onItemClick(FriendRequestRecyclerAdapter.ViewHolder.this, view, pos);
                    }
                }
            });
        }

        void onBind(UserModel item){
            name.setText(item.getNickName());
            if(item.getProfileImageUrl() != null){
                Picasso.get().load(item.getProfileImageUrl()).into(profile);
            }
        }
    }

    public UserModel getItem(int pos){ return friendList.get(pos); }
}
