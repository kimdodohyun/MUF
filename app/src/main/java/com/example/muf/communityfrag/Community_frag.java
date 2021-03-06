package com.example.muf.communityfrag;

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

import com.example.muf.R;
import com.example.muf.communityfrag.post.AddPostActivity;
import com.example.muf.communityfrag.post.PostFireBase;
import com.example.muf.communityfrag.post.PostInfoAdapter;
import com.example.muf.homeActivity;
import com.example.muf.otheruserprofile.OtherUserProfileActivity;
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
    private PostInfoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PostFireBase> arrayList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userUid;
    private PostFireBase postFireBase;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    static final String TAG = "COMMUNITY";
    private String kname = "";
    private String ename = "";
    private int flag = -1;
    private TextView No_textview, Set_textview;

    public static Community_frag newinstance(){
        return new Community_frag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        userUid = firebaseAuth.getUid();
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
        if(getArguments() != null){ //HomeActivity?????? bundle??????
            Bundle bundle = getArguments();
            flag = bundle.getInt("flag");
            kname = bundle.getString("name");
            ename = bundle.getString("englishname");
            Log.d("CommuFrag onCreateView", "locationname = " + kname);
            if(flag == 1){ //Zone??? ?????? ??? ??????
                No_textview.setVisibility(View.INVISIBLE);
                Set_textview.setText(kname);
                Set_textview.setVisibility(View.VISIBLE);
            }
            else if(flag == 0){
                No_textview.setVisibility(View.VISIBLE);
                Set_textview.setVisibility(View.INVISIBLE);
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView); //community_layout??? recyclerview ????????? ??????
        recyclerView.setHasFixedSize(true); //?????????????????? ???????????? ??????
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //PostFireBase ????????? ?????? ????????? ?????????(??????????????????)
        postFireBase = new PostFireBase();
        //???????????????????????? ????????? ?????? ????????????
        if(flag == 1){ //Zone??? ?????????????????? ???
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection(ename).document("PostLists").collection("contents")
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
            adapter = new PostInfoAdapter(arrayList, getActivity().getApplicationContext());
            recyclerView.setAdapter(adapter); //????????????????????? ????????? ??????

            adapter.setOnItemClickListener(new PostInfoAdapter.OnItemClickEventListener() {
                @Override
                public void onItemClick(View view, int pos) {
                    PostFireBase item = arrayList.get(pos);
                    homeActivity.mSpotifyAppRemote.getPlayerApi().play(item.getUri());
                }
            });
            //??????????????? ?????? ?????? ????????? ????????? ?????? ?????? ???????????? ??????
            adapter.setOnImageClickListener(new PostInfoAdapter.OnImageClickEventListener() {
                @Override
                public void onItemClick(int pos) {
                    String otherUserUid = arrayList.get(pos).getUid();
                    if(!otherUserUid.equals(userUid)){
                        Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
                        intent.putExtra("otherUid", otherUserUid);
                        intent.putExtra("myUid", userUid);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.Go_write_post:
                if(flag == 1){
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    intent.putExtra("englishname", ename);
                    startActivity(intent);
                }
                break;
        }
    };

    public void loadFromDB(){
        arrayList.clear();
        if(kname.length() > 0){ //Zone??? ?????????????????? ???
            firebaseFirestore.collection(ename).document("PostLists").collection("contents")
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
            recyclerView.setAdapter(adapter); //????????????????????? ????????? ??????
        }
    }
}
