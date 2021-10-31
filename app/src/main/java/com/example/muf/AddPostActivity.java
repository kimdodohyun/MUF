package com.example.muf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muf.Community_frag;
import com.example.muf.R;
import com.example.muf.post.PostFireBase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "AddPostActivity";
    private FirebaseUser user;
    private Community_frag community;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_post_layout);
        findViewById(R.id.upload).setOnClickListener(onClickListener);
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
            }
        }
    };

    private void postUpdate(){
        final String contents = ((EditText) findViewById(R.id.postcontents)).getText().toString();

        if(contents.length() > 0){
            user = FirebaseAuth.getInstance().getCurrentUser();
            PostFireBase postInfo = new PostFireBase(contents, user.getUid());
            uploader(postInfo);
        } else{
            startToast("내용을 입력해주세요.");
        }
    }

    private void uploader(PostFireBase postInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection( "posts").add(postInfo)
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

    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                Log.d(TAG, "setFrag: frag1");
                ft.replace(R.id.addpostlayout, community);
                ft.commit();
                break;
        }
    }
}