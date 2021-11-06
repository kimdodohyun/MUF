package com.example.muf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.post.Contents;
import com.example.muf.post.PostFireBase;
import com.example.muf.post.PostInfoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Community_frag extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PostFireBase> arrayList;
    private FirebaseFirestore firebaseFirestore;
    private PostFireBase postFireBase;

    private View view;
    static final String TAG = "HOME";

    public static Community_frag newinstance(){
        return new Community_frag();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_layout, container, false);

        view.findViewById(R.id.Go_write_post).setOnClickListener(onClickListener);

        recyclerView = view.findViewById(R.id.recyclerView); //community_layout의 recyclerview 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //PostFireBase 객체를 담을 어레이 리스트(어댑터쪽으로)
        postFireBase = new PostFireBase();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("totalpostlist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        postFireBase = document.toObject(PostFireBase.class);
                        arrayList.add(postFireBase);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        adapter = new PostInfoAdapter(arrayList, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결
        return view;
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.Go_write_post:
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
                break;
        }
    };
}
