package com.example.muf;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.muf.SetZone.SetZoneActivity;

public class Home_frag extends Fragment {
    private final static String TAG = "HOME";
    private View view;
    private TextView No_textview, Set_textview;
    private int flag = -1;
    private String locationname;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeFrag onCreate", "kimgijeong");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout, container, false);
        No_textview = view.findViewById(R.id.No_zone_inhome);
        Set_textview = view.findViewById(R.id.view_myzone_inhome);
        Log.d("HomeFrag onCreateView", "kimgijeong");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("HomeFrag", " onActivityCreated kimgijeong");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() != null){ //HomeActivity에서 bundle받기
            Bundle bundle = getArguments();
            flag = bundle.getInt("flag");
            locationname = bundle.getString("name");
            Log.d("HomeFrag onCreateView", "flagvalue = " + flag +" kimgijeong");
            if(flag == 1){ //Zone이 설정 된 경우
                No_textview.setVisibility(View.INVISIBLE);
                Set_textview.setText(locationname);
                Set_textview.setVisibility(View.VISIBLE);
            }
            else if(flag == 0){
                No_textview.setVisibility(View.VISIBLE);
                Set_textview.setVisibility(View.INVISIBLE);
            }
        }

        Log.d("onStart", "No_textview : " + No_textview.getVisibility() );
        Log.d("HomeFrag onStart", "flagvalue = " + flag +" kimgijeong");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFrag onResume", "kimgijeong");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("HomeFrag onPause", "kimgijeong");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("HomeFrag onStop", "kimgijeong");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("HomeFrag onDestroyView", "kimgijeong");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("HomeFrag onDestroy", "kimgijeong");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("HomeFrag onDetach", "kimgijeong");
    }
}
