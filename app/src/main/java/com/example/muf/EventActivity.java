package com.example.muf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;

public class EventActivity extends Activity {
    private String uid;
    private TextView tv_name;
    private ImageView img_profile;
    private Button btn_confirm;
    private Button btn_reject;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event);
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = (int) (display.getHeight() * 0.17);  //Display 사이즈의 90% 각자 원하는 사이즈로 설정하여 사용
        getWindow().getAttributes().height = height;

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        tv_name = findViewById(R.id.event_name);
        img_profile = findViewById(R.id.event_profile);
        btn_confirm = findViewById(R.id.event_confirm);
        btn_reject = findViewById(R.id.event_reject);
        db = FirebaseFirestore.getInstance();

        FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Myinfo")
                .document("info").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                UserModel user = new UserModel();
                user = doc.toObject(UserModel.class);
                if (user.getProfileImageUrl() != null) {
                    Picasso.get().load(user.getProfileImageUrl()).into(img_profile);
                }
                tv_name.setText(user.getNickName());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("FriendRequestLists").document(uid)
                        .update("requestlist", FieldValue.arrayUnion(FirebaseAuth.getInstance().getUid()))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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