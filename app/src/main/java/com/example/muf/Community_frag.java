package com.example.muf;

import static com.example.muf.homeActivity.fm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.muf.post.AddPostActivity;
import com.example.muf.post.PostFireBase;
import com.example.muf.post.PostInfoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    private FirebaseAuth firebaseAuth;
    private String user_uid;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    static final String TAG = "HOME";
    private String locationname = "";
    private String locationenglishname = "";
    private TextView No_textview, Set_textview;

    public static Community_frag newinstance(){
        return new Community_frag();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_layout, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFromDB();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        view.findViewById(R.id.Go_write_post).setOnClickListener(onClickListener);

        No_textview = view.findViewById(R.id.No_zone_incommunity);
        Set_textview = view.findViewById(R.id.view_myzone_incommunity);
        Log.d("CommuFrag onCreateView", "kimgijeong");
        if(getArguments() != null){ //HomeActivity에서 bundle받기
            Bundle bundle = getArguments();
            int flag = bundle.getInt("flag");
            locationname = bundle.getString("name");
            locationenglishname = bundle.getString("englishname");
            Log.d("CommuFrag onCreateView", "locationname = " + locationname);
            if(flag == 1){ //Zone이 설정 된 경우
                No_textview.setVisibility(View.INVISIBLE);
                Set_textview.setText(locationname);
                Set_textview.setVisibility(View.VISIBLE);
            }
            else if(flag == 0){
                No_textview.setVisibility(View.VISIBLE);
                Set_textview.setVisibility(View.INVISIBLE);
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView); //community_layout의 recyclerview 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //PostFireBase 객체를 담을 어레이 리스트(어댑터쪽으로)
        postFireBase = new PostFireBase();
        user_uid = FirebaseAuth.getInstance().getUid();
        //파이어베이스테서 게시글 정보 가져오기
        if(locationname.length() > 0){ //Zone이 설정되었다는 것
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection(locationenglishname+"PostLists").orderBy("timestamp", Query.Direction.DESCENDING)
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
            adapter = new PostInfoAdapter(arrayList, getActivity().getApplicationContext());
            recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결
        }
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.Go_write_post:
                if(locationenglishname.length()>0){
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    intent.putExtra("englishname", locationenglishname);
                    startActivity(intent);
                }
                break;
        }
    };

    public void loadFromDB(){
        arrayList.clear();
        firebaseFirestore.collection("TotalPostLists").orderBy("timestamp", Query.Direction.DESCENDING)
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
        recyclerView.setAdapter(adapter);
    }

}
