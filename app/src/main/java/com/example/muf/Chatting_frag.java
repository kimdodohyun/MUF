package com.example.muf;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.model.ChatModel;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Tree;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class Chatting_frag extends Fragment {

    private  RecyclerView recyclerView;
    private View view;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chatting_layout, container, false);
        recyclerView = view.findViewById(R.id.rv_chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>();
        public ChatRecyclerViewAdapter(){
            uid = FirebaseAuth.getInstance().getUid();

            FirebaseDatabase.getInstance().getReference().child("ChatRooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot item : snapshot.getChildren()){
                        ChatModel newChat = item.getValue(ChatModel.class);
                        if(!newChat.comments.isEmpty()) {
                            chatModels.add(newChat);
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chat,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String destinationUid = null;

            for(String user : chatModels.get(position).users.keySet()){
                if(!user.equals(uid)){
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }

            FirebaseFirestore.getInstance().collection("Users").document(destinationUid).collection("Myinfo").document("info").get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            UserModel user = doc.toObject(UserModel.class);
                            if(user.getProfileImageUrl() != null){
                                Glide.with(customViewHolder.itemView.getContext()).load(user.getProfileImageUrl()).into(customViewHolder.img);
                            }
                            customViewHolder.tv_title.setText(user.getNickName());
                        }
                    });

            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            customViewHolder.tv_last_message.setText(chatModels.get(position).comments.get(lastMessageKey).message);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            customViewHolder.tv_time.setText(simpleDateFormat.format(date));

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("destinationUid",destinationUsers.get(customViewHolder.getAdapterPosition()));

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView img;
            public TextView tv_title;
            public TextView tv_last_message;
            public TextView tv_time;
            public CustomViewHolder(View view) {
                super(view);

                img = (ImageView) view.findViewById(R.id.img_chatList);
                tv_title = (TextView) view.findViewById(R.id.tv_chattingName);
                tv_last_message = (TextView) view.findViewById(R.id.tv_lastMessage);
                tv_time = (TextView) view.findViewById(R.id.tv_timeStamp);
            }
        }
    }
}
