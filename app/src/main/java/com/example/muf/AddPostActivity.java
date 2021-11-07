package com.example.muf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.muf.Community_frag;
import com.example.muf.R;
import com.example.muf.model.UserModel;
import com.example.muf.music.Music;
import com.example.muf.post.Contents;
import com.example.muf.post.PostFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import kaaes.spotify.webapi.android.models.Image;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_uid = user.getUid();
    private Music selected_music;
    private UserModel userinfo;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String albumtitle;
    private String albumimg;
    private String artist;
    private ImageView imageView;
    private int number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AddPostActivityonCreate", "check" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_post_layout);
        findViewById(R.id.upload).setOnClickListener(onClickListener);
        findViewById(R.id.search_music).setOnClickListener(onClickListener);
        userinfo = new UserModel();
        imageView = findViewById(R.id.search_result_img);

        //파이어스토어에서 현재 user의 userinfo 가져오기
        DocumentReference docRef = db.collection("Users").document(user_uid);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.upload:
                    postUpdate();
                    finish();
                    break;
                case R.id.search_music:
                    Intent intent = new Intent(AddPostActivity.this, SearchActivity.class);
                    startActivityForResult(intent,101);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            selected_music = (Music)data.getSerializableExtra("music");
            albumimg = selected_music.getImg_uri();
            albumtitle = selected_music.getTitle();
            artist = selected_music.getArtist_name();
            Picasso.get().load(albumimg).into(imageView);
            imageView.setVisibility(View.VISIBLE);

        }
    }

    private void postUpdate(){
        final String inputtext = ((EditText) findViewById(R.id.postcontents)).getText().toString();
        final String userprofileimg = userinfo.getProfileImageUrl();
        final String username = userinfo.getNickName();
        final Timestamp timestamp = new Timestamp(new Date());

        if(inputtext.length() > 0){
            //사용자프로필사진, 사용자이름, 앨범title, artist, 앨범img, inputtext를 넘겨야함
            PostFireBase postInfo = new PostFireBase(userprofileimg, username, albumtitle, artist, albumimg, inputtext, timestamp, user_uid);
            uploader(postInfo);
        } else{
            startToast("내용을 입력해주세요.");
        }
    }

    private void uploader(PostFireBase postInfo){ //파이어스토어에 작성내용 업로드
        db.collection("TotalPostLists").add(postInfo) //파이어스토어 postlist 컬렉션에 postInfo 객체에 저장된 게시글내용을 업로드
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding documnet", e);
                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}