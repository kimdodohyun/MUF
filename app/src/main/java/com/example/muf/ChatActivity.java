package com.example.muf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muf.model.ChatModel;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatActivity extends AppCompatActivity {
    private Button btn_send;
    private EditText et_message;
    private String uid;
    private String destinationUid;
    private String chatRoomId;
    private RecyclerView recyclerView;
    private UserModel myInfo;
    private UserModel friendInfo;
    public List<ChatModel.Comment> comments;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        comments = new ArrayList<>();
        destinationUid = getIntent().getStringExtra("destinationUid");
        uid = FirebaseAuth.getInstance().getUid();
        btn_send = findViewById(R.id.btn_send);
        et_message = findViewById(R.id.et_message);
        myInfo = new UserModel();
        friendInfo = new UserModel();

        SoftKeyboardDectectorView softKeyboardDectectorView = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDectectorView,new FrameLayout.LayoutParams(-1,-1));

        softKeyboardDectectorView.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                recyclerView.scrollToPosition(comments.size()-1);
            }
        });

        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Myinfo").document("info").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        myInfo = doc.toObject(UserModel.class);
                    }
                });
        FirebaseFirestore.getInstance().collection("Users").document(destinationUid).collection("Myinfo").document("info").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        friendInfo = doc.toObject(UserModel.class);
                    }
                });

        recyclerView = findViewById(R.id.recyler_message);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = et_message.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(chatRoomId).child("comments").push().setValue(comment);

                et_message.setText("");
            }
        });
        checkChatRoom();
    }

    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("ChatRooms").orderByChild("users/"+uid).equalTo(true)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int flag = 0;
                    for(DataSnapshot item : snapshot.getChildren()){
                        ChatModel chatModel = item.getValue(ChatModel.class);
                        if(chatModel.users.containsKey(destinationUid) && chatModel.users.size() == 2){
                            chatRoomId = item.getKey();
                            flag = 1;
                            recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                            recyclerView.setAdapter(new RecyclerViewAdapter());
                        }
                    }
                    if(flag == 0){
                        ChatModel newRoom = new ChatModel();
                        newRoom.users.put(uid, true);
                        newRoom.users.put(destinationUid, true);
                        FirebaseDatabase.getInstance().getReference().child("ChatRooms").push().setValue(newRoom);
                        checkChatRoom();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
       // List<ChatModel.Comment> comments;
        public RecyclerViewAdapter() {
         //   comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("ChatRooms").child(chatRoomId).child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            comments.clear();
                            for(DataSnapshot item : snapshot.getChildren()){
                                comments.add(item.getValue(ChatModel.Comment.class));
                            }
                            notifyDataSetChanged();
                            recyclerView.scrollToPosition(comments.size()-1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        @Override
        public int getItemViewType(int position) {
            if(comments.get(position).uid.equals(uid)){
                return 1;
            }
            else
                return 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_other,parent,false);
            if(viewType == 1){
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_my,parent,false);
            }
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time =simpleDateFormat.format(date);

            if(comments.get(position).uid.equals(uid)) {
                ((MessageViewHolder) holder).tv_message_my.setText(comments.get(position).message);
                ((MessageViewHolder) holder).tv_chatName_my.setText(myInfo.getNickName());
                ((MessageViewHolder) holder).tv_date_my.setText(time);
                if(myInfo.getProfileImageUrl() != null){
                    Glide.with(holder.itemView.getContext()).load(myInfo.getProfileImageUrl()).into(((MessageViewHolder) holder).img_chatProfile_my);
                }
            }
            else{
                ((MessageViewHolder) holder).tv_message_friend.setText(comments.get(position).message);
                ((MessageViewHolder) holder).tv_chatName_friend.setText(friendInfo.getNickName());
                ((MessageViewHolder) holder).tv_date_friend.setText(time);
                if(friendInfo.getProfileImageUrl() != null){
                    Glide.with(holder.itemView.getContext()).load(friendInfo.getProfileImageUrl()).into(((MessageViewHolder) holder).img_chatProfile_friend);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_message_my;
            public TextView tv_message_friend;
            public TextView tv_chatName_my;
            public TextView tv_chatName_friend;
            public TextView tv_date_my;
            public TextView tv_date_friend;
            public ImageView img_chatProfile_my;
            public ImageView img_chatProfile_friend;
            public MessageViewHolder(View view) {
                super(view);
                tv_message_my = (TextView) view.findViewById(R.id.tv_message_my);
                tv_message_friend = (TextView) view.findViewById(R.id.tv_message_other);
                tv_chatName_my = (TextView) view.findViewById(R.id.tv_chatName_my);
                tv_chatName_friend = (TextView) view.findViewById(R.id.tv_chatName_other);
                tv_date_my = (TextView) view.findViewById(R.id.tv_date_my);
                tv_date_friend = (TextView) view.findViewById(R.id.tv_date_other);
                img_chatProfile_my = (ImageView) view.findViewById(R.id.img_chatProfile_my);
                img_chatProfile_friend = (ImageView) view.findViewById(R.id.img_chatProfile_other);
            }
        }
    }
}