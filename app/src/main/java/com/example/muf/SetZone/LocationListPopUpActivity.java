package com.example.muf.SetZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muf.ChatActivity;
import com.example.muf.R;
import com.example.muf.friend.FriendRecyclerAdapter;
import com.example.muf.friend.OnItemClickEventListener;
import com.example.muf.model.UserModel;
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

public class LocationListPopUpActivity extends Activity {
    private RecyclerView recyclerView;
    private LocationListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<LocationList> arrayList;
    private FirebaseFirestore firebaseFirestore;
    private LocationList locationList;
    private String user_uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.location_list_pop_up_layout);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.7); //Display 사이즈의 90% 각자 원하는 사이즈로 설정하여 사용
        int height = (int) (display.getHeight() * 0.7);  //Display 사이즈의 90% 각자 원하는 사이즈로 설정하여 사용
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.recyclerViewInLocationLists); //location_list_pop_up_layout의 recyclerview 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //LocationLists 객체를 담을 어레이 리스트(어댑터쪽으로)
        locationList = new LocationList();
        user_uid = FirebaseAuth.getInstance().getUid();
        //파이어베이스 LocationLists에서 정보 가져오기
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("LocationLists")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d("task 횟수 체크", "카운트");
                        locationList = document.toObject(LocationList.class);
                        arrayList.add(locationList);
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
        adapter = new LocationListAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        adapter.setOnLocationItemClickListener(new LocationOnItemClickEventListener() {
            @Override
            public void onItemClick(LocationListAdapter.LocationListViewHolder holder, View view, int pos) {
                Intent intent = new Intent();
                intent.putExtra("name",arrayList.get(pos).getName());
                intent.putExtra("englishname", arrayList.get(pos).getEnglishname());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){ //바깥 레이아웃 터치시 꺼지지 않도록
            return false;
        }
        return true;
    }
}
