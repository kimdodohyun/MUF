package com.example.muf.myprofilefrag;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.example.muf.R;
import com.example.muf.homeActivity;
import com.example.muf.model.Music;
import com.example.muf.model.UserModel;
import com.example.muf.music.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class ProfileEditActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage mStorage;
    private FirebaseFirestore db;
    private String userUid;
    private UserModel userinfo;
    private String nickName;
    private String profileImageUrl;
    private String profileMusicUrl;
    private String uid;
    private ArrayList<String> myzonelist;
    private int postcount;
    private int songcount;
    private int friendcount;
    private ImageView im_myProfilePicture;
    private ImageView im_myProfileMusicImage;
    private TextView tv_myNickname;
    private TextView tv_myProfileMusicTitle;
    private TextView tv_myProfileMusicArtist;
    private Button buttonSetNickName;
    private Button buttonSetProfilePicture;
    private Button buttonSetProfileMusic;
    private Button buttonSetComplete;
    private SpotifyApi spotifyApi;
    private Track track;
    private String parse[];
    private String musicUri;
    private String albumImgge;
    private String albumTitle;
    private String artistName;
    private Uri profilePictureUri;
    private String profilePicturePathUri;
    private Music selected_music;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_edit);

        firebaseAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        userUid = firebaseAuth.getUid();
        spotifyApi = new SpotifyApi();
        spotifyApi.setAccessToken(homeActivity.AUTH_TOKEN);
        im_myProfilePicture = findViewById(R.id.myProfilePictureInEdit);    //????????? ??????
        im_myProfileMusicImage = findViewById(R.id.myProfileMusicImageInEdit); //????????? ?????? ?????? ?????????
        tv_myNickname = findViewById(R.id.myNicknameInEdit);                //????????? ?????????
        tv_myProfileMusicTitle = findViewById(R.id.myProfileMusicTitleInEdit); //????????? ?????? ??????
        tv_myProfileMusicArtist = findViewById(R.id.myProfileMusicArtistInEdit); //????????? ?????? ????????????
        buttonSetProfilePicture = findViewById(R.id.buttonSetProfilePicture); //??????????????? ?????? ??????
        buttonSetProfileMusic = findViewById(R.id.buttonSetProfileMusic);     //????????? ?????? ?????? ??????
        buttonSetComplete = findViewById(R.id.buttonSetCompleteInEdit);
        buttonSetNickName = findViewById(R.id.editTextInEdit);

        buttonSetProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,1);
            }
        });

        buttonSetProfileMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileEditActivity.this, SearchActivity.class);
                startActivityForResult(intent,2);
            }
        });

        buttonSetNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileEditActivity.this, EditMyNickname.class);
                startActivityForResult(intent, 3);
            }
        });

        //????????? ?????? ?????? ?????? (???????????? 1.????????? 2.??????????????? 3.???????????????)
        buttonSetComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel user = new UserModel(userinfo);
                db.collection("Users").document(userUid).collection("Myinfo")
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
                finish();
            }
        });

        //Users/uid/Myinfo/info?????? ????????? ?????? ???????????? ?????????
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(userUid)
                .collection("Myinfo").document("info");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userinfo = document.toObject(UserModel.class);
                        //????????? ????????? ??????, ????????? ?????????
                        if(userinfo.getProfileImageUrl() != null){
                            Picasso.get().load(userinfo.getProfileImageUrl()).into(im_myProfilePicture);
                        }
                        if(userinfo.getNickName() != null){
                            tv_myNickname.setText(userinfo.getNickName());
                        }
                        musicUri = userinfo.getProfileMusicUri();
                        //????????? ????????? ?????? ??????, ?????? ?????????
                        if (musicUri != null) {
                            parse = musicUri.split(":");
                            track = spotifyApi.getService().getTrack(parse[2]);
                            albumImgge = track.album.images.get(0).url;
                            albumTitle = track.name;
                            artistName = track.artists.get(0).name;
                            Picasso.get().load(albumImgge).into(im_myProfileMusicImage);
                            tv_myProfileMusicTitle.setText(albumTitle);
                            tv_myProfileMusicArtist.setText(artistName);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //????????? ?????? ?????? ???
        if(requestCode == 1){
            //????????? ???????????? ?????? ?????????
            profilePictureUri = data.getData();
            profilePicturePathUri = getPath(data.getData());
            im_myProfilePicture.setImageURI(profilePictureUri);
            Uri file = Uri.fromFile(new File(profilePicturePathUri));

            StorageReference refer = mStorage.getReference().child("userprofileImages").child("uid/"+file.getLastPathSegment());
            refer.putFile(profilePictureUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    final Task<Uri> image_url = task.getResult().getStorage().getDownloadUrl();
                    while(!image_url.isComplete());

                    //????????? ????????? ?????? ???????????? ??????????????? ????????? db??? ????????????
                    userinfo.setProfileImageUrl(image_url.getResult().toString());
                }
            });
        }
        //????????? ?????? ?????? ??? ?????? ?????????
        else if(requestCode == 2 && resultCode == RESULT_OK){
            selected_music = (Music)data.getSerializableExtra("music");
            albumImgge = selected_music.getImg_uri();
            albumTitle = selected_music.getTitle();
            artistName = selected_music.getArtist_name();
            Picasso.get().load(albumImgge).into(im_myProfileMusicImage);
            tv_myProfileMusicTitle.setText(albumTitle);
            tv_myProfileMusicArtist.setText(artistName);

            musicUri = selected_music.getUri();
            userinfo.setProfileMusicUri(musicUri);
        }
        else if(requestCode == 3 && resultCode == RESULT_OK){
            String nickname = data.getStringExtra("nickname");
            if(nickname.length() > 0){
                tv_myNickname.setText(nickname);
                userinfo.setNickName(nickname);
            }
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
