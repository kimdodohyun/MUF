package com.example.muf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        findViewById(R.id.upload).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.upload:
                    postUpdate();
                    Intent intent = new Intent(getApplication(), homeActivity.class);
                    startActivity(intent);
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
}