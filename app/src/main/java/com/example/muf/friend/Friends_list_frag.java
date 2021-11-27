package com.example.muf.friend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.ChatActivity;
import com.example.muf.R;
import com.example.muf.homeActivity;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class Friends_list_frag extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private FriendRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private UserModel friend;
    private ArrayList<UserModel> friendList;
    private FirebaseFirestore db;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.friendslist_layout, container, false);
        recyclerView = view.findViewById(R.id.rv_friendList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        friendList = new ArrayList<>();
        uid = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
        friend = new UserModel();

        db.collection("FriendLists").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<String> uidList = (ArrayList<String>) doc.get("friends");
                Collections.sort(uidList);
                for(int i =0; i<uidList.size(); i++){
                    db.collection("Users").document(uidList.get(i)).collection("Myinfo")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                friend = doc.toObject(UserModel.class);
                                friendList.add(friend);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        adapter = new FriendRecyclerAdapter(friendList,getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickEventListener() {
            @Override
            public void onItemClick(FriendRecyclerAdapter.ViewHolder holder, View view, int pos) {
                UserModel item = adapter.getItem(pos);
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("destinationUid",friendList.get(pos).getUid());
                startActivity(intent);
            }
        });

        adapter.setOnTextClickListener(new OnTextClickEventListener() {
            @Override
            public void onItemClick(FriendRecyclerAdapter.ViewHolder holder, View view, int pos) {
                UserModel item = adapter.getItem(pos);
                homeActivity.mSpotifyAppRemote.getPlayerApi().play(item.getProfileMusicUri());
            }
        });

        return view;
    }
}
