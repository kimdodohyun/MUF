package com.example.muf.myprofilefrag;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.R;
import com.example.muf.communityfrag.post.PostFireBase;
import com.example.muf.communityfrag.post.PostInfoAdapter;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Myprofile_frag extends Fragment implements PopupMenu.OnMenuItemClickListener {

    static final String TAG = "MYPROFILE";
    private View view;
    private TextView postcount;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String user_uid;
    private PostFireBase postFireBase;
    private ArrayList<PostFireBase> arrayList;
    private UserModel userinfo;
    private DocumentReference docRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myprofile_layout, container, false);

        view.findViewById(R.id.profile_edit_but).setOnClickListener(onClickListener);
        postcount = view.findViewById(R.id.community_num);
        return view;
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.profile_edit_but:
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                break;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = view.findViewById(R.id.recyclerViewInMyProfile); //community_layout의 recyclerview 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //PostFireBase 객체를 담을 어레이 리스트(어댑터쪽으로)
        postFireBase = new PostFireBase();
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getUid();
        userinfo = new UserModel();

        //파이어베이스 Users/uid/Myinfo/info불러오기
        //내 게시글 갯수 TextView에 설정하기 위함
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("Users").document(user_uid)
                .collection("Myinfo").document("info");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        userinfo = document.toObject(UserModel.class);
                        String num_to_str = String.valueOf(userinfo.getPostcount());
                        postcount.setText(num_to_str);
                    }
                }
            }
        });

        //파이어베이스 MyPostLists DB 불러오기
        db.collection("Users").document(user_uid).collection("MyPostLists")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        adapter = new MyPostInfoAdapter(arrayList, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
}
