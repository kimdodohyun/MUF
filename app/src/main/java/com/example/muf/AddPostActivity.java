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
import android.widget.Toast;

import com.example.muf.Community_frag;
import com.example.muf.R;
import com.example.muf.music.Music;
import com.example.muf.post.PostFireBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";
    private FirebaseUser user;
    private String user_uid;
    private Community_frag community;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Music selected_music;
//    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_post_layout);
        findViewById(R.id.upload).setOnClickListener(onClickListener);
        findViewById(R.id.search_music).setOnClickListener(onClickListener);
        community = new Community_frag();
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
        }
    }

    private void postUpdate(){
        final String contents = ((EditText) findViewById(R.id.postcontents)).getText().toString();
        if(contents.length() > 0){
            user = FirebaseAuth.getInstance().getCurrentUser();
            user_uid = user.getUid();
            PostFireBase postInfo = new PostFireBase(contents, user_uid);
            uploader(postInfo);
        } else{
            startToast("내용을 입력해주세요.");
        }
    }

    private void uploader(PostFireBase postInfo){ //파이어스토어에 작성내용 업로드
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(user_uid).add(postInfo) //파이어스토어 user_uid 컬렉션에 postInfo 객체에 저장된 게시글내용을 업로드
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
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