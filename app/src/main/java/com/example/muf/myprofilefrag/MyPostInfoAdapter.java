package com.example.muf.myprofilefrag;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.R;
import com.example.muf.communityfrag.post.PostFireBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyPostInfoAdapter extends RecyclerView.Adapter<MyPostInfoAdapter.MyPostInfoViewHolder> {
    private ArrayList<PostFireBase> arrayList;
    private Context context;

    public MyPostInfoAdapter(ArrayList<PostFireBase> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_itme_myprofile, parent, false);
        MyPostInfoViewHolder holder = new MyPostInfoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostInfoViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class MyPostInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_usernickname;
        TextView tv_albumtitle;
        TextView tv_artist;
        ImageView album_imag;
        TextView tv_inputtext;
        ImageButton ib_option;

        public MyPostInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.my_profile_picture);
            this.tv_usernickname = itemView.findViewById(R.id.my_nickname);
            this.tv_albumtitle = itemView.findViewById(R.id.albumtitle);
            this.tv_artist = itemView.findViewById(R.id.artistname);
            this.album_imag = itemView.findViewById(R.id.album_image);
            this.tv_inputtext = itemView.findViewById(R.id.inputtext);
            this.ib_option = itemView.findViewById(R.id.post_option_button);

            ib_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("옵션버튼클릭", "onClick: " + "진입");
                    PopupMenu popupMenu = new PopupMenu(context.getApplicationContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.popupmenu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){
                                case R.id.edit:
                                    break;
                                case R.id.remove:
                                    removePost(position);
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }

    public void removePost(int position){
        String uid = arrayList.get(position).getUid();
        int postnumber = arrayList.get(position).getNumber();

        //Users/uid/MyPostLists/해당자동id문서 삭제
        //where쿼리문으로 현재 postnumber를 가진 문서 찾기
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).collection("MyPostLists")
                .whereEqualTo("number", postnumber).get().
    }
}
