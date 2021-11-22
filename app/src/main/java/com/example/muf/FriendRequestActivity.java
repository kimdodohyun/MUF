package com.example.muf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.muf.friend.FriendRequestRecyclerAdapter;
import com.example.muf.friend.OnConfirmClickListener;
import com.example.muf.friend.OnRejectClickListener;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private FriendRequestRecyclerAdapter adapter;
    private UserModel request;
    private ArrayList<UserModel> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        recyclerView = findViewById(R.id.rv_requestList);
        recyclerView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        requestList = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("FriendRequestLists").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<String> uidList = (ArrayList<String>) doc.get("requestlist");
                for(int i = 0; i<uidList.size(); i++){
                    FirebaseFirestore.getInstance().collection("Users").document(uidList.get(i)).collection("Myinfo")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                request = doc.toObject(UserModel.class);
                                requestList.add(request);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        adapter = new FriendRequestRecyclerAdapter(requestList,this);
        recyclerView.setAdapter(adapter);

        adapter.setOnCofirmClickListener(new OnConfirmClickListener() {
            @Override
            public void onItemClick(FriendRequestRecyclerAdapter.ViewHolder holder, View view, int pos) {
                String uid = adapter.getItem(pos).getUid();
                FirebaseFirestore.getInstance().collection("FriendLists").document(FirebaseAuth.getInstance().getUid())
                        .update("friends", FieldValue.arrayUnion(uid));
                FirebaseFirestore.getInstance().collection("FriendLists").document(uid)
                        .update("friends", FieldValue.arrayUnion(FirebaseAuth.getInstance().getUid()));
                requestList.remove(pos);
                adapter.notifyDataSetChanged();

                FirebaseFirestore.getInstance().collection("FriendRequestLists").document(FirebaseAuth.getInstance().getUid())
                        .update("requestlist",FieldValue.arrayRemove(uid));
            }
        });

        adapter.setOnrejectClickListener(new OnRejectClickListener() {
            @Override
            public void onItemClick(FriendRequestRecyclerAdapter.ViewHolder holder, View view, int pos) {
                String uid = adapter.getItem(pos).getUid();
                requestList.remove(pos);
                adapter.notifyDataSetChanged();
                FirebaseFirestore.getInstance().collection("FriendRequestLists").document(FirebaseAuth.getInstance().getUid())
                        .update("requestlist",FieldValue.arrayRemove(uid));
            }
        });
    }
}