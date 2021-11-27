package com.example.muf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingUserActivity extends AppCompatActivity {
    private String uid;
    private Uri img_uri;
    private String path_uri;
    private FirebaseFirestore mDatabase;
    private FirebaseStorage mStorage;
    private EditText et_nickname;
    private ImageView img_profile;
    private Button btn_setting_img;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_user);

        et_nickname = findViewById(R.id.et_nickname);
        img_profile = findViewById(R.id.img_profile);
        btn_setting_img = findViewById(R.id.btn_imgSetting);
        btn_confirm = findViewById(R.id.btn_confirm);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        mDatabase = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        btn_setting_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,1);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(img_uri != null){
                    Uri file = Uri.fromFile(new File(path_uri));

                    StorageReference refer = mStorage.getReference().child("userprofileImages").child("uid/"+file.getLastPathSegment());
                    refer.putFile(img_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            final Task<Uri> image_url = task.getResult().getStorage().getDownloadUrl();
                            while(!image_url.isComplete());

                            UserModel user = new UserModel(et_nickname.getText().toString(), image_url.getResult().toString(),
                                    null,uid, null, 0, 0, 0);

                            mDatabase.collection("Users").document(uid).collection("Myinfo")
                                    .document("info").set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("setting", "Success");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("setting", "Failure: " ,e);
                                        }
                                    });

                            Map<String, Object> friendList = new HashMap<>();
                            Map<String, Object> friendRequestList = new HashMap<>();
                            friendList.put("friends", Arrays.asList());
                            friendRequestList.put("requestlist",Arrays.asList());
                            mDatabase.collection("FriendLists").document(uid).set(friendList);
                            mDatabase.collection("FriendRequestLists").document(uid).set(friendRequestList);

                            Intent intent = new Intent(getApplicationContext(),homeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else{
                    UserModel user = new UserModel(et_nickname.getText().toString(), null,
                            null,uid, null, 0, 0, 0);

                    mDatabase.collection("Users").document(uid).collection("Myinfo")
                            .document("info").set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("setting", "Success");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("setting", "Failure: " ,e);
                                }
                            });

                    Map<String, Object> friendList = new HashMap<>();
                    Map<String, Object> friendRequestList = new HashMap<>();
                    friendList.put("friends", Arrays.asList());
                    friendRequestList.put("requestlist",Arrays.asList());
                    mDatabase.collection("FriendLists").document(uid).set(friendList);
                    mDatabase.collection("FriendRequestLists").document(uid).set(friendRequestList);

                    Intent intent = new Intent(getApplicationContext(),homeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            img_uri = data.getData();
            path_uri = getPath(data.getData());
            img_profile.setImageURI(img_uri);
        }
    }

    public String getPath(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
    }


}