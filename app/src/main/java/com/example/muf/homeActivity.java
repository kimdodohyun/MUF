package com.example.muf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muf.SetZone.LocationListPopUpActivity;
import com.example.muf.SetZone.SetZoneActivity;
import com.example.muf.Streaming.Stream;
import com.example.muf.communityfrag.Community_frag;
import com.example.muf.friend.Friends_list_frag;
import com.example.muf.myprofilefrag.Myprofile_frag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.HashMap;
import java.util.Map;

public class homeActivity extends AppCompatActivity {
    public static String AUTH_TOKEN;
    static final String TAG = "HOME";
    private BottomNavigationView bottomNavigationView;
    private ImageButton imageButton;
    public static FragmentManager fm;
    private FragmentTransaction ft;
    private Friends_list_frag frag1;
    private Chatting_frag frag2;
    private Home_frag frag3;
    private Community_frag frag4;
    private Myprofile_frag frag5;
    private int CurrentFrag;
    private int flag = -1;
    private String placename;
    private String placeenglishname;
    private static final String CLIENT_ID = "6102ea6562fe41fd99ebad74ecffd39f";
    private static final String REDIRECT_URI ="com.example.muf://callback";
    private static final int REQUEST_CODE = 1337;
    public static SpotifyAppRemote mSpotifyAppRemote;
    private ImageButton btn_play;
    private ImageButton btn_previous;
    private ImageButton btn_next;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String user_uid;
    private String current_uri;
    private ImageButton friendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }

        btn_play = findViewById(R.id.btn_play);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);
        imageButton = findViewById(R.id.search_location);
        friendRequest = findViewById(R.id.friend_request);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationListPopUpActivity.class);
                startActivityForResult(intent, 12161531);
            }
        });

        friendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FriendRequestActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.main_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.friends_list:
                        CurrentFrag = 0;
                        setFrag(0);
                        break;
                    case R.id.chatting:
                        CurrentFrag = 1;
                        setFrag(1);
                        break;
                    case R.id.main_home:
                        CurrentFrag = 2;
                        setFrag(2);
                        break;
                    case R.id.community:
                        CurrentFrag = 3;
                        setFrag(3);
                        break;
                    case R.id.my_profile:
                        CurrentFrag = 4;
                        setFrag(4);
                        break;
                }
                return true;
            }
        });

        frag1 = new Friends_list_frag();
        frag2 = new Chatting_frag();
        frag3 = new Home_frag();
        frag4 = new Community_frag();
        frag5 = new Myprofile_frag();
        CurrentFrag = 2;
        setFrag(2); //첫 프래그먼트 홈으로 지정

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
                    if(playerState.isPaused){
                        mSpotifyAppRemote.getPlayerApi().resume();
                    }
                    else{
                        mSpotifyAppRemote.getPlayerApi().pause();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        authSpotify();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
                            current_uri = playerState.track.uri;
                        });
                        if(flag == 1){
                            String val = FirebaseAuth.getInstance().getUid()+"_"+current_uri;
                            firebaseFirestore.collection(placeenglishname).document("UserLists")
                                    .update(FirebaseAuth.getInstance().getUid(), val);
                        }
                        connected();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    private void authSpotify(){
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    AUTH_TOKEN = response.getAccessToken();
                    Log.d("STARTING", "GOT AUTH TOKEN");
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Log.d("STARTING", response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
        else if (requestCode == 12161531 && resultCode == RESULT_OK){ //SearchLocaton에서 장소를 선택했으면
            Log.d("HomeActivity", "OnActivityResult kimgijeong");
            String name = data.getStringExtra("englishname"); //선택한 장소 ename을 받아와
            Intent intent = new Intent(this, SetZoneActivity.class); //SetZoneActivity로 전달
            intent.putExtra("locationname",name);
            startActivityForResult(intent, 1531);

        }
        else if(requestCode == 1531 && resultCode == RESULT_OK){ //SetZoneActivity완료후 flag값과 장소 이름 frag로 전달
            flag = data.getIntExtra("flag", -1);
            placename = data.getStringExtra("name");
            placeenglishname = data.getStringExtra("englishname");
            if(flag != -1){ //0 : Nozone, 1 : Setzone
                Bundle bundle = new Bundle();
                bundle.putInt("flag", flag);
                bundle.putString("name", placename);
                bundle.putString("englishname", placeenglishname);
                if(placename != null && placename.length() > 0){ //Zone 설정을 성공하면 파이어베이스 사용자 db에 위치권한 추가
                    user_uid = FirebaseAuth.getInstance().getUid();
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference docRef = firebaseFirestore.collection("Users").document(user_uid)
                            .collection("Myinfo").document("info");
                    docRef.update("myzonelist", FieldValue.arrayUnion(placename));
                }
                frag3.setArguments(bundle);
                frag4.setArguments(bundle);
                if(CurrentFrag == 2) setFrag(2);
                else if(CurrentFrag == 3) setFrag(3);
            }
        }
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    if(playerState.isPaused){
                        btn_play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    }
                    else{
                        btn_play.setImageResource(R.drawable.ic_baseline_pause_24);
                    }
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d(TAG, "state : "+playerState.toString());
                        TextView tv_title = findViewById(R.id.title_txt);
                        TextView tv_artist = findViewById(R.id.artist_txt);
                        ImageView img_track = findViewById(R.id.album_img);
                        tv_title.setText(track.name);
                        tv_artist.setText(track.artist.name);
                        tv_title.setSelected(true);
                        tv_artist.setSelected(true);
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            img_track.setImageBitmap(bitmap);
                                        });
                        if(placeenglishname != null && !current_uri.equals(track.uri)) {
                            dbFunction(track);
                            current_uri = track.uri;
                        }
                    }
                    else{
                        TextView tv_title = findViewById(R.id.title_txt);
                        TextView tv_artist = findViewById(R.id.artist_txt);
                        tv_title.setText("재생중인 곡이 없습니다.");
                        tv_artist.setText("Spotify로 노래를 재생시켜 주세요.");
                    }
                });
    }

    private void dbFunction(Track track){
        String val = FirebaseAuth.getInstance().getUid()+"_"+track.uri;
        firebaseFirestore.collection(placeenglishname).document("UserLists")
                .update(FirebaseAuth.getInstance().getUid(), val);

        firebaseFirestore.collection(placeenglishname).document("UserLists").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        DocumentSnapshot doc = documentSnapshot;
                        for (Object value : doc.getData().values()) {
                            Log.d(TAG, "uid : " + value.toString());
                            String parse[] = value.toString().split("_");
                            String uid = parse[0];
                            String uri = parse[1];
                            if(track.uri.equals(uri) && !FirebaseAuth.getInstance().getUid().equals(uid)){
                                Intent intent = new Intent(getApplicationContext(),EventActivity.class);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }

    //프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, frag2);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, frag4);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame, frag5);
                ft.commit();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        if(flag == 1) {
            Map<String, Object> data = new HashMap<>();
            data.put(FirebaseAuth.getInstance().getUid(), FieldValue.delete());
            firebaseFirestore.collection(placeenglishname).document("UserLists").update(data);
        }
    }
}