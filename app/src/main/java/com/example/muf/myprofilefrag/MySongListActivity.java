package com.example.muf.myprofilefrag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.muf.R;
import com.example.muf.model.Music;
import com.example.muf.model.UserModel;
import com.example.muf.music.SearchActivity;
import com.example.muf.music.SearchResultsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MySongListActivity extends AppCompatActivity {
    private Music selected_music;
    private String TAG = "MySongListActivity";
    private MySongListAdapter adapter;
    private Music music;
    private ArrayList<Music> arrayList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth firebaseAuth;
    private String user_uid;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private UserModel userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_song_list_layout);
        //노래 추가 버튼
        findViewById(R.id.buttonAddSong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.buttonAddSong: //노래 검색 SearchActivity로 이동
                        Intent intent = new Intent(MySongListActivity.this, SearchActivity.class);
                        startActivityForResult(intent,1531);
                }
            }
        });
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getUid();
        userinfo = new UserModel();
        docRef = db.collection("Users").document(user_uid)
                .collection("Myinfo").document("info");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        userinfo = document.toObject(UserModel.class);
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.recyclerViewInMySong);
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //Music 겍체를 담을 ArrayList
        music = new Music();

        //Users/uid/mySongList에서 내 애창곡 리스트 가져오기
        db.collection("Users").document(user_uid)
                .collection("mySongList").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                music = documentSnapshot.toObject(Music.class);
                                arrayList.add(music);
                                Log.d(TAG, "onComplete: " + music.getAlbum_name());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        adapter = new MySongListAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);
    }

    //SearchActivity에서 검색, 선택한 노래 db에 올리기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1531 && resultCode == RESULT_OK){
            selected_music = (Music)data.getSerializableExtra("music");
            arrayList.add(selected_music);
            adapter.notifyDataSetChanged();
            musicUpload();
        }
    }

    private void musicUpload(){
        //Users/uid/mySongList 내 애창곡 리스트에 추가한 곡 넣어주기
        db.collection("Users").document(user_uid)
                .collection("mySongList").add(selected_music)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("MySongListActivity", "onSuccess: " + "추가한 노래 db업로드 성공");
                        docRef.update("songcount", userinfo.getSongcount()+1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MySongListActivity", "onFailure: " + "추가한 노래 db업로드 실패");
                    }
                });
    }
}