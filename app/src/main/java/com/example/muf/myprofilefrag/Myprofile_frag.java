package com.example.muf.myprofilefrag;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.muf.R;
import com.example.muf.communityfrag.post.PostFireBase;
import com.example.muf.homeActivity;
import com.example.muf.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class Myprofile_frag extends Fragment {

    static final String TAG = "MYPROFILE";
    private RecyclerView recyclerView;
    private MyPostInfoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String user_uid;
    private PostFireBase postFireBase;
    private ArrayList<PostFireBase> arrayList;
    private UserModel userinfo;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FragmentManager fm;
    private FragmentTransaction ft;
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
    private SpotifyApi spotifyApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myprofile_layout, container, false);

        //????????? ?????? ??????
        view.findViewById(R.id.profile_edit_but).setOnClickListener(onClickListener);
        view.findViewById(R.id.favoriteSongButton).setOnClickListener(onClickListener);
        tv_nickname = view.findViewById(R.id.nicknameInMyProfile);
        iv_profileMusicImage = view.findViewById(R.id.myProfileMusicImage);
        tv_profileMusicTitle = view.findViewById(R.id.myProfileMusicTitle);
        tv_profileMusicArtist = view.findViewById(R.id.myProfileMusicArtist);
        im_profilePicture = view.findViewById(R.id.myProfilePictureInMyProfile);
        postCount = view.findViewById(R.id.community_num);
        songCount = view.findViewById(R.id.fsong_num);
        friendCount = view.findViewById(R.id.friend_num);
        return view;
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.profile_edit_but:
                Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                startActivity(intent);
                break;
            case R.id.favoriteSongButton:
                Intent intent1 = new Intent(getActivity(), MySongListActivity.class);
                startActivity(intent1);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        recyclerView = view.findViewById(R.id.recyclerViewInMyProfile); //myprofile_layout??? recyclerview ????????? ??????
        recyclerView.setHasFixedSize(true); //?????????????????? ???????????? ??????
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); //PostFireBase ????????? ?????? ????????? ?????????(??????????????????)
        postFireBase = new PostFireBase();
        firebaseAuth = FirebaseAuth.getInstance();
        user_uid = firebaseAuth.getUid();
        userinfo = new UserModel();
        fm = getParentFragmentManager();
        ft = fm.beginTransaction();
        spotifyApi = new SpotifyApi();
        spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);
        //?????????????????? Users/uid/Myinfo/info????????????
        //??? ?????????,??????, ????????? ?????? TextView??? ???????????? ?????? (countMyPost??? ??????????????? ??????)
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(user_uid)
                .collection("Myinfo").document("info");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        userinfo = document.toObject(UserModel.class);
                        Glide.with(getActivity().getApplicationContext()).load(userinfo.getProfileImageUrl()).into(im_profilePicture);
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
                        //??? ????????? ?????? ??????
                        createProfileMusic();
                    }
                }
            }
        });

        //?????????????????? MyPostLists DB ????????????
        db.collection("Users").document(user_uid).collection("MyPostLists")
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
                }else{
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        adapter = new MyPostInfoAdapter(arrayList, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter); //????????????????????? ????????? ??????

        adapter.setButtonItemClickListener(new MyPostInfoAdapter.OnButtonItemClickEventListener() {
            @Override
            public void onButtonItemClick(int position, int flag) {
                final PostFireBase item = arrayList.get(position);
                final int option = flag;
                switch(option){
                    case 0: // edit
                        break;
                    case 1: //remove
                        remove(item);
                        break;
                }
            }
        });
    }

    private void remove(PostFireBase item){
        final int postnum = item.getNumber();
        final String ename = item.getEname();
        //??? ????????? MyPostLists?????? ??????
        db.collection("Users").document(user_uid).collection("MyPostLists")
                .whereEqualTo("number", postnum).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            DocumentReference defRef = documentSnapshot.getReference();
                            defRef.delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + "??????");
                    }
                });
        //??? ????????? Location?????? ??????
        db.collection(ename).document("PostLists").collection("contents")
                .whereEqualTo("number", postnum)
                .whereEqualTo("uid", user_uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            DocumentReference defRef = documentSnapshot.getReference();
                            defRef.delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + "??????");
                    }
                });
        //???????????? ???????????? postnum?????? ?????? ??????????????? postnum??? 1??? ??????
        db.collection("Users").document(user_uid).collection("MyPostLists")
                .whereGreaterThan("number", postnum).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            DocumentReference defRef = documentSnapshot.getReference();
                            defRef.update("number", FieldValue.increment(-1));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + "??????");
                    }
                });
        //?????? ????????? ?????? postcount??? 1??????
        db.collection("Users").document(user_uid).collection("Myinfo")
                .document("info").update("postcount", FieldValue.increment(-1));
    }

    public void createProfileMusic(){
        //????????? ?????? ?????????
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
