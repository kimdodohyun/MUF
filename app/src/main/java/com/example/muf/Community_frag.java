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
import com.example.muf.post.PostInfoAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Community_frag extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Contents> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

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
        arrayList = new ArrayList<>(); //User 객체를 담을 어레이 리스트(어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("Contents"); //DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 실시간 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //혹시 기존에 남아있는 데이터가 없도록 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){ //반복문으로 데이터 List를 추출해냄
                    Contents contents = snapshot.getValue(Contents.class); //만들어뒀던 Contents 객체에 데이터 담기
                    arrayList.add(contents); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //DB를 가져오던 중 에러 발생 시
                Log.e("Community_Fag", String.valueOf(error.toException()));
            }
        });
        adapter = new PostInfoAdapter(arrayList, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결
        return view;
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.Go_write_post:
                //Fragment_to_Activity
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
                break;
        }
    };
}
