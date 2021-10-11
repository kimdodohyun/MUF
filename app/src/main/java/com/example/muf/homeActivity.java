package com.example.muf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class homeActivity extends AppCompatActivity {
        static final String TAG = "HOME";
        private BottomNavigationView bottomNavigationView;
        private FragmentManager fm;
        private FragmentTransaction ft;
        private Friends_list_frag frag1;
        private Chatting_frag frag2;
    private Home_frag frag3;
    private Community_frag frag4;
    private Myprofile_frag frag5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.main_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
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
        setFrag(2); //첫 프래그먼트 화면 지정

    }

    //프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n){
            case 0:
                Log.d(TAG, "setFrag: frag1");
                ft.replace(R.id.main_frame, frag1);
                ft.commit();
                break;
            case 1:
                Log.d(TAG, "setFrag: frag2");
                ft.replace(R.id.main_frame, frag2);
                ft.commit();
                break;
            case 2:
                Log.d(TAG, "setFrag: frag3");
                ft.replace(R.id.main_frame, frag3);
                ft.commit();
                break;
            case 3:
                Log.d(TAG, "setFrag: frag4");
                ft.replace(R.id.main_frame, frag4);
                ft.commit();
                break;
            case 4:
                Log.d(TAG, "setFrag: frag5");
                ft.replace(R.id.main_frame, frag5);
                ft.commit();
                break;

        }
    }
}