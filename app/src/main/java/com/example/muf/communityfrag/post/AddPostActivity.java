package com.example.muf.communityfrag.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.muf.R;
import com.example.muf.music.SearchActivity;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_uid = user.getUid();
    private com.example.muf.model.Music selected_music;
    private UserModel userinfo;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private String albumtitle;
    private String albumimg;
    private String artist;
    private String uri;
    private ImageView imageView;
    private String ename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_post_layout);
        findViewById(R.id.upload).setOnClickListener(onClickListener);
        findViewById(R.id.search_music).setOnClickListener(onClickListener);
        userinfo = new UserModel();
        imageView = findViewById(R.id.search_result_img);
        Intent intent = getIntent();
        ename = intent.getStringExtra("englishname");
        //파이어스토어에서 현재 user의 userinfo 가져오기
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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.upload:
                    postUpdate(ename);
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
            selected_music = (com.example.muf.model.Music)data.getSerializableExtra("music");
            albumimg = selected_music.getImg_uri();
            albumtitle = selected_music.getTitle();
            artist = selected_music.getArtist_name();
            uri = selected_music.getUri();
            Picasso.get().load(albumimg).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void postUpdate(String Ename){
        final String inputtext = ((EditText) findViewById(R.id.postcontents)).getText().toString();
        final String userprofileimg = userinfo.getProfileImageUrl();
        final String username = userinfo.getNickName();
        final int postnumber = userinfo.getPostcount()+1;     //업로드할 게시글 number는 현재 사용자 게시글 갯수 + 1
        final Timestamp timestamp = new Timestamp(new Date());

        if(inputtext.length() > 0){
            //사용자프로필사진, 사용자이름, 앨범title, artist, 앨범img, inputtext를 넘겨야함
            PostFireBase postInfo = new PostFireBase(userprofileimg, username, albumtitle, artist,
                    albumimg, inputtext, timestamp, user_uid, uri, postnumber, Ename);
            uploader(postInfo, Ename);
        } else{
            startToast("내용을 입력해주세요.");
        }
    }

    private void uploader(PostFireBase postInfo, String Ename){ //파이어스토어에 작성내용 업로드
        db.collection(Ename).document("PostLists").collection("contents").add(postInfo) // 현재장소PostLists 컬렉션에 postInfo 객체에 저장된 게시글내용을 업로드
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        //Users컬렉션에 uid문서의 MyPostLists컬렉션에도 게시글데이터 업로드
                        db.collection("Users").document(user_uid).collection("MyPostLists").add(postInfo)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        //게시글 작성 시 Users/uid/Myinfo/info에 postcount값 +1
                                        docRef.update("postcount", userinfo.getPostcount()+1);
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