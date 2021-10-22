package com.example.muf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Community_frag extends Fragment {

    private View view;
    static final String TAG = "HOME";

    public static Community_frag newinstance(){
        return new Community_frag();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_layout, container, false);

        view.findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), AddPostActivity.class);
                startActivity(intent);
                break;
        }
    };
}
