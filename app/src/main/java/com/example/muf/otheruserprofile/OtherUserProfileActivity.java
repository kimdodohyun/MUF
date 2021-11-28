package com.example.muf.otheruserprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muf.R;
import com.example.muf.communityfrag.post.PostFireBase;
import com.example.muf.communityfrag.post.PostInfoAdapter;
import com.example.muf.homeActivity;
import com.example.muf.model.UserModel;
import com.example.muf.myprofilefrag.MyPostInfoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class OtherUserProfileActivity extends AppCompatActivity {
    private String otherUid;
    private String myUid;
    private RecyclerView recyclerView;
    private PostInfoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private PostFireBase postFireBase;
    private ArrayList<PostFireBase> arrayList;
    private UserModel userinfo;
    private FirebaseFirestore db;
    private int countMyPost;
    private TextView postCount;
    private int countMySong;
    private TextView songCount;
    private int countMyFriend;
    private TextView friendCount;
    private View view;
    private TextView tv_nickname;
    private ImageView iv_profileMusicImage;
    private TextView tv_profileMusicTitle;
    private TextView tv_profileMusicArtist;
    private ImageView im_profilePicture;
    private Button requestFriendButton;
    private SpotifyApi spotifyApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_profile_layout);
        Intent intent = getIntent();
        otherUid = intent.getStringExtra("otherUid");
        myUid = intent.getStringExtra("myUid");
        tv_nickname = findViewById(R.id.nicknameInOhterProfile);
        iv_profileMusicImage = findViewById(R.id.otherProfileMusicImage);
        tv_profileMusicTitle = findViewById(R.id.otherProfileMusicTitle);
        tv_profileMusicArtist = findViewById(R.id.otherProfileMusicArtist);
        im_profilePicture = findViewById(R.id.otherProfilePictureInOtherProfile);
        requestFriendButton = findViewById(R.id.buttonRequestFriend);
        postCount = findViewById(R.id.postCountInOhterProfile);
        songCount = findViewById(R.id.songCountInOtherProfile);
        friendCount = findViewById(R.id.friendCountInOtherProfile);
        spotifyApi = new SpotifyApi();
        spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);

        requestFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("FriendRequestLists").document(otherUid)
                        .update("requestlist", FieldValue.arrayUnion(myUid))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.recyclerViewInOtherProfile);
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); //PostFireBase 객체를 담을 어레이 리스트(어댑터쪽으로)
        postFireBase = new PostFireBase();
        userinfo = new UserModel();
        //파이어베이스 Users/uid/Myinfo/info불러오기
        //게시글, 친구, 애창곡의 갯수와 닉네임, 프로필뮤직 그리기
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(otherUid)
                .collection("Myinfo").document("info");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        userinfo = document.toObject(UserModel.class);
                        Glide.with(getApplicationContext()).load(userinfo.getProfileImageUrl()).into(im_profilePicture);
                        countMyPost = userinfo.getPostcount();
                        countMySong = userinfo.getSongcount();
                        countMyFriend = userinfo.getFriendcount();
                        String num_to_str = String.valueOf(countMyPost);
                        postCount.setText(num_to_str);
                        num_to_str = String.valueOf(countMySong);
                        songCount.setText(num_to_str);
                        num_to_str = String.valueOf(countMyFriend);
                        friendCount.setText(num_to_str);
                        tv_nickname.setText(userinfo.getNickName());
                        //내 프로필 뮤직 셋팅
                        createProfileMusic();
                    }
                }
            }
        });

        //파이어베이스 MyPostLists DB 불러오기
        db.collection("Users").document(otherUid).collection("MyPostLists")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!arrayList.isEmpty()) arrayList.clear();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        postFireBase = document.toObject(PostFireBase.class);
                        arrayList.add(postFireBase);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter = new PostInfoAdapter(arrayList, getApplicationContext());
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

    }

    public void createProfileMusic(){
        //프로필 뮤직 그리기
        String musicUrl = userinfo.getProfileMusicUri();
        if(musicUrl != null){
            String parse[] = musicUrl.split(":");
            Track track = spotifyApi.getService().getTrack(parse[2]);
            String img_url = track.album.images.get(0).url;
            String title = track.name;
            String artist = track.artists.get(0).name;
            Picasso.get().load(img_url).into(iv_profileMusicImage);
            tv_profileMusicTitle.setText(title);
            tv_profileMusicArtist.setText(artist);
        }
    }
}