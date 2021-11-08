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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muf.SetZone.SetZoneActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class homeActivity extends AppCompatActivity {
    public static String AUTH_TOKEN;
    static final String TAG = "HOME";
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Friends_list_frag frag1;
    private Chatting_frag frag2;
    private Home_frag frag3;
    private Community_frag frag4;
    private Myprofile_frag frag5;
    private int flag = -1;
    private static final String CLIENT_ID = "6102ea6562fe41fd99ebad74ecffd39f";
    private static final String REDIRECT_URI ="com.example.muf://callback";
    private static final int REQUEST_CODE = 1337;
    public SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }


        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.main_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.friends_list:
                        setFrag(0);
                        break;
                    case R.id.chatting:
                        setFrag(1);
                        break;
                    case R.id.main_home:
                        setFrag(2);
                        break;
                    case R.id.community:
                        setFrag(3);
                        break;
                    case R.id.my_profile:
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

        if(flag == -1){ //Home에서 아직 위치가 설정되지 않은 경우
            Intent intent = new Intent(this, SetZoneActivity.class);
            startActivityForResult(intent, 1531);
        }

        Log.d("HomeActivity onCreate", "flagvalue = " + flag +" kimgijeong");
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
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
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
        else if (requestCode == 1531 && resultCode == RESULT_OK){
            Log.d("HomeActivity", "OnActivityResult kimgijeong");
            flag = data.getIntExtra("flag", -1);
            if(flag != -1){ //0 : Nozone, 1 : Setzone
                Bundle bundle = new Bundle();
                bundle.putInt("flag", flag);
                frag3.setArguments(bundle);
                frag4.setArguments(bundle);
                setFrag(2); //첫 프래그먼트 화면 지정
            }
        }
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        TextView tv_title = findViewById(R.id.title_txt);
                        TextView tv_artist = findViewById(R.id.artist_txt);
                        ImageView img_track = findViewById(R.id.album_img);
                        tv_title.setText(track.name);
                        tv_artist.setText(track.artist.name);
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            img_track.setImageBitmap(bitmap);
                                        });
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                    else{
                        TextView tv_title = findViewById(R.id.title_txt);
                        TextView tv_artist = findViewById(R.id.artist_txt);
                        tv_title.setText("재생중인 곡이 없습니다.");
                        tv_artist.setText("Spotify로 노래를 재생시켜 주세요.");
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
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

}